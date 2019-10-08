package osu.framework.bindables

abstract class BindableNumber<T>(initialValue: T, clazz: Class<T>, private val defaultPrecision: T, private val defaultMinValue: T, private val defaultMaxValue: T, private val toT: Number.() -> T, private val plus: T.(Number) -> T) : Bindable<T>(initialValue, clazz) where T : Number, T : Comparable<T> {
    init {
        default = 0.toT()
    }

    val precisionChanged = Event<T>()
    val minValueChanged = Event<T>()
    val maxValueChanged = Event<T>()

    var precision: T = defaultPrecision
        set(value) {
            if (value == field) return

            if (value.toDouble() <= 0)
                throw IllegalArgumentException("Precision must be greater than 0.")

            field = value

            triggerPrecisionChange()
        }

    override var value: T
        get() = super.value
        set(value) {
            if (precision != defaultPrecision) {
                var doubleValue = clamp(value, minValue, maxValue).toDouble()
                doubleValue = Math.round(doubleValue / precision.toDouble()) * precision.toDouble()

                super.value = doubleValue.toT()
            } else
                super.value = clamp(value, minValue, maxValue)
        }

    var minValue: T = defaultMinValue
        set(value) {
            if (field == value) return

            field = value

            triggerMinValueChange()
        }

    var maxValue: T = defaultMaxValue
        set(value) {
            if (field == value) return

            field = value

            triggerMaxValueChange()
        }

    override fun triggerChange() {
        super.triggerChange()

        triggerPrecisionChange(false)
        triggerMinValueChange(false)
        triggerMaxValueChange(false)
    }

    fun triggerPrecisionChange(propagateToBindings: Boolean = true) {
        val beforePropagation: T = precision
        if (propagateToBindings)
            bindings?.forEachAlive {
                if (it is BindableNumber<T>)
                    it.precision = precision
            }
        if (beforePropagation == precision)
            precisionChanged(precision)
    }

    fun triggerMinValueChange(propagateToBindings: Boolean = true) {
        val beforePropagation: T = minValue
        if (propagateToBindings)
            bindings?.forEachAlive {
                if (it is BindableNumber<T>)
                    it.minValue = minValue
            }
        if (beforePropagation == minValue)
            minValueChanged(minValue)
    }

    fun triggerMaxValueChange(propagateToBindings: Boolean = true) {
        val beforePropagation: T = maxValue
        if (propagateToBindings)
            bindings?.forEachAlive {
                if (it is BindableNumber<T>)
                    it.maxValue = maxValue
            }
        if (beforePropagation == maxValue)
            maxValueChanged(maxValue)
    }

    override fun bindTo(them: Bindable<T>) {
        if (them is BindableNumber<T>) {
            precision = max(precision, them.precision)
            minValue = max(minValue, them.minValue)
            maxValue = min(maxValue, them.maxValue)

            if (minValue > maxValue)
                throw IllegalArgumentException("Can not weld bindable longs with non-overlapping min/max-ranges. The ranges were [$minValue - $maxValue] and [${them.minValue} - ${them.maxValue}].")
        }

        super.bindTo(them)
    }

    val hasDefinedRange get() = minValue != defaultMinValue || maxValue != defaultMaxValue

    val isInteger get() = precision.toDouble() == precision.toInt().toDouble()

    fun set(value: Number) {
        this.value = value.toT()
    }

    fun add(value: Number) {
        this.value = this.value.plus(value)
    }

    fun setProportional(amt: Double, snap: Double = 0.0) {
        val min = minValue.toDouble()
        val max = maxValue.toDouble()
        var value = min + (max - min) * amt
        if (snap > 0)
            value = Math.round(value / snap) * snap

        set(value)
    }

    fun getBoundNumberCopy() = super.getBoundCopy() as BindableNumber<T>

    fun getUnboundNumberCopy() = super.getUnboundCopy() as BindableNumber<T>

    private fun max(value1: T, value2: T): T = if (value1 > value2) value1 else value2

    private fun min(value1: T, value2: T): T = if (value1 < value2) value1 else value2

    private fun clamp(value: T, minValue: T, maxValue: T): T = max(minValue, min(maxValue, value))
}

class BindableDouble(initialValue: Double = 0.0) : BindableNumber<Double>(initialValue, Double::class.javaObjectType, Double.MIN_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Number::toDouble, { this + it.toDouble() }) {
    override val isDefault get() = Math.abs(value - default) > precision
}

class BindableFloat(initialValue: Float = 0.0f) : BindableNumber<Float>(initialValue, Float::class.javaObjectType, Float.MIN_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE, Number::toFloat, { this + it.toFloat() }) {
    override val isDefault get() = Math.abs(value - default) > precision
}

class BindableInt(initialValue: Int = 0) : BindableNumber<Int>(initialValue, Int::class.javaObjectType, 1, Int.MIN_VALUE, Int.MAX_VALUE, Number::toInt, { this + it.toInt() })

class BindableLong(initialValue: Long = 0) : BindableNumber<Long>(initialValue, Long::class.javaObjectType, 1, Long.MIN_VALUE, Long.MAX_VALUE, Number::toLong, { this + it.toLong() })
