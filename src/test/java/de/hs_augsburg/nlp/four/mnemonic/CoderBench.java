package de.hs_augsburg.nlp.four.mnemonic;


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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CoderBench {

    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(".*" + CoderBench.class.getSimpleName() + ".*")
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

    public static List<String> loadText(String path) {
        List res;
        try {
            res = IOUtils.readLines(CoderBench.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return res;
    }


    @Benchmark
    public void java(CoderBench.BenchmarkState state) {
        String number = randomNumber();
        state.hole.consume(state.javaImpl.translate(number));
    }

    @Benchmark
    public void scala(CoderBench.BenchmarkState state) {
        String number = randomNumber();
        state.hole.consume(state.scalaImpl.translate(number));
    }

    private String randomNumber() {
        return String.valueOf(ThreadLocalRandom.current().ints(10, '0', '9'));
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile CoderAdapter scalaImpl;
        volatile JavaCoder javaImpl;
        volatile Blackhole hole = new Blackhole();
        volatile List<String> dictionary;

        @Setup
        public void setup() {
            dictionary = loadText("filtered-english.txt");
            scalaImpl = new CoderAdapter(dictionary);
            javaImpl = new JavaCoder(dictionary);
        }
    }
}
