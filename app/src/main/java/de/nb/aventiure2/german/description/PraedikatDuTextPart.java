package de.nb.aventiure2.german.description;

import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

public class PraedikatDuTextPart implements AbstractDuTextPart {
    private final PraedikatOhneLeerstellen praedikat;

    PraedikatDuTextPart(final PraedikatOhneLeerstellen praedikat) {
        this.praedikat = praedikat;
    }

    @Override
    public Wortfolge getDuHauptsatz() {
        return Wortfolge.joinToNullWortfolge(
                praedikat.getDuHauptsatz(new Modalpartikel[0]));
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
}
