package de.hs_augsburg.meixner.account;

public interface Account {
    void deposit(int i);
    boolean withdraw(int i);
    int getBalance();
}