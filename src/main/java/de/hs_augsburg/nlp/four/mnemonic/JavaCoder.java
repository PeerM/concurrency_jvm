package de.hs_augsburg.nlp.four.mnemonic;

import com.google.common.base.Joiner;
import one.util.streamex.IntStreamEx;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.pcollections.ConsPStack;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.pcollections.PStack;

import java.util.*;
import java.util.stream.Collectors;

public class JavaCoder {
    private final Map<String, String> charCode;
    private final Map<String, List<String>> wordsForNum;
    private Map<String, String> mnemonics;

    public JavaCoder(List<String> dictionary) {
        mnemonics = new HashMap<>(8);
        mnemonics.put("2", "ABC");
        mnemonics.put("3", "DEF");
        mnemonics.put("4", "GHI");
        mnemonics.put("5", "JKL");
        mnemonics.put("6", "MNO");
        mnemonics.put("7", "PQRS");
        mnemonics.put("8", "TUV");
        mnemonics.put("9", "WXYZ");
        charCode = mnemonics.entrySet().stream()
                .flatMap(
                        entry -> entry.getValue()
                                .chars()
                                .mapToObj(
                                        value -> new ImmutablePair<>(String.valueOf(value), entry.getKey())))
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));

        wordsForNum = dictionary.stream().collect(Collectors.groupingBy(this::wordCode));
    }

    private String wordCode(String word) {
        return word.toUpperCase().chars().mapToObj(value -> charCode.get(String.valueOf(value))).reduce("", String::concat);
    }

    public Set<String> translate(String number) {
        return encode(number).stream().map(strings -> Joiner.on(" ").join(strings)).collect(Collectors.toSet());
    }

    public Set<PStack<String>> encode(String number) {
        if (number.isEmpty()) {
            return HashTreePSet.singleton(ConsPStack.empty());
        }
        return IntStreamEx
                .range(1, number.length() + 1)
                .flatMapToObj(i -> {
                    String first = number.substring(0, i);
                    String second = number.substring(i, number.length());
                    List<String> possible_words = wordsForNum.getOrDefault(first, ConsPStack.empty());
                    return possible_words
                            .stream()
                            .flatMap(word -> encode(second)
                                    .stream()
                                    .map((PStack<String> rest) -> rest.plus(word)));
                }).collect(Collectors.toSet());
//        PSet<PStack<String>> accumulator = HashTreePSet.empty();
//        for (int i = 1; i < number.length() + 1; i++) {
//            String first = number.substring(0, i);
//            String second = number.substring(i, number.length());
//            List<String> possible_words = wordsForNum.getOrDefault(first, ConsPStack.empty());
//            for (String word : possible_words) {
//                for (PStack<String> rest : encode(second)) {
//                    accumulator = accumulator.plus(rest.plus(word));
//                }
//            }
//        }
//        return accumulator;
    }

}
