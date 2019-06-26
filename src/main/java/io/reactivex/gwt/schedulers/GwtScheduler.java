package io.reactivex.gwt.schedulers;

import elemental2.dom.DomGlobal;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;

public class GwtScheduler extends io.reactivex.Scheduler {
    private final boolean incremental;

    public GwtScheduler(boolean incremental) {
        this.incremental = incremental;
    }

    @Override
    public Worker createWorker() {
        return new GwtWorker(incremental);
    }

    static class GwtWorker extends Worker {
        private boolean disposed = false;
        private final boolean incremental;

        GwtWorker(boolean incremental) {
            this.incremental = incremental;
        }

        @Override
        public Disposable schedule(Runnable action, long delayTime, TimeUnit unit) {
            action = RxJavaPlugins.onSchedule(action);

            final ScheduledAction scheduledAction = new ScheduledAction(action);

            if (incremental && (delayTime <= 0 || unit == null)) {
                Promise.resolve(0).then(new IThenable.ThenOnFulfilledCallbackFn<Integer, Object>() {
                    @Override public IThenable<Object> onInvoke(Integer o) {
                        if (!GwtWorker.this.isDisposed()) {
                            scheduledAction.run();
                        }
                        return null;
                    }
                });
            } else {
                DomGlobal.setTimeout(new DomGlobal.SetTimeoutCallbackFn() {
                    @Override public void onInvoke(Object... args) {
                        if (!GwtWorker.this.isDisposed()) {
                            scheduledAction.run();
                        }
                    }
                }, (int) unit.toMillis(delayTime));
            }

            return scheduledAction;
        }

        @Override
        public void dispose() {
            disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }

    private static class ScheduledAction implements Runnable, Disposable {
        private Runnable action;

        ScheduledAction(Runnable action) {
            this.action = action;
        }

        @Override
        public void run() {
            if (action == null) {
                return; // disposed
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
