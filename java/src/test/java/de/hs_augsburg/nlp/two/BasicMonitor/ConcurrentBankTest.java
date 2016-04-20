package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.one.prime.Task;
import de.hs_augsburg.nlp.two.IBank;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertNotNull;

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
        accNos = Arrays.asList(impl.createAccount(), impl.createAccount(), impl.createAccount());
        accNoQueue = new ConcurrentLinkedQueue<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createAccount() throws Exception {
        long accNo = impl.createAccount();
        List<Entry> entries = impl.getAccountEntries(accNo);
        assertNotNull(entries);
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

    class ActionTask extends Thread{
        List<Action> actions;
        public ActionTask( List<Action> actions) {
            this.actions = actions;
        }

        @Override
        public void run() {
            actions.forEach(Action::apply);
        }
    }

    @Test
    public void creationTest() throws Exception {
        List<Action> actions = Collections.nCopies(20,create());

    }
}