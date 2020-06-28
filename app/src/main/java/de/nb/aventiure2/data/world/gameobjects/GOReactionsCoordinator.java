package de.nb.aventiure2.data.world.gameobjects;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public class GOReactionsCoordinator {
    private final GameObjectService gos;

    GOReactionsCoordinator(final GameObjectService gos) {
        this.gos = gos;
    }

    // IMovementReactions
    public AvTimeSpan onLeave(final GameObjectId locatableId,
                              final ILocationGO from,
                              @Nullable final GameObjectId toId) {
        // FIXME Hier und überall: Nicht den Aktor, der
        //  die Aktion durchgeführt hat, um eine Reaktion anfragen
        //  (wenn das nicht eh schon geschieht?!).
        //  Problem: Wenn der Dieb den Spieler bestiehlt o.Ä. kennt man den
        //  Aktor nicht. Aktor also separater Parameter?
        return onLeave(
                (ILocatableGO) gos.load(locatableId),
                from, toId);
    }

    private AvTimeSpan onLeave(final ILocatableGO locatable,
                               final ILocationGO from,
                               @Nullable final GameObjectId toId) {
        if (toId == null) {
            return onLeave(locatable, from, (ILocationGO) null);
        }

        final GameObject to = gos.load(toId);
        if (!(to instanceof ILocationGO)) {
            return noTime();
        }

        return onLeave(locatable, from, (ILocationGO) to);
    }

    private AvTimeSpan onLeave(final ILocatableGO locatable,
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
                (ILocatableGO) gos.load(locatableId),
                from, toId);
    }

    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final GameObjectId toId) {
        final GameObject to = gos.load(toId);
        if (!(to instanceof ILocationGO)) {
            return noTime();
        }

        return onEnter(locatable, from, (ILocationGO) to);
    }

    private AvTimeSpan onEnter(final ILocatableGO locatable,
                               @Nullable final ILocationGO from,
                               final ILocationGO to) {
        return doReactions(IMovementReactions.class,
                ((Predicate<IResponder>) locatable::equals).negate(),
                reactions -> reactions.onEnter(locatable, from, to));
    }

    // IEssenReactions
    public AvTimeSpan onEssen(final IGameObject gameObject) {
        return doReactions(IEssenReactions.class,
                ((Predicate<IResponder>) gameObject::equals).negate(),
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
                gos.loadResponders(reactionsInterface);
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
