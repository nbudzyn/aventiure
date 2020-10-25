package de.nb.aventiure2.scaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.IPlayerAction;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.SC_ACTION;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

/**
 * An action the player could choose.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractScAction implements IPlayerAction {
    // STORY Auch die NPCs könnten actions wie die AbstractScAction
    //  durchführen

    private static final AvTimeSpan MAX_WORLD_TICK = days(1);

    protected final AvDatabase db;
    protected final World world;
    protected final Narrator n;

    protected final SpielerCharakter sc;

    // TODO AbstractScAction so verallgemeinern (oder Inhalte extrahieren?),
    //  so dass dieselben Actions auch von einer NPC-AI zurückgegeben werden könnten?
    //  Das Framework nimmt dann die (User- oder NPC-) Action entgegen und führt sie aus?
    //  Ziel wäre: Doppelten Code für USER- und NPC-Actions verhindern und
    //  für den Action-Code einen guten Platz finden.
    //  Idee dazu: Operationen ("Verbs") in eigene Klassen auslagern

    protected AbstractScAction(final AvDatabase db, final Narrator n, final World world) {
        this.db = db;
        this.world = world;

        this.n = n;

        sc = world.loadSC();
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
        final AvDateTime start = db.nowDao().now();

        n.setNarrationSourceJustInCase(SC_ACTION);
        // TODO Möglichst dieses ewige Rumreichen der
        //  verflossenen Zeit ausbauen.
        //  Kann nicht die Zeit jeweils beim narraten upgedatet werden?
        //  Und man vergleich hier nur vorher-Zeit mit nachher-Zeit?

        narrateAndDo();

        db.scActionStepCountDao().inc();
        fireScActionDone(start);

        n.setNarrationSourceJustInCase(REACTIONS);

        // Jetzt die Zeit zurücksetzen und in der Welt das nachholen,
        // was passiert ist, während der Benutzer gehandelt hat!
        final AvDateTime until = db.nowDao().now();
        db.nowDao().setNow(start);
        updateWorld(until);

        final AvDateTime dateTimeBetweenMainWorldUpdateAndHints = db.nowDao().now();

        fireAfterScActionAndFirstWorldUpdate();

        if (db.nowDao().now().isAfter(dateTimeBetweenMainWorldUpdateAndHints)) {
            updateWorld(db.nowDao().now());
        }

        world.saveAll(true);
        n.saveAll();
    }

    private void fireScActionDone(final AvDateTime startTimeOfScAction) {
        world.scActionDone(startTimeOfScAction);
    }

    private void updateWorld(final AvDateTime until) {
        checkNotNull(until, "now is null");

        if (db.nowDao().now().isEqualOrAfter(until)) {
            return;
        }

        while (until.minus(db.nowDao().now()).longerThan(MAX_WORLD_TICK)) {
            final AvDateTime endOfTick = db.nowDao().now().plus(MAX_WORLD_TICK);
            world.narrateAndDoReactions().onTimePassed(db.nowDao().now(), endOfTick);

            db.nowDao().setNow(endOfTick);
        }

        world.narrateAndDoReactions().onTimePassed(db.nowDao().now(), until);

        if (db.nowDao().now().isAfter(until)) {
            // Sonderfall! Einzelne Game Objects haben Aktionen begonnen, die
            // Zusatzzeit gebraucht haben.
            // Dann lassen wir diese "Zusatzzeit" auch offiziell hier vergehen, so dass
            // jedes Game Object darauf reagieren kann.
            // (Rekursiv möglich.)
            updateWorld(db.nowDao().now());
        } else {
            db.nowDao().setNow(until);
        }
    }

    private void fireAfterScActionAndFirstWorldUpdate() {
        world.narrateAndDoReactions().afterScActionAndFirstWorldUpdate();
    }

    abstract public void narrateAndDo();

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
        // TODO Anapher im weitesten Sinne verwenden. Auch Wiederholung und Katapher ist
        //  Anapher. Perspron ist nur eine Form der Anapher.

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
                n.requireNarration().getAnaphPersPronWennMgl(describableGO);
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