package de.hs_augsburg.nlp.three.hist;


import de.hs_augsburg.nlp.four.histogram.StreamedHistogram;
import de.hs_augsburg.nlp.three.CljPerColorHistogram;
import de.hs_augsburg.nlp.three.ReducingHistogram;
import de.hs_augsburg.nlp.three.histogram.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Ignore
public class HistogramTest {

    private SequentialHistogram sequentialHistogram;
    private ThreadedHistogram threadedHistogram;
    private DecompositionHistogram decompositionHistogram;
    private AtomicDecompositionHistogram atomicDecompositionHistogram;
    private CljPerColorHistogram cljPerColorHistogram;
    private ReducingHistogram reducingHistogram;
    private StreamedHistogram streamedHistogram;

    private int[] imagePixels;


    @Before
    public void setUp() {
        this.sequentialHistogram = new SequentialHistogram();
        this.threadedHistogram = new ThreadedHistogram();
        this.decompositionHistogram = new DecompositionHistogram(Runtime.getRuntime().availableProcessors());
        this.atomicDecompositionHistogram = new AtomicDecompositionHistogram(Runtime.getRuntime().availableProcessors());
        this.cljPerColorHistogram = new CljPerColorHistogram();
        this.reducingHistogram = new ReducingHistogram();
        this.streamedHistogram = new StreamedHistogram();

        this.imagePixels = getPixels("benchdata/4160x2340/1.jpg");
    }


    @Test
    public void compareAnalyzedMaps() {
        Map<ColorMask, int[]> correctAnalyzedPixels = sequentialHistogram.histogram(imagePixels);
        Map<ColorMask, int[]> threadedAnalyzedPixels = threadedHistogram.histogram(imagePixels);
        Map<ColorMask, int[]> decomAnalyzedPixels = decompositionHistogram.histogram(imagePixels);
        Map<ColorMask, int[]> atomicDecomAnalyzedPixels = atomicDecompositionHistogram.histogram(imagePixels);
        Map<ColorMask, int[]> cljPerColorAnalyzedPixels = ((IHistogram) cljPerColorHistogram).histogram(imagePixels);
        Map<ColorMask, int[]> reducingAnalyzedPixels = ((IHistogram) reducingHistogram).histogram(imagePixels);
        Map<ColorMask, int[]> streamedAnalyzedPixels = streamedHistogram.histogram(imagePixels);

        for (ColorMask cm : ColorMask.values()) {
            Assert.assertArrayEquals("threaded", correctAnalyzedPixels.get(cm), threadedAnalyzedPixels.get(cm));
            Assert.assertArrayEquals("decom", correctAnalyzedPixels.get(cm), decomAnalyzedPixels.get(cm));
            Assert.assertArrayEquals("atomicDecom", correctAnalyzedPixels.get(cm), atomicDecomAnalyzedPixels.get(cm));
            Assert.assertArrayEquals("cljPerColor", correctAnalyzedPixels.get(cm), cljPerColorAnalyzedPixels.get(cm));
            Assert.assertArrayEquals("reduced", correctAnalyzedPixels.get(cm), reducingAnalyzedPixels.get(cm));
            Assert.assertArrayEquals("reduced", correctAnalyzedPixels.get(cm), streamedAnalyzedPixels.get(cm));
        }
    }

    int[] getPixels(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        return pixels;
    }
}
