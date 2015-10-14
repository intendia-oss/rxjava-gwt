package rx.internal.util.unsafe;

import java.util.AbstractQueue;
import java.util.Iterator;

public final class SpscLinkedQueue<E> extends AbstractQueue<E> {
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
