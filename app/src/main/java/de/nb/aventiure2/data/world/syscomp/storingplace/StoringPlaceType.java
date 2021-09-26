package de.nb.aventiure2.data.world.syscomp.storingplace;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUS;
import static de.nb.aventiure2.german.praedikat.IntentionalesReflVerb.SICH_BEMUEHEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.AN_SICH_NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ABTROCKNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFHEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFKLAUBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFSAMMELN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.EINSAMMELN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HERVORHOLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HERVORSUCHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.POLIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SAMMELN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZUSAMMENSAMMELN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZUSAMMENSUCHEN;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.ZweiPraedikatMitEinerObjektLeerstelle;

/**
 * Where an object is stored specifically
 */
public enum StoringPlaceType {
    EINE_TASCHE("in einer Tasche", "in eine Tasche"),
    HAENDE("in den Händen", "in die Hände"),
    VOR_DEM_SCHLOSS("vor dem Schloss", "vor das Schloss", AUFHEBEN,
            AUFKLAUBEN),
    BODEN_VOR_DEM_SCHLOSS("auf dem Boden", "auf den Boden",
            AUFHEBEN, AUFKLAUBEN,
            VOR_DEM_SCHLOSS),
    AM_GRUNDE_DES_BRUNNENS("am Grunde des Brunnens", "auf den Grund des Brunnens"),
    EIN_TISCH("auf einem Tisch", "auf einen Tisch", NEHMEN, EINSAMMELN),
    NEBEN_SC_AUF_EINER_BANK("neben dir auf einer Bank", "neben dich auf eine Bank",
            NEHMEN, EINSAMMELN),
    TISCH("auf dem Tisch", "auf den Tisch", NEHMEN, ZUSAMMENSAMMELN),
    STAMM_EINES_BAUMS("am Stamm eines Baumes", "an den Stamm eines Baumes"),
    NEBEN_DEM_BRUNNEN("neben dem Brunnnen", "neben den Brunnen",
            AUFHEBEN, EINSAMMELN),
    BETTGESTELL("im Bettgestell", "ins Bettgestell", NEHMEN, EINSAMMELN),
    ECKE_IM_BETTGESTELL("in einer Ecke des Bettgestells",
            "in eine unauffällige Ecke des Bettgestells",
            AN_SICH_NEHMEN, EINSAMMELN, BETTGESTELL),
    HOLZTISCH("auf dem hölzernen Tisch", "auf den Holztisch", NEHMEN, EINSAMMELN),
    VOR_DER_HUETTE("vor der Hütte", "vor die Hütte"),
    ERDBODEN_VOR_DER_HUETTE("auf dem Erdboden vor der Hütte",
            "auf den Erdboden vor der Hütte",
            AUFHEBEN, AUFSAMMELN,
            VOR_DER_HUETTE),
    IM_GEAEST("im Geäst", "ins Geäst", NEHMEN, ZUSAMMENSUCHEN),
    ASTGABEL("in einer Astgabel",
            "in eine Astgabel",
            HEBEN.mitAdvAngabe(
                    new AdvAngabeSkopusVerbWohinWoher(
                            AUS.mit(NomenFlexionsspalte.ASTGABEL))),
            SAMMELN.mitAdvAngabe(
                    new AdvAngabeSkopusVerbWohinWoher(
                            AUS.mit(NomenFlexionsspalte.ASTGABEL))),
            IM_GEAEST),
    WALD("im Wald", "in den Wald", NEHMEN, AUFKLAUBEN),
    MATSCHIGER_WALDBODEN("auf dem matschigen Waldboden",
            "auf den matschigen Waldboden",
            new ZweiPraedikatMitEinerObjektLeerstelle(AUFHEBEN, POLIEREN),
            ZUSAMMENSAMMELN,
            WALD),
    // IDEA    WALDBODEN("zwischen Blättern und Gestrüpp", "auf den Waldboden",
    //         HERAUSKLAUBEN),
    WEG("auf dem Weg", "auf den Weg"),
    VOR_TURM("vor dem Turm", "vor den Turm"),
    STEINIGER_GRUND_VOR_TURM("auf dem steinigen Grund vor dem Turm",
            "auf den steinigen Grund vor dem Turm", VOR_TURM),
    TURMZIMMER("im Turmzimmer", "in das Turmzimmer", NEHMEN, ZUSAMMENSUCHEN),
    HOLZDIELEN_OBEN_IM_TURM("auf den Holzdielen", "auf die Holzdielen",
            NEHMEN, ZUSAMMENSUCHEN, TURMZIMMER),
    UNTER_DEM_BAUM("unter dem Baum", "unter den Baum", AUFHEBEN, AUFHEBEN),
    IM_MORAST("im Morast", "in den Morast",
            // "du hebst die Kugel aus dem Morast und bemühst dich, sie etwas abzutrocknen"
            new ZweiPraedikatMitEinerObjektLeerstelle(
                    HEBEN.mitAdvAngabe(
                            new AdvAngabeSkopusVerbWohinWoher(AUS.mit(NomenFlexionsspalte.MORAST))),
                    SICH_BEMUEHEN.mitLexikalischemKern(ABTROCKNEN)),
            SAMMELN.mitAdvAngabe(new AdvAngabeSkopusSatz("widerwillig"))
                    .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher("aus dem Matsch"))),
    UNTER_DEM_BETT("unter dem Bett", "unter das Bett", HERVORHOLEN, HERVORSUCHEN);

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
     * Das Verb das beschreibt, das der Benutzer etwas, das <i>nicht aus vielen einzelnen
     * unverbundenen Teilen besteht</i> von diesem Ort <i>mitnimmt</i>.
     */
    private final PraedikatMitEinerObjektleerstelle mitnehmenPraedikatStandard;

    /**
     * Das Verb das beschreibt, das der Benutzer etwas, das <i>aus vielen einzelnen
     * unverbundenen Teilen besteht</i> von diesem Ort <i>mitnimmt</i>.
     */
    private final PraedikatMitEinerObjektleerstelle mitnehmenPraedikatVielteilig;

    StoringPlaceType(final String wo, final String wohin) {
        this(wo, wohin, NEHMEN, AUFSAMMELN);
    }

    StoringPlaceType(final String wo, final String wohin,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikatStandard,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikatVielteilig) {
        this(wo, wohin, mitnehmenPraedikatStandard, mitnehmenPraedikatVielteilig,
                null);
    }

    StoringPlaceType(final String wo, final String wohin,
                     @Nullable final StoringPlaceType forBelebtUndEherGross) {
        this(wo, wohin, NEHMEN, NEHMEN, forBelebtUndEherGross);
    }

    StoringPlaceType(final String wo, final String wohin,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikatStandard,
                     final PraedikatMitEinerObjektleerstelle mitnehmenPraedikatVielteilig,
                     @Nullable final StoringPlaceType forBelebtUndEherGross) {
        this.wo = wo;
        this.wohin = wohin;
        this.mitnehmenPraedikatStandard = mitnehmenPraedikatStandard;
        this.mitnehmenPraedikatVielteilig = mitnehmenPraedikatVielteilig;
        this.forBelebtUndEherGross = forBelebtUndEherGross;
    }

    public AdvAngabeSkopusVerbAllg getWoAdvAngabe(final boolean forBelebtUndEherGross) {
        return new AdvAngabeSkopusVerbAllg(getWo(forBelebtUndEherGross));
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

    /**
     * Gibt ein Prädikat für das Mitnehmen von dieser Location zurück.
     *
     * @param vielteilig Ob das Objekt, das mitgenommen wird, vielteilig ist
     *                   (z.B. viele Äste)
     */
    public PraedikatMitEinerObjektleerstelle getMitnehmenPraedikat(final boolean vielteilig) {
        return vielteilig ? mitnehmenPraedikatVielteilig : mitnehmenPraedikatStandard;
    }
}
