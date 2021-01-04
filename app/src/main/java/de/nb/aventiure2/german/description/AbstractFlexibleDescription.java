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
        return toTextDescriptionKeepParams(getDescriptionHauptsatzMitVorfeld(vorfeld));
    }

    public abstract ImmutableList<TextDescription> altDescriptionHaupsaetze();

    abstract Wortfolge getDescriptionHauptsatzMitVorfeld(final String vorfeld);

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitSpeziellemVorfeld() {
        return toTextDescriptionKeepParams(getDescriptionHauptsatzMitSpeziellemVorfeld());
    }

    abstract Wortfolge getDescriptionHauptsatzMitSpeziellemVorfeld();

    // FIXME Wortfolge sollte "extern" nicht mehr verwendet werden - stattdessen
    //  TextDescription.
    public abstract Wortfolge getDescriptionSatzanschlussOhneSubjekt();

    public abstract boolean hasSubjektDu();
}
