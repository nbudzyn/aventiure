package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
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
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELRUF;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.BEI_RAPUNZEL_OBEN_IM_TURM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.days;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescription.du;

/**
 * "Reaktionen" von Rapunzels Zauberin, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelsZauberinReactionsComp
        extends AbstractDescribableReactionsComp
        implements
        // Reaktionen auf die Bewegungen des SC und anderes Game Objects
        IMovementReactions, IRufReactions, IStateChangedReactions,
        ITimePassedReactions {
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

    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;
    private final MentalModelComp mentalModelComp;
    private final MovementComp movementComp;

    public RapunzelsZauberinReactionsComp(final AvDatabase db,
                                          final World world,
                                          final RapunzelsZauberinStateComp stateComp,
                                          final LocationComp locationComp,
                                          final MentalModelComp mentalModelComp,
                                          final MovementComp movementComp) {
        super(RAPUNZELS_ZAUBERIN, db, world);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.mentalModelComp = mentalModelComp;
        this.movementComp = movementComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        // Wenn die Zauberin den SC verlässt, ...
        if (locatable.is(getGameObjectId())) {
            // ...dann weiß sie nicht, wo das SC sich befindet.
            // Einzige Ausnahme:
            // Wenn die Zauberin den SC vor dem Turm verlassen hat, geht es erst einmal
            // davon aus, dass er wohl dort noch ist, denn das ist eine Sackgasse.
            if (!world.isOrHasRecursiveLocation(from, VOR_DEM_ALTEN_TURM) ||
                    !mentalModelComp.assumesLocation(
                            SPIELER_CHARAKTER,
                            VOR_DEM_ALTEN_TURM,
                            VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
            ) {
                mentalModelComp.unassumeLocation(SPIELER_CHARAKTER);
            }
        }

        if (locationComp.getLocationId() == null) {
            // Zauberin hat keinen Ort, kann also auch nicht getroffen werden
            return noTime();
        }

        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCLeave(from, to);
        }

        // STORY Kugel hinlegen: Kommentar von Hexe?

        return noTime();
    }

    public AvTimeSpan onSCLeave(final ILocationGO scFrom,
                                @Nullable final ILocationGO scTo) {
        if (locationComp.getLocationId() != null &&
                locationComp.hasSameUpperMostLocationAs(scFrom)) {
            // Wenn die Zauberin sieht, wie der Spieler weggeht,
            // weiß sie nicht mehr, wo er ist.
            if (scTo == null ||
                    !locationComp.hasSameUpperMostLocationAs(scTo)) {
                mentalModelComp.unassumeLocation(SPIELER_CHARAKTER);
            }

            if (scFrom.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                // Hier bemerkt der SC die Zauberin nicht
                return noTime();
            }

            if (scFrom.is(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    !world.isOrHasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                // Falls die Zauberin noch vor dem Turm wartet:
                if (stateComp.hasState(AUF_DEM_WEG_ZU_RAPUNZEL) &&
                        locationComp.hasLocation(VOR_DEM_ALTEN_TURM)) {
                    return zauberinRuftRapunzelspruchUndRapunzelReagiert();
                }
            }

            // STORY Wenn der Spieler oben im Turm ist
            //  "Unten vor dem Turm steht eine..."?

            return movementComp.narrateAndDoSCTrifftEvtlMovingGOInFrom(scFrom, scTo);
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
        // Wenn die Zauberin sieht, wie sich der Spieler in den Schatten der
        // Bäume setzt, weiß sie, dass er dort ist.
        if (locationComp.hasSameUpperMostLocationAs(SPIELER_CHARAKTER) &&
                scFrom != null &&
                scFrom.is(VOR_DEM_ALTEN_TURM) &&
                scTo.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            mentalModelComp.assumeLocation(SPIELER_CHARAKTER, scTo);
        }

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

        if (world.isOrHasRecursiveLocation(scTo, scFrom)) {
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen o.Ä.,
            // die Zauberin wurde bereits beschrieben.
            if (world.isOrHasRecursiveLocation(scTo, VOR_DEM_ALTEN_TURM) &&
                    stateComp.hasState(AUF_DEM_RUECKWEG_VON_RAPUNZEL)) {
                // Die Zauberin merkt, dass der SC unter den Bäumen verborgen war.
                // Sie ahnt, dass er sie wohl beobachtet hat.
                return zauberinZaubertVergessenszauber();
            }

            return noTime();
        }

        return movementComp.narrateAndDoSCTrifftMovingGOInTo(scFrom, scTo);
    }

    @Override
    public AvTimeSpan onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        // Hört die Zauberin den Ruf?
        if (!locationComp.hasSameUpperMostLocationAs(rufer) &&
                (!rufer.locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) ||
                        !locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM))) {
            return noTime();
        }

        if (ruftyp == Ruftyp.LASS_DEIN_HAAR_HERUNTER) {
            return onRapunzelruf(rufer);
        }

        return noTime();
    }

    private AvTimeSpan onRapunzelruf(final ILocatableGO rufer) {
        // Zauberin weiß jetzt, wo der Rufer ist
        mentalModelComp.assumeLocation(rufer, world.loadSC().locationComp().getLocation());

        if (!rufer.is(SPIELER_CHARAKTER)) {
            return noTime();
        }

        return zauberinZaubertVergessenszauber();
    }

    @Override
    public AvTimeSpan onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                                     final Enum<?> newState) {
        if (!gameObject.is(RAPUNZEL)) {
            return noTime();
        }

        if (stateComp.hasState(AUF_DEM_WEG_ZU_RAPUNZEL)) {
            return onRapunzelStateChangedAufDemWegZuRapunzel();
        }

        if (stateComp.hasState(AUF_DEM_RUECKWEG_VON_RAPUNZEL)) {
            return onRapunzelStateChangedAufDemRueckwegVonRapunzel();
        }

        return noTime();
    }

    private AvTimeSpan onRapunzelStateChangedAufDemWegZuRapunzel() {
        if (!locationComp.hasLocation(VOR_DEM_ALTEN_TURM)) {
            return noTime();
        }

        return zauberinSteigtAnDenHaarenZuRapunzelHinauf();
    }

    private AvTimeSpan zauberinSteigtAnDenHaarenZuRapunzelHinauf() {
        AvTimeSpan timeElapsed = noTime();
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);
            timeElapsed = timeElapsed.plus(n.add(
                    du("siehst",
                            desc.akk()
                                    + " an den Haarflechten hinaufsteigen", mins(4))));

        }

        timeElapsed = timeElapsed.plus(
                locationComp.narrateAndSetLocation(OBEN_IM_ALTEN_TURM));

        return timeElapsed.plus(stateComp.narrateAndSetState(BEI_RAPUNZEL_OBEN_IM_TURM));
    }

    private AvTimeSpan onRapunzelStateChangedAufDemRueckwegVonRapunzel() {
        if (!locationComp.hasLocation(OBEN_IM_ALTEN_TURM)) {
            return noTime();
        }

        return zauberinSteigtAnDenHaarenHerab();
    }

    private AvTimeSpan zauberinSteigtAnDenHaarenHerab() {
        AvTimeSpan extraTime = noTime();

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription();
            extraTime = extraTime.plus(n.add(
                    du("siehst",
                            ", wie "
                                    + desc.nom()
                                    + " an den Haaren herabsteigt",
                            mins(1))
                            .komma()
                            .beendet(SENTENCE)
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)));
            if (loadSC().locationComp().hasRecursiveLocation(
                    VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME) &&
                    !mentalModelComp.assumesLocation(SPIELER_CHARAKTER,
                            VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                extraTime = extraTime.plus(n.add(
                        neuerSatz(getAnaphPersPronWennMglSonstDescription(
                                true)
                                + " scheint dich nicht zu bemerken", noTime())));
                return extraTime.plus(
                        locationComp.narrateAndSetLocation(VOR_DEM_ALTEN_TURM));
            }

            extraTime = extraTime.plus(locationComp.narrateAndSetLocation(VOR_DEM_ALTEN_TURM));

            extraTime = zauberinZaubertVergessenszauber();
        }

        return extraTime.plus(movementComp.startMovement(
                db.nowDao().now().plus(extraTime),
                DRAUSSEN_VOR_DEM_SCHLOSS));
    }

    private AvTimeSpan zauberinZaubertVergessenszauber() {
        AvTimeSpan timeElapsed;
        if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            timeElapsed =
                    n.add(neuerSatz("Jetzt schaut oben aus dem Turmfenster die "
                                    + "magere Frau heraus. "
                                    + "Kurz sucht ihr Blick umher, dann sieht sie dich direkt an. "
                                    + "Ihre Augen sind - du kannst deinen Blick gar nicht abwenden…",
                            mins(5)));
        } else {
            timeElapsed =
                    n.add(neuerSatz(PARAGRAPH,
                            "Die magere Frau sieht dich mit einem Mal "
                                    + "direkt an. Ihre Augen sind - du kannst deinen Blick "
                                    + "gar nicht abwenden…",
                            mins(5)));
        }

        // Spieler wird verzaubert und vergisst alles.
        loadSC().memoryComp().forget(RAPUNZEL, RAPUNZELS_ZAUBERIN, RAPUNZELRUF);
        loadSC().feelingsComp().setMood(ERSCHOEPFT);
        db.counterDao().reset(VorDemTurmConnectionComp.COUNTER_ALTER_TURM_UMRUNDET);
        db.counterDao().reset(VorDemTurmConnectionComp.COUNTER_SC_HOERT_RAPUNZELS_GESANG);

        // Die Zauberin ist schon weit auf dem Rückweg
        timeElapsed = timeElapsed.plus(
                stateComp.narrateAndSetState(AUF_DEM_RUECKWEG_VON_RAPUNZEL));
        timeElapsed = timeElapsed.plus(
                locationComp.narrateAndSetLocation(IM_WALD_NAHE_DEM_SCHLOSS));
        timeElapsed.plus(
                movementComp.startMovement(
                        db.nowDao().now().plus(timeElapsed), DRAUSSEN_VOR_DEM_SCHLOSS));

        // Rapunzel ist still
        timeElapsed = timeElapsed.plus(
                loadRapunzel().stateComp().narrateAndSetState(RapunzelState.STILL));

        final String ortsbeschreibung;
        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            ortsbeschreibung = "sitzt im Unterholz vor dem alten Turm";
        } else {
            ortsbeschreibung = "stehst ganz allein vor dem alten Turm";
        }

        return timeElapsed.plus(
                n.add(neuerSatz(CHAPTER,
                        "Du " +
                                ortsbeschreibung +
                                " und "
                                + "fühlst dich etwas verwirrt: Was hattest du "
                                + "eigentlich gerade vor? Ob der Turm wohl "
                                + "bewohnt ist? Niemand ist zu sehen",
                        secs(15))));
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        checkArgument(!now.minus(lastTime).longerThan(days(1)),
                "World tick time too long - see AbstractScAction.");

        switch (stateComp.getState()) {
            case MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE:
                return onTimePassed_MachtZurzeitKeineRapunzelbesuche(now);
            case VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH:
                return onTimePassed_VorDemNaechstenRapunzelBesuch(now);
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                return onTimePassed_AufDemWegZuRapunzel(now);
            case BEI_RAPUNZEL_OBEN_IM_TURM:
                return onTimePassed_BeiRapunzelObenImTurm(now);
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                return onTimePassed_AufDemRueckwegVonRapunzel(now);
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Wandert zurück und kommt nie wieder
                return noTime();
            default:
                throw new IllegalStateException("Unexpected value: " + stateComp.getState());
        }
    }

    private static AvTimeSpan onTimePassed_MachtZurzeitKeineRapunzelbesuche(final AvDateTime now) {
        return noTime();
    }

    private AvTimeSpan onTimePassed_VorDemNaechstenRapunzelBesuch(final AvDateTime now) {
        if (!liegtImZeitfensterFuerRapunzelbesuch(now)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie (wieder) losgeht.
            return noTime();
        }

        if (now.isBefore(stateComp.getStateDateTime().plus(MIN_ZEIT_VOR_DEM_NAECHSTEN_LOSGEHEN))) {
            return noTime();
        }

        // Zustandswechsel nötig! Die Zauberin geht zu Rapunzel los.
        return onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(now);

        // TODO Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  recht spät oder gar nicht mehr los.
        //  Am besten durch ein zentrales Konzept beheben!
    }

    public static boolean liegtImZeitfensterFuerRapunzelbesuch(final AvDateTime now) {
        return now.getTime().isWithin(
                FRUEHESTE_LOSGEHZEIT_RAPUNZELBESUCH,
                SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH);
    }

    private AvTimeSpan onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(
            final AvDateTime now) {
        final AvTimeSpan extraTime = locationComp.narrateAndSetLocation(
                // Zauberin ist auf einmal draußen vor dem Schloss
                // (wer weiß, wo sie herkommt)
                // Aber der Spieler bemerkt sie nicht.
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> {
                    return stateComp.narrateAndSetState(AUF_DEM_WEG_ZU_RAPUNZEL)
                            // Keine extra-Zeit
                            ;
                });

        // Zauberin geht Richtung Turm
        return extraTime.plus(
                movementComp.startMovement(now, VOR_DEM_ALTEN_TURM)
        );
    }

    private AvTimeSpan onTimePassed_AufDemWegZuRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Weg
            return extraTime;
        }

        // TODO Wenn der World-Tick ungewöhnlich lang war, passiert das hier
        //  alles zu spät.
        //  Denkbare Lösungen:
        //  - Durch ein zentrales Konzept beheben (World-Ticks nie zu lang)
        //  - Zeit zwischen Ankunft und now von der Rapunzel-Besuchszeit abziehen
        //    und irgendwo (wo? hier in der Reactions-Comp?) speichern, wann
        //    der Besuch vorbei sein soll (besuchsEndeZeit = ankunft + BESUCH_DAUER)

        // Zauberin ist unten am alten Turm angekommen.
        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM) ||
                mentalModelComp.assumesLocation(SPIELER_CHARAKTER,
                        VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)
        ) {
            // Wenn der SC auch in der Gegend ist, dann wartet die Zauberin,
            // bis der SC weggeht.
            // Dasselbe, wenn die Zauberin gesehen hat, dass der Spieler sich zwischen die
            //  Bäume gestellt hat.
            return extraTime;
        }

        return extraTime.plus(zauberinRuftRapunzelspruchUndRapunzelReagiert());
    }

    private AvTimeSpan zauberinRuftRapunzelspruchUndRapunzelReagiert() {
        AvTimeSpan extraTime = noTime();

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);
            extraTime = extraTime.plus(n.add(
                    neuerSatz(PARAGRAPH, "Als "
                            + desc.nom()
                            + " unten am Turm steht, ruft "
                            + desc.persPron().nom()
                            + " etwas. Du kannst nicht alles verstehen, aber du hörst etwas wie: "
                            + "„…lass dein Haar herunter!”", mins(1))));
        }

        return extraTime.plus(
                world.narrateAndDoReactions().onRuf(
                        RAPUNZELS_ZAUBERIN, Ruftyp.LASS_DEIN_HAAR_HERUNTER));
        // Wenn Rapunzel die Haare herunterlässt, steigt, die Zauberin
        // an den Haaren zu Rapunzel hinauf und Rapunzel holt ihre Haare wieder
        // ein (Reactions von Zauberin und Rapunzel).

        // STORY Wenn Rapunzel die Haare nicht herunterlässt, wartet die
        //  Zauberin noch ein wenig und ruft (sofern der Spieler nicht auftaucht)
        //  noch ein paar Mal und geht am Ende davon aus, dass Rapunzel befreit wurde.
    }

    private AvTimeSpan onTimePassed_BeiRapunzelObenImTurm(final AvDateTime now) {
        if (now.isBefore(stateComp.getStateDateTime().plus(BESUCHSDAUER))) {
            // Zauberin bleibt oben im Turm
            return noTime();
        }

        // Zauberin macht Anstalten, Rapunzel zu verlassen.
        // Als Reaktion (!) darauf lässt Rapunzel ihr Haar zum Abstieg hinunter,
        // Die Zauberin klettert daran herunter etc.
        return stateComp.narrateAndSetState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
    }

    private AvTimeSpan onTimePassed_AufDemRueckwegVonRapunzel(final AvDateTime now) {
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
                    return stateComp.narrateAndSetState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH)
                            // Keine extra-Zeit
                            ;
                });
    }

    @NonNull
    private <R extends
            IHasStateGO<RapunzelState> &
            ITalkerGO<RapunzelTalkingComp>>
    R loadRapunzel() {
        return (R) world.load(RAPUNZEL);
    }
}