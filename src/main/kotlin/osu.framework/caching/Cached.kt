package osu.framework.caching

import osu.framework.caching.internal.CachingHelper

class TypedCached<T> {
    var value: T = CachingHelper.returnItself<T>(null)
        get() {
            if (!isValid)
                throw IllegalStateException("May not query ${::value.name} of an invalid ${TypedCached::class.simpleName}")

            return field
        }
        set(value) {
            field = value
            isValid = true
        }

    var isValid: Boolean = false
        private set

    fun invalidate(): Boolean {
        if (isValid) {
            isValid = false
            return true
        }

        return false
    }
}

class Cached {
    var isValid: Boolean = false
        private set

    fun invalidate(): Boolean {
        if (isValid) {
            isValid = false
            return true
        }

        return false
    }

    fun validate() {
        isValid = true
    }
}