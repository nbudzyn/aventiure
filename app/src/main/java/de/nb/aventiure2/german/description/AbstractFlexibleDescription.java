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
    public final TextDescription toTextDescriptionSatzanschlussOhneSubjekt() {
        return toTextDescriptionKeepParams(
                toSingleKonstituenteSatzanschlussOhneSubjekt());
    }

    abstract Konstituente toSingleKonstituenteSatzanschlussOhneSubjekt();

    public abstract boolean hasSubjektDu();

    /**
     * Ob vermieden werden soll, dass ein Satz mit "und" vorangestellt
     * werden soll.
     * <p>
     * Relevant, wenn ein weiterer Satz vorangestellt werden soll und man
     * "... und ... und..." vermeiden und statt dessen dann lieber "..., ... und... "
     * schreiben möchte
     */
    public abstract boolean vorangestelltenSatzanschlussMitUndVermeiden();
}
