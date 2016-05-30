package de.hs_augsburg.nlp.two.Functional;


import de.hs_augsburg.nlp.two.BasicMonitor.INumberGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicNumberGenerator implements INumberGenerator {
    private final AtomicLong aLong = new AtomicLong();

    @Override
    public synchronized long getNext() {
        return aLong.getAndIncrement();
    }
}
