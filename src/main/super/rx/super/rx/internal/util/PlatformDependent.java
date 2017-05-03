package rx.internal.util;

public final class PlatformDependent {

    public static final int ANDROID_API_VERSION_IS_NOT_ANDROID = 0;

    private PlatformDependent() {
        throw new IllegalStateException("No instances!");
    }

    public static boolean isAndroid() {
        return false;
    }

    public static int getAndroidApiVersion() {
        return ANDROID_API_VERSION_IS_NOT_ANDROID;
    }
}
