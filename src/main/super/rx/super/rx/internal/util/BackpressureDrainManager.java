/**
 * Copyright 2014 Netflix, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package rx.internal.util;

import com.google.gwt.core.client.Scheduler;
import rx.Producer;
import rx.annotations.Experimental;
import rx.schedulers.GwtScheduler;

/**
 * Manages the producer-backpressure-consumer interplay by matching up available elements with requested elements and/or
 * terminal events.
 */
@Experimental
public final class BackpressureDrainManager implements Producer, Scheduler.RepeatingCommand {

    /**
     * Interface representing the minimal callbacks required to operate the drain part of a backpressure system.
     */
    public interface BackpressureQueueCallback {
        /**
         * Override this method to peek for the next element, null meaning no next element available now. <p>It will be
         * called plain and while holding this object's monitor.
         *
         * @return the next element or null if no next element available
         */
        Object peek();

        /**
         * Override this method to poll (consume) the next element, null meaning no next element available now.
         *
         * @return the next element or null if no next element available
         */
        Object poll();

        /**
         * Override this method to deliver an element to downstream. The logic ensures that this happens only in the
         * right conditions.
         *
         * @param value the value to deliver, not null
         * @return true indicates that one should terminate the emission loop unconditionally and not deliver any
         * further elements or terminal events.
         */
        boolean accept(Object value);

        /**
         * Override this method to deliver a normal or exceptional terminal event.
         *
         * @param exception if not null, contains the terminal exception
         */
        void complete(Throwable exception);
    }

    /** The request counter, updated via REQUESTED_COUNTER. */
    protected volatile int requestedCount;
    /** Indicates if one is in emitting phase, guarded by this. */
    protected boolean emitting;
    /** Indicates a terminal state. */
    protected volatile boolean terminated;
    /** Indicates an error state, barrier is provided via terminated. */
    protected Throwable exception;
    /** The callbacks to manage the drain. */
    protected final BackpressureQueueCallback actual;

    /**
     * Constructs a backpressure drain manager with 0 requesedCount, no terminal event and not emitting.
     *
     * @param actual he queue callback to check for new element availability
     */
    public BackpressureDrainManager(BackpressureQueueCallback actual) {
        this.actual = actual;
    }

    /**
     * Checks if a terminal state has been reached.
     *
     * @return true if a terminal state has been reached
     */
    public final boolean isTerminated() {
        return terminated;
    }

    /**
     * Move into a terminal state. Call drain() anytime after.
     */
    public final void terminate() {
        terminated = true;
    }

    /**
     * Move into a terminal state with an exception. Call drain() anytime after. <p>Serialized access is expected with
     * respect to element emission.
     *
     * @param error the exception to deliver
     */
    public final void terminate(Throwable error) {
        if (!terminated) {
            exception = error;
            terminated = true;
        }
    }

    /**
     * Move into a terminal state and drain.
     */
    public final void terminateAndDrain() {
        terminated = true;
        drain();
    }

    /**
     * Move into a terminal state with an exception and drain. <p>Serialized access is expected with respect to element
     * emission.
     *
     * @param error the exception to deliver
     */
    public final void terminateAndDrain(Throwable error) {
        if (!terminated) {
            exception = error;
            terminated = true;
            drain();
        }
    }

    @Override
    public final void request(long longN) {
        int n = longN >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) longN;
        if (n == 0) {
            return;
        }
        boolean mayDrain = requestedCount == 0;
        if (requestedCount != Integer.MAX_VALUE) {
            if (n == Integer.MAX_VALUE) {
                requestedCount = n;
                mayDrain = true;
            } else {
                if (requestedCount > Integer.MAX_VALUE - n) {
                    requestedCount = Integer.MAX_VALUE;
                } else {
                    requestedCount = requestedCount + n;
                }
            }
        }
        // since we implement producer, we have to call drain on a 0-n request transition
        if (mayDrain) {
            drain();
        }
    }

    /**
     * Try to drain the "queued" elements and terminal events by considering the available and requested event counts.
     */
    public final void drain() {
        if (emitting) {
            return;
        }
        emitting = true;
        GwtScheduler.SCHEDULER.scheduleIncremental(this);
    }

    static final boolean CONTINUE = true;
    static final boolean TERMINATE = false;

    @Override
    public boolean execute() {
        if (requestedCount > 0 || terminated) {
            Object o;
            if (terminated) {
                o = actual.peek();
                if (o == null) {
                    Throwable e = exception;
                    actual.complete(e);
                    return TERMINATE;
                }
                if (requestedCount == 0) {
                    return TERMINATE;
                }
            }
            o = actual.poll();
            if (o == null) {
                emitting = false;
                return TERMINATE;
            } else {
                if (actual.accept(o)) {
                    return TERMINATE;
                }
                requestedCount--;
            }
        } else {
            boolean more = actual.peek() != null;
            if (requestedCount == Integer.MAX_VALUE) {
                if (!(more || terminated)) {
                    emitting = false;
                    return TERMINATE;
                }
            } else {
                if ((requestedCount == 0 || !more) && !terminated) {
                    emitting = false;
                    return TERMINATE;
                }
            }
        }
        return CONTINUE;
    }
}
