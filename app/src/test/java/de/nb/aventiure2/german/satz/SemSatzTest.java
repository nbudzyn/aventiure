package de.nb.aventiure2.german.satz;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.praedikat.VerbSubjObjWoertlicheRede.ENTGEGENBLAFFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObjWoertlicheRede.ENTGEGENRUFEN;

import org.junit.Test;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Personalpronomen;

public class SemSatzTest {
    @Test
    public void testMehrteiligeWoertlicheImNachfeld() {
        // GIVEN
        final SemSatz semSatz = ENTGEGENBLAFFEN
                .mitObjekt(Personalpronomen.get(P2, M))
                .mitWoertlicheRede("Vergiss es! Nie wieder!")
                .alsSatzMitSubjekt(Personalpronomen.get(P3, F));

        // WHEN
        final Konstituente actual = semSatz.getVerbzweitsatzStandard().joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isTrue();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText())
                .isEqualTo("sie blafft dir entgegen: „Vergiss es! Nie wieder!");
        assertThat(actual.kommaStehtAus()).isTrue(); // Wenn der SemSatz nach der wörtlichen Rede
        // weiter geht, dann erst nach einem Komma
        assertThat(actual.woertlicheRedeNochOffen()).isTrue();
    }

    @Test
    public void testWoertlicheRedeImVorfeldMitObjekt() {
        // GIVEN
        final SemSatz semSatz = ENTGEGENRUFEN
                .mitObjekt(Personalpronomen.get(P2, M))
                .mitWoertlicheRede("Du schon wieder!")
                .alsSatzMitSubjekt(Personalpronomen.get(P3, F));

        // WHEN
        final Konstituente actual = semSatz.getVerbzweitsatzStandard().joinToSingleKonstituente();

        // THEN
        assertThat(actual.vorkommaNoetig()).isTrue();
        assertThat(actual.vordoppelpunktNoetig()).isFalse();
        assertThat(actual.getText()).isEqualTo("„Du schon wieder!“, ruft sie dir entgegen");
        assertThat(actual.kommaStehtAus()).isFalse();
        assertThat(actual.woertlicheRedeNochOffen()).isFalse();
    }
}
