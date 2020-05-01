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
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectStateList.sl;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

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

    public GameObject createFroschprinz() {
        final StateComp stateComp =
                new StateComp(FROSCHPRINZ, db, sl(UNAUFFAELLIG, HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                        HAT_NACH_BELOHNUNG_GEFRAGT, HAT_FORDERUNG_GESTELLT,
                        AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN,
                        ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                        AUF_DEM_WEG_ZUM_SCHLOSSFEST,
                        HAT_HOCHHEBEN_GEFORDERT));
        final DescriptionComp descriptionComp =
                new DescriptionComp(FROSCHPRINZ, np(M, "ein dicker, hässlicher Frosch",
                        "einem dicken, hässlichen Frosch",
                        "einen dicken, hässlichen Frosch"), np(M, "der hässliche Frosch",
                        "dem hässlichen Frosch",
                        "den hässlichen Frosch"),
                        np(M, "der Frosch",
                                "dem Frosch",
                                "den Frosch"));
        return new TalkingCreature(FROSCHPRINZ,
                descriptionComp,
                new LocationComp(FROSCHPRINZ, db, IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD),
                stateComp,
                new FroschprinzTalkingComp(db, descriptionComp, stateComp));
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
        private final AbstractTalkingComp talkingComp;

        public TalkingCreature(final GameObjectId id,
                               final DescriptionComp descriptionComp,
                               final LocationComp locationComp,
                               final StateComp stateComp,
                               final AbstractTalkingComp talkingComp) {
            super(id, descriptionComp, locationComp, stateComp);
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
