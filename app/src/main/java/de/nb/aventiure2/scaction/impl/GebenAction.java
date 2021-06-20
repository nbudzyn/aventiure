package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.taking.ITakerGO;
import de.nb.aventiure2.data.world.syscomp.taking.SCTakeAction;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAus;
import static de.nb.aventiure2.german.praedikat.IntentionalesVerb.VERSUCHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ANBIETEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.HINHALTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.REICHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ZEIGEN;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Der Benutzer gibt einem Living Being einen Gegenstand, den er bei sich trägt.
 */
public class GebenAction<
        TAKER extends IDescribableGO & ILocatableGO & ITakerGO<?>,
        GIVEN extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    @NonNull
    private final TAKER taker;
    private final GIVEN given;

    public static <
            TAKER extends IDescribableGO & ILocatableGO & ITakerGO<?>,
            GIVEN extends IDescribableGO & ILocatableGO>
    Collection<GebenAction<TAKER, GIVEN>> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            final TAKER taker,
            final Collection<GIVEN> givenCandidates) {
        if (world.isOrHasRecursiveLocation(taker, SPIELER_CHARAKTER)) {
            return ImmutableList.of();
        }

        if (!taker.locationComp().hasRecursiveLocation(
                world.loadSC().locationComp().getLocation())) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<GebenAction<TAKER, GIVEN>> res =
                ImmutableList.builder();

        for (final GIVEN givenCandidate : givenCandidates) {
            if (givenCandidate.locationComp().isMovable() &&
                    !givenCandidate.is(taker)) {
                res.add(
                        new GebenAction<>(scActionStepCountDao, timeTaker, n, world, taker,
                                givenCandidate));
            }
        }

        return res.build();
    }

    private GebenAction(final SCActionStepCountDao scActionStepCountDao,
                        final TimeTaker timeTaker, final Narrator n, final World world,
                        final TAKER taker,
                        final GIVEN given) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.taker = taker;
        this.given = given;
    }

    @Override
    public String getName() {
        return capitalize(
                GEBEN
                        .mitDat(world.getDescription(taker, true))
                        .mit(world.getDescription(given))
                        .getInfinitiv(P2, SG).joinToString(
                ));
    }

    @Override
    public String getType() {
        return "actionGeben";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    public void narrateAndDo() {
        final SCTakeAction<GIVEN> action = taker.takingComp().getAction(given);
        narrateAnbietenBzwGeben(action);
        action.narrateTakerAndDo();

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAnbietenBzwGeben(final SCTakeAction<GIVEN> action) {
        final ImmutableList<PraedikatOhneLeerstellen> praedikatAlt =
                action.wirdZunaechstAngenommen() ?
                        altGebenPraedikate() : altAnbietenPraedikate();

        final ImmutableList<StructuredDescription> descAlt =
                mapToList(praedikatAlt, GebenAction::toAnbietenGebenDuDescription);

        n.narrateAlt(drueckeAus(getKohaerenzrelationFuerUmformulierung(), descAlt),
                secs(20));
    }

    private ImmutableList<PraedikatOhneLeerstellen> altAnbietenPraedikate() {
        final ImmutableList.Builder<PraedikatOhneLeerstellen> alt =
                ImmutableList.builder();
        final SubstantivischePhrase takerAnaph = world.anaph(taker);
        final EinzelneSubstantivischePhrase givenDesc = world.getDescription(given, false);

        alt.add(
                // "Du hältst IHR DIE GOLDENE KUGEL hin"
                HINHALTEN
                        .mitDat(takerAnaph)
                        .mit(givenDesc),
                // "Du bietest IHR DIE GOLDENE KUGEL an"
                ANBIETEN
                        .mitDat(takerAnaph)
                        .mit(givenDesc),
                //"Du zeigst IHR DIE GOLDENE KUGEL"
                ZEIGEN
                        .mitDat(takerAnaph)
                        .mit(givenDesc),
                // "Du versuchst IHR DIE GOLDENE KUGEL zu reichen"
                VERSUCHEN.mitLexikalischemKern(
                        REICHEN
                                .mitDat(takerAnaph)
                                .mit(givenDesc))
        );

        return alt.build();
    }

    private ImmutableList<PraedikatOhneLeerstellen> altGebenPraedikate() {
        final ImmutableList.Builder<PraedikatOhneLeerstellen> alt =
                ImmutableList.builder();
        final SubstantivischePhrase takerAnaph = world.anaph(taker);
        final EinzelneSubstantivischePhrase givenDesc = world.getDescription(given, false);

        alt.add(
                // "Du gibst IHR DIE GOLDENE KUGEL"
                GEBEN
                        .mitDat(takerAnaph)
                        .mit(givenDesc),
                // "Du reichst IHR DIE GOLDENE KUGEL"
                REICHEN
                        .mitDat(takerAnaph)
                        .mit(givenDesc)
        );

        return alt.build();
    }

    private static StructuredDescription toAnbietenGebenDuDescription(
            final PraedikatOhneLeerstellen anbietenGebenPraedikat) {
        return du(anbietenGebenPraedikat)
                .undWartest()
                .dann();
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.GEBEN, given, taker);
    }
}
