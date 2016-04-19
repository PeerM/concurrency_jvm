package de.hs_augsburg.nlp.two;

import org.openjdk.jmh.annotations.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Fork(2)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Threads(4)
public @interface CommonBenchmark {
}
