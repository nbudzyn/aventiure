package de.nb.aventiure2.scaction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.description.IDescribableGO;
import de.nb.aventiure2.data.world.location.ILocatableGO;
import de.nb.aventiure2.data.world.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.scaction.action.base.reaction.AbstractReactions;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;

abstract class AbstractCreatureReactions<REACTOR extends ILivingBeingGO & IDescribableGO>
        extends AbstractReactions {
    // TODO Die Creature-Reactions sollten am Creature-Game-Object hängen, z.B.
    //  neben den Components. Dabei sollte klar sein, welche Methoden Texte schreiben und
    //  welche nicht.

    protected final REACTOR reactor;

    // STORY Jeder Creature (jedem NPC) ein Ziel geben!

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

    public AbstractCreatureReactions(final AvDatabase db,
                                     final GameObjectId reactorId) {
        super(db);
        reactor = (REACTOR) load(db, reactorId);
    }

    public REACTOR getReactor() {
        return reactor;
    }

    /**
     * Called after the PC has left the <code>oldRoom</code>.
     * <i>Make sure you alwasy set <code>letzterRaum</code> to <code>oldRoom</code> when creating
     * <code>StoryState</code>s</i>.
     */
    public abstract AvTimeSpan onLeaveRoom(final IGameObject oldRoom,
                                           StoryState currentStoryState);

    /**
     * Called after the PC has entered the <code>newRoom</code>.
     * <i>Make sure you alwasy set <code>letzterRaum</code> to <code>oldRoom</code> when creating
     * <code>StoryState</code>s</i>.
     */
    public abstract AvTimeSpan onEnterRoom(final IHasStoringPlaceGO oldRoom,
                                           IHasStoringPlaceGO newRoom,
                                           StoryState currentStoryState);

    public abstract AvTimeSpan onNehmen(IHasStoringPlaceGO room,
                                        ILocatableGO genommenData,
                                        StoryState currentStoryState);

    public abstract AvTimeSpan onEssen(IHasStoringPlaceGO room,
                                       StoryState currentStoryState);

    public abstract AvTimeSpan onAblegen(IHasStoringPlaceGO room,
                                         IGameObject abgelegtData,
                                         StoryState currentStoryState);

    public abstract AvTimeSpan onHochwerfen(IHasStoringPlaceGO room,
                                            ILocatableGO objectData,
                                            StoryState currentStoryState);

    public abstract AvTimeSpan onTimePassed(AvDateTime lastTime, AvDateTime now,
                                            final StoryState currentStoryState);

    protected Nominalphrase getReactorDescription() {
        return getReactorDescription(false);
    }

    protected Nominalphrase getReactorDescription(final boolean shortIfKnown) {
        return getDescription(getReactor(), shortIfKnown);
    }
}
