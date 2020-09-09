package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat mit wörtlicher Rede, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"„Lass dein Haar herunter“ rufen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class PraedikatMitWoertlicherRedeOhneLeerstellen
        extends AbstractPraedikatOhneLeerstellen {
    /**
     * Die wörtliche Rede
     */
    @NonNull
    private final WoertlicheRede woertlicheRede;

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final String woertlicheRedeText) {
        this(verb, new WoertlicheRede(woertlicheRedeText));
    }

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede) {
        super(verb);
        this.woertlicheRede = woertlicheRede;
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return false;
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(modalpartikeln); // mal eben
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("rufst: ...")
     */
    @Override
    public String getNachfeld() {
        return joinToNull(
                ":",
                woertlicheRede.amSatzende()); // "„Kommt alle her.“"
    }
}
