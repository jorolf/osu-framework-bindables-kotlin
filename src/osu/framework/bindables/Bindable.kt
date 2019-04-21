package osu.framework.bindables

import osu.framework.bindables.internal.LateInitDelegate
import osu.framework.caching.TypedCached
import osu.framework.lists.SynchronizedWeakList
import java.lang.ref.WeakReference
import java.lang.reflect.Constructor

open class Bindable<T>(initialValue: T, internal val clazz: Class<T>) {
    val valueChanged: Event<ValueChangedEvent<T>> = Event()
    val disabledChanged: Event<Boolean> = Event()

    private var internalValue: T = initialValue
    private val defaultInitDelegate = LateInitDelegate<Bindable<T>, T>()
    var default: T by defaultInitDelegate

    private var internalDisabled = false
    open var disabled: Boolean
        get() = internalDisabled
        set(value) {
            throwIfLeased()

            if (internalDisabled == value) return

            setDisabled(value)
        }

    internal fun setDisabled(value: Boolean, bypassChecks: Boolean = false, source: Bindable<T>? = null) {
        if (!bypassChecks)
            throwIfLeased()

        internalDisabled = value
        triggerDisabledChange(source ?: this, true, bypassChecks)
    }

    open val isDefault: Boolean
        get() = defaultInitDelegate.isInitialized && default?.equals(value) ?: false

    fun setDefault() {
        value = default
    }

    open var value: T
        get() = internalValue
        set(value) {
            if (disabled)
                throw UnsupportedOperationException("Can not set value to $value as bindable is disabled.")

            if (value == internalValue) return

            setValue(internalValue, value)
        }

    internal fun setValue(previousValue: T, value: T, bypassChecks: Boolean = false, source: Bindable<T>? = null) {
        internalValue = value
        triggerValueChange(previousValue, source ?: this, true, bypassChecks)
    }

    private val weakReferenceCache: TypedCached<WeakReference<Bindable<T>>> = TypedCached()

    private val weakReference
        get() = if (weakReferenceCache.isValid) weakReferenceCache.value else WeakReference(this).also { weakReferenceCache.value = it }

    protected var bindings: SynchronizedWeakList<Bindable<T>>? = null
        private set

    var bindTarget: Bindable<T>
        get() = throw UnsupportedOperationException("This property only allows using the setter.")
        set(value) = bindTo(value)

    open fun bindTo(them: Bindable<T>) {
        value = them.value
        disabled = them.disabled
        if (defaultInitDelegate.isInitialized)
            default = them.default

        addWeakReference(them.weakReference)
        them.addWeakReference(weakReference)
    }

    fun bindValueChanged(runOnceImmediately: Boolean = false, onChange: (ValueChangedEvent<T>) -> Unit) {
        valueChanged += onChange
        if (runOnceImmediately)
            onChange(ValueChangedEvent(value, value))
    }

    fun bindDisabledChanged(runOnceImmediately: Boolean = false, onChange: (Boolean) -> Unit) {
        disabledChanged += onChange
        if (runOnceImmediately)
            onChange(disabled)
    }

    private fun addWeakReference(weakReference: WeakReference<Bindable<T>>) {
        if (bindings == null)
            bindings = SynchronizedWeakList()

        bindings!!.add(weakReference)
    }

    private fun removeWeakReference(weakReference: WeakReference<Bindable<T>>) {
        bindings?.remove(weakReference)
    }

    private val valueOf: (String) -> T by lazy {
        val method = clazz.getMethod("valueOf", String::class.java)::invoke
        return@lazy { str: String -> clazz.cast(method(null, arrayOf(str))) }
    }

    /**
     * Tries to parse the input and set ``value`` to it.
     * If the input is the same type as ``T`` then it is assigned to ``value`` as-is.
     * If the input is a ``String`` then it searches for a method with the signature ``valueOf(String)`` on the type ``T``
     */
    open fun parse(input: Any) {
        value = when {
            clazz.isInstance(input) -> clazz.cast(input)
            input is String -> valueOf(input)
            else -> throw IllegalArgumentException("Could not parse provided ${input::class.simpleName} ($input to ${clazz.simpleName})")
        }
    }

    open fun triggerChange() {
        triggerValueChange(value, this, false)
        triggerDisabledChange(this, false)
    }

    protected fun triggerValueChange(previousValue: T, source: Bindable<T>, propagateToBindings: Boolean = true, bypassChecks: Boolean = false) {
        val beforePropagation: T = internalValue
        if (propagateToBindings)
            bindings?.forEachAlive {
                if (it == source) return@forEachAlive

                it.setValue(previousValue, internalValue, bypassChecks, this)
            }
        if (beforePropagation == value)
            valueChanged(ValueChangedEvent(previousValue, internalValue))
    }

    protected fun triggerDisabledChange(source: Bindable<T>, propagateToBindings: Boolean = true, bypassChecks: Boolean = false) {
        val beforePropagation: Boolean = disabled
        if (propagateToBindings)
            bindings?.forEachAlive {
                if (it == source) return@forEachAlive

                it.setDisabled(disabled, bypassChecks, this)
            }
        if (beforePropagation == disabled)
            disabledChanged(disabled)
    }

    fun unbindEvents() {
        valueChanged.clear()
        disabledChanged.clear()
    }

    fun unbindBindings() {
        bindings?.forEachAlive { it.unbind(this) }
        bindings?.clear()
    }

    protected fun unbind(binding: Bindable<T>) {
        bindings?.remove(binding.weakReference)
    }

    open fun unbindAll() {
        if (isLeased)
            leasedBindable!!.returnBindable()

        unbindEvents()
        unbindBindings()
    }

    fun unbindFrom(them: Bindable<T>) {
        removeWeakReference(them.weakReference)
        them.removeWeakReference(weakReference)
    }

    override fun toString(): String = value?.toString() ?: ""

    fun getUnboundCopy(): Bindable<T> {
        val clone = getBoundCopy()
        clone.unbindAll()
        return clone
    }

    private val ctor: (T, Class<T>) -> Bindable<T> by lazy {
        @Suppress("UNCHECKED_CAST")
        val ctor = this.javaClass.declaredConstructors.first { ctor ->
            ctor.parameterCount == 2 && ctor.parameterTypes.let {
                it[0].isAssignableFrom(clazz) && it[1] == Class::class.java
            }
        } as Constructor<Bindable<T>>
        ctor.isAccessible = true
        return@lazy { value: T, clazz: Class<T> -> ctor.newInstance(value, clazz) }
    }

    fun getBoundCopy(): Bindable<T> {
        val copy = ctor(value, clazz)
        copy.bindTo(this)
        return copy
    }

    private var leasedBindable: LeasedBindable<T>? = null

    private val isLeased: Boolean
        get() = leasedBindable != null

    fun beginLease(revertValueOnReturn: Boolean): LeasedBindable<T> {
        if (checkForLease(this))
            throw UnsupportedOperationException("Attempted to lease a bindable that is already in a leased state.")

        return LeasedBindable(this, revertValueOnReturn).apply { this@Bindable.leasedBindable = this }
    }

    private fun checkForLease(source: Bindable<T>): Boolean {
        if (isLeased) return true

        var found = false
        bindings?.forEachAlive {
            if (it != source) found = found || it.checkForLease(this)
        }

        return found
    }

    internal fun endLease(returnedBindable: Bindable<T>) {
        if (!isLeased)
            throw UnsupportedOperationException("Attempted to end a lease without beginning one.")

        if (returnedBindable !== leasedBindable)
            throw UnsupportedOperationException("Attempted to end a lease but returned a different bindable to the one used to start the lease.")

        leasedBindable = null
    }

    private fun throwIfLeased() {
        if (isLeased)
            throw UnsupportedOperationException("Cannot perform this operation on a ${Bindable::class.simpleName} that is currently in a leased state.")
    }

    companion object {
        /**
         * Creates a new Bindable which's default value is null
         */
        @JvmStatic
        fun <T> newNullable(clazz: Class<T>, value: T? = null): Bindable<T?> {
            @Suppress("UNCHECKED_CAST")
            return Bindable(value, clazz as Class<T?>).apply { default = null }
        }

        inline fun <reified T> newNullable(value: T? = null): Bindable<T?> = newNullable(T::class.java, value)

        inline fun <reified T> new(value: T): Bindable<T> = Bindable(value, T::class.java)
    }
}