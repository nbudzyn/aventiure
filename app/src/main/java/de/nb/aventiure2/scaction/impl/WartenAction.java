package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.DescriptionUmformulierer;
import de.nb.aventiure2.german.description.Kohaerenzrelation;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WARTEN;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class WartenAction<LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
        extends AbstractScAction {
    private final LIVGO erwartet;
    private final ILocationGO location;

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    List<WartenAction<LIVGO>> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker, final Narrator n, final World world,
            final LIVGO erwartet,
            final ILocationGO location) {
        final ImmutableList.Builder<WartenAction<LIVGO>> res = ImmutableList.builder();
        if (location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME) &&
                erwartet.is(RAPUNZELS_ZAUBERIN) &&
                world.loadSC().memoryComp().isKnown(RAPUNZELS_ZAUBERIN) &&
                !erwartet.locationComp().hasSameOuterMostLocationAs(location)) {
            res.add(new WartenAction<>(scActionStepCountDao, timeTaker, n, world, erwartet,
                    location));
        }

        return res.build();
    }

    @VisibleForTesting
    WartenAction(final SCActionStepCountDao scActionStepCountDao,
                 final TimeTaker timeTaker,
                 final Narrator n,
                 final World world,
                 final LIVGO erwartet,
                 final ILocationGO location) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.erwartet = erwartet;
        this.location = location;
    }

    @Override
    public String getType() {
        return "actionRastenWarten";
    }

    @Override
    @NonNull
    public String getName() {
        // "Auf die magere Frau warten"
        return GermanUtil.capitalize(
                GermanUtil.joinToString(
                        WARTEN
                                .mit(world.getDescription(erwartet))
                                .getInfinitiv(P2, SG)
                )
        );
    }

    @Override
    public void narrateAndDo() {
        // FIXME Aktion
        //  - Vermutlich braucht man weitere Möglichkeiten, bei denen das Warten abgebrochen wird,
        //    z.B. wenn der Spieler müder oder hungriger wird?
        //  - Rapunzels Gesang sollte das Warten abbrechen - wenn man ihn noch nicht kennt.
        //  - Reactions-Componente anweisen: Unterbrich den Wartemodus, z.B. wenn
        //    der Spieler hungriger wird oder müder oder ein Tageszeitenwechsel geschieht o.Ä.).

        // Der SC wartet
        narrate();

        // Erst einmal vergeht fast keine Zeit. Die ScAutomaticReactionsComp sorgt
        // im onTimePassed() im Zusammenspiel mit der WaitingComp dafür, dass die
        // Zeit vergeht (maximal 3 Stunden).
        world.loadSC().waitingComp().startWaiting(
                timeTaker.now().plus(hours(3)));

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrate() {
        final Kohaerenzrelation kohaerenzrelation = getKohaerenzrelationFuerUmformulierung();

        if (kohaerenzrelation == VERSTEHT_SICH_VON_SELBST) {
            final SubstantivischePhrase anaph =
                    world.getAnaphPersPronWennMglSonstDescription(erwartet, false
                    );
            n.narrateAlt(secs(5),
                    du(WARTEN.mit(anaph))
                            .dann()
                            .phorikKandidat(anaph, erwartet.getId()),
                    du("beginnst",
                            "auf "
                                    + anaph.akk()
                                    + " zu warten")
                            .dann()
                            .phorikKandidat(anaph, erwartet.getId()));
        } else {
            final SubstantivischePhrase anaph =
                    world.getAnaphPersPronWennMglSonstDescription(erwartet, true);
            n.narrateAlt(
                    DescriptionUmformulierer.drueckeAus(
                            kohaerenzrelation,
                            du(WARTEN
                                    .mit(anaph)
                                    .mitAdverbialerAngabe(
                                            new AdverbialeAngabeSkopusSatz("weiter")
                                    )
                            )
                                    .dann()
                                    .phorikKandidat(anaph, erwartet.getId())
                    ), secs(5));
        }
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.WARTEN, erwartet);
    }
}
