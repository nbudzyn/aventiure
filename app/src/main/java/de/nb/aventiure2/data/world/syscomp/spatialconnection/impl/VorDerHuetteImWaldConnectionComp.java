package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public class VorDerHuetteImWaldConnectionComp extends AbstractSpatialConnectionComp {
        public VorDerHuetteImWaldConnectionComp(
                final AvDatabase db,
                final Narrator n, final World world) {
            super(VOR_DER_HUETTE_IM_WALD, db, n, world);
        }


    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
            return true;
    }

    @Override
    @NonNull
    @CheckReturnValue
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                con(ABZWEIG_IM_WALD,
                        "auf dem Weg",
                        "Auf den Waldweg zurückkehren",
                        mins(2),
                        neuerSatz("Durch Farn und Gestrüpp gehst du zurück zum "
                                + "Waldweg", mins(2))
                                .undWartest()
                                .dann()
                ),
                con(HUETTE_IM_WALD,
                        "in der Tür",
                        "Die Hütte betreten",
                        secs(15),
                        du("schiebst",
                                "die Tür zur Seite und "
                                        + "zwängst dich hinein. Durch Ritzen in den "
                                        + "Fensterläden fällt ein wenig Licht: "
                                        + "Die Hütte ist "
                                        + "anscheinend trocken und, wie es aussieht, "
                                        + "bis auf einige "
                                        + "Tausendfüßler "
                                        + "unbewohnt. Du siehst ein Bettgestell, "
                                        + "einen Tisch, aber sonst keine Einrichtung", mins(1)),
                        du("schiebst",
                                "die Tür zur Seite und "
                                        + "zwängst dich hinein. Erst ist alles "
                                        + "stockdunkel, aber dann kannst du doch mit "
                                        + "Mühe ein Bettgestell und einen Tisch "
                                        + "ausmachen", secs(90)),
                        du("schiebst", "dich noch einmal in die "
                                + "kleine Hütte. Durch Ritzen in den "
                                + "Fensterläden fällt ein wenig Licht: "
                                + "Die Hütte ist "
                                + "anscheinend trocken und, wie es aussieht, "
                                + "bis auf einige "
                                + "Tausendfüßler "
                                + "unbewohnt. Du siehst ein Bettgestell, "
                                + "einen Tisch, aber sonst keine Einrichtung", mins(1))
                                .komma()
                                .undWartest(),
                        du("schiebst", "dich noch einmal in die "
                                + "kleine Hütte, in der es außer Tisch und "
                                + "Bett wenig zu sehen gibt", secs(15))
                                .komma()
                                .undWartest()),
                // FIXME Klar machen, dass die Hütte kein kühler Ort ist! "Die hütte ist
                //  ordentlich aufgeheizt" oder ähnlich
                con(HINTER_DER_HUETTE,
                        "im Garten",
                        this::getActionNameTo_HinterDerHuette,
                        secs(30),
                        neuerSatz("Ein paar Schritte um die Hütte herum und "
                                + "du kommst in einen kleinen, völlig "
                                + "verwilderten Garten. In seiner Mitte "
                                + "steht einzeln… es könnte ein "
                                + "Apfelbaum sein. Früchte siehst du von "
                                + "unten keine.", secs(30)),
                        du("gehst",
                                "im Dunkeln vorsichtig ein paar Schritte "
                                        + "um die Hütte herum. Du kannst die Silhouette "
                                        + "eines einzelnen Baums erkennen, vielleicht – "
                                        + "ein Apfelbaum",
                                "vorsichtig",
                                mins(1)),
                        du("schaust", "noch einmal hinter die Hütte. "
                                + "Im Licht erkennst du dort einen kleinen, völlig "
                                + "verwilderten Garten mit dem einzelnen Baum in "
                                + "der Mitte", secs(30)),
                        du("schaust", "noch einmal in den alten "
                                + "Garten hinter der Hütte, wo der "
                                + "Baum wächst", secs(30))
                                .komma()
                                .undWartest()
                                .dann()
                ));
    }

    private String getActionNameTo_HinterDerHuette() {
        if (world.loadSC().memoryComp().isKnownFromLight(HINTER_DER_HUETTE)) {
            return "In den Garten hinter der Hütte gehen";
        }

        return "Um die Hütte herumgehen";
    }
}