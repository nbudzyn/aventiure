package de.nb.aventiure2.data.world.entity.creature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.alive.AliveComp;
import de.nb.aventiure2.data.world.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.description.DescriptionComp;
import de.nb.aventiure2.data.world.description.IDescribableGO;
import de.nb.aventiure2.data.world.gameobjectstate.GameObjectStateList;
import de.nb.aventiure2.data.world.gameobjectstate.IHasStateGO;
import de.nb.aventiure2.data.world.gameobjectstate.StateComp;
import de.nb.aventiure2.data.world.location.ILocatableGO;
import de.nb.aventiure2.data.world.location.LocationComp;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.german.base.Nominalphrase.np;

/**
 * A factory for special {@link GameObject}s: Creatures.
 */
public class CreatureFactory {
    private final AvDatabase db;

    public CreatureFactory(final AvDatabase db) {
        this.db = db;
    }

    public GameObject create(final GameObjectId id,
                             final NumerusGenus numerusGenus,
                             final String descriptionAtFirstSightNomDatAkk,
                             final String normalDescriptionWhenKnownNomDatAkk,
                             final String shortDescriptionWhenKnownNomDatAkk,
                             final GameObjectStateList states,
                             @Nullable final GameObjectId initialLocationId) {
        return create(id,
                np(numerusGenus, descriptionAtFirstSightNomDatAkk),
                np(numerusGenus, normalDescriptionWhenKnownNomDatAkk),
                np(numerusGenus, shortDescriptionWhenKnownNomDatAkk),
                states,
                initialLocationId);
    }

    public GameObject create(final GameObjectId id,
                             final Nominalphrase descriptionAtFirstSight,
                             final Nominalphrase normalDescriptionWhenKnown,
                             final Nominalphrase shortDescriptionWhenKnown,
                             final GameObjectStateList states,
                             @Nullable final GameObjectId initialLocationId) {
        return new Creature(id,
                new DescriptionComp(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, initialLocationId),
                new StateComp(id, db, states));
    }

    private static class Creature extends GameObject
            implements IDescribableGO, IHasStateGO, ILocatableGO, ILivingBeingGO {
        private final DescriptionComp descriptionComp;
        private final LocationComp locationComp;
        private final StateComp stateComp;
        private final AliveComp alive;

        public Creature(final GameObjectId id,
                        final DescriptionComp descriptionComp,
                        final LocationComp locationComp,
                        final StateComp stateComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.descriptionComp = addComponent(descriptionComp);
            this.locationComp = addComponent(locationComp);
            this.stateComp = addComponent(stateComp);
            alive = addComponent(new AliveComp(id));
        }

        @Override
        public DescriptionComp descriptionComp() {
            return descriptionComp;
        }

        @Nonnull
        @Override
        public LocationComp locationComp() {
            return locationComp;
        }

        @Override
        public StateComp stateComp() {
            return stateComp;
        }

        @Nonnull
        @Override
        public AliveComp aliveComp() {
            return alive;
        }
    }
}
