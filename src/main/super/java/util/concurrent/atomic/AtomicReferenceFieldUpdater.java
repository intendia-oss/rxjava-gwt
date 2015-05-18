package java.util.concurrent.atomic;

public abstract class AtomicReferenceFieldUpdater<T,V> {

    protected AtomicReferenceFieldUpdater() {
    }

    public abstract boolean compareAndSet(T obj, V expect, V update);

    public abstract boolean weakCompareAndSet(T obj, V expect, V update);

    public abstract void set(T obj, V newValue);

    public abstract void lazySet(T obj, V newValue);

    public abstract V get(T obj);

    public V getAndSet(T obj, V newValue) {
        V prev = get(obj);
        set(obj, newValue);
        return prev;
    }
}
