package rx.gwt.schedulers;

import static java.util.Objects.requireNonNull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.plugins.RxJavaHooks;
import rx.subscriptions.Subscriptions;

public class GwtScheduler extends rx.Scheduler {
    protected final Scheduler executor;
    private final boolean incremental;

    public GwtScheduler(Scheduler executor, boolean incremental) {
        this.executor = requireNonNull(executor, "executor required");
        this.incremental = incremental;
    }

    @Override
    public Worker createWorker() {
        return new GwtWorker(executor, incremental);
    }

    static class GwtWorker extends Worker {
        private final Scheduler executor;
        private final boolean incremental;
        private boolean unsubscribed;

        GwtWorker(Scheduler executor, boolean incremental) {
            this.executor = requireNonNull(executor, "executor required");
            this.incremental = incremental;
        }

        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action, 0, null);
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (unsubscribed) {
                return Subscriptions.unsubscribed();
            }

            action = RxJavaHooks.onScheduledAction(action);

            ScheduledAction scheduledAction = new ScheduledAction(action);

            if (incremental && (delayTime <= 0 || unit == null)) {
                executor.scheduleIncremental(scheduledAction);
            } else {
                executor.scheduleFixedDelay(scheduledAction, (int) unit.toMillis(delayTime));
            }

            return scheduledAction;
        }

        @Override
        public void unsubscribe() {
            unsubscribed = true;
        }

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }

    }

    private static class ScheduledAction implements RepeatingCommand, Subscription {
        private final Action0 action;
        private boolean unsubscribed;

        ScheduledAction(Action0 action) {
            this.action = action;
        }

        @Override
        public boolean execute() {
            if (unsubscribed) {
                return false;
            }
            try {
                action.call();
            } catch (OnErrorNotImplementedException e) {
                signalError(new IllegalStateException(
                        "Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e));
            } catch (Throwable e) {
                signalError(new IllegalStateException(
                        "Fatal Exception thrown on Scheduler.Worker thread.", e));
            } finally {
                unsubscribe();
            }
            return false;
        }

        void signalError(Throwable ie) {
            RxJavaHooks.onError(ie);
            Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
        }

        @Override
        public void unsubscribe() {
            unsubscribed = true;
        }

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }
    }
}
