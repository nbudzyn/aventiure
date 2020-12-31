package de.nb.aventiure2.data.world.gameobject;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.time.AvDateTime.latest;

public class GOReactionsCoordinator
        implements IMovementReactions, IEssenReactions, IStateChangedReactions,
        IRufReactions,
        ITimePassedReactions,
        ISCActionReactions {
    private final World world;
    private final TimeTaker timeTaker;
    private final Narrator n;

    // REFACTOR ReactionCoordinator zum zentralen Teil eines ReactionSystems machen?

    GOReactionsCoordinator(final Narrator n,
                           final World world, final TimeTaker timeTaker) {
        this.world = world;
        this.timeTaker = timeTaker;
        this.n = n;
    }

    // IMovementReactions
    public void onLeave(final GameObjectId locatableId,
                        final ILocationGO from,
                        @Nullable final GameObjectId toId) {
        onLeave(
                (ILocatableGO) world.load(locatableId),
                from, toId);
    }

    private void onLeave(final ILocatableGO locatable,
                         final ILocationGO from,
                         @Nullable final GameObjectId toId) {
        if (toId == null) {
            onLeave(locatable, from, (ILocationGO) null);
            return;
        }

        final GameObject to = world.load(toId);
        if (!(to instanceof ILocationGO)) {
            return;
        }

        onLeave(locatable, from, (ILocationGO) to);
    }

    @Override
    public void onLeave(final ILocatableGO locatable,
                        final ILocationGO from,
                        @Nullable final ILocationGO to) {
        doReactions(IMovementReactions.class,
                reactions -> reactions.onLeave(locatable, from, to));
    }

    public void onEnter(final GameObjectId locatableId,
                        @Nullable final GameObjectId fromId,
                        final GameObjectId toId) {
        onEnter(locatableId,
                fromId != null ? (ILocationGO) world.load(fromId) : null,
                toId);
    }

    public void onEnter(final GameObjectId locatableId,
                        @Nullable final ILocationGO from,
                        final GameObjectId toId) {
        onEnter((ILocatableGO) world.load(locatableId), from, toId);
    }

    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final GameObjectId toId) {
        final GameObject to = world.load(toId);
        if (!(to instanceof ILocationGO)) {
            return;
        }

        onEnter(locatable, from, (ILocationGO) to);
    }

    @Override
    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final ILocationGO to) {
        doReactions(IMovementReactions.class,
                reactions -> reactions.onEnter(locatable, from, to));
    }

    // IEssenReactions
    @Override
    public void onEssen(final IGameObject gameObject) {
        doReactions(IEssenReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onEssen(gameObject));
    }

    // IStateChangedReactions
    public void onStateChanged(final GameObjectId gameObjectId,
                               final Enum<?> oldState,
                               final Enum<?> newState) {
        final IGameObject gameObject = world.load(gameObjectId);
        if (!(gameObject instanceof IHasStateGO<?>)) {
            return;
        }

        onStateChanged((IHasStateGO<?>) gameObject, oldState, newState);
    }

    @Override
    public void onStateChanged(final IHasStateGO<?> gameObject,
                               final Enum<?> oldState,
                               final Enum<?> newState) {
        doReactions(IStateChangedReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onStateChanged(
                        gameObject, oldState, newState));
    }

    // IRufReactions
    public void onRuf(final GameObjectId ruferId, final Ruftyp ruftyp) {
        final IGameObject rufer = world.load(ruferId);

        if (!(rufer instanceof ILocatableGO)) {
            return;
        }

        onRuf((ILocatableGO) rufer, ruftyp);
    }

    @Override
    public void onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        doReactions(IRufReactions.class,
                ((Predicate<IResponder>) rufer::equals).negate(),
                reactions -> reactions.onRuf(
                        rufer, ruftyp));
    }

    // ITimePassedReactions
    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        doReactions(ITimePassedReactions.class,
                reactions -> reactions.onTimePassed(startTime, endTime));
    }

    // ISCActionReactions
    @Override
    public void afterScActionAndFirstWorldUpdate() {
        doReactions(ISCActionReactions.class,
                ISCActionReactions::afterScActionAndFirstWorldUpdate);
    }

    /**
     * Have all game objects that implement this <code>reactionInterface</code>
     * react.
     */
    private <R extends IReactions> void doReactions(
            final Class<R> reactionsInterface,
            final Consumer<R> narrateAndDoReaction) {
        doReactions(reactionsInterface, responder -> true,
                narrateAndDoReaction);
    }

    /**
     * Have all game objects react that
     * <ul>
     *     <li>implement this <code>reactionInterface</code>
     *     <li>and fulfil this <code>condition</code>.
     * </ul>
     */
    private <R extends IReactions> void doReactions(
            final Class<R> reactionsInterface,
            final Predicate<IResponder> condition,
            final Consumer<R> narrateAndDoReaction) {

        final List<? extends IResponder> respondersToReaction =
                world.loadResponders(reactionsInterface);
        // STORY: Natürlicher wäre "wachst erst nach einigen Stunden wieder auf" -
        //  Danach die Tageszeitreactions ("Es ist jetzt vollständig dunkel geworden"),
        //  dann die "Wann hast du eigentlich zuletzt etwas gegessen", dann
        //  "Plitsch platsch" Frosch-Reactions.
        //  Die verschiedeenen Responder könnten also eine "Initiative" o.Ä. haben.

        final AvDateTime reactionsStartTime = timeTaker.now();
        AvDateTime timeAfterAllReactions = reactionsStartTime;

        final Narration.NarrationSource narrationSourceBefore = n.getNarrationSourceJustInCase();
        try {
            n.setNarrationSourceJustInCase(REACTIONS);

            for (final IResponder responder : respondersToReaction) {
                // All reactions start at the same time.
                timeTaker.setNow(reactionsStartTime);

                if (condition.test(responder)) {
                    final R reactionsComp = (R) responder.reactionsComp();
                    narrateAndDoReaction.accept(reactionsComp);
                    timeAfterAllReactions = latest(timeAfterAllReactions, timeTaker.now());
                }
            }
        } finally {
            n.setNarrationSourceJustInCase(narrationSourceBefore);

            timeTaker.setNow(timeAfterAllReactions);
        }
    }
}
