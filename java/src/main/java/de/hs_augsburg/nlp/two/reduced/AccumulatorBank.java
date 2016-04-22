package de.hs_augsburg.nlp.two.reduced;

import de.hs_augsburg.meixner.account.Account;
import de.hs_augsburg.nlp.two.BasicMonitor.Entry;
import de.hs_augsburg.nlp.two.BasicMonitor.INumberGenerator;
import de.hs_augsburg.nlp.two.Functional.AtomicNumberGenerator;
import de.hs_augsburg.nlp.two.IBank;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.UnaryOperator;

public class AccumulatorBank implements IBank {
    private static final ThreadLocal<Integer> threadIndex = ThreadLocal.withInitial(() -> -1);
    private final int arraySize;
    private final AtomicReferenceArray<PMap<Long, ReducibleAccount>> accountReferences;
    private INumberGenerator accNoGenerator;
    private AtomicReference<PVector<Long>> validAccNos;

    public AccumulatorBank(int arraySize) {
        this.arraySize = arraySize;
        accNoGenerator = new AtomicNumberGenerator();
        validAccNos = new AtomicReference<>(TreePVector.empty());
        accountReferences = new AtomicReferenceArray<>(arraySize);
        for (int i = 0; i < arraySize; i++) {
            // maybe this should not be initialized, to make it easier to check for used/ unused buckets
            accountReferences.lazySet(i, HashTreePMap.empty());
        }
    }

    private int getThreadIndex() {
        int i;
        if ((i = threadIndex.get()) == -1)
            return changeThreadIndex();
        return i;
    }

    private int changeThreadIndex() {
        int value = ThreadLocalRandom.current().nextInt(arraySize);
        threadIndex.set(value);
        return value;
    }

    private void changeAccount(long accNo, UnaryOperator<ReducibleAccount> operation) {
        if (!validAccNos.get().contains(accNo)) {
            throw new IllegalArgumentException("accNo is not valid");
        }
        int threadIndex = getThreadIndex();
        while (true) {
            PMap<Long, ReducibleAccount> oldMap = accountReferences.get(threadIndex);
            PMap<Long, ReducibleAccount> newMap = oldMap.plus(accNo, operation.apply(oldMap.get(accNo)));
            if (accountReferences.compareAndSet(threadIndex, oldMap, newMap)) {
                return;
            } else {
                threadIndex = changeThreadIndex();
            }
        }
    }

    @Override
    public void deposit(long accNo, int amount) {
        changeAccount(accNo, acc -> ReducibleAccount.deposit(acc, amount, "deposit: " + new Date().getTime()));
    }

    @Override
    public void withdraw(long accNo, int amount) {
        changeAccount(accNo, acc -> ReducibleAccount.withdraw(acc, amount, "withdraw: " + new Date().getTime()));
    }

    @Override
    public void transfer(long from, long to, int amount) {
        List<Long> accNumbers = validAccNos.get();
        if (!(accNumbers.contains(from) && accNumbers.contains(to))) {
            throw new IllegalArgumentException("from or to is not valid");
        }
        long time = new Date().getTime();
        int threadIndex = getThreadIndex();
        while (true) {
            PMap<Long, ReducibleAccount> oldMap = accountReferences.get(threadIndex);
            PMap<Long, ReducibleAccount> newMap = oldMap
                    .plus(from, ReducibleAccount.withdraw(oldMap.get(from), amount, "transfer form: " + time))
                    .plus(to,   ReducibleAccount.deposit (oldMap.get(to  ), amount, "transfer to: " + time));
            if (accountReferences.compareAndSet(threadIndex, oldMap, newMap)) {
                return;
            } else {
                threadIndex = changeThreadIndex();
            }
        }
    }

    /**
     * @param accNo
     * @return
     */
    @Override
    public List<Entry> getAccountEntries(long accNo) {
        if (!validAccNos.get().contains(accNo)) {
            throw new IllegalArgumentException("accNo is not valid");
        }
        PVector<Entry> step = TreePVector.empty();
        for (int i = 0; i < arraySize; i++) {
            step = ReducibleAccount.reduceEntries(accountReferences.get(i).get(accNo), step);
        }
        return step;
    }

    /**
     * TODO Maybe this should be changed to a map for easier use
     *
     * @param accNos
     * @return
     */
    @Override
    public List<Entry> getAccountEntries(List<Long> accNos) {
        List<Long> validAccountNumbersGotten = validAccNos.get();
        for (Long accNo : accNos) {
            if (!validAccountNumbersGotten.contains(accNo)) {
                throw new IllegalArgumentException("accNo " + accNo + " has no corresponding account");
            }
        }
//        Map<Long, PVector<Entry>> entryMap = new HashMap<>();
//        for (long accNo : accNos) {
//            entryMap.put(accNo, TreePVector.empty());
//        }
//        for (int i = 0; i < arraySize; i++) {
//            PMap<Long, ReducibleAccount> accountMap = accountReferences.get(i);
//            for (long accNo : accNos) {
//                entryMap.compute(accNo, (aLong, entries) -> accountMap.get(accNo).reduceEntries(entries));
//            }
//        }
        List<Entry> entries = new MultipleBackedList<>();
        for (int i = 0; i < arraySize; i++) {
            PMap<Long, ReducibleAccount> accountMap = accountReferences.get(i);
            for (long accNo : accNos) {
                ReducibleAccount acc = accountMap.get(accNo);
                if (acc != null){
                    entries.addAll(acc.getEntries());
                }
            }
        }
        return entries;
    }

//    private ImmutableAccount getAcc(long accNo) {
//        return accounts.get().get(accNo);
//    }

    @Override
    public long createAccount() {
        long accNo = accNoGenerator.getNext();
        validAccNos.getAndUpdate(accNumbers -> accNumbers.plus(accNo));
        return accNo;
    }

    @Override
    public int getBalance(long accNo) {
        int balance = 0;
        for (int i = 0; i < arraySize; i++) {
            balance = ReducibleAccount.reduceBalance(accountReferences.get(i).get(accNo), balance);
        }
        return balance;
    }


    @Override
    public String toString() {
        return "Bank: Accumulation";
    }
}
