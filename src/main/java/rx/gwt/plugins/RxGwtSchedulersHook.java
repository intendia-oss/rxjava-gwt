package rx.gwt.plugins;

import rx.Scheduler;
import rx.functions.Action0;
import rx.gwt.schedulers.GwtSchedulers;

public class RxGwtSchedulersHook {
    private static final RxGwtSchedulersHook DEFAULT_INSTANCE = new RxGwtSchedulersHook();

    public static RxGwtSchedulersHook getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /**
     * GWT Scheduler to use by various {@link GwtSchedulers#animationFrame()} schedulers or {@code null} if default
     * ({@link com.google.gwt.core.client.Scheduler#get()}) should be used.
     * <p>
     * This instance should be or behave like a stateless singleton.
     */
    public com.google.gwt.core.client.Scheduler getGwtScheduler() {
        return null;
    }

    /**
     * Scheduler to return from {@link GwtSchedulers#animationFrame()} or {@code null} if default should be used.
     * <p>
     * This instance should be or behave like a stateless singleton.
     */
    public Scheduler getAnimationFrameScheduler() {
        return null;
    }


    /**
     * Scheduler to return from {@link GwtSchedulers#requestIdle()} or {@code null} if default should be used.
     * <p>
     * This instance should be or behave like a stateless singleton.
     */
    public Scheduler getRequestIdleScheduler() {
        return null;
    }



    /**
     * Invoked before the Action is handed over to the scheduler.  Can be used for
     * wrapping/decorating/logging. The default is just a passthrough.
     *
     * @param action action to schedule
     * @return wrapped action to schedule
     */
    public Action0 onSchedule(Action0 action) {
        return action;
    }
}
