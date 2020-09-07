package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.description.AbstractDescription;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.german.description.AllgDescription.paragraph;
import static java.util.Arrays.asList;

public enum RapunzelStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    TURM_GEFUNDEN(10, VOR_DEM_ALTEN_TURM,
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

    // STORY Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht
    private static AvTimeSpan narrateAndDoHintAction_TurmGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
        alt.add(paragraph("Hast du den Wald eigentlich schon überall erkundet?"));
        alt.add(paragraph("Was gibt es wohl noch alles im Wald zu entdecken, fragst du dich"));
        alt.add(paragraph("Dir kommt der geheimnisvolle Turm in den Sinn - du wirst sein "
                + "Geheimnis bestimmt noch lüften!"));

        // STORY (bis SC Rapunzel gefunden hat) Mutter sammelt im
        //  Wald Holz und klagt ihr Leid: Tochter an Zauberin verloren
        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_RapunzelSingenGehoert(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Ob der Turm wohl bewohnt ist?"));
            alt.add(paragraph("Eine Rast würde dir sicher guttun"));
        } else {
            alt.add(paragraph(
                    "Dir kommt noch einmal der alte Turm auf der Hügelkuppe "
                            + "in den Sinn. Ob der wohl bewohnt ist?"));
        }

        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_ZauberinAufTurmWegGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.addAll(altTurmWohnenHineinHeraus(world));

        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.addAll(altTurmWohnenHineinHeraus(world));

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Du wirst bestimmt noch den Turm hinaufkommen!"));
        }

        final ILocatableGO zauberin = (ILocatableGO) world.load(RAPUNZELS_ZAUBERIN);
        if (zauberin.locationComp()
                .hasSameUpperMostLocationAs(SPIELER_CHARAKTER) &&
                // Vor dem Schloss fällt sie dem SC nicht auf
                !zauberin.locationComp().hasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            alt.add(paragraph("Was will die Frau bloß?"));
            alt.add(paragraph("Was will die Frau wohl?"));
            alt.add(paragraph("Was mag die Frau wollen?"));
        }

        return n.addAlt(alt);
    }

    private static ImmutableList<AbstractDescription<?>> altTurmWohnenHineinHeraus(
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Wenn im Turm jemand wohnt – wie kommt er herein "
                    + "oder hinaus?"));
            alt.add(paragraph("Ob jemand im Turm ein und aus geht? Aber wie bloß?"));
        } else {
            alt.add(paragraph(
                    "Du musst wieder an den alten Turm denken… wenn dort jemand wohnt, "
                            + "wie kommt der bloß hinein oder heraus?"));
            alt.add(paragraph(
                    "Dir kommt auf einmal wieder der alte Turm in den Sinn: "
                            + "Wer wird darinnen wohl wohnen?"));
        }

        return alt.build();
    }
}
