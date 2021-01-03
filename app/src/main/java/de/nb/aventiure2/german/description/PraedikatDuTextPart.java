package de.nb.aventiure2.german.description;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;

public class PraedikatDuTextPart implements AbstractDuTextPart {
    private final PraedikatOhneLeerstellen praedikat;

    PraedikatDuTextPart(final PraedikatOhneLeerstellen praedikat) {
        this.praedikat = praedikat;
    }

    @Override
    public Wortfolge getDuHauptsatz() {
        return Wortfolge.joinToWortfolge(praedikat
                .alsSatzMitSubjekt(Personalpronomen.get(P2,
                        // Hier muss man darauf achten, keine Sätze mit
                        // "du, der du" zu generieren, weil es ja eine
                        // weibliche Spielerin sein könnte!
                        M))
                .getVerbzweitsatzStandard());
    }

    @Override
    public Wortfolge getDuHauptsatzMitVorfeld(final String vorfeld) {
        return Wortfolge.joinToWortfolge(
                praedikat
                        .alsSatzMitSubjekt(Personalpronomen.get(P2,
                                // Hier muss man darauf achten, keine Sätze mit
                                // "du, der du" zu generieren, weil es ja eine
                                // weibliche Spielerin sein könnte!
                                M))
                        .getVerbzweitsatzMitVorfeld(vorfeld));
    }

    @Override
    public Wortfolge getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final Iterable<Konstituente> speziellesVorfeld =
                praedikat.alsSatzMitSubjekt(Personalpronomen.get(P2, M))
                        .getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
        if (speziellesVorfeld != null) {
            return Wortfolge.joinToNullWortfolge(speziellesVorfeld);
        }

        return getDuHauptsatz();
    }

    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToWortfolge(
                praedikat.getVerbzweit(P2, SG));
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }

    // equals() und hashCode() überschreiben wir extra nicht! Alle PraedikatDuTextParts
    // sollen als "verschieden" gelten. Ansonsten müssten wir auch in allen
    // PraedikatOhneLeerstellen-Implementierungen equals() und hashCode() überschreiben.
    // Das wäre inhaltlich richtig, aber viel Arbeit.
}
