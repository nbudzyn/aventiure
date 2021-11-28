package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Personalpronomen;

/**
 * Der "leerer" textueller Kontext.
 * Anaphorische Bezüge, sind weder möglich noch zwingend.
 * Vorsicht! Wer dies hier verwendet, muss sicher sein,
 * dass er nicht etwas wie *"Rapunzel kämmt <i>Rapunzels</i> Haare" generiert!
 */
public final class EmptyTextContext implements ITextContext {
    public static final EmptyTextContext INSTANCE = new EmptyTextContext();

    private EmptyTextContext() {
    }

    /**
     * Gibt immer {@code null} zurück, da kein anaphorischer Bezug möglich ist.
     */
    @Nullable
    @Override
    public NumerusGenus getNumerusGenusAnaphWennMgl(final IBezugsobjekt bezugsobjekt) {
        return null;
    }

    /**
     * Gibt immer {@code null} zurück, da kein anaphorischer Bezug möglich ist.
     */
    @Nullable
    @Override
    public Personalpronomen getAnaphPersPronWennMgl(final IBezugsobjekt bezugsobjekt) {
        return null;
    }
}
