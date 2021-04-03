package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.adjektiv.AdjektivMitIndirektemFragesatz;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

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
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ANGEBOTE_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DINGE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ELEFANTEN_INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FRAU;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FREUDE_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.GESPRAECH_EIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ROTWEINE_INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BERICHTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.DISKUTIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

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
        final Nominalphrase np = GESPRAECH_EIN.mit(FROEHLICH);

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
        final Nominalphrase np = GESPRAECH_EIN.mit(FROEHLICH);

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
                GLUECKLICH.mitLexikalischerKern(SEHEN.mit(duSc())),
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
                GLUECKLICH.mitLexikalischerKern(SEHEN.mit(duSc())),
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

    @Test
    public void test_Reihung_nurAdjektiattribute_gleichrangig() {
        // GIVEN
        final Nominalphrase np = ROTWEINE_INDEF.mit(new ZweiAdjPhrOhneLeerstellen(LEICHT, HERB,
                true));

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
        final Nominalphrase np = ELEFANTEN_INDEF.mit(new ZweiAdjPhrOhneLeerstellen(GROSS, GRUEN,
                false));

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
                AdjektivMitIndirektemFragesatz.GESPANNT
                        .mitIndirektemFragesatz(
                                BERICHTEN.mit(Interrogativpronomen.WAS)
                                        .zuHabenPraedikat()
                                        .alsSatzMitSubjekt(duSc())),
                true));

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
                AdjektivMitIndirektemFragesatz.GESPANNT
                        .mitIndirektemFragesatz(
                                BERICHTEN.mit(Interrogativpronomen.WAS)
                                        .zuHabenPraedikat()
                                        .alsSatzMitSubjekt(duSc())),
                true));

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
                AdjektivMitIndirektemFragesatz.GESPANNT
                        .mitIndirektemFragesatz(
                                BERICHTEN.mit(Interrogativpronomen.WAS)
                                        .zuHabenPraedikat()
                                        .alsSatzMitSubjekt(duSc())),
                true));

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
                AdjektivMitIndirektemFragesatz.GESPANNT
                        .mitIndirektemFragesatz(
                                BERICHTEN.mit(Interrogativpronomen.WAS)
                                        .zuHabenPraedikat()
                                        .alsSatzMitSubjekt(duSc())),
                true));

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

    // FIXME Vermeiden von "Du / ich (Personalpronomen), glücklich Rapunzel zu sehen, tust dies
    //  und das" - besser "Glücklich, Rapunzel zu sehen, tust du ..." (vermutlich ist das eine
    //  Umformulierung als "depiktives Prädikativ", vgl. Duden 1205.) - Problem an der Sache:
    //  Funktioniert nicht bei allen Adjektivphrasen sinnvoll:
    //  "Die grüne Eidechse läuft über den Boden" - ?"Grün läuft die Eidechse über den Boden"
    //  Aber vielleicht bei praktisch allen mit w-Fragesatz oder anderweitig komplexen,
    //  für die das überhaupt nur relevant wäre? Letztlich müsste das
    //  Prädikat dem Satz mitteilen, dass "Glücklich, Rapunzel zu sehen" ins
    //  Vorfeld gerückt werden sollte (getSpeziellesVorfeldSehrErwuenscht(),
    //  getSpeziellesVorfeldAlsWeitereOption()).
    //  Der attributivAnteilLockererNachtrag muss eine eigene Konstituente sein,
    //  damit sie von getSpeziellesVorfeld...() auch zurückgegeben werden kann.
    //  Sätze mit "sein / werden" und Adjektivphrase können anscheinend kein so ein
    //  "depiktives Prädikativ" tragen? ?"Glücklich bist du hilfsbereit"

    // FIXME Idee: Neue Stelle für eine "prädikative Angabe" ("depiktives Prädikativ", vgl.
    //  Duden 1205? )  wie "Peter geht fröhlich durch
    //  den Wald". Zur Stellung vergleiche: "Heute schlägt Peter /fröhlich/ heftig auf das Holz
    //  ein",
    //  also vor der Verb-Allgemein-Adverbialen Angabe - am liebsten jedoch im Vorfeld:
    //  Fröhlich gibt Peter dem Mann das Buch". Vgl. auch: "Das Buch gibt Peter fröhlich dem Mann" -
    //  also auf jeden Fall vor dem Dativ-Objekt.
    //  Geht aber anscheinend nicht bei Prädikativum-Prädikaten: ?"Fröhlich ist Peter dumm"
    //  (aber: "Fröhlich ist Peter ein Esel" - andere Bedeutung?)

    // FIXME Idee: Zusammenfassungen in der Art "Rapunzel ist vom Wandern müde . Rapunzel tut
    //  dies und das" zu "Rapunzel, vom Wandern müde, tut dies und das" (oder
    //  "Vom Wandern müde tut Rapunzel dies und das") "Glücklich, Rapunzel zu sehen, tust du dies
    //  und das" (neue "adverbiale Angabe" - eigentlich wohl "depiktives Prädikativ" / neues
    //  Vorfeld)
}
