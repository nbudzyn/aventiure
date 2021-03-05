package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.syscomp.alive.AliveComp;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.impl.FroschprinzDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.MultiDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.RapunzeDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.RapunzelsZauberinDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.feelings.HungerData;
import de.nb.aventiure2.data.world.syscomp.feelings.IFeelingBeingGO;
import de.nb.aventiure2.data.world.syscomp.feelings.MenschlicherMuedigkeitsBiorhythmus;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.feelings.MuedigkeitsData;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.FroschprinzReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlosswacheReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheStateComp;
import de.nb.aventiure2.data.world.syscomp.taking.AbstractTakingComp;
import de.nb.aventiure2.data.world.syscomp.taking.ITakerGO;
import de.nb.aventiure2.data.world.syscomp.taking.impl.RapunzelTakingComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelsZauberinTalkingComp;

import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

/**
 * A factory for special {@link GameObject}s: Creatures.
 */
class CreatureFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    CreatureFactory(final AvDatabase db, final TimeTaker timeTaker,
                    final Narrator n, final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    GameObject createSchlosswache() {
        final SchlosswacheStateComp stateComp =
                new SchlosswacheStateComp(SCHLOSSWACHE, db, timeTaker, n, world);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(SCHLOSSWACHE,
                        np(F, INDEF,
                                "Schlosswache mit langer Hellebarde",
                                SCHLOSSWACHE),
                        np(F, DEF,
                                "Schlosswache mit ihrer "
                                        + "langen Hellebarde",
                                SCHLOSSWACHE),
                        np(F, DEF, "Schlosswache", SCHLOSSWACHE));
        final LocationComp locationComp =
                new LocationComp(SCHLOSSWACHE, db, world, SCHLOSS_VORHALLE,
                        DRAUSSEN_VOR_DEM_SCHLOSS,
                        false);

        return new ReactionsCreature<>(SCHLOSSWACHE,
                descriptionComp, locationComp, stateComp,
                new SchlosswacheReactionsComp(
                        db.counterDao(), n, world, stateComp,
                        locationComp));
    }

    GameObject createFroschprinz() {
        final FroschprinzStateComp stateComp = new FroschprinzStateComp(db, timeTaker, n, world);
        final MultiDescriptionComp descriptionComp =
                new FroschprinzDescriptionComp(stateComp);
        final LocationComp locationComp =
                new LocationComp(FROSCHPRINZ, db, world, IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD,
                        true);
        final FroschprinzTalkingComp talkingComp =
                new FroschprinzTalkingComp(db, timeTaker, n, world,
                        stateComp, false);
        return new TalkingReactionsCreature<>(FROSCHPRINZ,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new FroschprinzReactionsComp(db, n, world, stateComp, locationComp, talkingComp));
    }

    GameObject createRapunzel() {
        final RapunzelStateComp stateComp = new RapunzelStateComp(db, timeTaker, n, world);
        final AbstractDescriptionComp descriptionComp = new RapunzeDescriptionComp(world);
        final LocationComp locationComp =
                new LocationComp(RAPUNZEL, db, world, OBEN_IM_ALTEN_TURM, VOR_DEM_ALTEN_TURM,
                        false);
        final MemoryComp memoryComp =
                new MemoryComp(RAPUNZEL, db, world,
                        createKnownMapForRapunzel());
        final MenschlicherMuedigkeitsBiorhythmus muedigkeitsBiorhythmus =
                new MenschlicherMuedigkeitsBiorhythmus();
        final FeelingsComp feelingsComp =
                new FeelingsComp(RAPUNZEL, db, timeTaker, n,
                        world, null,
                        memoryComp,
                        locationComp, Mood.NEUTRAL,
                        muedigkeitsBiorhythmus,
                        MuedigkeitsData.createFromBiorhythmusFuerMenschen(
                                muedigkeitsBiorhythmus, timeTaker.now()),
                        createInitialHungerDataForRapunzel(),
                        hours(6),
                        createDefaultFeelingsTowardsForRapunzel(),
                        createInitialFeelingsTowardsForRapunzel());
        final RapunzelTalkingComp talkingComp =
                new RapunzelTalkingComp(db, timeTaker, n, world, memoryComp, stateComp,
                        feelingsComp,
                        false);
        final RapunzelReactionsComp reactionsComp =
                new RapunzelReactionsComp(db.counterDao(), timeTaker, n, world, memoryComp,
                        stateComp,
                        locationComp, feelingsComp,
                        talkingComp);
        final RapunzelTakingComp takingComp =
                new RapunzelTakingComp(db, n, world, stateComp, memoryComp, feelingsComp);
        return new TalkingMemoryFeelingsTakingReactionsCreature<>(RAPUNZEL,
                descriptionComp,
                locationComp,
                memoryComp,
                stateComp,
                feelingsComp,
                talkingComp,
                reactionsComp,
                takingComp);
    }

    private static HungerData createInitialHungerDataForRapunzel() {
        return new HungerData(Hunger.SATT,
                new AvDateTime(1, oClock(17)));
    }

    private static ImmutableMap<FeelingTowardsType, Float> createDefaultFeelingsTowardsForRapunzel() {
        return ImmutableMap.of(FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG,
                (float) FeelingIntensity.NEUTRAL);
    }

    private static ImmutableMap<GameObjectId, Map<FeelingTowardsType, Float>>
    createInitialFeelingsTowardsForRapunzel() {
        return ImmutableMap.of(
                RAPUNZELS_ZAUBERIN, ImmutableMap.of(
                        FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG,
                        (float) -FeelingIntensity.DEUTLICH
                )
        );
    }

    private static Map<GameObjectId, Known> createKnownMapForRapunzel() {
        return ImmutableMap.<GameObjectId, Known>builder()
                .put(RAPUNZEL, KNOWN_FROM_LIGHT)
                .put(RAPUNZELS_NAME, KNOWN_FROM_LIGHT)
                .put(RAPUNZELS_GESANG, KNOWN_FROM_LIGHT)
                .put(RAPUNZELS_HAARE, KNOWN_FROM_LIGHT)
                .put(RAPUNZELRUF, KNOWN_FROM_LIGHT)
                .put(RAPUNZELS_ZAUBERIN, KNOWN_FROM_LIGHT)
                .put(RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU,
                        KNOWN_FROM_LIGHT)
                .put(RAPUNZELS_FREIHEITSWUNSCH, KNOWN_FROM_LIGHT)
                .put(OBEN_IM_ALTEN_TURM, KNOWN_FROM_LIGHT)
                .put(BETT_OBEN_IM_ALTEN_TURM, KNOWN_FROM_LIGHT)
                .put(TAGESZEIT, KNOWN_FROM_LIGHT)
                .build();
    }

    GameObject createRapunzelsZauberin() {
        final RapunzelsZauberinStateComp stateComp =
                new RapunzelsZauberinStateComp(db, timeTaker, n, world);
        final AbstractDescriptionComp descriptionComp =
                new RapunzelsZauberinDescriptionComp(world);
        final LocationComp locationComp =
                new LocationComp(RAPUNZELS_ZAUBERIN, db, world,
                        // Muss zum Zustand der Zauberin passen!
                        null, IM_WALD_NAHE_DEM_SCHLOSS,
                        false);
        final MentalModelComp mentalModelComp =
                new MentalModelComp(RAPUNZELS_ZAUBERIN, db, world,
                        // Muss zum Zustand der Zauberin passen!
                        ImmutableMap.of());
        final MenschlicherMuedigkeitsBiorhythmus muedigkeitsBiorhythmus =
                new MenschlicherMuedigkeitsBiorhythmus();
        final FeelingsComp feelingsComp =
                new FeelingsComp(RAPUNZELS_ZAUBERIN, db, timeTaker, n,
                        world, null,
                        null,
                        locationComp, Mood.NEUTRAL,
                        muedigkeitsBiorhythmus,
                        MuedigkeitsData.createFromBiorhythmusFuerMenschen(
                                muedigkeitsBiorhythmus, timeTaker.now()),
                        createInitialHungerDataForRapunzelsZauberin(),
                        hours(6),
                        createDefaultFeelingsTowardsForRapunzelsZauberin(),
                        createInitialFeelingsTowardsForRapunzelsZauberin());
        final MovementComp movementComp =
                new MovementComp(RAPUNZELS_ZAUBERIN, db, world,
                        world.getSpatialConnectionSystem(),
                        new RapunzelsZauberinMovementNarrator(n, world),
                        locationComp,
                        1,
                        // Muss zum Zustand der Zauberin passen!
                        null);
        final RapunzelsZauberinTalkingComp talkingComp =
                new RapunzelsZauberinTalkingComp(
                        db, n, timeTaker, world, locationComp, stateComp,
                        feelingsComp, movementComp, false);
        movementComp.setConversationable(talkingComp);
        return new MovingTalkingMentalModelReactionsCreature<>(RAPUNZELS_ZAUBERIN,
                descriptionComp,
                locationComp,
                movementComp,
                stateComp,
                feelingsComp,
                talkingComp,
                mentalModelComp,
                new RapunzelsZauberinReactionsComp(db.counterDao(), timeTaker, n, world,
                        stateComp, locationComp, mentalModelComp, movementComp,
                        talkingComp));
    }

    private static HungerData createInitialHungerDataForRapunzelsZauberin() {
        return new HungerData(Hunger.SATT,
                new AvDateTime(1, oClock(17)));
    }

    private static ImmutableMap<FeelingTowardsType, Float>
    createDefaultFeelingsTowardsForRapunzelsZauberin() {
        return ImmutableMap.of(FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG,
                (float) -FeelingIntensity.MERKLICH);
    }

    private static ImmutableMap<GameObjectId, Map<FeelingTowardsType, Float>>
    createInitialFeelingsTowardsForRapunzelsZauberin() {
        return ImmutableMap.of(
                RAPUNZEL, ImmutableMap.of(
                        FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG,
                        (float) FeelingIntensity.DEUTLICH
                )
        );
    }


    private static class BasicCreature<S extends Enum<S>> extends GameObject
            implements IDescribableGO, IHasStateGO<S>, ILocatableGO, ILivingBeingGO {
        private final AbstractDescriptionComp descriptionComp;
        private final LocationComp locationComp;
        private final AbstractStateComp<S> stateComp;
        private final AliveComp alive;

        private BasicCreature(final GameObjectId id,
                              final AbstractDescriptionComp descriptionComp,
                              final LocationComp locationComp,
                              final AbstractStateComp<S> stateComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.descriptionComp = addComponent(descriptionComp);
            this.locationComp = addComponent(locationComp);
            this.stateComp = addComponent(stateComp);
            alive = addComponent(new AliveComp(id));
        }

        @NonNull
        @Override
        public AbstractDescriptionComp descriptionComp() {
            return descriptionComp;
        }

        @Nonnull
        @Override
        public LocationComp locationComp() {
            return locationComp;
        }

        @NonNull
        @Override
        public AbstractStateComp<S> stateComp() {
            return stateComp;
        }

        @Nonnull
        @Override
        public AliveComp aliveComp() {
            return alive;
        }
    }

    private static class ReactionsCreature<S extends Enum<S>>
            extends BasicCreature<S>
            implements IResponder {
        private final AbstractReactionsComp reactionsComp;

        private ReactionsCreature(final GameObjectId id,
                                  final AbstractDescriptionComp descriptionComp,
                                  final LocationComp locationComp,
                                  final AbstractStateComp<S> stateComp,
                                  final AbstractReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, stateComp);
            // Jede Komponente muss registiert werden!
            this.reactionsComp = addComponent(reactionsComp);
        }

        @Override
        @NonNull
        public AbstractReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }

    private static class TalkingReactionsCreature<S extends Enum<S>,
            TALKING_COMP extends AbstractTalkingComp>
            extends ReactionsCreature<S>
            implements ITalkerGO<TALKING_COMP> {
        private final TALKING_COMP talkingComp;

        TalkingReactionsCreature(final GameObjectId id,
                                 final AbstractDescriptionComp descriptionComp,
                                 final LocationComp locationComp,
                                 final AbstractStateComp<S> stateComp,
                                 final TALKING_COMP talkingComp,
                                 final AbstractReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, stateComp, reactionsComp);
            // Jede Komponente muss registiert werden!
            this.talkingComp = addComponent(talkingComp);
        }

        @Nonnull
        @Override
        public TALKING_COMP talkingComp() {
            return talkingComp;
        }
    }

    private static class TalkingMemoryFeelingsTakingReactionsCreature<S extends Enum<S>,
            TALKING_COMP extends AbstractTalkingComp,
            TAKING_COMP extends AbstractTakingComp>
            extends TalkingReactionsCreature<S, TALKING_COMP>
            implements IHasMemoryGO, IFeelingBeingGO, ITakerGO<TAKING_COMP> {
        private final MemoryComp memoryComp;
        private final FeelingsComp feelingsComp;
        private final TAKING_COMP takingComp;

        TalkingMemoryFeelingsTakingReactionsCreature(final GameObjectId id,
                                                     final AbstractDescriptionComp descriptionComp,
                                                     final LocationComp locationComp,
                                                     final MemoryComp memoryComp,
                                                     final AbstractStateComp<S> stateComp,
                                                     final FeelingsComp feelingsComp,
                                                     final TALKING_COMP talkingComp,
                                                     final AbstractReactionsComp reactionsComp,
                                                     final TAKING_COMP takingComp) {
            super(id, descriptionComp, locationComp, stateComp, talkingComp, reactionsComp);
            // Jede Komponente muss registiert werden!
            this.memoryComp = addComponent(memoryComp);
            this.feelingsComp = addComponent(feelingsComp);
            this.takingComp = addComponent(takingComp);
        }

        @Nonnull
        @Override
        public MemoryComp memoryComp() {
            return memoryComp;
        }

        @Nonnull
        @Override
        public FeelingsComp feelingsComp() {
            return feelingsComp;
        }

        @Nonnull
        @Override
        public TAKING_COMP takingComp() {
            return takingComp;
        }
    }

    private static class MovingTalkingReactionsCreature<S extends Enum<S>,
            TALKING_COMP extends AbstractTalkingComp>
            extends TalkingReactionsCreature<S, TALKING_COMP>
            implements IMovingGO {
        private final MovementComp movementComp;

        MovingTalkingReactionsCreature(final GameObjectId id,
                                       final AbstractDescriptionComp descriptionComp,
                                       final LocationComp locationComp,
                                       final MovementComp movementComp,
                                       final AbstractStateComp<S> stateComp,
                                       final TALKING_COMP talkingComp,
                                       final AbstractReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, stateComp, talkingComp, reactionsComp);
            // Jede Komponente muss registiert werden!
            this.movementComp = addComponent(movementComp);
        }

        @Nonnull
        @Override
        public MovementComp movementComp() {
            return movementComp;
        }
    }

    private static class MovingTalkingMentalModelReactionsCreature<S extends Enum<S>,
            TALKING_COMP extends AbstractTalkingComp>
            extends MovingTalkingReactionsCreature<S, TALKING_COMP>
            implements IFeelingBeingGO, IHasMentalModelGO {
        private final FeelingsComp feelingsComp;
        private final MentalModelComp mentalModelComp;

        MovingTalkingMentalModelReactionsCreature(final GameObjectId id,
                                                  final AbstractDescriptionComp descriptionComp,
                                                  final LocationComp locationComp,
                                                  final MovementComp movementComp,
                                                  final AbstractStateComp<S> stateComp,
                                                  final FeelingsComp feelingsComp,
                                                  final TALKING_COMP talkingComp,
                                                  final MentalModelComp mentalModelComp,
                                                  final AbstractReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, movementComp, stateComp, talkingComp,
                    reactionsComp);
            // Jede Komponente muss registiert werden!
            this.feelingsComp = addComponent(feelingsComp);
            this.mentalModelComp = addComponent(mentalModelComp);
        }

        @Nonnull
        @Override
        public FeelingsComp feelingsComp() {
            return feelingsComp;
        }

        @Nonnull
        @Override
        public MentalModelComp mentalModelComp() {
            return mentalModelComp;
        }
    }

}
