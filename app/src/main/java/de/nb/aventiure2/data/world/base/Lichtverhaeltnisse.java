package de.nb.aventiure2.data.world.base;

import static java.util.stream.Collectors.toSet;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ALLMAEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

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
    public ImmutableSet<EinzelnerSemSatz> altLangsamWirdEsSaetze() {
        final ImmutableSet.Builder<EinzelnerSemSatz> alt = ImmutableSet.builder();

        alt.addAll(Stream.of(LANGSAM, ALLMAEHLICH)
                .map(a -> esWirdSatz().mitAdvAngabe(new AdvAngabeSkopusSatz(a)))
                .collect(toSet()));

        return alt.build();
    }

    /**
     * Gibt einen SemSatz zurück wie "es wird hell".
     */
    private EinzelnerSemSatz esWirdSatz() {
        return esWirdSatz(null);
    }

    /**
     * Gibt einen SemSatz zurück wie "und es wird hell".
     */
    private EinzelnerSemSatz esWirdSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return getAdjektiv().alsEsWirdSatz(anschlusswort);
    }

    /**
     * Gibt einen SemSatz zurück wie "es ist hell".
     */
    public EinzelnerSemSatz esIstSatz() {
        return esIstSatz(null);
    }

    /**
     * Gibt einen SemSatz zurück wie "und es ist hell".
     */
    private EinzelnerSemSatz esIstSatz(
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
