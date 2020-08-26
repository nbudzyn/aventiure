package de.nb.aventiure2.data.world.gameobject;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.StoryWebReactionsComp;

import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;

/**
 * Factory für sehr abstrakte {@link GameObject}s, die damit zu tun haben, dass die
 * Welt einen Sinn erhält.
 */
public class MeaningFactory {
    private final AvDatabase db;
    private final World world;

    public MeaningFactory(final AvDatabase db,
                          final World world) {
        this.db = db;
        this.world = world;
    }

    public GameObject createStoryWeb() {
        return new StoryWeb(db, world);
    }

    private static class StoryWeb extends GameObject implements IResponder {
        private final StoryWebReactionsComp reactionsComp;

        public StoryWeb(final AvDatabase db, final World world) {
            super(STORY_WEB);
            // Jede Komponente muss registiert werden!
            reactionsComp = addComponent(new StoryWebReactionsComp(db, world));
        }

        @Nonnull
        @Override
        public StoryWebReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }
}
