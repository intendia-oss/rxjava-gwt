package rx.internal.util;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public abstract class GwtLongFieldUpdater<T> extends AtomicLongFieldUpdater<T> {

    protected abstract long getter(T obj);

    protected abstract void setter(T obj, long update);

    @Override public boolean compareAndSet(T obj, long expect, long update) {
        if (getter(obj) == expect) {
            set(obj, update);
            return true;
        } else {
            return false;
        }
    }

    @Override public boolean weakCompareAndSet(T obj, long expect, long update) {
        return compareAndSet(obj, expect, update);
    }

    @Override public void set(T obj, long newValue) {
        setter(obj, newValue);
    }

    @Override public void lazySet(T obj, long newValue) {
        set(obj, newValue);
    }

    @Override public long get(T obj) {
        return getter(obj);
    }
}
