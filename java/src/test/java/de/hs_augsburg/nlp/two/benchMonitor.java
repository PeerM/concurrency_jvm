package de.hs_augsburg.nlp.two;

import de.hs_augsburg.meixner.account.Account;
import de.hs_augsburg.meixner.account.MonitorAccount;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class benchMonitor {

    @Benchmark
    @CommonBenchmark
    public void deposit(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
    }

    @Benchmark
    @CommonBenchmark
    @OperationsPerInvocation(2)
    public void depositAndCheck(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
        acc.getBalance();
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile Account account = new MonitorAccount();
    }
}
