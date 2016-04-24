package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.two.Functional.CasBank;
import de.hs_augsburg.nlp.two.IBank;
import de.hs_augsburg.nlp.two.SmallLock.SmallLockBank;
import de.hs_augsburg.nlp.two.reduced.AccumulatorBank;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(Parameterized.class)
public class BankTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Parameterized.Parameter
    public IBank impl;
    private List<Long> accNos;
    private long accNo;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<IBank> data() {
        return Arrays.asList(
//                new UnsafeBank()
//                ,new CentralMoniBank()
//                ,new SmallLockBank()
//                ,new CasBank()
//                new AccumulatorBank(2),
                de.hs_augsburg.nlp.two.cl.RefBankFactory.makeRefBank()
        );
    }

    @Before
    public void setUp() throws Exception {
        this.accNo = impl.createAccount();
    }

    @After
    public void tearDown() throws Exception {
        this.accNo = -1;
        this.impl = null;

    }

    @Test
    public void createAccount() throws Exception {
        long accNo = impl.createAccount();
        List<Entry> entries = impl.getAccountEntries(accNo);
        assertNotNull(entries);
    }

    @Test
    public void depositIntoNonExistentAccount() throws Exception {
        boolean threw = false;
        try {
            impl.deposit(333039,5);
        } catch (Exception e) {
            threw = true;
        }
        assertTrue("deposit into non existent Account should throw an exception",threw);
    }

    @Test
    public void deposit() throws Exception {
        impl.deposit(accNo, 2);
        List<Entry> entries = impl.getAccountEntries(accNo);
        assertEquals(1, entries.size());
        assertEquals(2, impl.getBalance(accNo));

        impl.deposit(accNo, 3);
        entries = impl.getAccountEntries(accNo);
        assertEquals(2, entries.size());
        assertEquals(5, impl.getBalance(accNo));

        impl.deposit(accNo, 1);
        entries = impl.getAccountEntries(accNo);
        assertEquals(3, entries.size());
        assertEquals(6, impl.getBalance(accNo));

        Entry entry = entries.get(0);
        assertEquals(2, entry.amount);
        assertEquals(EntryType.DEPOSIT, entry.type);
        entry = entries.get(1);
        assertEquals(3, entry.amount);
        assertEquals(EntryType.DEPOSIT, entry.type);
        entry = entries.get(2);
        assertEquals(1, entry.amount);
        assertEquals(EntryType.DEPOSIT, entry.type);
    }

    @Test
    public void withdraw() throws Exception {
        impl.withdraw(accNo, 2);
        List<Entry> entries = impl.getAccountEntries(accNo);
        assertEquals(1, entries.size());
        assertEquals(-2, impl.getBalance(accNo));

        impl.withdraw(accNo, 3);
        entries = impl.getAccountEntries(accNo);
        assertEquals(2, entries.size());
        assertEquals(-5, impl.getBalance(accNo));

        impl.withdraw(accNo, 1);
        entries = impl.getAccountEntries(accNo);
        assertEquals(3, entries.size());
        assertEquals(-6, impl.getBalance(accNo));

        Entry entry = entries.get(0);
        assertEquals(2, entry.amount);
        assertEquals(EntryType.WITHDRAW, entry.type);
        entry = entries.get(1);
        assertEquals(3, entry.amount);
        assertEquals(EntryType.WITHDRAW, entry.type);
        entry = entries.get(2);
        assertEquals(1, entry.amount);
        assertEquals(EntryType.WITHDRAW, entry.type);
    }

    @Test
    public void transfer() throws Exception {
        long from = this.accNo;
        long to = impl.createAccount();
        // transfer once
        impl.transfer(from, to, 5);
        // make sure everything is as expected
        List<Entry> fromEntries = impl.getAccountEntries(from);
        List<Entry> toEntries = impl.getAccountEntries(to);
        Entry fromEntry = fromEntries.get(0);
        Entry toEntry = toEntries.get(0);
        assertEquals(5, fromEntry.amount);
        assertEquals(EntryType.WITHDRAW, fromEntry.type);
        assertEquals(5, toEntry.amount);
        assertEquals(EntryType.DEPOSIT, toEntry.type);
        assertEquals(-5, impl.getBalance(from));
        assertEquals(5, impl.getBalance(to));
        // transfer some back
        impl.transfer(to, from, 4);
        // make sure past is still the same
        fromEntries = impl.getAccountEntries(from);
        toEntries = impl.getAccountEntries(to);
        fromEntry = fromEntries.get(0);
        toEntry = toEntries.get(0);
        assertEquals(5, fromEntry.amount);
        assertEquals(EntryType.WITHDRAW, fromEntry.type);
        assertEquals(5, toEntry.amount);
        assertEquals(EntryType.DEPOSIT, toEntry.type);
        // check added things
        fromEntry = fromEntries.get(1);
        toEntry = toEntries.get(1);
        assertEquals(4, fromEntry.amount);
        assertEquals(EntryType.DEPOSIT, fromEntry.type);
        assertEquals(4, toEntry.amount);
        assertEquals(EntryType.WITHDRAW, toEntry.type);
        assertEquals(-1, impl.getBalance(from));
        assertEquals(1, impl.getBalance(to));
    }
}