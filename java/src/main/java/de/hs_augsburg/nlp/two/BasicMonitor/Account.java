package de.hs_augsburg.nlp.two.BasicMonitor;


import java.util.Date;
import java.util.List;

public class Account {
    private int balance;
    private Date lastModified = new Date();
    private List<Entry> entries;
    // only for debug toString?
    private final long id;

    public Account(long id) {
        this.id = id;
    }

    public void deposit(int a, String text){

    }

    public void withdraw(int a, String text){

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
