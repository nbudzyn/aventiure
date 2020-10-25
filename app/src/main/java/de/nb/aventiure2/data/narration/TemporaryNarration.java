package de.nb.aventiure2.data.narration;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.description.AbstractDescription;

/**
 * Das hier wurde bereits mit <code>narrate(...)</code> an den {@link Narrator} übergeben,
 * aber der Narration noch nicht hinzugefügt. Der <code>Narrator</code> hat also noch
 * Freiheit, wie er dies hier genau erzählen möchte.
 */
@Immutable
@ParametersAreNonnullByDefault
public class TemporaryNarration {
    @Nonnull
    private final Narration.NarrationSource narrationSource;

    @Nonnull
    private final Collection<AbstractDescription<?>> descriptionAlternatives;

    public TemporaryNarration(
            final Narration.NarrationSource narrationSource,
            final Collection<AbstractDescription<?>> descriptionAlternatives) {
        this.narrationSource = narrationSource;
        this.descriptionAlternatives = descriptionAlternatives;
    }

    @Nonnull
    public Narration.NarrationSource getNarrationSource() {
        return narrationSource;
    }

    @Nonnull
    public Collection<AbstractDescription<?>> getDescriptionAlternatives() {
        return descriptionAlternatives;
    }
}