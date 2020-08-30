package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static java.util.Arrays.asList;

public enum RapunzelStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    // TODO Erreichen der Nodes erkennen und speichern
    TURM_GEFUNDEN(20, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmGefunden),
    RAPUNZEL_SINGEN_GEHOERT(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelSingenGehoert,
            TURM_GEFUNDEN
    ),
    ZAUBERIN_AUF_TURM_WEG_GETROFFEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinAufTurmWegGefunden,
            TURM_GEFUNDEN),
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet,
            TURM_GEFUNDEN
    )
    // STORY HOCHGEKLETTERT ETC.
    ;

    private final ImmutableSet<RapunzelStoryNode> preconditions;

    private final int expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    private final IHinter hinter;

    RapunzelStoryNode(final int expAchievementSteps, @Nullable final GameObjectId locationId,
                      final IHinter hinter,
                      final RapunzelStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    RapunzelStoryNode(final Collection<RapunzelStoryNode> preconditions,
                      final int expAchievementSteps, @Nullable final GameObjectId locationId,
                      final IHinter hinter) {
        this.preconditions = ImmutableSet.copyOf(preconditions);
        this.locationId = locationId;
        this.expAchievementSteps = expAchievementSteps;
        this.hinter = hinter;
    }

    @Override
    public Story getStory() {
        return Story.RAPUNZEL;
    }

    @Override
    public ImmutableSet<RapunzelStoryNode> getPreconditions() {
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
        return this == values()[values().length - 1];
    }

    @Override
    public IHinter getHinter() {
        return hinter;
    }

    private static AvTimeSpan narrateAndDoHintAction_TurmGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY Man könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte, ganz normal mit addAlt().

        // STORY
        //  - "Hast du den Wald eigentlich schon überall erkundet?"
        //  - "Was gibt es wohl noch alles im Wald zu entdecken, fragst du dich"
        //  - (bis SC Rapunzel gefunden hat) Mutter sammelt im
        //  Wald Holz und klagt ihr Leid: Tochter an Zauberin verloren
        //  - Dir kommt der geheimnisvolle Turm in den Sinn - du wirst sein
        //   geheimnis bestimmt noch lüften!

        return n.addAlt(neuerSatz(PARAGRAPH,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime())
                .beendet(PARAGRAPH));
    }

    private static AvTimeSpan narrateAndDoHintAction_RapunzelSingenGehoert(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY Man könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte, ganz normal mit addAlt().

        // STORY
        //  - "Ob der Turm wohl bewohnt ist"?
        //  - (Außer direkt nach Schlafen:) Eine Rast würde dir sicher einmal gut tun.

        return n.addAlt(neuerSatz(PARAGRAPH,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime())
                .beendet(PARAGRAPH));
    }

    private static AvTimeSpan narrateAndDoHintAction_ZauberinAufTurmWegGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY Man könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte, ganz normal mit addAlt().

        // STORY
        //  - (bis ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET) Wenn im Turm jemand
        //  -  wohnt - wie kommt er herein oder hinaus?
        //  - (bis ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET) Ob jemand beim Turm ein und
        //  aus geht?

        return n.addAlt(neuerSatz(PARAGRAPH,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime())
                .beendet(PARAGRAPH));
    }

    private static AvTimeSpan narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY Man könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte, ganz normal mit addAlt().

        // STORY
        //  - Wohin will die magere Frau wohl?
        //  - Was will die magere Frau wohl?
        //  - Du wirst bestimmt noch in den Turm hinaufkommen!

        return n.addAlt(neuerSatz(PARAGRAPH,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime())
                .beendet(PARAGRAPH));
    }
}
