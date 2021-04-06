package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IKnownChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.StoryWebComp;
import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Reagiert auf die Aktionen des SCs und managet dabei die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Im Wesentlichen gibt es drei Reaktionen:
 * <ul>
 * <li>Der Spieler erhält einen Tipp.
 * <li>Eine neue Story (ein neues Märchen) wird begonnen.
 * <li>(Es passiert nichts.)
 * </ul>
 */
@SuppressWarnings({"UnnecessaryReturnStatement", "unchecked"})
public class StoryWebReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, IStateChangedReactions,
        IKnownChangedReactions, ISCActionReactions {
    private final CounterDao counterDao;
    private final StoryWebComp storyWebComp;

    public StoryWebReactionsComp(final CounterDao counterDao,
                                 final Narrator n,
                                 final World world,
                                 final StoryWebComp storyWebComp) {
        super(STORY_WEB, n, world);
        this.counterDao = counterDao;
        this.storyWebComp = storyWebComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCLeave(from, to);
            return;
        }

        if (world.isOrHasRecursiveLocation(FROSCHPRINZ, locatable)) {
            onFroschprinzRecLeave(to);
            return;
        }
    }

    private void onSCLeave(final ILocationGO from, @Nullable final ILocationGO to) {
        if (loadSC().memoryComp().isKnown(SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT)
                && world.isOrHasRecursiveLocation(from, OBEN_IM_ALTEN_TURM)
                && !world.isOrHasRecursiveLocation(to, OBEN_IM_ALTEN_TURM)) {
            reachStoryNode(RapunzelStoryNode.TURMZIMMER_VERLASSEN_UM_RAPUNZEL_ZU_BEFREIEN);
        }
    }

    private void onFroschprinzRecLeave(@Nullable final ILocationGO to) {
        final IHasStateGO<FroschprinzState> froschprinz =
                (IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ);
        if (froschprinz.stateComp().hasState(
                FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN) &&
                to == null) {
            reachStoryNode(FroschkoenigStoryNode.PRINZ_IST_WEGGEFAHREN);
            return;
        }
    }

    @Override
    public boolean verbirgtSichVorEintreffendemSC() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(to);
            return;
        }

        if (!(locatable instanceof ILivingBeingGO) &&
                !world.isOrHasRecursiveLocation(from, UNTEN_IM_BRUNNEN) &&
                world.isOrHasRecursiveLocation(to, UNTEN_IM_BRUNNEN)) {
            reachStoryNode(FroschkoenigStoryNode.ETWAS_IM_BRUNNEN_VERLOREN);
            return;
        }

        if (!(locatable instanceof ILivingBeingGO) &&
                world.isOrHasRecursiveLocation(from, UNTEN_IM_BRUNNEN) &&
                world.isOrHasRecursiveLocation(to, IM_WALD_BEIM_BRUNNEN)) {
            reachStoryNode(FroschkoenigStoryNode.FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT);
            // Für den nächsten Schritt in der Geschichte muss man die Nacht durchbringen
            // und das kann eine Zeitlang dauern. Daher "advancen" wir hier die
            // Rapunzel-Story (sofern nicht ohnehin schon geschehen).
            RapunzelStoryNode.ensureAdvancedToZauberinMachtRapunzelbesuche(counterDao, world);
            return;
        }

        // Rapuzels Zauberin hat einen anderen Ort erreicht -
        // oder ein Container, der sie (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(RAPUNZELS_ZAUBERIN, locatable)) {
            onRapunzelsZauberinRecEnterAusserFromOrToUntenImBrunnen(to);
            return;
        }

        // Die goldene Kugel hat einen anderen Ort erreicht -
        // oder ein Container, der die Goldene Kugel (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(GOLDENE_KUGEL, locatable)) {
            onGoldeneKugelRecEnterAusserFromOrToUntenImBrunnen(to);
            return;
        }
    }

    @NonNull
    @Override
    protected SpielerCharakter loadSC() {
        return super.loadSC();
    }

    private void onSCEnter(final ILocationGO to) {
        final ILocatableGO rapunzelsZauberin = (ILocatableGO) world.load(RAPUNZELS_ZAUBERIN);
        if (rapunzelsZauberin.locationComp().hasSameVisibleOuterMostLocationAs(to)) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_AUF_TURM_WEG_GETROFFEN);
        }

        if (to.is(IM_WALD_BEIM_BRUNNEN) &&
                ((ILocatableGO) world.load(GOLDENE_KUGEL)).locationComp()
                        .hasRecursiveLocation(SPIELER_CHARAKTER)) {
            reachStoryNode(FroschkoenigStoryNode.MIT_KUGEL_ZUM_BRUNNEN_GEGANGEN);
        }

        if (to.is(
                DRAUSSEN_VOR_DEM_SCHLOSS,
                SCHLOSS_VORHALLE,
                SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(SchlossfestState.BEGONNEN)) {
            reachStoryNode(FroschkoenigStoryNode.ZUM_SCHLOSSFEST_GEGANGEN);
        }

        if (to.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            reachStoryNode(FroschkoenigStoryNode.BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT);
        }

        if (world.isOrHasRecursiveLocation(to, VOR_DEM_ALTEN_TURM)) {
            reachStoryNode(RapunzelStoryNode.TURM_GEFUNDEN);

            if (((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp()
                    .hasState(RapunzelState.SINGEND)) {
                reachStoryNode(RapunzelStoryNode.RAPUNZEL_SINGEN_GEHOERT);
            }
        }

        if (world.isOrHasRecursiveLocation(to, OBEN_IM_ALTEN_TURM)) {
            reachStoryNode(RapunzelStoryNode.ZU_RAPUNZEL_HINAUFGESTIEGEN);
        }
    }

    /**
     * Rapunzels Zauberin hat <code>to</code> erreicht - oder ein Container, der sie
     * (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    private void onRapunzelsZauberinRecEnterAusserFromOrToUntenImBrunnen(
            final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            reachStoryNode(FroschkoenigStoryNode.KUGEL_GENOMMEN);
            return;
        }

        if (world.hasSameVisibleOuterMostLocationAsSC(to)) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_AUF_TURM_WEG_GETROFFEN);
        }
    }

    /**
     * Die Goldene Kugel hat <code>to</code> erreicht - oder ein Container, der die
     * Goldene Kugel (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    private void onGoldeneKugelRecEnterAusserFromOrToUntenImBrunnen(
            final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            reachStoryNode(FroschkoenigStoryNode.KUGEL_GENOMMEN);
            return;
        }
    }

    @Override
    public void onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                               final Enum<?> newState) {
        if (gameObject.is(FROSCHPRINZ)) {
            onFroschprinzStateChanged(
                    (FroschprinzState) newState
            );
            return;
        }

        if (gameObject.is(SCHLOSSFEST)) {
            onSchlossfestStateChanged(
                    (SchlossfestState) newState);
            return;
        }

        if (gameObject.is(RAPUNZEL)) {
            onRapunzelStateChanged(
                    (RapunzelState) newState
            );
            return;
        }

        if (gameObject.is(RAPUNZELS_ZAUBERIN)) {
            onRapunzelsZauberinStateChanged(
                    (RapunzelsZauberinState) newState
            );
            return;
        }
    }

    private void onFroschprinzStateChanged(final FroschprinzState newState) {
        if (newState == FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE) {
            reachStoryNode(FroschkoenigStoryNode.PRINZ_IST_ERLOEST);
            return;
        }
    }

    private void onRapunzelStateChanged(final RapunzelState newState) {
        if (newState == RapunzelState.SINGEND &&
                loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)
        ) {
            reachStoryNode(RapunzelStoryNode.RAPUNZEL_SINGEN_GEHOERT);
            return;
        }

        if (newState == RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN &&
                loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)
        ) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET);
            return;
        }
    }

    private void onRapunzelsZauberinStateChanged(final RapunzelsZauberinState newState) {
        if (newState == RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH) {
            reachStoryNode(RapunzelStoryNode.ZAUBERIN_MACHT_RAPUNZELBESUCHE);
            return;
        }
    }

    private void onSchlossfestStateChanged(final SchlossfestState newState) {
        if (newState == SchlossfestState.BEGONNEN &&
                loadSC().locationComp().hasRecursiveLocation(
                        DRAUSSEN_VOR_DEM_SCHLOSS, SCHLOSS_VORHALLE,
                        SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            reachStoryNode(FroschkoenigStoryNode.ZUM_SCHLOSSFEST_GEGANGEN);
            return;
        }
    }

    @Override
    public void onKnownChanged(final IHasMemoryGO knower,
                               final GameObjectId knowee,
                               final Known oldKnown,
                               final Known newKnown) {
        if (knower.is(SPIELER_CHARAKTER)) {
            onSCKnownChanged(knowee, newKnown);
            return;
        }
    }

    private void onSCKnownChanged(final GameObjectId knowee,
                                  final Known newKnown) {
        if (knowee.equals(SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT) && newKnown.isKnown()) {
            reachStoryNode(RapunzelStoryNode.RAPUNZEL_RETTUNG_VERSPROCHEN);
            return;
        }
    }

    private void reachStoryNode(final IStoryNode storyNode) {
        storyWebComp.reachStoryNode(storyNode);
    }

    @Override
    public void afterScActionAndFirstWorldUpdate() {
        storyWebComp.narrateAndDoHintActionIfAny();

        // IDEA Stilisierte Zeichnungen?! Eintopf mit Holzlöffel etc. Evtl. Lisi?
        //  Am besten statt einer HintAction merken, dass bald mal eine
        //  Zeichnung schön wäre (als Belohnung fürs Durchhalten), dann Zeichnung einblenden,
        //  wenn wieder ein zur Verfügung steht? Jede Zeichnung nur 1x.
    }
}
