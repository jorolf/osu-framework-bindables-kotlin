package osu.framework.bindables.internal;

import kotlin.UninitializedPropertyAccessException;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;

public class LateInitDelegate<T, U> implements ReadWriteProperty<T, U> {
    private boolean initialized = false;
    private U value = null;

    @Override
    public U getValue(T t, KProperty<?> kProperty) {
        if (initialized)
            return value;
        else
            throw new UninitializedPropertyAccessException("The " + kProperty.getName() + " property was not initialized");
    }

    @Override
    public void setValue(T t, KProperty<?> kProperty, U u) {
        initialized = true;
        value = u;
    }

    public boolean isInitialized(){
        return initialized;
    }
}
