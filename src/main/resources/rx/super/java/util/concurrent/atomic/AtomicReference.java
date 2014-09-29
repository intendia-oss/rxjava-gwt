package java.util.concurrent.atomic;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

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

    public final V getAndUpdate(UnaryOperator<V> updateFunction) {
        final V prev = get();
        final V next = updateFunction.apply(prev);
        final boolean updated = compareAndSet(prev, next);
        assert updated : "event loop model";
        return prev;
    }

    public final V updateAndGet(UnaryOperator<V> updateFunction) {
        final V prev = get();
        final V next = updateFunction.apply(prev);
        final boolean updated = compareAndSet(prev, next);
        assert updated : "event loop model";
        return next;
    }

    public final V getAndAccumulate(V x, BinaryOperator<V> accumulatorFunction) {
        final V prev = get();
        final V next = accumulatorFunction.apply(prev, x);
        final boolean updated = compareAndSet(prev, next);
        assert updated : "event loop model";
        return prev;
    }

    public final V accumulateAndGet(V x, BinaryOperator<V> accumulatorFunction) {
        final V prev = get();
        final V next = accumulatorFunction.apply(prev, x);
        final boolean updated = compareAndSet(prev, next);
        assert updated : "event loop model";
        return next;
    }

    public String toString() {
        return String.valueOf(get());
    }

}
