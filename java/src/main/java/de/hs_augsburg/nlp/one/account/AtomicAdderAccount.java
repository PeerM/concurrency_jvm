package de.hs_augsburg.nlp.one.account;

import de.hs_augsburg.meixner.account.Account;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class AtomicAdderAccount implements Account {

    private LongAdder deposits;
    private AtomicLong withdrawals;


    public AtomicAdderAccount() {
        deposits = new LongAdder();
        withdrawals = new AtomicLong();
    }

    public synchronized void deposit(int i) {
        deposits.add(i);
    }

    public boolean withdraw(int amount) {
        // withdraw performance is worse than AdderAccount, but you can't overdraw this account.
        // long deposited = deposits.sum();
        while (true) {
            long currentValue = withdrawals.get();
            // Summing the deposits every time makes this critical section bigger.
            // There is the possibility of making the trade off,
            // of summing only once and having a few more withdraw calls returning false.
            if (deposits.sum() - currentValue < amount)  // do not allow overdrawing
                return false;
            if (withdrawals.compareAndSet(currentValue, currentValue + amount))
                return true;
        }
    }

    public int getBalance() {
        return (int) (deposits.sum() - withdrawals.get());
    }

    public synchronized String toString() {
        return "Konto mit Stand: " + getBalance();
    }
}
