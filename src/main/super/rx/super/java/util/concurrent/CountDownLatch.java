package java.util.concurrent;

public class CountDownLatch {
    private int count;

    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.count = count;
    }

    public void await() throws InterruptedException {
        if (count == 0) return;
        throw new UnsupportedOperationException();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (count == 0) return true;
        throw new UnsupportedOperationException();
    }

    public void countDown() {
        count--;
    }

    public long getCount() {
        return count;
    }
}
