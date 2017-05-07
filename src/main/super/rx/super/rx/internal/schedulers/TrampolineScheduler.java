package rx.internal.schedulers;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;

public final class TrampolineScheduler extends Scheduler {
    private static final TrampolineScheduler INSTANCE = new TrampolineScheduler();

    public static TrampolineScheduler instance() {
        return INSTANCE;
    }

    @Override
    public Worker createWorker() {
        return new InnerCurrentThreadScheduler();
    }

    /* package accessible for unit tests */ TrampolineScheduler() { }

    private static class InnerCurrentThreadScheduler extends Scheduler.Worker implements Subscription {

        private int counter;
        private final Queue<TimedAction> queue = new PriorityQueue<TimedAction>();
        private final BooleanSubscription innerSubscription = new BooleanSubscription();
        private int wip;

        @Override
        public Subscription schedule(Action0 action) {
            return enqueue(action, now());
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (delayTime <= 0) return schedule(action);
            else throw new UnsupportedOperationException("trampoline scheduler do not support delayed actions on GWT");
        }

        private Subscription enqueue(Action0 action, long execTime) {
            if (innerSubscription.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            final TimedAction timedAction = new TimedAction(action, execTime, ++counter);
            queue.add(timedAction);

            if (wip++ == 0) {
                do {
                    final TimedAction polled = queue.poll();
                    if (polled != null) {
                        polled.action.call();
                    }
                } while (--wip > 0);
                return Subscriptions.unsubscribed();
            } else {
                // queue wasn't empty, a parent is already processing so we just add to the end of the queue
                return Subscriptions.create(new Action0() {

                    @Override
                    public void call() {
                        queue.remove(timedAction);
                    }

                });
            }
        }

        @Override
        public void unsubscribe() {
            innerSubscription.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return innerSubscription.isUnsubscribed();
        }

    }

    private static final class TimedAction implements Comparable<TimedAction> {
        final Action0 action;
        final Long execTime;
        final int count; // In case if time between enqueueing took less than 1ms

        private TimedAction(Action0 action, Long execTime, int count) {
            this.action = action;
            this.execTime = execTime;
            this.count = count;
        }

        @Override
        public int compareTo(TimedAction that) {
            int result = execTime.compareTo(that.execTime);
            if (result == 0) {
                return compare(count, that.count);
            }
            return result;
        }
    }

    // because I can't use Integer.compare from Java 7
    private static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

}
