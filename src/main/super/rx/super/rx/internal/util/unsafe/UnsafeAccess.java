package rx.internal.util.unsafe;

public final class UnsafeAccess {
    private UnsafeAccess() {
        throw new IllegalStateException("No instances!");
    }

    public static boolean isUnsafeAvailable() {
        return false;
    }

    public static int getAndIncrementInt(Object obj, long offset) {
        throw new UnsupportedOperationException();
    }

    public static int getAndAddInt(Object obj, long offset, int n) {
        throw new UnsupportedOperationException();
    }

    public static int getAndSetInt(Object obj, long offset, int newValue) {
        throw new UnsupportedOperationException();
    }

    public static boolean compareAndSwapInt(Object obj, long offset, int expected, int newValue) {
        throw new UnsupportedOperationException();
    }

    public static long addressOf(Class<?> clazz, String fieldName) {
        throw new UnsupportedOperationException();
    }
}
