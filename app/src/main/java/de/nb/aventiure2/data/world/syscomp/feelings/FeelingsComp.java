package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatWerdenMit;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Component for a {@link GameObject}: The game object
 * has needs and feelings.
 */
public class FeelingsComp extends AbstractStatefulComponent<FeelingsPCD> {
    protected TimeTaker timeTaker;

    private final SCActionStepCountDao scActionStepCountDao;

    protected final Narrator n;

    @Nullable
    private final MemoryComp memoryComp;

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

    // IDEA Je ein Default für in-Group (die Kumpels) und für out-Group (alle anderen). Es gibt
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
                        final TimeTaker timeTaker, final Narrator n,
                        @Nullable final MemoryComp memoryComp,
                        final Mood initialMood,
                        final Biorhythmus muedigkeitsBiorythmus,
                        final MuedigkeitsData initialMuedigkeitsData,
                        final HungerData initialHungerData,
                        final AvTimeSpan zeitspanneNachEssenBisWiederHungrig,
                        final Map<FeelingTowardsType, Float> defaultFeelingsTowards,
                        final Map<GameObjectId, Map<FeelingTowardsType, Float>>
                                initialFeelingsTowards) {
        super(gameObjectId, db.feelingsDao());

        this.timeTaker = timeTaker;
        this.n = n;
        scActionStepCountDao = db.scActionStepCountDao();
        this.memoryComp = memoryComp;
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

    public ImmutableList<AdverbialeAngabeSkopusSatz> altAdverbialeAngabenSkopusSatz() {
        if (getMuedigkeit() > Math.abs(getMood().getGradDerFreude())) {
            // Häufig wird die adverbiale Angabe wohl verwendet werden - daher setzen
            // wir den Counter neu.
            resetMuedigkeitshinweisStepCount();
            return getPcd().getMuedigkeitsData().altAdverbialeAngabenSkopusSatz();
        }

        return getMood().altAdverbialeAngabenSkopusSatz();
    }

    private ImmutableList<AdverbialeAngabeSkopusVerbAllg> altAdverbialeAngabenSkopusVerbAllg() {
        if (getMuedigkeit() > Math.abs(getMood().getGradDerFreude())) {
            // Häufig wird die adverbiale Angabe wohl verwendet werden - daher setzen
            // wir den Counter neu.
            resetMuedigkeitshinweisStepCount();
            return getPcd().getMuedigkeitsData().altAdverbialeAngabenSkopusVerbAllg();
        }

        return getMood().altAdverbialeAngabenSkopusVerbAllg();
    }

    /**
     * Eventuell Adjektive zur Beschreibung des Gefühls, <i>möglicherweise leer</i>.
     */
    @NonNull
    private ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr() {
        if (getMuedigkeit() > Math.abs(getMood().getGradDerFreude())) {
            // Häufig wird diese Phrase wohl verwendet werden - daher setzen
            // wir den Counter neu.
            resetMuedigkeitshinweisStepCount();
            return getPcd().getMuedigkeitsData().altAdjektivphrase();
        }

        return getMood().altAdjPhr();
    }

    private void resetMuedigkeitshinweisStepCount() {
        resetMuedigkeitshinweisStepCount(scActionStepCountDao.stepCount());
    }

    private void resetMuedigkeitshinweisStepCount(final int scActionStepCount) {
        getPcd().resetNextMuedigkeitshinweisActionStepCount(scActionStepCount);
    }

    public boolean hasMood(final Mood mood) {
        return getMood() == mood;
    }

    public boolean isEmotional() {
        return getMood().isEmotional();
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

    public void menschAusgeschlafen(final AvTimeSpan schlafdauer) {
        ausgeschlafen(MuedigkeitsData.calcAusschlafenEffektHaeltBeimMenschenVorFuer(schlafdauer));
    }

    public void ausgeschlafen(final AvTimeSpan ausschlafenEffektHaeltVorFuer) {
        getPcd().ausgeschlafen(timeTaker.now(),
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
        getPcd().updateHunger(timeTaker.now());
    }

    /**
     * Speichert, dass das {@link IFeelingBeingGO} satt ist. Hat ggf. auch Auswirkungen
     * auf Laune und Müdigkeit.
     */
    public void saveSatt() {
        getPcd().saveSatt(timeTaker.now(), zeitspanneNachEssenBisWiederHungrig,
                scActionStepCountDao.stepCount(),
                getMuedigkeitGemaessBiorhythmus());
    }

    /**
     * Gibt alternative Sätze zurück, die die gefühlsmäßige Reaktion dieses Feeling Beings
     * auf den SC beschreiben, wenn die beiden sich begegnen.
     * Hier werden <i>keine Begrüßungen</i> beschrieben!
     */
    @NonNull
    public ImmutableList<Satz> altReaktionBeiBegegnungMitScSaetze(
            final SubstantivischePhrase gameObjectSubjekt) {
        final FeelingTowardsType strongestFeelingTowards =
                getStrongestFeelingTowardsType();

        final int schwelle =
                Math.abs(getFeelingTowards(SPIELER_CHARAKTER, strongestFeelingTowards)) + 1;

        if (schwelle < getMuedigkeit() || schwelle < Math.abs(getMood().getGradDerFreude())) {
            final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr = altAdjPhr();
            final ImmutableList<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben =
                    altAdverbialeAngabenSkopusVerbAllg();

            return FeelingsSaetzeUtil.toReaktionSaetze(
                    gameObjectSubjekt, altAdjPhr, adverbialeAngaben);
        }

        return altReaktionBeiBegegnungMitScSaetze(gameObjectSubjekt, strongestFeelingTowards);
    }

    /**
     * Gibt alternative Sätze zurück, die die durch dieses Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings auf den SC beschreiben, wenn die beiden sich begegnen.
     * Hier werden <i>keine Begrüßungen</i> beschrieben!
     */
    @NonNull
    private ImmutableList<Satz> altReaktionBeiBegegnungMitScSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final FeelingTowardsType type) {
        return altReaktionBeiBegegnungSaetze(
                gameObjectSubjekt,
                SPIELER_CHARAKTER,
                Personalpronomen.get(P2,
                        // Wir tun hier so, als wäre der Spieler männlich, aber das
                        // ist egal - die Methode garantiert, dass niemals etwas
                        // wie "du, der du..." oder
                        // "du, die du..." generiert wird.
                        M),
                type);
    }

    /**
     * Gibt eventuell alternative Sätze zurück, die beschreiben, wie dieses Feeling Being
     * den SC ansieht, wenn die beiden sich begegnen.
     * <p>
     * Die Sätze sind bereits in
     * {@link #altReaktionBeiBegegnungSaetze(SubstantivischePhrase, GameObjectId, SubstantivischePhrase, FeelingTowardsType)}
     * enthalten.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    public ImmutableList<Satz> altSCBeiBegegnungAnsehenSaetze(
            final SubstantivischePhrase gameObjectSubjekt) {
        final FeelingTowardsType strongestFeelingTowards =
                getStrongestFeelingTowardsType();

        final int schwelle =
                Math.abs(getFeelingTowards(SPIELER_CHARAKTER, strongestFeelingTowards)) + 1;

        if (schwelle < getMuedigkeit() || schwelle < Math.abs(getMood().getGradDerFreude())) {
            return FeelingsSaetzeUtil.toAnsehenSaetze(
                    gameObjectSubjekt, altAdverbialeAngabenSkopusVerbAllg());
        }

        return altBeiBegegnungAnsehenSaetze(
                gameObjectSubjekt,
                SPIELER_CHARAKTER,
                Personalpronomen.get(P2,
                        // Wir tun hier so, als wäre der Spieler männlich, aber das
                        // ist egal - die Methode garantiert, dass niemals etwas
                        // wie "du, der du..." oder
                        // "du, die du..." generiert wird.
                        M),
                strongestFeelingTowards);
    }

    /**
     * Gibt eventuell alternative Sätze zurück, die den Eindruck
     * beschreiben, den dieses Feeling Beings auf den SC macht, wenn die beiden sich
     * begegnen.
     * <p>
     * Die Sätze sind bereits in
     * {@link #altReaktionBeiBegegnungSaetze(SubstantivischePhrase, GameObjectId, SubstantivischePhrase, FeelingTowardsType)}
     * enthalten.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    public ImmutableList<Satz> altEindruckAufScBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt) {
        final FeelingTowardsType strongestFeelingTowards =
                getStrongestFeelingTowardsType();
        final int schwelle =
                Math.abs(getFeelingTowards(SPIELER_CHARAKTER, strongestFeelingTowards)) + 1;

        if (schwelle < getMuedigkeit() || schwelle < Math.abs(getMood().getGradDerFreude())) {
            final ImmutableList<AdjPhrOhneLeerstellen> adjektivPhrasen = altAdjPhr();
            if (!adjektivPhrasen.isEmpty()) {
                return FeelingsSaetzeUtil.toEindrueckSaetze(gameObjectSubjekt, adjektivPhrasen);
            }
        }

        return altEindruckBeiBegegnungSaetze(
                gameObjectSubjekt,
                SPIELER_CHARAKTER,
                Personalpronomen.get(P2,
                        // Wir tun hier so, als wäre der Spieler männlich, aber das
                        // ist egal - die Methode garantiert, dass niemals etwas
                        // wie "du, der du..." oder
                        // "du, die du..." generiert wird.
                        M),
                strongestFeelingTowards);
    }


    /**
     * Gibt eventuell alternative adverbiale Angaben zurück, die beschreiben, welchen Eindruck dieses
     * Feeling Being auf den SC macht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste!
     */
    public ImmutableList<AdverbialeAngabeSkopusVerbAllg> altEindruckAufScBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt) {
        final FeelingTowardsType strongestFeelingTowards =
                getStrongestFeelingTowardsType();

        final int schwelle =
                Math.abs(getFeelingTowards(SPIELER_CHARAKTER, strongestFeelingTowards)) + 1;

        if (schwelle < getMuedigkeit() || schwelle < Math.abs(getMood().getGradDerFreude())) {
            return altAdverbialeAngabenSkopusVerbAllg();
        }

        return altEindruckBeiBegegnungAdvAngaben(
                gameObjectSubjekt, SPIELER_CHARAKTER,
                Personalpronomen.get(P2,
                        // Wir tun hier so, als wäre der Spieler männlich, aber das
                        // ist egal - die Methode garantiert, dass niemals etwas
                        // wie "du, der du..." oder
                        // "du, die du..." generiert wird.
                        M),
                strongestFeelingTowards);
    }

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den Eindruck
     * beschreiben, den dieses Feeling Being auf den SC macht, wenn die beiden sich
     * begegnen. Die Phrasen können mit
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Nicht alle diese Phrasen sind für adverbiale Angaben geeignet, dazu
     * siehe
     * {@link #altEindruckAufScBeiBegegnungAdvAngaben(SubstantivischePhrase)}!
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    @NonNull
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckAufScBeiBegegnungAdjPhr(
            final NumerusGenus gameObjectSubjektNumerusGenus) {
        final FeelingTowardsType strongestFeelingTowards =
                getStrongestFeelingTowardsType();

        final int schwelle =
                Math.abs(getFeelingTowards(SPIELER_CHARAKTER, strongestFeelingTowards)) + 1;

        if (schwelle < getMuedigkeit() || schwelle < Math.abs(getMood().getGradDerFreude())) {
            final ImmutableList<AdjPhrOhneLeerstellen> allgemein = altAdjPhr();
            if (!allgemein.isEmpty()) {
                return allgemein;
            }
        }

        return altEindruckBeiBegegnungAdjPhr(
                gameObjectSubjektNumerusGenus,
                SPIELER_CHARAKTER,
                Personalpronomen.get(P2,
                        // Wir tun hier so, als wäre der Spieler männlich, aber das
                        // ist egal - die Methode garantiert, dass niemals etwas
                        // wie "du, der du..." oder
                        // "du, die du..." generiert wird.
                        M),
                strongestFeelingTowards);
    }

    private FeelingTowardsType getStrongestFeelingTowardsType() {
        return Collections.max(asList(FeelingTowardsType.values()),
                Comparator.comparing(
                        f -> Math.abs(getFeelingTowards(getGameObjectId(), f))));
    }

    /**
     * Gibt alternative Sätze zurück, die die durch diese Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings auf das Target beschreibt, wenn die beiden sich begegnen.
     * Hier werden <i>keine Begrüßungen</i> beschrieben!
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     */
    @NonNull
    private ImmutableList<Satz> altReaktionBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc,
            final FeelingTowardsType type) {
        final boolean targetKnown = isTargetKnown(feelingTargetId);

        return type.altReaktionBeiBegegnungSaetze(
                gameObjectSubjekt, targetDesc,
                getFeelingTowards(feelingTargetId, type),
                targetKnown);
    }

    /**
     * Gibt eventuell alternative Sätze zurück, die beschreiben, wie dieses Feeling Being
     * das Target ansieht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    private ImmutableList<Satz> altBeiBegegnungAnsehenSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc, final FeelingTowardsType type) {
        return type.altBeiBegegnungAnsehenSaetze(
                gameObjectSubjekt, targetDesc,
                getFeelingTowards(feelingTargetId, type),
                isTargetKnown(feelingTargetId));
    }

    /**
     * Gibt eventuell alternative Sätze zurück, die den Eindruck
     * beschreiben, den dieses Feeling Beings auf das Target macht, wenn die beiden sich
     * begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    private ImmutableList<Satz> altEindruckBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc, final FeelingTowardsType type) {
        return type.altEindruckBeiBegegnungSaetze(
                gameObjectSubjekt, targetDesc,
                getFeelingTowards(feelingTargetId, type),
                isTargetKnown(feelingTargetId));
    }

    /**
     * Gibt eventuell adverbiale Angaben zurück, die beschreiben, welchen Eindruck dieses
     * Feeling Being - basiert auf diesem {@link FeelingTowardsType} - auf das  Target
     * macht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste!
     */
    private ImmutableList<AdverbialeAngabeSkopusVerbAllg> altEindruckBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc, final FeelingTowardsType type) {
        return type.altEindruckBeiBegegnungAdvAngaben(
                gameObjectSubjekt, targetDesc,
                getFeelingTowards(feelingTargetId, type),
                isTargetKnown(feelingTargetId));
    }

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den Eindruck
     * beschreiben, den dieses Feeling Beings auf das Target macht, wenn die beiden sich
     * begegnen. Die Phrasen können mit
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Nicht alle diese Phrasen sind für adverbiale Angaben geeignet, dazu
     * siehe
     * {@link #altEindruckBeiBegegnungAdvAngaben(SubstantivischePhrase, GameObjectId, SubstantivischePhrase, FeelingTowardsType)}.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    private ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc,
            final FeelingTowardsType type) {
        return type.altEindruckBeiBegegnungAdjPhr(
                getGameObjectPerson(), gameObjectSubjektNumerusGenus, targetDesc,
                getFeelingTowards(feelingTargetId, type),
                isTargetKnown(feelingTargetId));
    }

    private boolean isTargetKnown(final GameObjectId targetId) {
        return memoryComp != null && memoryComp.isKnown(targetId);
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

        final float oldValue = getFeelingTowardsAsFloat(target, type);

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
    public int getFeelingTowards(final GameObjectId target, final FeelingTowardsType type) {
        return Math.round(getFeelingTowardsAsFloat(target, type));
    }

    @CheckReturnValue
    private float getFeelingTowardsAsFloat(final GameObjectId target,
                                           final FeelingTowardsType type) {
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
        timeTaker.setNow(endTime);

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
                        du(SENTENCE,
                                "solltest", "etwas schlafen")
                                .beendet(PARAGRAPH),
                        du(SENTENCE,
                                "kannst", "gewiss eine Mütze Schlaf gebrauchen!")
                                .beendet(PARAGRAPH),
                        paragraph("Ein Bett!"));
                return;
            case FeelingIntensity.STARK:
                n.narrateAlt(noTime(),
                        du(SENTENCE,
                                "musst", "schlafen")
                                .beendet(PARAGRAPH),
                        du(SENTENCE,
                                "willst", "schlafen")
                                .beendet(PARAGRAPH),
                        neuerSatz(SENTENCE,
                                "es ist Zeit, schlafen zu gehen!")
                                .beendet(PARAGRAPH),
                        neuerSatz(SENTENCE,
                                "Zeit, sich schlafen zu legen.")
                                .beendet(PARAGRAPH)
                );

            case FeelingIntensity.SEHR_STARK:
            case FeelingIntensity.PATHOLOGISCH:
                n.narrateAlt(noTime(),
                        neuerSatz(SENTENCE,
                                "„So legt dich doch endlich schlafen!“, "
                                        + "denkst du bei dir"),
                        neuerSatz(SENTENCE,
                                "vielleicht könntest du hier ungestört schlafen?"),
                        neuerSatz(SENTENCE,
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
                timeTaker.now(),
                scActionStepCountDao.stepCount(),
                getMuedigkeitGemaessBiorhythmus());

        narrateMuedigkeitEvtlGeaendert(muedigkeitBisher);
    }

    private void narrateScWirdMuede() {
        n.narrateAlt(altScWirdMuede(), noTime());
    }

    @CheckReturnValue
    private Collection<AbstractDescription<?>> altScWirdMuede() {
        checkArgument(
                getMuedigkeit() > FeelingIntensity.NEUTRAL,
                "Wird müde, aber FeelingIntensity ist NEUTRAL?"
        );

        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                // FIXME schöner wäre du(prädikat), das dann den Satz
                //  speichern würde (nicht nur für 2. Person SG, sondern generell!)
                .map(p -> du(PARAGRAPH, praedikativumPraedikatWerdenMit(p)).beendet(PARAGRAPH))
                .collect(toList()));

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                .map(p -> du(PARAGRAPH, praedikativumPraedikatMit(p)).beendet(PARAGRAPH))
                .collect(toList()));

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                .map(p -> du(PARAGRAPH,
                        "fühlst", "dich auf einmal "
                                + GermanUtil.joinToString(
                                p.getPraedikativ(P2, SG)),
                        "auf einmal")
                        .komma(Wortfolge.joinToWortfolge(p.getPraedikativ(P2, SG))
                                .kommaStehtAus())
                        .beendet(PARAGRAPH))
                .collect(toList()));

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                .map(p -> du(PARAGRAPH,
                        praedikativumPraedikatMit(p).mitAdverbialerAngabe(
                                new AdverbialeAngabeSkopusSatz("auf einmal")))
                        .beendet(PARAGRAPH))
                .collect(toList()));

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                .map(p -> du(PARAGRAPH, praedikativumPraedikatMit(p).mitAdverbialerAngabe(
                        new AdverbialeAngabeSkopusSatz("mit einem Mal")))
                        .beendet(PARAGRAPH))
                .collect(toList()));

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
                timeTaker.now(), scActionStepCountDao.stepCount(),
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

        resetMuedigkeitshinweisStepCount(scActionStepCount);
    }

    @CheckReturnValue
    private Collection<AbstractDescription<?>> altScIstMuede() {
        checkArgument(
                getMuedigkeit() > FeelingIntensity.NEUTRAL,
                "Ist müde, aber FeelingIntensity ist NEUTRAL?"
        );

        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                .map(p -> du(PARAGRAPH, praedikativumPraedikatMit(p)).beendet(PARAGRAPH))
                .collect(toList()));

        res.addAll(getPcd().getMuedigkeitsData().altAdjektivphrase().stream()
                .map(p -> du(PARAGRAPH,
                        "fühlst", "dich  "
                                + GermanUtil.joinToString(
                                p.getPraedikativ(P2, SG)))
                        .komma(Wortfolge.joinToWortfolge(p.getPraedikativ(P2, SG))
                                .kommaStehtAus())
                        .beendet(PARAGRAPH))
                .collect(toList()));

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

            if (timeTaker.now().getTageszeit() == Tageszeit.NACHTS) {
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
        return muedigkeitsBiorythmus.get(timeTaker.now().getTime());
    }
}
