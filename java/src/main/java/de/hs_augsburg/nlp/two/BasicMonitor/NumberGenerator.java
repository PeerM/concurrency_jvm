package de.hs_augsburg.nlp.two.BasicMonitor;


public class NumberGenerator {
    private long current = 0;

    public synchronized long getNext() {
        return current++;
    }
}
