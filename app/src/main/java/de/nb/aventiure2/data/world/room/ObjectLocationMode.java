package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.german.TransitiveVerb;

import static de.nb.aventiure2.german.TransitiveVerb.AUFHEBEN;
import static de.nb.aventiure2.german.TransitiveVerb.HERAUSKLAUBEN;
import static de.nb.aventiure2.german.TransitiveVerb.NEHMEN;

/**
 * The kind of location an object has in a room: on the floor, on a table, ...
 */
public enum ObjectLocationMode {
    BODEN("auf dem Boden", "auf den Boden", AUFHEBEN),
    // TODO Not everything fits on a table
    TISCH("auf einem Tisch", "auf einen Tisch"),
    WALDBODEN("zwischen Blättern und Gestrüpp", "auf den Waldboden",
            HERAUSKLAUBEN),
    NEBEN_DEM_BRUNNEN("neben dem Brunnnen", "neben den Brunnen",
            AUFHEBEN),
    AM_GRUNDE_DES_BRUNNENS("am Grunde des Brunnens", "auf den Grund des Brunnens");

    private final String wo;

    private final String wohin;

    /**
     * Das Verb das beschreibt, das der Benutzer etwas von diesem Ort <i>nimmt</i>
     */
    private final TransitiveVerb nehmenVerb;

    ObjectLocationMode(final String wo, final String wohin) {
        this(wo, wohin, NEHMEN);
    }

    ObjectLocationMode(final String wo, final String wohin, final TransitiveVerb nehmenVerb) {
        this.wo = wo;
        this.wohin = wohin;
        this.nehmenVerb = nehmenVerb;
    }

    public String getWo() {
        return wo;
    }

    public String getWohin() {
        return wohin;
    }

    public TransitiveVerb getNehmenVerb() {
        return nehmenVerb;
    }
}
