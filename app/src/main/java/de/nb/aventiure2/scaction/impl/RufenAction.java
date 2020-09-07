package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELRUF;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp.LASS_DEIN_HAAR_HERUNTER;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.AllgDescription.satzanschluss;

/**
 * Der Spielercharakter ruft / stößt einen Ruf aus.
 */
public class RufenAction extends AbstractScAction {
    private final ILocationGO location;
    private final Ruftyp ruftyp;

    public static ImmutableList<AbstractScAction> buildActions(
            final AvDatabase db,
            final World world,
            @NonNull final ILocationGO location) {
        // STORY Alle Aktionen mit allen Gegenständen überall erlauben

        // STORY Überall kann man etwas wie "Ist da wer?" rufen (wenn man
        //  allein ist) Hexe, Rapunzel, Frosch, Schlosswache
        //  reagierem lassen -> Alternatover weg zu einem späteren milestone

        final ImmutableList.Builder<AbstractScAction> res = builder();

        if (world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
            if (world.isOrHasRecursiveLocation(location, VOR_DEM_ALTEN_TURM)
                // STORY && rapunzelIstNochNichtBefreit()
            ) {
                res.add(new RufenAction(db, world, location, LASS_DEIN_HAAR_HERUNTER));
            }
        }

        return res.build();
    }

    public RufenAction(final AvDatabase db, final World world, final ILocationGO location,
                       final Ruftyp ruftyp) {
        super(db, world);
        this.location = location;
        this.ruftyp = ruftyp;
    }

    @Override
    public String getType() {
        return "actionRedenRufen";
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(ruftyp.getName().getDescriptionInfinitiv(P1, SG));
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        @Nullable AdverbialeAngabe adverbialeAngabe = null;
        if (sc.memoryComp().getLastAction().is(Action.Type.RUFEN)) {
            adverbialeAngabe = new AdverbialeAngabe("noch einmal");
        }

        AvTimeSpan timeElapsed;
        if (n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt()
                && adverbialeAngabe == null) {
            timeElapsed =
                    n.add(satzanschluss(
                            "und " +
                                    ruftyp.getName()
                                            .getDescriptionHauptsatzMitEingespartemVorfeldSubj(),
                            secs(30)));
        } else if (adverbialeAngabe != null) {
            timeElapsed =
                    n.add(neuerSatz(
                            ruftyp.getName().getDescriptionDuHauptsatz(adverbialeAngabe),
                            secs(30)));
        } else {
            timeElapsed =
                    n.add(neuerSatz(
                            "Und " +
                                    uncapitalize(
                                            ruftyp.getName()
                                                    .getDescriptionDuHauptsatz()),
                            secs(30)));
        }

        timeElapsed = timeElapsed.plus(
                world.narrateAndDoReactions().onRuf(sc, Ruftyp.LASS_DEIN_HAAR_HERUNTER));

        // STORY Reaktion der Zauberin auf den Ruf. Wenn der SC ruft
        //  und die Zauberin in der Nähe ist, weiß sie danach auch, wo
        //  der SC sich befindet.
        //  ((IHasMentalModelGO) talker).mentalModelComp()
        //        .assumesLocation(
        //                SPIELER_CHARAKTER,
        //                world.loadSC().locationComp().getLocation());

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeElapsed;
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Der Ruf-Typ wird derzeit nicht in der Aktion gespeichert (es gibt kein
        // Feld passenden Typs dafür, er hat ja keine GameObjectId).
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.RUFEN);
    }
}
