package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

public abstract class AbstractFlexibleDescription<SELF extends AbstractDescription<SELF>>
        extends AbstractDescription<SELF> {
    AbstractFlexibleDescription(final StructuralElement startsNew) {
        super(startsNew);
    }

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitVorfeld(final String vorfeld) {
        return toTextDescriptionKeepParams(toWortfolgeMitVorfeld(vorfeld));
    }

    abstract Wortfolge toWortfolgeMitVorfeld(final String vorfeld);

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitSpeziellemVorfeld() {
        return toTextDescriptionKeepParams(toWortfolgeMitSpeziellemVorfeld());
    }

    abstract Wortfolge toWortfolgeMitSpeziellemVorfeld();

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionSatzanschlussOhneSubjekt() {
        return toTextDescriptionKeepParams(toWortfolgeSatzanschlussOhneSubjekt());
    }

    abstract Wortfolge toWortfolgeSatzanschlussOhneSubjekt();

    public abstract boolean hasSubjektDu();
}
