package de.nb.aventiure2.scaction.action.base.reaction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.time.Tageszeit;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;

public abstract class AbstractReactions {
    protected final AvDatabase db;
    protected final StoryStateDao n;

    protected final SpielerCharakter sc;

    public AbstractReactions(final AvDatabase db) {
        this.db = db;

        sc = loadSC(db);

        n = db.storyStateDao();
    }

    protected StoryStateBuilder alt(
            final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    private StoryStateBuilder alt(final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[0]));
    }

    protected StoryStateBuilder alt(final StoryStateBuilder... alternatives) {
        return n.chooseNextFrom(alternatives);
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject) {
        return getDescription(gameObject, false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject,
                                           final boolean shortIfKnown) {
        return GameObjects.getPOVDescription(sc, gameObject, shortIfKnown);
    }

    protected StoryStateBuilder t(
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {

        return StoryStateBuilder.t(startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(sc.locationComp().getLocation());
    }

    protected Lichtverhaeltnisse getLichtverhaeltnisse(final GameObjectId room) {
        return Lichtverhaeltnisse.getLichtverhaeltnisse(getTageszeit(), room);
    }

    protected Tageszeit getTageszeit() {
        return db.dateTimeDao().now().getTageszeit();
    }
}
