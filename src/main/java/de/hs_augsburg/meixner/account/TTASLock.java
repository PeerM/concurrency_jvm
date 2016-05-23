package de.hs_augsburg.meixner.account;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TTASLock implements Lock { 
    AtomicBoolean state = new AtomicBoolean(false); 
    
    public void lock() {
        while (true) {
            while (state.get()) {};
            if (!state.getAndSet(true)) 
                return;
        }    
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
