package de.hs_augsburg.nlp.two.SmallLock;


import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.EntryType;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is Not Thread-safe
 */
public class MonitorAccount {
    // only for debug toString?
    private final long id;
    private volatile int balance;
    private Instant lastModified = Instant.now();
    private List<Entry> entries;

    public MonitorAccount(long id) {
        this.id = id;
        entries = new LinkedList<>();
    }

    public synchronized void deposit(int a, String text) {
        balance += a;
        lastModified = Instant.now();
        entries.add(new Entry(text, a, EntryType.DEPOSIT));
    }

    public synchronized void withdraw(int a, String text) {
        balance -= a;
        lastModified = Instant.now();
        entries.add(new Entry(text, a, EntryType.WITHDRAW));
    }

    public synchronized List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public int currentBalance(){
        return balance;
    }

    @Override
    public synchronized String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("balance", balance)
                .append("lastModified", lastModified)
                .append("entries", entries)
                .append("id", id)
                .toString();
    }
}
