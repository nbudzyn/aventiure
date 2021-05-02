package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum StructuralElement implements IKonstituenteOrStructuralElement {
    // Reihenfolge ist relevant! Siehe #max()!
    CHAPTER, PARAGRAPH, SENTENCE, WORD;

    public boolean isAtLeast(final StructuralElement other) {
        return other == StructuralElement.min(this, other);
    }

    private static StructuralElement min(final StructuralElement endsThis,
                                         final StructuralElement startsNew) {
        if (endsThis.ordinal() > startsNew.ordinal()) {
            return endsThis;
        }
        return startsNew;
    }

    public static StructuralElement max(final StructuralElement endsThis,
                                        final StructuralElement startsNew) {
        if (endsThis.ordinal() < startsNew.ordinal()) {
            return endsThis;
        }
        return startsNew;
    }


    @Override
    public Collection<Konstituentenfolge> toAltKonstituentenfolgen() {
        if (this == WORD) {
            return Collections.singletonList(null);
        }

        return ImmutableList.of(new Konstituentenfolge(this));
    }
}
