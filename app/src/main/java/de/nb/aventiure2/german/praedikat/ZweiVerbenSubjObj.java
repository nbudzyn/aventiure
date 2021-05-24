package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Zwei Verben, die mit demselben Subjekt und (inhaltlich)
 * demselben (Präpositional-)objekt stehen.
 * <p>
 * Man hat hier nicht ein Prädikat, sondern zwei Prädikate, jeweils mit einer Objekt-Leerstelle.
 * Wenn man ein Objekt einsetzt, erhält man einen <i>zusammengezogenen Satz</i>, bei dem das
 * Subjekt im zweiten Teil <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] reinigst sie").
 */
public class ZweiVerbenSubjObj implements PraedikatMitEinerObjektleerstelle {
    private final VerbSubjObj erstesVerb;
    private final VerbSubjObj zweitesVerb;

    public ZweiVerbenSubjObj(final VerbSubjObj erstesVerb,
                             final VerbSubjObj zweitesVerb) {
        this.erstesVerb = erstesVerb;
        this.zweitesVerb = zweitesVerb;
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
                erstesVerb.mit(ersteSubstantivischePhrase),
                konnektor,
                // "sie"
                zweitesVerb.mit(zweiteSubstantivischePhrase));
    }
}
