package rx.internal.util;

import java.util.Collection;
import java.util.LinkedList;

public class SynchronizedQueue<T> extends LinkedList<T> {

    private final int size;

    public SynchronizedQueue() {
        this(-1);
    }

    public SynchronizedQueue(int size) {
        this.size = size;
    }

    @Override
    public synchronized boolean offer(T e) {
        if (size > -1 && size() + 1 > size) {
            return false;
        }
        return super.offer(e);
    }

    @Override
    public Object clone() {
        return doClone();
    }

    private Object doClone() {
        SynchronizedQueue<T> q = new SynchronizedQueue<T>(size);
        q.addAll(this);
        return q;
    }
}
