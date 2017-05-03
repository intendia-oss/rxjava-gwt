package java.lang;

import com.google.gwt.core.client.GWT;

public class Thread {
    private static final Thread EVENT_LOOP = new Thread(true);

    private static UncaughtExceptionHandler defaultUncaughtExceptionHandler = new UncaughtExceptionHandler() {
        @Override public void uncaughtException(Thread t, Throwable e) {
            GWT.getUncaughtExceptionHandler().onUncaughtException(e);
        }
    };

    private UncaughtExceptionHandler uncaughtExceptionHandler;

    public static Thread currentThread() {
        return EVENT_LOOP;
    }

    public static void sleep(long millis) throws InterruptedException {
        throw new UnsupportedOperationException("Blocking not supported");
    }

    public interface UncaughtExceptionHandler {
        void uncaughtException(Thread t, Throwable e);
    }

    public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return defaultUncaughtExceptionHandler;
    }

    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
        defaultUncaughtExceptionHandler = eh;
    }

    public Thread() {
        this(false);
    }

    public Thread(Runnable target, String name) {
        this(false);
    }

    private Thread(boolean inner) {
        if (!inner) throw new UnsupportedOperationException("Multi-thread not supported");
    }

    public String getName() {
        return "event-loop";
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler == null ? defaultUncaughtExceptionHandler : uncaughtExceptionHandler;
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
        uncaughtExceptionHandler = eh;
    }

    public void interrupt() {
        // Event loop always returns immediately because it is always the same thread
    }

    public StackTraceElement[] getStackTrace() {
        return new Exception().getStackTrace();
    }

    public final void setDaemon(boolean on) {
        throw new UnsupportedOperationException();
    }
}
