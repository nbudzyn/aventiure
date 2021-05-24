package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Konstituente;

public abstract class AbstractFlexibleDescription<SELF extends AbstractDescription<SELF>>
        extends AbstractDescription<SELF> {

    AbstractFlexibleDescription() {
        super();
    }

    AbstractFlexibleDescription(final DescriptionParams params) {
        super(params);
    }

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitVorfeld(final String vorfeld) {
        return toTextDescriptionKeepParams(
                toSingleKonstituenteMitVorfeld(vorfeld));
    }

    @Override
    @CheckReturnValue
    public Konstituente toSingleKonstituenteMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        @Nullable final Konstituente mitSpeziellemVorfeldOrNull =
                toSingleKonstituenteMitSpeziellemVorfeldOrNull();

        if (mitSpeziellemVorfeldOrNull != null) {
            return mitSpeziellemVorfeldOrNull;
        }

        return toSingleKonstituenteMitVorfeld(konjunktionaladverb);
    }

    @Nullable
    protected abstract Konstituente toSingleKonstituenteMitSpeziellemVorfeldOrNull();

    abstract Konstituente toSingleKonstituenteMitVorfeld(final String vorfeld);

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionSatzanschlussOhneSubjektOhneAnschlusswort() {
        return toTextDescriptionKeepParams(
                toSingleKonstituenteSatzanschlussOhneSubjektOhneAnschlusswortOhneKomma());
    }

    abstract Konstituente toSingleKonstituenteSatzanschlussOhneSubjektOhneAnschlusswortOhneKomma();

    @NonNull
    @CheckReturnValue
    public abstract TextDescription toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma();

    public abstract boolean hasSubjektDu();

    /**
     * Gibt zurück, ob diese Description ein Anschlusswort besitzt, das Semantik trägt
     * (z.B. "aber", "oder" etc. - im Gegensatz zu "und" oder gar keinem Anschlusswort).
     */
    public abstract boolean hasAnschlusswortDasBedeutungTraegt();
}
