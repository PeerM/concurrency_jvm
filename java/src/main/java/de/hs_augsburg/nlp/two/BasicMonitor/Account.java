package de.hs_augsburg.nlp.two.BasicMonitor;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is Not Thread-safe
 */
public class Account {
    // only for debug toString?
    private final long id;
    private int balance;
    private Instant lastModified = Instant.now();
    private List<Entry> entries;

    public Account(long id) {
        this.id = id;
        entries = new LinkedList<>();
    }

    public void deposit(int a, String text) {
        balance += a;
        lastModified = Instant.now();
        entries.add(new Entry(text, a, EntryType.DEPOSIT));
    }

    public void withdraw(int a, String text) {
        balance -= a;
        lastModified = Instant.now();
        entries.add(new Entry(text, a, EntryType.WITHDRAW));
    }

    public List<Entry> getEntries() {
        //make a shallow copy
        return Collections.unmodifiableList(entries);
    }

    public int currentBalance(){
        return balance;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("balance", balance)
                .append("lastModified", lastModified)
                .append("entries", entries)
                .append("id", id)
                .toString();
    }
}
