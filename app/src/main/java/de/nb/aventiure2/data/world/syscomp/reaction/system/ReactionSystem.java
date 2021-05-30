package de.nb.aventiure2.data.world.syscomp.reaction.system;

import com.google.common.collect.ImmutableList;

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

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.time.AvDateTime.latest;

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
        onLeave((ILocatableGO) world.load(locatableId), from, toId);
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
        onEnter(locatableId,
                fromId != null ? (ILocationGO) world.load(fromId) : null,
                toId);
    }

    private void onEnter(final GameObjectId locatableId,
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
        doReactions(IStateChangedReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
                reactions -> reactions.onStateChanged(
                        gameObject, oldState, newState));
    }

    // IKnownChangedReactions
    public void onKnownChanged(final GameObjectId knowerId, final GameObjectId knowee,
                               final Known oldKnown, final Known newKnown) {
        final IGameObject knower = world.load(knowerId);
        if (!(knower instanceof IHasMemoryGO)) {
            return;
        }

        onKnownChanged((IHasMemoryGO) knower, knowee, oldKnown, newKnown);
    }

    @Override
    public void onKnownChanged(final IHasMemoryGO knower, final GameObjectId knowee,
                               final Known oldKnown, final Known newKnown) {
        doReactions(IKnownChangedReactions.class,
                reactions -> reactions.onKnownChanged(
                        knower, knowee, oldKnown, newKnown));
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

    // onWetterChanged
    @Override
    public void onWetterChanged(final ImmutableList<WetterData> wetterSteps) {
        checkArgument(wetterSteps.size() >= 2,
                "At least to wetter steps necessary: old and new");

        doReactions(IWetterChangedReactions.class,
                reactions -> reactions.onWetterChanged(wetterSteps));
    }

    // ITimePassedReactions
    public void onTimePassed(final AvDateTime vorher, final AvDateTime nachher) {
        doReactions(ITimePassedReactions.class,
                reactions -> reactions.onTimePassed(new Change<>(vorher, nachher)));
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        doReactions(ITimePassedReactions.class,
                reactions -> reactions.onTimePassed(change));
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
    @SuppressWarnings("unchecked")
    private <R extends IReactions> void doReactions(
            final Class<R> reactionsInterface,
            final Predicate<IResponder> condition,
            final Consumer<R> narrateAndDoReaction) {

        final List<? extends IResponder> respondersToReaction =
                world.loadResponders(reactionsInterface);
        // IDEA: Natürlicher wäre "wachst erst nach einigen Stunden wieder auf" -
        //  Danach die Tageszeitreactions ("Es ist jetzt vollständig dunkel geworden"),
        //  dann die "Wann hast du eigentlich zuletzt etwas gegessen", dann
        //  "Plitsch platsch" Frosch-Reactions.
        //  Die verschiedeenen Responder könnten also eine "Initiative" o.Ä. haben, oder
        //  sie werden programmatisch in einer bestimmten Reihenfolge aufgerufen...

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
