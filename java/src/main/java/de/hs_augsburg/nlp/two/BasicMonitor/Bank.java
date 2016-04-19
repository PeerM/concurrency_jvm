package de.hs_augsburg.nlp.two.BasicMonitor;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank {
    private Map<Long, Account> accounts;
    private NumberGenerator accNoGenerator;

    public Bank() {
        accounts = new HashMap<>();
        accNoGenerator = new NumberGenerator();
    }

    public synchronized void deposit(long accNo, int amount) {
        getAcc(accNo).deposit(amount, "deposit: " + new Date().getTime());
    }

    public synchronized void withdraw(long accNo, int amount) {
        getAcc(accNo).withdraw(amount, "withdraw: " + new Date().getTime());
    }

    public synchronized void transfer(long from, long to, int amount) {
        long time = new Date().getTime();
        getAcc(from).withdraw(amount, "transfer form: " + time);
        getAcc(to).deposit(amount, "transfer to" + time);
    }

    public synchronized List<Entry> getAccountEntries(long accNo) {
        return getAcc(accNo).getEntries();
    }

    private Account getAcc(long accNo) {
        return accounts.get(accNo);
    }

    public synchronized long createAccount() {
        long accNo = accNoGenerator.getNext();
        Account acc = new Account(accNo);
        accounts.put(accNo, acc);
        return accNo;
    }
}
