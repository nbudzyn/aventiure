package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ALLMAEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static java.util.stream.Collectors.toSet;

public enum Lichtverhaeltnisse {
    HELL(AdjektivOhneErgaenzungen.HELL,
            ImmutableSet.of(NomenFlexionsspalte.HELLE)),
    DUNKEL(AdjektivOhneErgaenzungen.DUNKEL,
            ImmutableSet.of(NomenFlexionsspalte.DUNKEL, DUNKELHEIT));

    private final AdjektivOhneErgaenzungen adjektiv;

    private final ImmutableSet<NomenFlexionsspalte> altNomenFlexionsspalten;

    Lichtverhaeltnisse(
            final AdjektivOhneErgaenzungen adjektiv,
            final ImmutableSet<NomenFlexionsspalte> altNomenFlexionsspalten) {
        this.adjektiv = adjektiv;
        this.altNomenFlexionsspalten = altNomenFlexionsspalten;
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
    private EinzelnerSatz esWirdSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
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
    private EinzelnerSatz esIstSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return getAdjektiv().alsEsIstSatz(anschlusswort);
    }

    public AdjektivOhneErgaenzungen getAdjektiv() {
        return adjektiv;
    }

    public ImmutableSet<NomenFlexionsspalte> altNomenFlexionsspalten() {
        return altNomenFlexionsspalten;
    }
}
