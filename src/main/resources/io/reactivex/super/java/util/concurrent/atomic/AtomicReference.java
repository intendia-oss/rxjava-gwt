package java.util.concurrent.atomic;

public class AtomicReference<V> implements java.io.Serializable {

    private volatile V value;

    public AtomicReference() {}

    public AtomicReference(V initialValue) {
        value = initialValue;
    }

    public final V get() {
        return value;
    }

    public final void set(V newValue) {
        value = newValue;
    }

    public final void lazySet(V newValue) {
        set(newValue);
    }

    public final boolean compareAndSet(V expect, V update) {
        if (value != expect) return false;
        value = update;
        return true;
    }

    public final boolean weakCompareAndSet(V expect, V update) {
        return compareAndSet(expect, update);
    }

    public final V getAndSet(V newValue) {
        final V previous = value;
        value = newValue;
        return previous;
    }

    public String toString() {
        return String.valueOf(get());
    }

}
