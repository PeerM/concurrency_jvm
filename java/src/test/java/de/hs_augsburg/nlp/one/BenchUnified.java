package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.account.Account;
import de.hs_augsburg.meixner.account.MonitorAccount;
import de.hs_augsburg.nlp.one.account.AtomicAdderAccount;
import org.openjdk.jmh.annotations.*;

public class BenchUnified {


    @Benchmark
    @Fork(1)
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 4)
    @Measurement(iterations = 8)
    @Threads(4)
    public void deposit(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
    }

    @Benchmark
    @Fork(1)
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 4)
    @Measurement(iterations = 8)
    @Threads(4)
    @OperationsPerInvocation(2)
    public void depositAndCheck(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
        acc.getBalance();
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Monitor", "AtomicAdder"})
        String impl;
        volatile Account account;

        @Setup
        public void setup() {
            switch (impl) {
                case "Monitor":
                    account = new MonitorAccount();
                    break;
                case "AtomicAdder":
                    account = new AtomicAdderAccount();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + impl + "' not supported");
            }
        }
    }
}
