package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.NORTH;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.SOUTH;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;

public class HinterDerHuetteConnectionComp extends AbstractSpatialConnectionComp {
    public HinterDerHuetteConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(HINTER_DER_HUETTE, db, timeTaker, n, world);
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
                con(VOR_DER_HUETTE_IM_WALD,
                        "auf dem Weg",
                        SOUTH, "Zur Vorderseite der Hütte gehen",
                        secs(30),
                        du("kehrst", "zurück zur Vorderseite der Hütte")
                                .undWartest()
                                .dann()),
                con(BINSENSUMPF,
                        "oben am Hang",
                        NORTH, this::getActionName_HinterDerHuette_To_Binsensumpf,
                        mins(1),
                        neuerSatz(PARAGRAPH, "Hinter dem Garten geht es einen Hang hinab. Unten",
                                "wird es sumpfig, bis du nicht mehr weiter kommst. Du hörst",
                                "einen Fluss rauschen, um dich herum wachsen Binsen"),
                        neuerSatz(PARAGRAPH,
                                "Im Dunkel hinter dem Garten liegt ein Hang, den du nur",
                                "mit Mühe hinab kommst. Unten wird es bald sumpfig, und du",
                                "hörst einen Fluss rauschen. Vor dir wachsen Binsen")
                                .timed(mins(3)),
                        du("bist", "den Hang hinter dem verwilderten Garten",
                                "im Hellen schnell hinunter. Aber der Boden wird bald feucht,",
                                "an den Binsen kommst du nicht weiter")
                                .mitVorfeldSatzglied("im Hellen")
                                .undWartest(),
                        du("kraxelst", "wieder den Hang hinab,",
                                "dorthin, wo auf sumpfigem Boden",
                                "die Binsen wachsen").komma()
                                .undWartest().dann())
        );
    }

    private String getActionName_HinterDerHuette_To_Binsensumpf() {
        if (loadSC().memoryComp().isKnown(BINSENSUMPF)) {
            return "Den Hang hinter dem Garten hinabsteigen";
        }
        return "Hinter dem Garten weitergehen";
    }
}
