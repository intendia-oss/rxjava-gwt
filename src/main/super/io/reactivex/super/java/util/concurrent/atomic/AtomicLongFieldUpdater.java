package java.util.concurrent.atomic;

public abstract class AtomicLongFieldUpdater<T> {

    protected AtomicLongFieldUpdater() {
    }

    public abstract boolean compareAndSet(T obj, long expect, long update);

    public abstract boolean weakCompareAndSet(T obj, long expect, long update);

    public abstract void set(T obj, long newValue);

    public abstract void lazySet(T obj, long newValue);

    public abstract long get(T obj);

    public long getAndSet(T obj, long newValue) {
        long prev = get(obj);
        set(obj, newValue);
        return prev;
    }

    public long getAndIncrement(T obj) {
        long prev = get(obj);
        set(obj, prev + 1);
        return prev;
    }

    public long getAndDecrement(T obj) {
        long prev = get(obj);
        set(obj, prev - 1);
        return prev;
    }

    public long getAndAdd(T obj, long delta) {
        long prev = get(obj);
        set(obj, prev + delta);
        return prev;
    }

    public long incrementAndGet(T obj) {
        long next = get(obj) + 1;
        set(obj, next);
        return next;
    }

    public long decrementAndGet(T obj) {
        long next = get(obj) - 1;
        set(obj, next);
        return next;
    }

    public long addAndGet(T obj, long delta) {
        long next = get(obj) + delta;
        set(obj, next);
        return next;
    }
}
