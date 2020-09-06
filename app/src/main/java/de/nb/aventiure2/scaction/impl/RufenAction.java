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
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELRUF;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp.LASS_DEIN_HAAR_HERUNTER;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

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

        final StructuralElement startsNew =
                ruftyp.getName()
                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() ?
                        WORD : SENTENCE;
        String descriptionDuHauptsatz = ruftyp.getName().getDescriptionDuHauptsatz();
        if (startsNew == SENTENCE && adverbialeAngabe != null) {
            descriptionDuHauptsatz = "Und " + uncapitalize(descriptionDuHauptsatz);
        }

        AvTimeSpan timeElapsed = n.add(du(startsNew, descriptionDuHauptsatz, secs(30)));

        timeElapsed = timeElapsed.plus(
                world.narrateAndDoReactions().onRuf(sc, Ruftyp.LASS_DEIN_HAAR_HERUNTER));

        // STORY Rapunzel reagiert auf diesen Ruf! (Reations...)

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
