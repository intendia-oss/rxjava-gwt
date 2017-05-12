package rx.internal.schedulers;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import java.util.concurrent.ThreadFactory;
import rx.Subscription;
import rx.functions.Action0;
import rx.gwt.schedulers.GwtScheduler;
import rx.gwt.schedulers.GwtSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * This is the default computation scheduler and it is preferred to be overriden because
 * <ul>
 * <li>apply this as the GWT default computation scheduler not forcing the user to define it's own hooks always</li>
 * <li>maintain the {@link EventLoopsScheduler#scheduleDirect} method which has some specific performance
 * improvements</li>
 * </ul>
 */
public final class EventLoopsScheduler extends GwtScheduler {

    public EventLoopsScheduler(ThreadFactory tf) {
        super(GwtSchedulers.executor(), true);
    }

    public Subscription scheduleDirect(final Action0 action) {
        final Subscription s = Subscriptions.empty();
        executor.scheduleFinally(new ScheduledCommand() {
            @Override public void execute() {
                if (s.isUnsubscribed()) {
                    return;
                }
                action.call();
            }
        });
        return s;
    }
}
