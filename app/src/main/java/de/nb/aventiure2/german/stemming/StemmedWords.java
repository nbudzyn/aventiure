package de.nb.aventiure2.german.stemming;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

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
                return new StemmedWords(res); // ==>
            }

            int from = to;

            while (from >= 0 && Character.isLetter(text.charAt(from))) {
                from--;
            }

            res.addFirst(GermanStemmer.stemWord(text.substring(from + 1, to + 1)));

            if (res.size() >= maxWords) {
                return new StemmedWords(res);
            }

            to = from;
        }
    }

    private StemmedWords(final Collection<String> stemmedWords) {
        this.stemmedWords = ImmutableList.copyOf(stemmedWords);
    }

    public boolean isEmpty() {
        return stemmedWords.isEmpty();
    }

    public int size() {
        return stemmedWords.size();
    }

    public StemmedWords subList(final int fromIndex, final int toIndex) {
        return new StemmedWords(stemmedWords.subList(fromIndex, toIndex));
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return stemmedWords.iterator();
    }
}
