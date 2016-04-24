package de.hs_augsburg.nlp.two;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("WeakerAccess")
public class BenchList {


    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(BenchList.class.getSimpleName() + "")
//                .param("implName", "ConcurrentLinkedQueue", "VectorQueue")
                .forks(4)
                .warmupIterations(10)
                .measurementIterations(6)
                .mode(Mode.Throughput)
                .threads(5)
//                .jvmArgsAppend("-Xms3g")
//                .output("jmh_out.txt")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();

    }

    @Benchmark
    @Group("main")
    public void add(BenchmarkState state) {
        state.impl.offer(5);
    }

    @Benchmark
    @Group("main")
    public void poll(BenchmarkState state) {
        state.hole.consume(state.impl.poll());
    }


    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"ConcurrentLinkedQueue", "VectorQueue"})
        volatile String implName;
        volatile Queue<Integer> impl;
        volatile Blackhole hole = new Blackhole();

        @Setup
        public void setup() {
            switch (implName) {
                case "ConcurrentLinkedQueue":
                    impl = new ConcurrentLinkedQueue<>();
                    break;
                case "VectorQueue":
                    impl = new VectorQueue<>();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
        }
    }
}
