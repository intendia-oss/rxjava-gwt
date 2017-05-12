package rx.internal.schedulers;

import java.util.concurrent.ThreadFactory;
import rx.gwt.plugins.RxGwtPlugins;
import rx.gwt.schedulers.GwtScheduler;
import rx.gwt.schedulers.GwtSchedulers;

/**
 * This is the default io scheduler and it is preferred to be overriden because
 * <ul>
 * <li>apply this as the GWT default computation scheduler not forcing the user to define it's own hooks always</li>
 * </ul>
 */
public final class CachedThreadScheduler extends GwtScheduler {

    public CachedThreadScheduler(ThreadFactory tf) {
        super(GwtSchedulers.executor(), false);
    }
}
