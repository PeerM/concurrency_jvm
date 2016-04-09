package de.hs_augsburg.nlp.one.account;

import de.hs_augsburg.meixner.account.Account;

import java.util.concurrent.atomic.LongAdder;

// interestingly this is slower than the Atomic Account on my two core (with hyperthreading) laptop
public class AdderAccount implements Account {


    // volatile may not be necessary but class gets constructed in one thread and the used in an other, so better save then sorry
    private volatile LongAdder balance;

    public AdderAccount() {
        balance = new LongAdder();
    }

    public synchronized void deposit(int i) {
        balance.add(i);
    }

    public synchronized boolean withdraw(int i) {

        if (balance.sum() >= i) {
            // Warning: this smells like check then act
            // This will not leave the total balance inconsistent
            // but it might allow overdrawing if the one adds negative, while an other ones sums up for the check
            // this could be considered a semantic change from the.
            // LongAdder Only gives us quiescent consistency
            balance.add(i * -1);
            return true;
        }
        return false;
    }

    public int getBalance() {
        return balance.intValue();
    }

    public synchronized String toString() {
        return "Konto mit Stand: " + balance.sum();
    }
}

