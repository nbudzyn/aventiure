package de.nb.aventiure2.german.description;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;
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
                          final boolean kommaStehtAus) {
        // TODO Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, kommaStehtAus);
        this.duTextPart = duTextPart;
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // FIXME Derzeit ist die Sache mit dem Komma nicht einheitlich gelöst.
        //  Gut wäre es wohl, wenn die DesriptionParams KEIN isKommaStehtAus
        //  hätten, sondern wenn diese Informatoion hier on-the-fly ermittelt würde.
        //  In der AllgDescription müsste man die Information dann zusätzlich speichern,
        //  damit der Benutzer sie (nur dort?!) angeben kann.
        return duTextPart.getDuHauptsatzMitKonjunktionaladverbWennNoetig(konjunktionaladverb)
                .getString();
    }

    public Wortfolge getDescriptionHauptsatzMitVorfeld(final String vorfeld) {
        return duTextPart.getDuHauptsatzMitVorfeld(vorfeld);
    }

    public String getDescriptionHauptsatzMitSpeziellemVorfeld() {
        return duTextPart.getDuHauptsatzMitSpeziellemVorfeld().getString();
    }

    @Override
    public String getDescriptionHauptsatz() {
        return duTextPart.getDuHauptsatz().getString();
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    public String getDescriptionSatzanschlussOhneSubjekt() {
        return duTextPart.getDuSatzanschlussOhneSubjekt().getString();
    }
}
