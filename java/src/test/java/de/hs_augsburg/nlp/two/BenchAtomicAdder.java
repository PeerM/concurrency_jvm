package de.hs_augsburg.nlp.two;

import de.hs_augsburg.meixner.account.Account;
import de.hs_augsburg.nlp.one.account.AtomicAdderAccount;
import org.openjdk.jmh.annotations.*;

public class BenchAtomicAdder {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile AtomicAdderAccount account = new AtomicAdderAccount();
    }

    @Benchmark
    @Fork(2)
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 5)
    @Threads(4)
    public void deposit(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
    }

    @Benchmark
    @Fork(2)
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 5)
    @Threads(4)
    @OperationsPerInvocation(2)
    public void depositAndCheck(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
        acc.getBalance();
    }
}
