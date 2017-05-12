package rx.internal.schedulers;

public final class GenericScheduledExecutorService implements SchedulerLifecycle {

    public final static GenericScheduledExecutorService INSTANCE = new GenericScheduledExecutorService();

    private GenericScheduledExecutorService() { }

    @Override
    public void start() { }

    @Override
    public void shutdown() { }
}
