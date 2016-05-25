package de.hs_augsburg.nlp.four;


import de.hs_augsburg.nlp.three.histogram.*;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class StringStreamBenchmark {

    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(StringStreamBenchmark.class.getSimpleName() + "")
//                .param("implName", "Sequential", "Parallel")
//                .param("persistentThreads", "false")
                .forks(1)
                .warmupIterations(6)
                .measurementIterations(7)
                .mode(Mode.Throughput)
                .threads(1)
//                .jvmArgsAppend("-Xms3g")
//                .output("jmh_out.txt")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();

    }

    public static Stream<String> loadText(String path) {
        Stream<String> res;
        try {
            res = IOUtils.readLines(StringStreamBenchmark.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8).stream();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return res;
    }




    @Benchmark
    public void main(StringStreamBenchmark.BenchmarkState state) {
        state.hole.consume(state.impl.makeHistogram(state.words));
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Sequential", "Parallel"})
        volatile String implName;
        volatile IStringStream impl;
        volatile Blackhole hole = new Blackhole();
        volatile Stream<String> words;

        @Setup
        public void setup() {
            words = loadText("pride.txt");
            switch (implName) {
                case "Sequential":
                    impl = new StringStream();
                    break;
                case "Parallel":
                    impl = new StringStreamParallel();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
        }
    }
}
