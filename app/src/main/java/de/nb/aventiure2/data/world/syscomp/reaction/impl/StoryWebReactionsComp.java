package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.StoryWebComp;
import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobject.World.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;
import static de.nb.aventiure2.data.world.gameobject.World.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Reagiert auf die Aktionen des SCs und managet dabei die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Im Wesentlichen gibt es drei Reaktionen:
 * <ul>
 * <li>Der Spieler erhält einen Tipp.
 * <li>Eine neue Story (ein neues Märchen) wird begonnen.
 * <li>(Es passiert nichts.)
 * </ul>
 */
public class StoryWebReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, IStateChangedReactions, ISCActionReactions {
    private final StoryWebComp storyWebComp;

    public StoryWebReactionsComp(final AvDatabase db, final World world,
                                 final StoryWebComp storyWebComp) {
        super(STORY_WEB, db, world);
        this.storyWebComp = storyWebComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable, final ILocationGO from,
                              @Nullable final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(FROSCHPRINZ, locatable)) {
            return onFroschprinzRecLeave(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onFroschprinzRecLeave(final ILocationGO from,
                                             @Nullable final ILocationGO to) {
        final IHasStateGO<FroschprinzState> froschprinz =
                (IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ);
        if (froschprinz.stateComp().hasState(
                FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN) &&
                to == null) {
            reachStoryNode(FroschkoenigStoryNode.PRINZ_IST_WEGGEFAHREN);
            return noTime();
        }

        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                              final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        if (!(locatable instanceof ILivingBeingGO) &&
                !world.isOrHasRecursiveLocation(from, UNTEN_IM_BRUNNEN) &&
                world.isOrHasRecursiveLocation(to, UNTEN_IM_BRUNNEN)) {
            reachStoryNode(FroschkoenigStoryNode.ETWAS_IM_BRUNNEN_VERLOREN);
            return noTime();
        }

        if (!(locatable instanceof ILivingBeingGO) &&
                world.isOrHasRecursiveLocation(from, UNTEN_IM_BRUNNEN) &&
                world.isOrHasRecursiveLocation(to, IM_WALD_BEIM_BRUNNEN)) {
            reachStoryNode(FroschkoenigStoryNode.FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT);
            return noTime();
        }

        // Rapuzels Zauberin hat einen anderen Ort erreicht -
        // oder ein Container, der sie (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(RAPUNZELS_ZAUBERIN, locatable)) {
            return onRapunzelsZauberinRecEnterAusserFromOrToUntenImBrunnen(from, to);
        }

        // Die goldene Kugel hat einen anderen Ort erreicht -
        // oder ein Container, der die Goldene Kugel (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(GOLDENE_KUGEL, locatable)) {
            return onGoldeneKugelRecEnterAusserFromOrToUntenImBrunnen(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        final ILocatableGO rapunzelsZauberin = (ILocatableGO) world.load(RAPUNZELS_ZAUBERIN);
        if (rapunzelsZauberin.locationComp().hasSameUpperMostLocationAs(to) &&
                !rapunzelsZauberin.locationComp().hasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_AUF_TURM_WEG_GETROFFEN);
        }

        if (to.is(IM_WALD_BEIM_BRUNNEN) &&
                ((ILocatableGO) world.load(GOLDENE_KUGEL)).locationComp()
                        .hasRecursiveLocation(SPIELER_CHARAKTER)) {
            reachStoryNode(FroschkoenigStoryNode.MIT_KUGEL_ZUM_BRUNNEN_GEGANGEN);
            return noTime();
        }

        if (to.is(
                DRAUSSEN_VOR_DEM_SCHLOSS,
                SCHLOSS_VORHALLE,
                SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(SchlossfestState.BEGONNEN)) {
            reachStoryNode(FroschkoenigStoryNode.ZUM_SCHLOSSFEST_GEGANGEN);
            return noTime();
        }

        if (to.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            reachStoryNode(FroschkoenigStoryNode.BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT);
            return noTime();
        }

        if (world.isOrHasRecursiveLocation(to, VOR_DEM_ALTEN_TURM)) {
            reachStoryNode(RapunzelStoryNode.TURM_GEFUNDEN);

            if (((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp()
                    .hasState(RapunzelState.SINGEND)) {
                reachStoryNode(RapunzelStoryNode.RAPUNZEL_SINGEN_GEHOERT);
            }

            return noTime();
        }

        return noTime();
    }

    /**
     * Rapunzels Zauberin hat <code>to</code> erreicht - oder ein Container, der sie
     * (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    private AvTimeSpan onRapunzelsZauberinRecEnterAusserFromOrToUntenImBrunnen(
            @Nullable final ILocationGO from,
            final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            reachStoryNode(FroschkoenigStoryNode.KUGEL_GENOMMEN);
            return noTime();
        }

        if (loadSC().locationComp().hasSameUpperMostLocationAs(to) &&
                !world.isOrHasRecursiveLocation(to, DRAUSSEN_VOR_DEM_SCHLOSS)) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_AUF_TURM_WEG_GETROFFEN);
        }

        return noTime();
    }

    /**
     * Die Goldene Kugel hat <code>to</code> erreicht - oder ein Container, der die
     * Goldene Kugel (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    private AvTimeSpan onGoldeneKugelRecEnterAusserFromOrToUntenImBrunnen(
            @Nullable final ILocationGO from,
            final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            reachStoryNode(FroschkoenigStoryNode.KUGEL_GENOMMEN);
            return noTime();
        }

        return noTime();
    }

    @Override
    public AvTimeSpan onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                                     final Enum<?> newState) {
        if (gameObject.is(FROSCHPRINZ)) {
            return onFroschprinzStateChanged(
                    (FroschprinzState) oldState, (FroschprinzState) newState
            );
        }

        if (gameObject.is(RAPUNZEL)) {
            return onRapunzelStateChanged(
                    (RapunzelState) oldState, (RapunzelState) newState
            );
        }

        if (gameObject.is(SCHLOSSFEST)) {
            return onSchlossfestStateChanged(
                    (SchlossfestState) oldState, (SchlossfestState) newState);
        }

        return noTime();
    }

    private AvTimeSpan onFroschprinzStateChanged(final FroschprinzState oldState,
                                                 final FroschprinzState newState) {
        if (newState == FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE) {
            reachStoryNode(FroschkoenigStoryNode.PRINZ_IST_ERLOEST);
            return noTime();
        }

        return noTime();
    }

    private AvTimeSpan onRapunzelStateChanged(final RapunzelState oldState,
                                              final RapunzelState newState) {
        if (newState == RapunzelState.SINGEND &&
                loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)
            // STORY und der Spieler war wach oder ist rechtzeitig aufgewacht...
        ) {
            reachStoryNode(RapunzelStoryNode.RAPUNZEL_SINGEN_GEHOERT);
            return noTime();
        }

        if (newState == RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN &&
                loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)
            // STORY und der Spieler war wach oder ist rechtzeitig aufgewacht...
        ) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET);
            return noTime();
        }

        return noTime();
    }

    private AvTimeSpan onSchlossfestStateChanged(final SchlossfestState oldState,
                                                 final SchlossfestState newState) {
        if (newState == SchlossfestState.BEGONNEN &&
                loadSC().locationComp().hasRecursiveLocation(
                        DRAUSSEN_VOR_DEM_SCHLOSS, SCHLOSS_VORHALLE,
                        SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            reachStoryNode(FroschkoenigStoryNode.ZUM_SCHLOSSFEST_GEGANGEN);
            return noTime();
        }

        return noTime();
    }

    private void reachStoryNode(final IStoryNode storyNode) {
        storyWebComp.reachStoryNode(storyNode);
    }

    @Override
    public AvTimeSpan afterScActionAndFirstWorldUpdate() {
        return storyWebComp.narrateAndDoHintActionIfAny();
    }
}
