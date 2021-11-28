package de.nb.aventiure2.data.world.syscomp.talking;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.world.gameobject.World.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObjectDescriptionMixin;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.movement.IConversationable;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;

/**
 * Component for a {@link GameObject}: Das Game Object kann mit einem anderen
 * im Gespräch sein (dann ist diese Beziehung reflexiv).
 * <p>
 * Möglicherweise gibt es {@link SCTalkAction}s, also mögliche Redebeiträge, die der
 * Spieler(-Charakter) an das {@link ITalkerGO} richten kann (und auf die das
 * {@link ITalkerGO} dann irgendwie reagiert).
 */
public abstract class AbstractTalkingComp extends AbstractStatefulComponent<TalkingPCD>
        implements ITalkContext, IConversationable,
// Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin, IGameObjectDescriptionMixin {
    private static final ImmutableSet<String> TAGESZEITUNABHAENGIE_BEGRUESSUNGEN =
            ImmutableSet.of("hallo", "holla", "Gott zum Gruß", "Gott zum Gruße");

    private static final ImmutableSet<String> TAGESZEITUNABHAENGIE_VERABSCHIEDUNGEN =
            ImmutableSet.of("auf Wiedersehen", "bis ein andermal", "tschüss",
                    "na, dann bis bald einmal", "na dann, bis dann",
                    "na dann, man sieht sich", "man sieht sich",
                    "auf ein andermal", "na, dann erstmal!");

    protected final CounterDao counterDao;
    protected final TimeTaker timeTaker;
    protected final World world;

    protected final Narrator n;

    private final boolean initialSchonBegruesstMitSC;

    /**
     * Constructor for {@link AbstractTalkingComp}.
     */
    protected AbstractTalkingComp(final GameObjectId gameObjectId,
                                  final AvDatabase db,
                                  final TimeTaker timeTaker, final Narrator n,
                                  final World world,
                                  final boolean initialSchonBegruesstMitSC) {
        super(gameObjectId, db.talkingDao());
        counterDao = db.counterDao();
        this.n = n;
        this.timeTaker = timeTaker;
        this.world = world;
        this.initialSchonBegruesstMitSC = initialSchonBegruesstMitSC;
    }

    public List<SCTalkAction> getSCConversationSteps() {
        final ImmutableList.Builder<SCTalkAction> res =
                ImmutableList.builder();

        for (final SCTalkAction step : getSCTalkActionsWithoutCheckingConditions()) {
            if (step.isPossible()) {
                res.add(step);
            }
        }

        return res.build();
    }

    protected abstract Iterable<? extends SCTalkAction>
    getSCTalkActionsWithoutCheckingConditions();

    @Override
    @NonNull
    protected TalkingPCD createInitialState() {
        return new TalkingPCD(getGameObjectId(), null, true,
                initialSchonBegruesstMitSC);
    }

    public void updateSchonBegruesstMitSCOnLeave(
            final ILocatableGO locatable, final ILocationGO from,
            @Nullable final ILocationGO to) {
        if (!LocationSystem.haveSameOuterMostLocation(from, to) &&
                (locatable.is(SPIELER_CHARAKTER) || locatable.is(getGameObjectId())) &&
                !isTalkingTo(SPIELER_CHARAKTER)) {
            // SC und das ITalkingBeing  verlassen einander. Ab jetzt können sie
            // einander wieder begrüßen.
            setSchonBegruesstMitSC(false);
        }
    }

    public void setTalkingTo(@Nullable final GameObjectId talkingToId) {
        if (talkingToId == null) {
            talkerBeendetGespraech();
            return;
        }

        setTalkingTo((ITalkerGO<?>) load(talkingToId));
    }

    public void setTalkingTo(@Nullable final ITalkerGO<?> newOtherTalker) {
        @Nullable final GameObjectId newTalkingToId =
                newOtherTalker != null ? newOtherTalker.getId() : null;

        if (getGameObjectId().equals(newTalkingToId)) {
            throw new IllegalStateException("A game object cannot talk to itself.");
        }

        @Nullable final GameObjectId oldTalkingToId = getTalkingToId();
        if (Objects.equals(oldTalkingToId, newTalkingToId)) {
            return;
        }

        if (oldTalkingToId != null) {
            ((ITalkerGO<?>) loadRequired(oldTalkingToId)).talkingComp()
                    .unsetTalkingTo(false);
        }

        requirePcd().setTalkingToId(newTalkingToId);

        if (newOtherTalker != null &&
                newOtherTalker.is(SPIELER_CHARAKTER) &&
                !getGameObjectId().equals(SPIELER_CHARAKTER)) {
            setSchonBegruesstMitSC(true);
        }

        if (newOtherTalker != null) {
            newOtherTalker.talkingComp().setTalkingTo(getGameObjectId());
        }
    }

    @Override
    public void talkerBeendetGespraech() {
        unsetTalkingTo(true);
    }

    @Override
    public void gespraechspartnerBeendetGespraech() {
        unsetTalkingTo(false);
    }

    /**
     * Setzt den Gesprächspartner auf <code>null</code>.
     */
    @SuppressWarnings("WeakerAccess")
    public void unsetTalkingTo(final boolean esIstDerTalkerDerDasGespraechBeendet) {
        @Nullable final ITalkerGO<?> talkingTo = getTalkingTo();
        requireNonNull(requirePcd())
                .setTalkerHatletztesGespraechSelbstBeendet(esIstDerTalkerDerDasGespraechBeendet);

        if (talkingTo == null) {
            return;
        }

        if (talkingTo.is(SPIELER_CHARAKTER)) {
            setSchonBegruesstMitSC(true);
        }

        requirePcd().setTalkingToId(null);
        talkingTo.talkingComp().unsetTalkingTo(!esIstDerTalkerDerDasGespraechBeendet);
    }

    @Override
    public boolean isInConversation() {
        return getTalkingTo() != null;
    }

    public boolean isTalkingTo(final @NonNull ITalkerGO<?> other) {
        return isTalkingTo(other.getId());
    }

    public boolean isTalkingTo(final GameObjectId otherId) {
        return Objects.equals(getTalkingToId(), otherId);
    }

    @Nullable
    @VisibleForTesting
    public ITalkerGO<?> getTalkingTo() {
        @Nullable final GameObjectId talkingToId = getTalkingToId();
        return load(talkingToId);
    }

    @Nullable
    private GameObjectId getTalkingToId() {
        return requirePcd().getTalkingToId();
    }

    public boolean isTalkerHatLetztesGespraechSelbstBeendet() {
        return requirePcd().isTalkerHatletztesGespraechSelbstBeendet();
    }

    /**
     * Gibt alternative Begrüßungen zurück, jeweils beginnend mit Kleinbuchstaben
     * und ohne Satzschlusszeichen
     */
    protected ImmutableList<String> altBegruessungen() {
        return ImmutableList.<String>builder()
                .addAll(timeTaker.now().getTageszeit().altSpTagezeitabhaengigeBegruessungen())
                .addAll(TAGESZEITUNABHAENGIE_BEGRUESSUNGEN)
                .build();
    }

    @Override
    public void setSchonBegruesstMitSC(final boolean schonBegruesstMitSC) {
        if (getGameObjectId() == SPIELER_CHARAKTER) {
            throw new IllegalStateException("setSchonBegruesstMitSC() für SPIELER_CHARAKTER - "
                    + "ergibt keinen Sinn");
        }

        requirePcd().setSchonBegruesstMitSC(schonBegruesstMitSC);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isSchonBegruesstMitSC() {
        return requirePcd().isSchonBegruesstMitSC();
    }

    /**
     * Gibt alternative Verabschiedungen zurück, jeweils beginnend mit Kleinbuchstaben
     * und ohne Satzschlusszeichen
     */
    protected ImmutableList<String> altVerabschiedungen() {
        return ImmutableList.<String>builder()
                .addAll(timeTaker.now().getTageszeit().altSpTagezeitabhaengigeVerabschiedungen())
                .addAll(TAGESZEITUNABHAENGIE_VERABSCHIEDUNGEN)
                .build();
    }

    @Override
    public World getWorld() {
        return world;
    }
}
