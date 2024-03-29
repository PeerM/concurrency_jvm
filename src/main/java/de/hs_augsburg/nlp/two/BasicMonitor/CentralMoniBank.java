package de.hs_augsburg.nlp.two.BasicMonitor;


import de.hs_augsburg.nlp.two.IBank;

import java.util.*;

public class CentralMoniBank implements IBank {
    private Map<Long, UnsafeAccount> accounts;
    private NumberGenerator accNoGenerator;

    public CentralMoniBank() {
        accounts = new HashMap<>();
        accNoGenerator = new NumberGenerator();
    }

    public CentralMoniBank(Map<Long, UnsafeAccount> accounts, NumberGenerator accNoGenerator) {
        if (accounts == null) {
            this.accounts = new HashMap<>();
        } else {
            this.accounts = accounts;
        }
        this.accNoGenerator = accNoGenerator;
    }

    @Override
    public synchronized void deposit(long accNo, int amount) {
        getAcc(accNo).deposit(amount, "deposit: " + new Date().getTime());
    }

    @Override
    public synchronized void withdraw(long accNo, int amount) {
        getAcc(accNo).withdraw(amount, "withdraw: " + new Date().getTime());
    }

    @Override
    public synchronized void transfer(long from, long to, int amount) {
        long time = new Date().getTime();
        getAcc(from).withdraw(amount, "transfer form: " + time);
        getAcc(to).deposit(amount, "transfer to: " + time);
    }

    /**
     * It seems like this is not thread save, but here you get only the entries of one account
     * @param accNo
     * @return
     */
    @Override
    public synchronized List<Entry> getAccountEntries(long accNo) {
        return getAcc(accNo).getEntries();
    }

    /**
     * Down here you get the entries of multiple accounts and this is atomic and does not go against our invariant
     * TODO Maybe this should be changed to a map for easier use
     * @param accNos
     * @return
     */
    @Override
    public synchronized List<Entry> getAccountEntries(List<Long> accNos) {
        List<Entry> entries = new ArrayList<>();
        for (long accNo :
                accNos) {
            entries.addAll(getAcc(accNo).getEntries());
        }
        return entries;
    }

    private UnsafeAccount getAcc(long accNo) {
        return accounts.get(accNo);
    }

    @Override
    public synchronized long createAccount() {
        long accNo = accNoGenerator.getNext();
        UnsafeAccount acc = new UnsafeAccount(accNo);
        accounts.put(accNo, acc);
        return accNo;
    }

    @Override
    public synchronized int getBalance(long accNo) {
        return getAcc(accNo).currentBalance();
    }


    @Override
    public String toString() {
        return "Bank: Central Monitor";
    }
}
