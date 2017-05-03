
package rx.plugins;

import java.util.concurrent.ThreadFactory;
import rx.Scheduler;
import rx.annotations.Experimental;
import rx.functions.Action0;
import rx.internal.schedulers.CachedThreadScheduler;
import rx.internal.schedulers.EventLoopsScheduler;
import rx.internal.schedulers.NewThreadScheduler;
import rx.internal.util.RxThreadFactory;
import rx.schedulers.GwtScheduler;
import rx.schedulers.Schedulers;

public class RxJavaSchedulersHook {

    private final static RxJavaSchedulersHook DEFAULT_INSTANCE = new RxJavaSchedulersHook();

    @Experimental
    public static Scheduler createComputationScheduler() {
        return GwtScheduler.instance();
    }

    @Experimental
    public static Scheduler createComputationScheduler(ThreadFactory threadFactory) {
        return GwtScheduler.instance();
    }

    @Experimental
    public static Scheduler createIoScheduler() {
        return GwtScheduler.instance();
    }

    @Experimental
    public static Scheduler createIoScheduler(ThreadFactory threadFactory) {
        return GwtScheduler.instance();
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
