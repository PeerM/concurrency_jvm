package de.hs_augsburg.nlp.three.histogram;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ClojureHelpers {
    public static int[] pathToPixels(String path) {
        try {
            BufferedImage image = ImageIO.read(ClojureHelpers.class.getClassLoader().getResourceAsStream(path));
            return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] partialHistogram(int[] pixels, ColorMask mask, int start, int end) {
        int[] result = new int[256];
        for (int i = start; i < end; i++) {
            int value = mask.apply(pixels[i]);
            result[value]++;
        }
        return result;
    }

    public static int[] arrayElementBasedAdd(int[] a, int[] b) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }
    public static void arrayElementBasedAddImperativ(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }


}
