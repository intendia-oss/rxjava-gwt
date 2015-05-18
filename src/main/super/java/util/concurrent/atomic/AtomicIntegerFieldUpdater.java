package java.util.concurrent.atomic;

public abstract class AtomicIntegerFieldUpdater<T> {

    protected AtomicIntegerFieldUpdater() {
    }

    public abstract boolean compareAndSet(T obj, int expect, int update);

    public abstract boolean weakCompareAndSet(T obj, int expect, int update);

    public abstract void set(T obj, int newValue);

    public abstract void lazySet(T obj, int newValue);

    public abstract int get(T obj);

    public int getAndSet(T obj, int newValue) {
        int prev = get(obj);
        set(obj, newValue);
        return prev;
    }

    public int getAndIncrement(T obj) {
        int prev = get(obj);
        set(obj, prev + 1);
        return prev;
    }

    public int getAndDecrement(T obj) {
        int prev = get(obj);
        set(obj, prev - 1);
        return prev;
    }

    public int getAndAdd(T obj, int delta) {
        int prev = get(obj);
        set(obj, prev + delta);
        return prev;
    }

    public int incrementAndGet(T obj) {
        int next = get(obj) + 1;
        set(obj, next);
        return next;
    }

    public int decrementAndGet(T obj) {
        int next = get(obj) - 1;
        set(obj, next);
        return next;
    }

    public int addAndGet(T obj, int delta) {
        int next = get(obj) + delta;
        set(obj, next);
        return next;
    }
}
