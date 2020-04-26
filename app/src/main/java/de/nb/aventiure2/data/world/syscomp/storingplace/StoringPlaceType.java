package de.nb.aventiure2.data.world.syscomp.storingplace;

import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.ZweiVerbenSubjObj;

import static de.nb.aventiure2.german.praedikat.ReflPraepositionalkasusVerbAkkObj.AN_SICH_NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFHEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.POLIEREN;

/**
 * Where an object is stored specifically
 */
public enum StoringPlaceType {
    IN_EINER_TASCHE("in einer Tasche", "in eine Tasche"),
    AM_GRUNDE_DES_BRUNNENS("am Grunde des Brunnens", "auf den Grund des Brunnens"),
    BODEN("auf dem Boden", "auf den Boden", AUFHEBEN),
    // TODO Not everything fits on a table
    EIN_TISCH("auf einem Tisch", "auf einen Tisch"),
    GRAS_NEBEN_DEM_BRUNNEN("neben dem Brunnnen", "neben den Brunnen",
            AUFHEBEN),
    NEBEN_DIR_IM_BETT("neben dir im Bett", "neben dich in das Bett",
            AN_SICH_NEHMEN),
    // TODO Not everything fits on a table
    HOLZTISCH("auf dem hölzernen Tisch", "auf den Holztisch"),
    VOR_DER_HUETTE("auf dem Erdboden vor der Hütte", "auf den Erdboden vor der Hütte",
            AUFHEBEN),
    MATSCHIGER_WALDBODENN("auf dem matschigen Waldboden",
            "auf den matschigen Waldboden",
            new ZweiVerbenSubjObj(AUFHEBEN, POLIEREN)),
    // STORY    WALDBODEN("zwischen Blättern und Gestrüpp", "auf den Waldboden",
    //         HERAUSKLAUBEN),
    WALDWEG("auf dem Weg", "auf den Weg"),
    UNTER_DEM_BAUM("unter dem Baum", "unter den Baum", AUFHEBEN);

    private final String wo;

    private final String wohin;

    /**
     * Das Verb das beschreibt, das der Benutzer etwas von diesem Ort <i>nimmt</i>
     */
    private final PraedikatMitEinerObjektleerstelle nehmenPraedikat;

    StoringPlaceType(final String wo, final String wohin) {
        this(wo, wohin, NEHMEN);
    }

    StoringPlaceType(final String wo, final String wohin,
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
