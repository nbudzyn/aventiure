package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.AllgDescription;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static java.util.Arrays.asList;

public enum FroschkoenigStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)
    KUGEL_GENOMMEN(20, SCHLOSS_VORHALLE,
            FroschkoenigStoryNode::narrateAndDoHintAction_KugelGenommen),
    MIT_KUGEL_ZUM_BRUNNEN_GEGANGEN(8, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_MitKugelZumBrunnenGegangen,
            KUGEL_GENOMMEN
    ),
    ETWAS_IM_BRUNNEN_VERLOREN(6, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_EtwasImBrunnenVerloren,
            KUGEL_GENOMMEN // Ansonsten gibt es derzeit gar nichts zum Verlieren
    ),
    FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT(10, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt,
            ETWAS_IM_BRUNNEN_VERLOREN
    ),
    ZUM_SCHLOSSFEST_GEGANGEN(30, // STORY Diese Zahl ermitteln!
            DRAUSSEN_VOR_DEM_SCHLOSS,
            FroschkoenigStoryNode::narrateAndDoHintAction_ZumSchlossfestGegangen,
            KUGEL_GENOMMEN, // Ansonsten kann der Spieler nicht wissen, dass es ein Schlossfest
            // überhaupt gibt
            FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT // Ansonsten bringt einem das Schlossfest nichts
    ),
    BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT(4, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            FroschkoenigStoryNode::narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt,
            ZUM_SCHLOSSFEST_GEGANGEN
    ),
    PRINZ_IST_ERLOEST(6, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            FroschkoenigStoryNode::narrateAndDoHintAction_PrinzIstErloest,
            BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT
    ),
    PRINZ_IST_WEGGEFAHREN(4, DRAUSSEN_VOR_DEM_SCHLOSS,
            FroschkoenigStoryNode::narrateAndDoHintAction_PrinzIstWeggefahren,
            PRINZ_IST_ERLOEST
    );

    private final ImmutableSet<FroschkoenigStoryNode> preconditions;

    private final int expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    private final IHinter hinter;

    FroschkoenigStoryNode(final int expAchievementSteps, @Nullable final GameObjectId locationId,
                          final IHinter hinter,
                          final FroschkoenigStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    FroschkoenigStoryNode(final Collection<FroschkoenigStoryNode> preconditions,
                          final int expAchievementSteps, @Nullable final GameObjectId locationId,
                          final IHinter hinter) {
        this.preconditions = ImmutableSet.copyOf(preconditions);
        this.locationId = locationId;
        this.expAchievementSteps = expAchievementSteps;
        this.hinter = hinter;
    }

    @Override
    public Story getStory() {
        return Story.FROSCHKOENIG;
    }

    @Override
    public ImmutableSet<FroschkoenigStoryNode> getPreconditions() {
        return preconditions;
    }

    @Override
    public int getExpAchievementSteps() {
        return expAchievementSteps;
    }

    @Nullable
    @Override
    public GameObjectId getLocationId() {
        return locationId;
    }

    @Override
    public boolean beendetStory() {
        return this == PRINZ_IST_WEGGEFAHREN;
    }

    @Override
    public IHinter getHinter() {
        return hinter;
    }

    private static AvTimeSpan narrateAndDoHintAction_KugelGenommen(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            alt.add(paragraph("Du fühlst dich von der goldenen Kugel magisch angezogen"));
        } else {
            alt.add(paragraph("Zusammenhanglos kommt dir ein Gedanke in den "
                    + "Kopf: Hättest du vielleicht die goldene Kugel mitnehmen sollen?"));

            alt.add(paragraph("Die goldene Kugel aus dem Schloss will dir nicht mehr aus dem "
                    + "Kopf"));

            alt.add(paragraph("Auf einmal musst du an die goldene Kugel denken, die dich im "
                    + "Schloss so angelacht hat"));

            alt.add(paragraph("Du musst spontan denken: Bei den reichen Leuten liegen oft so "
                    + "viele Herrlichkeiten ungenutzt herum… – wie kommst du jetzt "
                    + "bloß darauf?"));

            alt.add(paragraph("Deine Gedanken schweifen ab und du musst an die goldene Kugel "
                    + "denken, die du im Schloss gelassen hast. Warum eigentlich?"));
        }

        if (world.loadSC().feelingsComp().getMood().isTraurigerAls(ERSCHOEPFT)) {
            alt.add(paragraph("Dir kommt ein Gedanke: Ein glänzendes Spielzeug – das würde "
                    + "dich wohl aufheitern! Aber woher nehmen und nicht stehlen?"));
        }

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_MitKugelZumBrunnenGegangen(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY Man könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte, ganz normal mit addAlt().

        // STORY
        //  - Tagsüber: "Es ziemlich heiß heute - ein kühler Ort wäre schön"

        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    public static AvTimeSpan narrateAndDoHintAction_EtwasImBrunnenVerloren(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY
        //  - (bis BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT) Eine verwirrte alte Frau
        //   geht vorbei und jammert: Im Königreich nebenan
        //   ist der Prinz verschwunden. Er soll verwünscht oder in ein Tier
        //   verwandelt worden sein.
        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    public static AvTimeSpan narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY
        //  (traurig) - Du bist.... Es fällt dir schwer, deine Gefühle zu unterdrücken.
        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    public static AvTimeSpan narrateAndDoHintAction_ZumSchlossfestGegangen(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY
        //  - "Wann sollte eigentlich das Schlossfest sein? Da gibt es sicher etwas
        //  Gutes zu essen",
        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    public static AvTimeSpan narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY
        //  - "Der leckere Duft aus dem Schloss geht dir nicht aus der Nase und aus dem
        //  Sinn. Was hattest du dem Frosch am Brunnen noch versprochen? - "
        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    public static AvTimeSpan narrateAndDoHintAction_PrinzIstErloest(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY
        //  - Plötzlich überkommt dich ein schlechtes Gewissen. Hättest du nicht
        //   längst mit dem Frosch gemeinsam essen sollen? Hattest du das nicht
        //   versprochen?
        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    public static AvTimeSpan narrateAndDoHintAction_PrinzIstWeggefahren(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY Du willst auch sehen, was vor dem Schloss geschieht
        return n.addAlt(paragraph("Du hast das Gefühl, es gibt noch viel zu erleben"));
    }

    private static AllgDescription paragraph(final String paragraph) {
        return neuerSatz(PARAGRAPH,
                paragraph,
                noTime())
                .beendet(PARAGRAPH);
    }
}
