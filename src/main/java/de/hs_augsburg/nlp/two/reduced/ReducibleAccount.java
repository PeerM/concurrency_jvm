package de.hs_augsburg.nlp.two.reduced;


import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.EntryType;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.time.Instant;
import java.util.List;

public class ReducibleAccount {
    public static final ReducibleAccount empty = new ReducibleAccount();
    private final int balance;
    private final Instant lastModified;
    private final PVector<Entry> entries;

    public ReducibleAccount() {
        balance = 0;
        lastModified = Instant.now();
        entries = TreePVector.empty();
    }

    public ReducibleAccount(int balance, PVector<Entry> entries) {
        this.balance = balance;
        this.entries = entries;
        this.lastModified = Instant.now();
    }


    public static ReducibleAccount deposit(ReducibleAccount old, int a, String text) {
        if (old == null) {
            old = empty;
        }
        int nextBalance = old.balance + a;
        PVector<Entry> nextEntries = old.entries.plus(new Entry(text, a, EntryType.DEPOSIT));
        return new ReducibleAccount(nextBalance, nextEntries);
    }

    public static ReducibleAccount withdraw(ReducibleAccount old, int a, String text) {
        if (old == null) {
            old = empty;
        }
        int nextBalance = old.balance - a;
        PVector<Entry> nextEntries = old.entries.plus(new Entry(text, a, EntryType.WITHDRAW));
        return new ReducibleAccount(nextBalance, nextEntries);
    }

    public static PVector<Entry> reduceEntries(ReducibleAccount toAdd, PVector<Entry> last) {
        if (toAdd == null) {
            toAdd = empty;
        }
        return last.plusAll(toAdd.entries);
    }

    public static int reduceBalance(ReducibleAccount toAdd, int last) {
        if (toAdd == null) {
            toAdd = empty;
        }
        return toAdd.balance + last;
    }

    public static void reduceEntries(ReducibleAccount toAdd, List<Entry> last) {
        last.addAll(toAdd.entries);
    }

    public ReducibleAccount deposit(int a, String text) {
        return deposit(this, a, text);
    }

    public ReducibleAccount withdraw(int a, String text) {
        return withdraw(this, a, text);
    }

    public int reduceBalance(int last) {
        return reduceBalance(this, last);
    }

    public PVector<Entry> reduceEntries(PVector<Entry> last) {
        return reduceEntries(this, last);
    }

    public List<Entry> getEntries() {
        return entries;
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
