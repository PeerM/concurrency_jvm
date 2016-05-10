package de.hs_augsburg.nlp.three;

import de.hs_augsburg.nlp.three.histogram.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class BenchHistogramm {


    // to play with JMH without plugins or custom runners
    public static void main(String[] args) throws RunnerException {
        // this is the config, you can play around with this
        Options opt = new OptionsBuilder()
                .include(BenchHistogramm.class.getSimpleName() + "")
//                .param("implName", "CljPerColor", "Reducing")
//                .param("persistentThreads", "false")
                .forks(1)
                .warmupIterations(4)
                .measurementIterations(3)
                .mode(Mode.Throughput)
                .threads(1)
//                .jvmArgsAppend("-Xms3g")
//                .output("jmh_out.txt")
//                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();

    }

    public static int[] loadImage(String path) {
        BufferedImage image = null;
        try {
            URL resource = BenchHistogramm.class.getClassLoader().getResource(path);
            if (resource == null) {
                throw new FileNotFoundException(path);
            }
            image = ImageIO.read(resource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image", e);
        }
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    private int[] randomImage(BenchmarkState state) {
        int index = (int) (System.nanoTime() % state.images.size());
        return state.images.get(index);
    }


    @Benchmark
    public void main(BenchmarkState state) {
        state.hole.consume(state.impl.histogram(randomImage(state)));
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"Sequential", "Threaded", "Decomposition", "AtomicDecomposition", "CljPerColor", "Reducing"})
        volatile String implName;
        //        @Param({"false", "true"})
//        volatile boolean persistentThreads;
        volatile IHistogram impl;
        volatile Blackhole hole = new Blackhole();
        volatile List<int[]> images = new ArrayList<>();

        @Setup
        public void setup() {
//            URLClassLoader classLoader = (URLClassLoader) BenchHistogramm.class.getClassLoader();
//            for(URL url : classLoader.getURLs())
//                System.out.println(url.getPath());
            for (int i = 1; i < 5; i++) {
                images.add(loadImage("benchdata/4160x2340/" + i + ".jpg"));
            }
            switch (implName) {
                case "Sequential":
                    impl = new SequentialHistogram();
                    break;
                case "Threaded":
                    impl = new ThreadedHistogram();
                    break;
                case "Decomposition":
                    impl = new DecompositionHistogram(Runtime.getRuntime().availableProcessors() + 1);
                    break;
                case "AtomicDecomposition":
                    impl = new AtomicDecompositionHistogram(Runtime.getRuntime().availableProcessors() + 1);
                    break;
                case "CljPerColor":
                    impl = new CljPerColorHistogram();
                    break;
                case "Reducing":
                    impl = new ReducingHistogram();
                    break;
                default:
                    throw new IllegalArgumentException("impl '" + implName + "' not supported");
            }
            // volatile write just to be sure
            images = images;
        }
    }
}
