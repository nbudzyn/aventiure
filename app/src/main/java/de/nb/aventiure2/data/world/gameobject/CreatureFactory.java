package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.alive.AliveComp;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.impl.FroschprinzDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
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
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelsZauberinTalkingComp;

import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

/**
 * A factory for special {@link GameObject}s: Creatures.
 */
class CreatureFactory {
    private final AvDatabase db;
    private final World world;

    CreatureFactory(final AvDatabase db, final World world) {
        this.db = db;
        this.world = world;
    }

    GameObject createSchlosswache() {
        final SchlosswacheStateComp stateComp =
                new SchlosswacheStateComp(SCHLOSSWACHE, db, world);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(SCHLOSSWACHE,
                        np(F, INDEF,
                                "Schlosswache mit langer Hellebarde"),
                        np(F, DEF,
                                "Schlosswache mit ihrer langen Hellebarde"),
                        np(F, DEF, "Schlosswache"));
        final LocationComp locationComp =
                new LocationComp(SCHLOSSWACHE, db, world, SCHLOSS_VORHALLE,
                        DRAUSSEN_VOR_DEM_SCHLOSS,
                        false);

        return new ReactionsCreature<>(SCHLOSSWACHE,
                descriptionComp, locationComp, stateComp,
                new SchlosswacheReactionsComp(
                        db, world, world.getLocationSystem(), stateComp,
                        locationComp));
    }

    GameObject createFroschprinz() {
        final FroschprinzStateComp stateComp = new FroschprinzStateComp(db, world);
        final FroschprinzDescriptionComp descriptionComp =
                new FroschprinzDescriptionComp(stateComp);
        final LocationComp locationComp =
                new LocationComp(FROSCHPRINZ, db, world, IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD,
                        true);
        final FroschprinzTalkingComp talkingComp =
                new FroschprinzTalkingComp(db, world, stateComp);
        return new TalkingReactionsCreature<>(FROSCHPRINZ,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new FroschprinzReactionsComp(db, world, stateComp, locationComp));
    }

    GameObject createRapunzel() {
        final RapunzelStateComp stateComp = new RapunzelStateComp(db, world);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(RAPUNZEL,
                        // STORY Beschreibung für Rapunzel
                        np(F, INDEF, "STORY"),
                        np(F, DEF, "STORY"),
                        np(F, DEF, "STORY"));
        final LocationComp locationComp =
                new LocationComp(RAPUNZEL, db, world, OBEN_IM_ALTEN_TURM, VOR_DEM_ALTEN_TURM,
                        false);
        final RapunzelTalkingComp talkingComp =
                new RapunzelTalkingComp(db, world, stateComp);
        return new TalkingReactionsCreature<>(RAPUNZEL,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new RapunzelReactionsComp(db, world, stateComp, locationComp));
    }

    GameObject createRapunzelsZauberin() {
        final RapunzelsZauberinStateComp stateComp = new RapunzelsZauberinStateComp(db, world);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(RAPUNZELS_ZAUBERIN,
                        np(F, INDEF, "magere Frau mit krummer, bis zum Kinn "
                                        + "reichender Nase",
                                "mageren Frau mit krummer, bis zum Kinn "
                                        + "reichender Nase"),
                        np(F, DEF, "magere Frau",
                                "mageren Frau"),
                        np(F, DEF, "Frau"));
        final LocationComp locationComp =
                new LocationComp(RAPUNZELS_ZAUBERIN, db, world,
                        // Muss zum Zustand der Zauberin passen!
                        null, IM_WALD_NAHE_DEM_SCHLOSS,
                        false);
        final MentalModelComp mentalModelComp =
                new MentalModelComp(RAPUNZELS_ZAUBERIN, db, world,
                        // Muss zum Zustand der Zauberin passen!
                        ImmutableMap.of());
        final MovementComp movementComp =
                new MovementComp(RAPUNZELS_ZAUBERIN, db, world,
                        world.getSpatialConnectionSystem(),
                        new RapunzelsZauberinMovementNarrator(db.narrationDao(), world),
                        locationComp,
                        1,
                        // Muss zum Zustand der Zauberin passen!
                        null);
        final RapunzelsZauberinTalkingComp talkingComp =
                new RapunzelsZauberinTalkingComp(db, world, locationComp, stateComp);
        return new MovingTalkingMentalModelReactionsCreature<>(RAPUNZELS_ZAUBERIN,
                descriptionComp,
                locationComp,
                movementComp,
                stateComp,
                talkingComp,
                mentalModelComp,
                new RapunzelsZauberinReactionsComp(db, world,
                        stateComp, locationComp, mentalModelComp, movementComp));
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
            implements IHasMentalModelGO {
        private final MentalModelComp mentalModelComp;

        MovingTalkingMentalModelReactionsCreature(final GameObjectId id,
                                                  final AbstractDescriptionComp descriptionComp,
                                                  final LocationComp locationComp,
                                                  final MovementComp movementComp,
                                                  final AbstractStateComp<S> stateComp,
                                                  final TALKING_COMP talkingComp,
                                                  final MentalModelComp mentalModelComp,
                                                  final AbstractReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, movementComp, stateComp, talkingComp,
                    reactionsComp);
            // Jede Komponente muss registiert werden!
            this.mentalModelComp = addComponent(mentalModelComp);
        }

        @Nonnull
        @Override
        public MentalModelComp mentalModelComp() {
            return mentalModelComp;
        }
    }

}
