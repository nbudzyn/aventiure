package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.german.base.WoertlicheRede;
import de.nb.aventiure2.german.praedikat.PraedikatWoertlicheRedeOhneLeerstellen;

import static de.nb.aventiure2.german.praedikat.VerbSubjWoertlicheRede.RUFEN;

/**
 * Was jemand gerufen hat.
 */
public enum Ruftyp {
    WARTE_NIMM_MICH_MIT("Warte, nimm mich mit!"),
    LASS_DEIN_HAAR_HERUNTER("Lass dein Haar herunter");
    /**
     * Prädikat für den Namen des Rufts, z.B. ("Lass dein Haar herunter" rufen).
     */
    private final PraedikatWoertlicheRedeOhneLeerstellen name;

    Ruftyp(final String woertlicheRedeText) {
        this(new WoertlicheRede(woertlicheRedeText));
    }

    Ruftyp(final WoertlicheRede woertlicheRede) {
        this(RUFEN.mitWoertlicheRede(woertlicheRede));
    }

    Ruftyp(final PraedikatWoertlicheRedeOhneLeerstellen name) {
        this.name = name;
    }

    public PraedikatWoertlicheRedeOhneLeerstellen getName() {
        return name;
    }
}
