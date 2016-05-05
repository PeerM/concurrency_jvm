package de.hs_augsburg.nlp.three;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public class ClojureHelpers {
    public static int[] pathToPixels(String path) {
        try {
            BufferedImage image = ImageIO.read(ClojureHelpers.class.getClassLoader().getResourceAsStream(path));
            return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
