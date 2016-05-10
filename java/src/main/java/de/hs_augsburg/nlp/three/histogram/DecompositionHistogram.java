package de.hs_augsburg.nlp.three.histogram;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import org.math.plot.Plot2DPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@SuppressWarnings("Duplicates")
public class DecompositionHistogram implements IHistogram {

    private int concurrency;

    public DecompositionHistogram(int concurrency) {
        this.concurrency = concurrency;
    }

    public static void main(String[] args) {
        DecompositionHistogram histogram = new DecompositionHistogram(Runtime.getRuntime().availableProcessors());
        histogram.visualize(histogram.analyseImage("flickr1.jpg"));
    }

    private void visualize(Map<ColorMask, int[]> histogram) {

        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();

        // add a line plot to the PlotPanel
        for (Map.Entry<ColorMask, int[]> entry : histogram.entrySet()) {
            plot.addStaircasePlot(entry.getKey().name(), entry.getKey().color, Doubles.toArray(Ints.asList(entry.getValue())));
        }

        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("a plot panel");
        frame.setContentPane(plot);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void makeHistogram(int[] data, ColorMask mask, int[] result, int start, int end, CountDownLatch barrier) {
        for (int i = start; i < end; i++) {
            int value = mask.apply(data[i]);
            synchronized (result) {
                result[value]++;
            }
        }
        barrier.countDown();
    }

    Map<ColorMask, int[]> analyseImage(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
//        int[] pixels = ((DataBuffer)image.getRaster().getDataBuffer());
//        final byte[] pixels = (image.getRaster().getDataBuffer().
//                .getDataBuffer()).getData();
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        return histogram(pixels);
//        return makeHistogram(pixels, 0xFF);
//        return null;
    }

    @Override
    public Map<ColorMask, int[]> histogram(int[] pixels) {
        Map<ColorMask, int[]> hist = new ConcurrentHashMap<>(3);
        // TODO don't create a new pool each time
        ExecutorService executor = Executors.newFixedThreadPool(this.concurrency);
        CountDownLatch latch = new CountDownLatch((this.concurrency + 1) * 3);
        for (ColorMask mask : ColorMask.values()) {
            int[] partial = new int[256];
            int start = 0;
            int end = pixels.length / concurrency;
            for (int i = 0; i < concurrency; i++) {
                final int startLocal = start;
                final int endLocal = end;
                executor.execute(() -> makeHistogram(pixels, mask, partial, startLocal, endLocal, latch));
                start += pixels.length / concurrency;
                end += pixels.length / concurrency;
            }
            int remainder = pixels.length % concurrency;
            executor.execute(()->makeHistogram(pixels, mask, partial, pixels.length - remainder, pixels.length, latch));
            hist.put(mask, partial);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("problem with latch await", e);
        }
        executor.shutdown();
        return hist;
    }
}
