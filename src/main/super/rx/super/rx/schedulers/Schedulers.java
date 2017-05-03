package rx.schedulers;

import rx.Scheduler;
import rx.internal.schedulers.EventLoopsScheduler;
import rx.internal.schedulers.TrampolineScheduler;

public final class Schedulers {

    private Schedulers() {}

    public static Scheduler immediate() {
        return GwtScheduler.instance();
    }

    public static Scheduler trampoline() {
        return TrampolineScheduler.instance();
    }

    public static Scheduler computation() {
        return EventLoopsScheduler.INSTANCE;
    }

    public static Scheduler io() {
        return GwtScheduler.instance();
    }
}
