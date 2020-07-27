package de.nb.aventiure2.data.world.syscomp.storingplace;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;
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
    EINE_TASCHE("in einer Tasche", "in eine Tasche"),
    HAENDE("in den Händen", "in die Hände"),
    VOR_DEM_SCHLOSS("vor dem Schloss", "vor das Schloss"),
    BODEN_VOR_DEM_SCHLOSS("auf dem Boden", "auf den Boden", VOR_DEM_SCHLOSS),
    AM_GRUNDE_DES_BRUNNENS("am Grunde des Brunnens", "auf den Grund des Brunnens"),
    // TODO Not everything fits on a table
    EIN_TISCH("auf einem Tisch", "auf einen Tisch"),
    NEBEN_SC_AUF_BANK("neben dir auf der Bank", "neben dich auf die Bank"),
    TISCH("auf dem Tisch", "auf den Tisch"),
    SCHATTEN_DER_BAEUME("im Schatten der Bäume", "in den Schatten der Bäume"),
    NEBEN_DEM_BRUNNEN("neben dem Brunnnen", "neben den Brunnen",
            AUFHEBEN),
    NEBEN_DIR_IM_BETT("neben dir im Bett", "neben dich in das Bett",
            AN_SICH_NEHMEN),
    HOLZTISCH("auf dem hölzernen Tisch", "auf den Holztisch"),
    VOR_DER_HUETTE("vor der Hütte", "vor die Hütte"),
    ERDBODEN_VOR_DER_HUETTE("auf dem Erdboden vor der Hütte",
            "auf den Erdboden vor der Hütte",
            AUFHEBEN,
            VOR_DER_HUETTE),
    WALD("im Wald", "in den Wald"),
    MATSCHIGER_WALDBODEN("auf dem matschigen Waldboden",
            "auf den matschigen Waldboden",
            new ZweiVerbenSubjObj(AUFHEBEN, POLIEREN), WALD),
    // STORY    WALDBODEN("zwischen Blättern und Gestrüpp", "auf den Waldboden",
    //         HERAUSKLAUBEN),
    WEG("auf dem Weg", "auf den Weg"),
    VOR_TURM("vor dem Turm", "vor den Turm"),
    STEINIGER_GRUND_VOR_TURM("auf dem steinigen Grund vor dem Turm",
            "auf den steinigen Grund vor dem Turm", VOR_TURM),
    TURMZIMMER("im Turmzimmer", "in das Turmzimmer"),
    TEPPICHBODEN_OBEN_IM_TURM("auf den Teppich", "auf dem Teppich", TURMZIMMER),
    UNTER_DEM_BAUM("unter dem Baum", "unter den Baum", AUFHEBEN);

    private final String wo;

    private final String wohin;

    /**
     * Wenn das Game Object belebt und eher groß ist,
     * soll (in manchen Fällen) eine Beschreibung gewählt werden, die weniger
     * den Fokus auf die Beschreibung legt.
     * Z.B. nicht "Du triffst die Zauberin auf dem
     * matschigen Waldboden", sondern "Du triffst die Zauberin im Wald"
     */
    @Nullable
    private final StoringPlaceType forBelebtUndEherGroß;

    /**
     * Das Verb das beschreibt, das der Benutzer etwas von diesem Ort <i>mitnimmt</i>
     */
    private final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat;

    StoringPlaceType(final String wo, final String wohin) {
        this(wo, wohin, NEHMEN);
    }

    StoringPlaceType(final String wo, final String wohin,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat) {
        this(wo, wohin, mitnehmenPraedikat, null);
    }

    StoringPlaceType(final String wo, final String wohin,
                     @Nullable final StoringPlaceType forBelebtUndEherGroß) {
        this(wo, wohin, NEHMEN, forBelebtUndEherGroß);
    }

    StoringPlaceType(final String wo, final String wohin,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat,
                     @Nullable final StoringPlaceType forBelebtUndEherGroß) {
        this.wo = wo;
        this.wohin = wohin;
        this.mitnehmenPraedikat = mitnehmenPraedikat;
        this.forBelebtUndEherGroß = forBelebtUndEherGroß;
    }

    public AdverbialeAngabe getWoAdvAngabe(final boolean forBelebtUndEherGroß) {
        return new AdverbialeAngabe(getWo(forBelebtUndEherGroß));
    }

    public String getWo(final boolean forBelebtUndEherGroß) {
        if (forBelebtUndEherGroß && this.forBelebtUndEherGroß != null) {
            return this.forBelebtUndEherGroß.getWo(forBelebtUndEherGroß);
        }

        return wo;
    }

    public AdverbialeAngabe getWohinAdvAngabe(final boolean forBelebtUndEherGroß) {
        return new AdverbialeAngabe(getWohin(forBelebtUndEherGroß));
    }

    public String getWohin(final boolean forBelebtUndEherGroß) {
        if (forBelebtUndEherGroß && this.forBelebtUndEherGroß != null) {
            return this.forBelebtUndEherGroß.getWohin(forBelebtUndEherGroß);
        }

        return wohin;
    }

    public PraedikatMitEinerObjektleerstelle getMitnehmenPraedikat() {
        return mitnehmenPraedikat;
    }
}
