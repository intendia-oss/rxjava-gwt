package java.lang;

import java.util.function.Supplier;

public class ThreadLocal<T> {

    private T value;

    protected T initialValue() {
        return null;
    }

    public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }

    public ThreadLocal() {
        value = initialValue();
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void remove() {
        value = null;
    }
}
