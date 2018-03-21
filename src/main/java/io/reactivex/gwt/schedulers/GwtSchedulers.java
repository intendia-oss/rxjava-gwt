package io.reactivex.gwt.schedulers;

import io.reactivex.Scheduler;

/** GWT-specific Schedulers. */
public final class GwtSchedulers {

    static final class UserDeferredHolder {
        static final Scheduler DEFAULT = new GwtScheduler(false);
    }

    static final class UserIncrementalHolder {
        static final Scheduler DEFAULT = new GwtScheduler(true);
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

    private GwtSchedulers() { }
}
