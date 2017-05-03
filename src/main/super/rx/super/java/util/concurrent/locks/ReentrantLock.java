package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public class ReentrantLock implements Lock {
    @Override public void lock() { }

    @Override public void lockInterruptibly() throws InterruptedException { }

    @Override public boolean tryLock() { return true; }

    @Override public boolean tryLock(long time, TimeUnit unit) throws InterruptedException { return true; }

    @Override public void unlock() { }

    @Override public Condition newCondition() { throw new UnsupportedOperationException(); }
}
