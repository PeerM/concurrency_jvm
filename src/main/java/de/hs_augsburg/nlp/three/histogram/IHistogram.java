package de.hs_augsburg.nlp.three.histogram;

import java.util.Map;

public interface IHistogram {
    Map<ColorMask, int[]> histogram(int[] pixels);
}
