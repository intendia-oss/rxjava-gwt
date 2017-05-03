package rx.internal.schedulers;

import java.util.concurrent.ThreadFactory;
import rx.Scheduler;

public final class NewThreadScheduler extends Scheduler {

    public NewThreadScheduler(ThreadFactory threadFactory) { }

    @Override
    public Worker createWorker() { throw new UnsupportedOperationException(); }
}
