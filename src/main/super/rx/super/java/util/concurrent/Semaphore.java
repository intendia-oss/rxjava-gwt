package java.util.concurrent;

import java.util.Collection;
import java.util.Collections;

public class Semaphore implements java.io.Serializable {
    private int permits;
    private boolean fair;

    public Semaphore(int permits) {
        this(permits, false);
    }

    public Semaphore(int permits, boolean fair) {
        this.permits = permits; this.fair = fair;
    }

    public void acquire() throws InterruptedException {
        acquire(1);
    }

    public void acquireUninterruptibly() {
        acquireUninterruptibly(1);
    }

    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return tryAcquire();
    }

    public void release() {
        release(1);
    }

    public void acquire(int permits) throws InterruptedException {
        acquireUninterruptibly(permits);
    }

    public void acquireUninterruptibly(int permits) {
        if (!tryAcquire(permits)) throw new IllegalStateException("blocking not supported");
    }

    public boolean tryAcquire(int permits) {
        if (permits < 0) throw new IllegalArgumentException();
        if (this.permits < permits) return false;
        this.permits -= permits;
        return true;
    }

    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        return tryAcquire(permits);
    }

    public void release(int permits) {
        if (permits < 0) throw new IllegalArgumentException();
        this.permits += permits;
    }

    public int availablePermits() {
        return permits;
    }

    public int drainPermits() {
        reducePermits(permits);
        return permits;
    }

    protected void reducePermits(int reduction) {
        if (reduction < 0) throw new IllegalArgumentException();
        this.permits -= reduction;
    }

    public boolean isFair() {
        return fair;
    }

    public final boolean hasQueuedThreads() {
        return false;
    }

    public final int getQueueLength() {
        return 0;
    }

    protected Collection<Thread> getQueuedThreads() {
        return Collections.emptyList();
    }
}
