package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Map;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;

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
        getPcd().ausgeschlafen(nowDao.now(), ausschlafenEffektHaeltVorFuer,
                getMuedigkeitGemaessBiorhythmus());
    }

    /**
     * Gibt die Müdigkeit als positiver {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public int getMuedigkeit() {
        return getPcd().getMuedigkeit();
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

        narrateAndUpdateHunger();
        narrateAndUpdateMuedigkeit();
    }

    private void narrateAndUpdateHunger() {
        final Hunger hungerBisher = getHunger();

        updateHunger();

        if (getGameObjectId() == SPIELER_CHARAKTER &&
                hungerBisher == SATT && getHunger() != SATT) {
            narrateScWirdHungrig();
        }
    }

    private void narrateScWirdHungrig() {
        n.narrateAlt(noTime(),
                du(PARAGRAPH, "fühlst", "dich allmählich etwas hungrig")
                        .undWartest(),
                neuerSatz("Wann hast du eigentlich zuletzt etwas gegessen? Das "
                        + "muss schon eine Weile her sein."),
                du(PARAGRAPH, "bekommst", "so langsam Hunger",
                        "so langsam"),
                neuerSatz(PARAGRAPH, "Allmählich überkommt dich der Hunger"),
                neuerSatz(PARAGRAPH, "Allmählich regt sich wieder der Hunger")
                        .undWartest(),
                neuerSatz("Dir fällt auf, dass du Hunger hast")
                        .komma(),
                du("empfindest", "wieder leichten Hunger")
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

    public void narrateAndDoSCMitSchlafgelegenheitKonfrontiert() {
        final int muedigkeit = getMuedigkeit();
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                // fall-through
            case FeelingIntensity.NUR_LEICHT:
                return;

            case FeelingIntensity.MERKLICH:
            case FeelingIntensity.DEUTLICH:
                n.narrateAlt(noTime(),
                        du(PARAGRAPH,
                                "solltest", "etwas schlafen")
                                .beendet(PARAGRAPH),
                        du(PARAGRAPH,
                                "kannst", "gewiss eine Mütze Schlaf gebrauchen!")
                                .beendet(PARAGRAPH),
                        paragraph("Ein Bett!"));
                return;
            case FeelingIntensity.STARK:
                n.narrateAlt(noTime(),
                        du(PARAGRAPH,
                                "solltest", "etwas schlafen")
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "es ist Zeit, schlafen zu gehen!")
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Zeit, sich schlafen zu legen.")
                                .beendet(PARAGRAPH)
                );

            case FeelingIntensity.SEHR_STARK:
            case FeelingIntensity.PATHOLOGISCH:
                n.narrateAlt(noTime(),
                        neuerSatz(PARAGRAPH,
                                "Höchste Zeit, schlafen zu gehen!")
                                .beendet(PARAGRAPH)
                );
            default:
                throw new IllegalStateException("Unerwartete Müdigkeit: " + muedigkeit);
        }
    }

    private void narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
        // FIXME Hunger - sinnvoll machen... -- vollgefressen -> müde? (z.B. für 90 Minuten)

        n.narrateAlt(noTime(),
                neuerSatz("Mmh!")
                        .beendet(PARAGRAPH),
                neuerSatz("Dir läuft das Wasser im Munde zusammen"),
                du(SENTENCE, "hast", "Hunger")
                        .undWartest(),
                du(SENTENCE, "bist", "hungrig")
                        .undWartest(),
                neuerSatz("Dir fällt auf, wie hungrig du bist")
                        .komma()
        );
    }

    private void narrateAndUpdateMuedigkeit() {
        final int muedigkeitBisher = getMuedigkeit();

        getPcd().updateMuedigkeit(nowDao.now(), getMuedigkeitGemaessBiorhythmus());

        narrateMuedigkeitEvtlGeaendert(muedigkeitBisher);
    }

    private void narrateScWirdMuede(final int muedigkeitAlt, final int muedigkeitNeu) {
        n.narrateAlt(altScWirdMuede(muedigkeitNeu), noTime());
    }

    private static Collection<AbstractDescription<?>> altScWirdMuede(final int muedigkeitNeu) {
        checkArgument(
                muedigkeitNeu > FeelingIntensity.NEUTRAL,
                "Wird müde, aber FeelingIntensity ist NEUTRAL?"
        );

        switch (muedigkeitNeu) {
            case FeelingIntensity.NUR_LEICHT:
                return ImmutableList.of(
                        du(PARAGRAPH, "fühlst", "dich ein wenig erschöpft")
                                .beendet(PARAGRAPH),
                        du(PARAGRAPH, "spürst", "die Anstrengung")
                                .beendet(PARAGRAPH)
                        // FIXME Hier weitere Texte zur Müdigkeit erzeugen
                );
            case FeelingIntensity.DEUTLICH:
                // FIXME Hier andere Texte zur Müdigkeit erzeugen
            case FeelingIntensity.STARK:
                // FIXME Hier andere Texte zur Müdigkeit erzeugen
            case FeelingIntensity.SEHR_STARK:
                // FIXME Hier andere Texte zur Müdigkeit erzeugen
            case FeelingIntensity.PATHOLOGISCH:
                // FIXME Hier andere Texte zur Müdigkeit erzeugen
            case FeelingIntensity.MERKLICH:
                return ImmutableList.of(
                        // Kann z.B. mit dem Vorsatz kombiniert werden zu etwas wie
                        // "Unten angekommen bist du ziemlich erschöpft..."
                        du("bist", "ziemlich erschöpft; ein Nickerchen täte dir "
                                + "gut")
                                .beendet(PARAGRAPH),
                        du("bist", "ziemlich erschöpft. Und müde")
                                .beendet(PARAGRAPH),
                        // FIXME "Das war anstrengend" nur, wenn
                        //  die Müdikeit NICHT durch den Biorhythmus kam!
                        neuerSatz("Das war anstrengend!")
                                .beendet(PARAGRAPH)
                );

            default:
                throw new IllegalStateException("Unexpected value: " + muedigkeitNeu);
        }
    }

    public void narrateAndUpgradeTemporaereMinimalmuedigkeit(
            final int temporaereMinimalmuedigkeit, final AvTimeSpan duration) {
        final int muedigkeitBisher = getMuedigkeit();

        getPcd().upgradeTemporaereMinimalmuedigkeit(
                nowDao.now(), temporaereMinimalmuedigkeit, duration,
                getMuedigkeitGemaessBiorhythmus());

        narrateMuedigkeitEvtlGeaendert(muedigkeitBisher);
    }

    private void narrateMuedigkeitEvtlGeaendert(final int muedigkeitBisher) {
        if (getGameObjectId() == SPIELER_CHARAKTER &&
                muedigkeitBisher < getMuedigkeit()) {
            narrateScWirdMuede(muedigkeitBisher, getMuedigkeit());
        }
    }

    private int getMuedigkeitGemaessBiorhythmus() {
        return muedigkeitsBiorythmus.get(nowDao.now().getTime());
    }
}
