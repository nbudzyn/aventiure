package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.gameobject.World.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

public class SimpleConnectionCompFactory {
    private final AvDatabase db;
    private final World world;

    public SimpleConnectionCompFactory(final AvDatabase db,
                                       final World world) {
        this.db = db;
        this.world = world;
    }

    @NonNull
    public SimpleConnectionComp createVorDerHuetteImWald() {
        return new SimpleConnectionComp(VOR_DER_HUETTE_IM_WALD,
                db,
                world,
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
                        neuerSatz("Du schiebst die Tür zur Seite und "
                                + "zwängst dich hinein. Durch Ritzen in den "
                                + "Fensterläden fällt ein wenig Licht: "
                                + "Die Hütte ist "
                                + "anscheinend trocken und, wie es aussieht, "
                                + "bis auf einige "
                                + "Tausendfüßler "
                                + "unbewohnt. Du siehst ein Bettgestell, "
                                + "einen Tisch, aber sonst keine Einrichtung", mins(1)),
                        neuerSatz("Du schiebst die Tür zur Seite und "
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
                con(HINTER_DER_HUETTE,
                        "im Garten",
                        "Um die Hütte herumgehen",
                        secs(30),
                        neuerSatz("Ein paar Schritte um die Hütte herum und "
                                + "du kommst in einen kleinen, völlig "
                                + "verwilderten Garten. In seiner Mitte "
                                + "steht einzeln… es könnte ein "
                                + "Apfelbaum sein. Früchte siehst du von "
                                + "unten keine.", secs(30)),
                        neuerSatz("Vorsichtig gehst du im Dunkeln ein paar Schritte "
                                + "um die Hütte herum. Du kannst die Silhuette "
                                + "eines einzelnen Baums erkennen, vielleicht – "
                                + "ein Apfelbaum", mins(1)),
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

    @NonNull
    public SimpleConnectionComp createHuetteImWald() {
        return new SimpleConnectionComp(HUETTE_IM_WALD,
                db,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "in der Tür",
                        "Die Hütte verlassen",
                        secs(15),
                        du("zwängst", "dich wieder durch die Tür nach "
                                + "draußen", secs(15))
                                .undWartest()
                                .dann()
                ),

                con(BETT_IN_DER_HUETTE_IM_WALD,
                        "auf der Bettkante",
                        "In das Bett legen",
                        secs(15),
                        du(PARAGRAPH, "legst", "dich in das hölzere Bettgestell. "
                                + "Gemütlich ist etwas anderes, aber nach den "
                                + "vielen Schritten tut es sehr gut, sich "
                                + "einmal auszustrecken", secs(15)),

                        du("legst", "dich noch einmal in das Holzbett",
                                "noch einmal", secs(15))
                                .undWartest()
                                .dann()
                ));
    }

    @NonNull
    public SimpleConnectionComp createHinterDerHuette() {
        return new SimpleConnectionComp(HINTER_DER_HUETTE,
                db,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "auf dem Weg",
                        "Zur Vorderseite der Hütte gehen",
                        secs(30),
                        du("kehrst", "zurück zur Vorderseite der "
                                + "Hütte", secs(30))
                                .undWartest()
                                .dann()
                )
        );
    }

    @NonNull
    public SimpleConnectionComp createBettInDerHuetteImWald() {
        return new SimpleConnectionComp(BETT_IN_DER_HUETTE_IM_WALD,
                db,
                world,
                con(HUETTE_IM_WALD,
                        "auf der Bettkante",
                        "Aufstehen",
                        secs(10),
                        du(SENTENCE, "reckst", "dich noch einmal und stehst "
                                + "wieder auf", secs(10))
                                .dann()
                ));
    }

    @NonNull
    public SimpleConnectionComp createWaldwildnisHinterDemBrunnen() {
        return new SimpleConnectionComp(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                db,
                world,
                con(IM_WALD_BEIM_BRUNNEN,
                        "mitten im wilden Wald",
                        "Zum Brunnen gehen",
                        mins(3),
                        du("suchst", "dir einen Weg "
                                        + "durch den wilden Wald zurück zum Brunnen",
                                "durch den wilden Wald",
                                mins(3))
                                .undWartest()
                                .dann()
                ));
        // STORY Nächster Raum: "wirst du von einer dichten Dornenhecke
        //  zurückgehalten"
    }

    @NonNull
    public SimpleConnectionComp createAbzweigImWald() {
        return new SimpleConnectionComp(ABZWEIG_IM_WALD,
                db,
                world,
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "auf dem Weg zum Schloss",
                        "In Richtung Schloss gehen",
                        mins(5),
                        du("gehst", "weiter in Richtung Schloss", mins(5))
                ),

                con(VOR_DER_HUETTE_IM_WALD,
                        "in all dem Unkraut",
                        "Den überwachsenen Abzweig nehmen",
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
                        "Auf dem Hauptweg tiefer in den Wald gehen",
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

    public SimpleConnectionComp createNoConnections(final GameObjectId gameObjectId) {
        return new SimpleConnectionComp(gameObjectId,
                db, world);
    }
}
