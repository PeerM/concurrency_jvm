package de.hs_augsburg.nlp.three;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SequentialHistogram {
    public static void main(String[] args) {
        new SequentialHistogram().analyseImage("SingingRingingTree.jpg", ColorMask.BLUE);
    }

    private int[] makeHistogram(int[] data, ColorMask mask) {
        int[] histogram = new int[256];
        for (int i = 0; i < data.length; i++) {
            int value = mask.apply(data[i]);
            histogram[value]++;
        }
        return histogram;
    }

    int[] analyseImage(String path, ColorMask mask) {
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
        return makeHistogram(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()), mask);
//        return makeHistogram(pixels, 0xFF);
//        return null;
    }
}
