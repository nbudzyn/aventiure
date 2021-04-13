package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ALLMAEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static java.util.stream.Collectors.toSet;

public enum Lichtverhaeltnisse {
    HELL(AdjektivOhneErgaenzungen.HELL), DUNKEL(AdjektivOhneErgaenzungen.DUNKEL);

    private final AdjektivOhneErgaenzungen adjektiv;

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
    private EinzelnerSatz esWirdSatz() {
        return esWirdSatz(null);
    }

    /**
     * Gibt einen Satz zurück wie "und es wird hell".
     */
    private EinzelnerSatz esWirdSatz(final @Nullable String anschlusswort) {
        return getAdjektiv().alsEsWirdSatz(anschlusswort);
    }

    /**
     * Gibt einen Satz zurück wie "es ist hell".
     */
    public EinzelnerSatz esIstSatz() {
        return esIstSatz(null);
    }

    /**
     * Gibt einen Satz zurück wie "und es ist hell".
     */
    private EinzelnerSatz esIstSatz(final @Nullable String anschlusswort) {
        return getAdjektiv().alsEsIstSatz(anschlusswort);
    }

    private AdjektivOhneErgaenzungen getAdjektiv() {
        return adjektiv;
    }
}
