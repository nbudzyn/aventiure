package de.nb.aventiure2.scaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

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
    protected final World world;
    protected final StoryStateDao n;

    protected final SpielerCharakter sc;

    /**
     * The {@link StoryState} at the beginning of the action.
     */
    protected final StoryState initialStoryState;

    protected AbstractScAction(@NonNull final AvDatabase db, final World world,
                               final StoryState initialStoryState) {
        this.db = db;
        this.world = world;

        n = db.storyStateDao();

        sc = world.loadSC();

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

        // STORY Wenn der Benutzer länger nicht weiterkommt, erzeugt eine Tippgenerator
        //  (neues Game Object?) Sätze wie "Wann soll eigentlich das Schlossfest sein?" o.Ä.

        final AvDateTime start = db.nowDao().now();

        final AvTimeSpan timeElapsed = narrateAndDo();

        final AvDateTime dateTimeAfterActionBeforeUpdateWorld = start.plus(timeElapsed);
        db.nowDao().setNow(dateTimeAfterActionBeforeUpdateWorld);

        final AvTimeSpan extraTimeElapsedDuringWorldUpdate =
                updateWorld(start, start.plus(timeElapsed));

        db.nowDao().setNow(dateTimeAfterActionBeforeUpdateWorld
                .plus(extraTimeElapsedDuringWorldUpdate));

        world.saveAll();
    }

    private AvTimeSpan updateWorld(final AvDateTime lastTime,
                                   @NonNull final AvDateTime now) {
        if (now.equals(lastTime)) {
            return noTime();
        }

        AvTimeSpan additionalTimeElapsed =
                world.narrateAndDoReactions().onTimePassed(lastTime, now);

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
     * nicht dasselbe schon einmal getan hat - oder dass sich das System unsicher ist.
     */
    abstract protected boolean isDefinitivWiederholung();

    /**
     * Gibt zurück, ob diese Handlung definitiv eine <i>Diskontinuität</i> in der Erzählung
     * bedeutet. Das könnte z.B. der Fall, sein, wenn der Benutzer einen Gegenstand nimmt
     * und ihn dann gleich wieder an derselben Stelle absetzt.
     * <p>
     * Ein Rückgabewert von {@code false} kann also bedeuten, dass es sich definitiv <i>nicht </i>
     * um eine Diskontinuität handelt - oder das sich das System unsicher ist.
     */
    abstract protected boolean isDefinitivDiskontinuitaet();

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
                (IDescribableGO) world.load(describableId), true);
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

        return world.getDescription(describableGO, descShortIfKnown);
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}