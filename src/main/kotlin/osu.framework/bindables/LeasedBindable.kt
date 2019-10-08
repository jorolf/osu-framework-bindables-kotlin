package osu.framework.bindables

import osu.framework.bindables.internal.LateInitDelegate

class LeasedBindable<T> private constructor(initialValue: T, clazz: Class<T>) : Bindable<T>(initialValue, clazz) {
    private lateinit var source: Bindable<T>

    private var valueBeforeLease: T by LateInitDelegate<LeasedBindable<T>, T>()
    private var disabledBeforeLease = false
    private var revertValueOnReturn = false

    internal constructor(source: Bindable<T>, revertValueOnReturn: Boolean) : this(source.value, source.clazz) {
        bindTo(source)

        this.source = source

        if (revertValueOnReturn) {
            this.revertValueOnReturn = true
            valueBeforeLease = value
        }

        disabledBeforeLease = disabled

        disabled = true
    }

    private var hasBeenReturned = false

    fun returnBindable() {
        if (!::source.isInitialized) throw UnsupportedOperationException("Must return from original leased source.")

        if (hasBeenReturned) throw UnsupportedOperationException("This bindable has already been returned.")

        unbindAll()
    }

    override var value: T
        get() = super.value
        set(value) {
            checkValid()

            if (value == super.value)
                return

            setValue(super.value, value, true)
        }

    override var disabled: Boolean
        get() = super.disabled
        set(value) {
            checkValid()

            if (disabled == value) return

            setDisabled(value, true)
        }

    override fun unbindAll() {
        if (::source.isInitialized && !hasBeenReturned) {
            if (revertValueOnReturn)
                value = valueBeforeLease

            disabled = disabledBeforeLease

            source.endLease(this)
            hasBeenReturned = true
        }

        super.unbindAll()
    }

    private fun checkValid() {
        if (hasBeenReturned)
            throw UnsupportedOperationException("Cannot perform operations on a ${LeasedBindable::class.simpleName} that has been returned")
    }
}