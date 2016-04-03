package de.hs_augsburg.meixner.account;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicAccount implements Account{

    private AtomicInteger balance = new AtomicInteger();

    public void deposit(int i) {
        balance.addAndGet(i);
    }

    public boolean withdraw(int amount) {
        while (true) {
            int currentValue = balance.get();
            if (currentValue < amount)  // do not allow overdrawing
//                return false; // don't wait for money
                continue; // wait for money in spinning mode
            if (balance.compareAndSet(currentValue, currentValue-amount))
                return true;
        }
    }

    public int getBalance() {
        return balance.get();
    }
    
    public String toString() {
        return "Konto mit Stand: " + balance.get();
    }
}
