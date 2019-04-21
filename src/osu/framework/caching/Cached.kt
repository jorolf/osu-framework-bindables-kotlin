package osu.framework.caching

import osu.framework.caching.internal.CachingHelper

class TypedCached<T> {
    var value: T = CachingHelper.returnItself<T>(null)
        get() {
            if (!privateIsValid)
                throw IllegalStateException("May not query ${::value.name} of an invalid ${TypedCached::class.simpleName}")

            return field
        }
        set(value) {
            field = value
            privateIsValid = true
        }

    private var privateIsValid: Boolean = false

    val isValid: Boolean
        get() = privateIsValid

    fun invalidate(): Boolean {
        if (privateIsValid) {
            privateIsValid = false
            return true
        }

        return false
    }
}

class Cached {
    private var privateIsValid: Boolean = false

    val isValid: Boolean
        get() = privateIsValid

    fun invalidate(): Boolean {
        if (privateIsValid) {
            privateIsValid = false
            return true
        }

        return false
    }

    fun validate() {
        privateIsValid = true
    }
}