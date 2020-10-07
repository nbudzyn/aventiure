package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import java.util.Map;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

/**
 * Component for a {@link GameObject}: The game object
 * has needs and feelings.
 */
public class FeelingsComp extends AbstractStatefulComponent<FeelingsPCD> {
    protected final NarrationDao n;

    @NonNull
    private final Mood initialMood;
    @NonNull
    private final Hunger initialHunger;
    @NonNull
    private final AvDateTime initialZuletztGegessen;
    /**
     * Zeit, die es braucht, bis das FeelingBeing nach dem Essen wieder hungrig wird
     */
    private final AvTimeSpan zeitspanneNachEssenBisWiederHungrig;

    // STORY Je ein Default für in-Group (die Kumpels) und für out-Group (alle anderen). Es gibt
    //  eine Methode, die ermittelt, wer in-group ist.
    private final Map<FeelingTowardsType, Float> defaultFeelingsTowards;

    @NonNull
    private final Map<GameObjectId, Map<FeelingTowardsType, Float>> initialFeelingsTowards;

    /**
     * Constructor for {@link FeelingsComp}.
     */
    public FeelingsComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        @NonNull final Mood initialMood,
                        @NonNull final Hunger initialHunger,
                        @NonNull final AvDateTime initialZuletztGegessen,
                        final AvTimeSpan zeitspanneNachEssenBisWiederHungrig,
                        final Map<FeelingTowardsType, Float> defaultFeelingsTowards,
                        final Map<GameObjectId, Map<FeelingTowardsType, Float>>
                                initialFeelingsTowards) {
        super(gameObjectId, db.feelingsDao());

        n = db.narrationDao();

        this.initialMood = initialMood;
        this.initialHunger = initialHunger;
        this.initialZuletztGegessen = initialZuletztGegessen;
        this.zeitspanneNachEssenBisWiederHungrig = zeitspanneNachEssenBisWiederHungrig;
        this.defaultFeelingsTowards = defaultFeelingsTowards;
        this.initialFeelingsTowards = initialFeelingsTowards;
    }

    @Override
    @NonNull
    protected FeelingsPCD createInitialState() {
        return new FeelingsPCD(getGameObjectId(), initialMood, initialHunger,
                initialZuletztGegessen, initialFeelingsTowards);
    }

    public boolean hasMood(final Mood mood) {
        return getMood() == mood;
    }

    @NonNull
    public Mood getMood() {
        return getPcd().getMood();
    }

    public void setMoodMin(final Mood mood) {
        if (getMood().isTraurigerAls(mood)) {
            getPcd().setMood(mood);
        }
    }

    public void setMoodMax(final Mood mood) {
        if (!getMood().isTraurigerAls(mood)) {
            getPcd().setMood(mood);
        }
    }

    public void setMood(final Mood mood) {
        getPcd().setMood(mood);
    }

    @NonNull
    public Hunger getHunger() {
        return getPcd().getHunger();
    }

    public void setHunger(final Hunger hunger) {
        getPcd().setHunger(hunger);
    }

    public AvDateTime getWiederHungrigAb() {
        return getZuletztGegessen()
                .plus(getZeitspanneNachEssenBisWiederHungrig());
    }

    @NonNull
    private AvDateTime getZuletztGegessen() {
        return getPcd().getZuletztGegessen();
    }

    public void setZuletztGegessen(final AvDateTime zuletztGegessen) {
        getPcd().setZuletztGegessen(zuletztGegessen);
    }

    private AvTimeSpan getZeitspanneNachEssenBisWiederHungrig() {
        return zeitspanneNachEssenBisWiederHungrig;
    }

    /**
     * Aktualisiert die Gefühle des Typs {@link FeelingTowardsType}: Die
     * Gefühle werden um das <code>increment</code> erhöht oder gesenkt;
     * wenn die Gefühle allerdings bereits stärker als <code>bound</code>
     * sind, werden sie nicht verändert. Außerdem werden die Gefühle nicht über
     * <code>bound</code> bzw. <code>-bound</code> hinaus erhöht bzw. gesenkt.
     * <p>
     * <ul>
     * <li>Beispiel 1: Die Oma mag den Schüler ein wenig. Der Schüler hilft der Oma über die
     * Straße. Danach mag die Oma den Schüler ein wenig mehr.
     * <li>Beispiel 2: Die Oma mag den Schüler nicht so sehr. Der Schüler hilft der Oma über die
     * Straße. Danach mag die Oma den Schüler ein wenig mehr.
     * <li>Beispiel 3: Die Oma ist vom Schüler total begeistert. Der Schüler hilft der Oma über die
     * Straße. Danach ist die Oma immer noch vom Schüler total begeistert (unverändert).
     * <li>Beispiel 4: Die Oma kann den Schüler absolut nicht ausstehen.. Der Schüler hilft der Oma über die
     * Straße. Danach kann Oma den Schüler immer noch absolut nicht ausstehen (unverändert).
     * (Das stärker Gefühl überschattet das neue.)
     *
     * @param increment Um wieviel die Gefühle geändert werden sollen (positiv oder negativ).
     *                  Hierfür ist
     *                  die Frage relevant: Wie häufig muss etwas passieren, damit sich die Gefühle
     *                  ändern?
     * @param bound     Hier bieten sich die Werte aus {@link FeelingIntensity} an. Ob die Werte
     *                  positiv oder negativ angegeben werden ist irrelevant. Für diesen Parameter
     *                  ist die Frage relevant: Was ist die Stärke an Gefühlen, ab denen diese
     *                  Handlung keinen Unterschied mehr macht? Wenn die Beziehung zerrüttet
     *                  ist, vielleicht macht es dann einen Unterschied, wenn man 100x täglich
     *                  Rosen mitbringt. Aber es macht vielleicht keinen Unterschied, auch wenn man
     *                  10.000x Staub saugt. Also wäre <code>bound</code> für das Mitbringen von
     *                  Rosen höher.
     */
    public void upgradeFeelingsTowards(final GameObjectId target, final FeelingTowardsType type,
                                       final float increment, final int bound) {
        if (increment == 0) {
            return;
        }

        final float oldValue = getFeelingTowards(target, type);

        if (Math.abs(oldValue) >= Math.abs(bound)) {
            // Das stärkere, schon bestehende Gefühl überschattet die Änderung.
            return;
        }

        final float newValue;
        if (increment > 0) {
            newValue = Math.min(oldValue + increment, Math.abs(bound));
        } else {
            newValue = Math.max(oldValue + increment, -Math.abs(bound));
        }

        getPcd().setFeelingTowards(target, type, newValue);
    }

    /**
     * Setzt alle Gefühle zu diesem <code>target</code> auf den Default-Wert zurück.
     * <p>
     * Diese Methode wird man sehr selten brauchen - etwa, wenn jemand verzaubert wird
     * und alle Gefühle zu jemand anderem vergisst.
     */
    public void resetFeelingsTowards(final GameObjectId target) {
        getPcd().removeFeelingsTowards(target);
    }

    @CheckReturnValue
    public float getFeelingTowards(final GameObjectId target, final FeelingTowardsType type) {
        final Float res = getPcd().getFeelingTowards(target, type);

        if (res != null) {
            return res;
        }

        return defaultFeelingsTowards.get(type);
    }
}
