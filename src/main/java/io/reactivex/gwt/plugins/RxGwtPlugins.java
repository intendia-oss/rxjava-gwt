package io.reactivex.gwt.plugins;

public final class RxGwtPlugins {
    static com.google.gwt.core.client.Scheduler executor;

    public static com.google.gwt.core.client.Scheduler getExecutor() {
        if (executor == null) {
            executor = com.google.gwt.core.client.Scheduler.get();
        }
        return executor;
    }

    public static void setExecutor(com.google.gwt.core.client.Scheduler executor) {
        RxGwtPlugins.executor = executor;
    }

    public static void reset() {
        setExecutor(null);
    }

    private RxGwtPlugins() { }
}
