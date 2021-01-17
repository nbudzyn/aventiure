package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Iterator;

public class StemmedWords implements Iterable<String> {
    private final ImmutableList<String> stemmedWords;

    StemmedWords(final Collection<String> stemmedWords) {
        this.stemmedWords = ImmutableList.copyOf(stemmedWords);
    }

    public boolean isEmpty() {
        return stemmedWords.isEmpty();
    }

    public int size() {
        return stemmedWords.size();
    }

    StemmedWords subList(final int fromIndex, final int toIndex) {
        return new StemmedWords(stemmedWords.subList(fromIndex, toIndex));
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return stemmedWords.iterator();
    }
}
