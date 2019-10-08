package osu.framework.bindables

class Event<T> {
    private val listeners = mutableListOf<(T) -> Unit>()

    operator fun invoke(params: T) {
        listeners.forEach { it(params) }
    }

    @JvmName("addListener")
    operator fun plusAssign(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    @JvmName("removeListener")
    operator fun minusAssign(listener: (T) -> Unit) {
        listeners.remove(listener)
    }

    operator fun contains(listener: (T) -> Unit) = listener in listeners

    fun clear() {
        listeners.clear()
    }
}