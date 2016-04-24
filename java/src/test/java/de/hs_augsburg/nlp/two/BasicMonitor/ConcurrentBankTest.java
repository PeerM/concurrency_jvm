package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.two.IBank;
import de.hs_augsburg.nlp.two.reduced.AccumulatorBank;
import one.util.streamex.StreamEx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

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
        impl.withdraw(this.accNo, 1);
    }
}

class DepositAction extends SingleAccAction {

    protected DepositAction(IBank impl, long accNo) {
        super(impl, accNo);
    }

    @Override
    public void apply() {
        impl.deposit(this.accNo, 1);
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
        impl.transfer(from, to, 1);
    }
}

@RunWith(Parameterized.class)
public class ConcurrentBankTest {
    private static final int parallelism = 6;
    @Parameterized.Parameter
    public IBank impl;
    private List<Long> accNos;
    private ConcurrentLinkedQueue<Long> accNoQueue;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static List<IBank> data() {
        return Arrays.asList(
//                new UnsafeBank()
//                , new CentralMoniBank()
//                new SmallLockBank()
//                new CasBank()
                new AccumulatorBank(parallelism + (int) (parallelism * 0.5f))
//                new AccumulatorBank(parallelism )
        );
    }


    @Before
    public void setUp() throws Exception {
        accNos = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            accNos.add(impl.createAccount());
        }
        accNoQueue = new ConcurrentLinkedQueue<>();
    }

    @After
    public void tearDown() throws Exception {
        impl = null;
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
        return impl.getAccountEntries(new ArrayList<Long>(accNos));
    }

    @Test
    public void creationTest() throws Exception {
        List<Action> actions = Collections.nCopies(100000, create());
        runActions(actions);
        assertEquals(100000, accNoQueue.size());
    }

    @Test
    public void balanceTest() throws Exception {
        int factor = 100000;
        for (int i = 0; i < 5 - accNos.size(); i++) {
            accNos.add(impl.createAccount());
        }
        List<List<Action>> groups = Arrays.asList(
                Collections.nCopies(2 * factor, new DepositAction(impl, accNos.get(0))),
                Collections.nCopies(factor, new WithdrawAction(impl, accNos.get(0))),
                Collections.nCopies(factor, new DepositAction(impl, accNos.get(1))),
                Collections.nCopies(factor, new WithdrawAction(impl, accNos.get(2))),
                Collections.nCopies(factor, new DepositAction(impl, accNos.get(3))),
                Collections.nCopies(factor, new TransferAction(impl, accNos.get(3), accNos.get(4)))
        );
        List<Action> actions = new ArrayList<>(7 * factor);
        groups.forEach(actions::addAll);
        Collections.shuffle(actions);
        runActions(actions);
        assertEquals(factor, impl.getBalance(accNos.get(0)));
        assertEquals(factor, impl.getBalance(accNos.get(1)));
        assertEquals(-1 * factor, impl.getBalance(accNos.get(2)));
        assertEquals(0, impl.getBalance(accNos.get(3)));
        assertEquals(factor, impl.getBalance(accNos.get(4)));
    }

    @Test
    public void transferTest() throws Exception {
        int nrActions = 400000;
        InvariantChecker checker = new InvariantChecker();
        checker.start();
        List<Action> actions = Collections.nCopies(nrActions, transfer());
        runActions(actions);
        assertEquals(nrActions * 2, getEntries().size());
        checker.interrupt();
        checker.join();
        assertFalse(checker.message, checker.isSomethingWrong);
    }

    @Test
    public void depositAndWithdrawTest() throws Exception {
        InvariantChecker checker = new InvariantChecker();
        checker.start();
        int nrActions = 200000;
        List<Action> deposits = Collections.nCopies(nrActions, deposit());
        List<Action> withdrawals = Collections.nCopies(nrActions, withdraw());
        List<Action> actions = new ArrayList<>(nrActions * 2);
        actions.addAll(deposits);
        actions.addAll(withdrawals);
        Collections.shuffle(actions);
        runActions(actions);
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
        int nrActions = 200000;
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
        runActions(actions);
        List<Entry> entries = getEntries();
        assertEquals(nrActions * 2, entries.stream().filter(entry -> entry.type == EntryType.DEPOSIT).count());
        assertEquals(nrActions * 2, entries.stream().filter(entry -> entry.type == EntryType.WITHDRAW).count());
        assertEquals(nrActions * 4, entries.size());
        assertEquals(nrActions, accNoQueue.size());
        checker.interrupt();
        checker.join();
        assertFalse(checker.message, checker.isSomethingWrong);
    }

    private void runActions(List<Action> actions) {
//        runWithStream(actions, parallelism);
        runWithThread(actions, parallelism);
    }

    private void runWithThread(List<Action> actions, int parallelism) {
        CyclicBarrier barrier = new CyclicBarrier(parallelism + 1);
        List<Thread> threads = StreamEx.ofSubLists(actions, (actions.size() / parallelism) + 1)
                .map(actionList -> new ActionThread(actionList, barrier))
                .collect(Collectors.toList());
        threads.forEach(Thread::start);
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            fail(e.toString());
        }
        // at this point the threads are running
        try {
            barrier.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            fail(e.toString());
        }

//        for (Thread actionThread : threads) {
//            try {
//                actionThread.join();
//            } catch (InterruptedException e) {
//                fail(e.toString());
//            }
//        }
    }

    private void runWithStream(List<Action> actions, int parallelism) {
        ForkJoinPool fjp = new ForkJoinPool(parallelism);
        StreamEx.of(actions.stream()).parallel(fjp).peek(Action::apply).count();
        fjp.awaitQuiescence(5, TimeUnit.SECONDS);
        fjp.shutdown();
    }

    class ActionThread extends Thread {
        List<Action> actions;
        CyclicBarrier barrier;

        public ActionThread(List<Action> actions, CyclicBarrier barrier) {
//            super("ActionThread");
            this.setName(getName() + " Action");
            this.actions = actions;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            actions.forEach(Action::apply);
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    class InvariantChecker extends Thread {
        private boolean isSomethingWrong = true;
        private String message = "Invariant Checker did not complete";

        public InvariantChecker() {
            super("Invariant Checker");
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
            isSomethingWrong = false;
            return;
        }
    }
}