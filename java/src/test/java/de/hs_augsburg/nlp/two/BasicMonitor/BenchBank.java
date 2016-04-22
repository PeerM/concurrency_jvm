package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.two.Functional.CasBank;
import de.hs_augsburg.nlp.two.IBank;
import de.hs_augsburg.nlp.two.SmallLock.SmallLockBank;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class BenchBank {


    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
//                .include(BenchBank.class.getSimpleName() + ".mixed")
                .param("implName", "Monitor", "Cas", "SmallLock")
//                .param("numberOfAccounts", "10")
                .include(BenchBank.class.getSimpleName() + ".readOnly")
//                .param("implName", "Unsafe", "Monitor", "Cas", "SmallLock")
                .param("numberOfAccounts", "10", "32", "200", "1000")
                .forks(1)
                .warmupIterations(10)
                .measurementIterations(6)
                .mode(Mode.Throughput)
                .threads(5)
                .jvmArgsAppend("-Xms3g")
//                .output("jmh_out.txt")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();

    }

    private long randomAccNo(BenchmarkState state) {
        int index = (int) (System.nanoTime() % state.accNos.size());
//        state.logger.info(Integer.toString(index));
        return state.accNos.get(index);
    }

    @Benchmark
    @Group("depositOnly")
    public void depositOnly(BenchmarkState state) {
        deposit(state);
    }

    @Benchmark
    @Group("readOnly")
    public void balanceOnly(BenchmarkState state) {
        balance(state);
    }

    @Benchmark
    @Group("mixed")
    public void deposit(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.deposit(randomAccNo(state), 5);
    }

    @Benchmark
    @Group("mixed")
    public void withdraw(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.withdraw(randomAccNo(state), 4);
    }

    @Benchmark
    @Group("mixed")
    public void getEntries(BenchmarkState state) {
        IBank impl = state.bankImpl;
        state.hole.consume(impl.getAccountEntries(randomAccNo(state)));
    }

    @Benchmark
    @Group("mixed")
    public void transfer(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.transfer(randomAccNo(state), randomAccNo(state), 6);
    }

    @Benchmark
    @Group("mixed")
    public void balance(BenchmarkState state) {
        IBank impl = state.bankImpl;
        state.hole.consume(impl.getBalance(randomAccNo(state)));
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Monitor", "SmallLock", "Unsafe", "Cas"})
        volatile String implName;
        @Param({"2", "10", "30", "100"})
        volatile int numberOfAccounts;
        volatile IBank bankImpl;
        volatile List<Long> accNos = new ArrayList<>(numberOfAccounts);
        volatile Blackhole hole = new Blackhole();
//        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Setup
        public void setup() {
            switch (implName) {
                case "Monitor":
                    bankImpl = new CentralMoniBank();
                    break;
                case "SmallLock":
                    bankImpl = new SmallLockBank();
                    break;
                case "Unsafe":
                    bankImpl = new UnsafeBank();
                    break;
                case "Cas":
                    bankImpl = new CasBank();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
            for (int i = 0; i < numberOfAccounts; i++) {
                accNos.add(bankImpl.createAccount());
            }
            numberOfAccounts = numberOfAccounts;
        }
    }
}
