package de.hs_augsburg.nlp.four;


import de.hs_augsburg.nlp.three.BenchHistogramm;
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
                .include(BenchHistogramm.class.getSimpleName() + "")
//                .param("implName", "CljPerColor", "Reducing")
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

    public Stream<String> loadText(String path) {
        Stream<String> res;
        try {
            res = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8).stream();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return res;
    }

    private int[] randomImage(BenchHistogramm.BenchmarkState state) {
        int index = (int) (System.nanoTime() % state.images.size());
        return state.images.get(index);
    }


    @Benchmark
    public void main(BenchHistogramm.BenchmarkState state) {
        state.hole.consume(state.impl.histogram(randomImage(state)));
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Sequential"})
        volatile String implName;
        volatile Blackhole hole = new Blackhole();

        @Setup
        public void setup() {
            for (int i = 1; i < 5; i++) {
                images.add(loadText("benchdata/4160x2340/" + i + ".jpg"));
            }
            switch (implName) {
                case "Sequential":
                    impl = new SequentialHistogram();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
        }
    }
}
