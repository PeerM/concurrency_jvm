package de.hs_augsburg.nlp.three.sort;

import de.hs_augsburg.nlp.three.radix.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class BenchSorting {
    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(BenchSorting.class.getSimpleName() + "")
                .param("implName", "Sequential", "PHist", "FutureHist")
                .param("arraySize", "2000")
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(6)
                .mode(Mode.Throughput)
                .threads(1)
//                .addProfiler("stack")
//                .jvmArgsAppend("-Xms3g")
//                .output("jmh_out.txt")
//                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();

    }

    @Benchmark
    public void main(BenchmarkState state) {
        ArrayUtils.shuffleArray(state.data);
        state.hole.consume(state.impl.sort(state.data));
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Sequential", "JDK", "PHist", "FutureHist"})
        volatile String implName;
        @Param({"500", "5000"})
        volatile int arraySize;
        volatile ISort impl;
        volatile Blackhole hole = new Blackhole();
        volatile int[] data;

        @Setup
        public void setup() {
            switch (implName) {
                case "Sequential":
                    impl = new SequentialRadixSort();
                    break;
                case "JDK":
                    impl = new JdkSort();
                    break;
                case "PHist":
                    impl = new PHistRadixSort();
                    break;
                case "FutureHist":
                    impl = new FutureHistRadixSort();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
            Random random = new Random();
            data = random.ints(arraySize, 0, Integer.MAX_VALUE).toArray();
        }

        @TearDown
        public void teardown() {
            impl.close();
        }
    }
}
