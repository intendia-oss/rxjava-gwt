package rx.schedulers;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.subscriptions.Subscriptions;

public class GwtScheduler extends Scheduler {

    public static final GwtScheduler INSTANCE = new GwtScheduler();
    public static final com.google.gwt.core.client.Scheduler SCHEDULER = com.google.gwt.core.client.Scheduler.get();

    static GwtScheduler instance() {
        return INSTANCE;
    }

    GwtScheduler() {}

    @Override
    public Worker createWorker() {
        return new InnerGwtWorker();
    }

    private static class InnerGwtWorker extends Scheduler.Worker {
        private final com.google.gwt.core.client.Scheduler executor;
        private final RxJavaSchedulersHook schedulersHook;
        volatile boolean isUnsubscribed;

        public InnerGwtWorker() {
            executor = SCHEDULER;
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

    private static class SubscriptionRepeatingCommand implements RepeatingCommand, Subscription {
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
