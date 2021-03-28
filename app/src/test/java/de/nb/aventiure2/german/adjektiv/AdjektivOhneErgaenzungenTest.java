package de.nb.aventiure2.german.adjektiv;

import org.junit.Test;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Nominalphrase;
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
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.DISKUTIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

public class AdjektivOhneErgaenzungenTest {
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
    public void test_nurRelativsatz() {
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
        assertThat(actual.getPhorikKandidat()).isSameInstanceAs(World.RAPUNZEL);
    }

    //  FIXME "Du sprichst mit der Frau, die gespannt ist, was du zu berichten hast"

    // FIXME "die Frau, gespannt, ob du etwas zu berichten hast[,]"
    // FIXME "Du hilfst der Frau des Herzogs, die mit dem Tag zufrieden ist"
    // FIXME "die junge Frau des Herzogs, gespannt, ob du etwas zu berichten hast[,]"
    // FIXME "(die Frau), zufrieden, dich zu sehen, und gespannt, ob du etwas zu berichten hast[,]"

    // FIXME "rosa und grüne Elefanten"
    // FIXME "(die )junge (Frau des Herzogs, die dich überrascht hat, gespannt, ob du
    //  etwas zu berichten hast[,]"
}
