package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * A description based on a {@link PraedikatOhneLeerstellen} - assuming the player
 * character is the (first) subject. Somehting like "Du gehst in den Wald."
 */
public class PraedikatDuDescription
        extends AbstractDuDescription<PraedikatOhneLeerstellen, PraedikatDuDescription> {
    PraedikatDuDescription(final StructuralElement startsNew,
                           final PraedikatOhneLeerstellen praedikat) {
        super(startsNew, praedikat);
    }

    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return copy(duTextPart.mitAdverbialerAngabe(adverbialeAngabe));
    }

    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return copy(duTextPart.mitAdverbialerAngabe(adverbialeAngabe));
    }

    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return copy(duTextPart.mitAdverbialerAngabe(adverbialeAngabe));
    }

    private PraedikatDuDescription copy(final PraedikatOhneLeerstellen praedikat) {
        return new PraedikatDuDescription(getStartsNew(),
                praedikat);
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return duTextPart;
    }
}
