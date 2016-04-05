package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.account.Account;

public class LessMonitorAccount implements Account {

    private int balance;

    public synchronized void  deposit(int i) {
        balance += i;
    }

    public synchronized boolean withdraw(int i) {
        if (balance >= i) { // don't allow overdrawing
            balance -= i;
            return true;
        }
        return false;
    }

    public int getBalance() {
        return balance;
    }

    public synchronized String toString() {
        return "Konto mit Stand: " + balance;
    }
}

