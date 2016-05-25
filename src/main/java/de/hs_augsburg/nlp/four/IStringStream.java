package de.hs_augsburg.nlp.four;

import de.hs_augsburg.nlp.three.histogram.ClojureHelpers;

import java.util.stream.Stream;

public interface IStringStream {

    int[] makeHistogram(Stream<String> words);
}
