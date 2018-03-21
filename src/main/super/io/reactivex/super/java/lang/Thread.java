package java.lang;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;

public class Thread {
    public final static int MIN_PRIORITY = 1;
    public final static int NORM_PRIORITY = 5;
    public final static int MAX_PRIORITY = 10;
    private static final Thread EVENT_LOOP = new Thread(true);

    @FunctionalInterface @JsFunction interface ReThrow { void run() throws Throwable; }
    private static Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = (t, e) -> {
        Promise.resolve(0).then(Js.uncheckedCast(((ReThrow) () -> { throw e; })));
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

    public int getPriority() {
        return NORM_PRIORITY;
    }

    public void setPriority(int priority) {
        throw new UnsupportedOperationException();
    }
}
