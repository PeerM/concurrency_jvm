package de.hs_augsburg.nlp.one;

import de.hs_augsburg.meixner.account.Account;
import de.hs_augsburg.meixner.account.MonitorAccount;
import de.hs_augsburg.nlp.one.account.AtomicAdderAccount;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchUnified {


    @Benchmark
    @Threads(4)
    public void deposit(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
    }

    @Benchmark
    @OperationsPerInvocation(2)
    public void depositAndCheck(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
        acc.getBalance();
    }

    @Benchmark
    @OperationsPerInvocation(2)
    public void depositAndWithdraw(BenchmarkState state) {
        Account acc = state.account;
        acc.deposit(1);
        acc.withdraw(1);
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


    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(BenchUnified.class.getSimpleName()+".depositAndWithdraw")
                .param("impl","Monitor","AtomicAdder")
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(8)
                .mode(Mode.Throughput)
                .threads(4)
                .build();

        new Runner(opt).run();

    }
}
