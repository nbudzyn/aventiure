package de.nb.aventiure2.german.base;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum StructuralElement implements IKonstituenteOrStructuralElement {
    // Reihenfolge ist relevant! Siehe #max()!
    CHAPTER, PARAGRAPH, SENTENCE, WORD;

    public boolean isAtLeast(final StructuralElement other) {
        return other == StructuralElement.min(this, other);
    }

    public static StructuralElement min(final StructuralElement endsThis,
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
}
