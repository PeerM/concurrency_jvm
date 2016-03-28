package de.hs_augsburg.nlp.util;

import de.hs_augsburg.meixner.utils.profiling.Clock;

import java.util.function.Supplier;

public class TimeIt {

    public static <T> TimeData<T> timeIt(Supplier<T> function) {
        Clock.startRec();
        T returned = function.get();
        Clock.stopRec();
        TimeData<T> data = new TimeData<>(returned, Clock.elapsed(), Clock.elapsedCpu());
        Clock.reset();
        return data;
    }

    public static class TimeData<O> {
        public final O returned;
        public final long elapsed;
        public final long elapsedCpu;

        TimeData(O returned, long elapsed, long elapsedCpu) {
            this.returned = returned;
            this.elapsed = elapsed;
            this.elapsedCpu = elapsedCpu;
        }
    }
}
