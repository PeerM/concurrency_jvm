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
