package rx.schedulers;

import rx.Scheduler;

public final class Schedulers {
    private static final Schedulers INSTANCE = new Schedulers();

    private Schedulers() {}

    public static Scheduler immediate() {
        return ImmediateScheduler.instance();
    }

    public static Scheduler trampoline() {
        return GwtScheduler.instance();
    }

    public static Scheduler newThread() {
        return GwtScheduler.instance();
    }

    public static Scheduler computation() {
        return GwtScheduler.instance();
    }

    public static Scheduler io() {
        return GwtScheduler.instance();
    }

    public static TestScheduler test() {
        return new TestScheduler();
    }
}
