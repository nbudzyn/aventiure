package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.days;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingsSaetzeUtil.altNachsehenHinterhersehenSaetze;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingsSaetzeUtil.altZusehenSaetze;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp.Counter.ZAUBERIN_KOMMT_AUSSER_DER_REIHE_NACHDEM_MAN_VON_IHR_GESPROCHEN_HAT;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp.Counter.ZAUBERIN_TRIFFT_OBEN_EIN_WAEHREND_SC_VERSTECKT_IST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.BEI_RAPUNZEL_OBEN_IM_TURM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.WARTEZEIT_NACH_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IKnownChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelsZauberinTalkingComp;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

/**
 * "Reaktionen" von Rapunzels Zauberin, z.B. darauf, dass Zeit vergeht
 */
@SuppressWarnings({"UnnecessaryReturnStatement", "GrazieInspection"})
public class RapunzelsZauberinReactionsComp
        extends AbstractDescribableReactionsComp
        implements
        // Reaktionen auf die Bewegungen des SC und anderes Game Objects
        IMovementReactions, IRufReactions, IStateChangedReactions,
        IKnownChangedReactions,
        ITimePassedReactions {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public
    enum Counter {
        ZAUBERIN_TRIFFT_OBEN_EIN_WAEHREND_SC_VERSTECKT_IST,
        ZAUBERIN_KOMMT_AUSSER_DER_REIHE_NACHDEM_MAN_VON_IHR_GESPROCHEN_HAT
    }

    // Vorher ist es der Zauberin für einen Rapunzelbesuch zu früh
    private static final AvTime FRUEHESTE_LOSGEHZEIT_RAPUNZELBESUCH =
            oClock(6, 30);

    // Danach wird es der Zauberin für einen Rapunzelbesuch zu spät
    private static final AvTime SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH =
            oClock(17);

    private static final AvTimeSpan BESUCHSDAUER = mins(31);

    // Wenn die Zauberin zu Hause angekommen ist, dann geht sie nach dieser Zeit
    //  wieder los. Sie sieht ziemlich oft bei Rapunzel vorbei.
    private static final AvTimeSpan MIN_ZEIT_VOR_DEM_NAECHSTEN_LOSGEHEN = mins(47);

    private final TimeTaker timeTaker;

    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;
    private final MentalModelComp mentalModelComp;
    private final MovementComp movementComp;
    private final RapunzelsZauberinTalkingComp talkingComp;

    public RapunzelsZauberinReactionsComp(final CounterDao counterDao,
                                          final TimeTaker timeTaker,
                                          final Narrator n, final World world,
                                          final RapunzelsZauberinStateComp stateComp,
                                          final LocationComp locationComp,
                                          final MentalModelComp mentalModelComp,
                                          final MovementComp movementComp,
                                          final RapunzelsZauberinTalkingComp talkingComp) {
        super(RAPUNZELS_ZAUBERIN, counterDao, n, world);
        this.timeTaker = timeTaker;
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.mentalModelComp = mentalModelComp;
        this.movementComp = movementComp;
        this.talkingComp = talkingComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable,
                        final ILocationGO from,
                        @Nullable final ILocationGO to) {
        talkingComp.updateSchonBegruesstMitSCOnLeave(locatable, from, to);

        // Wenn die Zauberin den SC verlässt, ...
        if (locatable.is(getGameObjectId())) {
            // ...dann weiß sie nicht, wo der SC sich befindet.
            // Einzige Ausnahme:
            // Wenn die Zauberin den SC vor dem Turm verlassen hat, geht sie erst einmal
            // davon aus, dass er wohl dort noch ist, denn das ist eine Sackgasse.
            if (!world.isOrHasRecursiveLocation(from, VOR_DEM_ALTEN_TURM) ||
                    !mentalModelComp.hasAssumedLocation(
                            SPIELER_CHARAKTER,
                            VOR_DEM_ALTEN_TURM,
                            VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                mentalModelComp.unsetAssumedLocation(SPIELER_CHARAKTER);
            }
        } else if (locatable.is(SPIELER_CHARAKTER)) {
            onSCLeave(from, to);
            return;
        }
    }

    private void onSCLeave(final ILocationGO scFrom,
                           @Nullable final ILocationGO scTo) {
        if (    // Der Spieler kommt aus Richtung Brunnen
            // zum "Rapunzel-Abzweig" zurück.
                scFrom.is(ABZWEIG_IM_WALD)
                        && scTo != null && scTo.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            onSCLeave_AbzweigImWald_To_ImWaldNaheDemSchloss();
        } else if (scFrom.is(VOR_DEM_ALTEN_TURM)
                && scTo != null && scTo.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
                && locationComp.hasSameVisibleOuterMostLocationAs(SPIELER_CHARAKTER)) {
            narrateZauberinSiehtSCNach();
        }

        narrateAndDoScTrifftEvtlZauberinImDazwischen(scFrom, scTo);

        if (locationComp.hasSameVisibleOuterMostLocationAs(scFrom)) {
            // Wenn die Zauberin sieht, wie der Spieler weggeht,
            // weiß sie nicht mehr, wo er ist
            if (!locationComp.hasSameVisibleOuterMostLocationAs(scTo)) {
                mentalModelComp.unsetAssumedLocation(SPIELER_CHARAKTER);
            }
            return;
        }
    }

    private void narrateZauberinSiehtSCNach() {
        final SubstantivischePhrase anaph = anaph();
        final AltDescriptionsBuilder alt = alt();
        alt.add(neuerSatz(anaph.nomK(), "schickt dir böse Blicke hinterher"));
        alt.addAll(altNachsehenHinterhersehenSaetze(anaph, duSc())
        );
        alt.addAll(altZusehenSaetze(anaph, duSc(),
                mapToList(
                        ImmutableList.of("ärgerlich", "verdrossen"), AdvAngabeSkopusVerbAllg::new))
        );

        n.narrateAlt(alt.schonLaenger(), NO_TIME);
    }

    private void narrateAndDoScTrifftEvtlZauberinImDazwischen(final ILocationGO scFrom,
                                                              @Nullable final
                                                              ILocationGO scTo) {
        if (scTo != null && movementComp.isMoving()
                && locationComp.getLocationId() == null) {
            // Zauberin ist in Bewegung - und gerade im "Dazwischen"

            final boolean spielerHatZauberinUeberholt =
                    LocationSystem.haveSameOuterMostLocation(
                            scFrom, movementComp.getCurrentStepFrom()) &&
                            LocationSystem.haveSameOuterMostLocation(
                                    scTo, movementComp.getCurrentStepTo());

            final boolean spielerUndZauberinKommenEinanderEntgegen =
                    LocationSystem.haveSameOuterMostLocation(
                            scFrom, movementComp.getCurrentStepTo()) &&
                            LocationSystem.haveSameOuterMostLocation(
                                    scTo, movementComp.getCurrentStepFrom());

            if (spielerHatZauberinUeberholt || spielerUndZauberinKommenEinanderEntgegen) {
                // Zauberin im "dazwischen" getroffen
                movementComp.narrateAndDoScTrifftMovingGOImDazwischen(scFrom, scTo);
            }
        }
    }

    private void onSCLeave_AbzweigImWald_To_ImWaldNaheDemSchloss() {
        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)
                && stateComp.hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE,
                VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH)
                && umstaendeGeeignetFuerRapunzelbesuch(timeTaker.now())) {
            // Automatisch die Zauberin loslaufen lassen,
            //  sodass der SC die Zauberin auf jeden Fall
            //  auf der Kreuzung trifft

            locationComp.narrateAndSetLocation(
                    // Zauberin ist auf einmal draußen vor dem Schloss
                    // (wer weiß, wo sie herkommt)
                    DRAUSSEN_VOR_DEM_SCHLOSS,
                    () -> stateComp.narrateAndSetState(AUF_DEM_WEG_ZU_RAPUNZEL));

            // Zauberin geht Richtung Turm. Der SC soll die Zauberin _jetzt_
            // auf der Kreuzung treffen - keine Zeit für den ersten Schritt der
            // Zauberin einplanen.
            movementComp.startMovement(timeTaker.now(),
                    VOR_DEM_ALTEN_TURM, true);
        }
    }

    @Override
    public boolean isVorScVerborgen() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO scFrom, final ILocationGO scTo) {
        // Wenn die Zauberin sieht, wie sich der Spieler in den Schatten der
        // Bäume setzt, weiß sie, dass er dort ist.
        if (locationComp.hasSameVisibleOuterMostLocationAs(SPIELER_CHARAKTER) &&
                scFrom != null &&
                scFrom.is(VOR_DEM_ALTEN_TURM) &&
                scTo.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            mentalModelComp.setAssumedLocation(SPIELER_CHARAKTER, scTo);
        }

        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocationId())) {
            // SC und Zauberin sind nicht am gleichen Ort

            if (world.isOrHasRecursiveLocation(scFrom, VOR_DEM_ALTEN_TURM) &&
                    !world.isOrHasRecursiveLocation(scTo, VOR_DEM_ALTEN_TURM)) {
                // SC hat den Platz vor dem Turm verlassen.
                // Falls die Zauberin noch vor dem Turm wartet:
                if (stateComp.hasState(AUF_DEM_WEG_ZU_RAPUNZEL) &&
                        locationComp.hasLocation(VOR_DEM_ALTEN_TURM)) {
                    zauberinRuftRapunzelspruchUndRapunzelReagiert();
                    return;
                }
            }

            return;
        }

        if (LocationSystem.isOrHasRecursiveLocation(scTo, scFrom) || LocationSystem
                .isOrHasRecursiveLocation(scFrom, scTo)) {
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen,
            // wieder vom Tisch herabgestiegen o.Ä.,
            // die Zauberin wurde bereits beschrieben.
            if (world.isOrHasRecursiveLocation(scTo, VOR_DEM_ALTEN_TURM) &&
                    stateComp.hasState(AUF_DEM_RUECKWEG_VON_RAPUNZEL)) {
                // Die Zauberin merkt, dass der SC unter den Bäumen verborgen war.
                // Sie ahnt, dass er sie wohl beobachtet hat.
                talkingComp.zauberinZaubertVergessenszauber();
                return;
            }

            if (world.isOrHasRecursiveLocation(scTo, OBEN_IM_ALTEN_TURM)) {
                // Der Spieler war unter dem Bett verborgen und kommt
                // hervor, während die Zauberin noch da ist.
                talkingComp.zauberinZaubertVergessenszauber();
                return;
            }
        }

        if (!movementComp.isMoving()) {
            if (isVorScVerborgen()
                    || !locationComp.hasVisiblyRecursiveLocation(scTo.getId())
                    || !world.shouldBeDescribedAfterScMovement(scFrom, scTo, getGameObjectId())) {
                return;
            }

            movementComp.narrateAndDoScTrifftStehendesMovingGOInTo(scTo);
        }
    }

    @Override
    public void onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        // Hört die Zauberin den Ruf?
        if (!locationComp.hasSameOuterMostLocationAs(rufer) &&
                (!rufer.locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) ||
                        !locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM))) {
            return;
        }

        if (ruftyp == Ruftyp.LASS_DEIN_HAAR_HERUNTER) {
            onRapunzelruf(rufer);
            return;
        }
    }

    private void onRapunzelruf(final ILocatableGO rufer) {
        // Zauberin weiß jetzt, wo der Rufer ist
        mentalModelComp.setAssumedLocationToActual(rufer);

        if (!rufer.is(SPIELER_CHARAKTER)) {
            return;
        }

        talkingComp.zauberinZaubertVergessenszauber();
    }

    @Override
    public <S extends Enum<S>> void onStateChanged(final IHasStateGO<S> gameObject,
                                                   final S oldState,
                                                   final S newState) {
        if (!gameObject.is(RAPUNZEL)) {
            return;
        }

        if (stateComp.hasState(AUF_DEM_WEG_ZU_RAPUNZEL)) {
            onRapunzelStateChangedAufDemWegZuRapunzel(
                    (RapunzelState) newState);
            return;
        }

        if (stateComp.hasState(AUF_DEM_RUECKWEG_VON_RAPUNZEL)) {
            onRapunzelStateChangedAufDemRueckwegVonRapunzel((RapunzelState) newState);
            return;
        }
    }

    private void onRapunzelStateChangedAufDemWegZuRapunzel(
            final RapunzelState newState
    ) {
        if (!locationComp.hasLocation(VOR_DEM_ALTEN_TURM)) {
            return;
        }

        if (newState == RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN) {
            zauberinSteigtAnDenHaarenZuRapunzelHinauf();
            return;
        }
    }

    private void zauberinSteigtAnDenHaarenZuRapunzelHinauf() {
        if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            zauberinSteigtAnDenHaarenZuRapunzelHinauf_SCObenImTurm();
            return;
        }

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final EinzelneSubstantivischePhrase desc = getDescription(true);
            n.narrate(
                    du("siehst",
                            desc.akkK(), "an den Haarflechten hinaufsteigen")
                            .timed(mins(4)));

        }

        locationComp.narrateAndSetLocation(OBEN_IM_ALTEN_TURM);
        stateComp.narrateAndSetState(BEI_RAPUNZEL_OBEN_IM_TURM);
    }

    private void zauberinSteigtAnDenHaarenZuRapunzelHinauf_SCObenImTurm() {
        if (!loadSC().locationComp().hasRecursiveLocation(BETT_OBEN_IM_ALTEN_TURM)) {
            // Sonderfall: Zauberin steigt hoch, während der SC oben im Turm
            // ist und sich nicht versteckt hat
            talkingComp.zauberinZaubertVergessenszauber();
            return;
        }

        n.narrateAlt(secs(15), ZAUBERIN_TRIFFT_OBEN_EIN_WAEHREND_SC_VERSTECKT_IST,
                du("hörst",
                        anaph(false).akkK(),
                        "durchs Fenster hineinsteigen"),
                neuerSatz("keuchend steigt",
                        anaph(false).nomK(),
                        "durchs Fenster hinein"));

        locationComp.narrateAndSetLocation(OBEN_IM_ALTEN_TURM);
        stateComp.narrateAndSetState(BEI_RAPUNZEL_OBEN_IM_TURM);

        if (counterDao.get(ZAUBERIN_TRIFFT_OBEN_EIN_WAEHREND_SC_VERSTECKT_IST) == 1) {
            final SubstantivischePhrase anaph = anaph();
            n.narrate(neuerSatz(
                    anaph.nomK(), "begrüßt",
                    world.getDescription(RAPUNZEL).akkK(),
                    ", dann ist", anaph.persPron().nomK(),
                    "auf einmal still. „Wonach riecht es hier?“, fragt",
                    anaph(false).nomK(), "mit scharfer Stimme")
                    .timed(secs(20)));
            ((RapunzelReactionsComp) loadRapunzel().reactionsComp())
                    .reagiertAufFrageVonZauberinNachGeruch();
        }
    }

    private void onRapunzelStateChangedAufDemRueckwegVonRapunzel(
            final RapunzelState newState) {
        if (!locationComp.hasLocation(OBEN_IM_ALTEN_TURM)) {
            return;
        }

        if (newState == RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN) {
            zauberinSteigtAnDenHaarenHerab();
            return;
        }
    }

    private void zauberinSteigtAnDenHaarenHerab() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            n.narrate(
                    du("siehst", ", wie", getDescription().nomK(),
                            "an den Haaren herabsteigt")
                            .timed(mins(1)).komma());

            if (!loadSC().locationComp().hasRecursiveLocation(
                    VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
                    || mentalModelComp.hasAssumedLocation(SPIELER_CHARAKTER,
                    VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                locationComp.narrateAndSetLocation(VOR_DEM_ALTEN_TURM);

                talkingComp.zauberinZaubertVergessenszauber();
                return;
            }

            n.narrate(neuerSatz(anaph(true).nomK(),
                    "hat dich nicht bemerkt").timed(NO_TIME));
        } else if (loadSC().locationComp().hasRecursiveLocation(BETT_OBEN_IM_ALTEN_TURM)) {
            n.narrate(neuerSatz("Endlich verabschiedet sich",
                    getDescription().nomK())
                    .timed(secs(30)).komma());

            loadRapunzel().talkingComp().narrateZauberinIstGegangen();
        }

        locationComp.narrateAndSetLocation(VOR_DEM_ALTEN_TURM);
        movementComp.startMovement(timeTaker.now(), ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN);
    }

    @Override
    public void onKnownChanged(final IHasMemoryGO knower, final GameObjectId knowee,
                               final Known oldKnown, final Known newKnown) {
        if ( // Spieler hat gerade von Rapunzel gelern, dass die Zauberin, die sie
            // gefangen hält, die magere Frau ist.
                knower.is(SPIELER_CHARAKTER)
                        && knowee
                        .equals(RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU)
                        && newKnown.isKnown()
                        && loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)
                        && !locationComp
                        .hasRecursiveLocation(OBEN_IM_ALTEN_TURM, VOR_DEM_ALTEN_TURM)
                        && liegtImZeitfensterFuerRapunzelbesuch(timeTaker.now())
                        && stateComp
                        .hasState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH, AUF_DEM_WEG_ZU_RAPUNZEL,
                                AUF_DEM_RUECKWEG_VON_RAPUNZEL, WARTEZEIT_NACH_RAPUNZEL_BESUCH)
                        && counterDao.get(
                        ZAUBERIN_KOMMT_AUSSER_DER_REIHE_NACHDEM_MAN_VON_IHR_GESPROCHEN_HAT) == 0) {
            // Wenn man von Teufel spricht, so kommt er!
            counterDao.inc(ZAUBERIN_KOMMT_AUSSER_DER_REIHE_NACHDEM_MAN_VON_IHR_GESPROCHEN_HAT);

            locationComp.narrateAndSetLocation(
                    // Die Zauberin kommt auf einmal den Weg zum Turm herauf
                    IM_WALD_NAHE_DEM_SCHLOSS,
                    () -> stateComp.narrateAndSetState(AUF_DEM_WEG_ZU_RAPUNZEL));

            movementComp.startMovement(timeTaker.now(),
                    VOR_DEM_ALTEN_TURM, true);
            return;
        }
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        checkArgument(!span(change).longerThan(days(1)),
                "World tick time too long - see AbstractScAction.");

        switch (stateComp.getState()) {
            case MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE:
                onTimePassed_MachtZurzeitKeineRapunzelbesuche();
                return;
            case VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH:
                onTimePassed_VorDemNaechstenRapunzelBesuch(change.getNachher());
                return;
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                onTimePassed_AufDemWegZuRapunzel(change.getNachher());
                return;
            case BEI_RAPUNZEL_OBEN_IM_TURM:
                onTimePassed_BeiRapunzelObenImTurm(change.getNachher());
                return;
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                onTimePassed_AufDemRueckwegVonRapunzel(change.getNachher());
                return;
            case WARTEZEIT_NACH_RAPUNZEL_BESUCH:
                onTimePassed_WartezeitNachRapunzelBesuch(change.getNachher());
                return;
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Wandert zurück und kommt nie wieder
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + stateComp.getState());
        }
    }

    private static void onTimePassed_MachtZurzeitKeineRapunzelbesuche() {
    }

    private void onTimePassed_VorDemNaechstenRapunzelBesuch(final AvDateTime now) {
        if (!umstaendeGeeignetFuerRapunzelbesuch(now)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie (wieder) losgeht.
            return;
        }

        // Zustandswechsel nötig! Die Zauberin geht zu Rapunzel los.
        onTimePassed_ToAufDemWegZuRapunzel(now);

        // IDEA Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  recht spät oder gar nicht mehr los.
        //  Am besten durch ein zentrales Konzept beheben!
    }

    /**
     * Ob die Umstände geeignet sind, dass die Zauberin sich auf den Weg zu Rapunzel macht.
     */
    private boolean umstaendeGeeignetFuerRapunzelbesuch(final AvDateTime now) {
        return liegtImZeitfensterFuerRapunzelbesuch(now)
                &&
                // Bei kräftigem Wind geht die Zauberin nicht mehr los
                (world.loadWetter().wetterComp().getWindstaerkeUnterOffenemHimmel()
                        .compareTo(Windstaerke.KRAEFTIGER_WIND) < 0
                        //  (außer es wäre für die Geschichte notwendig)
                        || !world.loadSC().memoryComp().isKnown(RAPUNZELRUF));
    }

    public static boolean liegtImZeitfensterFuerRapunzelbesuch(final AvDateTime now) {
        return now.getTime().isWithin(
                FRUEHESTE_LOSGEHZEIT_RAPUNZELBESUCH,
                SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH);
    }

    private void onTimePassed_ToAufDemWegZuRapunzel(
            final AvDateTime now) {
        locationComp.narrateAndSetLocation(
                // Zauberin ist auf einmal draußen vor dem Schloss
                // (wer weiß, wo sie herkommt)
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> stateComp.narrateAndSetState(AUF_DEM_WEG_ZU_RAPUNZEL));

        // Zauberin geht Richtung Turm
        movementComp.startMovement(now, VOR_DEM_ALTEN_TURM);
    }

    private void onTimePassed_AufDemWegZuRapunzel(final AvDateTime now) {
        final boolean wasMovingBefore = movementComp.isMoving();

        movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Weg
            return;
        }

        // Zauberin ist unten am alten Turm angekommen.

        // IDEA Wenn der World-Tick ungewöhnlich lang war, passiert das Nachfolgende
        //  alles zu spät.
        //  Denkbare Lösungen:
        //  - Durch ein zentrales Konzept beheben (World-Ticks nie zu lang)
        //  - Zeit zwischen Ankunft und now von der Rapunzel-Besuchszeit abziehen
        //    und irgendwo (wo? die Reactions-Comps sind derzeit stateless - vielleich in der
        //    StateComp?!) speichern, wann
        //    der Besuch vorbei sein soll (besuchsEndeZeit = ankunft + BESUCH_DAUER)
        onTimePassed_AufDemWegZuRapunzel_UntenVorTurmAngekommen(now, wasMovingBefore);
    }

    private void onTimePassed_AufDemWegZuRapunzel_UntenVorTurmAngekommen(final AvDateTime now,
                                                                         final boolean wasMovingBefore) {
        if (!wasMovingBefore
                // Verhindern, dass die Zauberin sofort wieder umdreht. Das führt zu unsinnigen
                // Kombinationen in der Art "Die magere Frau kommt dir hinterher. Sie kommt auf
                // dich zu und geht an dir vorbei"
                && !now.getTime()
                .isBefore(SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH.rotateMinus(BESUCHSDAUER))) {
            // Gegen Abend geht die Zauberin wieder zurück.
            talkingComp.zauberinNichtImTurmBeginntRueckweg();
            return;
        }

        if (
            // Wenn der SC vor dem Turm steht...
                loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM)
                        // ...oder die Zauberin gesehen hat, wie der Spieler sich
                        // zwischen die Bäume gestellt hat
                        || mentalModelComp.hasAssumedLocation(SPIELER_CHARAKTER,
                        VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
        ) {
            // Zauberin wartet, dass der SC weggeht.
            return;
        }

        zauberinSteigtNachMoeglichkeitZuRapunzelHinauf();
    }

    private void zauberinSteigtNachMoeglichkeitZuRapunzelHinauf() {
        if (loadRapunzel().stateComp()
                .hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            zauberinSteigtAnDenHaarenZuRapunzelHinauf();
            return;
        }

        zauberinRuftRapunzelspruchUndRapunzelReagiert();
    }

    private void zauberinRuftRapunzelspruchUndRapunzelReagiert() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final EinzelneSubstantivischePhrase desc = getDescription(true);
            n.narrate(
                    neuerSatz(PARAGRAPH, "Als",
                            desc.nomK(),
                            "unten am Turm steht, ruft",
                            desc.persPron().nomK(),
                            "etwas. Du kannst nicht alles verstehen, aber du hörst etwas wie: "
                                    + "„…lass dein Haar herunter!“")
                            .timed(mins(1)));
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.narrateAlt(secs(10),
                    neuerSatz(PARAGRAPH,
                            "Auf einmal hörst du von unten etwas rufen"),
                    neuerSatz(PARAGRAPH,
                            "Plötzlich hörst du, wie unten jemand etwas ruft"));
        }

        world.narrateAndDoReactions().onRuf(
                RAPUNZELS_ZAUBERIN, Ruftyp.LASS_DEIN_HAAR_HERUNTER);
        // Wenn Rapunzel die Haare herunterlässt, steigt, die Zauberin
        // an den Haaren zu Rapunzel hinauf und Rapunzel holt ihre Haare wieder
        // ein (Reactions von Zauberin und Rapunzel).
    }

    private void onTimePassed_BeiRapunzelObenImTurm(final AvDateTime now) {
        if (now.isBefore(stateComp.getStateDateTime().plus(BESUCHSDAUER))) {
            rapunzelUndZauberinUnterhaltenSichObenImTurm();

            // Zauberin bleibt oben im Turm
            return;
        }

        if (
            // Ist der SC ist noch irgendwo vor dem Turm?
                loadSC().locationComp().hasRecursiveLocation(
                        VOR_DEM_ALTEN_TURM)
                        // (Wobei die Zauberin nur im Schatten der Bäume schaut, wenn sie den
                        // SC dort vermutet)
                        &&
                        (
                                !loadSC().locationComp().hasRecursiveLocation(
                                        VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
                                        ||
                                        mentalModelComp.hasAssumedLocation(SPIELER_CHARAKTER,
                                                VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
                        )
        ) {
            // Dann warten sie noch!
            return;
        }

        // Zauberin macht Anstalten, Rapunzel zu verlassen.
        // Als Reaktion (!) darauf lässt Rapunzel ihr Haar zum Abstieg hinunter,
        // Die Zauberin klettert daran herunter etc.
        stateComp.narrateAndSetState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
    }

    private void rapunzelUndZauberinUnterhaltenSichObenImTurm() {
        if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            final AltTimedDescriptionsBuilder alt = altTimed();

            alt.add(neuerSatz(anaph(false).nomK(),
                    "und",
                    world.getDescription(RAPUNZEL).nomK(),
                    "unterhalten sich, aber sie haben einander kaum etwas",
                    "zu sagen").timed(mins(5)),
                    neuerSatz(anaph(false).nomK(),
                            "hat Essen und Trinken mitgebracht und du hörst den",
                            "beiden bei der Mahlzeit zu",
                            loadSC().feelingsComp().getHunger() == HUNGRIG ?
                                    ". Dein Magen knurrt, aber es scheint niemand zu "
                                            + "bemerken" :
                                    null)
                            .timed(mins(7)),
                    neuerSatz(anaph(false).nomK(),
                            "erzählt von ihren täglichen Verrichtungen und",
                            world.getDescription(RAPUNZEL).nomK(),
                            "hört artig zu").timed(NO_TIME));
            if (counterDao.get(NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT) > 0) {
                alt.add(du("hörst", "dem Gespräch nur mit halbem Ohr zu –",
                        "auf einmal Stille. Was hatte",
                        world.getDescription(RAPUNZEL).nomK(),
                        "gerade gefragt? „Wie tragen die jungen Frauen draußen eigentlich ihr",
                        "Haar?“",
                        "„Warum fragst du das?“, antwortet",
                        getDescription(true).nomK(),
                        "schließlich langsam. „Ach, ist auch nicht so wichtig“, plappert",
                        world.getDescription(RAPUNZEL).nomK(),
                        "aufgedreht, „wie war das, was hattest du gestern eingekocht?“ – Du",
                        "wirst etwas nervös").timed(NO_TIME)
                        .withCounterIdIncrementedIfTextIsNarrated(
                                NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT));
            }

            n.narrateAlt(alt);

            loadSC().feelingsComp().requestMoodMax(ANGESPANNT);
        }
    }

    private void onTimePassed_AufDemRueckwegVonRapunzel(final AvDateTime now) {
        movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Rückweg
            return;
        }

        // Zauberin hat den Rückweg zurückgelegt.
        onTimePassed_fromAufDemRueckwegVonRapunzelToWartezeitNachRapunzelBesuch();
    }

    private void onTimePassed_WartezeitNachRapunzelBesuch(final AvDateTime now) {
        if (now.isBefore(
                stateComp.getStateDateTime().plus(MIN_ZEIT_VOR_DEM_NAECHSTEN_LOSGEHEN))) {
            return;
        }

        if (!umstaendeGeeignetFuerRapunzelbesuch(now)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie (wieder) losgeht.
            return;
        }

        // Zustandswechsel nötig! Die Zauberin geht zu Rapunzel los.
        onTimePassed_ToAufDemWegZuRapunzel(now);

        // IDEA Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  recht spät oder gar nicht mehr los.
        //  Am besten durch ein zentrales Konzept beheben!
    }

    private void onTimePassed_fromAufDemRueckwegVonRapunzelToWartezeitNachRapunzelBesuch
            () {
        locationComp.narrateAndUnsetLocation(
                // Zauberin "verschwindet" fürs erste
                () -> {
                    stateComp.narrateAndSetState(WARTEZEIT_NACH_RAPUNZEL_BESUCH)
                    // Keine extra-Zeit
                    ;
                });
    }

    @NonNull
    private <R extends
            IHasMemoryGO &
            IHasStateGO<RapunzelState> &
            ITalkerGO<RapunzelTalkingComp> &
            IResponder>
    R loadRapunzel() {
        return world.loadRequired(RAPUNZEL);
    }
}