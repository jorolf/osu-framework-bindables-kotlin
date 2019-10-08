package osu.framework.caching.internal;

public class CachingHelper {
    /**
     * Used whenever a nullable type needs to be converted to a non-nullable type in Kotlin
     * @param object The object to convert
     * @param <T> The type it should be converted into
     * @return The object passed into this function
     */
    public static <T> T returnItself(T object) {
        return object;
    }
}
