package de.hs_augsburg.nlp.two;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ListTest {
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
    public void pollWhenEmpty() {
        assertNull(vectorQueue.poll());
    }

    @Test
    public void offerWhenFull() {
        for (int i = 0; i < 50; i++) {
            vectorQueue.offer(i);
        }
        assertFalse(vectorQueue.offer(5));

    }

    @Test
    public void fifoTest() {
        for (int i = 0; i < 50; i++) {
            vectorQueue.offer(i);
        }
        for (int j = 0; j < 50; j++) {
            int out = vectorQueue.poll();
            assertEquals(j, out);
        }
    }

    @Test
    public void objectCorrect() {
        vectorQueue.offer(5);
        int out = vectorQueue.poll();
        assertEquals(5, out);
    }
}
