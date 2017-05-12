package rx.gwt.plugins;

import java.util.concurrent.atomic.AtomicReference;
import rx.annotations.Experimental;

public final class RxGwtPlugins {
    private static final RxGwtPlugins INSTANCE = new RxGwtPlugins();

    public static RxGwtPlugins getInstance() {
        return INSTANCE;
    }

    private final AtomicReference<RxGwtSchedulersHook> schedulersHook = new AtomicReference<RxGwtSchedulersHook>();

    private RxGwtPlugins() { }

    @Experimental
    public void reset() {
        schedulersHook.set(null);
    }

    public RxGwtSchedulersHook getSchedulersHook() {
        // We don't return from here but call get() again in case of thread-race so the winner will always get returned
        if (schedulersHook.get() == null) schedulersHook.compareAndSet(null, RxGwtSchedulersHook.getDefaultInstance());
        return schedulersHook.get();
    }

    public void registerSchedulersHook(RxGwtSchedulersHook impl) {
        if (!schedulersHook.compareAndSet(null, impl)) {
            throw new IllegalStateException("Another strategy was already registered: " + schedulersHook.get());
        }
    }
}
