package de.hs_augsburg.nlp.two.BasicMonitor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class BenchBank {


    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(BenchBank.class.getSimpleName() + "")
                .param("impl", "Monitor")
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(8)
                .mode(Mode.Throughput)
                .threads(4)
                .build();

        new Runner(opt).run();

    }

    public long randomAccNo(BenchmarkState state) {
        int index = (int) (System.nanoTime() % state.accNos.size());
//        state.logger.info(Integer.toString(index));
        return state.accNos.get(index);
    }

    @Benchmark
    @Group("one")
    public void deposit(BenchmarkState state) {
        Bank impl = state.bankImpl;
        impl.deposit(randomAccNo(state),5);
    }

    @Benchmark
    @Group("one")
    public void withdraw(BenchmarkState state) {
        Bank impl = state.bankImpl;
        impl.withdraw(randomAccNo(state),4);
    }

    @Benchmark
    @Group("one")
    public void getEntries(BenchmarkState state) {
        Bank impl = state.bankImpl;
        impl.getAccountEntries(randomAccNo(state));
    }

    @Benchmark
    @Group("one")
    public void transfer(BenchmarkState state) {
        Bank impl = state.bankImpl;
        impl.transfer(randomAccNo(state),randomAccNo(state),6);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Monitor"})
        String implName;
        volatile Bank bankImpl;
        volatile List<Long> accNos = new Vector<>();
//        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Setup
        public void setup() {
            switch (implName) {
                case "Monitor":
                    bankImpl = new Bank();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
            for (int i = 0; i < 10; i++) {
                accNos.add(bankImpl.createAccount());
            }
        }
    }
}
