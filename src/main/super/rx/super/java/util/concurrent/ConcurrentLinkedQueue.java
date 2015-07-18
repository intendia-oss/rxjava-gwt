package java.util.concurrent;

import java.util.Collection;
import java.util.LinkedList;

public class ConcurrentLinkedQueue<E> extends LinkedList<E> {

    public ConcurrentLinkedQueue() {
    }

    public ConcurrentLinkedQueue(Collection<? extends E> c) {
        super(c);
    }
}
