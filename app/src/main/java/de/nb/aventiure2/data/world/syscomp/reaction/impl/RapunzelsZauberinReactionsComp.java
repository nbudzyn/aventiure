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

    // FIXME Zauberin muss vergessen, dass der SC im Gebüsch sitzt (wenn sie den Wald
    //  verlässt o. Ä.)

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
    public void onLeave(final ILocatableGO locatable,
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
            return;
        }

        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCLeave(from, to);
            return;
        }

        // STORY Kugel hinlegen: Kommentar von Hexe?
    }

    public void onSCLeave(final ILocationGO scFrom,
                          @Nullable final ILocationGO scTo) {
        if (locationComp.getLocationId() != null &&
                locationComp.hasSameUpperMostLocationAs(scFrom)) {
            // Wenn die Zauberin sieht, wie der Spieler weggeht,
            // weiß sie nicht mehr, wo er ist.
            if (scTo == null || !locationComp.hasSameUpperMostLocationAs(scTo)) {
                mentalModelComp.unassumeLocation(SPIELER_CHARAKTER);
            }

            if (scFrom.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                // Hier bemerkt der SC die Zauberin nicht
                return;
            }

            if (scFrom.is(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    !world.isOrHasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                // Falls die Zauberin noch vor dem Turm wartet:
                if (stateComp.hasState(AUF_DEM_WEG_ZU_RAPUNZEL) &&
                        locationComp.hasLocation(VOR_DEM_ALTEN_TURM)) {
                    zauberinRuftRapunzelspruchUndRapunzelReagiert();
                    return;
                }
            }

            // STORY Wenn der Spieler oben im Turm ist
            //  "Unten vor dem Turm steht eine..."?
            //  Zumindest, wenn der Spieler aus dem Fenster schaut?!
            //  Oder schaut vielleicht Rapunzel aus dem Fenster, bevor sie die
            //  Haare herunterlässt?

            movementComp.narrateAndDoSCTrifftEvtlMovingGOInFrom(scFrom, scTo);
            return;
        }
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
        if (locationComp.hasSameUpperMostLocationAs(SPIELER_CHARAKTER) &&
                scFrom != null &&
                scFrom.is(VOR_DEM_ALTEN_TURM) &&
                scTo.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            mentalModelComp.assumeLocation(SPIELER_CHARAKTER, scTo);
        }

        if (locationComp.getLocationId() == null) {
            // Zauberin hat keinen Ort, kann also auch nicht getroffen werden
            return;
        }
        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocationId())) {
            // SC und Zauberin sind nicht am gleichen Ort
            return;
        }

        if (scTo.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return;
        }

        if (world.isOrHasRecursiveLocation(scTo, scFrom) ||
                world.isOrHasRecursiveLocation(scFrom, scTo)) {
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen,
            // wieder vom Tisch herabgestiegen o.Ä.,
            // die Zauberin wurde bereits beschrieben.
            if (world.isOrHasRecursiveLocation(scTo, VOR_DEM_ALTEN_TURM) &&
                    stateComp.hasState(AUF_DEM_RUECKWEG_VON_RAPUNZEL)) {
                // Die Zauberin merkt, dass der SC unter den Bäumen verborgen war.
                // Sie ahnt, dass er sie wohl beobachtet hat.
                zauberinZaubertVergessenszauber();
                return;
            }

            if (world.isOrHasRecursiveLocation(scTo, OBEN_IM_ALTEN_TURM)) {
                // Der Spieler war unter dem Bett verborgen und kommt
                // hervor, während die Zauberin noch da ist.
                zauberinZaubertVergessenszauber();
                return;
            }

            return;
        }

        movementComp.narrateAndDoSCTrifftMovingGOInTo(scFrom, scTo);
    }

    @Override
    public void onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        // Hört die Zauberin den Ruf?
        if (!locationComp.hasSameUpperMostLocationAs(rufer) &&
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
        mentalModelComp.assumeLocation(rufer, world.loadSC().locationComp().getLocation());

        if (!rufer.is(SPIELER_CHARAKTER)) {
            return;
        }

        zauberinZaubertVergessenszauber();
    }

    @Override
    public void onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                               final Enum<?> newState) {
        if (!gameObject.is(RAPUNZEL)) {
            return;
        }

        if (stateComp.hasState(AUF_DEM_WEG_ZU_RAPUNZEL)) {
            onRapunzelStateChangedAufDemWegZuRapunzel(
                    (RapunzelState) oldState, (RapunzelState) newState);
            return;
        }

        if (stateComp.hasState(AUF_DEM_RUECKWEG_VON_RAPUNZEL)) {
            onRapunzelStateChangedAufDemRueckwegVonRapunzel(
                    (RapunzelState) oldState, (RapunzelState) newState);
            return;
        }
    }

    private void onRapunzelStateChangedAufDemWegZuRapunzel(
            final RapunzelState oldState, final RapunzelState newState
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
        // Sonderfall: Zauberin steigt hoch, während der SC oben im Turm
        // ist und sich nicht versteckt hat
        if (loadSC().locationComp().hasLocation(OBEN_IM_ALTEN_TURM)) {
            zauberinZaubertVergessenszauber();
            return;
        }

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);
            n.add(
                    du("siehst",
                            desc.akk()
                                    + " an den Haarflechten hinaufsteigen", mins(4)));

        }
        // STORY UNTER_DEM_BETT_OBEN_IM_ALTEN_TURM
//        else if (loadSC().locationComp().hasRecursiveLocation(UNTER_DEM_BETT_OBEN_IM_ALTEN_TURM)) {
//            final Nominalphrase desc = getDescription(true);
//            timeElapsed = timeElapsed.plus(n.add(
//                    du("hörst", ", "wie ...."
// , mins(4))));
//
//        }

        locationComp.narrateAndSetLocation(OBEN_IM_ALTEN_TURM);

        stateComp.narrateAndSetState(BEI_RAPUNZEL_OBEN_IM_TURM);
    }

    private void onRapunzelStateChangedAufDemRueckwegVonRapunzel(
            final RapunzelState oldState, final RapunzelState newState
    ) {
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
            final Nominalphrase desc = getDescription();
            n.add(
                    du("siehst",
                            ", wie "
                                    + desc.nom()
                                    + " an den Haaren herabsteigt",
                            mins(1))
                            .komma()
                            .beendet(SENTENCE)
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN));

            if (!loadSC().locationComp().hasRecursiveLocation(
                    VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME) ||
                    mentalModelComp.assumesLocation(SPIELER_CHARAKTER,
                            VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                locationComp.narrateAndSetLocation(VOR_DEM_ALTEN_TURM);

                zauberinZaubertVergessenszauber();
                return;
            }

            n.add(
                    neuerSatz(getAnaphPersPronWennMglSonstDescription(
                            true).nom()
                            + " hat dich nicht bemerkt", noTime()));
        }
        // STORY UNTER_DEM_BETT_OBEN_IM_ALTEN_TURM

        locationComp.narrateAndSetLocation(VOR_DEM_ALTEN_TURM);

        movementComp.startMovement(
                db.nowDao().now(),
                DRAUSSEN_VOR_DEM_SCHLOSS);
    }

    private void zauberinZaubertVergessenszauber() {
        if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.add(neuerSatz(SENTENCE,
                    "Jetzt geht alles ganz schnell. Die magere Frau schaut "
                            + "zum Fenster "
                            + "herein. Ihr Blick fällt auf dich – und mit einem Mal "
                            + "sieht sie direkt in die Augen. Du bist wie gebannt und"
                            + " kannst deinen Blick gar nicht abwenden…",
                    mins(5)));
        } else if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.add(neuerSatz("Jetzt schaut oben aus dem Turmfenster die "
                            + "magere Frau heraus. "
                            + "Kurz sucht ihr Blick umher, dann sieht sie dich direkt an. "
                            + "Ihre Augen sind - du kannst deinen Blick gar nicht abwenden…",
                    mins(5)));
        } else {
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
        stateComp.narrateAndSetState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
        locationComp.narrateAndSetLocation(IM_WALD_NAHE_DEM_SCHLOSS);
        movementComp.startMovement(
                db.nowDao().now(), DRAUSSEN_VOR_DEM_SCHLOSS);

        // Rapunzel ist still (Rapunzel hat auch alles vergessen!)
        loadRapunzel().stateComp().narrateAndSetState(RapunzelState.STILL);

        if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            // Ohne Reactions - der Spieler bekommt ja nichts davon mit.
            loadSC().locationComp().setLocation(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME);
        }

        final String ortsbeschreibung;
        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            ortsbeschreibung = "sitzt im Unterholz vor dem alten Turm";
        } else {
            ortsbeschreibung = "stehst ganz allein vor dem alten Turm";
        }

        n.add(neuerSatz(CHAPTER,
                "Du " +
                        ortsbeschreibung +
                        " und "
                        + "fühlst dich etwas verwirrt: Was hattest du "
                        + "eigentlich gerade vor? Ob der Turm wohl "
                        + "bewohnt ist? Niemand ist zu sehen",
                secs(15)));
    }

    @Override
    public void onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        checkArgument(!now.minus(lastTime).longerThan(days(1)),
                "World tick time too long - see AbstractScAction.");

        switch (stateComp.getState()) {
            case MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE:
                onTimePassed_MachtZurzeitKeineRapunzelbesuche(now);
                return;
            case VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH:
                onTimePassed_VorDemNaechstenRapunzelBesuch(now);
                return;
            case AUF_DEM_WEG_ZU_RAPUNZEL:

                onTimePassed_AufDemWegZuRapunzel(now);
                return;
            case BEI_RAPUNZEL_OBEN_IM_TURM:
                onTimePassed_BeiRapunzelObenImTurm(now);
                return;
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                onTimePassed_AufDemRueckwegVonRapunzel(now);
                return;
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Wandert zurück und kommt nie wieder
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + stateComp.getState());
        }
    }

    private static void onTimePassed_MachtZurzeitKeineRapunzelbesuche(final AvDateTime now) {
    }

    private void onTimePassed_VorDemNaechstenRapunzelBesuch(final AvDateTime now) {
        if (!liegtImZeitfensterFuerRapunzelbesuch(now)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie (wieder) losgeht.
            return;
        }

        if (now.isBefore(stateComp.getStateDateTime().plus(MIN_ZEIT_VOR_DEM_NAECHSTEN_LOSGEHEN))) {
            return;
        }

        // Zustandswechsel nötig! Die Zauberin geht zu Rapunzel los.
        onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(now);

        // TODO Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  recht spät oder gar nicht mehr los.
        //  Am besten durch ein zentrales Konzept beheben!
    }

    public static boolean liegtImZeitfensterFuerRapunzelbesuch(final AvDateTime now) {
        return now.getTime().isWithin(
                FRUEHESTE_LOSGEHZEIT_RAPUNZELBESUCH,
                SPAETESTE_LOSGEHZEIT_RAPUNZELBESUCH);
    }

    private void onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(
            final AvDateTime now) {
        locationComp.narrateAndSetLocation(
                // Zauberin ist auf einmal draußen vor dem Schloss
                // (wer weiß, wo sie herkommt)
                // Aber der Spieler bemerkt sie nicht.
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> stateComp.narrateAndSetState(AUF_DEM_WEG_ZU_RAPUNZEL));

        // Zauberin geht Richtung Turm
        movementComp.startMovement(now, VOR_DEM_ALTEN_TURM);
    }

    private void onTimePassed_AufDemWegZuRapunzel(final AvDateTime now) {
        movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Weg
            return;
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
            return;
        }

        zauberinRuftRapunzelspruchUndRapunzelReagiert();
    }

    private void zauberinRuftRapunzelspruchUndRapunzelReagiert() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);
            n.add(
                    neuerSatz(PARAGRAPH, "Als "
                            + desc.nom()
                            + " unten am Turm steht, ruft "
                            + desc.persPron().nom()
                            + " etwas. Du kannst nicht alles verstehen, aber du hörst etwas wie: "
                            + "„…lass dein Haar herunter!”", mins(1)));
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.addAlt(
                    neuerSatz(PARAGRAPH,
                            "Auf einmal hörst du von unten etwas rufen",
                            secs(10)),
                    neuerSatz(PARAGRAPH,
                            "Plötzlich hörst du, wie unten jemand etwas ruft",
                            secs(10)));
        }

        world.narrateAndDoReactions().onRuf(
                RAPUNZELS_ZAUBERIN, Ruftyp.LASS_DEIN_HAAR_HERUNTER);
        // Wenn Rapunzel die Haare herunterlässt, steigt, die Zauberin
        // an den Haaren zu Rapunzel hinauf und Rapunzel holt ihre Haare wieder
        // ein (Reactions von Zauberin und Rapunzel).

        // STORY Wenn Rapunzel die Haare nicht herunterlässt, wartet die
        //  Zauberin noch ein wenig und ruft (sofern der Spieler nicht auftaucht)
        //  noch ein paar Mal und geht am Ende davon aus, dass Rapunzel befreit wurde.
    }

    private void onTimePassed_BeiRapunzelObenImTurm(final AvDateTime now) {
        if (now.isBefore(stateComp.getStateDateTime().plus(BESUCHSDAUER))) {
            // Zauberin bleibt oben im Turm
            return;
        }

        // Zauberin macht Anstalten, Rapunzel zu verlassen.
        // Als Reaktion (!) darauf lässt Rapunzel ihr Haar zum Abstieg hinunter,
        // Die Zauberin klettert daran herunter etc.
        stateComp.narrateAndSetState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
    }

    private void onTimePassed_AufDemRueckwegVonRapunzel(final AvDateTime now) {
        movementComp.onTimePassed(now);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Rückweg
            return;
        }

        // Zauberin hat den Rückweg zurückgelegt.
        onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch();
    }

    private void onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch
            () {
        locationComp.narrateAndUnsetLocation(
                // Zauberin "verschwindet" fürs erste
                () -> {
                    stateComp.narrateAndSetState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH)
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