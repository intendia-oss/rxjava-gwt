/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.internal.util;

import java.util.Queue;

import rx.Observer;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.operators.NotificationLite;

/**
 * This assumes Spsc or Spmc usage. This means only a single producer calling the on* methods. This is the Rx contract of an Observer.
 * Concurrent invocations of on* methods will not be thread-safe.
 */
public class RxRingBuffer implements Subscription {

    public static RxRingBuffer getSpscInstance() {
        return new RxRingBuffer();
    }

    public static RxRingBuffer getSpmcInstance() {
        return new RxRingBuffer();
    }

    private static final NotificationLite<Object> on = NotificationLite.instance();

    private Queue<Object> queue;

    private final int size;
    private final ObjectPool<Queue<Object>> pool;

    /**
     * We store the terminal state separately so it doesn't count against the size.
     * We don't just +1 the size since some of the queues require sizes that are a power of 2.
     * This is a subjective thing ... wanting to keep the size (ie 1024) the actual number of onNext
     * that can be sent rather than something like 1023 onNext + 1 terminal event. It also simplifies
     * checking that we have received only 1 terminal event, as we don't need to peek at the last item
     * or retain a boolean flag.
     */
    public volatile Object terminalState;

    public static final int SIZE = 16; // lower default for GWT

    private RxRingBuffer(Queue<Object> queue, int size) {
        this.queue = queue;
        this.pool = null;
        this.size = size;
    }

    private RxRingBuffer(ObjectPool<Queue<Object>> pool, int size) {
        this.pool = pool;
        this.queue = pool.borrowObject();
        this.size = size;
    }

    public synchronized void release() {
        Queue<Object> q = queue;
        ObjectPool<Queue<Object>> p = pool;
        if (p != null && q != null) {
            q.clear();
            queue = null;
            p.returnObject(q);
        }
    }

    @Override
    public void unsubscribe() {
        release();
    }

    /* package accessible for unit tests */RxRingBuffer() {
        this(new SynchronizedQueue<Object>(SIZE), SIZE);
    }

    /**
     *
     * @param o
     * @throws MissingBackpressureException
     *             if more onNext are sent than have been requested
     */
    public void onNext(Object o) throws MissingBackpressureException {
        boolean iae = false;
        boolean mbe = false;
        synchronized (this) {
            Queue<Object> q = queue;
            if (q != null) {
                mbe = !q.offer(on.next(o));
            } else {
                iae = true;
            }
        }

        if (iae) {
            throw new IllegalStateException("This instance has been unsubscribed and the queue is no longer usable.");
        }
        if (mbe) {
            throw new MissingBackpressureException();
        }
    }

    public void onCompleted() {
        // we ignore terminal events if we already have one
        if (terminalState == null) {
            terminalState = on.completed();
        }
    }

    public void onError(Throwable t) {
        // we ignore terminal events if we already have one
        if (terminalState == null) {
            terminalState = on.error(t);
        }
    }

    public int available() {
        return size - count();
    }

    public int capacity() {
        return size;
    }

    public int count() {
        Queue<Object> q = queue;
        if (q == null) {
            return 0;
        }
        return q.size();
    }

    public boolean isEmpty() {
        Queue<Object> q = queue;
        if (q == null) {
            return true;
        }
        return q.isEmpty();
    }

    public Object poll() {
        Object o;
        synchronized (this) {
            Queue<Object> q = queue;
            if (q == null) {
                // we are unsubscribed and have released the underlying queue
                return null;
            }
            o = q.poll();

            Object ts = terminalState;
            if (o == null && ts != null && q.peek() == null) {
                o = ts;
                // once emitted we clear so a poll loop will finish
                terminalState = null;
            }
        }
        return o;
    }

    public Object peek() {
        Object o;
        synchronized (this) {
            Queue<Object> q = queue;
            if (q == null) {
                // we are unsubscribed and have released the underlying queue
                return null;
            }
            o = q.peek();
            Object ts = terminalState;
            if (o == null && ts != null && q.peek() == null) {
                o = ts;
            }
        }
        return o;
    }

    public boolean isCompleted(Object o) {
        return on.isCompleted(o);
    }

    public boolean isError(Object o) {
        return on.isError(o);
    }

    public Object getValue(Object o) {
        return on.getValue(o);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean accept(Object o, Observer child) {
        return on.accept(child, o);
    }

    public Throwable asError(Object o) {
        return on.getError(o);
    }

    @Override
    public boolean isUnsubscribed() {
        return queue == null;
    }

}
