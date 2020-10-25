package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import java.util.Map;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

/**
 * Component for a {@link GameObject}: The game object
 * has needs and feelings.
 */
public class FeelingsComp extends AbstractStatefulComponent<FeelingsPCD> {
    protected AvNowDao nowDao;

    protected final Narrator n;

    @NonNull
    private final Mood initialMood;

    @NonNull
    private final Biorhythmus muedigkeitsBiorythmus;

    @NonNull
    private final MuedigkeitsData initialMuedigkeitsData;

    @NonNull
    private final HungerData initialHungerData;

    @NonNull
    private final AvTimeSpan zeitspanneNachEssenBisWiederHungrig;

    // STORY Je ein Default für in-Group (die Kumpels) und für out-Group (alle anderen). Es gibt
    //  eine Methode, die ermittelt, wer in-group ist.
    @NonNull
    private final Map<FeelingTowardsType, Float> defaultFeelingsTowards;

    @NonNull
    private final Map<GameObjectId, Map<FeelingTowardsType, Float>> initialFeelingsTowards;

    /**
     * Constructor for {@link FeelingsComp}.
     */
    public FeelingsComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final Narrator n,
                        final Mood initialMood,
                        final Biorhythmus muedigkeitsBiorythmus,
                        final MuedigkeitsData initialMuedigkeitsData,
                        final HungerData initialHungerData,
                        final AvTimeSpan zeitspanneNachEssenBisWiederHungrig,
                        final Map<FeelingTowardsType, Float> defaultFeelingsTowards,
                        final Map<GameObjectId, Map<FeelingTowardsType, Float>>
                                initialFeelingsTowards) {
        super(gameObjectId, db.feelingsDao());

        nowDao = db.nowDao();
        this.n = n;
        this.initialMood = initialMood;
        this.muedigkeitsBiorythmus = muedigkeitsBiorythmus;
        this.initialMuedigkeitsData = initialMuedigkeitsData;
        this.initialHungerData = initialHungerData;
        this.zeitspanneNachEssenBisWiederHungrig = zeitspanneNachEssenBisWiederHungrig;
        this.defaultFeelingsTowards = defaultFeelingsTowards;
        this.initialFeelingsTowards = initialFeelingsTowards;
    }

    @Override
    protected FeelingsPCD createInitialState() {
        return new FeelingsPCD(getGameObjectId(), initialMood,
                initialMuedigkeitsData,
                initialHungerData,
                initialFeelingsTowards);
    }

    public AdverbialeAngabeSkopusSatz getAdverbialeAngabe() {
        if (getMuedigkeit() > Math.abs(getMood().getGradDerFreude())) {
            return getPcd().getMuedigkeitsData().getAdverbialeAngabe();
        }

        return getMood().getAdverbialeAngabe();
    }

    public boolean hasMood(final Mood mood) {
        return getMood() == mood;
    }

    public boolean isEmotional() {
        return getMood().isEmotional();
    }

    public boolean isSehrEmotional() {
        return getMood().isSehrEmotional();
    }

    public boolean isFroehlicherAls(final Mood other) {
        return getMood().isFroehlicherAls(other);
    }

    public boolean isTraurigerAls(final Mood other) {
        return getMood().isTraurigerAls(other);
    }

    private Mood getMood() {
        return getPcd().getMood();
    }

    public void requestMoodMin(final Mood mood) {
        getPcd().requestMoodMin(mood);
    }

    public void requestMoodMax(final Mood mood) {
        getPcd().requestMoodMax(mood);
    }

    public void requestMood(final Mood mood) {
        getPcd().requestMood(mood);
    }

    public Hunger getHunger() {
        return getPcd().getHunger();
    }

    public void ausgeschlafen(final AvTimeSpan ausschlafenEffektHaeltVorFuer) {
        final AvDateTime now = nowDao.now();
        final int muedigkeitGemaessBiorhythmus = muedigkeitsBiorythmus.get(now.getTime());

        getPcd().ausgeschlafen(now, ausschlafenEffektHaeltVorFuer, muedigkeitGemaessBiorhythmus);
    }

    public void upgradeTemporaereMinimalmuedigkeit(
            final int temporaereMinimalmuedigkeit, final AvTimeSpan duration) {
        final AvDateTime now = nowDao.now();
        final int muedigkeitGemaessBiorhythmus = muedigkeitsBiorythmus.get(now.getTime());

        getPcd().upgradeTemporaereMinimalmuedigkeit(
                now, temporaereMinimalmuedigkeit, duration, muedigkeitGemaessBiorhythmus);
    }

    /**
     * Gibt die Müdigkeit als positiver {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public int getMuedigkeit() {
        return getPcd().getMuedigkeit();
    }

    private void updateMuedigkeit() {
        final int muedigkeitGemaeßBiorhythmus = muedigkeitsBiorythmus.get(nowDao.now().getTime());

        getPcd().updateMuedigkeit(nowDao.now(), muedigkeitGemaeßBiorhythmus);
    }

    private void updateHunger() {
        getPcd().updateHunger(nowDao.now());
    }

    public void saveSatt() {
        getPcd().saveSatt(nowDao.now(), zeitspanneNachEssenBisWiederHungrig);
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
    public void upgradeFeelingsTowards(final GameObjectId target,
                                       final FeelingTowardsType type,
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

    /**
     * Diese Methode muss für jedes Feeling Being aufgerufen werden, wenn Zeit vergeht!
     */
    public void onTimePassed(final AvDateTime startTime,
                             final AvDateTime endTime) {
        nowDao.setNow(endTime);

        final Hunger hungerBisher = getHunger();

        updateHunger();

        if (getGameObjectId() == SPIELER_CHARAKTER &&
                hungerBisher == SATT && getHunger() != SATT) {
            narrateScWirdHungrig();
        }

        updateMuedigkeit();
        // FIXME Hier Texte zur Müdigkeit erzeugen (retrofitten!)
    }

    private void narrateScWirdHungrig() {
        n.narrateAlt(
                du(PARAGRAPH, "fühlst", "dich allmählich etwas hungrig",
                        noTime())
                        .undWartest(),
                neuerSatz("Wann hast du eigentlich zuletzt etwas gegessen? Das "
                                + "muss schon eine Weile her sein.",
                        noTime()),
                du(PARAGRAPH, "bekommst", "so langsam Hunger",
                        "so langsam",
                        noTime()),
                neuerSatz(PARAGRAPH, "Allmählich überkommt dich der Hunger",
                        noTime()),
                neuerSatz(PARAGRAPH, "Allmählich regt sich wieder der Hunger",
                        noTime())
                        .undWartest(),
                neuerSatz("Dir fällt auf, dass du Hunger hast",
                        noTime())
                        .komma(),
                du("empfindest", "wieder leichten Hunger",
                        noTime())
                        .undWartest()
        );
    }

    public void narrateAndDoSCMitEssenKonfrontiert() {
        final Hunger hunger = getHunger();
        switch (hunger) {
            case SATT:
                return;
            case HUNGRIG:
                narrateAnDoSCMitEssenKonfrontiertReagiertHungrig();
                return;
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private void narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
        // FIXME Hunger - sinnvoll machen... -- vollgefressen -> müde?

        n.narrateAlt(
                neuerSatz("Mmh!", noTime())
                        .beendet(PARAGRAPH),
                neuerSatz("Dir läuft das Wasser im Munde zusammen", noTime()),
                du(SENTENCE, "hast", "Hunger", noTime())
                        .undWartest(),
                du(SENTENCE, "bist", "hungrig", noTime())
                        .undWartest(),
                neuerSatz("Dir fällt auf, wie hungrig du bist", noTime())
                        .komma()
        );
    }
}
