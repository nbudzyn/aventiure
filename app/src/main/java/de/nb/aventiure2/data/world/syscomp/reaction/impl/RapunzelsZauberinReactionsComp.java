package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.BESUCHT_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.days;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * "Reaktionen" von Rapunzels Zauberin, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelsZauberinReactionsComp
        extends AbstractDescribableReactionsComp
        implements
        // Reaktionen auf die Bewegungen des SC und anderes Game Objects
        IMovementReactions, ITimePassedReactions {
    // Vorher ist es der Zauberin für einen Rapunzelbesuch zu früh
    private static final AvTime FRUEHESTE_LOSGEHZEIT_RAPUNZELBESUCH = oClock(14);

    // Danach wird es der Zauberin für einen Rapunzelbesuch zu spät
    private static final AvTime SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH =
            oClock(15, 30);

    private static final AvTimeSpan BESUCHSDAUER = mins(45);

    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;
    private final MovementComp movementComp;

    public RapunzelsZauberinReactionsComp(final AvDatabase db,
                                          final World world,
                                          final RapunzelsZauberinStateComp stateComp,
                                          final LocationComp locationComp,
                                          final MovementComp movementComp) {
        super(RAPUNZELS_ZAUBERIN, db, world);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.movementComp = movementComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        if (locationComp.getLocationId() == null) {
            // Zauberin hat keinen Ort, kann also auch nicht getroffen werden
            return noTime();
        }

        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCLeave(from, to);
        }

        return noTime();
    }

    public AvTimeSpan onSCLeave(final ILocationGO scFrom,
                                @Nullable final ILocationGO scTo) {
        if (locationComp.getLocationId() != null &&
                locationComp.hasSameUpperMostLocationAs(scFrom)) {
            if (scFrom.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                // Hier bemerkt der SC die Zauberin nicht
                return noTime();
            }

            // STORY Wenn der Spieler oben im Turm ist
            //  "Unten vor dem Turm steht eine..."?

            // STORY Reaktion der Zauberin, wenn SC die Zauberin oben im Turm antrifft
            //  (falls das sein kann).

            movementComp.narrateAndDoSCTrifftEvtlMovingGOInFrom(scFrom, scTo);
        }

        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO scFrom, final ILocationGO scTo) {
        if (locationComp.getLocationId() == null) {
            // Zauberin hat keinen Ort, kann also auch nicht getroffen werden
            return noTime();
        }
        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocationId())) {
            // SC und Zauberin sind nicht am gleichen Ort
            return noTime();
        }

        if (scTo.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        // STORY Wenn der Spieler oben im Turm ist
        //  "Unten vor dem Turm steht eine..."?

        // STORY Reaktion der Zauberin, wenn SC die Zauberin oben im Turm antrifft
        //  (falls das sein kann).

        return movementComp.narrateAndDoSCTrifftMovingGOInTo(scFrom, scTo);
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        checkArgument(!now.minus(lastTime).longerThan(days(1)),
                "World tick time too long - see AbstractScAction.");

        switch (stateComp.getState()) {
            case VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH:
                return onTimePassed_fromVorDemNaechstenRapunzelBesuch(now);
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                return onTimePassed_fromAufDemWegZuRapunzel(now);
            case BESUCHT_RAPUNZEL:
                return onTimePassed_fromBesuchtRapunzel(now);
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                // STORY Lässt sich an den Haaren herunterhiefen und wandert zurück
                return onTimePassed_fromAufDemRueckwegVonRapunzel(now);
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Wandert zurück und kommt nie wieder
                return noTime();
            default:
                throw new IllegalStateException("Unexpected value: " + stateComp.getState());
        }
    }

    private AvTimeSpan onTimePassed_fromVorDemNaechstenRapunzelBesuch(
            final AvDateTime now) {
        if (!now.getTime().isWithin(
                FRUEHESTE_LOSGEHZEIT_RAPUNZELBESUCH,
                SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie (wieder) losgeht.
            return noTime();
        }

        // Zustandswechsel nötig! Die Zauberin geht zu Rapunzel los.
        return onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(now);

        // TODO Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  recht spät oder gar nicht mehr los.
        //  Am besten durch ein zentrales Konzept beheben!
    }

    private AvTimeSpan onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(
            final AvDateTime now) {
        final AvTimeSpan extraTime = locationComp.narrateAndSetLocation(
                // Zauberin ist auf einmal draußen vor dem Schloss
                // (wer weiß, wo sie herkommt)
                // Aber der Spieler bemerkt sie nicht.
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> {
                    stateComp.setState(AUF_DEM_WEG_ZU_RAPUNZEL);
                    // Keine extra-Zeit
                    return noTime();
                });

        // Zauberin geht Richtung Turm
        return extraTime.plus(
                movementComp.startMovement(now, VOR_DEM_ALTEN_TURM)
        );
    }

    private AvTimeSpan onTimePassed_fromAufDemWegZuRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Weg
            return extraTime;
        }

        // TODO Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  erst jetzt (also zu spät) in den BESUCHT_RAPUNZEL-State.
        //  Rapunzel wird also zu lange besucht.
        //  Denkbare Lösungen:
        //  - Durch ein zentrales Konzept beheben (World-Ticks nie zu lang)
        //  - Zeit zwischen Ankunft und now von der Rapunzel-Besuchszeit abziehen
        //    und irgendwo (wo? hier in der Reactions-Comp?) speichern, wann
        //    der Besucht vorbei sein soll (besuchsEndeZeit = ankunft + BESUCH_DAUER)

        // Zauberin ist unten am alten Turm angekommen.
        stateComp.setState(BESUCHT_RAPUNZEL);

        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM)) {
            // Wenn der SC auch direkt vor dem Turm steht, dann wartet die Frau,
            // bis der SC weggeht
            // TODO Wenn der SC weggeht lässt sich die Zauberin (ohne dass der SC
            //  es merkt, in den Turm hochziehen)
            return extraTime;
        }

        return extraTime.plus(zauberinRuftRapunzelspruch());
    }

    private AvTimeSpan zauberinRuftRapunzelspruch() {
        AvTimeSpan extraTime = noTime();

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);
            extraTime = extraTime.plus(n.add(
                    neuerSatz(PARAGRAPH, "Als "
                            + desc.nom()
                            + " unten am Turm steht, ruft "
                            + desc.persPron().nom()
                            + " laut: „Rapunzel, "
                            + "Rapunzel, lass mir dein Haar herunter!”", mins(1))
                            .phorikKandidat(desc, getGameObjectId())));
        }

        ((RapunzelTalkingComp) ((ITalkerGO) world.load(RAPUNZEL)).talkingComp()).
                reactToRapunzelruf(getGameObjectId());

        // if (Rapunzel hat die Haare heruntergelassen){
        //                STORY "Du siehst die magere Frau daran hinaufsteigen";
        locationComp.narrateAndSetLocation(OBEN_IM_ALTEN_TURM);
        //} else{
        // STORY Zauberin wartet und ruft noch ein paar Mal und erkennt am Ende, dass
        //  Rapunzel befreit wurde
        //}
        return extraTime;
    }

    private AvTimeSpan onTimePassed_fromBesuchtRapunzel(final AvDateTime now) {
        if (now.isBefore(stateComp.getStateDateTime().plus(BESUCHSDAUER))) {
            // Falls die Zauberin noch vor dem Turm wartet:
            if (locationComp.hasLocation(VOR_DEM_ALTEN_TURM) &&
                    !loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM)) {
                // Wenn der SC auch direkt vor dem Turm steht, dann wartet die Zauberin,
                // bis der SC weggeht
                return zauberinRuftRapunzelspruch();
            }

            // Zauberin bleibt noch bei Rapunzel
            return noTime();
        }

        // Zauberin verlässt Rapunzel
        // STORY Zauberin wird wieder heruntergelassen

        stateComp.setState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
        return movementComp.startMovement(now, DRAUSSEN_VOR_DEM_SCHLOSS);
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Rückweg
            return extraTime;
        }

        // Zauberin hat den Rückweg zurückgelegt.
        return onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch();
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch
            () {
        return locationComp.narrateAndUnsetLocation(
                // Zauberin "verschwindet" fürs erste
                () -> {
                    stateComp.setState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH);

                    // Keine extra-Zeit
                    return noTime();
                });
    }
}