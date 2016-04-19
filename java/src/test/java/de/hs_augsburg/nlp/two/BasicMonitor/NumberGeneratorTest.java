package de.hs_augsburg.nlp.two.BasicMonitor;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.LongStream;

import static org.junit.Assert.*;

public class NumberGeneratorTest {
    @Test
    public void testNext() throws Exception {
        NumberGenerator gen = new NumberGenerator();
        assertEquals(0, gen.getNext());
        assertEquals(1, gen.getNext());
        assertEquals(2, gen.getNext());
        assertEquals(3, gen.getNext());
    }

    @Test
    public void testParallel() throws Exception {
        NumberGenerator gen = new NumberGenerator();
        long[] values = LongStream.range(0, 300).parallel().map(i -> gen.getNext()).sorted().toArray();
        Assert.assertArrayEquals(LongStream.range(0, 300).toArray(), values);
    }
}