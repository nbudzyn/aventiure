package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELRUF;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescription.du;

/**
 * "Reaktionen" von Rapunzel, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, IRufReactions, ITimePassedReactions {

    private final RapunzelStateComp stateComp;
    private final LocationComp locationComp;

    public RapunzelReactionsComp(final AvDatabase db,
                                 final World world,
                                 final RapunzelStateComp stateComp,
                                 final LocationComp locationComp) {
        super(RAPUNZEL, db, world);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        if (locatable.is(RAPUNZELS_ZAUBERIN)) {
            return onZauberinEnter(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return onSCEnter_VorDemAltenTurm(from);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm(@Nullable final ILocationGO from) {
        switch (stateComp.getState()) {
            case SINGEND:
                return onSCEnter_VorDemAltenTurm_Singend(from);
            case HAARE_VOM_TURM_HERUNTERGELASSEN:
                return onSCEnter_VorDemAltenTurm_HaareHeruntergelassen(from);
            default:
                // STORY Konzept dafür entwickeln, dass der Benutzer Rapunzel gut gelaunt
                //  verlässt und niedergeschlagen zu Rapunzel zurückkehrt und
                //  Rapunzel auf den Wechsel reagiert (Mental Model für Rapunzel?)
                return noTime();
        }
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm_Singend(@Nullable final ILocationGO from) {
        if (!world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            return noTime();
        }

        loadSC().feelingsComp().setMoodMin(BEWEGT);

        if (db.counterDao().incAndGet(VorDemTurmConnectionComp.COUNTER_SC_HOERT_RAPUNZELS_GESANG)
                == 1) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Wie du näher kommst, hörst du einen Gesang, so lieblich, dass es "
                            + "dir das Herz rührt. Du hältst still und horchst: Kommt die "
                            + "Stimme aus dem kleinen Fensterchen oben im Turm?",
                    secs(20))
                    .beendet(PARAGRAPH));
        }
        return n.addAlt(
                du("hörst",
                        "erneut die süße Stimme aus dem Turmfenster singen",
                        "erneut", secs(10)),
                du("hörst",
                        "es wieder von oben aus dem Turm singen",
                        "von oben aus dem Turm",
                        noTime()),
                du(PARAGRAPH, "hörst",
                        "wieder Gesang von oben",
                        "wieder",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz("Erneut hörst du den Gesang aus dem Turmfenster",
                        noTime())
        );
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm_HaareHeruntergelassen(
            @Nullable final ILocationGO from) {
        if (!world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            return noTime();
        }

        loadSC().feelingsComp().setMoodMin(NEUTRAL);
        // STORY Andere und alternative Beschreibungen, wenn der SC
        //  Rapunzel schon kennengelernt hat
        return n.add(neuerSatz(SENTENCE, "Aus dem kleinen "
                        + "Fenster oben im Turm hängen lange, goldene Haarzöpfe herab",
                noTime()));
    }

    private AvTimeSpan onZauberinEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                from != null && from.is(VOR_DEM_ALTEN_TURM) &&
                to.is(OBEN_IM_ALTEN_TURM)) {
            return zauberinBesuchtRapunzelImAltenTurm();
        }

        return noTime();
    }

    private AvTimeSpan zauberinBesuchtRapunzelImAltenTurm() {
        AvTimeSpan timeElapsed = stateComp.narrateAndSetState(STILL);

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            timeElapsed = timeElapsed.plus(
                    n.add(neuerSatz(
                            "Dann "
                                    // TODO Dies ist ein Beispiel für "dann", das nur Sinn ergibt, wenn
                                    //  die Zauberin vorher etwas getan hat - aber nicht, wenn der SC vorher
                                    //  etwas getan hat!
                                    + "verschwinden die prächtigen Haare wieder oben im Fenster. "
                                    + "„Das ist also die Leiter, auf welcher man hinaufkommt!“, denkst du "
                                    + "bei dir", secs(20))
                            .undWartest()));
            world.upgradeKnownToSC(RAPUNZELRUF);
        }

        return timeElapsed;
    }

    @Override
    public AvTimeSpan onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        // Hört Rapunzel den Ruf?
        if (!locationComp.hasSameUpperMostLocationAs(rufer) &&
                (!rufer.locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) ||
                        !locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM))) {
            return noTime();
        }

        if (ruftyp == Ruftyp.LASS_DEIN_HAAR_HERUNTER) {
            return onRapunzelruf();
        }

        return noTime();
    }

    public AvTimeSpan onRapunzelruf() {
        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            return noTime();
        }

        AvTimeSpan extraTime = noTime();

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (stateComp.hasState(SINGEND)) {
                extraTime = extraTime.plus(n.add(
                        neuerSatz(SENTENCE,
                                "Sofort hört der Gesang auf – und gleich darauf fallen "
                                        + "aus dem kleinen "
                                        + "Fenster oben im Turm lange, goldene Haarzöpfe herab, "
                                        + "sicher zwanzig Ellen tief bis auf den Boden. ",
                                secs(30))));
            } else {
                extraTime = extraTime.plus(n.add(
                        neuerSatz(SENTENCE, "Gleich darauf fallen aus dem kleinen "
                                + "Fenster oben im Turm lange, goldene Haarzöpfe herab, sicher "
                                + "zwanzig Ellen tief bis auf den Boden. ", secs(30))));
            }
        }

        // STORY Der Spieler kann sich an den Haaren hochziehen!

        return extraTime.plus(
                stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN));
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        if (rapunzelMoechteSingen(now)) {
            return onTimePassed_RapunzelMoechteSingen(lastTime, now);
        }

        return onTimePassed_RapunzelMoechteNichtSingen(lastTime, now);
    }

    private boolean rapunzelMoechteSingen(final AvDateTime now) {
        if (!stateComp.hasState(STILL, SINGEND)) {
            return false;
        }

        // Rapunzel singt nur, wenn es denkt, niemand wäre in der Gegend.
        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN))
                .locationComp().hasLocation(VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN)).locationComp()
                .hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            // Während Rapunzel von der Zauberin Besuch hat, singt sie nicht
            return false;
        }

        // Ansonsten singt Rapunzel innerhalb gewisser Zeiten immer mal wieder
        return now.getTageszeit().getLichtverhaeltnisseDraussen() == HELL &&
                !isZeitFuerMittagsruhe(now) &&
                immerMalWieder(now);
    }

    private static boolean isZeitFuerMittagsruhe(final AvDateTime now) {
        return now.getTime().isWithin(oClock(1), oClock(2, 30));
    }

    private static boolean immerMalWieder(final AvDateTime now) {
        return now.getTime().isInRegularTimeIntervalIncl(
                // Ab...
                oClock(7),
                // ... immer für ...
                mins(10),
                // ... Minuten mit
                mins(25),
                // ... Minuten Pause danach - bis um
                oClock(19));
    }

    private AvTimeSpan onTimePassed_RapunzelMoechteSingen(final AvDateTime lastTime,
                                                          final AvDateTime now) {
        if (stateComp.hasState(STILL)) {
            return stateComp.narrateAndSetState(SINGEND).plus(
                    onTimePassed_moechteSingen_bislangStill());
        }

        // Rapunzel hat schon die ganze Zeit gesungen
        return noTime();
    }

    private AvTimeSpan onTimePassed_moechteSingen_bislangStill() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return noTime();
        }

        loadSC().feelingsComp().setMoodMin(BEWEGT);

        if (db.counterDao().incAndGet(VorDemTurmConnectionComp.COUNTER_SC_HOERT_RAPUNZELS_GESANG)
                == 1) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Auf einmal hebt ein Gesang an, so lieblich, dass es dir das "
                            + "Herz rührt. Du hältst still und horchst: Kommt die Stimme aus "
                            + "dem kleinen Fensterchen oben im Turm?",
                    secs(20)));
        }
        return n.addAlt(
                du("hörst",
                        "erneut die süße Stimme aus dem Turmfenster singen",
                        "erneut", secs(10)),
                du("hörst",
                        "es von oben aus dem Turm singen",
                        "von oben aus dem Turm",
                        noTime()),
                du(PARAGRAPH, "hörst",
                        "wieder Gesang von oben schallen",
                        "wieder",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH, "Plötzlich erschallt über dir wieder Gesang",
                        noTime()),
                du("hörst",
                        "den Gesang erneut",
                        "erneut",
                        noTime())
        );
    }

    private AvTimeSpan onTimePassed_RapunzelMoechteNichtSingen(final AvDateTime lastTime,
                                                               final AvDateTime now) {
        if (stateComp.hasState(SINGEND)) {
            return stateComp.narrateAndSetState(STILL).plus(
                    onTimePassed_moechteNichtMehrSingen_bislangGesungen());
        }

        // Rapunzel hat schon die ganze Zeit nicht gesungen
        return noTime();

    }

    private AvTimeSpan onTimePassed_moechteNichtMehrSingen_bislangGesungen() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return noTime();
        }

        loadSC().feelingsComp().setMoodMin(BEWEGT);

        return n.addAlt(
                neuerSatz("Plötzlich endet der Gesang",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz("Plötzlich wird es still",
                        noTime()),
                neuerSatz(PARAGRAPH,
                        "Nun hat der Gesang geendet - wie gern würdest noch länger "
                                + "zuhören!",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Jetzt ist es wieder still. Dein Herz ist noch ganz bewegt",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Auf einmal ist nichts mehr zu hören. Es lässt dir keine Ruhe: "
                                + "Wer mag dort oben so lieblich singen?",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Nun ist es wieder still",
                        noTime()),
                neuerSatz(PARAGRAPH,
                        "Jetzt hat der süße Gesang aufgehört",
                        noTime())
        );
    }
}