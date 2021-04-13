package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

/**
 * Eine Ergänzung des Adjektivs wie "sehr", "äußerst", "kaum" etc. Sie kann in
 * der Regel <i>nicht</i> abgetrennt (aus der Adjektivphrase herausgelöst) werden.
 */
class GraduativeAngabe {
    private final String text;

    GraduativeAngabe(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @NonNull
    @Override
    public String toString() {
        // So muss es bleiben - wird so ausgegeben!
        return text;
    }
}
