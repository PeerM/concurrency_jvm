package de.hs_augsburg.nlp.four;

import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.IBank;

import java.util.List;

/***
 * Scala can not directly implement an Interface with primitive datatypes as parameters, this is a straight forward adapter to make the AtomicBank conform to the interface
 */
public class ScalaBankAdapter implements IBank {
    private final AtomicBank bank = new AtomicBank();

    @Override
    public void deposit(long accNo, int amount) {
        bank.deposit(accNo, amount);
    }

    @Override
    public void withdraw(long accNo, int amount) {
        bank.withdraw(accNo, amount);
    }

    @Override
    public void transfer(long from, long to, int amount) {
        bank.transfer(from, to, amount);
    }

    @Override
    public List<Entry> getAccountEntries(long accNo) {
        return bank.getAccountEntries(accNo);
    }

    @Override
    public List<Entry> getAccountEntries(List<Long> accNo) {
        return bank.getAccountEntries(accNo);
    }

    @Override
    public long createAccount() {
        return bank.createAccount();
    }

    @Override
    public int getBalance(long accNo) {
        return bank.getBalance(accNo);
    }
}
