package de.nb.aventiure2.german.description;

import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

public class StructuredDuTextPart implements AbstractDuTextPart {
    private final Satz satz;

    StructuredDuTextPart(final Satz satz) {
        this.satz = satz;
    }

    @Override
    public Wortfolge getDuHauptsatz() {
        return Wortfolge.joinToWortfolge(satz.getVerbzweitsatzStandard());
    }

    @Override
    public Wortfolge getDuHauptsatzMitVorfeld(final String vorfeld) {
        return Wortfolge.joinToWortfolge(satz.getVerbzweitsatzMitVorfeld(vorfeld));
    }

    @Override
    public Wortfolge getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final Iterable<Konstituente> speziellesVorfeld =
                satz.getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
        if (speziellesVorfeld != null) {
            return Wortfolge.joinToNullWortfolge(speziellesVorfeld);
        }

        return getDuHauptsatz();
    }

    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToWortfolge(getPraedikat().getVerbzweit(satz.getSubjekt()));
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return satz.getPraedikat();
    }

    public Satz getSatz() {
        return satz;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StructuredDuTextPart that = (StructuredDuTextPart) o;
        return satz.equals(that.satz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(satz);
    }
}
