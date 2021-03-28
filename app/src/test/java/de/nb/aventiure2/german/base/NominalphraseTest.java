package de.nb.aventiure2.german.base;

import org.junit.Test;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.adjektiv.AdjektivMitZuInfinitiv;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BESONDERS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUNKEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EITEL_NICHT_FLEKTIERBAR;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROEHLICH;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ANGEBOTE_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DINGE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.EIN_GESPRAECH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FREUDE_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.DISKUTIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

@SuppressWarnings("ConstantConditions")
public class NominalphraseTest {
    @Test
    public void test_nurAdjektiattribute_Def_Nom_Sg() {
        // GIVEN
        final Nominalphrase np = HIMMEL.mit(BLAU);

        // WHEN
        final Konstituente actual = np.imK(NOM).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("der blaue Himmel");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(M);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_Def_Dat_Pl() {
        // GIVEN
        final Nominalphrase np = DINGE.mit(BESONDERS);

        // WHEN
        final Konstituente actual = np.imK(DAT).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("den besonderen Dingen");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(PL_MFN);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_Indef_Nom_Sg() {
        // GIVEN
        final Nominalphrase np = EIN_GESPRAECH.mit(FROEHLICH);

        // WHEN
        final Konstituente actual = np.imK(NOM).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("ein fröhliches Gespräch");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(N);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_Indef_Dat_Sg() {
        // GIVEN
        final Nominalphrase np = EIN_GESPRAECH.mit(FROEHLICH);

        // WHEN
        final Konstituente actual = np.imK(DAT).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("einem fröhlichen Gespräch");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(N);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_Indef_Pl() {
        // GIVEN
        final Nominalphrase np = ANGEBOTE_OHNE_ART.mit(DUNKEL);

        // WHEN
        final Konstituente actual = np.imK(AKK).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("dunkle Angebote");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(PL_MFN);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektivattribute_ohne_Artikel() {
        // GIVEN
        final Nominalphrase np = FREUDE_OHNE_ART.mit(BESONDERS);

        // WHEN
        final Konstituente actual = np.imK(AKK).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("besondere Freude");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(F);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektivattribute_nicht_flektierbar() {
        // GIVEN
        final Nominalphrase np = FREUDE_OHNE_ART.mit(EITEL_NICHT_FLEKTIERBAR);

        // WHEN
        final Konstituente actual = np.imK(NOM).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("eitel Freude");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(F);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_GraduativeAngabe() {
        // GIVEN
        final Nominalphrase np = HIMMEL.mit(DUNKEL.mitGraduativerAngabe("sehr"));

        // WHEN
        final Konstituente actual = np.imK(DAT).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("dem sehr dunklen Himmel");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(M);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nichtNom_nurRelativsatz() {
        // GIVEN
        // "Rapunzel, glücklich, dich zu sehen[,]"
        final Nominalphrase np = np(
                // Adjektivphrase: "glücklich, dich zu sehen"
                AdjektivMitZuInfinitiv.GLUECKLICH.mitLexikalischerKern(SEHEN.mit(duSc())),
                // Nomen: "Rapunzel"
                NomenFlexionsspalte.RAPUNZEL,
                // Bezugsobjekt: Rapunzel
                World.RAPUNZEL);

        // WHEN
        // "Du diskutierst mit Rapunzel, die glücklich ist, dich zu sehen[,]"
        final Satz satz = DISKUTIEREN.mit(np).alsSatzMitSubjekt(duSc());

        final Konstituentenfolge konstituentenfolge = satz.getVerbzweitsatzStandard();
        final Konstituente actual = konstituentenfolge.joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo(
                "du diskutierst mit Rapunzel, die glücklich ist, dich zu sehen");
        assertThat(actual.kommaStehtAus()).isTrue();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getPhorikKandidat().getBezugsobjekt()).isSameInstanceAs(World.RAPUNZEL);
    }

    @Test
    public void test_Nom_nurLockererNachtrag() {
        // GIVEN
        // "Rapunzel, glücklich, dich zu sehen[,]"
        final Nominalphrase np = np(
                // Adjektivphrase: "glücklich, dich zu sehen"
                AdjektivMitZuInfinitiv.GLUECKLICH.mitLexikalischerKern(SEHEN.mit(duSc())),
                // Nomen: "Rapunzel"
                NomenFlexionsspalte.RAPUNZEL,
                // Bezugsobjekt: Rapunzel
                World.RAPUNZEL);

        // WHEN
        // "Rapunzel, glücklich, dich zu sehen, schaut dich an"
        final Satz satz = ANSCHAUEN.mit(duSc()).alsSatzMitSubjekt(np);

        final Konstituentenfolge konstituentenfolge = satz.getVerbzweitsatzStandard();
        final Konstituente actual = konstituentenfolge.joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo(
                "Rapunzel, glücklich, dich zu sehen, schaut dich an");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getPhorikKandidat().getBezugsobjekt()).isSameInstanceAs(World.RAPUNZEL);
    }

    // FIXME Jetzt noch ZweiAdjPhrOhneLeerstellen testen:
    // - "rosa und grüne Elefanten"
    // - "Die Frau, zufrieden, dich zu sehen, und gespannt, ob du etwas zu berichten hast[,]..."
    // - "die junge Frau, gespannt, ob du etwas zu berichten hast[,]"
    // - "Du hilfst der jungen Frau, die gespannt ist, ob du etwas zu berichten hast[,]"

    // FIXME Bestehende Nominalphrasen durch Kombinationen aus BLAU und HIMMEL (z.B.) ersetzen

    // FIXME Im Kontext Wetter z.B. die Farbe des Himmel (BLAU) zurückgeben und dann daraus
    //  prädikative und attribute Sätze erzeugen

    // FIXME Vermeiden von "Du / ich (Personalpronomen), glücklich Rapunzel zu sehen, tust dies
    //  und das" - besser "Glücklich, Rapunzel zu sehen, tust du ..." (Umformulierung als
    //  "Adverbiale Angabe")

    // FIXME Idee: Zusammenfassungen in der Art "Rapunzel ist vom Wandern müde . Rapunzel tut
    //  dies und das" zu "Rapunzel, vom Wandern müde, tut dies und das" sowie
    //  "Glück, Rapunzel zu sehen, tust du dies und das" (neue "adverbiale Angabe" / neues
    //  Vorfeld)
}
