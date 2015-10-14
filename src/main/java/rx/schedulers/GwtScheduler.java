package rx.schedulers;

import com.google.gwt.core.client.Scheduler;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.subscriptions.Subscriptions;

public class GwtScheduler extends rx.Scheduler {
    public static rx.Scheduler INSTANCE;

    public static rx.Scheduler instance() {
        if (INSTANCE == null) {
            INSTANCE = new GwtScheduler(Scheduler.get());
        }
        return INSTANCE;
    }

    private final Scheduler executor;

    public GwtScheduler(Scheduler executor) {
        this.executor = executor;
    }

    @Override
    public Worker createWorker() {
        return new InnerGwtWorker();
    }

    private class InnerGwtWorker extends rx.Scheduler.Worker {
        private final RxJavaSchedulersHook schedulersHook;
        private volatile boolean isUnsubscribed;

        public InnerGwtWorker() {
            schedulersHook = RxJavaPlugins.getInstance().getSchedulersHook();
        }

        @Override
        public Subscription schedule(final Action0 action) {
            return schedule(action, 0, null);
        }

        @Override
        public Subscription schedule(final Action0 action, long delayTime, @Nullable TimeUnit unit) {
            if (isUnsubscribed) {
                // don't schedule, we are unsubscribed
                return Subscriptions.empty();
            }
            return scheduleActual(action, delayTime, unit);
        }

        public ScheduledAction scheduleActual(final Action0 action, long delayTime, @Nullable TimeUnit unit) {
            Action0 decoratedAction = schedulersHook.onSchedule(action);
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
