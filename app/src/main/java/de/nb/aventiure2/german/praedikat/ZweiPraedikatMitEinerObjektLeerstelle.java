package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Zwei Prädikate, deren einzige Leerstelle mit (inhaltlich)
 * demsselben Objekt gefüllt werden soll.
 * <p>
 * Man hat hier nicht ein Prädikat, sondern zwei Prädikate, jeweils mit einer Objekt-Leerstelle.
 * Wenn man ein Objekt einsetzt, erhält man einen <i>zusammengezogenen Satz</i>, bei dem das
 * Subjekt im zweiten Teil <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] reinigst sie").
 */
public class ZweiPraedikatMitEinerObjektLeerstelle implements PraedikatMitEinerObjektleerstelle {
    private final PraedikatMitEinerObjektleerstelle erstesPraedikat;
    private final PraedikatMitEinerObjektleerstelle zweitesPraedikat;

    public ZweiPraedikatMitEinerObjektLeerstelle(
            final PraedikatMitEinerObjektleerstelle erstesPraedikat,
            final PraedikatMitEinerObjektleerstelle zweitesPraedikat) {
        this.erstesPraedikat = erstesPraedikat;
        this.zweitesPraedikat = zweitesPraedikat;
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mit(
            final SubstantivischePhrase substantivischePhrase) {
        return mit(
                // "die goldene Kugel"
                substantivischePhrase,
                // "sie"
                substantivischePhrase.persPron());
    }

    private ZweiPraedikateOhneLeerstellen mit(
            final SubstantivischePhrase ersteSubstantivischePhrase,
            final SubstantivischePhrase zweiteSubstantivischePhrase) {
        return mit(ersteSubstantivischePhrase,
                NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND,
                zweiteSubstantivischePhrase);
    }

    private ZweiPraedikateOhneLeerstellen mit(
            final SubstantivischePhrase ersteSubstantivischePhrase,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final SubstantivischePhrase zweiteSubstantivischePhrase) {
        return new ZweiPraedikateOhneLeerstellen(
                // "die goldene Kugel"
                erstesPraedikat.mit(ersteSubstantivischePhrase),
                konnektor,
                // "sie"
                zweitesPraedikat.mit(zweiteSubstantivischePhrase));
    }
}
