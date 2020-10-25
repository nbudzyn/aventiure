package de.nb.aventiure2.data.narration;

import androidx.annotation.Nullable;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.description.AbstractDescription;

/**
 * Das hier wurde bereits mit <code>narrate(...)</code> an den {@link Narrator} übergeben,
 * aber der Narration noch nicht hinzugefügt. Der <code>Narrator</code> hat also noch
 * Freiheit, wie er dies hier genau erzählen möchte.
 */
@Immutable
public class TemporaryNarration {
    private final Narration.NarrationSource narrationSource;
    @Nonnull
    private final Collection<AbstractDescription<?>> descriptionAlternatives;

    public TemporaryNarration(
            final Narration.NarrationSource narrationSource,
            final Collection<AbstractDescription<?>> descriptionAlternatives) {
        this.narrationSource = narrationSource;
        this.descriptionAlternatives = descriptionAlternatives;
    }

    public Narration.NarrationSource getNarrationSource() {
        return narrationSource;
    }

    @Nullable
    public Collection<AbstractDescription<?>> getDescriptionAlternatives() {
        return descriptionAlternatives;
    }
}