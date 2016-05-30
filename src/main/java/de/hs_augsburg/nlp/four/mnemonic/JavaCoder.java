package de.hs_augsburg.nlp.four.mnemonic;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import one.util.streamex.IntStreamEx;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.pcollections.ConsPStack;
import org.pcollections.HashTreePSet;
import org.pcollections.PStack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class JavaCoder {
    private final Map<String, String> charCode;
    private final Map<String, List<String>> wordsForNum;

    public JavaCoder(List<String> dictionary) {
        Map<String, String> mnemonics = ImmutableMap.<String, String>builder()
                .put("2", "ABC")
                .put("3", "DEF")
                .put("4", "GHI")
                .put("5", "JKL")
                .put("6", "MNO")
                .put("7", "PQRS")
                .put("8", "TUV")
                .put("9", "WXYZ")
                .build();
        charCode = mnemonics.entrySet().stream()
                .flatMap(entry -> entry.getValue()
                        .chars()
                        .mapToObj(value -> new ImmutablePair<>(String.valueOf(value), entry.getKey())))
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
                .flatMapToObj(i -> wordsForNum.getOrDefault(number.substring(0, i), ConsPStack.empty())
                        .stream()
                        .flatMap(word -> encode(number.substring(i, number.length()))
                                .stream()
                                .map((PStack<String> rest) -> rest.plus(word)))).collect(Collectors.toSet());
    }

}
