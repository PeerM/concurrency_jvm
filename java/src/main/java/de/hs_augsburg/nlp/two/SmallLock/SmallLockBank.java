package de.hs_augsburg.nlp.two.SmallLock;


import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.NumberGenerator;
import de.hs_augsburg.nlp.two.IBank;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmallLockBank implements IBank {
    private Map<Long, MonitorAccount> accounts;
    private NumberGenerator accNoGenerator;
    private ReadWriteLock readWriteLock;

    public SmallLockBank() {
        accounts = new Hashtable<>();
        accNoGenerator = new NumberGenerator();
        readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void deposit(long accNo, int amount) {
        getAcc(accNo).deposit(amount, "deposit: " + Instant.now().toString());
    }

    @Override
    public void withdraw(long accNo, int amount) {
        getAcc(accNo).withdraw(amount, "withdraw: " + Instant.now().toString());
    }

    @Override
    public void transfer(long from, long to, int amount) {
        readWriteLock.writeLock().lock();
        try {
            String time = Instant.now().toString();
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
            for (long accNo : accNos) {
                MonitorAccount acc = getAcc(accNo);
                synchronized (acc) {
                    List<Entry> entriesOfAcc = acc.getEntries();
                    entries.addAll(entriesOfAcc);
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return entries;
    }

    private MonitorAccount getAcc(long accNo) {
        return accounts.get(accNo);
    }

    @Override
    public long createAccount() {
        long accNo = accNoGenerator.getNext();
        MonitorAccount acc = new MonitorAccount(accNo);
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
