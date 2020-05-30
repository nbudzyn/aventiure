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
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ABSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINLEGEN;

/**
 * Der Benutzer legt einen Gegenstand ab.
 */
public class AblegenAction
        <GO extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    private final IHasStoringPlaceGO room;
    @NonNull
    private final GO gameObject;

    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<AblegenAction<GO>> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final GO gameObject,
            final IHasStoringPlaceGO room) {
        if (gameObject instanceof ILivingBeingGO &&
                !(gameObject.is(FROSCHPRINZ))) {
            return ImmutableList.of();
        }

        return ImmutableList
                .of(new AblegenAction<>(db, initialStoryState, gameObject, room));
    }

    private AblegenAction(final AvDatabase db,
                          final StoryState initialStoryState,
                          final @NonNull GO gameObject,
                          final IHasStoringPlaceGO room) {
        super(db, initialStoryState);
        this.room = room;
        this.gameObject = gameObject;
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                gameObject instanceof ILivingBeingGO ? ABSETZEN : HINLEGEN;

        return capitalize(
                praedikat.mitObj(getDescription(gameObject, true))
                        .getDescriptionInfinitiv(P1, SG));
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

        AvTimeSpan timeElapsed = n.addAlt(
                du(PARAGRAPH,
                        "wühlst", "in deiner Tasche und auf einmal "
                                + "schauert's dich und "
                                + "der nasse Frosch sitzt in deiner Hand. Schnell "
                                + room.storingPlaceComp().getLocationMode().getWohin()
                                + " mit ihm!",
                        secs(7))
                        .dann()
                        .beendet(PARAGRAPH),
                du(PARAGRAPH,
                        "schüttest", "deine Tasche aus, bis der Frosch endlich " +
                                room.storingPlaceComp().getLocationMode().getWohin() +
                                " fällt. Puh.",
                        secs(7))
                        .dann()
                        .beendet(PARAGRAPH),
                du(PARAGRAPH,
                        "wühlst", "in deiner Tasche. Da quakt es erbost, auf einmal "
                                + "springt der Fosch heraus und direkt "
                                + room.storingPlaceComp().getLocationMode().getWohin(),
                        secs(7)
                ));
        timeElapsed = timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
        sc.feelingsComp().setMood(Mood.NEUTRAL);

        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoObject() {
        final AvTimeSpan timeElapsed;
        timeElapsed = narrateObject();
        // TODO onLeave() und onEnter() feuern!

        return timeElapsed.plus(narrateUpgradeKnownAndSetLocationAndAction());
    }

    private AvTimeSpan narrateUpgradeKnownAndSetLocationAndAction() {
        sc.memoryComp().upgradeKnown(gameObject,
                Known.getKnown(room.storingPlaceComp().getLichtverhaeltnisseInside()));
        final AvTimeSpan timeSpan = gameObject.locationComp().narrateAndSetLocation(room);
        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeSpan;
    }

    private AvTimeSpan narrateObject() {
        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            @Nullable final Personalpronomen gameObjektPersPron =
                    initialStoryState.getAnaphPersPronWennMgl(gameObject);

            if (gameObjektPersPron != null) {
                if (sc.memoryComp().lastActionWas(Action.Type.NEHMEN, gameObject)) {
                    return n.add(satzanschluss(
                            "– und legst "
                                    + gameObjektPersPron.akk()
                                    + " sogleich wieder hin", secs(3)));
                }

                if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    return n.add(satzanschluss(", dann legst du "
                            + gameObjektPersPron.akk()
                            + " hin", secs(5))
                            .undWartest());
                }

                String text = "und legst " + gameObjektPersPron.akk();
                if (sc.memoryComp().getLastAction().is(Action.Type.BEWEGEN)) {
                    text += " dort";
                }

                text += " hin";

                n.add(satzanschluss(text, secs(3)));
            }
        }

        if (sc.memoryComp().lastActionWas(Action.Type.NEHMEN, gameObject)) {
            return n.add(
                    du(PARAGRAPH, "legst",
                            getDescription(gameObject, false).akk() + " wieder hin",
                            secs(5))
                            .undWartest()
                            .dann());
        }

        return n.add(
                du(PARAGRAPH, "legst",
                        getDescription(gameObject, false).akk() + " hin", secs(3))
                        .undWartest()
                        .dann());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.ABLEGEN, gameObject);
    }
}
