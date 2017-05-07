package rx.plugins;

import java.util.concurrent.ThreadFactory;
import rx.Scheduler;
import rx.annotations.Experimental;
import rx.functions.Action0;

public class RxJavaSchedulersHook {

    private final static RxJavaSchedulersHook DEFAULT_INSTANCE = new RxJavaSchedulersHook();

    @Experimental
    public static Scheduler createComputationScheduler() {
        throw new UnsupportedOperationException();
    }

    @Experimental
    public static Scheduler createComputationScheduler(ThreadFactory threadFactory) {
        throw new UnsupportedOperationException();
    }

    @Experimental
    public static Scheduler createIoScheduler() {
        throw new UnsupportedOperationException();
    }

    @Experimental
    public static Scheduler createIoScheduler(ThreadFactory threadFactory) {
        throw new UnsupportedOperationException();
    }

    @Experimental
    public static Scheduler createNewThreadScheduler() {
        throw new UnsupportedOperationException();
    }

    @Experimental
    public static Scheduler createNewThreadScheduler(ThreadFactory threadFactory) {
        throw new UnsupportedOperationException();
    }

    public Scheduler getComputationScheduler() {
        return null;
    }

    public Scheduler getIOScheduler() {
        return null;
    }

    public Scheduler getNewThreadScheduler() {
        return null;
    }

    @Deprecated
    public Action0 onSchedule(Action0 action) {
        return action;
    }

    public static RxJavaSchedulersHook getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }
}
