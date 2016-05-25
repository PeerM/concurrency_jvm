package de.hs_augsburg.nlp.four;


import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

public class StringStreamTest {
    public StringStream stringStream;
    public Collection<String> words;

    @Before
    public void setUp() {
        this.stringStream = new StringStream();
        this.words = new LinkedList<>();
        words.add("aaaa");
        words.add("aaaa");
        words.add("aaaa");
        words.add("aaaaaaaa");
        words.add("aaaaaaaa");
        words.add("aaaaaaaa");
        words.add("qoprui");
    }

    @Test
    public void stringStreamCorrectnessTest() {
        stringStream.makeHistogram(words.stream());
    }
}
