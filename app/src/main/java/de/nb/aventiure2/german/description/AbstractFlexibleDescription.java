package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;

public abstract class AbstractFlexibleDescription<SELF extends AbstractDescription<SELF>>
        extends AbstractDescription<SELF> {
    AbstractFlexibleDescription(final StructuralElement startsNew,
                                @Nullable final PhorikKandidat phorikKandidat) {
        super(startsNew, phorikKandidat);
    }

    // FIXME Shift-Alt-L (load context) ausprobieren!

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitVorfeld(final String vorfeld) {
        return toTextDescriptionKeepParams(toSingleKonstituenteMitVorfeld(vorfeld));
    }

    @Override
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
        return toSatzanschlussTextDescriptionKeepParams(
                toSingleKonstituenteSatzanschlussOhneSubjekt());
    }

    abstract Konstituente toSingleKonstituenteSatzanschlussOhneSubjekt();

    public abstract boolean hasSubjektDu();
}
