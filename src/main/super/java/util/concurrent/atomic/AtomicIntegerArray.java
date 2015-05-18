package java.util.concurrent.atomic;

import java.util.Arrays;

public class AtomicIntegerArray implements java.io.Serializable {

    private final int[] array;

    public AtomicIntegerArray(int length) {
        array = new int[length];
    }

    public AtomicIntegerArray(int[] array) {
        this.array = Arrays.copyOf(array, array.length);
    }

    public final int length() {
        return array.length;
    }

    public final int get(int i) {
        return array[i];
    }

    public final void set(int i, int newValue) {
        array[i] = newValue;
    }

    public final void lazySet(int i, int newValue) {
        set(i, newValue);
    }

    public final int getAndSet(int i, int newValue) {
        final int prev = array[i];
        array[i] = newValue;
        return prev;
    }

    public final boolean compareAndSet(int i, int expect, int update) {
        if (array[i] == expect) {
            array[i] = update;
            return true;
        } else {
            return false;
        }
    }


    public final boolean weakCompareAndSet(int i, int expect, int update) {
        return compareAndSet(i, expect, update);
    }

    public final int getAndIncrement(int i) {
        return getAndAdd(i, 1);
    }

    public final int getAndDecrement(int i) {
        return getAndAdd(i, -1);
    }

    public final int getAndAdd(int i, int delta) {
        return getAndSet(i, array[i] + delta);
    }

    public final int incrementAndGet(int i) {
        return getAndAdd(i, 1) + 1;
    }

    public final int decrementAndGet(int i) {
        return getAndAdd(i, -1) - 1;
    }

    public final int addAndGet(int i, int delta) {
        return getAndAdd(i, delta) + delta;
    }

    public String toString() {
        return Arrays.toString(array);
    }
}
