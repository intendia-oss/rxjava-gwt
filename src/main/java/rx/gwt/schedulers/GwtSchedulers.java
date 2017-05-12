package rx.gwt.schedulers;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import rx.Scheduler;
import rx.annotations.Experimental;
import rx.gwt.plugins.RxGwtPlugins;
import rx.gwt.plugins.RxGwtSchedulersHook;

/** GWT-specific Schedulers. */
public final class GwtSchedulers {
    private static GwtSchedulers INSTANCE;

    private final com.google.gwt.core.client.Scheduler executor;
    private final Scheduler animationFrameScheduler;
    private final Scheduler requestIdleScheduler;

    private static GwtSchedulers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GwtSchedulers();
        }
        return INSTANCE;
    }

    private GwtSchedulers() {
        RxGwtSchedulersHook hook = RxGwtPlugins.getInstance().getSchedulersHook();

        com.google.gwt.core.client.Scheduler executor = hook.getGwtScheduler();
        if (executor != null) this.executor = executor;
        else this.executor = com.google.gwt.core.client.Scheduler.get();

        Scheduler animationFrame = hook.getAnimationFrameScheduler();
        if (animationFrame != null) animationFrameScheduler = animationFrame;
        else animationFrameScheduler = from(this.executor, false);

        Scheduler requestIdle = hook.getRequestIdleScheduler();
        if (requestIdle != null) requestIdleScheduler = requestIdle;
        else requestIdleScheduler = from(this.executor, false);
    }

    public static com.google.gwt.core.client.Scheduler executor() {
        return getInstance().executor;
    }

    /** An animation frame friendly {@link Scheduler} (use standard scheduled if unsupported). */
    public static Scheduler animationFrame() {
        return getInstance().animationFrameScheduler;
    }

    /** An request idle friendly {@link Scheduler} (use standard scheduled if unsupported). */
    public static Scheduler requestIdle() {
        return getInstance().requestIdleScheduler;
    }

    /**
     * A {@link Scheduler} which executes actions on {@code scheduler} optionally using {@link
     * com.google.gwt.core.client.Scheduler#scheduleIncremental(RepeatingCommand)} for immediately execution.
     */
    public static Scheduler from(com.google.gwt.core.client.Scheduler scheduler, boolean incremental) {
        if (scheduler == null) throw new NullPointerException("scheduler required");
        return new GwtScheduler(scheduler, incremental);
    }

    /**
     * Resets the current {@link GwtSchedulers} instance. This will re-init the cached schedulers on the next usage,
     * which can be useful in testing.
     */
    @Experimental
    public static void reset() {
        INSTANCE = null;
    }
}
