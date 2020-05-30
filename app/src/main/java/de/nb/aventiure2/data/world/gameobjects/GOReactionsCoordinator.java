package de.nb.aventiure2.data.world.gameobjects;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Predicates.not;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public class GOReactionsCoordinator {
    private final AvDatabase db;

    GOReactionsCoordinator(final AvDatabase db) {
        this.db = db;
    }

    // IMovementReactions
    public AvTimeSpan onLeave(final GameObjectId locatableId,
                              final IHasStoringPlaceGO from,
                              @Nullable final GameObjectId toId) {
        // FIXME Hier und überall: Nicht den Aktor, der
        //  die Aktion durchgeführt hat, um eine Reaktion anfragen
        //  (wenn das nicht eh schon geschieht?!).
        //  Problem: Wenn der Dieb den Spieler bestiehlt o.Ä. kennt man den
        //  Aktor nicht. Aktor also separater Parameter?
        return onLeave(
                (ILocatableGO) GameObjects.load(db, locatableId),
                from, toId);
    }

    public AvTimeSpan onLeave(final GameObjectId locatableId,
                              final IHasStoringPlaceGO from,
                              @Nullable final IHasStoringPlaceGO to) {
        return onLeave(
                (ILocatableGO) GameObjects.load(db, locatableId),
                from, to);
    }

    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final IHasStoringPlaceGO from,
                              @Nullable final GameObjectId toId) {
        if (toId == null) {
            return onLeave(locatable, from, (IHasStoringPlaceGO) null);
        }

        final GameObject to = GameObjects.load(db, toId);
        if (!(to instanceof IHasStoringPlaceGO)) {
            return noTime();
        }

        return onLeave(locatable, from, (IHasStoringPlaceGO) to);
    }

    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final IHasStoringPlaceGO from,
                              @Nullable final IHasStoringPlaceGO to) {
        return doReactions(IMovementReactions.class,
                not(locatable::equals),
                reactions -> reactions.onLeave(locatable, from, to));
    }

    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final GameObjectId fromId,
                              final IHasStoringPlaceGO to) {
        if (fromId == null) {
            return onEnter(locatable, (IHasStoringPlaceGO) null, to);
        }

        final GameObject from = GameObjects.load(db, fromId);
        if (!(from instanceof IHasStoringPlaceGO)) {
            return noTime();
        }

        return onEnter(locatable, (IHasStoringPlaceGO) from, to);
    }

    public AvTimeSpan onEnter(final GameObjectId locatableId,
                              @Nullable final IHasStoringPlaceGO from,
                              final GameObjectId toId) {
        return onEnter(
                (ILocatableGO) GameObjects.load(db, locatableId),
                from, toId);
    }

    public AvTimeSpan onEnter(final GameObjectId locatableId,
                              @Nullable final IHasStoringPlaceGO from,
                              final IHasStoringPlaceGO to) {
        return onEnter(
                (ILocatableGO) GameObjects.load(db, locatableId),
                from, to);
    }

    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final IHasStoringPlaceGO from,
                              final GameObjectId toId) {
        final GameObject to = GameObjects.load(db, toId);
        if (!(to instanceof IHasStoringPlaceGO)) {
            return noTime();
        }

        return onEnter(locatable, from, (IHasStoringPlaceGO) to);
    }

    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final IHasStoringPlaceGO from,
                              final IHasStoringPlaceGO to) {
        return doReactions(IMovementReactions.class,
                not(locatable::equals),
                reactions -> reactions.onEnter(locatable, from, to));
    }

    // IEssenReactions
    public AvTimeSpan onEssen(final GameObjectId gameObjectId) {
        return onEssen(GameObjects.load(db, gameObjectId));
    }

    public AvTimeSpan onEssen(final IGameObject gameObject) {
        return doReactions(IEssenReactions.class,
                not(gameObject::equals),
                reactions -> reactions.onEssen(gameObject));
    }

    // ITimePassedReactions
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        return doReactions(ITimePassedReactions.class,
                reactions -> reactions.onTimePassed(lastTime, now));
    }

    /**
     * Have all game objects that implement this <code>reactionInterface</code>
     * react.
     */
    private <R extends IReactions> AvTimeSpan doReactions(
            final Class<R> reactionsInterface,
            final Function<R, AvTimeSpan> narrateAndDoReaction) {
        return doReactions(reactionsInterface, alwaysTrue(),
                narrateAndDoReaction);
    }

    /**
     * Have all game objects react that
     * <ul>
     *     <li>implement this <code>reactionInterface</code>
     *     <li>and fulfil this <code>condition</code>.
     * </ul>
     */
    private <R extends IReactions> AvTimeSpan doReactions(
            final Class<R> reactionsInterface,
            final Predicate<IResponder> condition,
            final Function<R, AvTimeSpan> narrateAndDoReaction) {

        final List<? extends IResponder> respondersToReaction =
                GameObjects.loadResponders(db, reactionsInterface);
        // STORY: Natürlicher wäre "wachst erst nach einigen Stunden wieder auf" -
        //  Danach die Die Tageszeitreactions ("Es ist jetzt vollständig dunkel geworden"),
        //  dann die "Wann hast du eigentlich zuletzt etwas gegessen", dann
        //  "Plitsch platsch" Frosch-Recations.
        //  Die verschiedeenen Responder könnten also eine "Initiative" o.Ä. haben.

        AvTimeSpan timeElapsed = noTime();

        for (final IResponder responder : respondersToReaction) {
            if (condition.test(responder)) {
                final R reactionsComp = (R) responder.reactionsComp();
                timeElapsed = timeElapsed.plus(narrateAndDoReaction.apply(reactionsComp));
            }
        }

        return timeElapsed;
    }
}
