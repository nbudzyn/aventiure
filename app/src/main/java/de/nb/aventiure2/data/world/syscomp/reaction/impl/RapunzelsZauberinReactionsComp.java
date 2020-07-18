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
        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocation())) {
            // SC und Zauberin sind nich am gleichen Ort
            return noTime();
        }

        return onSCTrifftZauberin(from, to);
    }

    private AvTimeSpan onSCTrifftZauberin(@Nullable final ILocationGO from,
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

    private AvTimeSpan narrateScTrifftZauberin_Default(@Nullable final ILocationGO from,
                                                       final ILocationGO to) {
        final Nominalphrase desc = getDescription();

        if (!movementComp.isMoving()) {
            return n.addAlt(
                    neuerSatz(SENTENCE,
                            "Hier steht " +
                                    desc.nom(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH),
                    neuerSatz(PARAGRAPH,
                            "Du begegnest " + desc.dat(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH)
            );
        }

        if (from != null && locationComp.lastLocationWas(from) &&
                movementComp.isEntering()) {
            // Zauberin in SC sind denselben Weg gegangen, die Zauberin ist eigentlich noch nicht
            // ganz angekommen
            return n.add(du("gehst an " +
                    desc.dat() +
                    " vorbei", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(SENTENCE));
        }

        if (locationComp.getLastLocationId() != null &&
                !world.isOrHasRecursiveLocation(from, locationComp.getLastLocationId()) &&
                movementComp.isEntering()) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Dir kommt " +
                            desc.nom() +
                            " entgegen", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        return n.add(
                neuerSatz(PARAGRAPH,
                        "Dir begegnet " + desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH)
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

            // TODO Wenn die Zauberin losgeht und der SC einmal um den Turm herumgeht, darf danach nicht
            //  als Beschreibung kommen: "Vor dem Turm sieht du die Frau stehen" - Ist
            //  das Problem gelöst?

            // TODO Sinn all diese Fälle richtig berücksichtigt?
            //  - SC geht den Weg von A nach B
            //    - und X steht in A: (Kein Text)
            //    - und X steht in B: "Hier steht X"
            //    - und X geht in B (und kommt von C): "Auf dem Weg geht X", "Von dem C-Weg her
            //       kommt X gegangen", eventuell "X geht von C nach D"?
            //    - und X geht in B (und kommt auch von A): "X kommt daher", "Auch X geht..."
            //    - und X steht oder ist am Gehen (Status) und befindet sich zurzeit in C: (Kein Text)
            //  - SC steht in A
            //    - und X geht von A über den Weg nach B: "X geht auf den Weg davon"
            //    - und X kommt von B über den Weg nach A: "Über den Weg kommt X"
            //  - In aller Regel bewegt sich immer nur EINER von beiden: SC oder X
            //    - Wir brauchen also keine allgemeingültige Lösung für diese Fälle:
            //      - SC geht von A nach B, gleichzeitig geht X von B nach A
            //      - SC und B gehen gleichzeitig von A nach B

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
        final Nominalphrase desc = getDescription();
        return n.add(neuerSatz(PARAGRAPH,
                // STORY Andere / schönere Formulierungen
                desc.nom() +
                        " geht von dannen", noTime())
                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                .beendet(PARAGRAPH));
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

        // TODO Wenn die Zauberin dabei ist, den Pfad hinunterzukommen,
        //  und der SC unten vor dem Pfad steht, müsste er die Zauberin treffen!
        //  Funktioniert das?

        // TODO Wenn der SC vom Wald den Pfad hinaufgeht und die Zauberin
        //  gleichzeitig dabei ist, den Pfad hinunterzukommen, müsste der SC die Zauberin treffen! -
        //  Funktioniert das?

        // TODO Wenn die Zauberin dabei ist, den Pfad hinunterzukommen,
        //  und der SC gerade den Pfad hinaufgegangen ist, müsste er die Zauberin treffen!
        //  Funktioniert das?
        if (!world.isOrHasRecursiveLocation(scLastLocation, from)) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Dir kommt " +
                            desc.nom() +
                            " entgegen", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        // Default
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);
        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt herzu", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt gegangen", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        "Es kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt daher", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt gegangen", noTime())
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