package de.hs_augsburg.nlp.two.SmallLock;


import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.NumberGenerator;
import de.hs_augsburg.nlp.two.BasicMonitor.UnsafeAccount;
import de.hs_augsburg.nlp.two.IBank;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmallMoniBank implements IBank {
    private Map<Long, UnsafeAccount> accounts;
    private NumberGenerator accNoGenerator;
    private ReadWriteLock readWriteLock;

    public SmallMoniBank() {
        accounts = new Hashtable<>();
        accNoGenerator = new NumberGenerator();
        readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void deposit(long accNo, int amount) {
        getAcc(accNo).deposit(amount, "deposit: " + new Date().getTime());
    }

    @Override
    public void withdraw(long accNo, int amount) {
        getAcc(accNo).withdraw(amount, "withdraw: " + new Date().getTime());
    }

    @Override
    public void transfer(long from, long to, int amount) {
        readWriteLock.writeLock().lock();
        try {
            long time = new Date().getTime();
            getAcc(from).withdraw(amount, "transfer form: " + time);
            getAcc(to).deposit(amount, "transfer to: " + time);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * It seems like this is not thread save, but here you get only the entries of one account
     *
     * @param accNo
     * @return
     */
    @Override
    public List<Entry> getAccountEntries(long accNo) {
        return getAcc(accNo).getEntries();
    }

    /**
     * Down here you get the entries of multiple accounts and this is atomic and does not go against our invariant
     * TODO Maybe this should be changed to a map for easier use
     *
     * @param accNos
     * @return
     */
    @Override
    public List<Entry> getAccountEntries(List<Long> accNos) {
        List<Entry> entries = new ArrayList<>();
        readWriteLock.readLock().lock();
        try {
            for (long accNo :
                    accNos) {
                entries.addAll(getAcc(accNo).getEntries());
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return entries;
    }

    private UnsafeAccount getAcc(long accNo) {
        return accounts.get(accNo);
    }

    @Override
    public long createAccount() {
        long accNo = accNoGenerator.getNext();
        UnsafeAccount acc = new UnsafeAccount(accNo);
        accounts.put(accNo, acc);
        return accNo;
    }

    @Override
    public int getBalance(long accNo) {
        return getAcc(accNo).currentBalance();
    }


    @Override
    public String toString() {
        return "Bank: Smaller Locks";
    }
}
