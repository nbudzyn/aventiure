package de.nb.aventiure2.scaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.IPlayerAction;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.SC_ACTION;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.days;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * An action the player could choose.
 */
public abstract class AbstractScAction implements IPlayerAction {
    private static final AvTimeSpan MAX_WORLD_TICK = days(1);

    protected final AvDatabase db;
    protected final World world;
    protected final NarrationDao n;

    protected final SpielerCharakter sc;

    protected AbstractScAction(@NonNull final AvDatabase db, final World world) {
        this.db = db;
        this.world = world;

        n = db.narrationDao();

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
        // TODO Möglichst dieses ewige Rumreichen der
        //  verflossenen Zeit ausbauen.
        //  Kann nicht die Zeit jeweils beim narraten upgedatet werden?
        //  Und man vergleich hier nur vorher-Zeit mit nachher-Zeit?

        // STORY Wenn der Benutzer länger nicht weiterkommt (länger kein
        //  neuer Geschichtsschritt erreicht), erzeugt ein Tippgenerator
        //  (neues Game Object?) Sätze wie "Wann soll eigentlich das Schlossfest sein?",
        //  "Vielleicht hättest du doch die Kugel mitnehmen sollen?" o.Ä.
        //  Als Tipp für den Froschprinzen z.B. durch einen NSC ankündigen lassen: Im Königreich
        //  nebenan ist der Prinz
        //  verschwunden.
        //  Tipp für Rapunzel: Mutter sammelt im Wald Holz und klagt ihr Leid.
        //  Tipps könnten von den Geschichtsmeilensteinen generiert werden, die
        //  noch nicht erreicht, deren Voraussetzungen jedoch bereits gegeben sind.
        //  (Jeder Geschichtsmeilenstein könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte.)
        //  Jeder Geschichtsmeilenstein hat eine Anzahl von Schritten, in denen er
        //  erreicht sein sollte. Ein Tipp würde nur generiert, wenn es Geschichtsmeilensteine
        //  gibt, deren Voraussetzungen gegeben sind und deren Schrittzahl überschritten ist.
        //  Tipps sollten zum aktuellen (oder zu einem nahen) Raum passen (ein Geschichtsmeilenstein
        //  könnte optional einen Lieblingsraum haben).
        //  Statt eines Tipps könnte auch eine neue Geschichte / Task starten.

        final AvDateTime start = db.nowDao().now();

        n.setNarrationSourceJustInCase(SC_ACTION);
        final AvTimeSpan timeElapsed = narrateAndDo();

        fireScActionDone(start);

        final AvDateTime dateTimeAfterActionBeforeUpdateWorld = start.plus(timeElapsed);
        db.nowDao().setNow(dateTimeAfterActionBeforeUpdateWorld);

        n.setNarrationSourceJustInCase(REACTIONS);
        final AvTimeSpan extraTimeElapsedDuringWorldUpdate =
                updateWorld(start, dateTimeAfterActionBeforeUpdateWorld);

        db.nowDao().setNow(dateTimeAfterActionBeforeUpdateWorld
                .plus(extraTimeElapsedDuringWorldUpdate));

        world.saveAll(true);
    }

    private void fireScActionDone(final AvDateTime startTimeOfUserAction) {
        world.scActionDone(startTimeOfUserAction);
    }

    private AvTimeSpan updateWorld(final AvDateTime lastTime,
                                   @NonNull final AvDateTime now) {
        if (now.equals(lastTime)) {
            return noTime();
        }

        if (now.minus(lastTime).longerThan(MAX_WORLD_TICK)) {
            final AvDateTime tickTime = lastTime.plus(MAX_WORLD_TICK);

            final AvTimeSpan additionalTimeElapsedTick = updateWorld(lastTime, tickTime);

            final AvTimeSpan remainingTime = now.minus(tickTime);
            final AvTimeSpan extraTime;
            if (additionalTimeElapsedTick.longerThan(remainingTime)) {
                extraTime = additionalTimeElapsedTick.minus(remainingTime);
            } else {
                extraTime = noTime();
            }

            return updateWorld(tickTime, now.plus(extraTime));
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
                db.narrationDao().requireNarration().getAnaphPersPronWennMgl(describableGO);
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