package rx.schedulers;

import rx.Scheduler;

public final class Schedulers {
    private Schedulers() {}

    public static Scheduler immediate() {
        return GwtScheduler.instance();
    }

    public static Scheduler trampoline() {
        return TrampolineScheduler.instance();
    }

    public static Scheduler computation() {
        return GwtScheduler.instance();
    }

    public static Scheduler io() {
        return GwtScheduler.instance();
    }
}
