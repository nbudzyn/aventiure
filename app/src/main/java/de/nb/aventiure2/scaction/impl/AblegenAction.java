package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.getPOVDescription;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableNonLivingHasStoringPlaceInventory;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
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
    private final IHasStoringPlaceGO location;

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
     * {@link IHasStoringPlaceGO}s.
     * <p>
     * Beispiel: Erzeugt die Aktionen, mit denen der Benutzer die goldene Kugel in einem
     * Raum ablegen kann oder auf dem Tisch, der sich in dem Raum befindet.
     */
    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<AblegenAction<GO>> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final GO gameObject,
            final IHasStoringPlaceGO location) {
        return buildActions(db, initialStoryState, gameObject, location, true);
    }

    private static <GO extends IDescribableGO & ILocatableGO>
    Collection<AblegenAction<GO>> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final GO gameObject,
            final IHasStoringPlaceGO location,
            final boolean isOutermost) {
        final ImmutableList.Builder<AblegenAction<GO>> res = ImmutableList.builder();

        if (!(gameObject instanceof ILivingBeingGO) || gameObject.is(FROSCHPRINZ)) {
            res.add(new AblegenAction<>(
                    db, initialStoryState, gameObject, location, isOutermost));
        }

        res.addAll(buildRecursiveActions(db, initialStoryState, gameObject, location));

        return res.build();
    }

    /**
     * Erzeugt alle Aktionen, mit denen der Benutzer dieses <code>gameObject</code>
     * auf / in allen {@link IHasStoringPlaceGO}s ablegen kann, die die
     * <code>outerLocation</code> enthält
     * <p>
     * Beispiel: Erzeugt die Aktion, auf dem Tisch ablegen kann, der sich in einem bestimmten
     * Raum (<code>outerLocation</code>) befindet.
     */
    private static <GO extends IDescribableGO & ILocatableGO>
    Collection<AblegenAction<GO>> buildRecursiveActions(
            final AvDatabase db, final StoryState initialStoryState, final GO gameObject,
            final IHasStoringPlaceGO outerLocation) {
        final ImmutableList.Builder<AblegenAction<GO>> res = ImmutableList.builder();

        for (final IHasStoringPlaceGO innerLocation :
                loadDescribableNonLivingHasStoringPlaceInventory(
                        db, outerLocation)) {
            // Z.B. "Auf dem Tisch absetzen"
            res.addAll(
                    buildActions(
                            db, initialStoryState, gameObject, innerLocation,
                            false));
        }

        return res.build();
    }

    private AblegenAction(final AvDatabase db,
                          final StoryState initialStoryState,
                          final @NonNull GO gameObject,
                          final IHasStoringPlaceGO location,
                          final boolean detailLocationNecessaryInDescription) {
        super(db, initialStoryState);
        this.location = location;
        this.gameObject = gameObject;
        this.detailLocationNecessaryInDescription = detailLocationNecessaryInDescription;
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(
                getPraedikat()
                        .mitObj(
                                getPOVDescription(
                                        db, SPIELER_CHARAKTER, gameObject, true))
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
            // Wenn kein wohin-Detail nötig ist, dann ist es wohl Tisch o.Ä. und "fällt" passt.
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

    private AvTimeSpan narrateAndDoObject() {
        final AvTimeSpan timeElapsed = narrateObject();
        return timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
    }

    private AvTimeSpan narrateUpgradeKnownAndSetLocationAndAction() {
        sc.memoryComp().upgradeKnown(gameObject,
                Known.getKnown(location.storingPlaceComp().getLichtverhaeltnisseInside()));
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
                if (sc.memoryComp().lastActionWas(Action.Type.NEHMEN, gameObject, location)) {
                    return n.add(satzanschluss(
                            "– und legst "
                                    + gameObjektPersPron.akk()
                                    + " sogleich wieder "
                                    + (wohinDetail != null ? "dort" : "")
                                    + "hin", secs(3)));
                }

                // STORY nimmst... und legst sie auf den Tisch
                // STORY nimmst... vom Tisch und legst sie auf den Boden

                if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    return n.add(satzanschluss(", dann legst du "
                                    + gameObjektPersPron.akk()
                                    + (wohinDetail == null ? " hin" : " " + wohinDetail), // "auf den Tisch"
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

        if (sc.memoryComp().lastActionWas(Action.Type.NEHMEN, gameObject, location)) {
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

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.ABLEGEN, gameObject, location);
    }
}
