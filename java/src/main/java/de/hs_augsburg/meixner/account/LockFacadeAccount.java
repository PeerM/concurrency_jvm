package de.hs_augsburg.meixner.account;

import java.util.concurrent.locks.Lock;


public class LockFacadeAccount implements Account {

    private Lock lock ;
    private Account account = new UnsafeAccount();

    public LockFacadeAccount(Lock lock) {
        this.lock = lock;
    }
    public  void  deposit(int i) {
        lock.lock();
        try {
           account.deposit(i);
        } finally {
           lock.unlock();
        }
    }
    
    public  boolean  withdraw(int i) {
        lock.lock();
        try {
           return account.withdraw(i);
        } finally {
           lock.unlock();
        }
    }

    public int getBalance() {
        lock.lock();
        try {
            return account.getBalance();
        } finally {
           lock.unlock();
        }
    }
    
    public String toString() {
        lock.lock();
        try {
            return "AccountFacade: " + account.toString();
        } finally {
           lock.unlock();
        }
    }
}
