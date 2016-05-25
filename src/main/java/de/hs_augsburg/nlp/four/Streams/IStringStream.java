package de.hs_augsburg.nlp.four.streams;

import java.util.stream.Stream;

public interface IStringStream {

    int[] makeHistogram(Stream<String> words);
}
