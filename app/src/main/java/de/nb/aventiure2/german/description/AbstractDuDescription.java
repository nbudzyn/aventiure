package de.nb.aventiure2.german.description;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehst in den Wald."
 */
public abstract class AbstractDuDescription<
        P extends AbstractDuTextPart,
        SELF extends AbstractDescription<SELF>>
        extends AbstractDescription<SELF> {
    final P duTextPart;

    AbstractDuDescription(final StructuralElement startsNew,
                          final P duTextPart,
                          final AvTimeSpan timeElapsed) {
        // TODO Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, timeElapsed);
        this.duTextPart = duTextPart;
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {

        return duTextPart.getDuHauptsatzMitKonjunktionaladverbWennNoetig(konjunktionaladverb);
    }

    public String getDescriptionHauptsatzMitVorfeld(final String vorfeld) {
        return duTextPart.getDuHauptsatzMitVorfeld(vorfeld);
    }

    public String getDescriptionHauptsatzMitSpeziellemVorfeld() {
        return duTextPart.getDuHauptsatzMitSpeziellemVorfeld();
    }

    @Override
    public String getDescriptionHauptsatz() {

        return duTextPart.getDuHauptsatz();
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    public String getDescriptionSatzanschlussOhneSubjekt() {

        return duTextPart.getDuSatzanschlussOhneSubjekt();
    }
}
