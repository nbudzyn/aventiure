package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.NEHMEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ABSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINLEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.LEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SETZEN;

/**
 * Der Benutzer legt einen Gegenstand ab - z.B. direkt in einen Raum (d.h. auf den Boden o.Ä.)
 * oder auf / in einen Gegenstand, der sich (direkt oder indirekt) in dem Raum befindet.
 */
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
    Collection<AblegenAction<GO>> buildActions(
            final AvDatabase db, final GameObjectService gos,
            final StoryState initialStoryState,
            final GO gameObject,
            final ILocationGO location) {
        if ((gameObject instanceof ILivingBeingGO) && !gameObject.is(FROSCHPRINZ)) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<AblegenAction<GO>> res = ImmutableList.builder();
        res.add(new AblegenAction<>(
                db, gos, initialStoryState, gameObject, location,
                true));

        for (final ILocationGO innerLocation :
                gos.loadDescribableNonLivingLocationRecursiveInventory(
                        location)) {
            // Z.B. "Auf dem Tisch absetzen"
            res.add(new AblegenAction<>(
                    db, gos, initialStoryState, gameObject, innerLocation,
                    false));
        }

        return res.build();
    }

    private AblegenAction(final AvDatabase db,
                          final GameObjectService gos,
                          final StoryState initialStoryState,
                          final @NonNull GO gameObject,
                          final ILocationGO location,
                          final boolean detailLocationNecessaryInDescription) {
        super(db, gos, initialStoryState);
        this.location = location;
        this.gameObject = gameObject;
        this.detailLocationNecessaryInDescription = detailLocationNecessaryInDescription;
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(
                getPraedikat()
                        .mitObj(getDescription(gameObject, true))
                        .getDescriptionInfinitiv(P1, SG, getWohinDetail()));
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
    private AdverbialeAngabe getWohinDetail() {
        return detailLocationNecessaryInDescription ?
                null :
                location.storingPlaceComp().getLocationMode().getWohinAdvAngabe();
    }

    @Override
    public String getType() {
        return "actionAblegen";
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        if (gameObject instanceof ILivingBeingGO) {
            return narrateAndDoLivingBeing();
        }

        return narrateAndDoObject();
    }

    private AvTimeSpan narrateAndDoLivingBeing() {
        checkState(gameObject.is(FROSCHPRINZ),
                "Unexpected creature data: " + gameObject);

        return narrateAndDoFroschprinz();
    }

    private AvTimeSpan narrateAndDoFroschprinz() {
        if (((IHasStateGO) gameObject).stateComp().hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return narrateAndDoFroschprinz_HatHochhebenGefordert();
        }

        final ImmutableList.Builder<AbstractDescription<?>> alt =
                ImmutableList.builder();

        alt.add(du(PARAGRAPH,
                "wühlst", "in deiner Tasche und auf einmal "
                        + "schauert's dich und "
                        + "der nasse Frosch sitzt in deiner Hand. Schnell "
                        + location.storingPlaceComp().getLocationMode().getWohin()
                        + " mit ihm!",
                secs(7))
                .dann()
                .beendet(PARAGRAPH));

        if (getWohinDetail() == null) {
            // Wenn kein wohin-Detail nötig ist, dann ist es wohl kein Tisch o.Ä. und "fällt" passt.
            alt.add(du(PARAGRAPH,
                    "schüttest", "deine Tasche aus, bis der Frosch endlich " +
                            location.storingPlaceComp().getLocationMode().getWohin() +
                            " fällt. Puh.",
                    secs(7))
                    .dann()
                    .beendet(PARAGRAPH));
        }

        alt.add(du(PARAGRAPH,
                "wühlst", "in deiner Tasche. Da quakt es erbost, auf einmal "
                        + "springt der Fosch heraus und direkt "
                        + location.storingPlaceComp().getLocationMode().getWohin(),
                secs(7)
        ));

        AvTimeSpan timeElapsed = n.addAlt(alt);

        timeElapsed = timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
        sc.feelingsComp().setMood(Mood.NEUTRAL);

        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoFroschprinz_HatHochhebenGefordert() {
        if (!location.is(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST)) {
            AvTimeSpan timeElapsed = narrateFroschprinz_HatHochhebenGefordert();
            timeElapsed = timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
            sc.feelingsComp().setMood(Mood.ANGESPANNT);

            return timeElapsed;
        }

        final SubstantivischePhrase gameObjectOrPersPron =
                getAnaphPersPronWennMglSonstDescription(gameObject, true);

        AvTimeSpan timeElapsed = n.add(
                du("setzt", gameObjectOrPersPron.akk() +
                                " " +
                                location.storingPlaceComp().getLocationMode().getWohin(),
                        secs(2))
                        .undWartest()
                        .dann()
                        .phorikKandidat(M, FROSCHPRINZ));

        timeElapsed = timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
        sc.feelingsComp().setMood(Mood.ANGESPANNT);

        return timeElapsed;
    }

    private AvTimeSpan narrateFroschprinz_HatHochhebenGefordert() {
        if (isDefinitivDiskontinuitaet() &&
                initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                initialStoryState.dann()) {

            final ImmutableList.Builder<AbstractDescription<?>> alt =
                    ImmutableList.builder();
            alt.add(satzanschluss(
                    ", aber dann denkst du dir: „So ein Ekeltier hat auf "
                            + "meiner Tafel nichts "
                            + "verloren!“, und setzt den Frosch wieder ab",
                    secs(5))
                    .dann()
                    .phorikKandidat(M, FROSCHPRINZ));
            alt.add(satzanschluss(
                    ", aber dann stellst du dir vor, die schleimigen "
                            + "Patscher auf den Tisch zu stellen, und setzt den "
                            + "Frosch gleich wieder ab",
                    secs(5))
                    .dann()
                    .phorikKandidat(M, FROSCHPRINZ));
            return n.addAlt(alt);
        }

        return n.add(neuerSatz(
                "Der Frosch will auf den Tisch, aber du setzt den Frosch"
                        + (location.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) ?
                        " wieder " :
                        " ")
                        + location.storingPlaceComp().getLocationMode().getWohin()
                        + " und "
                        + "wendest dich demonstrativ ab",
                secs(5))
                .dann()
                .phorikKandidat(M, FROSCHPRINZ));
    }

    private AvTimeSpan narrateAndDoObject() {
        final AvTimeSpan timeElapsed = narrateObject();
        return timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
    }

    private AvTimeSpan narrateUpgradeKnownAndSetLocationAndAction() {
        sc.memoryComp().upgradeKnown(gameObject,
                Known.getKnown(location.storingPlaceComp().getLichtverhaeltnisse()));
        final AvTimeSpan timeSpan = gameObject.locationComp().narrateAndSetLocation(
                location);
        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeSpan;
    }

    private AvTimeSpan narrateObject() {
        @Nullable final AdverbialeAngabe wohinDetail = getWohinDetail();

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            @Nullable final Personalpronomen gameObjektPersPron =
                    initialStoryState.getAnaphPersPronWennMgl(gameObject);

            if (gameObjektPersPron != null) {
                if (isDefinitivDiskontinuitaet()) {
                    return n.add(satzanschluss(
                            "– und legst "
                                    + gameObjektPersPron.akk()
                                    + " sogleich wieder "
                                    + (wohinDetail != null ? "dort" : "")
                                    + "hin", secs(3)));
                }

                if (sc.memoryComp().getLastAction().is(NEHMEN) &&
                        sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    return n.add(du("legst",
                            gameObjektPersPron.akk() +
                                    " " +
                                    location.storingPlaceComp().getLocationMode()
                                            .getWohinAdvAngabe().getText(),
                            secs(3))
                            .dann()
                            .phorikKandidat(gameObjektPersPron, gameObject.getId()));
                }

                if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    return n.add(satzanschluss(", dann legst du "
                                    + gameObjektPersPron.akk()
                                    + (wohinDetail == null ?
                                    " hin" :
                                    " " + wohinDetail), // "auf den Tisch"
                            secs(5))
                            .undWartest());
                }

                return n.add(du("legst",
                        gameObjektPersPron.akk() +
                                ((sc.memoryComp().getLastAction().is(Action.Type.BEWEGEN) &&
                                        wohinDetail == null) ?
                                        " dort" :
                                        "") +
                                (wohinDetail == null ? " hin" : " " + wohinDetail),
                        secs(3)));
            }
        }

        if (isDefinitivDiskontinuitaet()) {
            return n.add(
                    du(PARAGRAPH, "legst",
                            getDescription(gameObject, false).akk() +
                                    " wieder "
                                    + (wohinDetail != null ? "dort" : "")
                                    + "hin",
                            secs(5))
                            .undWartest()
                            .dann());
        }

        return n.add(
                du(PARAGRAPH, "legst",
                        getDescription(gameObject, false).akk()
                                + (wohinDetail == null ? " hin" : " " + wohinDetail),
                        secs(3))
                        .undWartest()
                        .dann());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return sc.memoryComp().getLastAction().is(NEHMEN) &&
                sc.memoryComp().getLastAction().hasObject(gameObject) &&
                gameObject.locationComp().lastLocationWas(location);
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.ABLEGEN, gameObject, location);
    }
}
