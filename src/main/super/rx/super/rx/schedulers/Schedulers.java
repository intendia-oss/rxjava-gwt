package rx.schedulers;

import rx.Scheduler;
import rx.internal.schedulers.*;
import rx.internal.schedulers.ImmediateScheduler;
import rx.internal.schedulers.TrampolineScheduler;

public final class Schedulers {

    private Schedulers() {}

    public static Scheduler immediate() {
        return ImmediateScheduler.INSTANCE;
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
