package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableCollection;

import java.util.Objects;

import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

/**
 * Was den den SC am Einschlafen hindern koennte
 */
public final class EinschlafhindernisSc {
    private final int minimaleMuedigkeit;

    private final ImmutableCollection<AbstractDescription<?>> altDescriptions;

    public EinschlafhindernisSc(final int minimaleMuedigkeit,
                                final AltDescriptionsBuilder alt) {
        this(minimaleMuedigkeit, alt.build());
    }


    private EinschlafhindernisSc(final int minimaleMuedigkeit,
                                 final ImmutableCollection<AbstractDescription<?>> altDescriptions) {
        this.minimaleMuedigkeit = minimaleMuedigkeit;
        this.altDescriptions = altDescriptions;
    }

    public int getMinimaleMuedigkeit() {
        return minimaleMuedigkeit;
    }

    public ImmutableCollection<AbstractDescription<?>> altDescriptions() {
        return altDescriptions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinschlafhindernisSc that = (EinschlafhindernisSc) o;
        return minimaleMuedigkeit == that.minimaleMuedigkeit &&
                altDescriptions.equals(that.altDescriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimaleMuedigkeit, altDescriptions);
    }

    @Override
    public String toString() {
        return "EinschlafhindernisSc{" +
                "minimaleMuedigkeit=" + minimaleMuedigkeit +
                ", altDescriptions=" + altDescriptions +
                '}';
    }
}
