package java.util.concurrent.atomic;

public class AtomicLong extends Number implements java.io.Serializable {
    private long value;

    public AtomicLong(long initialValue) {
        this.value = initialValue;
    }

    public AtomicLong() {
    }

    public final long get() {
        return value;
    }

    public final void set(long newValue) {
        value = newValue;
    }

    public final void lazySet(long newValue) {
        set(newValue);
    }

    public final long getAndSet(long newValue) {
        long current = value;
        value = newValue;
        return current;
    }

    public final boolean compareAndSet(long expect, long update) {
        if (value == expect) {
            value = update;
            return true;
        } else {
            return false;
        }
    }

    public final long getAndIncrement() {
        return value++;
    }

    public final long getAndDecrement() {
        return value--;
    }

    public final long getAndAdd(long delta) {
        long current = value;
        value += delta;
        return current;
    }

    public final long incrementAndGet() {
        return ++value;
    }

    public final long decrementAndGet() {
        return --value;
    }

    public final long addAndGet(long delta) {
        value += delta;
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    public int intValue() {
        return (int) value;
    }

    public long longValue() {
        return value;
    }

    public float floatValue() {
        return (float) value;
    }

    public double doubleValue() {
        return (double) value;
    }
}
