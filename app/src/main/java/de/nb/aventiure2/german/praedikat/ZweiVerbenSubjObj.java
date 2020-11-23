package de.nb.aventiure2.german.praedikat;

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
    public PraedikatOhneLeerstellen mitObj(
            final SubstantivischePhrase substantivischePhrase) {
        return mitObj(
                // "die goldene Kugel"
                substantivischePhrase,
                // "sie"
                substantivischePhrase.persPron());
    }

    public PraedikatOhneLeerstellen mitObj(
            final SubstantivischePhrase ersteSubstantivischePhrase,
            final SubstantivischePhrase zweiteSubstantivischePhrase) {
        return new ZweiPraedikateOhneLeerstellen(
                // "die goldene Kugel"
                erstesVerb.mitObj(ersteSubstantivischePhrase),
                // "sie"
                zweitesVerb.mitObj(zweiteSubstantivischePhrase));
    }
}
