package de.nb.aventiure2.data.world.syscomp.reaction.system;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.time.AvDateTime.latest;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IKnownChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IWetterChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterData;

/**
 * Functionality concerned with Reactions that might delta several game objects.
 */
public class ReactionSystem
        implements
        IMovementReactions, IEssenReactions, IStateChangedReactions,
        IKnownChangedReactions,
        IRufReactions,
        IWetterChangedReactions,
        ITimePassedReactions,
        ISCActionReactions {
    private static final Comparator<IResponder> REACTION_ORDER_COMPARATOR =
            new ReactionOrderComparator();


    private final World world;
    private final TimeTaker timeTaker;
    private final Narrator n;

    public ReactionSystem(final Narrator n,
                          final World world, final TimeTaker timeTaker) {
        this.world = world;
        this.timeTaker = timeTaker;
        this.n = n;
    }

    // IMovementReactions
    public void onLeave(final GameObjectId locatableId,
                        final ILocationGO from,
                        @Nullable final GameObjectId toId) {
        onLeave((ILocatableGO) world.loadRequired(locatableId), from, toId);
    }

    private void onLeave(final ILocatableGO locatable,
                         final ILocationGO from,
                         @Nullable final GameObjectId toId) {
        // In Ausnahmefällen ist es hier wohl möglich, dass from.getId() == toId()
        // (z.B. wenn der SC um den Turm herumgeht, vielleicht auch beim Hochwerfen und
        // Wieder-Auffangen.)

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
        // In Ausnahmefällen w#re es hier wohl möglich, dass from.getId() == to.getId()
        // (z.B. wenn der SC um den Turm herumgeht, vielleicht auch beim Hochwerfen und
        // Wieder-Auffangen.)

        doReactions(IMovementReactions.class,
                reactions -> reactions.onLeave(locatable, from, to)
        );
    }

    @Override
    public boolean isVorScVerborgen() {
        throw new IllegalStateException(
                "ReactionSystem#isVorScVerborgen() wurde aufgerufen."
                        + " Das ist ziemlich sinnlos, denn jeder IResponder "
                        + "erzeugt ja seinen eigenen Rückgabewert.");
    }

    public void onEnter(final GameObjectId locatableId,
                        @Nullable final GameObjectId fromId,
                        final GameObjectId toId) {
        onEnter(locatableId, (ILocationGO) world.load(fromId), toId);
    }

    private void onEnter(final GameObjectId locatableId,
                         @Nullable final ILocationGO from,
                         final GameObjectId toId) {
        onEnter((ILocatableGO) world.loadRequired(locatableId), from, toId);
    }

    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final GameObjectId toId) {
        // In Ausnahmefällen ist es hier wohl möglich, dass from.getId() == toId()
        // (z.B. wenn der SC um den Turm herumgeht, vielleicht auch beim Hochwerfen und
        // Wieder-Auffangen.)

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
        // In Ausnahmefällen ist es hier wohl möglich, dass from.getId() == toId()
        // (z.B. wenn der SC um den Turm herumgeht, vielleicht auch beim Hochwerfen und
        // Wieder-Auffangen.)

        doReactions(IMovementReactions.class,
                reactions -> reactions.onEnter(locatable, from, to)
        );
    }

    // IEssenReactions
    @Override
    public void onEssen(final IGameObject gameObject) {
        doReactions(IEssenReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onEssen(gameObject),
                REACTION_ORDER_COMPARATOR);
    }

    // IStateChangedReactions
    @SuppressWarnings("unchecked")
    public <S extends Enum<S>> void onStateChanged(final GameObjectId gameObjectId,
                                                   final S oldState,
                                                   final S newState) {
        final IGameObject gameObject = world.load(gameObjectId);
        if (!(gameObject instanceof IHasStateGO<?>)) {
            return;
        }

        onStateChanged((IHasStateGO<S>) gameObject, oldState, newState);
    }

    @Override
    public <S extends Enum<S>> void onStateChanged(final IHasStateGO<S> gameObject,
                                                   final S oldState,
                                                   final S newState) {
        checkArgument(oldState != newState, "State unverändert: " + oldState);

        doReactions(IStateChangedReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onStateChanged(
                        gameObject, oldState, newState),
                REACTION_ORDER_COMPARATOR);
    }

    // IKnownChangedReactions
    public void onKnownChanged(final GameObjectId knowerId, final GameObjectId knowee,
                               final Known oldKnown, final Known newKnown) {
        checkArgument(oldKnown != newKnown, "Known unverändert: " + oldKnown);

        final IGameObject knower = world.load(knowerId);
        if (!(knower instanceof IHasMemoryGO)) {
            return;
        }

        onKnownChanged((IHasMemoryGO) knower, knowee, oldKnown, newKnown);
    }

    @Override
    public void onKnownChanged(final IHasMemoryGO knower, final GameObjectId knowee,
                               final Known oldKnown, final Known newKnown) {
        checkArgument(oldKnown != newKnown, "Known unverändert: " + oldKnown);

        doReactions(IKnownChangedReactions.class,
                reactions -> reactions.onKnownChanged(
                        knower, knowee, oldKnown, newKnown)
        );
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
                        rufer, ruftyp), REACTION_ORDER_COMPARATOR);
    }

    // onWetterChanged
    @Override
    public void onWetterChanged(final ImmutableList<WetterData> wetterSteps) {
        checkArgument(wetterSteps.size() >= 2,
                "At least to wetter steps necessary: old and new");

        doReactions(IWetterChangedReactions.class,
                reactions -> reactions.onWetterChanged(wetterSteps)
        );
    }

    // ITimePassedReactions
    public void onTimePassed(final AvDateTime vorher, final AvDateTime nachher) {
        onTimePassed(new Change<>(vorher, nachher));
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        doReactions(ITimePassedReactions.class,
                reactions -> reactions.onTimePassed(change)
        );
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
        doReactions(reactionsInterface, responder -> true, narrateAndDoReaction,
                ReactionSystem.REACTION_ORDER_COMPARATOR);
    }

    /**
     * Have all game objects react that
     * <ul>
     *     <li>implement this <code>reactionInterface</code>
     *     <li>and fulfil this <code>condition</code>.
     * </ul>
     */
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private <R extends IReactions, G extends GameObject & IResponder> void doReactions(
            final Class<R> reactionsInterface,
            final Predicate<IResponder> condition,
            final Consumer<R> narrateAndDoReaction,
            @Nullable final Comparator<? super IResponder> order) {

        List<G> respondersToReaction =
                world.loadResponders(reactionsInterface);

        if (order != null) {
            respondersToReaction = new ArrayList<>(respondersToReaction);
            respondersToReaction.sort(order);
        }

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
