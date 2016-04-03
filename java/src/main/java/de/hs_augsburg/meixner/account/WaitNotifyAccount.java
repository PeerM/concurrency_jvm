package de.hs_augsburg.meixner.account;

public class WaitNotifyAccount implements Account {

    private int balance;

    public synchronized void  deposit(int i) {
        balance += i;
        notifyAll(); // what happens if there is no notify at all
        // why not notify();
    }

    public synchronized boolean withdraw(int i) {
        while (balance < i)
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        balance -= i;
        return true;
    }

    public synchronized int getBalance() {
        return balance;
    }
    
    public synchronized String toString() {
        return "Konto mit Stand: " + balance;
    }
}
