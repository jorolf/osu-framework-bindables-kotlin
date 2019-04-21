import osu.framework.bindables.*

fun main() {
    initialization()
    nullableInitialization()
    simpleNullableInitialization()
    valueChanged()
}

fun initialization() {
    val bindable = Bindable("Hello", String::class.java)

    bindable.value += " World!"

    println(bindable.value.toUpperCase()) //No null check
}

fun simpleInitialization() {
    val bindable = Bindable.new("Hello")

    bindable.value += " World!"

    println(bindable.value)
}

fun nullableInitialization() {
    val bindable = Bindable.newNullable(String::class.java)

    bindable.value = "Hello World!"

    println(bindable.value?.toUpperCase()) //Null check required
}

fun simpleNullableInitialization() {
    val bindable = Bindable.newNullable<String>()

    bindable.value = "Hello World!"

    println(bindable.value?.toUpperCase()) //Null check required
}

fun valueChanged() {
    val bindable = Bindable("", String::class.java)
    bindable.valueChanged += { println(it.newValue) }

    bindable.value = "Hello World!"
}

fun intBindables() {
    val bindable1 = BindableInt(1)
    val bindable2 = BindableInt()
    bindable2.bindTo(bindable1)
}