package de.hs_augsburg.nlp.two.BasicMonitor;


public class NumberGenerator implements INumberGenerator {
    private long current = 0;

    @Override
    public synchronized long getNext() {
        return current++;
    }
}
