package de.nb.aventiure2.data.world.gameobjects;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.alive.AliveComp;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.FroschprinzReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlosswacheReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.StateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;

import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.WARTET_AUF_SC_BEIM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.WILL_BEIM_SCHLOSSFEST_ZUSAMMEN_ESSEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectStateList.sl;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

/**
 * A factory for special {@link GameObject}s: Creatures.
 */
class CreatureFactory {
    private final AvDatabase db;
    private final GameObjectService gos;

    CreatureFactory(final AvDatabase db, final GameObjectService gos) {
        this.db = db;
        this.gos = gos;
    }

    GameObject createSchlosswache() {
        final StateComp stateComp =
                new StateComp(SCHLOSSWACHE, db, sl(UNAUFFAELLIG, AUFMERKSAM));
        final DescriptionComp descriptionComp =
                new DescriptionComp(SCHLOSSWACHE,
                        np(F, "eine Schlosswache mit langer Hellebarde",
                                "einer Schlosswache mit langer Hellebarde"),
                        np(F, "die Schlosswache mit ihrer langen Hellebarde",
                                "der Schlosswache mit ihrer langen Hellebarde"),
                        np(F, "die Schlosswache",
                                "der Schlosswache"));
        final LocationComp locationComp =
                new LocationComp(SCHLOSSWACHE, db, gos, SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS,
                        false);

        return new ReactionsCreature(SCHLOSSWACHE,
                descriptionComp, locationComp, stateComp,
                new SchlosswacheReactionsComp(db, gos, descriptionComp, stateComp, locationComp));
    }

    GameObject createFroschprinz() {
        final StateComp stateComp =
                new StateComp(FROSCHPRINZ, db, sl(UNAUFFAELLIG, HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                        HAT_NACH_BELOHNUNG_GEFRAGT, HAT_FORDERUNG_GESTELLT,
                        AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN,
                        ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                        AUF_DEM_WEG_ZUM_SCHLOSSFEST,
                        WARTET_AUF_SC_BEIM_SCHLOSSFEST,
                        HAT_HOCHHEBEN_GEFORDERT,
                        WILL_BEIM_SCHLOSSFEST_ZUSAMMEN_ESSEN));
        final DescriptionComp descriptionComp =
                new DescriptionComp(FROSCHPRINZ, np(M, "ein dicker, hässlicher Frosch",
                        "einem dicken, hässlichen Frosch",
                        "einen dicken, hässlichen Frosch"), np(M, "der hässliche Frosch",
                        "dem hässlichen Frosch",
                        "den hässlichen Frosch"),
                        np(M, "der Frosch",
                                "dem Frosch",
                                "den Frosch"));
        final LocationComp locationComp =
                new LocationComp(FROSCHPRINZ, db, gos, IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD,
                        true);
        final FroschprinzTalkingComp talkingComp =
                new FroschprinzTalkingComp(db, gos, descriptionComp, stateComp);
        return new TalkingReactionsCreature(FROSCHPRINZ,
                descriptionComp,
                locationComp,
                stateComp,
                talkingComp,
                new FroschprinzReactionsComp(db, gos,
                        descriptionComp, talkingComp, stateComp, locationComp));
    }

    private static class BasicCreature extends GameObject
            implements IDescribableGO, IHasStateGO, ILocatableGO, ILivingBeingGO {
        private final DescriptionComp descriptionComp;
        private final LocationComp locationComp;
        private final StateComp stateComp;
        private final AliveComp alive;

        private BasicCreature(final GameObjectId id,
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

        @NonNull
        @Override
        public DescriptionComp descriptionComp() {
            return descriptionComp;
        }

        @Nonnull
        @Override
        public LocationComp locationComp() {
            return locationComp;
        }

        @NonNull
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

    private static class ReactionsCreature extends BasicCreature
            implements IResponder {
        private final AbstractReactionsComp reactionsComp;

        private ReactionsCreature(final GameObjectId id,
                                  final DescriptionComp descriptionComp,
                                  final LocationComp locationComp,
                                  final StateComp stateComp,
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

    private static class TalkingReactionsCreature extends ReactionsCreature
            implements ITalkerGO {
        private final AbstractTalkingComp talkingComp;

        TalkingReactionsCreature(final GameObjectId id,
                                 final DescriptionComp descriptionComp,
                                 final LocationComp locationComp,
                                 final StateComp stateComp,
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
