package rx.internal.schedulers;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;

public final class ImmediateScheduler extends Scheduler {
    public static final ImmediateScheduler INSTANCE = new ImmediateScheduler();

    private ImmediateScheduler() {
        // the class is singleton
    }

    @Override
    public Worker createWorker() {
        return new InnerImmediateScheduler();
    }

    private class InnerImmediateScheduler extends Scheduler.Worker implements Subscription {

        final BooleanSubscription innerSubscription = new BooleanSubscription();

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (delayTime <= 0) return schedule(action);
            else throw new UnsupportedOperationException("immediate scheduler do not support delayed actions on GWT");
        }

        @Override
        public Subscription schedule(Action0 action) {
            action.call();
            return Subscriptions.unsubscribed();
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

}
