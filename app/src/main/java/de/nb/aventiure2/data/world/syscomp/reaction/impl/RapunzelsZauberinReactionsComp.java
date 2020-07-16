package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
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
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.days;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * "Reaktionen" von Rapunzels Zauberin, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelsZauberinReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    private static final AvTime LOSGEHZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM = oClock(14);

    private static final AvTimeSpan WEGZEIT_WALD_NAHE_SCHLOSS_ZUM_ALTEN_TURM = mins(30);
    private static final AvTime ANKUNFTSZEIT_UNTEN_AM_TURM =
            LOSGEHZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM
                    .rotate(WEGZEIT_WALD_NAHE_SCHLOSS_ZUM_ALTEN_TURM);

    private static final AvTimeSpan RUF_UND_HOCHZIEHZEIT = mins(2);
    private static final AvTime ANKUNFTSZEIT_OBEN_IM_TURM =
            ANKUNFTSZEIT_UNTEN_AM_TURM
                    .rotate(RUF_UND_HOCHZIEHZEIT);

    private static final AvTimeSpan BESUCHSDAUER = hours(1);
    private static final AvTime BESUCHSZEITENDE =
            ANKUNFTSZEIT_OBEN_IM_TURM.rotate(BESUCHSDAUER);

    private static final AvTime RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM =
            BESUCHSZEITENDE.rotate(WEGZEIT_WALD_NAHE_SCHLOSS_ZUM_ALTEN_TURM);

    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;

    public RapunzelsZauberinReactionsComp(final AvDatabase db,
                                          final World world,
                                          final AbstractDescriptionComp descriptionComp,
                                          final RapunzelsZauberinStateComp stateComp,
                                          final LocationComp locationComp) {
        super(RAPUNZELS_ZAUBERIN, db, world, descriptionComp);
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

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, IM_WALD_NAHE_DEM_SCHLOSS)) {
            return onSCEnter_ImWaldNaheDemSchloss(from);
        }

        if (world.isOrHasRecursiveLocation(to, VOR_DEM_ALTEN_TURM)) {
            return onSCEnter_VorDemAltenTurm(from);
        }

        // STORY Reaktion der Zauberin, wenn SC die Zauberin oben im Turm antrifft
        //  (falls das sein kann).
        return noTime();
    }

    private AvTimeSpan onSCEnter_ImWaldNaheDemSchloss(@Nullable final ILocationGO from) {
        if (!locationComp.hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS)) {
            return noTime();
        }

        final AvTimeSpan extraTime = narrateScTrifftZauberin_ImWaldNaheDemSchloss(from);

        world.upgradeKnownToSc(RAPUNZELS_ZAUBERIN, IM_WALD_NAHE_DEM_SCHLOSS);
        return extraTime;
    }

    private AvTimeSpan narrateScTrifftZauberin_ImWaldNaheDemSchloss(
            @Nullable final ILocationGO scFrom) {
        return narrateZauberinUndScTreffenSich_ImWaldNaheDemSchloss(
                scFrom,
                locationComp.getLastLocationId() != null ?
                        locationComp.getLastLocationId() :
                        DRAUSSEN_VOR_DEM_SCHLOSS
        );
    }

    @Override
    protected Nominalphrase getDescription() {
        return super.getDescription();
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm(@Nullable final ILocationGO from) {
        if (!locationComp.hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return noTime();
        }

        final AvTimeSpan extraTime = narrateScTrifftZauberin_VorDemAltenTurm(from);

        world.upgradeKnownToSc(RAPUNZELS_ZAUBERIN, VOR_DEM_ALTEN_TURM);
        return extraTime;
    }

    private AvTimeSpan narrateScTrifftZauberin_VorDemAltenTurm(@Nullable final ILocationGO scFrom) {
        if (world.isOrHasRecursiveLocation(scFrom, IM_WALD_NAHE_DEM_SCHLOSS)) {
            // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
            //  den SC mit bösen und giftigen Blicken an?
            final Nominalphrase desc = getDescription();

            return n.add(neuerSatz(SENTENCE,
                    "Vor dem Turm steht " +
                            desc.nom(), noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        if (world.isOrHasRecursiveLocation(scFrom, VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);

            return n.add(neuerSatz(SENTENCE,
                    "Vor dem Turm siehst du " +
                            desc.nom() +
                            " stehen", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        if (world.isOrHasRecursiveLocation(scFrom, OBEN_IM_ALTEN_TURM)) {
            // STORY "Unten vor dem Turm steht eine..."
            // STORY Reaktion der Zauberin: Sie zaubert einen Vergessenszauber?!
            //  Auch für Rapunzel??
            //  Sieht sie einen zornig an?
            return noTime();
        }

        // Wie kann das sein?!
        return noTime();
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        checkArgument(!now.minus(lastTime).longerThan(days(1)),
                "World tick time too long - see AbstractScAction.");

        switch (stateComp.getState()) {
            case VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH:
                return onTimePassed_fromVorDemNaechstenRapunzelBesuch(lastTime, now);
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                return onTimePassed_fromAufDemWegZuRapunzel(lastTime, now);
            case IST_OBEN_IM_TURM_BEI_RAPUNZEL:
                // STORY Zauberin geht irgendwann wieder
                return noTime();
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                // STORY Lässt sich an den Haaren herunterhiefen und wandert zurück
                return onTimePassed_fromAufDemRueckwegVonRapunzel(lastTime, now);
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Wandert zurück und kommt nie wieder
                return noTime();
            default:
                throw new IllegalStateException("Unexpected value: " + stateComp.getState());
        }
    }

    private AvTimeSpan onTimePassed_fromVorDemNaechstenRapunzelBesuch(
            final AvDateTime lastTime, final AvDateTime now) {
        if (now.getTime().isBefore(LOSGEHZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM)) {
            // Kein Zustandswechsel. Die Zauberin soll noch warten, bevor sie losgeht.
            return noTime();
        }

        if (now.getTime().isBefore(ANKUNFTSZEIT_UNTEN_AM_TURM)) {
            // Zustandswechsel nötig! Die Zauberin sollte auf dem Hinweg sein!
            return onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel();
        }

        // TODO Ab hier sind die Sonderfälle, wo der World-Tick
        //  ungewöhnlich lang war.

        // STORY Zauberin ruft Rapunzel, lässt sich hochziehen (RUF_UND_HOCHZIEHZEIT!)
        //  und besucht Raunzel -
        //  oder wartet auf Rapunzel und erkennt, dass Rapunzel befreit wurde

        if (now.getTime().isBefore(BESUCHSZEITENDE)) {
            // Die Zauberin sollte schon wieder auf dem Rückweg sein (oder
            // gemerkt habe, dass Rapunzel befreit wurde)

            // STORY Zauberin geht ggf. den Pfad, ruft Rapunzel, lässt sich hochziehen,
            //  und besucht Raunzel -
            //  oder hat auf Rapunzel gewartet und erkannt, dass Rapunzel befreit wurde
            return noTime();
        }

        if (now.getTime().isBefore(RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM)) {
            // Die Zauberin sollte schon wieder auf dem Rückweg sein

            // STORY Zauberin geht ggf. den Pfad, ruft Rapunzel, lässt sich hochziehen,
            //  und besucht Raunzel, wird wieder herabgelassen und geht zurück -
            //  oder hat auf Rapunzel gewartet und erkannt, dass Rapunzel befreit wurde
            return noTime();
        }

        // Die Zauberin sollte schon wieder zurück sein

        // STORY Zauberin macht ihren vollen Rapunzelbesuch -
        //  merkt dabei ggf., dass Rapunzel befreit wurde
        return noTime();
    }

    private AvTimeSpan onTimePassed_fromVorDemNaechstenRapunzelBesuchToAufDemWegZuRapunzel() {
        return locationComp.narrateAndSetLocation(
                // Zauberin geht in den Wald und den Pfad Richtung Turm
                IM_WALD_NAHE_DEM_SCHLOSS,
                () -> {
                    stateComp.setState(AUF_DEM_WEG_ZU_RAPUNZEL);

                    if (loadSC().locationComp()
                            .hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS)) {
                        final AvTimeSpan extraTime = narrateZauberinTrifftSc_ImWaldNaheDemSchloss(
                                DRAUSSEN_VOR_DEM_SCHLOSS);
                        world.upgradeKnownToSc(RAPUNZELS_ZAUBERIN, IM_WALD_NAHE_DEM_SCHLOSS);
                        return extraTime;
                    }

                    // Keine extra-Zeit
                    return noTime();
                });
    }

    private AvTimeSpan narrateZauberinTrifftSc_ImWaldNaheDemSchloss(
            final GameObjectId zauberinLastLocationId) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();

        return narrateZauberinUndScTreffenSich_ImWaldNaheDemSchloss(scLastLocation,
                zauberinLastLocationId);
    }

    private AvTimeSpan narrateZauberinUndScTreffenSich_ImWaldNaheDemSchloss(
            final ILocationGO scLastLocation,
            final GameObjectId zauberinLastLocationId) {
        final Nominalphrase desc = getDescription();

        // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
        //  den SC mit bösen und giftigen Blicken an?

        if (world.isOrHasRecursiveLocation(scLastLocation, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                zauberinLastLocationId == VOR_DEM_ALTEN_TURM) {

            return n.add(neuerSatz(PARAGRAPH,
                    "Von dem Pfad her kommt dir " +
                            desc.nom() +
                            " entgegen", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        if (world.isOrHasRecursiveLocation(scLastLocation, ABZWEIG_IM_WALD) &&
                zauberinLastLocationId == VOR_DEM_ALTEN_TURM) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Von dem Pfad her kommt " +
                            desc.nom(), noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        if (world.isOrHasRecursiveLocation(scLastLocation, VOR_DEM_ALTEN_TURM) &&
                zauberinLastLocationId == DRAUSSEN_VOR_DEM_SCHLOSS) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Dir kommt " +
                            desc.nom() +
                            " entgegen", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        return n.add(neuerSatz(PARAGRAPH,
                "Dir begegnet " + desc.nom(), noTime())
                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                .beendet(PARAGRAPH));
    }

    private AvTimeSpan onTimePassed_fromAufDemWegZuRapunzel(
            final AvDateTime lastTime, final AvDateTime now) {
        if (now.getTime().isBefore(LOSGEHZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM)) {
            // Die Zauberin sollte schon wieder zurück sein
            // TODO Zauberin macht ihren vollen Rapunzelbesuch -
            //  merkt dabei ggf., dass Rapunzel befreit wurde
            //  (Ein Sonderfall, nur relevant wenn die World-Tick-Zeit ungewöhnlich lang war)
            return noTime();
        }

        if (now.getTime().isBefore(ANKUNFTSZEIT_UNTEN_AM_TURM)) {
            // Kein Zustands- oder Ortswechsel. Die Zauberin geht weiter den
            // Weg zum Turm.
            return noTime();
        }

        if (now.getTime().isBefore(ANKUNFTSZEIT_OBEN_IM_TURM) &&
                // TODO Die "Karte" wird hier quasi fix mit einprogrammiert.
                //  Ungünstig für Änderungen der Karte...
                //  Besser ein Pathfinding implementieren mit
                //  Wegpunkten für die Zauberin!
                locationComp.hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS)
        ) {
            return locationComp.narrateAndSetLocation(
                    VOR_DEM_ALTEN_TURM,
                    () -> {
                        if (world.loadSC().locationComp()
                                .hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
                            final AvTimeSpan extraTime = narrateZauberinTrifftSc_VorDemAltenTurm();
                            world.upgradeKnownToSc(RAPUNZELS_ZAUBERIN, VOR_DEM_ALTEN_TURM);
                            return extraTime;
                        }

                        // STORY Zauberin ruft Rapunzel (wenn der Spieler nicht vor Ort ist) und
                        //  lässt sich hochziehen

                        // Keine extra-Zeit
                        return noTime();
                    });

        }

        if ((now.getTime().isBefore(
                // TODO Richtige Zeit? Wie ist es mit dem Hochziehen oder dem
                //  vergeblichen Warten?
                ANKUNFTSZEIT_OBEN_IM_TURM) &&
                locationComp.hasRecursiveLocation(VOR_DEM_ALTEN_TURM))) {
            // STORY Zauberin ruft Rapunzel bzw. lässt sich hochziehen
            // STORY vergebliches Warten

            return noTime();
        }

        if ((now.getTime().isBefore(BESUCHSZEITENDE) &&
                locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM))) {
            // STORY Zauberin bleibt bei Rapunzel
            return noTime();
        }

        if (now.getTime().isBefore(BESUCHSZEITENDE)) {
            // TODO Das hier ist wohl ein Sonderfall, wo der World-Tick
            //  ungewöhnlich lang war.

            // STORY Zauberin geht ggf. den Pfad, ruft Rapunzel, lässt sich hochziehen,
            //  und besucht Raunzel
            return noTime();
        }

        if (now.getTime().isBefore(RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM) &&
                locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            // Die Zauberin sollte schon wieder auf dem Rückweg sein
            // STORY Zauberin wird wieder herabgelassen und geht zurück
            return noTime();
        }

        if (now.getTime().isBefore(RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM) &&
                locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            // Die Zauberin sollte schon wieder auf dem Rückweg sein (oder
            // gemerkt habe, dass Rapunzel befreit wurde)
            // STORY Zauberin geht ggf. den Pfad, ruft Rapunzel, lässt sich hochziehen,
            //  und besucht Raunzel, wird wieder herabgelassen und geht zurück -
            //  oder hat auf Rapunzel gewartet und erkannt, dass Rapunzel befreit wurde
            return noTime();
        }

        if (now.getTime().isBefore(RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM) &&
                locationComp.hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            // Die Zauberin sollte schon wieder auf dem Rückweg sein (oder
            // gemerkt habe, dass Rapunzel befreit wurde)

            // STORY Zauberin merkt (ggf.), dass Rapunzel befreit wurde

            return onTimePassed_fromAufDemWegZuRapunzelToAufDemRueckwegVonRapunzel();
        }

        if (now.getTime().isBefore(RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM)) {
            // Die Zauberin sollte schon wieder auf dem Rückweg sein (oder
            // gemerkt habe, dass Rapunzel befreit wurde)

            // TODO Dies ist ein Sonderfall, wo der World-Tick
            //  ungewöhnlich lang war.

            // STORY Zauberin geht zurück

            // STORY Zauberin merkt, dass Rapunzel befreit wurde
            return noTime();
        }


        // Die Zauberin sollte schon wieder zurück sein
        // STORY Zauberin macht ihren vollen Rapunzelbesuch -
        //  merkt dabei ggf., dass Rapunzel befreit wurde
        return noTime();
    }

    private AvTimeSpan onTimePassed_fromAufDemWegZuRapunzelToAufDemRueckwegVonRapunzel() {
        stateComp.setState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);

        if (loadSC().locationComp()
                .hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final AvTimeSpan extraTime = narrateZauberinVerlaesstSc_VorDemAltenTurm();
            world.upgradeKnownToSc(RAPUNZELS_ZAUBERIN, VOR_DEM_ALTEN_TURM);
            return extraTime;
        }

        // Keine extra-Zeit
        return noTime();
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzel(final AvDateTime lastTime,
                                                                  final AvDateTime now) {
        if (now.getTime().isBefore(BESUCHSZEITENDE)) {
            // TODO Das hier ist wohl ein Sonderfall, wo der World-Tick
            //  ungewöhnlich lang war.
            return noTime();
        }

        if (now.getTime().isBefore(RUEKKEHRZEIT_UNTEN_AM_PFAD_ZUM_ALTEN_TURM)) {
            // Kein Zustandswechsel. Die Zauberin ist auf dem Rückweg
            return noTime();
        }

        return onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch();
    }

    private AvTimeSpan onTimePassed_fromAufDemRueckwegVonRapunzelToVorDemNaechstenRapunzelBesuch() {
        return locationComp.narrateAndUnsetLocation(
                // Zauberin verlässt den Wald
                () -> {
                    stateComp.setState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH);

                    if (loadSC().locationComp()
                            .hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS)) {
                        final AvTimeSpan extraTime = narrateZauberinTrifftSc_ImWaldNaheDemSchloss(
                                VOR_DEM_ALTEN_TURM);
                        world.upgradeKnownToSc(RAPUNZELS_ZAUBERIN, IM_WALD_NAHE_DEM_SCHLOSS);
                        return extraTime;
                    }

                    // FIXME Auch wenn man vom Wald den Pfad hinaufgeht und die Zauberin
                    //   den Pfad hinunterkommt, müsste man die Zauberin treffen!

                    // Keine extra-Zeit
                    return noTime();
                });
    }


// STORY Zauberin überrascht den Spieler
//            if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
//                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
//                //  an sie erinnern kann.
//                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
//                return noTime();
//            }
//
//            if (world.loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
//                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
//                //  an sie erinnern kann.
//                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
//                return noTime();
//                // STORY Die Zauberin hat den Spieler (ggf. sogar mehrfach) oben im alten Turm
//                //  überrascht - sollte da nicht mehr passieren?!
//            }

    private AvTimeSpan narrateZauberinTrifftSc_VorDemAltenTurm() {
        final SubstantivischePhrase desc =
                getAnaphPersPronWennMglSonstDescription(false);

        // STORY Spieler sieht von unten, wie die Zauberin heruntersteigt?

        if (loadSC().memoryComp().getLastAction().is(BEWEGEN) &&
                loadSC().locationComp().lastLocationWas(IM_WALD_NAHE_DEM_SCHLOSS)) {
            return n.add(neuerSatz(PARAGRAPH,
                    desc.nom() +
                            " kommt hinter dir den Pfad herauf", noTime())
                    .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        return n.add(neuerSatz(PARAGRAPH,
                desc.nom() +
                        " kommt den Pfad herauf", noTime())
                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                .beendet(PARAGRAPH));
    }

    private AvTimeSpan narrateZauberinVerlaesstSc_VorDemAltenTurm() {
        final SubstantivischePhrase desc =
                getAnaphPersPronWennMglSonstDescription(false);

        // FIXME Das ist ein Problem, wenn der SC einmal um den Turm herumgeht -
        //  Dann kommt als Beschreibung: "Vor dem Turm sieht du die Frau stehen"
        //  Eigentlich ist sie aber schon einige Minuten den Pfad hinunter.

        // FIXME Wie lässt sich das lösen?
        //  Welche Fälle gibt es?
        //  - SC geht den Weg von A nach B
        //    - und X steht in A: (Kein Text)
        //    - und X steht in B: "Hier steht X" oder "Auf dem Weg geht X" (Basis ein Status von X?)
        //    - und X geht von B (ganz oder teilweise) nach C: (Kein Text)
        //    - und X geht von C Richtung B, ist aber noch nicht angekommen: (Kein Text)
        //    - und X geht von B nach A: "Auf dem Weg kommt dir X entgegen. Dann erreichst du B"
        //      (PROBLEM: Keine Interaktion mit X möglich)
        //    - und X auch geht von A nach B, aber X kommt früher in B an: "Hier steht X"
        //    - und X auch geht von A nach B, aber SC kommt früher in B an: "Auf dem Weg kommst
        //      du an X vorbei. Dann erreichst du B" (PROBLEM: Keine Interaktion mit X möglich)
        //  - X geht von A über den Weg los in Richtung B
        //    - und SC steht in A: "X geht auf den Weg davon"
        //  - X kommt über den Weg in B an (von A)
        //    - und SC steht in B: "Über den Weg kommt X"
        //  DAS KLINGT ZU KOMPLIZIERT.

        // FIXME Neue Idee:
        //  - Wesen geht immer sofort auf den nächsten Ort
        //  - und bleibt dann dort für die "Hinwegzeit".
        //  - Erst danach geht es den nächsten Schritt.
        //  Welche Fälle gibt es dann?
        //  - SC geht den Weg von A nach B
        //    - und X steht in A: (Kein Text)
        //    - und X steht in B: "Hier steht X"
        //    - und X geht in B (und kommt von C): "Auf dem Weg geht X", "Von dem C-Weg her
        //       kommt X gegangen" (Basis ein Status von X?), eventuell "X geht von C nach D"?
        //    - und X steht oder ist am Gehen (Status) und befindet sich zurzeit in C: (Kein Text)
        //  - SC steht in A
        //    - und X geht von A über den Weg nach B: "X geht auf den Weg davon"
        //    - und X kommt von B über den Weg nach A: "Über den Weg kommt X"
        //  - In aller Regel bewegt sich immer nur EINER von beiden: SC oder X
        //    - Wir brauchen also keine allgemeingültige Lösung für diese Fälle:
        //      - SC geht von A nach B, gleichzeitig geht X von B nach A
        //      - SC und B gehen gleichzeitig von A nach B

        // TODO Movement-Componente, die die Daten für eigenständige Bewegungen
        //  hält.
        //  - Weiß, wie lange ein Wesen warten muss, bis es den nächsten Schritt geht
        //  - Weiß eine Schrittfolge oder das Ziel
        //  - Weiß vielleicht, ob sich ein Wesen in Bewegung befindet?

        return n.add(neuerSatz(PARAGRAPH,
                desc.nom() +
                        " geht den Pfad hinab", noTime())
                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                .beendet(PARAGRAPH));
    }
}