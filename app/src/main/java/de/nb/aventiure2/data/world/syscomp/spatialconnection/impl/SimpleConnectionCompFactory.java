package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection.con;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

public class SimpleConnectionCompFactory {
    private final AvDatabase db;

    public SimpleConnectionCompFactory(final AvDatabase db) {
        this.db = db;
    }

    @NonNull
    public SimpleConnectionComp createVorDerHuetteImWald() {
        return new SimpleConnectionComp(VOR_DER_HUETTE_IM_WALD,
                db,
                con(ABZWEIG_IM_WALD,
                        "Auf den Hauptpfad zurückkehren",
                        neuerSatz("Durch Farn und Gestrüpp gehst du zurück zum "
                                + "Hauptpfad", mins(2))
                                .undWartest()
                                .dann()
                ),
                con(HUETTE_IM_WALD,
                        "Die Hütte betreten",
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
                        "Um die Hütte herumgehen",
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
                con(VOR_DER_HUETTE_IM_WALD,
                        "Die Hütte verlassen",
                        du("zwängst", "dich wieder durch die Tür nach "
                                + "draußen", secs(15))
                                .undWartest()
                                .dann()
                ),

                con(BETT_IN_DER_HUETTE_IM_WALD,
                        "In das Bett legen",
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
                con(VOR_DER_HUETTE_IM_WALD,
                        "Zur Vorderseite der Hütte gehen",
                        du("kehrst", "zurück zur Vorderseite der "
                                + "Hütte", secs(15))
                                .undWartest()
                                .dann()
                )
        );
    }

    @NonNull
    public SimpleConnectionComp createBettInDerHuetteImWald() {
        return new SimpleConnectionComp(BETT_IN_DER_HUETTE_IM_WALD,
                db,
                con(HUETTE_IM_WALD,
                        "Aufstehen",
                        du(SENTENCE, "reckst", "dich noch einmal und stehst "
                                + "wieder auf", secs(10))
                                .dann()
                ));
    }

    @NonNull
    public SimpleConnectionComp createWaldwildnisHinterDemBrunnen() {
        return new SimpleConnectionComp(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                db,
                con(IM_WALD_BEIM_BRUNNEN,
                        "Zum Brunnen gehen",
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
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "In Richtung Schloss gehen",
                        neuerSatz("Von dort gehst du weiter in Richtung Schloss", mins(5))
                ),

                con(VOR_DER_HUETTE_IM_WALD,
                        "Den überwachsenen Abzweig nehmen",
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
                        "Auf dem Hauptpfad tiefer in den Wald gehen",
                        neuerSatz("Der breitere Pfad führt zu einer alten "
                                + "Linde, unter der ist ein Brunnen. "
                                + "Hinter dem Brunnen endet der Weg und der "
                                + "wilde Wald beginnt.\n"
                                + "Du setzt "
                                + "dich an den Brunnenrand – "
                                + "hier ist es "
                                + "angenehm kühl", mins(5))
                                .dann()
                                .beendet(PARAGRAPH),

                        du("gehst", "den breiteren Pfad weiter in "
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
                db);
    }
}