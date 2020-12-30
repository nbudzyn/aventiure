package de.nb.aventiure2.german.description;

import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

public class PraedikatDuTextPart implements AbstractDuTextPart {
    private final PraedikatOhneLeerstellen praedikat;

    PraedikatDuTextPart(final PraedikatOhneLeerstellen praedikat) {
        this.praedikat = praedikat;
    }

    @Override
    public Wortfolge getDuHauptsatz() {
        return Wortfolge.joinToNullWortfolge(praedikat.getDuHauptsatz());
    }

    @Override
    public Wortfolge getDuHauptsatzMitVorfeld(final String vorfeld) {
        return Wortfolge.joinToNullWortfolge(
                praedikat.getDuHauptsatzMitVorfeld(vorfeld));
    }

    @Override
    public Wortfolge getDuHauptsatzMitSpeziellemVorfeld() {
        return Wortfolge.joinToNullWortfolge(
                praedikat.getDuHauptsatzMitSpeziellemVorfeld());
    }

    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToNullWortfolge(
                praedikat.getDuSatzanschlussOhneSubjekt());
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }

    // equals() und hashCode() überschreiben wir extra nicht! Alle PraedikatDuTextParts
    // sollen als "verschieden" gelten. Ansonsten müssten wir auch in allen
    // PraedikatOhneLeerstellen-Implementierungen equals() und hashCode() überschreiben.
    // Das wäre inhaltlich richtig, aber viel Arbeit.
}
