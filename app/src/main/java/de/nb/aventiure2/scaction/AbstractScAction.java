package de.nb.aventiure2.scaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.getPOVDescription;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * An action the player could choose to advance the story.
 */
public abstract class AbstractScAction implements IPlayerAction {
    // TODO Vielleicht sollten die Aktionen (möglichst?) nicht die Creature-Reaktionen
    //  entscheiden, sondern dazu der Creature-KI die Gelegenheit geben, eine
    //  Entscheidung zu treffen. Denkbar wäre:
    //  - Die Aktion fragt die Creature-KI nach einer Entscheidung - danach springt die
    //    Aktion in den entsprechenden Zweig und schreibt einen entsprechenden Text.
    //  - Die Aktion gibt der Creature-KI die Möglichkeit, einzuhaken und einen
    //    entsprechenden Text zu erzeugen. Entweder schreibt die Creature-KI
    //    selbst den Text, der Narrator baut den Text von Aktion und Creature-KI
    //    zusammen oder die Aktion nimmt den Text der Creature-KI und baut ihn
    //    selbst in ihren eigenen Text ein.

    protected final AvDatabase db;
    protected final StoryStateDao n;

    protected final SpielerCharakter sc;

    /**
     * The {@link StoryState} at the beginning of the action.
     */
    protected final StoryState initialStoryState;

    protected AbstractScAction(@NonNull final AvDatabase db, final StoryState initialStoryState) {
        this.db = db;

        n = db.storyStateDao();

        sc = loadSC(db);

        this.initialStoryState = initialStoryState;
    }

    /**
     * Returns the name of the action as it is displayed to the player.
     */
    abstract public String getName();

    /**
     * Führt die Aktion aus (inkl. Erzählung), lässt die entsprechende Zeit verstreichen.
     * Aktualisiert dabei auch die Welt.
     */
    public final void doAndPassTime() {
        // TODO Möglichst dieses ewige Rumreichen der
        //  verflossenen Zeit ausbauen.
        //  Kann nicht die Zeit jeweils beim narraten upgedatet werden?
        //  Und man vergleich hier nur vorher-Zeit mit nachher-Zeit?

        final AvDateTime start = db.nowDao().now();

        final AvTimeSpan timeElapsed = narrateAndDo();

        final AvDateTime dateTimeAfterActionBeforeUpdateWorld = start.plus(timeElapsed);
        db.nowDao().setNow(dateTimeAfterActionBeforeUpdateWorld);

        final AvTimeSpan extraTimeElapsedDuringWorldUpdate =
                updateWorld(start, start.plus(timeElapsed));

        db.nowDao().setNow(dateTimeAfterActionBeforeUpdateWorld
                .plus(extraTimeElapsedDuringWorldUpdate));

        GameObjects.saveAll(db);
    }

    private static AvTimeSpan updateWorld(final AvDateTime lastTime,
                                          @NonNull final AvDateTime now) {
        if (now.equals(lastTime)) {
            return noTime();
        }

        AvTimeSpan additionalTimeElapsed =
                GameObjects.narrateAndDoReactions().onTimePassed(lastTime, now);

        // Falls jetzt noch etwas passiert ist,
        // was Zeit gebraucht hat, dann erneut
        // allen die Zeit geben, in dieser Zeit etwas
        // getan zu haben.
        additionalTimeElapsed =
                additionalTimeElapsed.plus(updateWorld(now, now.plus(additionalTimeElapsed)));

        return additionalTimeElapsed;
    }

    abstract public AvTimeSpan narrateAndDo();

    /**
     * Gibt zurück, ob der Benutzer dasselbe definitiv schon einmal getan und zwischendrin nichts
     * anderes getan hat. (Es könnte zwischendrin durchaus Reaktionen von anderen Wesen
     * gegeben haben.)
     * <p>
     * Ein Rückgabewert von {@code false} kann also bedeuten, dass der Benutzer definitiv
     * nicht dasselbe schon einmal getan hat - oder das sich das System unsicher ist.
     */
    abstract protected boolean isDefinitivWiederholung();

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Es muss sich um eine {@link IDescribableGO} handeln!
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die Lampe" zurück.
     */
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription(
            final GameObjectId describableId) {
        return getAnaphPersPronWennMglSonstDescription(
                (IDescribableGO) GameObjects.load(db, describableId), true);
    }


    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" oder "die Lampe" zurück.
     */
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription(
            final IDescribableGO describableGO) {
        return getAnaphPersPronWennMglSonstDescription(
                describableGO, true);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" zurück.
     */
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstDescription(
            final IDescribableGO describableGO,
            final boolean descShortIfKnown) {
        @Nullable final Personalpronomen anaphPersPron =
                db.storyStateDao().getStoryState().getAnaphPersPronWennMgl(describableGO);
        if (anaphPersPron != null) {
            return anaphPersPron;
        }

        return getDescription(describableGO, descShortIfKnown);
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * der Spieler das Game Object schon kennt oder nicht.
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject) {
        return getDescription(gameObject, false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject,
                                           final boolean shortIfKnown) {
        return getPOVDescription(sc, gameObject, shortIfKnown);
    }
}