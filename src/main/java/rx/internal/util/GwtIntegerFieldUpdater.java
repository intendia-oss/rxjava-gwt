package rx.internal.util;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class GwtIntegerFieldUpdater<T> extends AtomicIntegerFieldUpdater<T> {

    protected abstract int getter(T obj);

    protected abstract void setter(T obj, int update);

    @Override public boolean compareAndSet(T obj, int expect, int update) {
        if (getter(obj) == expect) {
            set(obj, update);
            return true;
        } else {
            return false;
        }
    }

    @Override public boolean weakCompareAndSet(T obj, int expect, int update) {
        return compareAndSet(obj, expect, update);
    }

    @Override public void set(T obj, int newValue) {
        setter(obj, newValue);
    }

    @Override public void lazySet(T obj, int newValue) {
        set(obj, newValue);
    }

    @Override public int get(T obj) {
        return getter(obj);
    }
}
