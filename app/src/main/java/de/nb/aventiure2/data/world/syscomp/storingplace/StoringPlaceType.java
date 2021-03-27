package de.nb.aventiure2.data.world.syscomp.storingplace;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.ZweiVerbenSubjObj;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUS;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.AN_SICH_NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFHEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HERVORHOLEN;
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
    EIN_TISCH("auf einem Tisch", "auf einen Tisch"),
    NEBEN_SC_AUF_EINER_BANK("neben dir auf einer Bank", "neben dich auf eine Bank"),
    TISCH("auf dem Tisch", "auf den Tisch"),
    STAMM_EINES_BAUMS("am Stamm eines Baumes", "an den Stamm eines Baumes"),
    NEBEN_DEM_BRUNNEN("neben dem Brunnnen", "neben den Brunnen",
            AUFHEBEN),
    BETTGESTELL("im Bettgestell", "ins Bettgestell"),
    ECKE_IM_BETTGESTELL("in einer Ecke des Bettgestells",
            "in eine unauffällige Ecke des Bettgestells",
            AN_SICH_NEHMEN, BETTGESTELL),
    HOLZTISCH("auf dem hölzernen Tisch", "auf den Holztisch"),
    VOR_DER_HUETTE("vor der Hütte", "vor die Hütte"),
    ERDBODEN_VOR_DER_HUETTE("auf dem Erdboden vor der Hütte",
            "auf den Erdboden vor der Hütte",
            AUFHEBEN,
            VOR_DER_HUETTE),
    IM_GEAEST("im Geäst", "ins Geäst"),
    ASTGABEL("in einer Astgabel",
            "in eine Astgabel",
            HEBEN.mitAdvAngabe(
                    new AdvAngabeSkopusVerbWohinWoher(
                            AUS.mit(NomenFlexionsspalte.ASTGABEL))),
            IM_GEAEST),
    WALD("im Wald", "in den Wald"),
    MATSCHIGER_WALDBODEN("auf dem matschigen Waldboden",
            "auf den matschigen Waldboden",
            new ZweiVerbenSubjObj(AUFHEBEN, POLIEREN), WALD),
    // IDEA    WALDBODEN("zwischen Blättern und Gestrüpp", "auf den Waldboden",
    //         HERAUSKLAUBEN),
    WEG("auf dem Weg", "auf den Weg"),
    VOR_TURM("vor dem Turm", "vor den Turm"),
    STEINIGER_GRUND_VOR_TURM("auf dem steinigen Grund vor dem Turm",
            "auf den steinigen Grund vor dem Turm", VOR_TURM),
    TURMZIMMER("im Turmzimmer", "in das Turmzimmer"),
    HOLZDIELEN_OBEN_IM_TURM("auf den Holzdielen", "auf die Holzdielen", TURMZIMMER),
    UNTER_DEM_BAUM("unter dem Baum", "unter den Baum", AUFHEBEN),
    UNTER_DEM_BETT("unter dem Bett", "unter das Bett", HERVORHOLEN);

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
    private final StoringPlaceType forBelebtUndEherGross;

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
                     @Nullable final StoringPlaceType forBelebtUndEherGross) {
        this(wo, wohin, NEHMEN, forBelebtUndEherGross);
    }

    StoringPlaceType(final String wo, final String wohin,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat,
                     @Nullable final StoringPlaceType forBelebtUndEherGross) {
        this.wo = wo;
        this.wohin = wohin;
        this.mitnehmenPraedikat = mitnehmenPraedikat;
        this.forBelebtUndEherGross = forBelebtUndEherGross;
    }

    public String getWo(final boolean forBelebtUndEherGross) {
        if (forBelebtUndEherGross && this.forBelebtUndEherGross != null) {
            return this.forBelebtUndEherGross.getWo(true);
        }

        return wo;
    }

    public AdvAngabeSkopusVerbWohinWoher getWohinAdvAngabe(
            final boolean forBelebtUndEherGross) {
        // "Du setzt den Frosch auf den Tisch"
        return new AdvAngabeSkopusVerbWohinWoher(getWohin(forBelebtUndEherGross));
    }

    public String getWohin(final boolean forBelebtUndEherGross) {
        if (forBelebtUndEherGross && this.forBelebtUndEherGross != null) {
            return this.forBelebtUndEherGross.getWohin(true);
        }

        return wohin;
    }

    public PraedikatMitEinerObjektleerstelle getMitnehmenPraedikat() {
        return mitnehmenPraedikat;
    }
}
