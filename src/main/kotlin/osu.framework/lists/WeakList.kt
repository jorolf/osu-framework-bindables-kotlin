package osu.framework.lists

import java.lang.ref.WeakReference

open class WeakList<T> {
    private val list: MutableList<WeakReference<T>> = mutableListOf()

    open fun add(obj: T) = add(WeakReference(obj))

    open fun add(weakReference: WeakReference<T>) {
        list.add(weakReference)
    }

    open fun remove(item: T) {
        list.removeAll { it.get()?.let { obj -> obj == item } ?: false }
    }

    open fun remove(weakReference: WeakReference<T>) = list.remove(weakReference)

    open fun contains(item: T) = list.any { it.get()?.equals(item) ?: false }

    open fun contains(weakReference: WeakReference<T>) = list.contains(weakReference)

    open fun clear() = list.clear()

    open fun forEachAlive(action: (T) -> Unit) {
        list.removeAll { it.get() == null }

        list.forEach {
            it.get()?.let { obj -> action(obj) }
        }
    }
}