package rx.internal.util.unsafe;

import java.util.AbstractQueue;
import java.util.Iterator;

public final class SpscArrayQueue<E> extends AbstractQueue<E> {
    public SpscArrayQueue(int capacity) {
        throw new UnsupportedOperationException();
    }

    @Override public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override public int size() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean offer(E e) {
        throw new UnsupportedOperationException();
    }

    @Override public E poll() {
        throw new UnsupportedOperationException();
    }

    @Override public E peek() {
        throw new UnsupportedOperationException();
    }
}
