package de.hs_augsburg.nlp.four;


import de.hs_augsburg.nlp.three.histogram.ClojureHelpers;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Stream;

public class StringStreamParallel {
    public Collection<String> strings;
    public Stream stream;

    public static void main(String[] args) {
        StringStream stringStream = new StringStream();
        Stream<String> words = stringStream.getWords("pride.txt");
        stringStream.makeHistogram(words);
    }

    public Stream<String> getWords(String filePath)
    {
        Stream<String> res;
        try {
            res = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream(filePath), StandardCharsets.UTF_8).stream();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return res;
    }

    public int[] makeHistogram(Stream<String> words){
        int[] hist = words
                .flatMap(word -> word.chars().boxed())
                .parallel()
                .collect(
                        () -> new int[256],
                        (int[] ints, Integer integer) -> {
                            ints[integer]++;
                        },
                        ClojureHelpers::arrayElementBasedAddImperativ);
        System.out.println(hist);
        return hist;
    }
}
