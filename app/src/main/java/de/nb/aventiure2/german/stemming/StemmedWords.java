package de.nb.aventiure2.german.stemming;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class StemmedWords implements Iterable<String> {
    private final ImmutableList<String> stemmedWords;

    /**
     * Teilt den Text grob in einzelne Wortstämme auf
     */
    public static StemmedWords stem(final String text) {
        return stemEnd(text, Integer.MAX_VALUE);
    }

    /**
     * Teilt das Ende des Textes grob in einzelne Wortstämme auf
     */
    public static StemmedWords stemEnd(@NonNull final String text,
                                       final int maxWords) {
        final LinkedList<String> res = new LinkedList<>();
        int to = text.length() - 1;
        while (true) {
            while (to >= 0 && !Character.isLetter(text.charAt(to))) {
                to--;
            }
            if (to < 0) {
                return new StemmedWords(ImmutableList.copyOf(res)); // ==>
            }

            int from = to;

            while (from >= 0 && Character.isLetter(text.charAt(from))) {
                from--;
            }

            res.addFirst(GermanStemmer.stemWord(text.substring(from + 1, to + 1)));

            if (res.size() >= maxWords) {
                return new StemmedWords(ImmutableList.copyOf(res));
            }

            to = from;
        }
    }

    public StemmedWords(final ImmutableList<String> stemmedWords) {
        this.stemmedWords = stemmedWords;
    }

    public boolean isEmpty() {
        return stemmedWords.isEmpty();
    }


    public String get(final int index) {
        return stemmedWords.get(index);
    }

    public int size() {
        return stemmedWords.size();
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return stemmedWords.iterator();
    }

    public static boolean subListsEqual(final StemmedWords one, final int oneFrom,
                                        final StemmedWords other, final int otherFrom,
                                        final int length) {
        for (int i = 0; i < length; i++) {
            if (!Objects.equals(
                    one.stemmedWords.get(oneFrom + i),
                    other.stemmedWords.get(otherFrom + i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StemmedWords strings = (StemmedWords) o;
        return stemmedWords.equals(strings.stemmedWords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stemmedWords);
    }
}
