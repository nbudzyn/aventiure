package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.taking.ITakerGO;
import de.nb.aventiure2.data.world.syscomp.taking.SCTakeAction;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.PraedikatDuDescription;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAus;
import static de.nb.aventiure2.german.praedikat.IntentionalesVerb.VERSUCHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ANBIETEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.HINHALTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.REICHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ZEIGEN;

/**
 * Der Benutzer gibt einem Living Being einen Gegenstand, den er bei sich trägt.
 */
public class GebenAction<
        TAKER extends IDescribableGO & ILocatableGO & ITakerGO<?>,
        GIVEN extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {

    // FIXME Man könnte auch der Zauberin Dinge zu geben versuchen.
    //  Sie identifiziert die Goldene Kugel sofort als Diebesgut - "lasst euch nicht erwischen" oder
    //  "so einer seit ihr also"

    @NonNull
    private final TAKER taker;
    private final GIVEN given;

    public static <
            TAKER extends IDescribableGO & ILocatableGO & ITakerGO<?>,
            GIVEN extends IDescribableGO & ILocatableGO>
    Collection<GebenAction<TAKER, GIVEN>> buildActions(
            final AvDatabase db, final Narrator n, final World world,
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
                        new GebenAction<>(db, n, world, taker, givenCandidate));
            }
        }

        return res.build();
    }

    private GebenAction(final AvDatabase db, final Narrator n, final World world, final TAKER taker,
                        final GIVEN given) {
        super(db, n, world);
        this.taker = taker;
        this.given = given;
    }

    @Override
    public String getName() {
        return capitalize(
                GermanUtil.joinToNullString(
                        GEBEN
                                .mitDat(world.getDescription(taker, true))
                                .mit(world.getDescription(given))
                                .getInfinitiv(P1, SG)));
    }

    @Override
    public String getType() {
        return "actionGeben";
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
                        getGebenPraedikatAlt() : getAnbietenPraedikatAlt();

        final ImmutableList<PraedikatDuDescription> descAlt =
                praedikatAlt.stream().map(GebenAction::toAnbietenGebenDuDescription)
                        .collect(ImmutableList.toImmutableList());

        n.narrateAlt(drueckeAus(getKohaerenzrelationFuerUmformulierung(), descAlt),
                secs(20));
    }

    private ImmutableList<PraedikatOhneLeerstellen> getAnbietenPraedikatAlt() {
        final ImmutableList.Builder<PraedikatOhneLeerstellen> alt = ImmutableList.builder();
        final SubstantivischePhrase takerAnaph =
                getAnaphPersPronWennMglSonstShortDescription(taker);
        final Nominalphrase givenDesc = world.getDescription(given, false);

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

    private ImmutableList<PraedikatOhneLeerstellen> getGebenPraedikatAlt() {
        final ImmutableList.Builder<PraedikatOhneLeerstellen> alt = ImmutableList.builder();
        final SubstantivischePhrase takerAnaph =
                getAnaphPersPronWennMglSonstShortDescription(taker);
        final Nominalphrase givenDesc = world.getDescription(given, false);

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

    private static PraedikatDuDescription toAnbietenGebenDuDescription(
            final PraedikatOhneLeerstellen anbietenGebenPraedikat) {
        return du(anbietenGebenPraedikat)
                .undWartest()
                .dann()
                // FIXME Hier wäre es etwas schöner wenn die im anbietenGebenPraedikat
                //  verwendete Description für den Taker als phorikKandidat gesetzt würde.
                ;
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
