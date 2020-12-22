package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

/**
 * Eine Adjektivphrase, in der alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"glücklich"
 *     <li>"glücklich, dich zu sehen!"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus Langeweile") können immer noch eingefügt werden.
 */
abstract class AbstractAdjPhrOhneLeerstellen implements AdjPhrOhneLeerstellen {
    // Zu diskontinuierlichen Adjektivphrasen vgl.
    // https://ids-pub.bsz-bw.de/frontdoor/deliver/index/docId/3132/file/Engel_Rytel-Kuc-Diskontinuierliche_Phrasen_im_Deutschen_und_im_Polnischen-1987.pdf

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    @Nullable
    private final GraduativeAngabe graduativeAngabe;

    AbstractAdjPhrOhneLeerstellen(final Adjektiv adjektiv) {
        this(null, adjektiv);
    }

    AbstractAdjPhrOhneLeerstellen(
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
        this.graduativeAngabe = graduativeAngabe;
    }

    public abstract AbstractAdjPhrOhneLeerstellen mitGraduativerAngabe(
            @Nullable GraduativeAngabe graduativeAngabe);

    @NonNull
    Adjektiv getAdjektiv() {
        return adjektiv;
    }
}
