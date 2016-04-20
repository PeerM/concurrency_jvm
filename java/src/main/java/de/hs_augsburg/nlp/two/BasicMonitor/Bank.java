package de.hs_augsburg.nlp.two.BasicMonitor;


import de.hs_augsburg.nlp.two.IBank;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank implements IBank {
    private Map<Long, Account> accounts;
    private NumberGenerator accNoGenerator;

    public Bank() {
        accounts = new HashMap<>();
        accNoGenerator = new NumberGenerator();
    }

    public Bank(Map<Long, Account> accounts, NumberGenerator accNoGenerator) {
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
        getAcc(to).deposit(amount, "transfer to" + time);
    }

    @Override
    public synchronized List<Entry> getAccountEntries(long accNo) {
        return getAcc(accNo).getEntries();
    }

    private Account getAcc(long accNo) {
        return accounts.get(accNo);
    }

    @Override
    public synchronized long createAccount() {
        long accNo = accNoGenerator.getNext();
        Account acc = new Account(accNo);
        accounts.put(accNo, acc);
        return accNo;
    }
}