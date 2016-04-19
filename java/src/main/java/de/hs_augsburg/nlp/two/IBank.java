package de.hs_augsburg.nlp.two;

import de.hs_augsburg.nlp.two.BasicMonitor.Entry;

import java.util.List;

/**
 * Created by Peer on 19.04.2016.
 */
public interface IBank {
    void deposit(long accNo, int amount);

    void withdraw(long accNo, int amount);

    void transfer(long from, long to, int amount);

    List<Entry> getAccountEntries(long accNo);

    long createAccount();
}
