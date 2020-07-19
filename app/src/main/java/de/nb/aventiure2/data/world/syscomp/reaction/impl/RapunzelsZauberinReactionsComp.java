package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.OBEN_IM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.BESUCHT_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.days;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

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

    private static final AvTimeSpan BESUCHSDAUER = hours(1);

    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;
    private final MovementComp movementComp;

    private final RapunzelsZauberinMovementNarrator movementNarrator;

    public RapunzelsZauberinReactionsComp(final AvDatabase db,
                                          final World world,
                                          final AbstractDescriptionComp descriptionComp,
                                          final RapunzelsZauberinStateComp stateComp,
                                          final LocationComp locationComp,
                                          final MovementComp movementComp) {
        super(RAPUNZELS_ZAUBERIN, db, world, descriptionComp);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.movementComp = movementComp;

        movementNarrator = new RapunzelsZauberinMovementNarrator(
                n, world, descriptionComp);
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

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO scFrom, final ILocationGO scTo) {
        if (scFrom != null &&
                locationComp.getLocationId() != null &&
                world.isOrHasRecursiveLocation(scFrom, locationComp.getLocationId())) {
            // SPEZIALFÄLLE, SC und die Zauberin treffen noch in scFrom zusammen:

            if (movementComp.isLeaving() && movementComp.getTargetLocation().is(scTo)) {
                // Zauberin verlässt gerade auch scFrom und will auch nach scTo
                return narrateScUeberholtZauberin();
            }
            if (movementComp.isEntering() &&
                    locationComp.getLastLocationId() != null &&
                    world.isOrHasRecursiveLocation(locationComp.getLastLocationId(), scTo)) {
                // Zauberin hat scFrom schon betreten und kommt von scTo
                return narrateScGehtZauberinEntgegenUndLaesstSieHinterSich();
            }
        }

        if (locationComp.getLocationId() != null &&
                !world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocationId())) {
            // SC und Zauberin sind nicht am gleichen Ort
            return noTime();
        }

        return onSCTrifftZauberinInTo(scFrom, scTo);
    }

    private AvTimeSpan onSCTrifftZauberinInTo(@Nullable final ILocationGO scFrom,
                                              final ILocationGO scTo) {
        if (scTo.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        final Nominalphrase desc = getDescription();

        final AvTimeSpan extraTime;

        if (world.isOrHasRecursiveLocation(scTo, IM_WALD_NAHE_DEM_SCHLOSS)) {

            // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
            //  den SC mit bösen und giftigen Blicken an?

            if (!movementComp.isMoving()) {
                extraTime = n.add(neuerSatz(SENTENCE,
                        "Auf dem Weg steht " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (world.isOrHasRecursiveLocation(scFrom, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    locationComp.lastLocationWas(VOR_DEM_ALTEN_TURM) &&
                    movementComp.isEntering()) {

                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (world.isOrHasRecursiveLocation(scFrom, ABZWEIG_IM_WALD) &&
                    locationComp.lastLocationWas(VOR_DEM_ALTEN_TURM) &&
                    movementComp.isEntering()) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else {
                extraTime = narrateScTrifftZauberin_Default(scFrom, scTo);
            }
        } else if (world.isOrHasRecursiveLocation(scTo, VOR_DEM_ALTEN_TURM)) {
            if (!movementComp.isMoving()) {
                if (world.isOrHasRecursiveLocation(scFrom, IM_WALD_NAHE_DEM_SCHLOSS)) {
                    // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
                    //  den SC mit bösen und giftigen Blicken an?
                    extraTime = n.add(neuerSatz(SENTENCE,
                            "Vor dem Turm steht " +
                                    desc.nom(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH));
                } else if (world.isOrHasRecursiveLocation(scFrom, VOR_DEM_ALTEN_TURM)) {
                    final Nominalphrase descShort = getDescription(true);

                    extraTime = n.add(neuerSatz(SENTENCE,
                            "Vor dem Turm siehst du " +
                                    descShort.nom() +
                                    " stehen", noTime())
                            .phorikKandidat(descShort, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH));
                } else if (world.isOrHasRecursiveLocation(scFrom, OBEN_IM_ALTEN_TURM)) {
                    // STORY "Unten vor dem Turm steht eine..."
                    // STORY Reaktion der Zauberin: Sie zaubert einen Vergessenszauber?!
                    //  Auch für Rapunzel??
                    //  Sieht sie einen zornig an?
                    extraTime = narrateScTrifftZauberin_Default(scFrom, scTo);
                } else {
                    extraTime = narrateScTrifftZauberin_Default(scFrom, scTo);
                }
            } else {
                if (scFrom != null &&
                        locationComp.lastLocationWas(IM_WALD_NAHE_DEM_SCHLOSS) &&
                        !scFrom.is(IM_WALD_NAHE_DEM_SCHLOSS) &&
                        movementComp.isEntering()) {
                    extraTime = n.add(neuerSatz("Den Pfad herauf kommt " +
                                    desc.nom(),
                            noTime()
                            // TODO Etwas unklar: Hier automatisch DIE ZEIT ABWARTEN,
                            //  die die Zauberin noch braucht?
                            //  Oder bei späteren Aktionen ggf. die Zeit abwarten?
                            //  Oder später den Zustand prüfen (z.B. wenn der Spieler
                            //  vor der Zauberin fliehen möchte)?
                    )
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(
                                    SENTENCE));
                } else {
                    extraTime = narrateScTrifftZauberin_Default(scFrom, scTo);
                }
            }
        } else {
            extraTime = narrateScTrifftZauberin_Default(scFrom, scTo);
        }

        // STORY Reaktion der Zauberin, wenn SC die Zauberin oben im Turm antrifft
        //  (falls das sein kann).

        world.upgradeKnownToSC(RAPUNZELS_ZAUBERIN);

        return extraTime;
    }

    // TODO Die Default-Sonderfälle an einen zentraleren Ort umziehen - z.B.
    //  eine Default-Implementierung des IMovementNarrator-Interfaces,
    //  von dem man dann bei Bedarf EINZELNE METHODEN implementieren kann
    //  (so in der Art narrateKommtEntgegen() und ggf. super... aufrufen)!
    // TODO Dann alle Default-Sonderfälle für mit den für Einzelfälle vorgegbenen
    //  Fällen vergleichen und ggf. die Einzelfälle ergänzen!
    private AvTimeSpan narrateScTrifftZauberin_Default(@Nullable final ILocationGO scFrom,
                                                       final ILocationGO scTo) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        if (!movementComp.isMoving()) {
            return n.addAlt(
                    neuerSatz(SENTENCE,
                            "Hier steht " +
                                    desc.nom(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                    du("triffst",
                            desc.akk(), noTime())
                            .undWartest()
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                    du("triffst auf",
                            desc.akk(), noTime())
                            .undWartest()
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                    du(SENTENCE,
                            "begegnest",
                            desc.dat(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH)
            );
        }

        if (movementComp.isEntering() && scFrom != null &&
                locationComp.getLastLocationId() != null) {
            if (world.isOrHasRecursiveLocation(scFrom, locationComp.getLastLocationId())) {
                // Zauberin in SC sind denselben Weg gegangen, die Zauberin ist noch nicht
                // im "Zentrum" angekommen
                return narrateScUeberholtZauberin();
            }

            return movementNarrator.narrateZauberinKommtScEntgegen();
        }

        if (movementComp.isLeaving()) {
            if (scFrom != null &&
                    world.isOrHasRecursiveLocation(scFrom, movementComp.getTargetLocation())) {
                return narrateScGehtZauberinEntgegenUndLaesstSieHinterSich();
            }

            return n.addAlt(
                    du(SENTENCE, "siehst",
                            ", wie " +
                                    anaphOderDesc.nom() +
                                    " von dannen geht", noTime())
                            .komma()
                            .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                    du(SENTENCE, "siehst",
                            anaphOderDesc.akk() +
                                    " davongehen", noTime())
                            .komma()
                            .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                    neuerSatz(SENTENCE,
                            anaphOderDesc.nom() +
                                    " ist gerade dabei, davonzugehen", noTime())
                            .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH),
                    neuerSatz(SENTENCE,
                            "Vor dir geht " +
                                    desc.nom(), noTime())
                            .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                    neuerSatz(SENTENCE,
                            "Ein Stück vor dir geht " +
                                    desc.nom(), noTime())
                            .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                    neuerSatz(SENTENCE,
                            desc.nom() +
                                    " geht gerade fort", noTime())
                            .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH)
            );
        }

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir begegnet " + desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                du("begegnest ",
                        desc.dat(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
        );
    }

    private AvTimeSpan narrateScGehtZauberinEntgegenUndLaesstSieHinterSich() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(
                du(SENTENCE,
                        anaphOderDesc.nom() +
                                " kommt dir entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht hinter dir davon", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und läuft vorbei", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH)
        );
    }

    private AvTimeSpan narrateScUeberholtZauberin() {
        final Nominalphrase desc = getDescription();

        return n.addAlt(
                du("gehst",
                        "an " +
                                desc.dat() +
                                " vorbei", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE),
                du("gehst schnellen Schrittes an " +
                        desc.dat() +
                        " vorüber", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE),
                du("gehst mit schnellen Schritten an " +
                        desc.dat() +
                        " vorüber", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE)
        );
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        checkArgument(!now.minus(lastTime).longerThan(days(1)),
                "World tick time too long - see AbstractScAction.");

        switch (stateComp.getState()) {
            case VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH:
                return onTimePassed_fromVorDemNaechstenRapunzelBesuch(now)
                        ;
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                return onTimePassed_fromAufDemWegZuRapunzel(now);
            case BESUCHT_RAPUNZEL:
                return onTimePassed_fromBesuchtRapunzel(now);
            // STORY Zauberin überrascht den Spieler oben im Turm
//            if (world.loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
//                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
//                //  an sie erinnern kann.
//                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
//                return noTime();
//            }
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
                movementComp.startMovement(now, VOR_DEM_ALTEN_TURM, movementNarrator)
        );
    }


    private AvTimeSpan onTimePassed_fromAufDemWegZuRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now, movementNarrator);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Weg
            return extraTime;
        }

        // Zauberin ist unten am alten Turm angekommen.

        // STORY Zunächst sollte die Zauberin den Turm besuchen:
        //  Zauberin ruft Rapunzel (wenn der Spieler nicht vor Ort ist) und
        //  lässt sich hochziehen

        stateComp.setState(BESUCHT_RAPUNZEL);

        // TODO Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  erst jetzt (also zu spät) in den BESUCHT_RAPUNZEL-State.
        //  Rapunzel wird also zu lange besucht.
        //  Denkbare Lösungen:
        //  - Durch ein zentrales Konzept beheben (World-Ticks nie zu lang)
        //  - Zeit zwischen Ankunft und now von der Rapunzel-Besuchszeit abziehen
        //    und irgendwo (wo? hier in der Reactions-Comp?) speichern, wann
        //    der Besucht vorbei sein soll (besuchsEndeZeit = ankunft + BESUCH_DAUER)

        // STORY Zauberin ruft Rapunzel, vergebliches Warten, Erkennen, dass Rapunzel befreit wurde

        return extraTime;
    }

    private AvTimeSpan onTimePassed_fromBesuchtRapunzel(final AvDateTime now) {
        if (now.isBefore(
                stateComp.getStateDateTime().plus(BESUCHSDAUER))) {
            // Zauberin bleibt noch bei Rapunzel
            return noTime();
        }

        // Zauberin verlässt Rapunzel
        stateComp.setState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
        return movementComp.startMovement(now, DRAUSSEN_VOR_DEM_SCHLOSS, movementNarrator);
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now, movementNarrator);

        if (movementComp.isMoving()) {
            // Zauberin ist noch auf dem Rückweg
            return extraTime;
        }

        // Zauberin hat den Rückweg zurückgelegt.
        return onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch();
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch() {
        return locationComp.narrateAndUnsetLocation(
                // Zauberin "verschwindet" fürs erste
                () -> {
                    stateComp.setState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH);

                    // Keine extra-Zeit
                    return noTime();
                });
    }


}