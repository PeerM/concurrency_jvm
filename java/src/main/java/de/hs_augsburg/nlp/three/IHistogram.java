package de.hs_augsburg.nlp.three;

import java.util.Map;

public interface IHistogram {
    Map<ColorMask, int[]> histogram(int[] pixels);
}
