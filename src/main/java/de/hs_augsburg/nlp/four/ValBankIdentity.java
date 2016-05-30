package de.hs_augsburg.nlp.four;

import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.IBank;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ValBankIdentity implements IBank {
    private final AtomicReference<ValBank> identity = new AtomicReference<>(ValBank.empty());

    @Override
    public void deposit(long accNo, int amount) {
        identity.updateAndGet(bank -> bank.deposit(accNo, amount));
    }

    @Override
    public void withdraw(long accNo, int amount) {
        identity.updateAndGet(bank -> bank.withdraw(accNo, amount));
    }

    @Override
    public void transfer(long from, long to, int amount) {
        identity.updateAndGet(bank -> bank.transfer(from, to, amount));
    }

    @Override
    public List<Entry> getAccountEntries(long accNo) {
        return identity.get().getAccountEntries(accNo);
    }

    @Override
    public List<Entry> getAccountEntries(List<Long> accNo) {
        return identity.get().getAccountEntries(accNo);
    }

    @Override
    public long createAccount() {
        return identity.getAndUpdate(bank -> identity.get().createAccount()).currentAccNo();
    }

    @Override
    public int getBalance(long accNo) {
        return identity.get().getBalance(accNo);
    }
}
