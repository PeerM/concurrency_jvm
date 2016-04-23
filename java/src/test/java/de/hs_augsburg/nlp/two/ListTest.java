package de.hs_augsburg.nlp.two;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


@SuppressWarnings("Duplicates")
public class ListTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public VectorQueue<Integer> vectorQueue;

    @Before
    public void setUp() {
        vectorQueue = new VectorQueue<>();
    }

    @Test
    public void offer() {
        for (int i = 0; i < 20; i++) {
            vectorQueue.offer(i);
            vectorQueue.poll();
        }
        assertEquals(vectorQueue.size(), 0);
    }

    @Test
    public void getSize() throws Exception {
        int size = vectorQueue.size();
        assertEquals(0, size);
    }

    @Test
    public void pollWhenEmpty()
    {
        assertNull(vectorQueue.poll());
    }

    @Test
    public void offerWhenFull()
    {
        for(int i = 0; i<50; i++)
        {
            vectorQueue.offer(i);
        }
        assertFalse(vectorQueue.offer(5));

    }
}
