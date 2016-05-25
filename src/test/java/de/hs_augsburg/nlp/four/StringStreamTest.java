package de.hs_augsburg.nlp.four;


import de.hs_augsburg.nlp.four.streams.StringStream;
import de.hs_augsburg.nlp.four.streams.StringStreamParallel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class StringStreamTest {
    public StringStream stringStream;
    public StringStreamParallel stringStreamParallel;
    public Collection<String> words;

    @Before
    public void setUp() {
        this.stringStream = new StringStream();
        this.stringStreamParallel = new StringStreamParallel();
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
        int[] res = stringStream.makeHistogram(words.stream());
        assertEquals(res[97], 36);
    }

    @Test
    public void stringStreamParallelTest() {
        int[] res = stringStream.makeHistogram(words.stream());
        assertEquals(res[97], 36);
    }

}
