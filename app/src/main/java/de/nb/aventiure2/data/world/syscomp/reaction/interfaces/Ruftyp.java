package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.german.base.WoertlicheRede;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

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
    private final PraedikatOhneLeerstellen name;

    Ruftyp(final String woertlicheRedeText) {
        this(new WoertlicheRede(woertlicheRedeText));
    }

    Ruftyp(final WoertlicheRede woertlicheRede) {
        this(RUFEN.mitWoertlicherRede(woertlicheRede));
    }

    Ruftyp(final PraedikatOhneLeerstellen name) {
        this.name = name;
    }

    public PraedikatOhneLeerstellen getName() {
        return name;
    }
}
