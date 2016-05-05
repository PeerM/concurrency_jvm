package de.hs_augsburg.nlp.three;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import org.math.plot.Plot2DPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("Duplicates")
public class ThreadedHistogram implements IHistogram {
    private final boolean persistentThreads;

    public ThreadedHistogram(boolean persistentThreads) {
        this.persistentThreads = persistentThreads;
    }

    public static void main(String[] args) {
        ThreadedHistogram histogram = new ThreadedHistogram(false);
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

    private int[] makeHistogram(int[] data, ColorMask mask) {
        int[] histogram = new int[256];
        for (int i = 0; i < data.length; i++) {
            int value = mask.apply(data[i]);
            histogram[value]++;
        }
        return histogram;
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
        List<Thread> threads = new ArrayList<>(3);
        for (ColorMask mask : ColorMask.values()) {
            Thread thread = new Thread(() -> hist.put(mask, makeHistogram(pixels, mask)));
            thread.start();
            threads.add(thread);
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("joining thread was interrupted", e);
        }
        return hist;
    }
}
