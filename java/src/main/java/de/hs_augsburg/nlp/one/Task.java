package de.hs_augsburg.nlp.one;

public class Task {
    final long start;
    final long end;
    final long number;

    Task(long start, long end, long number) {
        this.start = start;
        this.end = end;
        this.number = number;
    }

    public static Task construct(long start, long end, long number) {
        return new Task(start, end, number);
    }

    @Override
    public String toString() {
        return "start: " + start + ", end: " + end;
    }
}