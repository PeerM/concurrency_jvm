package de.hs_augsburg.nlp.two.Functional;

import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.INumberGenerator;
import de.hs_augsburg.nlp.two.BasicMonitor.NumberGenerator;
import de.hs_augsburg.nlp.two.IBank;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CasBank implements IBank {
    private AtomicReference<PMap<Long, ImmutableAccount>> accounts;
    private INumberGenerator accNoGenerator;

    public CasBank() {
        accounts = new AtomicReference<>(HashTreePMap.empty());
        accNoGenerator = new AtomicNumberGenerator();
    }

    @Override
    public void deposit(long accNo, int amount) {
        accounts.getAndUpdate(
                accs -> accs
                        .plus(accNo, accs.get(accNo).deposit(amount, "deposit: " + new Date().getTime())));
    }

    @Override
    public void withdraw(long accNo, int amount) {
        accounts.getAndUpdate(
                accs -> accs
                        .plus(accNo, accs.get(accNo).withdraw(amount, "withdraw: " + new Date().getTime())));
    }

    @Override
    public void transfer(long from, long to, int amount) {
        long time = new Date().getTime();
        accounts.getAndUpdate(
                accs -> accs
                        .plus(from, accs.get(from).withdraw(amount, "transfer form: " + time))
                        .plus(to, accs.get(to).deposit(amount, "transfer to: " + time)));
    }

    /**
     * @param accNo
     * @return
     */
    @Override
    public List<Entry> getAccountEntries(long accNo) {
        return getAcc(accNo).getEntries();
    }

    /**
     * TODO Maybe this should be changed to a map for easier use
     *
     * @param accNos
     * @return
     */
    @Override
    public List<Entry> getAccountEntries(List<Long> accNos) {
        PMap<Long, ImmutableAccount> accountPMap = accounts.get();
        List<Entry> entries = new ArrayList<>();
        for (long accNo : accNos) {
            entries.addAll((accountPMap.get(accNo).getEntries()));
        }
        return entries;
    }

    private ImmutableAccount getAcc(long accNo) {
        return accounts.get().get(accNo);
    }

    @Override
    public long createAccount() {
        long accNo = accNoGenerator.getNext();
        ImmutableAccount acc = new ImmutableAccount(accNo);
        accounts.updateAndGet(accs -> accs.plus(accNo, acc));
        return accNo;
    }

    @Override
    public int getBalance(long accNo) {
        return accounts.get().get(accNo).currentBalance();
    }


    @Override
    public String toString() {
        return "Bank: Cas";
    }
}
