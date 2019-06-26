package java.lang.reflect;

public class Array {
    private Array() {}

    public static Object newInstance(Class<?> componentType, int length) {
        return new Object[length];
    }
}
