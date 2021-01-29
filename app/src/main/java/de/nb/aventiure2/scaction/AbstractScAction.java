package de.nb.aventiure2.scaction;

import androidx.annotation.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.narration.IPlayerAction;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.german.description.Kohaerenzrelation;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.SC_ACTION;
import static de.nb.aventiure2.data.time.AvTimeSpan.days;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.DISKONTINUITAET;

/**
 * An action the player could choose.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractScAction implements IPlayerAction {
    private static final AvTimeSpan MAX_WORLD_TICK = days(1);

    private final SCActionStepCountDao scActionStepCountDao;
    protected final TimeTaker timeTaker;
    protected final World world;
    protected final Narrator n;

    protected final SpielerCharakter sc;

    protected AbstractScAction(final SCActionStepCountDao scActionStepCountDao,
                               final TimeTaker timeTaker, final Narrator n,
                               final World world) {
        this.scActionStepCountDao = scActionStepCountDao;
        this.timeTaker = timeTaker;
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
        final AvDateTime start = timeTaker.now();

        n.setNarrationSourceJustInCase(SC_ACTION);

        narrateAndDo();

        scActionStepCountDao.inc();
        fireScActionDone(start);

        n.setNarrationSourceJustInCase(REACTIONS);

        // Jetzt die Zeit zurücksetzen und in der Welt das nachholen,
        // was passiert ist, während der Benutzer gehandelt hat!
        final AvDateTime until = timeTaker.now();
        timeTaker.setNow(start);
        updateWorld(until);

        final AvDateTime dateTimeBetweenMainWorldUpdateAndHints = timeTaker.now();

        fireAfterScActionAndFirstWorldUpdate();

        if (timeTaker.now().isAfter(dateTimeBetweenMainWorldUpdateAndHints)) {
            updateWorld(timeTaker.now());
        }

        world.saveAll(true);
        n.saveAll();
        timeTaker.save();
    }

    private void fireScActionDone(final AvDateTime startTimeOfScAction) {
        world.scActionDone(startTimeOfScAction);
    }

    private void updateWorld(final AvDateTime until) {
        checkNotNull(until, "now is null");

        if (timeTaker.now().isEqualOrAfter(until)) {
            return;
        }

        while (until.minus(timeTaker.now()).longerThan(MAX_WORLD_TICK)) {
            final AvDateTime endOfTick = timeTaker.now().plus(MAX_WORLD_TICK);
            world.narrateAndDoReactions().onTimePassed(timeTaker.now(), endOfTick);

            timeTaker.setNow(endOfTick);
        }

        world.narrateAndDoReactions().onTimePassed(timeTaker.now(), until);

        final AvDateTime timeAfterReactions = timeTaker.now();
        if (timeAfterReactions.isAfter(until)) {
            // Sonderfall! Einzelne Game Objects haben Aktionen begonnen, die
            // Zusatzzeit gebraucht haben.
            // Dann lassen wir diese "Zusatzzeit" auch offiziell hier vergehen, so dass
            // jedes Game Object darauf reagieren kann.
            // (Rekursiv möglich.)
            timeTaker.setNow(until);

            updateWorld(timeAfterReactions);
        } else {
            timeTaker.setNow(until);
        }
    }

    private void fireAfterScActionAndFirstWorldUpdate() {
        world.narrateAndDoReactions().afterScActionAndFirstWorldUpdate();
    }

    abstract public void narrateAndDo();

    protected Kohaerenzrelation getKohaerenzrelationFuerUmformulierung() {
        if (isDefinitivDiskontinuitaet()) {
            return DISKONTINUITAET;
        }

        if (isDefinitivWiederholung()) {
            return Kohaerenzrelation.WIEDERHOLUNG;
        }

        if (isDefinitivFortsetzung()) {
            return Kohaerenzrelation.FORTSETZUNG;
        }

        return Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
    }

    /**
     * Gibt zurück, ob der Benutzer dasselbe definitiv schon einmal getan und zwischendrin nichts
     * anderes getan hat und dass es sich um eine Wiederholung (also einen zweiten / dritten...
     * Versuch) handelt. (Es könnte zwischendrin durchaus Reaktionen von anderen Wesen gegeben
     * haben.)
     * <p>
     * Ein Rückgabewert von {@code false} kann also bedeuten, dass der Benutzer definitiv
     * nicht dasselbe schon einmal getan hat - oder dass sich das System unsicher ist.
     *
     * @see #isDefinitivFortsetzung()
     */
    protected abstract boolean isDefinitivWiederholung();

    /**
     * Gibt zurück, ob der Benutzer dasselbe definitiv schon einmal getan und zwischendrin nichts
     * anderes getan hat und dass es sich um eine Fortsetzung handelt (er wartet weiter o.Ä.).
     * (Es könnte zwischendrin durchaus Reaktionen von anderen Wesen gegeben haben.)
     * <p>
     * Ein Rückgabewert von {@code false} kann also bedeuten, dass der Benutzer definitiv
     * nicht dasselbe schon einmal getan hat - oder dass sich das System unsicher ist.
     *
     * @see #isDefinitivWiederholung()
     */
    protected abstract boolean isDefinitivFortsetzung();

    /**
     * Gibt zurück, ob diese Handlung definitiv eine <i>Diskontinuität</i> in der Erzählung
     * bedeutet. Das könnte z.B. der Fall, sein, wenn der Benutzer einen Gegenstand nimmt
     * und ihn dann gleich wieder an derselben Stelle absetzt.
     * <p>
     * Ein Rückgabewert von {@code false} kann also bedeuten, dass es sich definitiv <i>nicht </i>
     * um eine Diskontinuität handelt - oder das sich das System unsicher ist.
     */
    abstract protected boolean isDefinitivDiskontinuitaet();

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}