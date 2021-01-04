package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

public abstract class AbstractFlexibleDescription<SELF extends AbstractDescription<SELF>>
        extends AbstractDescription<SELF> {
    AbstractFlexibleDescription(final StructuralElement startsNew,
                                final boolean woertlicheRedeNochOffen,
                                final boolean kommaStehtAus) {
        super(startsNew, woertlicheRedeNochOffen, kommaStehtAus);
    }

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitVorfeld(final String vorfeld) {
        return toTextDescriptionKeepParams(toWortfolgeMitVorfeld(vorfeld));
    }

    public abstract ImmutableList<TextDescription> altTextDescriptions();

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
