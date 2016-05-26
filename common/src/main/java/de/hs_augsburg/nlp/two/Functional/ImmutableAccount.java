package de.hs_augsburg.nlp.two.Functional;


import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.EntryType;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.List;

public class ImmutableAccount {
    public static final ImmutableAccount empty = new ImmutableAccount();
    private final int balance;
    private final Instant lastModified;
    private final PVector<Entry> entries;

    public ImmutableAccount() {
        balance = 0;
        lastModified = Instant.now();
        entries = TreePVector.empty();
    }

    public ImmutableAccount(int balance, PVector<Entry> entries) {
        this.balance = balance;
        this.entries = entries;
        this.lastModified = Instant.now();
    }

    public ImmutableAccount deposit(int a, String text) {
        int nextBalance = this.balance + a;
        PVector<Entry> nextEntries = entries.plus(new Entry(text, a, EntryType.DEPOSIT));
        return new ImmutableAccount(nextBalance, nextEntries);
    }

    public ImmutableAccount withdraw(int a, String text) {
        int nextBalance = this.balance - a;
        PVector<Entry> nextEntries = entries.plus(new Entry(text, a, EntryType.WITHDRAW));
        return new ImmutableAccount(nextBalance, nextEntries);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public int currentBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("balance", balance)
                .append("lastModified", lastModified)
                .append("entries size", entries.size())
                .toString();
    }
}
