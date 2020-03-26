package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;

import static de.nb.aventiure2.german.praedikat.ReflPraepositionalkasusVerbAkkObj.AN_SICH_NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFHEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HERAUSKLAUBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

/**
 * The kind of location an object has in a room: on the floor, on a table, ...
 */
public enum ObjectLocationMode {
    BODEN("auf dem Boden", "auf den Boden", AUFHEBEN),
    // TODO Not everything fits on a table
    EIN_TISCH("auf einem Tisch", "auf einen Tisch"),
    WALDBODEN("zwischen Blättern und Gestrüpp", "auf den Waldboden",
            HERAUSKLAUBEN),
    VOR_DER_HUETTE("auf dem Erdboden vor der Hütte", "auf den Erdboden vor der Hütte",
            AUFHEBEN),
    // TODO Not everything fits on a table
    HOLZTISCH("auf dem hölzernen Tisch", "auf den Holztisch"),
    NEBEN_DIR_IM_BETT("neben dir im Bett", "neben dich in das Bett",
            AN_SICH_NEHMEN),
    GRAS_NEBEN_DEM_BRUNNEN("neben dem Brunnnen", "neben den Brunnen", AUFHEBEN),
    AM_GRUNDE_DES_BRUNNENS("am Grunde des Brunnens", "auf den Grund des Brunnens"),
    UNTER_DEM_BAUM("unter dem Baum", "unter den Baum", AUFHEBEN);

    private final String wo;

    private final String wohin;

    /**
     * Das Verb das beschreibt, das der Benutzer etwas von diesem Ort <i>nimmt</i>
     */
    private final PraedikatMitEinerObjektleerstelle nehmenPraedikat;

    ObjectLocationMode(final String wo, final String wohin) {
        this(wo, wohin, NEHMEN);
    }

    ObjectLocationMode(final String wo, final String wohin,
                       final PraedikatMitEinerObjektleerstelle nehmenPraedikat) {
        this.wo = wo;
        this.wohin = wohin;
        this.nehmenPraedikat = nehmenPraedikat;
    }

    public String getWo() {
        return wo;
    }

    public String getWohin() {
        return wohin;
    }

    public PraedikatMitEinerObjektleerstelle getNehmenPraedikat() {
        return nehmenPraedikat;
    }
}
