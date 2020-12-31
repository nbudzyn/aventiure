package de.nb.aventiure2.data.world.gameobject;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.StoryWebReactionsComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.story.IStoryWebGO;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.data.world.syscomp.story.StoryWebComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Factory für sehr abstrakte {@link GameObject}s, die damit zu tun haben, dass die
 * Welt einen Sinn erhält.
 */
class MeaningFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;
    private final LocationSystem locationSystem;
    private final SpatialConnectionSystem spatialConnectionSystem;

    MeaningFactory(final AvDatabase db,
                   final TimeTaker timeTaker, final Narrator n,
                   final World world,
                   final LocationSystem locationSystem,
                   final SpatialConnectionSystem spatialConnectionSystem) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
        this.locationSystem = locationSystem;
        this.spatialConnectionSystem = spatialConnectionSystem;
    }

    GameObject createStoryWeb() {
        return new StoryWeb(db, timeTaker, n, world, locationSystem, spatialConnectionSystem);
    }

    private static class StoryWeb extends GameObject
            implements IStoryWebGO, IResponder {
        private final StoryWebComp storyWebComp;
        private final StoryWebReactionsComp reactionsComp;

        StoryWeb(final AvDatabase db, final TimeTaker timeTaker,
                 final Narrator n, final World world,
                 final LocationSystem locationSystem,
                 final SpatialConnectionSystem spatialConnectionSystem) {
            super(STORY_WEB);
            // Jede Komponente muss registiert werden!
            storyWebComp = addComponent(new StoryWebComp(
                    db, timeTaker, n, world, locationSystem, spatialConnectionSystem,
                    Story.FROSCHKOENIG, Story.RAPUNZEL));
            reactionsComp =
                    addComponent(
                            new StoryWebReactionsComp(db, db.counterDao(), n, world, storyWebComp));
        }

        @Nonnull
        @Override
        public StoryWebComp storyWebComp() {
            return storyWebComp;
        }

        @Nonnull
        @Override
        public StoryWebReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }
}
