package de.hs_augsburg.meixner.account;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TASLock implements Lock { 
    AtomicBoolean state = new AtomicBoolean(false); 
    
    public void lock() {
        while (state.getAndSet(true)) {}
    }
    
    public void unlock() {
        state.set(false);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
        
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        return state.compareAndSet(false,true);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}