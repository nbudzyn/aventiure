package de.nb.aventiure2.german.description;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * A description of something and the time it takes.
 */
public class TimedDescription<D extends AbstractDescription<?>> {
    private final D description;

    /**
     * Zeit, die vergangen ist, während das Beschriebene geschehen ist
     */
    private final AvTimeSpan timeElapsed;

    /**
     * Wenn der Text sicher erzählt wird, wird dieser Counter hochgezählt.
     */
    @Nullable
    private final String counterIdIncrementedIfTextIsNarrated;

    public TimedDescription(final D description,
                            final AvTimeSpan timeElapsed) {
        this(description, timeElapsed, null);
    }
    public TimedDescription(final  D description,
                            final AvTimeSpan timeElapsed,
                            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        this.description = description;
        this.timeElapsed = timeElapsed;
        this.counterIdIncrementedIfTextIsNarrated = counterIdIncrementedIfTextIsNarrated;
    }

    @CheckReturnValue
    public TimedDescription<D> multiplyTimeElapsedWith(final double speedFactor) {
        return new TimedDescription<>(description,
                timeElapsed.times(speedFactor), counterIdIncrementedIfTextIsNarrated);
    }

    public StructuralElement getStartsNew() {
        return getDescription().getStartsNew();
    }

    public TimedDescription<D> komma() {
        getDescription().komma(true);
        return this;
    }

    public TimedDescription<D> komma(final boolean kommaStehtAus) {
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
    public TimedDescription<D> undWartest() {
        getDescription().undWartest(true);
        return this;
    }

    public TimedDescription<D> undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        getDescription().undWartest(
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt);
        return this;
    }

    public boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return getDescription().isAllowsAdditionalDuSatzreihengliedOhneSubjekt();
    }

    public TimedDescription<D> dann() {
        getDescription().dann(true);
        return this;
    }

    public TimedDescription<D> dann(final boolean dann) {
        getDescription().dann(dann);
        return this;
    }

    public boolean isDann() {
        return getDescription().isDann();
    }

    public TimedDescription<D> beendet(final StructuralElement structuralElement) {
        getDescription().beendet(structuralElement);
        return this;
    }

    public TimedDescription<D> phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                                           final IBezugsobjekt bezugsobjekt) {
        getDescription().phorikKandidat(substantivischePhrase.getNumerusGenus(), bezugsobjekt);
        return this;
    }

    public TimedDescription<D> phorikKandidat(final NumerusGenus numerusGenus,
                                           final IBezugsobjekt bezugsobjekt) {
        getDescription().phorikKandidat(new PhorikKandidat(numerusGenus, bezugsobjekt));
        return this;
    }

    public TimedDescription<D> phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        getDescription().phorikKandidat(phorikKandidat);
        return this;
    }

    public D getDescription() {
        return description;
    }

    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }

    @Nullable
    public String getCounterIdIncrementedIfTextIsNarrated() {
        return counterIdIncrementedIfTextIsNarrated;
    }
}
