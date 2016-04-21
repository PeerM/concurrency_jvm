package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.two.IBank;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Vector;

@SuppressWarnings("WeakerAccess")
public class BenchBank {


    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(BenchBank.class.getSimpleName() + "")
//                .param("impl", "Monitor")
                .forks(2)
                .warmupIterations(10)
                .measurementIterations(8)
                .mode(Mode.Throughput)
                .threads(2)
                .build();

        new Runner(opt).run();

    }

    private long randomAccNo(BenchmarkState state) {
        int index = (int) (System.nanoTime() % state.accNos.size());
//        state.logger.info(Integer.toString(index));
        return state.accNos.get(index);
    }

    @Benchmark
    @Group("one")
    public void deposit(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.deposit(randomAccNo(state), 5);
    }

    @Benchmark
    @Group("one")
    public void withdraw(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.withdraw(randomAccNo(state), 4);
    }

    @Benchmark
    @Group("one")
    public void getEntries(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.getAccountEntries(randomAccNo(state));
    }

    @Benchmark
    @Group("one")
    public void transfer(BenchmarkState state) {
        IBank impl = state.bankImpl;
        impl.transfer(randomAccNo(state), randomAccNo(state), 6);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Monitor"})
        String implName;
        volatile IBank bankImpl;
        volatile List<Long> accNos = new Vector<>();
//        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Setup
        public void setup() {
            switch (implName) {
                case "Monitor":
                    bankImpl = new CentralMoniBank();
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
