package de.nb.aventiure2.german.base;

public enum StructuralElement {
    // Reihenfolge ist relevant! Siehe #max()!
    CHAPTER, PARAGRAPH, SENTENCE, WORD;

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
