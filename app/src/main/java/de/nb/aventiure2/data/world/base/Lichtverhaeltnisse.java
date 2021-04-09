package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ALLMAEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static java.util.stream.Collectors.toSet;

public enum Lichtverhaeltnisse {
    HELL(AdjektivOhneErgaenzungen.HELL), DUNKEL(AdjektivOhneErgaenzungen.DUNKEL);

    private final AdjektivOhneErgaenzungen adjektiv;

    public static ImmutableSet<AbstractDescription<?>> altSCKommtNachDraussenInDunkelheit() {
        return alt().add(neuerSatz("Draußen ist es dunkel"))
                .schonLaenger()
                .build();
    }

    Lichtverhaeltnisse(final AdjektivOhneErgaenzungen adjektiv) {
        this.adjektiv = adjektiv;
    }

    /**
     * Gibt Sätze zurück wie "langsam wird es dunkel"
     */
    public ImmutableSet<EinzelnerSatz> altLangsamWirdEsSaetze() {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(Stream.of(LANGSAM, ALLMAEHLICH)
                .map(a -> esWirdSatz().mitAdvAngabe(new AdvAngabeSkopusSatz(a)))
                .collect(toSet()));

        return alt.build();
    }

    /**
     * Gibt einen Satz zurück wie "es wird hell".
     */
    public EinzelnerSatz esWirdSatz() {
        return esWirdSatz(null);
    }

    /**
     * Gibt einen Satz zurück wie "und es wird hell".
     */
    public EinzelnerSatz esWirdSatz(final @Nullable String anschlusswort) {
        return getAdjektiv().alsEsWirdSatz(anschlusswort);
    }

    public AdjektivOhneErgaenzungen getAdjektiv() {
        return adjektiv;
    }
}
