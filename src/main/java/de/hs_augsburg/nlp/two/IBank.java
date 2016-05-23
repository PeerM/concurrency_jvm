package de.hs_augsburg.nlp.two;

import de.hs_augsburg.nlp.two.BasicMonitor.Entry;

import java.util.List;

public interface IBank {
    void deposit(long accNo, int amount);

    void withdraw(long accNo, int amount);

    void transfer(long from, long to, int amount);

    List<Entry> getAccountEntries(long accNo);

    List<Entry> getAccountEntries(List<Long> accNo);

    long createAccount();

    int getBalance(long accNo);
}
