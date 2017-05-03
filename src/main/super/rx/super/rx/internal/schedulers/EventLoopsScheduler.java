package rx.internal.schedulers;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import java.util.concurrent.ThreadFactory;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.GwtScheduler;
import rx.subscriptions.Subscriptions;

public final class EventLoopsScheduler extends GwtScheduler {
    public static final EventLoopsScheduler INSTANCE = new EventLoopsScheduler(null);

    public EventLoopsScheduler(ThreadFactory tf) {
        super(GwtScheduler.EXECUTOR);
    }

    public Subscription scheduleDirect(final Action0 action) {
        final Subscription s = Subscriptions.empty();
        GwtScheduler.EXECUTOR.scheduleFinally(new ScheduledCommand() {
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
