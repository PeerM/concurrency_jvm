package de.hs_augsburg.meixner.account;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SpinLockFacadeAccount implements Account {

    private Lock lock = new ReentrantLock();
    private Account account = new UnsafeAccount();

    public  void  deposit(int i) {
        while (!lock.tryLock()) { };
        try {
           account.deposit(i);
        } finally {
           lock.unlock();
        }
    }
    
    public  boolean  withdraw(int i) {
        while (!lock.tryLock()) { };
        try {
           return account.withdraw(i);
        } finally {
           lock.unlock();
        }
    }

    public int getBalance() {
        while (!lock.tryLock()) { };
        try {
            return account.getBalance();
        } finally {
           lock.unlock();
        }
    }
    
    public String toString() {
        while (!lock.tryLock()) { };
        try {
            return "AccountFacade: " + account.toString();
        } finally {
           lock.unlock();
        }
    }
}

