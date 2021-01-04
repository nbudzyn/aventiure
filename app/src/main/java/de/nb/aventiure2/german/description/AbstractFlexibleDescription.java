package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

public abstract class AbstractFlexibleDescription<SELF extends AbstractDescription<SELF>>
        extends AbstractDescription<SELF> {
    // FIXME Wortfolge sollte hier nicht mehr verwendet werden - stattdessen
    //  AllgDescription.

    public AbstractFlexibleDescription(final StructuralElement startsNew) {
        super(startsNew);
    }

    AbstractFlexibleDescription(final StructuralElement startsNew,
                                final boolean woertlicheRedeNochOffen,
                                final boolean kommaStehtAus) {
        super(startsNew, woertlicheRedeNochOffen, kommaStehtAus);
    }

    protected AbstractFlexibleDescription(final DescriptionParams params) {
        super(params);
    }

    @NonNull
    @CheckReturnValue
    public AllgDescription toAllgDescriptionMitVorfeld(final String vorfeld) {
        return toAllgDescriptionKeepParams(getDescriptionHauptsatzMitVorfeld(vorfeld));
    }

    public abstract ImmutableList<AllgDescription> altDescriptionHaupsaetze();

    abstract Wortfolge getDescriptionHauptsatzMitVorfeld(final String vorfeld);

    public abstract String getDescriptionHauptsatzMitSpeziellemVorfeld();

    public abstract Wortfolge getDescriptionSatzanschlussOhneSubjekt();

    public abstract boolean hasSubjektDu();
}
