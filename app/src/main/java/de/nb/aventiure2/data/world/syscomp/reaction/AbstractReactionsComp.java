package de.nb.aventiure2.data.world.syscomp.reaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;

/**
 * Component für ein {@link GameObject}: The game object might
 * react to certain events.
 */
public abstract class AbstractReactionsComp extends AbstractStatelessComponent
        implements IReactions {
    protected final AvDatabase db;
    protected final World world;

    protected final StoryStateDao n;

    // STORY Jeder Creature (jedem NPC) ein Ziel geben - oder in jedem NPC
    //  ein "Potenzial" anlegen (durch seine Werte oder Möglichkeiten),
    //  dass er im Rahmen seiner KI ausschöpfen möchte.

    // STORY Zu jedem Schritt einen Konflikt / ein Hindernis vorsehen: Gegenspieler, Rivale o.Ä.

    // STORY Der Wald kämpft (nachts) gegen den Spieler. Als wäre er böse.

    // STORY Player should care about their character / stuff / achievements / reputation.

    // STORY Es gibt Tasks. Gewisse Aktionen schalten ein neuen Task frei. Wenn ein
    //  Task-Ziel erreicht wird, wird eine neue (möglichst abstrakte) Überschrift gesetzt und
    //  damit ein Kapitel begonnen. Die Überschrift bezieht sich lose auf einen der
    //  Task, die der Spieler zuerst begonnen hat und die noch nicht abgeschlossen
    //  wurden. Für jeden Task stehen mehrere Überschriften bereit, die in Reihenfolge
    //  gewählt werden.
    //  Es könnte zwischen Taskbeginn und erreichten Task-Ziel auch Zwischenpunkte geben,
    //  basierend auf Story-Telling-Theorien.
    //  Tasks einschließlich dieser Zwischenpunkte könnten auch generiert werden,
    //  basierend auf Story-Telling-Theorien.

    public AbstractReactionsComp(final GameObjectId id,
                                 final AvDatabase db,
                                 final World world) {
        super(id);
        this.db = db;
        this.world = world;

        n = db.storyStateDao();
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }
}
