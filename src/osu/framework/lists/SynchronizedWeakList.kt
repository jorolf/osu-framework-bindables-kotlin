package osu.framework.lists

import java.lang.ref.WeakReference

class SynchronizedWeakList<T> : WeakList<T>() {
    override fun add(obj: T) {
        synchronized(this) {
            super.add(obj)
        }
    }

    override fun add(weakReference: WeakReference<T>) {
        synchronized(this) {
            super.add(weakReference)
        }
    }

    override fun remove(item: T) {
        synchronized(this) {
            super.remove(item)
        }
    }

    override fun remove(weakReference: WeakReference<T>): Boolean {
        synchronized(this) {
            return super.remove(weakReference)
        }
    }

    override fun contains(item: T): Boolean {
        synchronized(this) {
            return super.contains(item)
        }
    }

    override fun contains(weakReference: WeakReference<T>): Boolean {
        synchronized(this) {
            return super.contains(weakReference)
        }
    }

    override fun clear() {
        synchronized(this) {
            super.clear()
        }
    }

    override fun forEachAlive(action: (T) -> Unit) {
        synchronized(this) {
            super.forEachAlive(action)
        }
    }
}