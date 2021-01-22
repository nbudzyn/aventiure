package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

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
        return toTextDescriptionKeepParams(toWortfolgeMitVorfeld(vorfeld));
    }

    @Override
    public Wortfolge toWortfolgeMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        @Nullable final Wortfolge wortfolgeMitSpeziellemVorfeldOrNull =
                toWortfolgeMitSpeziellemVorfeldOrNull();

        if (wortfolgeMitSpeziellemVorfeldOrNull != null) {
            return wortfolgeMitSpeziellemVorfeldOrNull;
        }

        return toWortfolgeMitVorfeld(konjunktionaladverb);
    }

    @Nullable
    protected abstract Wortfolge toWortfolgeMitSpeziellemVorfeldOrNull();

    abstract Wortfolge toWortfolgeMitVorfeld(final String vorfeld);

    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionSatzanschlussOhneSubjekt() {
        return toTextDescriptionKeepParams(toWortfolgeSatzanschlussOhneSubjekt());
    }

    abstract Wortfolge toWortfolgeSatzanschlussOhneSubjekt();

    public abstract boolean hasSubjektDu();
}
