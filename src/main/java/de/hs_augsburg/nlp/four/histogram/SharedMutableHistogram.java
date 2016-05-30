package de.hs_augsburg.nlp.four.histogram;


import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import de.hs_augsburg.nlp.three.histogram.ClojureHelpers;
import de.hs_augsburg.nlp.three.histogram.ColorMask;
import de.hs_augsburg.nlp.three.histogram.IHistogram;
import org.math.plot.Plot2DPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SharedMutableHistogram implements IHistogram {

    public static void main(String[] args) {

        SharedMutableHistogram histogram = new SharedMutableHistogram();
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
        int[] hist = new int[256];
        Arrays.stream(data)
                .parallel()
                .map(mask::apply)
                .forEach(value -> {
                    synchronized (hist) {
                        hist[value]++;
                    }
                });
        return hist;
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
        Map<ColorMask, int[]> hist = new HashMap<>(3);
        for (ColorMask mask : ColorMask.values()) {
            hist.put(mask, makeHistogram(pixels, mask));
        }
        return hist;
    }
}
