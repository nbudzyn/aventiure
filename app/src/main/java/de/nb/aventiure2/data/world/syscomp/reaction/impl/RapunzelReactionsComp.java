package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * "Reaktionen" von Rapunzel, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {

    private final RapunzelStateComp stateComp;
    private final LocationComp locationComp;

    public RapunzelReactionsComp(final AvDatabase db,
                                 final World world,
                                 final AbstractDescriptionComp descriptionComp,
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

        return noTime();
    }

    private AvTimeSpan onSCEnter(final ILocationGO from, final ILocationGO to) {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return onSCEnter_VorDemAltenTurm(from);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm(final ILocationGO from) {
        switch (stateComp.getState()) {
            case SINGEND:
                return onSCEnter_VorDemAltenTurm_Singend(from);
            default:
                // STORY Konzept entwickeln, das diese "Statusübergänge" realisiert:
                //  - Benutzer verlässt den Ort, während Rapunzel singt und kehrt zurück, wenn Rapunzel nicht
                //    mehr singt
                //  - Benutzer verlässt Rapunzel gut gelaunt und kehrt niedergeschlage zu Rapunzel zurück,
                //    Rapunzel reagiert auf den Wechsel (Mental Model für Rapunzel?)
                return noTime();
        }
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm_Singend(final ILocationGO from) {
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
                neuerSatz("Von oben aus dem Turm hörst du es wieder singen",
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

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        if (rapunzelMoechteSingen(now)) {
            return onTimePassed_RapunzelMoechteSingen(lastTime, now);
        }

        return onTimePassed_RapunzelMoechteNichtSingen(lastTime, now);
    }

    /**
     * Gibt zurück, ob es für Rapunzel eine gute Zeit ist zu singen
     */
    private boolean rapunzelMoechteSingen(final AvDateTime now) {
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
        if (now.getTime().isInRegularTimeIntervalIncl(
                // Ab...
                oClock(7),
                // ... immer für ...
                mins(10),
                // ... Minuten mit
                mins(25),
                // ... Minuten Pause danach - bis um
                oClock(12, 30))) {
            return true;
        }

        if (now.getTime().isInRegularTimeIntervalIncl(
                oClock(14, 30),
                mins(10),
                mins(25),
                oClock(19))) {
            return true;
        }

        return false;
    }

    private AvTimeSpan onTimePassed_RapunzelMoechteSingen(final AvDateTime lastTime,
                                                          final AvDateTime now) {
        // STORY Konzept entwickeln, um dies zu realisieren:
        //  - Benutzer rastet für längere Zeit (wach) und Rapunzel beginnt mehrfach
        //    zu singen und hört wieder auf, letztlich singt Rapunzel

        if (stateComp.hasState(STILL)) {
            stateComp.setState(SINGEND);

            return onTimePassed_ZeitZumSingen_bislangStill();
        }

        // Rapunzel hat schon die ganze Zeit gesungen

        return noTime();
    }

    private AvTimeSpan onTimePassed_ZeitZumSingen_bislangStill() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return noTime();
        }

        // STORY Konzept entwickeln, dass diese "Statusübergänge" realisiert:
        //  - Benutzer schläft an einem Ort, Rapunzel beginnt dort zu singen und hört wieder auf
        //     (Benutzer merkt nichts)
        //  - Benutzer schläft ein, während Rapunzel nicht singt und wacht auf und Rapunzel hat
        //    zwischenzeitlich angefangen zu singen

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
                neuerSatz("Von oben aus dem Turm hörst du es singen",
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
        // STORY Konzept entwickeln, um dies zu realisieren:
        //  - Benutzer rastet für längere Zeit (wach) und Rapunzel beginnt mehrfach
        //    zu singen und hört wieder auf, letztlich singt Rapunzel nicht mehr

        if (stateComp.hasState(SINGEND)) {
            stateComp.setState(STILL);

            return onTimePassed_NichtZeitZumSingen_bislangGesungen();
        }

        // Rapunzel hat schon die ganze Zeit nicht gesungen

        return noTime();

    }

    private AvTimeSpan onTimePassed_NichtZeitZumSingen_bislangGesungen() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return noTime();
        }

        // TODO Frosch läuft während des Schlafs weg. Oder kommt ggf. Auch wieder. Oder läuft
        //  weg und kommt wieder.
        //  Es sollte in der Zeit keine narrations geben (der Spieler bekommt ja nichts mit, es sei
        //  denn er wacht auf). Aber danach sollte etwas kommen wie... ist verschwunden.
        //  Wie macht man das sinnvoll?
        //  Für die Tageszeit haben wir ein gutes Konzept.
        //  Für andere Dinge (Frosch weg, Kugel weg) scheint es nicht zu funktionieren? Wenn
        //  man die Zeit mittendrin weiterlaufen lässt, funktioniert das mit den Tageszeiten
        //  nicht mehr!
        //  Man könnte sagen: Schlafen ist wie Bewegen: Es gibt eine neue Beschreibung der
        //  äußeren Umstände, zumindest soweit sie sich verändert haben. Dazu muss der Unterschied
        //  (vorher / nachher) ermittelt werden. Und die Zeit muss zwischendrin vergehen -
        //  allerdings ohne narration.
        //  Andere Idee könnte sein: Beim Vergehen von Zeit gibt es DREI Parameter:
        //  letzter Zeitpunkt, letzter WACHER Zeitpunkt und aktueller Zeitpunkt
        //  Entsprechend kann dann der Text gestaltet werden, z.B. "Der Frosch ist verschwunden."

        // STORY Konzept entwickeln, dass diese "Statusübergänge" realisiert:
        //  - Benutzer rastet für längere Zeit (wach) und Rapunzel beginnt mehrfach
        //    zu singen und hört wieder auf, letztlich hat Rapunzel aufgehört
        //  - Benutzer schläft ein, während Rapunzel singt, aufhört und wieder anfängt
        //  - Benutzer schläft ein, während Rapunzel singt und wacht auf und Rapunzel hat
        //    zwischenzeitlich aufgehört zu singen

        // TODO Idee: Jede Reaktion speichert den letzten Zustand (PCD), auf Basis dessen sie einen
        //  Text gerendert hat sowie den Zeitpunkt dazu. Wenn wieder Gelegenheit ist, ein Text zu
        //  rendern, wird geprüft, ob sich der Status gegenüber dem Zeitpunkt geändert hat,
        //  außerdem wird geprüft, ob der Zeitpunkt Benutzer etwas versäumt hat oder die ganze
        //  Zeit anwesend und aufnahmefähig war - entsprechend etwas wie "Plötzlich endet der Gesang"
        //  oder "Es ist kein Gesang mehr zu hören" gerendert.

        // STORY Zum Beispiel wäre der Benutzer über alle Statusänderungen zu unterrichten,
        //  Die zwischenzeitlich passiert sind ("der Frosch ist verschwunden").

        // STORY Man könnte auch, wenn der Benutzer erstmals wieder nach draußem kommt, etwas
        //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
        //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
        //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
        //  werden.
        //  Man müsste also die Möglichkeit anbieten, jederzeit den Status eines bestimmten
        //  Game Objects unter einem "Label" zu persistieren (inkl. Zeitpunkt), so dass
        //  man ihn später wieder laden kann. Alternativ auch mehrere Game Objects,
        //  denn nur so kann man prüfen, was sich nach dem Schlafen an einem Ort verändert hat.

        // TODO Der Benutzer (oder auch andere Game Objects) könnte auch ein "mental Model" habe, wo
        //  der Stand der Welt, wie der Benutzer ihn sich vorstellt, gespeichert ist
        //  (z.B. wie ein Raum war als der Benutzer ihn verlassen hat...)
        //  Dann könnte man beim Erzählen vergleich...

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