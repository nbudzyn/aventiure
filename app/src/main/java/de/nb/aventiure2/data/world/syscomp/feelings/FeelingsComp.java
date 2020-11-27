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
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

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

    private final SCActionStepCountDao scActionStepCountDao;

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
        scActionStepCountDao = db.scActionStepCountDao();
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
            // Häufig wird die adverbiale Angabe wohl verwendet werden - daher setzen
            // wir den Counter neu.
            getPcd().resetNextMuedigkeitshinweisActionStepCount(
                    scActionStepCountDao.stepCount()
            );
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

    /**
     * Gibt zurück, wie stark man durch die Müdigkeit im Gehen verlangsamt wird.
     */
    public double getMovementSpeedFactor() {
        return getPcd().getMovementSpeedFactor();
    }

    public void ausgeschlafen(final AvTimeSpan ausschlafenEffektHaeltVorFuer) {
        getPcd().ausgeschlafen(nowDao.now(),
                scActionStepCountDao.stepCount(),
                ausschlafenEffektHaeltVorFuer,
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

    /**
     * Speichert, dass das {@link IFeelingBeingGO} satt ist. Hat ggf. auch Auswirkungen
     * auf Laune und Müdigkeit.
     */
    public void saveSatt() {
        getPcd().saveSatt(nowDao.now(), zeitspanneNachEssenBisWiederHungrig,
                scActionStepCountDao.stepCount(),
                getMuedigkeitGemaessBiorhythmus());
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
                                "musst", "schlafen")
                                .beendet(PARAGRAPH),
                        du(PARAGRAPH,
                                "willst", "schlafen")
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
                        neuerSatz(SENTENCE,
                                "„So legt dich doch endlich schlafen!”, "
                                        + "denkst du bei dir"),
                        neuerSatz(SENTENCE,
                                "vielleicht könntest du hier ungestört schlafen?"),
                        neuerSatz(PARAGRAPH,
                                "Höchste Zeit, schlafen zu gehen!")
                                .beendet(PARAGRAPH)
                );
            default:
                throw new IllegalStateException("Unerwartete Müdigkeit: " + muedigkeit);
        }
    }

    private void narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
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

        getPcd().updateMuedigkeit(
                nowDao.now(),
                scActionStepCountDao.stepCount(),
                getMuedigkeitGemaessBiorhythmus());

        narrateMuedigkeitEvtlGeaendert(muedigkeitBisher);
    }

    private void narrateScWirdMuede() {
        n.narrateAlt(altScWirdMuede(), noTime());
    }

    private Collection<AbstractDescription<?>> altScWirdMuede() {
        checkArgument(
                getMuedigkeit() > FeelingIntensity.NEUTRAL,
                "Wird müde, aber FeelingIntensity ist NEUTRAL?"
        );

        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        res.add(
                du(PARAGRAPH, "wirst",
                        getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ()),
                du(PARAGRAPH, "bist",
                        getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ()),
                du(PARAGRAPH, "fühlst", "dich auf einmal " +
                                getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ(),
                        "auf einmal"),
                du(PARAGRAPH, "bist",
                        "auf einmal " +
                                getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ()),
                du(PARAGRAPH, "bist",
                        "mit einem Mal " +
                                getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ())
        );

        if (getMuedigkeit() == FeelingIntensity.NUR_LEICHT) {
            // NUR_LEICHT: "leicht erschöpft"

            res.add(
                    du(PARAGRAPH, "fühlst", "dich ein wenig erschöpft")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "spürst", "die Anstrengung")
                            .beendet(PARAGRAPH),
                    du("wirst", "etwas schläfrig")
                            .beendet(PARAGRAPH),
                    du(SENTENCE, "bist", "darüber etwas schläfrig geworden",
                            "darüber")
                            .beendet(SENTENCE)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.MERKLICH) {
            // MERKLICH: "erschöpft"
            res.add(
                    // Kann z.B. mit dem Vorsatz kombiniert werden zu etwas wie
                    // "Unten angekommen bist du ziemlich erschöpft..."
                    du("bist", "ziemlich erschöpft; ein Nickerchen täte dir "
                            + "gut")
                            .beendet(PARAGRAPH),
                    du("bist", "ziemlich erschöpft. Und müde")
                            .beendet(PARAGRAPH),
                    neuerSatz("Das war alles anstrengend!")
                            .beendet(PARAGRAPH)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.DEUTLICH) {
            //  DEUTLICH: "müde"
            res.add(
                    du("beginnst", "müde zu werden")
                            .beendet(SENTENCE),
                    du(PARAGRAPH, "bist", "jetzt müde", "jetzt")
                            .beendet(SENTENCE),
                    du(PARAGRAPH, "bist", "indessen müde geworden",
                            "indessen")
                            .beendet(SENTENCE),
                    neuerSatz("da wollen dir deine Augen nicht länger offen bleiben "
                            + "und du bekommst Lust zu schlafen")
                            .beendet(SENTENCE)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.STARK) {
            //  STARK: "völlig übermüdet"
            res.add(
                    du("bist", "ganz müde")
                            .beendet(SENTENCE)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.SEHR_STARK) {
            // SEHR_STARK: "todmüde"
            res.add(
                    du("bist", "hundemüde")
                            .beendet(PARAGRAPH),
                    neuerSatz("auf einmal beginnen dir die Augen zuzufallen")
            );
        }

        if (getMuedigkeit() == FeelingIntensity.PATHOLOGISCH) {
            // PATHOLOGISCH: "benommen"
            res.add(
                    neuerSatz("auf einmal ist dir, als hättest du einen "
                            + "Schlaftrunk genommen")
                            .komma()
            );
        }


        return res.build();
    }

    public void narrateAndUpgradeTemporaereMinimalmuedigkeit(
            final int temporaereMinimalmuedigkeit, final AvTimeSpan duration) {
        final int muedigkeitBisher = getMuedigkeit();

        getPcd().upgradeTemporaereMinimalmuedigkeit(
                nowDao.now(), scActionStepCountDao.stepCount(),
                temporaereMinimalmuedigkeit, duration,
                getMuedigkeitGemaessBiorhythmus());

        narrateMuedigkeitEvtlGeaendert(muedigkeitBisher);
    }

    private void narrateMuedigkeitEvtlGeaendert(final int muedigkeitBisher) {
        if (getGameObjectId() == SPIELER_CHARAKTER &&
                muedigkeitBisher < getMuedigkeit()) {
            narrateScWirdMuede();
        }
    }

    /**
     * Beschreibt - sofern nötig - die aktuelle Müdigkeit des SC.
     * (Dies ist eine Erinnerung an den Spieler.)
     */
    public void narrateScMuedigkeitIfNecessary() {
        if (getGameObjectId() != SPIELER_CHARAKTER) {
            return;
        }

        if (getMuedigkeit() == FeelingIntensity.NEUTRAL) {
            return;
        }

        final int scActionStepCount = scActionStepCountDao.stepCount();
        if (!getPcd().muedigkeitshinweisNoetig(scActionStepCount)) {
            return;
        }

        n.narrateAlt(altScIstMuede(), noTime());

        getPcd().resetNextMuedigkeitshinweisActionStepCount(scActionStepCount);
    }

    private Collection<AbstractDescription<?>> altScIstMuede() {
        checkArgument(
                getMuedigkeit() > FeelingIntensity.NEUTRAL,
                "Ist müde, aber FeelingIntensity ist NEUTRAL?"
        );

        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        res.add(
                du(PARAGRAPH, "bist",
                        getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ())
                        .beendet(PARAGRAPH),
                du(PARAGRAPH, "fühlst", "dich " +
                        getPcd().getMuedigkeitsData().getAdjektivphrasePraedikativ())
                        .beendet(PARAGRAPH)
        );

        // STORY In den Grimms-Märchen schauen, wie solche Sätze verknüpft sind und ggf. neue
        //  Verknüpfungen in den Combiner einbauen.

        if (getMuedigkeit() == FeelingIntensity.NUR_LEICHT) {
            // NUR_LEICHT: "leicht erschöpft"

            res.add(
                    du(PARAGRAPH, "fühlst", "dich ein wenig erschöpft")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "bist", "etwas erschöpft")
                            .beendet(PARAGRAPH)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.MERKLICH) {
            // MERKLICH: "erschöpft"
            res.add(
                    // Kann z.B. mit dem Vorsatz kombiniert werden zu etwas wie
                    // "Unten angekommen bist du ziemlich erschöpft..."
                    du(PARAGRAPH, "bist", "ziemlich erschöpft; ein "
                            + "Nickerchen täte dir gut")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "solltest", "etwas ruhen")
                            .beendet(PARAGRAPH),
                    du(SENTENCE, "möchtest", "ein wenig ruhen")
                            .beendet(PARAGRAPH)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.DEUTLICH) {
            //  DEUTLICH: "müde"
            res.add(
                    du(PARAGRAPH, "musst", "ein wenig schlafen")
                            .beendet(SENTENCE),
                    du(PARAGRAPH, "würdest", "gern ein wenig schlafen")
                            .beendet(SENTENCE),
                    du(PARAGRAPH, "möchtest", "dich schlafen legen")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "bist", "müde und möchtest gern schlafen")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "bist", "müde – wo ist ein Bett, in dass du dich "
                            + "legen und schlafen kannst?")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "bist", "matt und müde", "matt")
                            .beendet(SENTENCE),
                    neuerSatz(PARAGRAPH, "all die Erlebnisse haben dich müde gemacht"),
                    du(PARAGRAPH, "möchtest", "gern ein Auge zutun",
                            "gern")
                            .beendet(PARAGRAPH)
            );

            if (nowDao.now().getTageszeit() == Tageszeit.NACHTS) {
                res.add(
                        neuerSatz("es ist Schlafenszeit")
                                .beendet(SENTENCE)
                );
            }
        }

        if (getMuedigkeit() == FeelingIntensity.STARK) {
            //  STARK: "völlig übermüdet"

            res.add(
                    du(SENTENCE, "kannst", "kaum mehr die Augen offenhalten"),
                    du(PARAGRAPH, "musst", "endlich wieder einmal ausschlafen!"),
                    paragraph("wenn du dich doch schlafen legen könntest!"),
                    paragraph("könntest du dich doch in ein Bett legen!"),
                    du(SENTENCE, "bist",
                            "so müde, du kannst kaum mehr weiter")
                            .beendet(SENTENCE)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.SEHR_STARK) {
            //  SEHR_STARK: "todmüde"
            res.add(
                    du(PARAGRAPH, "bist",
                            "so müde, dass du auf der Stelle einschlafen könntest")
                            .komma()
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "bist",
                            "so müde von allem, dass du auf der Stelle einschlafen "
                                    + "könntest",
                            "von allem")
                            .komma()
                            .beendet(PARAGRAPH),
                    neuerSatz("immer wieder fallen dir die Augen zu")
                            .beendet(SENTENCE),
                    du("bist", "so müde, dass du die Augen kaum aufhalten magst")
                            .beendet(SENTENCE),
                    neuerSatz(SENTENCE, "dir ist, als könntest du vor Müdigkeit kaum mehr "
                            + "ein Glied regen")
                            .beendet(SENTENCE),
                    du("kannst",
                            "dich des Schlafes kaum wehren")
                            .beendet(PARAGRAPH)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.PATHOLOGISCH) {
            //  PATHOLOGISCH: "benommen"
            res.add(
                    du("empfindest",
                            "so große Müdigkeit, dass dich deine Glieder "
                                    + "kaum halten")
                            .komma()
                            .beendet(SENTENCE),
                    du("kannst",
                            "dich des Schlafes kaum wehren")
                            .beendet(PARAGRAPH),
                    du(PARAGRAPH, "fühlst",
                            "dich wie eingeschläfert")
            );
        }

        return res.build();
    }

    private int getMuedigkeitGemaessBiorhythmus() {
        return muedigkeitsBiorythmus.get(nowDao.now().getTime());
    }
}
