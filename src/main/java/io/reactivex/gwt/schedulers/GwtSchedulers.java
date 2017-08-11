package io.reactivex.gwt.schedulers;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import io.reactivex.Scheduler;
import io.reactivex.gwt.plugins.RxGwtPlugins;

/** GWT-specific Schedulers. */
public final class GwtSchedulers {

    static final class UserDeferredHolder {
        static final Scheduler DEFAULT = new GwtScheduler(executor(), false);
    }

    static final class UserIncrementalHolder {
        static final Scheduler DEFAULT = new GwtScheduler(executor(), true);
    }

    public static com.google.gwt.core.client.Scheduler executor() {
        return RxGwtPlugins.getExecutor();
    }

    public static Scheduler deferredScheduler() {
        return UserDeferredHolder.DEFAULT;
    }

    public static Scheduler incrementalScheduler() {
        return UserIncrementalHolder.DEFAULT;
    }

    /** An animation frame friendly {@link Scheduler} (use standard scheduled if unsupported). */
    public static Scheduler animationFrame() {
        return deferredScheduler();
    }

    /** An request idle friendly {@link Scheduler} (use standard scheduled if unsupported). */
    public static Scheduler requestIdle() {
        return deferredScheduler();
    }

    /**
     * A {@link Scheduler} which executes actions on {@code scheduler} optionally using {@link
     * com.google.gwt.core.client.Scheduler#scheduleIncremental(RepeatingCommand)} for immediately execution.
     */
    public static Scheduler from(com.google.gwt.core.client.Scheduler scheduler, boolean incremental) {
        if (scheduler == null) throw new NullPointerException("scheduler required");
        return new GwtScheduler(scheduler, incremental);
    }

    private GwtSchedulers() { }
}
