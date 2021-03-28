package de.nb.aventiure2.german.adjektiv;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ANDERS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BESONDERS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUNKEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EITEL_NICHT_FLEKTIERBAR;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

public class AdjektivTest {
    @Test
    public void test_praedikativ() {
        // GIVEN
        final Adjektiv blau = BLAU.getAdjektiv();
        final Adjektiv dunkel = DUNKEL.getAdjektiv();
        final Adjektiv anders = ANDERS.getAdjektiv();

        assertThat(
                // blau
                blau.getPraedikativ()).isEqualTo("blau");
        assertThat(
                // dunkel
                dunkel.getPraedikativ()).isEqualTo("dunkel");
        assertThat(
                // besonders
                anders.getPraedikativ()).isEqualTo("anders");
    }

    @Test
    public void test_attributiv_regelfall() {
        // GIVEN
        final Adjektiv blau = BLAU.getAdjektiv();

        assertThat(
                // der blaue Himmel
                blau.getAttributiv(M, NOM, true))
                .isEqualTo("blaue");
        assertThat(
                // blaue Himmel
                blau.getAttributiv(PL_MFN, AKK, false))
                .isEqualTo("blaue");
        assertThat(
                // blauer Himmel, ein blauer Himmel
                blau.getAttributiv(M, NOM, false))
                .isEqualTo("blauer");
    }

    @Test
    public void test_attributiv_besonders() {
        // GIVEN
        final Adjektiv besonders = BESONDERS.getAdjektiv();

        assertThat(
                // ein besonderes Bett
                besonders.getAttributiv(N, AKK, false))
                .isEqualTo("besonderes");
    }

    @Test
    public void test_attributiv_eitel() {
        // GIVEN
        final Adjektiv eitel = EITEL_NICHT_FLEKTIERBAR.getAdjektiv();

        assertThat(
                // eitel Sonnenschein
                eitel.getAttributiv(M, NOM, false))
                .isEqualTo("eitel");
    }
}
