package io.reactivex.gwt.schedulers;

import static java.util.Objects.requireNonNull;

import com.google.gwt.core.client.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;

public class GwtScheduler extends io.reactivex.Scheduler {
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
        private Scheduler executor;
        private final boolean incremental;

        GwtWorker(Scheduler executor, boolean incremental) {
            this.executor = requireNonNull(executor, "executor required");
            this.incremental = incremental;
        }

        @Override
        public Disposable schedule(Runnable action, long delayTime, TimeUnit unit) {
            if (executor == null) {
                return Disposables.empty();
            }

            action = RxJavaPlugins.onSchedule(action);

            ScheduledAction scheduledAction = new ScheduledAction(action);

            if (incremental && (delayTime <= 0 || unit == null)) {
                executor.scheduleIncremental(scheduledAction);
            } else {
                executor.scheduleFixedDelay(scheduledAction, (int) unit.toMillis(delayTime));
            }

            return scheduledAction;
        }

        @Override
        public void dispose() {
            executor = null;
        }

        @Override
        public boolean isDisposed() {
            return executor == null;
        }
    }

    private static class ScheduledAction implements Scheduler.RepeatingCommand, Disposable {
        private Runnable action;

        ScheduledAction(Runnable action) {
            this.action = action;
        }

        @Override
        public boolean execute() {
            if (action == null) {
                return false;
            }
            try {
                action.run();
            } catch (OnErrorNotImplementedException e) {
                signalError(new IllegalStateException(
                        "Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e));
            } catch (Throwable e) {
                signalError(new IllegalStateException(
                        "Fatal Exception thrown on Scheduler.Worker thread.", e));
            } finally {
                dispose();
            }
            return false;
        }

        void signalError(Throwable ie) {
            RxJavaPlugins.onError(ie);
            Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
        }

        @Override
        public void dispose() {
            action = null;
        }

        @Override
        public boolean isDisposed() {
            return action == null;
        }
    }
}
