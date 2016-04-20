package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.two.IBank;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

abstract class Action {
    public final IBank impl;

    Action(IBank impl) {
        this.impl = impl;
    }

    abstract public void apply();
}

abstract class SingleAccAction extends Action {
    protected final long accNo;

    protected SingleAccAction(IBank impl, long accNo) {
        super(impl);
        this.accNo = accNo;
    }
}

class WithdrawAction extends SingleAccAction {

    protected WithdrawAction(IBank impl, long accNo) {
        super(impl, accNo);
    }

    @Override
    public void apply() {
        impl.withdraw(this.accNo, 3);
    }
}

class DepositAction extends SingleAccAction {

    protected DepositAction(IBank impl, long accNo) {
        super(impl, accNo);
    }

    @Override
    public void apply() {
        impl.deposit(this.accNo, 4);
    }
}

class CreateAction extends Action {

    private ConcurrentLinkedQueue<Long> accNos;

    CreateAction(IBank impl, ConcurrentLinkedQueue<Long> accNos) {
        super(impl);
        this.accNos = accNos;
    }

    @Override
    public void apply() {
        long accNo = impl.createAccount();
        accNos.add(accNo);
    }
}

class TransferAction extends Action {

    final long from;
    final long to;

    TransferAction(IBank impl, long from, long to) {
        super(impl);
        this.from = from;
        this.to = to;
    }

    @Override
    public void apply() {
        impl.transfer(from, to, 6);
    }
}

public class ConcurrentBankTest {
    public IBank impl;
    private List<Long> accNos;
    private ConcurrentLinkedQueue<Long> accNoQueue;

    @Before
    public void setUp() throws Exception {
        impl = new Bank();
        accNos = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            accNos.add(impl.createAccount());
        }
        accNos = Arrays.asList(impl.createAccount(), impl.createAccount());
        accNoQueue = new ConcurrentLinkedQueue<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    public long randomAccNo() {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(accNos.size());
        return accNos.get(index);
    }

    public Action create() {
        return new CreateAction(this.impl, accNoQueue);
    }

    public Action withdraw() {
        return new WithdrawAction(this.impl, randomAccNo());
    }

    public Action deposit() {
        return new DepositAction(this.impl, randomAccNo());
    }

    public Action transfer() {
        return new TransferAction(this.impl, randomAccNo(), randomAccNo());
    }

    public List<Entry> getEntries() {
        // We need to get all of them atomically
        return impl.getAccountEntries(accNos);
    }

    @Test
    public void creationTest() throws Exception {
        List<Action> actions = Collections.nCopies(100000, create());
        actions.stream().parallel().peek(Action::apply).count();
        assertEquals(100000, accNoQueue.size());
    }

    @Test
    public void depositTest() throws Exception {
        List<Action> actions = Collections.nCopies(100000, deposit());
        actions.stream().parallel().peek(Action::apply).count();
        assertEquals(100000, getEntries().size());
    }

    @Test
    public void transferTest() throws Exception {
        int nrActions = 100000;
        InvariantChecker checker = new InvariantChecker();
        checker.start();
        List<Action> actions = Collections.nCopies(nrActions, transfer());
        actions.stream().parallel().peek(Action::apply).count();
        assertEquals(nrActions * 2, getEntries().size());
        checker.interrupt();
        checker.join();
        assertFalse(checker.message, checker.isSomethingWrong);
    }

    @Test
    public void depositAndWithdrawTest() throws Exception {
        InvariantChecker checker = new InvariantChecker();
        checker.start();
        int nrActions = 100000;
        List<Action> deposits = Collections.nCopies(nrActions, deposit());
        List<Action> withdrawals = Collections.nCopies(nrActions, withdraw());
        List<Action> actions = new ArrayList<>(nrActions * 2);
        actions.addAll(deposits);
        actions.addAll(withdrawals);
        Collections.shuffle(actions);
        actions.stream().parallel().peek(Action::apply).count();
        List<Entry> entries = getEntries();
        assertEquals(nrActions, entries.stream().filter(entry -> entry.type == EntryType.DEPOSIT).count());
        assertEquals(nrActions, entries.stream().filter(entry -> entry.type == EntryType.WITHDRAW).count());
        assertEquals(nrActions * 2, entries.size());
        checker.interrupt();
        checker.join();
        assertFalse(checker.message, checker.isSomethingWrong);
    }

    @Test
    public void mixedTest() throws Exception {
        InvariantChecker checker = new InvariantChecker();
        checker.start();
        int nrActions = 100000;
        List<Action> deposits = Collections.nCopies(nrActions, deposit());
        List<Action> withdrawals = Collections.nCopies(nrActions, withdraw());
        List<Action> transfers = Collections.nCopies(nrActions, transfer());
        List<Action> creations = Collections.nCopies(nrActions, create());
        List<Action> actions = new ArrayList<>(nrActions * 4);
        actions.addAll(deposits);
        actions.addAll(withdrawals);
        actions.addAll(transfers);
        actions.addAll(creations);
        Collections.shuffle(actions);
        actions.stream().parallel().peek(Action::apply).count();
        List<Entry> entries = getEntries();
        assertEquals(nrActions * 2, entries.stream().filter(entry -> entry.type == EntryType.DEPOSIT).count());
        assertEquals(nrActions * 2, entries.stream().filter(entry -> entry.type == EntryType.WITHDRAW).count());
        assertEquals(nrActions * 4, entries.size());
        assertEquals(nrActions, accNoQueue.size());
        checker.interrupt();
        checker.join();
        assertFalse(checker.message, checker.isSomethingWrong);
    }

    class ActionTask extends Thread {
        List<Action> actions;

        public ActionTask(List<Action> actions) {
            this.actions = actions;
        }

        @Override
        public void run() {
            actions.forEach(Action::apply);
        }
    }

    class InvariantChecker extends Thread {
        private boolean isSomethingWrong = false;
        private String message;

        public InvariantChecker() {

        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                List<Entry> entries = getEntries();
                long fromCount = entries.stream().filter(entry -> entry.text.startsWith("transfer form:")).count();
                long toCount = entries.stream().filter(entry -> entry.text.startsWith("transfer to:")).count();
                if (fromCount != toCount) {
                    isSomethingWrong = true;
                    message = "from Count was " + fromCount + ", but to Count was " + toCount;
                    return;
                }
            }
        }
    }
}