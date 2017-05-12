package rx.internal.schedulers;

import java.util.concurrent.Executor;
import rx.Scheduler;

public final class ExecutorScheduler extends Scheduler {

    public ExecutorScheduler(Executor executor) { }

    @Override
    public Worker createWorker() { throw new UnsupportedOperationException(); }
}
