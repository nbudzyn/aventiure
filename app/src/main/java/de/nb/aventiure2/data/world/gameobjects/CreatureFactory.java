package de.nb.aventiure2.data.world.gameobjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.alive.AliveComp;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.GameObjectStateList;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.StateComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.TalkingComp;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * A factory for special {@link GameObject}s: Creatures.
 */
public class CreatureFactory {
    private final AvDatabase db;

    public CreatureFactory(final AvDatabase db) {
        this.db = db;
    }

    public GameObject createBasic(final GameObjectId id,
                                  final Nominalphrase descriptionAtFirstSight,
                                  final Nominalphrase normalDescriptionWhenKnown,
                                  final Nominalphrase shortDescriptionWhenKnown,
                                  final GameObjectStateList states,
                                  @Nullable final GameObjectId initialLocationId,
                                  @Nullable final GameObjectId initialLastLocationId) {
        return new BasicCreature(id,
                new DescriptionComp(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, initialLocationId, initialLastLocationId),
                new StateComp(id, db, states));
    }

    public GameObject createTalking(final GameObjectId id,
                                    final Nominalphrase descriptionAtFirstSight,
                                    final Nominalphrase normalDescriptionWhenKnown,
                                    final Nominalphrase shortDescriptionWhenKnown,
                                    final GameObjectStateList states,
                                    @Nullable final GameObjectId initialLocationId,
                                    @Nullable final GameObjectId initialLastLocationId) {
        return new TalkingCreature(id,
                new DescriptionComp(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, initialLocationId, initialLastLocationId),
                new StateComp(id, db, states),
                new TalkingComp(id, db));
    }

    private static class BasicCreature extends GameObject
            implements IDescribableGO, IHasStateGO, ILocatableGO, ILivingBeingGO {
        private final DescriptionComp descriptionComp;
        private final LocationComp locationComp;
        private final StateComp stateComp;
        private final AliveComp alive;

        public BasicCreature(final GameObjectId id,
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

    private static class TalkingCreature extends BasicCreature
            implements ITalkerGO {
        private final TalkingComp talkingComp;

        public TalkingCreature(final GameObjectId id,
                               final DescriptionComp descriptionComp,
                               final LocationComp locationComp,
                               final StateComp stateComp,
                               final TalkingComp talkingComp) {
            super(id, descriptionComp, locationComp, stateComp);
            // Jede Komponente muss registiert werden!
            this.talkingComp = addComponent(talkingComp);
        }

        @Nonnull
        @Override
        public TalkingComp talkingComp() {
            return talkingComp;
        }
    }
}
