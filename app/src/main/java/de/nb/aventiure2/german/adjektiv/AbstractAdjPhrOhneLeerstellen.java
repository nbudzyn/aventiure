package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Eine Adjektivphrase, in der alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"glücklich"
 *     <li>"glücklich, dich zu sehen!"
 *     <li>"sehr glücklich, dich zu sehen!"
 *     <li>"immer wieder glücklich, dich zu sehen!"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus Langeweile") können immer noch eingefügt werden.
 */
abstract class AbstractAdjPhrOhneLeerstellen implements AdjPhrOhneLeerstellen {
    // Zu diskontinuierlichen Adjektivphrasen vgl.
    // https://ids-pub.bsz-bw.de/frontdoor/deliver/index/docId/3132/file/Engel_Rytel-Kuc
    // -Diskontinuierliche_Phrasen_im_Deutschen_und_im_Polnischen-1987.pdf

    /**
     * Eine adverbiale Angabe, die sich auf die gesamte Adjektivphrase bezieht, z.B.
     * "immer wieder" oder "noch".
     */
    @Nullable
    private final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz;

    /**
     * Etwas wie "sehr", "äußerst", "ein bisschen".
     * <p>
     * Steht in aller Regel direkt vor dem Adjektiv.
     * <p>
     * Entspricht wohl den <i>Gradpartikeln</i> der Duden-Grammatik
     * sowohl grob den
     * <i>ereignismodifizierende Qualitativsupplemente im engeren Sinne</i>
     * und den <i>Identitätsmodifikatoren</i> wie z.B. den
     * <i>Intesitätspartikeln</i> (auch <i>Intensivpartikel</i>).
     * <p>
     * Gradparikel sind bei Verben nur in Einzelfällen möglich - z.B.
     * "Sie freut sich sehr" oder "Sie freut sich sehr auf das Essen".
     */
    @Nullable
    private final GraduativeAngabe graduativeAngabe;

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AbstractAdjPhrOhneLeerstellen(final Adjektiv adjektiv) {
        this(null, null, adjektiv);
    }

    AbstractAdjPhrOhneLeerstellen(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv) {
        this.advAngabeSkopusSatz = advAngabeSkopusSatz;
        this.adjektiv = adjektiv;
        this.graduativeAngabe = graduativeAngabe;
    }

    @Nullable
    Konstituente getAdvAngabeSkopusSatzDescription(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (advAngabeSkopusSatz == null) {
            return null;
        }

        return advAngabeSkopusSatz
                .getDescription(personSubjekt, numerusSubjekt); // "immer noch"
    }

    @Nullable
    public IAdvAngabeOderInterrogativSkopusSatz getAdvAngabeSkopusSatz() {
        return advAngabeSkopusSatz;
    }

    @Nullable
    GraduativeAngabe getGraduativeAngabe() {
        return graduativeAngabe;
    }

    @NonNull
    Adjektiv getAdjektiv() {
        return adjektiv;
    }

    @Override
    public boolean hasVorangestellteAngaben() {
        return graduativeAngabe != null || advAngabeSkopusSatz != null;
    }
}
