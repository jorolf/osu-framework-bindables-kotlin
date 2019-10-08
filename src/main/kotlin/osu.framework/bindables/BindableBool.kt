package osu.framework.bindables

class BindableBool(initialValue: Boolean = false) : Bindable<Boolean>(initialValue, Boolean::class.javaObjectType) {
    override fun parse(input: Any) {
        when (input) {
            "1" -> value = true
            "0" -> value = false
            else -> super.parse(input)
        }
    }

    fun toggle() {
        value = !value
    }
}