package de.nb.aventiure2.data.world.gameobject;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.narration.NarrationDao;
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
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.max;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public class GOReactionsCoordinator
        implements IMovementReactions, IEssenReactions, IStateChangedReactions,
        IRufReactions,
        ITimePassedReactions,
        ISCActionReactions {
    private final World world;
    private final NarrationDao n;

    // TODO ReactionCoordinaror zum zentralen Teil eines ReactionSystems machen?

    GOReactionsCoordinator(final World world, final NarrationDao narrationDao) {
        this.world = world;
        n = narrationDao;
    }

    // IMovementReactions
    public AvTimeSpan onLeave(final GameObjectId locatableId,
                              final ILocationGO from,
                              @Nullable final GameObjectId toId) {
        return onLeave(
                (ILocatableGO) world.load(locatableId),
                from, toId);
    }

    private AvTimeSpan onLeave(final ILocatableGO locatable,
                               final ILocationGO from,
                               @Nullable final GameObjectId toId) {
        if (toId == null) {
            return onLeave(locatable, from, (ILocationGO) null);
        }

        final GameObject to = world.load(toId);
        if (!(to instanceof ILocationGO)) {
            return noTime();
        }

        return onLeave(locatable, from, (ILocationGO) to);
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        return doReactions(IMovementReactions.class,
                ((Predicate<IResponder>) locatable::equals).negate(),
                reactions -> reactions.onLeave(locatable, from, to));
    }

    public AvTimeSpan onEnter(final GameObjectId locatableId,
                              @Nullable final ILocationGO from,
                              final GameObjectId toId) {
        return onEnter(
                (ILocatableGO) world.load(locatableId),
                from, toId);
    }

    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final GameObjectId toId) {
        final GameObject to = world.load(toId);
        if (!(to instanceof ILocationGO)) {
            return noTime();
        }

        return onEnter(locatable, from, (ILocationGO) to);
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final ILocationGO to) {
        return doReactions(IMovementReactions.class,
                reactions -> reactions.onEnter(locatable, from, to));
    }

    // IEssenReactions
    @Override
    public AvTimeSpan onEssen(final IGameObject gameObject) {
        return doReactions(IEssenReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onEssen(gameObject));
    }

    // IStateChangedReactions
    public AvTimeSpan onStateChanged(final GameObjectId gameObjectId,
                                     final Enum<?> oldState,
                                     final Enum<?> newState) {
        final IGameObject gameObject = world.load(gameObjectId);
        if (!(gameObject instanceof IHasStateGO<?>)) {
            return noTime();
        }

        return onStateChanged((IHasStateGO<?>) gameObject, oldState, newState);
    }

    @Override
    public AvTimeSpan onStateChanged(final IHasStateGO<?> gameObject,
                                     final Enum<?> oldState,
                                     final Enum<?> newState) {
        return doReactions(IStateChangedReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onStateChanged(
                        gameObject, oldState, newState));
    }

    // IRufReactions
    public AvTimeSpan onRuf(final GameObjectId ruferId, final Ruftyp ruftyp) {
        final IGameObject rufer = world.load(ruferId);

        if (!(rufer instanceof ILocatableGO)) {
            return noTime();
        }

        return onRuf((ILocatableGO) rufer, ruftyp);
    }

    @Override
    public AvTimeSpan onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        return doReactions(IRufReactions.class,
                ((Predicate<IResponder>) rufer::equals).negate(),
                reactions -> reactions.onRuf(
                        rufer, ruftyp));
    }

    // ITimePassedReactions
    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        return doReactions(ITimePassedReactions.class,
                reactions -> reactions.onTimePassed(lastTime, now));
    }

    // ISCActionReactions
    @Override
    public AvTimeSpan afterScActionAndFirstWorldUpdate() {
        return doReactions(ISCActionReactions.class,
                ISCActionReactions::afterScActionAndFirstWorldUpdate);
    }

    /**
     * Have all game objects that implement this <code>reactionInterface</code>
     * react.
     */
    private <R extends IReactions> AvTimeSpan doReactions(
            final Class<R> reactionsInterface,
            final Function<R, AvTimeSpan> narrateAndDoReaction) {
        return doReactions(reactionsInterface, responder -> true,
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
                world.loadResponders(reactionsInterface);
        // STORY: Natürlicher wäre "wachst erst nach einigen Stunden wieder auf" -
        //  Danach die Tageszeitreactions ("Es ist jetzt vollständig dunkel geworden"),
        //  dann die "Wann hast du eigentlich zuletzt etwas gegessen", dann
        //  "Plitsch platsch" Frosch-Recations.
        //  Die verschiedeenen Responder könnten also eine "Initiative" o.Ä. haben.

        AvTimeSpan timeElapsed = noTime();

        final Narration.NarrationSource narrationSourceBefore = n.getNarrationSourceJustInCase();
        try {
            n.setNarrationSourceJustInCase(REACTIONS);

            for (final IResponder responder : respondersToReaction) {
                if (condition.test(responder)) {
                    final R reactionsComp = (R) responder.reactionsComp();
                    timeElapsed = max(timeElapsed, narrateAndDoReaction.apply(reactionsComp));
                }
            }
        } finally {
            n.setNarrationSourceJustInCase(narrationSourceBefore);
        }

        return timeElapsed;
    }
}
