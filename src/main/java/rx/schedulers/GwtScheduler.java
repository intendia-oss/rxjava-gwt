package rx.schedulers;

import com.google.gwt.core.client.Scheduler;
import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.internal.schedulers.SchedulerLifecycle;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.Subscriptions;

public class GwtScheduler extends rx.Scheduler implements SchedulerLifecycle {
    public static Scheduler EXECUTOR = Scheduler.get();
    public static rx.Scheduler INSTANCE;

    public static rx.Scheduler instance() {
        if (INSTANCE == null) {
            INSTANCE = new GwtScheduler(EXECUTOR);
        }
        return INSTANCE;
    }

    private final Scheduler executor;

    public GwtScheduler(Scheduler executor) {
        this.executor = executor;
    }

    @Override
    public Worker createWorker() {
        return new InnerGwtWorker(executor);
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
    }

    static class InnerGwtWorker extends rx.Scheduler.Worker {
        private final Scheduler executor;
        private boolean isUnsubscribed;

        InnerGwtWorker(Scheduler executor) {
            this.executor = executor;
        }

        @Override
        public Subscription schedule(final Action0 action) {
            return schedule(action, 0, null);
        }

        @Override
        public Subscription schedule(final Action0 action, long delayTime, TimeUnit unit) {
            if (isUnsubscribed) {
                // don't schedule, we are unsubscribed
                return Subscriptions.empty();
            }
            return scheduleActual(action, delayTime, unit);
        }

        private ScheduledAction scheduleActual(final Action0 action, long delayTime, TimeUnit unit) {
            Action0 decoratedAction = RxJavaHooks.onScheduledAction(action);
            final ScheduledAction run = new ScheduledAction(decoratedAction);
            final SubscriptionRepeatingCommand command = new SubscriptionRepeatingCommand(run);
            if (delayTime <= 0 || unit == null) {
                executor.scheduleIncremental(command);
            } else {
                executor.scheduleFixedDelay(command, (int) unit.toMillis(delayTime));
            }
            run.add(command);

            return run;
        }

        @Override
        public void unsubscribe() {
            isUnsubscribed = true;
        }

        @Override
        public boolean isUnsubscribed() {
            return isUnsubscribed;
        }

    }

    private static class SubscriptionRepeatingCommand implements Scheduler.RepeatingCommand, Subscription {
        private final ScheduledAction run;
        private boolean isUnsubscribed;

        public SubscriptionRepeatingCommand(ScheduledAction run) {
            this.run = run;
        }

        @Override
        public boolean execute() {
            if (!isUnsubscribed) {
                run.run();
            }
            return false;
        }

        @Override
        public void unsubscribe() {
            isUnsubscribed = true;
        }

        @Override
        public boolean isUnsubscribed() {
            return isUnsubscribed;
        }
    }
}
