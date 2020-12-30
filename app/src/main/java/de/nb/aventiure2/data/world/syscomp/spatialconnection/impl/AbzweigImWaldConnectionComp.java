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
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public class AbzweigImWaldConnectionComp extends AbstractSpatialConnectionComp {
    public AbzweigImWaldConnectionComp(
            final AvDatabase db,
            final Narrator n, final World world) {
        super(ABZWEIG_IM_WALD, db, n, world);
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
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "auf dem Weg zum Schloss",
                        "In Richtung Schloss gehen",
                        mins(5),
                        du("gehst", "weiter in Richtung Schloss", mins(5))
                ),

                con(VOR_DER_HUETTE_IM_WALD,
                        "in all dem Unkraut",
                        this::getActionNameTo_HinterDerHuette,
                        mins(2),
                        du(SENTENCE, "fasst",
                                "dir ein Herz und stapfst zwischen "
                                        + "dem Unkraut einen Weg entlang, "
                                        + "der wohl schon länger nicht mehr benutzt wurde.\n"
                                        + "Hinter der "
                                        + "nächsten Biegung stehst du unvermittelt vor"
                                        + " einer Holzhütte. "
                                        + "Die Fensterläden sind "
                                        + "geschlossen, die Tür hängt nur noch lose "
                                        + "in den Angeln", mins(2))
                                .beendet(PARAGRAPH),

                        neuerSatz("Hat gerade neben dir im Unterholz geknarzt? "
                                + "Wie auch immer, du fasst dir ein Herz und "
                                + "stapfst durch das "
                                + "dem Unkraut einen düsteren Trampelpfad entlang. "
                                + "Hinter der "
                                + "nächsten Biegung stehst du unvermittelt vor"
                                + " der Tür einer Holzhütte. "
                                + "Die Tür hängt nur noch lose "
                                + "in den Angeln", mins(2)),

                        du("wählst", "noch einmal den überwachsenen "
                                + "Pfad zur Hütte. Es wirkt alles so, also sei "
                                + "er schon lange nicht mehr benutzt worden", mins(2))
                                .komma()
                                .dann(),

                        du("wählst", "noch einmal den überwachsenen "
                                + "Pfad zur Hütte", mins(2))
                                .undWartest()
                ),

                con(IM_WALD_BEIM_BRUNNEN,
                        "auf dem breiten Weg tiefer in den Wald",
                        this::getActionNameTo_ImWaldBeimBrunnen,
                        mins(3),
                        neuerSatz("Der breitere Weg führt zu einer alten "
                                + "Linde, unter der ist ein Brunnen. "
                                + "Hinter dem Brunnen endet der Weg und der "
                                + "wilde Wald beginnt.\n"
                                + "Du setzt "
                                + "dich an den Brunnenrand – "
                                + "hier ist es "
                                + "angenehm kühl", mins(5))
                                .dann()
                                .beendet(PARAGRAPH),

                        // FIXME Wenn der Benutzer bereits zuvor die Kugel im Brunnen (o.Ä.)
                        //  im Brunnen verloren hat: Mood setzen zum Heulen
                        //  (sofern der Frosch noch nicht aktiv geworden ist)
                        //  - Dasselbe auch bei der umbekehrten Richtung

                        du("gehst", "den breiteren Weg weiter in "
                                + "den Wald hinein. Wohl ist dir dabei nicht.\n"
                                + "In der Ferne heult ein Wolf – oder hast du "
                                + "dir das eingebildet?\nDann kommst du an einen "
                                + "Baum, unter dem ist ein Brunnen. Kühl ist es "
                                + "hier, und der Weg scheint zu Ende zu sein", mins(10)),

                        du("kehrst", "zurück zum Brunnen – unter einer Linde, wie "
                                + "du bei Licht erkennen kannst. Hinter dem "
                                + "Brunnen beginnt der wilde Wald", mins(4))
                                .komma(),

                        du("kehrst", "zurück zum Brunnen", mins(3))
                                .undWartest()
                                .dann()));
    }

    private String getActionNameTo_HinterDerHuette() {
        if (world.loadSC().memoryComp().isKnown(VOR_DER_HUETTE_IM_WALD)) {
            return "Den überwachsenen Abzweig zur Hütte nehmen";
        }

        return "Den überwachsenen Abzweig nehmen";
    }

    private String getActionNameTo_ImWaldBeimBrunnen() {
        if (world.loadSC().memoryComp().isKnown(IM_WALD_BEIM_BRUNNEN)) {
            return "Zum Brunnen gehen";
        }

        return "Auf dem Hauptweg tiefer in den Wald gehen";
    }
}
