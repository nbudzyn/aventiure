package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AbstractAdvAngabe;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.NEHMEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ABSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINLEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.LEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SETZEN;

/**
 * Der Benutzer legt einen Gegenstand ab - z.B. direkt in einen Raum (d.h. auf den Boden o.Ä.)
 * oder auf / in einen Gegenstand, der sich (direkt oder indirekt) in dem Raum befindet.
 */
@SuppressWarnings("unchecked")
public class AblegenAction
        <GO extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    private final ILocationGO location;

    @NonNull
    private final GO gameObject;

    /**
     * Ob der genaue Ort, wo das {@link #gameObject} abgelegt wird, in der Beschreibung
     * erwähnt werden <i>muss</i> (z.B. "legst ... auf den Tisch") oder nicht
     * ("legst ... hin", also auf den Fußboden).
     */
    private final boolean detailLocationNecessaryInDescription;

    /**
     * Erzeugt alle Aktionen, mit denen der Benutzer dieses <code>gameObject</code> in dieser
     * <code>location</code> ablegen kann - sowie auf / in allen rekursiv enthaltenen
     * {@link ILocationGO}s.
     * <p>
     * Beispiel: Erzeugt die Aktionen, mit denen der Benutzer die goldene Kugel in einem
     * Raum ablegen kann oder auf dem Tisch, der sich in dem Raum befindet.
     */
    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<AblegenAction<GO>> buildActions(final SCActionStepCountDao scActionStepCountDao,
                                               final TimeTaker timeTaker,
                                               final Narrator n, final World world,
                                               final GO gameObject,
                                               final ILocationGO location) {
        if ((gameObject instanceof ILivingBeingGO) && !gameObject.is(FROSCHPRINZ)) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<AblegenAction<GO>> res = ImmutableList.builder();
        res.add(new AblegenAction<>(
                scActionStepCountDao, timeTaker, n, world, gameObject, location,
                true));

        for (final ILocationGO innerLocation :
                world.loadDescribableNonLivingLocationVisiblyRecursiveInventory(location)) {
            // Z.B. "Auf dem Tisch absetzen"
            res.add(new AblegenAction<>(scActionStepCountDao, timeTaker,
                    n, world, gameObject, innerLocation,
                    false));
        }

        return res.build();
    }

    private AblegenAction(final SCActionStepCountDao scActionStepCountDao,
                          final TimeTaker timeTaker,
                          final Narrator n,
                          final World world,
                          final @NonNull GO gameObject,
                          final ILocationGO location,
                          final boolean detailLocationNecessaryInDescription) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.location = location;
        this.gameObject = gameObject;
        this.detailLocationNecessaryInDescription = detailLocationNecessaryInDescription;
    }

    @Override
    @NonNull
    public String getName() {
        return joinToKonstituentenfolge(
                SENTENCE,
                getPraedikat()
                        .mit(world.getDescription(gameObject, true))
                        .mitAdvAngabe(getWohinDetail())
                        .getInfinitiv(P2, SG))
                .joinToString();
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @NonNull
    private PraedikatMitEinerObjektleerstelle getPraedikat() {
        if (getWohinDetail() == null) {
            return gameObject instanceof ILivingBeingGO ? ABSETZEN : HINLEGEN;
        } else {
            return gameObject instanceof ILivingBeingGO ? SETZEN : LEGEN;
        }
    }

    @Nullable
    private AdvAngabeSkopusVerbWohinWoher getWohinDetail() {
        return detailLocationNecessaryInDescription ?
                null :
                location.storingPlaceComp().getLocationMode()
                        .getWohinAdvAngabe(false);
    }

    @Override
    public String getType() {
        return "actionAblegen";
    }

    @Override
    public void narrateAndDo() {
        if (gameObject instanceof ILivingBeingGO) {
            narrateAndDoLivingBeing();
            return;
        }

        narrateAndDoObject();
    }

    private void narrateAndDoLivingBeing() {
        checkState(gameObject.is(FROSCHPRINZ),
                "Unexpected creature data: " + gameObject);

        narrateAndDoFroschprinz();
    }

    private void narrateAndDoFroschprinz() {
        if (((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            narrateAndDoFroschprinz_HatHochhebenGefordert();
            return;
        }

        final AltTimedDescriptionsBuilder alt = altTimed();

        alt.add(du(PARAGRAPH, "wühlst",
                "in deiner Tasche und auf einmal",
                "schauert's dich und",
                "der nasse Frosch sitzt in deiner Hand. Schnell",
                location.storingPlaceComp().getLocationMode().getWohin(false),
                "mit ihm!", PARAGRAPH)
                .timed(secs(7))
                .dann());

        if (getWohinDetail() == null) {
            // Wenn kein wohin-Detail nötig ist, dann ist es wohl kein Tisch o.Ä. und "fällt" passt.
            alt.add(du(PARAGRAPH, "schüttest",
                    "deine Tasche aus, bis der Frosch endlich",
                    location.storingPlaceComp().getLocationMode()
                            .getWohin(false),
                    "fällt. Puh.", PARAGRAPH)
                    .timed(secs(7))
                    .dann());
        }

        alt.add(du(PARAGRAPH, "wühlst",
                "in deiner Tasche. Da quakt es erbost, auf einmal",
                "springt der Frosch heraus und direkt",
                location.storingPlaceComp().getLocationMode().getWohin(false))
                .timed(secs(7)));

        n.narrateAlt(alt);

        narrateUpgradeKnownAndSetLocationAndAction();
        sc.feelingsComp().requestMood(NEUTRAL);
    }

    private void narrateAndDoFroschprinz_HatHochhebenGefordert() {
        if (!location.is(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST)) {
            narrateFroschprinz_HatHochhebenGefordert();
            narrateUpgradeKnownAndSetLocationAndAction();
            sc.feelingsComp().requestMood(ANGESPANNT);

            return;
        }

        final SubstantivischePhrase anaph = world.anaph(gameObject, true);

        n.narrate(
                du("setzt",
                        anaph.akkK(),
                        location.storingPlaceComp().getLocationMode()
                                .getWohin(false))
                        .timed(secs(2))
                        .undWartest()
                        .dann());

        narrateUpgradeKnownAndSetLocationAndAction();
        sc.feelingsComp().requestMood(ANGESPANNT);
    }

    private void narrateFroschprinz_HatHochhebenGefordert() {
        if (isDefinitivDiskontinuitaet() &&
                n.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                n.dann()) {

            final AltTimedDescriptionsBuilder alt = altTimed();
            alt.add(satzanschluss(", aber dann denkst du dir: „So ein Ekeltier hat auf",
                    "meiner Tafel nichts",
                    "verloren!“, und setzt den Frosch wieder ab")
                    .timed(secs(5))
                    .dann()
                    .phorikKandidat(M, FROSCHPRINZ));
            alt.add(satzanschluss(", aber dann stellst du dir vor, die schleimigen",
                    "Patscher auf den Tisch zu stellen, und setzt den",
                    "Frosch gleich wieder ab")
                    .timed(secs(5))
                    .dann()
                    .phorikKandidat(M, FROSCHPRINZ));
            n.narrateAlt(alt);
            return;
        }

        n.narrate(neuerSatz("Der Frosch will auf den Tisch, aber du setzt den Frosch",
                (location.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) ? "wieder" : null),
                location.storingPlaceComp().getLocationMode().getWohin(false),
                "und wendest dich demonstrativ ab")
                .timed(secs(5))
                .dann()
                .phorikKandidat(M, FROSCHPRINZ));
    }

    private void narrateAndDoObject() {
        narrateObject();
        narrateUpgradeKnownAndSetLocationAndAction();
    }

    private void narrateUpgradeKnownAndSetLocationAndAction() {
        world.loadSC().memoryComp().narrateAndUpgradeKnown(gameObject);
        gameObject.locationComp().narrateAndSetLocation(location);
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateObject() {
        @Nullable final AbstractAdvAngabe wohinDetail = getWohinDetail();

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            @Nullable final Personalpronomen gameObjektPersPron =
                    n.getAnaphPersPronWennMgl(gameObject);

            if (gameObjektPersPron != null) {
                if (isDefinitivDiskontinuitaet()) {
                    n.narrate(satzanschluss("– und legst",
                            gameObjektPersPron.akkK(),
                            "sogleich wieder",
                            (wohinDetail != null ? "dort" : null),
                            "hin")
                            .timed(secs(3)));
                    return;
                }

                if (sc.memoryComp().getLastAction().is(NEHMEN) &&
                        sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    n.narrate(du(StructuralElement.WORD,
                            LEGEN.mit(gameObjektPersPron)
                                    .mitAdvAngabe(
                                            location.storingPlaceComp().getLocationMode()
                                                    .getWohinAdvAngabe(false)))
                            .timed(secs(3))
                            .dann());
                    return;
                }

                if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    // "auf den Tisch"
                    n.narrate(du("legst",
                            gameObjektPersPron.akkK(),
                            (wohinDetail == null ? k("hin") : wohinDetail.getDescription(P2, SG)))
                            .timed(secs(5))
                            .undWartest());
                    return;
                }

                if (sc.memoryComp().getLastAction().is(Action.Type.BEWEGEN) &&
                        wohinDetail == null) {
                    n.narrate(du("legst",
                            gameObjektPersPron.akkK(), " zurück")
                            .timed(secs(3)));
                    return;
                }

                n.narrate(du("legst",
                        gameObjektPersPron.akkK(),
                        (wohinDetail == null ? k("hin") : wohinDetail.getDescription(P2, SG)))
                        .timed(secs(3)));
                return;
            }
        }

        if (isDefinitivDiskontinuitaet()) {
            n.narrate(
                    du(PARAGRAPH, "legst",
                            world.getDescription(gameObject, false)
                                    .akkK(),
                            (wohinDetail != null ? " zurück" : " wieder hin"))
                            .timed(secs(5))
                            .undWartest()
                            .dann());
            return;
        }

        n.narrate(
                du(PARAGRAPH, "legst",
                        world.getDescription(gameObject, false).akkK(),
                        (wohinDetail == null ? k("hin") : wohinDetail.getDescription(P2, SG)))
                        .timed(secs(3))
                        .undWartest()
                        .dann());
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
        return
                // Es ist oft keine Diskontinuität, wenn
                // zwischen zwei Aktionen eine Reaction liegt.
                !n.lastNarrationWasFromReaction() &&
                        sc.memoryComp().getLastAction().is(NEHMEN) &&
                        sc.memoryComp().getLastAction().hasObject(gameObject) &&
                        gameObject.locationComp().lastLocationWas(location);
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.ABLEGEN, gameObject, location);
    }
}
