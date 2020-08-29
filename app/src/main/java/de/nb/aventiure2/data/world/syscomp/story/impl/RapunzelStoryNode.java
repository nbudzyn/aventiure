package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;

import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static java.util.Arrays.asList;

public enum RapunzelStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    // TODO Erreichen der Nodes erkennen und speichern
    TURM_GEFUNDEN(20, VOR_DEM_ALTEN_TURM),
    RAPUNZEL_SINGEN_GEHOERT(10, VOR_DEM_ALTEN_TURM,
            TURM_GEFUNDEN
    ),
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(20, VOR_DEM_ALTEN_TURM,
            TURM_GEFUNDEN
    )
    // STORY HOCHGEKLETTERT ETC.
    ;

    private final ImmutableSet<RapunzelStoryNode> preconditions;

    private final int expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    RapunzelStoryNode(final int expAchievementSteps, @Nullable final GameObjectId locationId,
                      final RapunzelStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId);
    }

    RapunzelStoryNode(final Collection<RapunzelStoryNode> preconditions,
                      final int expAchievementSteps, @Nullable final GameObjectId locationId) {
        this.preconditions = ImmutableSet.copyOf(preconditions);
        this.expAchievementSteps = expAchievementSteps;
        this.locationId = locationId;
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
}
