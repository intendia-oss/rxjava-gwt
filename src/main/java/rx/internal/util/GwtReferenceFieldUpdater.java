package rx.internal.util;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class GwtReferenceFieldUpdater<T,V> extends AtomicReferenceFieldUpdater<T,V> {

    protected abstract V getter(T obj);

    protected abstract void setter(T obj, V update);

    @Override public boolean compareAndSet(T obj, V expect, V update) {
        if (getter(obj) == expect) {
            set(obj, update);
            return true;
        } else {
            return false;
        }
    }

    @Override public boolean weakCompareAndSet(T obj, V expect, V update) {
        return compareAndSet(obj, expect, update);
    }

    @Override public void set(T obj, V newValue) {
        setter(obj, newValue);
    }

    @Override public void lazySet(T obj, V newValue) {
        set(obj, newValue);
    }

    @Override public V get(T obj) {
        return getter(obj);
    }
}
