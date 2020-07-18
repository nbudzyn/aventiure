package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.movement.IMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
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
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
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
        IMovementReactions, ITimePassedReactions,
        // Beschreibt dem Spieler die Bewegung der Zauberin
        IMovementNarrator {
    private static final AvTime LOSGEHZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM = oClock(14);

    private static final AvTimeSpan BESUCHSDAUER = hours(1);

    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;
    private final MovementComp movementComp;

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

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (from != null &&
                locationComp.getLocationId() != null &&
                world.isOrHasRecursiveLocation(from, locationComp.getLocationId())) {
            // SPEZIALFÄLLE, SC und die Zauberin treffen noch in from zusammen:

            if (movementComp.isLeaving() && movementComp.getTargetLocation().is(to)) {
                // Zauberin verlässt gerade auch from und will auch nach to
                return narrateScUeberholtZauberin();
            }
            if (movementComp.isEntering() &&
                    world.isOrHasRecursiveLocation(locationComp.getLastLocationId(), to)) {
                // Zauberin hat from schon betreten und kommt von to
                return narrateScGehtZauberinEntgegenUndLaesstSieHinterSich();
            }
        }

        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocation())) {
            // SC und Zauberin sind nicht am gleichen Ort
            return noTime();
        }

        return onSCTrifftZauberinInTo(from, to);
    }

    private AvTimeSpan onSCTrifftZauberinInTo(@Nullable final ILocationGO from,
                                              final ILocationGO to) {
        if (to.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        final Nominalphrase desc = getDescription();

        final AvTimeSpan extraTime;

        if (world.isOrHasRecursiveLocation(to, IM_WALD_NAHE_DEM_SCHLOSS)) {

            // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
            //  den SC mit bösen und giftigen Blicken an?

            if (!movementComp.isMoving()) {
                extraTime = n.add(neuerSatz(SENTENCE,
                        "Auf dem Weg steht " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (world.isOrHasRecursiveLocation(from, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    locationComp.lastLocationWas(VOR_DEM_ALTEN_TURM) &&
                    movementComp.isEntering()) {

                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (world.isOrHasRecursiveLocation(from, ABZWEIG_IM_WALD) &&
                    locationComp.lastLocationWas(VOR_DEM_ALTEN_TURM) &&
                    movementComp.isEntering()) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else {
                extraTime = narrateScTrifftZauberin_Default(from, to);
            }
        } else if (world.isOrHasRecursiveLocation(to, VOR_DEM_ALTEN_TURM)) {
            if (!movementComp.isMoving()) {
                if (world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
                    // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
                    //  den SC mit bösen und giftigen Blicken an?
                    extraTime = n.add(neuerSatz(SENTENCE,
                            "Vor dem Turm steht " +
                                    desc.nom(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH));
                } else if (world.isOrHasRecursiveLocation(from, VOR_DEM_ALTEN_TURM)) {
                    final Nominalphrase descShort = getDescription(true);

                    extraTime = n.add(neuerSatz(SENTENCE,
                            "Vor dem Turm siehst du " +
                                    descShort.nom() +
                                    " stehen", noTime())
                            .phorikKandidat(descShort, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH));
                } else if (world.isOrHasRecursiveLocation(from, OBEN_IM_ALTEN_TURM)) {
                    // STORY "Unten vor dem Turm steht eine..."
                    // STORY Reaktion der Zauberin: Sie zaubert einen Vergessenszauber?!
                    //  Auch für Rapunzel??
                    //  Sieht sie einen zornig an?
                    extraTime = narrateScTrifftZauberin_Default(from, to);
                } else {
                    extraTime = narrateScTrifftZauberin_Default(from, to);
                }
            } else {
                if (locationComp.lastLocationWas(IM_WALD_NAHE_DEM_SCHLOSS) &&
                        !from.is(IM_WALD_NAHE_DEM_SCHLOSS) &&
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
                    extraTime = narrateScTrifftZauberin_Default(from, to);
                }
            }
        } else {
            extraTime = narrateScTrifftZauberin_Default(from, to);
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
    private AvTimeSpan narrateScTrifftZauberin_Default(@Nullable final ILocationGO from,
                                                       final ILocationGO to) {
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

        if (movementComp.isEntering() && from != null &&
                locationComp.getLastLocationId() != null) {
            if (world.isOrHasRecursiveLocation(from, locationComp.getLastLocationId())) {
                // Zauberin in SC sind denselben Weg gegangen, die Zauberin ist noch nicht
                // im "Zentrum" angekommen
                return narrateScUeberholtZauberin();
            }

            return narrateZauberinKommtScEntgegen();
        }

        if (movementComp.isLeaving()) {
            if (from != null &&
                    world.isOrHasRecursiveLocation(from, movementComp.getTargetLocation())) {
                return narrateScGehtZauberinEntgegenUndLaesstSieHinterSich();
            }

            return n.addAlt(
                    du(SENTENCE, "siehst",
                            "noch, wie " +
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
    protected Nominalphrase getDescription() {
        return super.getDescription();
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
        if (now.getTime().isBefore(LOSGEHZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie losgeht.
            return noTime();
        }

        // Zustandswechsel nötig! Die Zauberin geht zu Rapunzel los.
        return onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel(now);

        // TODO Wenn der World-Tick ungewöhnlich lang war, geht die Zauberin
        //  erst jetzt (also zu spät) los. Ggf. sogar nachts o.Ä.
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
                movementComp.startMovement(now, VOR_DEM_ALTEN_TURM, this)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsLeaving(
            final FROM from, final ILocationGO to) {
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        final AvTimeSpan extraTime;

        // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
        //  den SC mit bösen und giftigen Blicken an?

        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();

        if (!world.isOrHasRecursiveLocation(scLastLocation, IM_WALD_NAHE_DEM_SCHLOSS) &&
                from.is(VOR_DEM_ALTEN_TURM) && to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            final SubstantivischePhrase anaphOderDesc =
                    getAnaphPersPronWennMglSonstDescription(false);

            // TODO Movement-Componente
            //  - Wenn X noch in Bewegung ist und die Zeit für den Schritt noch nicht
            //    abegelaufen ist, kann SC mit X interagieren (z.B. mit X reden), aber
            //    es wird die Restzeit noch abgewartet. Vielleicht Zusatztext in der Art
            //    "Du wartest, bis ... herangekommen ist und"...
            //    Außerdem wird möglicherweise die Bewegung "ausgesetzt" und (zumindest von der
            //    Zeitmessung her) erst nach der Aktion forgesetzt. Z.B. auch erst
            //    nach einem Dialog (sofern X auf den Dialog eingeht und ihn nicht von sich
            //    aus beendet)

            extraTime = n.add(neuerSatz(PARAGRAPH,
                    // TODO Nicht schön: "Vor dem Turm siehst du die Frau stehen. Sie geht den
                    //  Pfad hinab." Besser wäre "Dann geht sie den Pfad hinab."
                    //  - Denkbar wäre, .dann() optional mit einem Akteur zu qualifizieren:
                    //    .dann(RAPUNZELS_ZAUBERIN). Ein "Dann" würde nur dann
                    //    erzeugt, wenn der Folgesatz denselben Akteur hat.
                    anaphOderDesc.nom() +
                            " geht den Pfad hinab", noTime())
                    .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        } else {
            // Default
            extraTime = narrateAndDoMovementAsExperiencedBySC_StartsLeaving_Default(from, to);
        }

        world.upgradeKnownToSC(RAPUNZELS_ZAUBERIN);

        return extraTime;
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsLeaving_Default(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();

        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        if (world.isOrHasRecursiveLocation(scLastLocation, to)) {
            return n.addAlt(
                    neuerSatz(PARAGRAPH,
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
                                    " entgegen und geht hinter dir von dannen", noTime())
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

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht von dannen", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht davon", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weiter", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weg", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                // STORY: "Die Zauberin geht ihres Wegs" - Possessivartikel vor Genitiv!
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht fort", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft vorbei", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft weiter", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsEntering(
            final FROM from, final ILocationGO to) {
        if (to.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final AvTimeSpan extraTime;
        if (to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
            //  den SC mit bösen und giftigen Blicken an?
            if (world.isOrHasRecursiveLocation(scLastLocation, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    from.is(VOR_DEM_ALTEN_TURM)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (world.isOrHasRecursiveLocation(scLastLocation, ABZWEIG_IM_WALD) &&
                    from.is(VOR_DEM_ALTEN_TURM)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else {
                extraTime = narrateMovementAsExperiencedBySC_StartsEntering_Default(from, to);
            }
        } else if (to.is(VOR_DEM_ALTEN_TURM)) {
            // STORY Spieler sieht von unten, wie die Zauberin heruntersteigt?

            // STORY Zauberin überrascht den Spieler vor dem Turm
//                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
//                //  an sie erinnern kann.
//                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
//                return noTime();

            if (loadSC().memoryComp().getLastAction().is(BEWEGEN) &&
                    loadSC().locationComp().lastLocationWas(IM_WALD_NAHE_DEM_SCHLOSS) &&
                    from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt hinter dir den Pfad herauf", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt den Pfad herauf", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else {
                extraTime = narrateMovementAsExperiencedBySC_StartsEntering_Default(from, to);
            }
        } else {
            extraTime = narrateMovementAsExperiencedBySC_StartsEntering_Default(from, to);
        }

        world.upgradeKnownToSC(RAPUNZELS_ZAUBERIN);

        return extraTime;
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovementAsExperiencedBySC_StartsEntering_Default(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();
        final Nominalphrase desc = getDescription();

        // TODO Wenn der SC vom Wald den Pfad hinaufgeht und die Zauberin
        //  gleichzeitig dabei ist, den Pfad hinunterzukommen, müsste der SC die Zauberin treffen! -
        //  Funktioniert das?

        if (loadSC().memoryComp().getLastAction().is(BEWEGEN)) {
            if (world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return n.addAlt(
                        neuerSatz(PARAGRAPH,
                                "Dir kommt " +
                                        desc.nom() +
                                        " nach", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Hinter dir geht " +
                                        desc.nom(), noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Hinter kommt " +
                                        desc.nom() +
                                        " gegangen", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Dir kommt " +
                                        desc.nom() +
                                        " heran", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH)
                );
            }

            if (!world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return narrateZauberinKommtScEntgegen();
            }
        }

        // Default
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);
        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt herzu", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt heran", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt gegangen", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        "Es kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt daher", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt gegangen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
        );
    }

    private AvTimeSpan narrateZauberinKommtScEntgegen() {
        final Nominalphrase desc = getDescription();

        // STORY Wenn to mehr als zwei Zugänge hat ist auch bei "entgegen" nicht klar,
        //  woher die Zauberin kommt. Aus der SpatialConnection Details erfragen, z.B.
        //  "den kleinen Pfad herab", "auf dem Weg geht X",
        //  "Von dem Pfad her kommt X gegangen"

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegengegangen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH)
        );
    }


    private AvTimeSpan onTimePassed_fromAufDemWegZuRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now, this);

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
        return movementComp.startMovement(now, DRAUSSEN_VOR_DEM_SCHLOSS, this);
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzel(final AvDateTime now) {
        final AvTimeSpan extraTime = movementComp.onTimePassed(now, this);

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