package java.lang;

public class Runtime {
    private static Runtime currentRuntime = new Runtime();

    public static Runtime getRuntime() {
        return currentRuntime;
    }

    private Runtime() {}

    public int availableProcessors() {
        return 1; // javascscript event loop
    }
}
