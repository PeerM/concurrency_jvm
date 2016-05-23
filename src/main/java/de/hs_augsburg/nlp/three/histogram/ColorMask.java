package de.hs_augsburg.nlp.three.histogram;


import java.awt.*;

public enum ColorMask {
    RED(16, 0xFF0000, Color.RED), GREEN(8, 0xFF00, Color.GREEN), BLUE(0, 0xFF, Color.BLUE);

    public final Color color;
    private final int offset;
    private final int mask;

    ColorMask(int offset, int mask, Color color) {
        this.offset = offset;
        this.mask = mask;
        this.color = color;
    }

    public int apply(int value) {
        return (value & mask) >> offset;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
