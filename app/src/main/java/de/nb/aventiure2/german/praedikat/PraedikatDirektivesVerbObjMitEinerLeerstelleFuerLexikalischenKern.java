package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat eines <i>direktiven Verbs</i>, in dem noch eine
 * Leerstelle für den lexikalischen Kern besteht. Beispiele:
 * <ul>
 *     <li>"die junge Frau bitten..."
 * </ul>
 * <p>
 * Zu direktiven Verben siehe Peter Eisenberg, Der Satz, S. 357 (Kapitel 11.2)
 */
public class PraedikatDirektivesVerbObjMitEinerLeerstelleFuerLexikalischenKern
        implements Praedikat {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus mit dem das direktive Verb steht.
     */
    @NonNull
    private final Kasus kasus;

    /**
     * Das Objekt, an das die "Direktive" geht
     */
    private final SubstantivischePhrase objekt;

    public PraedikatDirektivesVerbObjMitEinerLeerstelleFuerLexikalischenKern(
            final Verb verb,
            final Kasus kasus,
            final SubstantivischePhrase objekt) {
        this.verb = verb;
        this.kasus = kasus;
        this.objekt = objekt;
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    public PraedikatOhneLeerstellen mitLexikalischemKern(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        return new PraedikatDirektivesVerbOhneLeerstellen(verb, kasus, objekt, lexikalischerKern);
    }
}
