package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * A description of something and the time it takes.
 */
public class TimedDescription {
    private final AbstractDescription<?> description;

    /**
     * Zeit, die vergangen ist, während das Beschriebene geschehen ist
     */
    private final AvTimeSpan timeElapsed;

    public TimedDescription(final AbstractDescription<?> description,
                            final AvTimeSpan timeElapsed) {
        this.description = description;
        this.timeElapsed = timeElapsed;
    }

    public StructuralElement getStartsNew() {
        return getDescription().getStartsNew();
    }

    public TimedDescription komma() {
        getDescription().komma(true);
        return this;
    }

    public TimedDescription komma(final boolean kommaStehtAus) {
        getDescription().komma(kommaStehtAus);
        return this;
    }

    public boolean isKommaStehtAus() {
        return getDescription().isKommaStehtAus();
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public TimedDescription undWartest() {
        getDescription().undWartest(true);
        return this;
    }

    public TimedDescription undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        getDescription().undWartest(
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt);
        return this;
    }

    public boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return getDescription().isAllowsAdditionalDuSatzreihengliedOhneSubjekt();
    }

    public TimedDescription dann() {
        getDescription().dann(true);
        return this;
    }

    public TimedDescription dann(final boolean dann) {
        getDescription().dann(dann);
        return this;
    }

    public boolean isDann() {
        return getDescription().isDann();
    }

    public TimedDescription beendet(final StructuralElement structuralElement) {
        getDescription().beendet(structuralElement);
        return this;
    }

    public TimedDescription phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                                           final IBezugsobjekt bezugsobjekt) {
        getDescription().phorikKandidat(substantivischePhrase.getNumerusGenus(), bezugsobjekt);
        return this;
    }

    public TimedDescription phorikKandidat(final NumerusGenus numerusGenus,
                                           final IBezugsobjekt bezugsobjekt) {
        getDescription().phorikKandidat(new PhorikKandidat(numerusGenus, bezugsobjekt));
        return this;
    }

    public TimedDescription phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        getDescription().phorikKandidat(phorikKandidat);
        return this;
    }

    public AbstractDescription<?> getDescription() {
        return description;
    }

    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }

}
