package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;

import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static java.util.Arrays.asList;

public enum FroschkoenigStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)
    KUGEL_GENOMMEN(20, SCHLOSS_VORHALLE),
    // TODO Erreichen der folgenden Nodes erkennen und speichern
    MIT_KUGEL_ZUM_BRUNNEN_GEGANGEN(8, IM_WALD_BEIM_BRUNNEN,
            KUGEL_GENOMMEN
    ),
    ETWAS_IM_BRUNNEN_VERLOREN(6, IM_WALD_BEIM_BRUNNEN,
            KUGEL_GENOMMEN // Ansonsten gibt es derzeit gar nichts zum Verlieren
    ),
    DEM_FROSCH_ALLES_VERSPROCHEN(10, IM_WALD_BEIM_BRUNNEN,
            ETWAS_IM_BRUNNEN_VERLOREN
    ),
    ZUM_SCHLOSSFEST_GEGANGEN(30, // STORY Diese Zahl ermitteln!
            DRAUSSEN_VOR_DEM_SCHLOSS,
            KUGEL_GENOMMEN, // Ansonsten kann der Spieler nicht wissen, dass es ein Schlossfest
            // überhaupt gibt
            DEM_FROSCH_ALLES_VERSPROCHEN // Ansonsten bringt einem das Schlossfest nichts
    ),
    BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT(4, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            ZUM_SCHLOSSFEST_GEGANGEN
    ),
    PRINZ_IST_ERLOEST(6, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT
    ),
    PRINZ_IST_WEGGEFAHREN(4, DRAUSSEN_VOR_DEM_SCHLOSS,
            PRINZ_IST_ERLOEST
    );

    private final ImmutableSet<FroschkoenigStoryNode> preconditions;

    private final int expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    FroschkoenigStoryNode(final int expAchievementSteps, @Nullable final GameObjectId locationId,
                          final FroschkoenigStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId);
    }

    FroschkoenigStoryNode(final Collection<FroschkoenigStoryNode> preconditions,
                          final int expAchievementSteps, @Nullable final GameObjectId locationId) {
        this.preconditions = ImmutableSet.copyOf(preconditions);
        this.locationId = locationId;
        this.expAchievementSteps = expAchievementSteps;
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
}
