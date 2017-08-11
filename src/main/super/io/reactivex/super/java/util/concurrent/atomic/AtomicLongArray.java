package java.util.concurrent.atomic;

import java.util.Arrays;

public class AtomicLongArray implements java.io.Serializable {

    private final long[] array;

    public AtomicLongArray(int length) {
        array = new long[length];
    }

    public AtomicLongArray(long[] array) {
        this.array = Arrays.copyOf(array, array.length);
    }

    public final int length() {
        return array.length;
    }

    public final long get(int i) {
        return array[i];
    }

    public final void set(int i, long newValue) {
        array[i] = newValue;
    }

    public final void lazySet(int i, long newValue) {
        set(i, newValue);
    }

    public final long getAndSet(int i, long newValue) {
        final long prev = array[i];
        array[i] = newValue;
        return prev;
    }

    public final boolean compareAndSet(int i, long expect, long update) {
        if (array[i] == expect) {
            array[i] = update;
            return true;
        } else {
            return false;
        }
    }


    public final boolean weakCompareAndSet(int i, long expect, long update) {
        return compareAndSet(i, expect, update);
    }

    public final long getAndIncrement(int i) {
        return getAndAdd(i, 1);
    }

    public final long getAndDecrement(int i) {
        return getAndAdd(i, -1);
    }

    public final long getAndAdd(int i, long delta) {
        return getAndSet(i, array[i] + delta);
    }

    public final long incrementAndGet(int i) {
        return getAndAdd(i, 1) + 1;
    }

    public final long decrementAndGet(int i) {
        return getAndAdd(i, -1) - 1;
    }

    public final long addAndGet(int i, long delta) {
        return getAndAdd(i, delta) + delta;
    }

    public String toString() {
        return Arrays.toString(array);
    }
}
