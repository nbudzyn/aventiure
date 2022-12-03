package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Abstrakte Oberklasse für einfache Infinitivkonstruktion mit oder ohne "zu" ("den Frosch
 * ignorieren", "das Leben zu genießen").
 */
public abstract class AbstractEinfacherInfinitiv
        implements IKonstituentenfolgable {
    /**
     * Der Konnektor vor dieser Infitivkonstruktion:
     * "und", "aber", "oder" oder "sondern"
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;

    private final TopolFelder topolFelder;

    private final String verbalkomplex;

    AbstractEinfacherInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final TopolFelder topolFelder, final String verbalkomplex) {
        this.konnektor = konnektor;
        this.topolFelder = topolFelder;
        this.verbalkomplex = verbalkomplex;
    }

    @Override
    @Nullable
    public Konstituentenfolge toKonstituentenfolge() {
        return toKonstituentenfolge(true);
    }

    @Nullable
    private Konstituentenfolge toKonstituentenfolge(final boolean mitNachfeld) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                toKonstituentenfolgeOhneNachfeld(null, false),
                mitNachfeld ? topolFelder.getNachfeld() : null);
    }

    public Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean nachfeldEingereiht) {
        return joinToKonstituentenfolge(
                konnektor,
                getMittelfeld(),
                nachfeldEingereiht ? getNachfeld() : null,
                finiteVerbformFuerOberfeld, // "hat"
                getVerbalkomplex()); // "laufen"

        // (wollen)
    }


    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor() {
        return konnektor;
    }

    public TopolFelder getTopolFelder() {
        return topolFelder;
    }

    @Nonnull
    public Mittelfeld getMittelfeld() {
        return topolFelder.getMittelfeld();
    }

    public String getVerbalkomplex() {
        return verbalkomplex;
    }

    @Nonnull
    @NonNull
    public Nachfeld getNachfeld() {
        return topolFelder.getNachfeld();
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return topolFelder.getSpeziellesVorfeldSehrErwuenscht();
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return topolFelder.getSpeziellesVorfeldAlsWeitereOption();
    }

    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return topolFelder.getRelativpronomen();
    }

    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return topolFelder.getErstesInterrogativwort();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractEinfacherInfinitiv that = (AbstractEinfacherInfinitiv) o;
        return Objects.equals(konnektor, that.konnektor)
                && verbalkomplex.equals(verbalkomplex)
                && topolFelder.equals(that.topolFelder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(konnektor, verbalkomplex, topolFelder);
    }

    @NonNull
    @Override
    public String toString() {
        @Nullable final Konstituentenfolge konstituentenfolge = toKonstituentenfolge();
        if (konstituentenfolge == null) {
            return "";
        }

        return konstituentenfolge.joinToSingleKonstituente().toTextOhneKontext();
    }
}
