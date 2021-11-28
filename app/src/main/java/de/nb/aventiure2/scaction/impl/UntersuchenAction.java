package de.nb.aventiure2.scaction.impl;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAusTimed;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_WENDEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.SICH_ZUWENDEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspectableGO;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspection;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der SC untersucht ein Game Object.
 */
public class UntersuchenAction extends AbstractScAction {
    @NonNull
    private final IInspection inspection;

    /**
     * Erzeugt alle Aktionen, mit denen der SC dieses <code>gameObject</code>
     * untersuchen kann.
     */
    public static <INSPECTABLE extends IDescribableGO & ILocatableGO & IInspectableGO>
    Collection<UntersuchenAction> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            final INSPECTABLE inspectable) {
        if (world.isOrHasRecursiveLocation(inspectable, SPIELER_CHARAKTER)) {
            return ImmutableList.of();
        }

        if (!inspectable.locationComp().hasVisiblyRecursiveLocation(
                world.loadSC().locationComp().getLocationId())) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<UntersuchenAction> res = ImmutableList.builder();

        final ImmutableList<IInspection> inspections =
                inspectable.inspectionComp().getInspections();

        for (final IInspection inspection : inspections) {
            res.add(new UntersuchenAction(scActionStepCountDao, timeTaker,
                    n, world, inspection));
        }

        return res.build();
    }

    private UntersuchenAction(final SCActionStepCountDao scActionStepCountDao,
                              final TimeTaker timeTaker,
                              final Narrator n, final World world,
                              final IInspection inspection) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.inspection = inspection;
    }

    @Override
    public String getType() {
        return "actionUntersuchen";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        return requireNonNull(inspection.getActionName());
    }

    @Override
    protected void narrateAndDo() {
        if (isDefinitivDiskontinuitaet()) {
            n.narrateAlt(secs(5),
                    du(PARAGRAPH, SICH_ZUWENDEN
                            .mit(getDescription(false)))
                            .undWartest(),
                    du(PARAGRAPH, SICH_WENDEN
                            .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher(
                                    ZU.mit(getDescription(false)))))
                            .undWartest(),
                    du(PARAGRAPH, "schaust",
                            "nach",
                            getDescription(false).datK(),
                            ":", SENTENCE)
                            .undWartest());

            n.narrateAlt(inspection.altTimedDescriptions());
        } else {
            n.narrateAlt(drueckeAusTimed(getKohaerenzrelationFuerUmformulierung(),
                    true,
                    inspection.altTimedDescriptions()));
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
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
        return sc.memoryComp().getLastAction().is(Action.Type.UNTERSUCHEN)
                && !sc.memoryComp().getLastAction().hasObject(inspection.getInspectable());
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.UNTERSUCHEN, inspection.getInspectable());
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    @SuppressWarnings("SameParameterValue")
    private EinzelneSubstantivischePhrase getDescription(final boolean shortIfKnown) {
        return getDescription(textContext, getInspectable().getId(), shortIfKnown);
    }

    private IInspectableGO getInspectable() {
        return inspection.getInspectable();
    }
}
