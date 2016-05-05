package de.hs_augsburg.nlp.three;


public enum ColorMask {
    RED(32, 0xFF0000), GREEN(16, 0xFF00), BLUE(0, 0xFF);

    private final int offset;
    private int mask;

    ColorMask(int offset, int mask) {
        this.offset = offset;
        this.mask = mask;
    }

    public int apply(int value) {
        return (value & mask) >> offset;
    }
}
