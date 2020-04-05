package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;

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
    public PraedikatOhneLeerstellen mitObj(final DescribableAsDeklinierbarePhrase describable) {
        // "die Kugel"
        final DeklinierbarePhrase erstesObj = describable.getDescription(true);

        return mitObj(
                // "die Kugel"
                erstesObj,
                // "sie"
                erstesObj.persPron());
    }

    public PraedikatOhneLeerstellen mitObj(final DescribableAsDeklinierbarePhrase erstesDescribable,
                                           final DescribableAsDeklinierbarePhrase zweitesDescribable) {
        return new ZweiPraedikateSubjObjOhneLeerstellen(
                // "die Kugel"
                erstesVerb.mitObj(erstesDescribable),
                // "sie"
                zweitesVerb.mitObj(zweitesDescribable));
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        // Etwas vemeiden wie "Du hebst die Kugel auf und polierst sie und..."
        return false;
    }
}
