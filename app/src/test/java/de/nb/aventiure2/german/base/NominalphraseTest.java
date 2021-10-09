package de.nb.aventiure2.german.base;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivMitZuInfinitiv.GLUECKLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BESONDERS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUNKEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EITEL_NICHT_FLEKTIERBAR;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GROSS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GRUEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HERB;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.JUNG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LEICHT;
import static de.nb.aventiure2.german.base.ArtikelFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.ArtikelFlexionsspalte.Typ.NEG_INDEF;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ANGEBOTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DINGE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ELEFANTEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FRAU;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FREUDE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.GESPRAECH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.RAPUNZEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ROTWEINE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BERICHTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.DISKUTIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.adjektiv.AdjektivMitIndirektemFragesatz;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

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
        final Nominalphrase np = np(INDEF, FROEHLICH, GESPRAECH);

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
        final Nominalphrase np = np(INDEF, FROEHLICH, GESPRAECH);

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
        final Nominalphrase np = np(INDEF, DUNKEL, ANGEBOTE);

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
    public void test_nurAdjektiattribute_Neg_Indef_Nom_Sg() {
        // GIVEN
        final Nominalphrase np = np(NEG_INDEF, FROEHLICH, GESPRAECH);

        // WHEN
        final Konstituente actual = np.imK(NOM).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("kein fröhliches Gespräch");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(N);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_Neg_Indef_Dat_Sg() {
        // GIVEN
        final Nominalphrase np = np(NEG_INDEF, FROEHLICH, GESPRAECH);

        // WHEN
        final Konstituente actual = np.imK(DAT).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("keinem fröhlichen Gespräch");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(N);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektiattribute_Neg_Indef_Pl() {
        // GIVEN
        final Nominalphrase np = np(NEG_INDEF, DUNKEL, ANGEBOTE);

        // WHEN
        final Konstituente actual = np.imK(AKK).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("keine dunklen Angebote");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(PL_MFN);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_nurAdjektivattribute_ohne_Artikel() {
        // GIVEN
        final Nominalphrase np = np(null, BESONDERS, FREUDE);

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
        final Nominalphrase np = np(null, EITEL_NICHT_FLEKTIERBAR, FREUDE);

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
                GLUECKLICH.mitLexikalischerKern(SEHEN.mit(duSc())),
                // Nomen: "Rapunzel"
                RAPUNZEL,
                // Bezugsobjekt: Rapunzel
                World.RAPUNZEL);

        // WHEN
        // "Du diskutierst mit Rapunzel, die glücklich ist, dich zu sehen[,]"
        final Satz satz = DISKUTIEREN.mit(np).alsSatzMitSubjekt(duSc());

        final Konstituentenfolge konstituentenfolge = satz.getVerbzweitsatzStandard();
        final Konstituente actual = konstituentenfolge.joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isTrue();
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
                GLUECKLICH.mitLexikalischerKern(SEHEN.mit(duSc())),
                // Nomen: "Rapunzel"
                RAPUNZEL,
                // Bezugsobjekt: Rapunzel
                World.RAPUNZEL);

        // WHEN
        // "Rapunzel, glücklich, dich zu sehen, schaut dich an"
        final Satz satz = ANSCHAUEN.mit(duSc()).alsSatzMitSubjekt(np);

        final Konstituentenfolge konstituentenfolge = satz.getVerbzweitsatzStandard();
        final Konstituente actual = konstituentenfolge.joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isTrue();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo(
                "Rapunzel, glücklich, dich zu sehen, schaut dich an");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getPhorikKandidat().getBezugsobjekt()).isSameInstanceAs(World.RAPUNZEL);
    }

    @Test
    public void test_Reihung_nurAdjektiattribute_gleichrangig() {
        // GIVEN
        final Nominalphrase np =
                np(INDEF, new ZweiAdjPhrOhneLeerstellen(LEICHT, true, HERB
                        ),
                        ROTWEINE);

        // WHEN
        final Konstituente actual = np.imK(AKK).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("leichte, herbe Rotweine");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(PL_MFN);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_Reihung_nurAdjektivattribute_nichtGleichrangig() {
        // GIVEN
        final Nominalphrase np =
                np(INDEF, new ZweiAdjPhrOhneLeerstellen(GROSS, false, GRUEN
                        ),
                        ELEFANTEN);

        // WHEN
        final Konstituente actual = np.imK(AKK).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("große grüne Elefanten");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(PL_MFN);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_Reihung_nurLockereNachtraege() {
        // GIVEN
        final Nominalphrase np = FRAU.mit(new ZweiAdjPhrOhneLeerstellen(
                GLUECKLICH.mitLexikalischerKern(SEHEN.mit(World.duSc())),
                true, AdjektivMitIndirektemFragesatz.GESPANNT
                .mitIndirektemFragesatz(
                        BERICHTEN.mit(Interrogativpronomen.WAS)
                                .zuHabenPraedikat()
                                .alsSatzMitSubjekt(duSc()))
        ));

        // WHEN
        final Konstituente actual = np.imK(NOM).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo(
                "die Frau, glücklich, dich zu sehen, und gespannt, was du zu berichten hast");
        assertThat(actual.kommaStehtAus()).isTrue();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(F);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_Reihung_Adjektivattribut_und_lockererNachtrag() {
        // GIVEN
        final Nominalphrase np = FRAU.mit(new ZweiAdjPhrOhneLeerstellen(
                JUNG,
                true, AdjektivMitIndirektemFragesatz.GESPANNT
                .mitIndirektemFragesatz(
                        BERICHTEN.mit(Interrogativpronomen.WAS)
                                .zuHabenPraedikat()
                                .alsSatzMitSubjekt(duSc()))
        ));

        // WHEN
        final Konstituente actual = np.imK(NOM).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText())
                .isEqualTo("die junge Frau, gespannt, was du zu berichten hast");
        assertThat(actual.kommaStehtAus()).isTrue();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(F);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_Reihung_Adjektivattribut_und_Relativsatz() {
        // GIVEN
        final Nominalphrase np = FRAU.mit(new ZweiAdjPhrOhneLeerstellen(
                JUNG,
                true, AdjektivMitIndirektemFragesatz.GESPANNT
                .mitIndirektemFragesatz(
                        BERICHTEN.mit(Interrogativpronomen.WAS)
                                .zuHabenPraedikat()
                                .alsSatzMitSubjekt(duSc()))
        ));

        // WHEN
        final Konstituente actual = np.imK(DAT).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText())
                .isEqualTo("der jungen Frau, die gespannt ist, was du zu berichten hast");
        assertThat(actual.kommaStehtAus()).isTrue();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(F);
        assertThat(actual.getPhorikKandidat()).isNull();
    }

    @Test
    public void test_Reihung_nurRelativsaetze() {
        // GIVEN
        final Nominalphrase np = FRAU.mit(new ZweiAdjPhrOhneLeerstellen(
                GLUECKLICH.mitLexikalischerKern(SEHEN.mit(World.duSc())),
                true, AdjektivMitIndirektemFragesatz.GESPANNT
                .mitIndirektemFragesatz(
                        BERICHTEN.mit(Interrogativpronomen.WAS)
                                .zuHabenPraedikat()
                                .alsSatzMitSubjekt(duSc()))
        ));

        // WHEN
        final Konstituente actual = np.imK(DAT).joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isFalse();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isIn(ImmutableList.of(
                "der Frau, die glücklich ist, dich zu sehen, und gespannt, was du zu berichten "
                        + "hast",
                "der Frau, die glücklich, dich zu sehen, ist, und gespannt, was du zu berichten "
                        + "hast"));
        assertThat(actual.kommaStehtAus()).isTrue();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
        assertThat(actual.getKannAlsBezugsobjektVerstandenWerdenFuer()).isSameInstanceAs(F);
        assertThat(actual.getPhorikKandidat()).isNull();
    }
}
