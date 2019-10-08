package osu.framework.bindables

import osu.framework.caching.TypedCached
import osu.framework.lists.SynchronizedWeakList
import java.lang.ref.WeakReference

class BindableMutableList<T>() : AbstractMutableList<T>() {
    val itemAdded = Event<T>()
    val itemRemoved = Event<T>()
    val disabledChanged = Event<Boolean>()

    private val list = mutableListOf<T>()
    private val weakReferenceCache = TypedCached<WeakReference<BindableMutableList<T>>>()
    private val weakReference get() = if (weakReferenceCache.isValid) weakReferenceCache.value else WeakReference(this).also(weakReferenceCache::value.setter)
    private var bindings: SynchronizedWeakList<BindableMutableList<T>>? = null

    constructor(items: Collection<T>) : this() {
        addAll(items)
    }

    override val size: Int
        get() = list.size

    override fun add(index: Int, element: T) = add(index, element, null)

    override fun get(index: Int): T = list[index]

    override fun removeAt(index: Int): T = removeAt(index, null)

    override fun set(index: Int, element: T): T = set(index, element, null)

    private fun set(index: Int, item: T, caller: BindableMutableList<T>?): T {
        ensureMutationAllowed()

        val lastItem = list[index]

        list[index] = item

        bindings?.forEachAlive {
            if (it != caller)
                it.set(index, item, this)
        }

        itemRemoved(lastItem)
        itemAdded(item)

        return lastItem
    }

    private fun add(index: Int, item: T, caller: BindableMutableList<T>?) {
        ensureMutationAllowed()

        list.add(index, item)

        bindings?.forEachAlive {
            if (it != caller)
                it.add(index, item, this)
        }

        itemAdded(item)
    }

    private fun removeAt(index: Int, caller: BindableMutableList<T>?): T {
        ensureMutationAllowed()

        val item = list[index]

        list.removeAt(index)

        bindings?.forEachAlive {
            if (it != caller)
                it.removeAt(index, this)
        }

        itemRemoved(item)
        return item
    }

    var disabled = false
        set(value) {
            if (value == field) return

            field = value

            triggerDisabledChange()
        }

    fun bindDisabledChanged(onChange: (Boolean) -> Unit, runOnceImmediately: Boolean = false) {
        disabledChanged += onChange
        if (runOnceImmediately)
            onChange(disabled)
    }

    fun triggerDisabledChange(propagateToBindings: Boolean = true) {
        val beforePropagation = disabled

        if (propagateToBindings)
            bindings?.forEachAlive { it.disabled = disabled }

        if (beforePropagation == disabled)
            disabledChanged(disabled)
    }

    fun unbindEvents() {
        itemAdded.clear()
        itemRemoved.clear()
        disabledChanged.clear()
    }

    fun unbindBindings() {
        bindings?.forEachAlive { it.unbind(this) }
        bindings?.clear()
    }

    fun unbindAll() {
        unbindEvents()
        unbindBindings()
    }

    fun unbindFrom(them: BindableMutableList<T>) {
        removeWeakReference(them.weakReference)
        them.removeWeakReference(weakReference)
    }

    private fun unbind(binding: BindableMutableList<T>) {
        bindings!!.remove(binding.weakReference)
    }

    fun bindTo(them: BindableMutableList<T>) {
        if (them === this)
            throw IllegalArgumentException("A BindableMutableList can not be bound to itself.")

        clear()
        addAll(them)

        disabled = them.disabled
        addWeakReference(them.weakReference)
        them.addWeakReference(weakReference)
    }

    private fun addWeakReference(weakReference: WeakReference<BindableMutableList<T>>) {
        if (bindings == null)
            bindings = SynchronizedWeakList()

        bindings?.add(weakReference)
    }

    private fun removeWeakReference(weakReference: WeakReference<BindableMutableList<T>>) {
        bindings?.remove(weakReference)
    }

    private val ctor: () -> BindableMutableList<T> by lazy {
        @Suppress("UNCHECKED_CAST")
        val ctor = javaClass.getConstructor()
        return@lazy { ctor.newInstance() }
    }

    fun getBoundCopy(): BindableMutableList<T> {
        val copy = ctor()
        copy.bindTo(this)
        return copy
    }

    private fun ensureMutationAllowed() {
        if (disabled)
            throw UnsupportedOperationException("Cannot mutate the MutableBindableList while it is disabled.")
    }

    val isDefault get() = size == 0
}