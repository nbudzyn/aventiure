package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

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
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.FroschprinzReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp;
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
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
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
                new SchlosswacheStateComp(SCHLOSSWACHE, db);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(SCHLOSSWACHE,
                        np(F, "eine Schlosswache mit langer Hellebarde",
                                "einer Schlosswache mit langer Hellebarde"),
                        np(F, "die Schlosswache mit ihrer langen Hellebarde",
                                "der Schlosswache mit ihrer langen Hellebarde"),
                        np(F, "die Schlosswache",
                                "der Schlosswache"));
        final LocationComp locationComp =
                new LocationComp(SCHLOSSWACHE, db, world, SCHLOSS_VORHALLE,
                        DRAUSSEN_VOR_DEM_SCHLOSS,
                        false);

        return new ReactionsCreature<>(SCHLOSSWACHE,
                descriptionComp, locationComp, stateComp,
                new SchlosswacheReactionsComp(
                        db, world, descriptionComp, stateComp, locationComp));
    }

    GameObject createFroschprinz() {
        final FroschprinzStateComp stateComp = new FroschprinzStateComp(db);
        final FroschprinzDescriptionComp descriptionComp =
                new FroschprinzDescriptionComp(stateComp);
        final LocationComp locationComp =
                new LocationComp(FROSCHPRINZ, db, world, IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD,
                        true);
        final FroschprinzTalkingComp talkingComp =
                new FroschprinzTalkingComp(db, world, descriptionComp, stateComp);
        return new TalkingReactionsCreature<>(FROSCHPRINZ,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new FroschprinzReactionsComp(db, world, descriptionComp, stateComp, locationComp));
    }

    GameObject createRapunzel() {
        final RapunzelStateComp stateComp = new RapunzelStateComp(db);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(RAPUNZEL,
                        // STORY Beschreibung für Rapunzel
                        np(F, "STORY",
                                "STORY"),
                        np(F, "STORY",
                                "STORY"),
                        np(F, "STORY",
                                "STORY"));
        final LocationComp locationComp =
                new LocationComp(RAPUNZEL, db, world, OBEN_IM_ALTEN_TURM, VOR_DEM_ALTEN_TURM,
                        false);
        final RapunzelTalkingComp talkingComp =
                new RapunzelTalkingComp(db, world, descriptionComp, stateComp);
        return new TalkingReactionsCreature<>(RAPUNZEL,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new RapunzelReactionsComp(db, world, descriptionComp, stateComp, locationComp));
    }

    GameObject createRapunzelsZauberin() {
        final RapunzelsZauberinStateComp stateComp = new RapunzelsZauberinStateComp(db);
        final AbstractDescriptionComp descriptionComp =
                new SimpleDescriptionComp(RAPUNZELS_ZAUBERIN,
                        // STORY Beschreibung für Rapunzels Zauberin
                        np(F, "STORY",
                                "STORY"),
                        np(F, "STORY",
                                "STORY"),
                        np(F, "STORY",
                                "STORY"));
        final LocationComp locationComp =
                new LocationComp(RAPUNZELS_ZAUBERIN, db, world,
                        null, VOR_DEM_ALTEN_TURM,
                        false);
        final RapunzelsZauberinTalkingComp talkingComp =
                new RapunzelsZauberinTalkingComp(db, world, descriptionComp, stateComp);
        return new TalkingReactionsCreature<>(RAPUNZELS_ZAUBERIN,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new RapunzelsZauberinReactionsComp(db, world,
                        descriptionComp, stateComp, locationComp));
    }

    private static class BasicCreature<S extends Enum<S>> extends GameObject
            implements IDescribableGO, IHasStateGO<S>, ILocatableGO, ILivingBeingGO {
        private final AbstractDescriptionComp descriptionComp;
        private final LocationComp locationComp;
        private final AbstractStateComp stateComp;
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

    private static class ReactionsCreature<S extends Enum<S>> extends BasicCreature
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

    private static class TalkingReactionsCreature<S extends Enum<S>> extends ReactionsCreature
            implements ITalkerGO {
        private final AbstractTalkingComp talkingComp;

        TalkingReactionsCreature(final GameObjectId id,
                                 final AbstractDescriptionComp descriptionComp,
                                 final LocationComp locationComp,
                                 final AbstractStateComp<S> stateComp,
                                 final AbstractTalkingComp talkingComp,
                                 final AbstractReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, stateComp, reactionsComp);
            // Jede Komponente muss registiert werden!
            this.talkingComp = addComponent(talkingComp);
        }

        @Nonnull
        @Override
        public AbstractTalkingComp talkingComp() {
            return talkingComp;
        }
    }
}
