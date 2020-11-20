package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public class SimpleConnectionCompFactory {
    private final AvDatabase db;
    private final Narrator n;
    private final World world;

    public SimpleConnectionCompFactory(final AvDatabase db,
                                       final Narrator n,
                                       final World world) {
        this.db = db;
        this.n = n;
        this.world = world;
    }

    @NonNull
    public SimpleConnectionComp createHuetteImWald() {
        return new SimpleConnectionComp(HUETTE_IM_WALD,
                db,
                n,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "in der Tür",
                        "Die Hütte verlassen",
                        secs(15),
                        du("zwängst", "dich wieder durch die Tür nach "
                                + "draußen", secs(15))
                                .undWartest()
                                .dann()
                ));
    }

    @NonNull
    public SimpleConnectionComp createHinterDerHuette() {
        return new SimpleConnectionComp(HINTER_DER_HUETTE,
                db,
                n,
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
    @CheckReturnValue
    public SimpleConnectionComp createWaldwildnisHinterDemBrunnen() {
        return new SimpleConnectionComp(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                db,
                n, world,
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
                n, world,
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "auf dem Weg zum Schloss",
                        "In Richtung Schloss gehen",
                        mins(5),
                        du("gehst", "weiter in Richtung Schloss", mins(5))
                ),

                con(VOR_DER_HUETTE_IM_WALD,
                        "in all dem Unkraut",
                        "Den überwachsenen Abzweig nehmen",
                        // FIXME Besser etwas wie "Den Abzweig zur Hütte nehmen" o.Ä. -
                        //  wenn man den Turm schon kennt.
                        //  Die Grundidee könnte sein: Statt "Abzweig" oder so
                        //  das wichtigste, dem Spieler schon bekannte Ziel nennen,
                        //  zu dem dieser Abzweig führt. Also wohl eine Methodenreferenz
                        //  erlauben, die diesen Text ermittelt - auf Basis dessen, was
                        //  der Spieler schon kennt!
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

                        // STORY Wenn der Benutzer die Kugel im Brunnen (o.Ä.)
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

    public SimpleConnectionComp createNoConnections(final GameObjectId gameObjectId) {
        return new SimpleConnectionComp(gameObjectId,
                db, n, world);
    }
}
