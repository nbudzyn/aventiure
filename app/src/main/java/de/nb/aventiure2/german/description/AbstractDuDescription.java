package de.nb.aventiure2.german.description;

import java.util.Objects;

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
        // FIXME Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final AbstractDuDescription<?, ?> that = (AbstractDuDescription<?, ?>) o;
        return duTextPart.equals(that.duTextPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), duTextPart);
    }
}
