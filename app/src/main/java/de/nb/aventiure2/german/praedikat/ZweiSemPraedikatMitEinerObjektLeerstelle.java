package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Zwei Prädikate, deren einzige Leerstelle mit (inhaltlich)
 * demsselben Objekt gefüllt werden soll.
 * <p>
 * Man hat hier nicht ein Prädikat, sondern zwei Prädikate, jeweils mit einer Objekt-Leerstelle.
 * Wenn man ein Objekt einsetzt, erhält man einen <i>zusammengezogenen SemSatz</i>, bei dem das
 * Subjekt im zweiten Teil <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] reinigst sie").
 */
public class ZweiSemPraedikatMitEinerObjektLeerstelle
        implements SemPraedikatMitEinerObjektleerstelle {
    private final SemPraedikatMitEinerObjektleerstelle erstesPraedikat;
    private final SemPraedikatMitEinerObjektleerstelle zweitesPraedikat;

    public ZweiSemPraedikatMitEinerObjektLeerstelle(
            final SemPraedikatMitEinerObjektleerstelle erstesPraedikat,
            final SemPraedikatMitEinerObjektleerstelle zweitesPraedikat) {
        this.erstesPraedikat = erstesPraedikat;
        this.zweitesPraedikat = zweitesPraedikat;
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mit(
            final SubstantivischePhrase substantivischePhrase) {
        return mit(
                // "die goldene Kugel"
                substantivischePhrase,
                // "sie"
                substantivischePhrase.persPron());
    }

    private ZweiPraedikateOhneLeerstellenSem mit(
            final SubstantivischePhrase ersteSubstantivischePhrase,
            final SubstantivischePhrase zweiteSubstantivischePhrase) {
        return mit(ersteSubstantivischePhrase,
                NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND,
                zweiteSubstantivischePhrase);
    }

    private ZweiPraedikateOhneLeerstellenSem mit(
            final SubstantivischPhrasierbar erstesSubstPhrasierbar,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final SubstantivischPhrasierbar zweitesSubstPhrasierbar) {
        return new ZweiPraedikateOhneLeerstellenSem(
                // "die goldene Kugel"
                erstesPraedikat.mit(erstesSubstPhrasierbar),
                konnektor,
                // "sie"
                zweitesPraedikat.mit(zweitesSubstPhrasierbar));
    }
}
