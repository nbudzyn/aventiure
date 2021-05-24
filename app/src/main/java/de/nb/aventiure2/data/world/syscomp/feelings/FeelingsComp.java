package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingComp;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.Arrays.asList;

/**
 * Component for a {@link GameObject}: The game object
 * has needs and feelings.
 */
public class FeelingsComp extends AbstractStatefulComponent<FeelingsPCD> {
    private final TimeTaker timeTaker;

    private final SCActionStepCountDao scActionStepCountDao;

    private final Narrator n;

    private final World world;
    @Nullable
    private final WaitingComp waitingComp;
    @Nullable
    private final MemoryComp memoryComp;

    @Nullable
    private final LocationComp locationComp;
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
                        final World world,
                        @Nullable final WaitingComp waitingComp,
                        @Nullable final MemoryComp memoryComp,
                        @Nullable final LocationComp locationComp,
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
        this.world = world;
        this.waitingComp = waitingComp;
        this.memoryComp = memoryComp;
        this.locationComp = locationComp;
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

    public ImmutableList<AdvAngabeSkopusSatz> altAdvAngabenSkopusSatz() {
        if (getMuedigkeit() > Math.abs(getMood().getGradDerFreude())) {
            // Häufig wird die adverbiale Angabe wohl verwendet werden - daher setzen
            // wir den Counter neu.
            resetMuedigkeitshinweisStepCount();
            return requirePcd().getMuedigkeitsData().altAdvAngabenSkopusSatz();
        }

        return getMood().altAdvAngabenSkopusSatz();
    }

    private ImmutableList<AdvAngabeSkopusVerbAllg> altAdvAngabenSkopusVerbAllg() {
        if (getMuedigkeit() > Math.abs(getMood().getGradDerFreude())) {
            // Häufig wird die adverbiale Angabe wohl verwendet werden - daher setzen
            // wir den Counter neu.
            resetMuedigkeitshinweisStepCount();
            return requirePcd().getMuedigkeitsData().altAdvAngabenSkopusVerbAllg();
        }

        return getMood().altAdvAngabenSkopusVerbAllg();
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
            return altMuedigkeitAdjPhr();
        }

        return getMood().altAdjPhr();
    }

    public ImmutableList<AdjPhrOhneLeerstellen> altMuedigkeitAdjPhr() {
        return requirePcd().getMuedigkeitsData().altAdjektivphrase();
    }

    private void resetMuedigkeitshinweisStepCount() {
        resetMuedigkeitshinweisStepCount(scActionStepCountDao.stepCount());
    }

    private void resetMuedigkeitshinweisStepCount(final int scActionStepCount) {
        requirePcd().resetNextMuedigkeitshinweisActionStepCount(scActionStepCount);
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
        return requirePcd().getMood();
    }

    public void requestMoodMin(final Mood mood) {
        requirePcd().requestMoodMin(mood);
    }

    public void requestMoodMax(final Mood mood) {
        requirePcd().requestMoodMax(mood);
    }

    public void requestMood(final Mood mood) {
        requirePcd().requestMood(mood);
    }

    public Hunger getHunger() {
        return requirePcd().getHunger();
    }

    /**
     * Gibt zurück, wie stark man durch die Müdigkeit im Gehen verlangsamt wird.
     */
    public double getMovementSpeedFactor() {
        return requirePcd().getMovementSpeedFactor();
    }

    // IDEA Man könnte also die Möglichkeit anbieten, jederzeit den Status eines bestimmten
    //  Game Objects unter einem "Label" zu persistieren (inkl. Zeitpunkt), so dass
    //  man ihn später wieder laden kann. Alternativ auch mehrere Game Objects,
    //  denn nur so kann man prüfen, was sich nach dem Schlafen an einem Ort verändert hat.

    // IDEA Frosch läuft während des Schlafs weg. Oder kommt ggf. Auch wieder. Oder läuft
    //  weg und kommt wieder.
    //  Es sollte in der Zeit keine narrations geben (der Spieler bekommt ja nichts mit, es sei
    //  denn man lässt ihn dann aufwachen...). Nach dem (regulären) Aufwachen sollte etwas
    //  kommen wie... ist verschwunden.
    //  - Für die Tageszeit haben wir ein gutes Konzept.
    //  - Für das Bewegen (Frosch weg, Kugel weg) funktioniert es gut über die AssumedLocations.
    //  - Für andere Statusänderungen scheint es nicht zu funktionieren? Wenn
    //    man die Zeit mittendrin weiterlaufen lässt, funktioniert das mit den Tageszeiten
    //    nicht mehr!
    // IDEA Wenn man schläft, "verpasst" man Reactions, die man dann später
    //  (beim Aufwachen) merkt ("Der Frosch ist verschwunden".) Man speichert
    //  am besten den Stand VOR dem Einschlafen und vergleicht mit dem Stand NACH dem
    //  Einschlafen. Vielleicht beim Aufwachen dasselbe Konzept wie beim Bewegen
    //  verwenden! (Setzt voraus, dass der SC Änderungen während des Schlafens
    //  weder erzählt bekommt noch sie in den assumedLocations registriert werden.)
    //  Man könnte sagen: Schlafen ist wie Bewegen: Es gibt eine neue Beschreibung der
    //  äußeren Umstände, zumindest soweit sie sich verändert haben. Dazu muss der Unterschied
    //  (vorher / nachher) ermittelt werden. Und die Zeit muss zwischendrin vergehen -
    //  allerdings ohne narration. (Ein Vergehen der Zeit - bisher mit Narration - passiert
    //  bei der WartenAction.)
    //  Andere Idee könnte sein: Beim Vergehen von Zeit gibt es DREI Parameter:
    //  letzter Zeitpunkt, letzter WACHER Zeitpunkt und aktueller Zeitpunkt
    //  Entsprechend kann dann der Text gestaltet werden, z.B. "Der Gesang hat aufgehört."

    // IDEA Konzept entwickeln, dass diese "Statusübergänge" realisiert:
    //  - Benutzer schläft ein, während Rapunzel singt, aufhört und wieder anfängt
    //  - Benutzer schläft ein, während Rapunzel singt und wacht auf und Rapunzel hat
    //    zwischenzeitlich aufgehört zu singen

    // IDEA Idee: Jede Reaktion speichert den letzten Zustand (PCD), auf Basis dessen sie einen
    //  Text gerendert hat sowie den Zeitpunkt dazu. Wenn wieder Gelegenheit ist, ein Text zu
    //  rendern, wird geprüft, ob sich der Status gegenüber dem Zeitpunkt geändert hat,
    //  außerdem wird geprüft, ob der Zeitpunkt Benutzer etwas versäumt hat oder die ganze
    //  Zeit anwesend und aufnahmefähig war - entsprechend etwas wie "Plötzlich endet der
    //  Gesang"
    //  oder "Es ist kein Gesang mehr zu hören" gerendert.

    // IDEA Zum Beispiel wäre der Benutzer über alle Statusänderungen zu unterrichten,
    //  Die zwischenzeitlich passiert sind ("der Frosch ist verschwunden").

    // IDEA Der Benutzer (oder auch andere Game Objects) könnte auch über
    //  die Assumed Locations hinaus ein Mental Model haben, wo
    //  der Stand der Welt, wie der Benutzer ihn sich vorstellt, gespeichert ist
    //  (z.B. die Welt, oder der Raum bevor der Benutzer eingeschlafen ist...)
    //  Dann könnte man beim Erzählen (z.B. beim Aufwachen) vergleichen...

    // IDEA Der Frosch läuft während des Schlafens davon - nicht beim Aufwachen.
    //  Alternativ könnte der Spieler durch das Weglaufen aufgeweckt werden
    //  (so ähnlich, wie das Warten unterbrochen wird).

    // IDEA Konzept dafür entwickeln, dass der Benutzer einen  Ort verlässt, während XYZ
    //  passiert und zurückkehrt, wenn XYZ nicht mehr passiert

    // IDEA Konzept entwickeln, dass diese "Statusübergänge" realisiert:
    //  - Benutzer schläft an einem Ort, Rapunzel beginnt dort zu singen und hört wieder auf
    //     (Benutzer merkt nichts)
    //  - Benutzer schläft ein, während Rapunzel nicht singt und wacht auf und Rapunzel hat
    //    zwischenzeitlich angefangen zu singen

    public AvTimeSpan calcSchlafdauerMensch() {
        final AvDateTime now = timeTaker.now();

        if (getMuedigkeit() < FeelingIntensity.DEUTLICH) {
            return mins(59);
        }

        if (now.getTime().isBefore(oClock(16, 30))) {
            return hours(8);
        } else {
            return now.timeSpanUntil(oClock(7));
        }
    }

    /**
     * Der SC wacht nach einem Schlaf wieder auf.
     *
     * @param schlafdauer       Wie lange der SC geschlafen hat
     * @param wollteEinschlafen Ob der SC einschlafen wolle ({@code true}) oder er nur
     *                          versehentlich weggenickt ist ({@code false}).
     */
    public void narrateAndDoAufwachenSC(
            final AvTimeSpan schlafdauer, final boolean wollteEinschlafen) {
        menschAusgeschlafen(schlafdauer);
        requestMoodMin(NEUTRAL);
        requestMoodMax(BEWEGT);

        final AltDescriptionsBuilder alt = alt();

        if (schlafdauer.longerThanOrEqual(hours(7))) {
            alt.add(du(CHAPTER, "wachst",
                    "nach einem langen Schlaf gut erholt wieder auf")
                            .mitVorfeldSatzglied("nach einem langen Schlaf")
                            .schonLaenger()
                    ,
                    du(CHAPTER, "schläfst", "deine Müdigkeit aus; erst nach langem",
                            "Schlaf erwachst du wieder").schonLaenger()
            );
        }

        if (schlafdauer.longerThanOrEqual(hours(4))) {
            alt.add(du(CHAPTER, "schläfst",
                    "tief und fest und wachst erst nach einigen Stunden wieder auf")
                    .schonLaenger()
                    .mitVorfeldSatzglied("tief"));
        }

        if (schlafdauer.isBetween(hours(3), hours(6))) {
            alt.add(neuerSatz(CHAPTER,
                    "Als du die Augen wieder aufschlägst, sind einige Stunden vergangen"));
        }

        if (schlafdauer.isBetween(mins(45), mins(7))) {
            alt.add(du(CHAPTER,
                    "schläfst", "vielleicht eine Stunde und wachst "
                            + "gekräftigt wieder auf"));
        }

        if (schlafdauer.shorterThan(hours(1))) {
            alt.add(neuerSatz(CHAPTER,
                    "Keine Stunde und du erwachst wieder"),
                    du(CHAPTER, "bist",
                            "nach einem kurzen Nickerchen wieder wach")
                            .mitVorfeldSatzglied("nach einem kurzen Nickerchen")
                            .schonLaenger(),
                    du(CHAPTER, "bist", "nach knapp einer Stunde wieder wach")
                            .mitVorfeldSatzglied("nach knapp einer Stunde").schonLaenger()
            );
        }

        if (schlafdauer.shorterThanOrEqual(mins(20))) {
            if (wollteEinschlafen) {
                alt.add(neuerSatz(CHAPTER,
                        "Als du wieder aufwachst, hast du",
                        ImmutableList.of("den Eindruck", "das Gefühl"),
                        ", dich gerade erst hingelegt zu haben"));
            } else {
                alt.add(neuerSatz(CHAPTER,
                        "Keine halbe Stunde später schreckst du wieder auf"));
                if (locationComp != null && !locationComp.isNiedrig()) {
                    alt.add(neuerSatz(CHAPTER,
                            "Keine halbe Stunde später schreckst du wieder hoch"));
                }
            }
        }

        n.narrateAlt(alt, NO_TIME);

        if (schlafdauer.longerThan(hours(5))) {
            world.resetSchonBegruesstMitSC();
        }
    }

    private void menschAusgeschlafen(final AvTimeSpan schlafdauer) {
        ausgeschlafen(MuedigkeitsData.calcAusschlafenEffektHaeltBeimMenschenVorFuer(schlafdauer));
    }

    public void ausgeschlafen(final AvTimeSpan ausschlafenEffektHaeltVorFuer) {
        requirePcd().ausgeschlafen(timeTaker.now(),
                scActionStepCountDao.stepCount(),
                ausschlafenEffektHaeltVorFuer,
                getMuedigkeitGemaessBiorhythmus());
    }

    /**
     * Gibt die Müdigkeit als positiver {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public int getMuedigkeit() {
        return requirePcd().getMuedigkeit();
    }

    private void updateHunger() {
        requirePcd().updateHunger(timeTaker.now());
    }

    /**
     * Speichert, dass das {@link IFeelingBeingGO} satt ist. Hat ggf. auch Auswirkungen
     * auf Laune und Müdigkeit.
     */
    public void saveSatt() {
        requirePcd().saveSatt(timeTaker.now(), zeitspanneNachEssenBisWiederHungrig,
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
        return dispatchFeelings(
                SPIELER_CHARAKTER,
                1,
                () -> {
                    final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr = altAdjPhr();
                    final ImmutableList<AdvAngabeSkopusVerbAllg> advAngaben =
                            altAdvAngabenSkopusVerbAllg();

                    return FeelingsSaetzeUtil.toReaktionSaetze(
                            gameObjectSubjekt, duSc(),
                            true, altAdjPhr,
                            advAngaben);
                },
                (feelingTowardsType) -> altReaktionBeiBegegnungMitScSaetze(
                        gameObjectSubjekt, feelingTowardsType));
    }

    /**
     * Gibt alternative Sätze zurück, die die gefühlsmäßige Reaktion dieses Feeling Beings
     * beschreiben, wenn der SC gehen möchte.
     * <p>
     * Die Methode geht davon aus, dass Subjekt und Feeling Target einander sehen.
     */
    @NonNull
    public ImmutableList<Satz> altReaktionWennSCGehenMoechteSaetze(
            final SubstantivischePhrase gameObjectSubjekt) {
        return dispatchFeelings(
                SPIELER_CHARAKTER,
                3,
                () -> {
                    final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr = altAdjPhr();
                    final ImmutableList<AdvAngabeSkopusVerbAllg> advAngaben =
                            altAdvAngabenSkopusVerbAllg();

                    return FeelingsSaetzeUtil.toReaktionSaetze(
                            gameObjectSubjekt, duSc(),
                            true, altAdjPhr,
                            advAngaben);
                },
                (feelingTowardsType) -> altReaktionWennSCGehenMoechteSaetze(
                        gameObjectSubjekt, feelingTowardsType));
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
                duSc(),
                type);
    }

    /**
     * Gibt alternative Sätze zurück, die die durch dieses Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings beschreiben, wenn der SC gehen möchte.
     * <p>
     * Die Methode geht davon aus, dass Subjekt und Feeling Target einander sehen.
     */
    @NonNull
    private ImmutableList<Satz> altReaktionWennSCGehenMoechteSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final FeelingTowardsType type) {
        return altReaktionWennTargetGehenMoechteSaetze(
                gameObjectSubjekt,
                SPIELER_CHARAKTER,
                duSc(),
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
        return dispatchFeelings(
                SPIELER_CHARAKTER,
                1,
                () -> FeelingsSaetzeUtil.altAnsehenSaetze(
                        gameObjectSubjekt,
                        duSc(),
                        altAdvAngabenSkopusVerbAllg()),
                (feelingTowardsType) -> FeelingsSaetzeUtil.altAnsehenSaetze(gameObjectSubjekt,
                        duSc(),
                        feelingTowardsType.altEindruckBeiBegegnungAdvAngaben(
                                gameObjectSubjekt, duSc(),
                                getFeelingTowards(SPIELER_CHARAKTER, feelingTowardsType),
                                isTargetKnown(SPIELER_CHARAKTER)))
        );
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
            final SubstantivischePhrase gameObjectSubjekt,
            final boolean scKannGameObjectSubjektSehen) {
        return dispatchFeelings(
                SPIELER_CHARAKTER,
                1,
                () -> {
                    final ImmutableList<AdjPhrOhneLeerstellen> adjektivPhrasen = altAdjPhr();
                    return FeelingsSaetzeUtil
                            .altEindrueckSaetze(gameObjectSubjekt, scKannGameObjectSubjektSehen,
                                    adjektivPhrasen);
                },
                (feelingTowardsType) -> altEindruckBeiBegegnungSaetze(
                        gameObjectSubjekt,
                        SPIELER_CHARAKTER,
                        scKannGameObjectSubjektSehen, duSc(),
                        feelingTowardsType));
    }


    /**
     * Gibt eventuell alternative adverbiale Angaben zurück, die beschreiben, welchen Eindruck
     * dieses
     * Feeling Being auf den SC macht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste!
     */
    public ImmutableList<AdvAngabeSkopusVerbAllg> altEindruckAufScBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt) {
        return dispatchFeelings(
                SPIELER_CHARAKTER,
                1,
                this::altAdvAngabenSkopusVerbAllg,
                (feelingTowardsType) -> altEindruckBeiBegegnungAdvAngaben(
                        gameObjectSubjekt, SPIELER_CHARAKTER,
                        duSc(),
                        feelingTowardsType));
    }

    /**
     * Entscheidet, welche Art von Gefühlen zurückgegeben wird: allgemeinge Gefühle
     * oder spezielle Gefühle gegenüber diesem Feelings Target. Beim Vergleich wird
     * das Offset mit einbezogen: Je höher das Offet, desto mehr treten allgemeine
     * Gefühle in den Hintergrund.
     *
     * @return Möglicherweise eine leere Collection - je nach übergebenen Werten!
     */
    @SuppressWarnings("SameParameterValue")
    @NonNull
    private <C extends Collection<?>> C dispatchFeelings(
            final GameObjectId feelingTargetId,
            final int offset,
            final Supplier<C> allgemeinFeelingSupplier,
            final Function<FeelingTowardsType, C> feelingTowardsFeelingRetriever) {
        final FeelingTowardsType strongestFeelingTowards =
                getStrongestFeelingTowardsType(feelingTargetId);

        final int schwelle =
                Math.abs(getFeelingTowards(feelingTargetId, strongestFeelingTowards)) + offset;

        if (schwelle < getMuedigkeit() || schwelle < Math.abs(getMood().getGradDerFreude())) {
            final C allgemein = allgemeinFeelingSupplier.get();
            if (!allgemein.isEmpty()) {
                return allgemein;
            }
        }

        return feelingTowardsFeelingRetriever.apply(strongestFeelingTowards);
    }

    private FeelingTowardsType getStrongestFeelingTowardsType(final GameObjectId feelingTargetId) {
        return Collections.max(asList(FeelingTowardsType.values()),
                Comparator.comparing(
                        f -> Math.abs(getFeelingTowards(feelingTargetId, f))));
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
                getFeelingTowardsForActionsMitEmpathischerSchranke(feelingTargetId, type),
                targetKnown);
    }

    /**
     * Gibt alternative Sätze zurück, die die durch diese Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings beschreiben, wenn das Target gehen möchte.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     * <p>
     * Die Methode geht davon aus, dass Subjekt und Feeling Target einander sehen.
     */
    @NonNull
    private ImmutableList<Satz> altReaktionWennTargetGehenMoechteSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc,
            final FeelingTowardsType type) {
        final boolean targetKnown = isTargetKnown(feelingTargetId);

        return type.altReaktionWennTargetGehenMoechteSaetze(
                gameObjectSubjekt, targetDesc,
                getFeelingTowardsForActionsMitEmpathischerSchranke(feelingTargetId, type),
                targetKnown);
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
            final boolean targetKannSubjektSehen,
            final SubstantivischePhrase targetDesc, final FeelingTowardsType type) {
        return type.altEindruckBeiBegegnungSaetze(
                gameObjectSubjekt, targetDesc,
                targetKannSubjektSehen, getFeelingTowards(feelingTargetId, type),
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
    private ImmutableList<AdvAngabeSkopusVerbAllg> altEindruckBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt,
            final GameObjectId feelingTargetId,
            final SubstantivischePhrase targetDesc, final FeelingTowardsType type) {
        return type.altEindruckBeiBegegnungAdvAngaben(
                gameObjectSubjekt, targetDesc,
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
     * <li>Beispiel 4: Die Oma kann den Schüler absolut nicht ausstehen.. Der Schüler hilft der
     * Oma über die
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

        requirePcd().setFeelingTowards(target, type, newValue);
    }

    /**
     * Setzt alle Gefühle zu diesem <code>target</code> auf den Default-Wert zurück.
     * <p>
     * Diese Methode wird man sehr selten brauchen - etwa, wenn jemand verzaubert wird
     * und alle Gefühle zu jemand anderem vergisst.
     */
    public void resetFeelingsTowards(final GameObjectId target) {
        requirePcd().removeFeelingsTowards(target);
    }

    /**
     * Gibt den Feeling-Towards-Wert zurück - allerdings möglicherweise beschränkt,
     * wenn das Target deutlich negativere Gefühle hat. (Liebesschwüre an jemanden, der
     * einem egal ist, sind creepy und sollen vermieden werden.)
     */
    @CheckReturnValue
    public int getFeelingTowardsForActionsMitEmpathischerSchranke(final GameObjectId targetId,
                                                                  final FeelingTowardsType type) {
        final GameObject target = world.load(targetId);
        final int upperBound;
        if (!(target instanceof IFeelingBeingGO)) {
            upperBound = Integer.MAX_VALUE;
        } else {
            upperBound = ((IFeelingBeingGO) target).feelingsComp()
                    .getFeelingTowards(getGameObjectId(), type) + 2;
        }

        return Integer.min(getFeelingTowards(targetId, type), upperBound);
    }

    @CheckReturnValue
    public int getFeelingTowards(final GameObjectId targetId, final FeelingTowardsType type) {
        return Math.round(getFeelingTowardsAsFloat(targetId, type));
    }

    @CheckReturnValue
    private float getFeelingTowardsAsFloat(final GameObjectId targetId,
                                           final FeelingTowardsType type) {
        final Float res = requirePcd().getFeelingTowards(targetId, type);

        if (res != null) {
            return res;
        }

        return defaultFeelingsTowards.get(type);
    }

    /**
     * Diese Methode muss für jedes Feeling Being aufgerufen werden, wenn Zeit vergeht!
     */
    public void onTimePassed(final Change<AvDateTime> change) {
        timeTaker.setNow(change.getNachher());

        narrateAndUpdateHunger();
        narrateAndUpdateMuedigkeit();
    }

    private void narrateAndUpdateHunger() {
        final Hunger hungerBisher = getHunger();

        updateHunger();

        if (getGameObjectId() == SPIELER_CHARAKTER &&
                hungerBisher == SATT && getHunger() != SATT) {
            narrateAndDoScWirdHungrig();
        }
    }

    private void narrateAndDoScWirdHungrig() {
        n.narrateAlt(NO_TIME,
                du(PARAGRAPH, "fühlst", "dich allmählich etwas hungrig")
                        .schonLaenger()
                        .undWartest(),
                neuerSatz("Wann hast du eigentlich zuletzt etwas gegessen? Das "
                        + "muss schon eine Weile her sein."),
                du(PARAGRAPH, "bekommst", "so langsam Hunger")
                        .schonLaenger()
                        .mitVorfeldSatzglied("so langsam"),
                neuerSatz(PARAGRAPH, "Allmählich überkommt dich der Hunger"),
                neuerSatz(PARAGRAPH, "Allmählich regt sich wieder der Hunger")
                        .undWartest(),
                neuerSatz("Dir fällt auf, dass du Hunger hast")
                        .komma(),
                du(SENTENCE, "empfindest", "wieder leichten Hunger")
                        .schonLaenger()
                        .undWartest()
        );

        if (waitingComp != null) {
            waitingComp.stopWaiting();
        }
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
                // fall-through
            case FeelingIntensity.DEUTLICH:
                n.narrateAlt(NO_TIME,
                        du(SENTENCE,
                                "solltest", "etwas schlafen", PARAGRAPH)
                                .schonLaenger()
                        ,
                        du(SENTENCE,
                                "kannst", "gewiss eine Mütze Schlaf",
                                "gebrauchen!", PARAGRAPH).schonLaenger()
                        ,
                        paragraph("Ein Bett!"));
                return;
            case FeelingIntensity.STARK:
                n.narrateAlt(NO_TIME,
                        du(SENTENCE,
                                "musst", "schlafen", PARAGRAPH).schonLaenger()
                        ,
                        du(SENTENCE,
                                "willst", "schlafen", PARAGRAPH).schonLaenger()
                        ,
                        neuerSatz(SENTENCE,
                                "es ist Zeit, schlafen zu gehen!", PARAGRAPH),
                        neuerSatz(SENTENCE,
                                "Zeit, sich schlafen zu legen.", PARAGRAPH)
                );
                return;
            case FeelingIntensity.SEHR_STARK:
                // fall-through
            case FeelingIntensity.PATHOLOGISCH:
                n.narrateAlt(NO_TIME,
                        neuerSatz(SENTENCE,
                                "„So legt dich doch endlich schlafen!“, "
                                        + "denkst du bei dir"),
                        neuerSatz(SENTENCE,
                                "vielleicht könntest du hier ungestört schlafen?"),
                        neuerSatz(SENTENCE,
                                "Höchste Zeit, schlafen zu gehen!", PARAGRAPH)
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Müdigkeit: " + muedigkeit);
        }
    }

    private void narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
        n.narrateAlt(NO_TIME,
                neuerSatz("Mmh!", PARAGRAPH),
                neuerSatz("Dir läuft das Wasser im Munde zusammen"),
                du(SENTENCE, "hast", "Hunger")
                        .schonLaenger()
                        .undWartest(),
                du(SENTENCE, "bist", "hungrig")
                        .schonLaenger()
                        .undWartest(),
                neuerSatz("Dir fällt auf, wie hungrig du bist")
                        .komma()
        );
    }

    private void narrateAndUpdateMuedigkeit() {
        final int muedigkeitBisher = getMuedigkeit();

        requirePcd().updateMuedigkeit(
                timeTaker.now(),
                scActionStepCountDao.stepCount(),
                getMuedigkeitGemaessBiorhythmus());

        narrateAndDoMuedigkeitEvtlErhoeht(muedigkeitBisher);
    }

    private void narrateScWirdMuede() {
        n.narrateAlt(altScWirdMuede(), NO_TIME);
    }

    @CheckReturnValue
    private Collection<AbstractDescription<?>> altScWirdMuede() {
        checkArgument(
                getMuedigkeit() > FeelingIntensity.NEUTRAL,
                "Wird müde, aber FeelingIntensity ist NEUTRAL?"
        );

        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        res.addAll(mapToList(altMuedigkeitAdjPhr(),
                p -> du(PARAGRAPH, p.alsWerdenPraedikativumPraedikat(), PARAGRAPH)));

        res.addAll(mapToList(altMuedigkeitAdjPhr(),
                p -> du(PARAGRAPH, p.alsPraedikativumPraedikat(), PARAGRAPH)));

        res.addAll(mapToList(altMuedigkeitAdjPhr(), p -> du(PARAGRAPH, "fühlst",
                "dich auf einmal", p.getPraedikativ(World.duSc()), PARAGRAPH)
                .mitVorfeldSatzglied("auf einmal")));

        res.addAll(
                mapToList(altMuedigkeitAdjPhr(), p -> du(PARAGRAPH,
                        p.alsPraedikativumPraedikat().mitAdvAngabe(
                                new AdvAngabeSkopusSatz("auf einmal")),
                        PARAGRAPH)));

        res.addAll(mapToList(altMuedigkeitAdjPhr(),
                p -> du(PARAGRAPH, p.alsPraedikativumPraedikat().mitAdvAngabe(
                        new AdvAngabeSkopusSatz("mit einem Mal")),
                        PARAGRAPH)));

        if (getMuedigkeit() == FeelingIntensity.NUR_LEICHT) {
            // NUR_LEICHT: "leicht erschöpft"

            res.add(
                    du(PARAGRAPH, "fühlst", "dich ein wenig erschöpft",
                            PARAGRAPH).schonLaenger()
                    ,
                    du(PARAGRAPH, "spürst", "die Anstrengung", PARAGRAPH)
                            .schonLaenger()
                    ,
                    du("wirst", "etwas schläfrig", PARAGRAPH).schonLaenger()
                    ,
                    du(SENTENCE, "bist", "darüber etwas schläfrig geworden")
                            .mitVorfeldSatzglied("darüber")
                            .schonLaenger()
            );
        }

        if (getMuedigkeit() == FeelingIntensity.MERKLICH) {
            // MERKLICH: "erschöpft"
            res.add(
                    // Kann z.B. mit dem Vorsatz kombiniert werden zu etwas wie
                    // "Unten angekommen bist du ziemlich erschöpft..."
                    du("bist", "ziemlich erschöpft; ein Nickerchen täte dir "
                            + "gut", PARAGRAPH).schonLaenger()
                    ,
                    du(SENTENCE, "bist", "ziemlich erschöpft. Und müde",
                            PARAGRAPH).schonLaenger()
                    ,
                    neuerSatz("Das war alles anstrengend!", PARAGRAPH)
            );
        }

        if (getMuedigkeit() == FeelingIntensity.DEUTLICH) {
            //  DEUTLICH: "müde"
            res.add(
                    du("beginnst", "müde zu werden").schonLaenger()
                    ,
                    du(PARAGRAPH, "bist", "jetzt müde")
                            .mitVorfeldSatzglied("jetzt"),
                    du(PARAGRAPH, "bist", "indessen müde geworden")
                            .mitVorfeldSatzglied("indessen")
                            .schonLaenger()
                    ,
                    neuerSatz("da wollen dir deine Augen nicht länger offen bleiben "
                            + "und du bekommst Lust zu schlafen")
            );
        }

        if (getMuedigkeit() == FeelingIntensity.STARK) {
            //  STARK: "völlig übermüdet"
            res.add(du(SENTENCE, "bist", "ganz müde").schonLaenger()
            );
        }

        if (getMuedigkeit() == FeelingIntensity.SEHR_STARK) {
            // SEHR_STARK: "todmüde"
            res.add(du(SENTENCE, "bist", "hundemüde", PARAGRAPH).schonLaenger()
                    ,
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

        requirePcd().upgradeTemporaereMinimalmuedigkeit(
                timeTaker.now(), scActionStepCountDao.stepCount(),
                temporaereMinimalmuedigkeit, duration,
                getMuedigkeitGemaessBiorhythmus());

        narrateAndDoMuedigkeitEvtlErhoeht(muedigkeitBisher);
    }

    private void narrateAndDoMuedigkeitEvtlErhoeht(final int muedigkeitBisher) {
        if (getGameObjectId() == SPIELER_CHARAKTER && muedigkeitBisher < getMuedigkeit()) {
            narrateScWirdMuede();

            if (waitingComp != null) {
                waitingComp.stopWaiting();
            }
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
        if (!requirePcd().muedigkeitshinweisNoetig(scActionStepCount)) {
            return;
        }

        n.narrateAlt(altScIstMuede(), NO_TIME);

        resetMuedigkeitshinweisStepCount(scActionStepCount);
    }

    @CheckReturnValue
    private Collection<AbstractDescription<?>> altScIstMuede() {
        checkArgument(
                getMuedigkeit() > FeelingIntensity.NEUTRAL,
                "Ist müde, aber FeelingIntensity ist NEUTRAL?"
        );

        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        res.addAll(
                mapToList(altMuedigkeitAdjPhr(),
                        p -> du(PARAGRAPH, p.alsPraedikativumPraedikat(), PARAGRAPH)
                                .schonLaenger()));

        res.addAll(mapToList(altMuedigkeitAdjPhr(), p -> du(PARAGRAPH, "fühlst",
                "dich", p.getPraedikativ(World.duSc()), PARAGRAPH)
                .schonLaenger()));

        if (getMuedigkeit() == FeelingIntensity.NUR_LEICHT) {
            // NUR_LEICHT: "leicht erschöpft"

            res.add(
                    du(PARAGRAPH, "fühlst", "dich ein wenig erschöpft",
                            PARAGRAPH).schonLaenger()
                    ,
                    du(PARAGRAPH, "bist", "etwas erschöpft", PARAGRAPH)
                            .schonLaenger()
            );
        }

        if (getMuedigkeit() == FeelingIntensity.MERKLICH) {
            // MERKLICH: "erschöpft"
            res.add(
                    // Kann z.B. mit dem Vorsatz kombiniert werden zu etwas wie
                    // "Unten angekommen bist du ziemlich erschöpft..."
                    du(PARAGRAPH, "bist", "ziemlich erschöpft; ein "
                            + "Nickerchen täte dir gut", PARAGRAPH).schonLaenger()
                    ,
                    du(PARAGRAPH, "solltest", "etwas ruhen", PARAGRAPH)
                            .schonLaenger()
                    ,
                    du(SENTENCE, "möchtest", "ein wenig ruhen", PARAGRAPH)
                            .schonLaenger()

            );
        }

        if (getMuedigkeit() == FeelingIntensity.DEUTLICH) {
            //  DEUTLICH: "müde"
            res.add(
                    du(PARAGRAPH, "musst", "ein wenig schlafen")
                            .schonLaenger()
                    ,
                    du(PARAGRAPH, "würdest", "gern ein wenig schlafen")
                            .schonLaenger()
                    ,
                    du(PARAGRAPH, "möchtest", "dich schlafen legen")
                            .schonLaenger()
                    ,
                    du(PARAGRAPH, "bist", "müde und möchtest gern schlafen",
                            PARAGRAPH).schonLaenger()
                    ,
                    du(PARAGRAPH, "bist", "müde – wo ist ein Bett, in dass du dich "
                            + "legen und schlafen kannst?", PARAGRAPH).schonLaenger()
                    ,
                    du(PARAGRAPH, "bist", "matt und müde").schonLaenger()

                            .mitVorfeldSatzglied("matt"),
                    neuerSatz(PARAGRAPH, "all die Erlebnisse haben dich müde gemacht"),
                    du(PARAGRAPH, "möchtest", "gern ein Auge zutun", PARAGRAPH)
                            .mitVorfeldSatzglied("gern").schonLaenger()

            );

            if (timeTaker.now().getTageszeit() == Tageszeit.NACHTS) {
                res.add(neuerSatz("es ist Schlafenszeit"));
            }
        }

        if (getMuedigkeit() == FeelingIntensity.STARK) {
            //  STARK: "völlig übermüdet"

            res.add(
                    du(SENTENCE, "kannst", "kaum mehr die Augen offenhalten")
                            .schonLaenger()
                    ,
                    du(PARAGRAPH, "musst", "endlich wieder einmal ausschlafen!")
                            .schonLaenger()
                    ,
                    paragraph("wenn du dich doch schlafen legen könntest!"),
                    paragraph("könntest du dich doch in ein Bett legen!"),
                    du(SENTENCE, "bist",
                            "so müde, du kannst kaum mehr weiter")
                            .schonLaenger()

            );
        }

        if (getMuedigkeit() == FeelingIntensity.SEHR_STARK) {
            //  SEHR_STARK: "todmüde"
            res.add(
                    du(PARAGRAPH, "bist",
                            "so müde, dass du auf der Stelle einschlafen könntest",
                            PARAGRAPH)
                            .komma()
                            .schonLaenger()
                    ,
                    du(PARAGRAPH, "bist",
                            "so müde von allem, dass du auf der Stelle einschlafen",
                            "könntest", PARAGRAPH)
                            .mitVorfeldSatzglied("von allem")
                            .schonLaenger()
                            .komma(),
                    neuerSatz("immer wieder fallen dir die Augen zu"),
                    du(SENTENCE, "bist", "so müde, dass du die",
                            "Augen kaum aufhalten magst").schonLaenger()
                    ,
                    neuerSatz(SENTENCE, "dir ist, als könntest du vor Müdigkeit kaum mehr "
                            + "ein Glied regen"),
                    du("kannst", "dich des Schlafes kaum wehren", PARAGRAPH)
                            .schonLaenger()
            );
        }

        if (getMuedigkeit() == FeelingIntensity.PATHOLOGISCH) {
            //  PATHOLOGISCH: "benommen"
            res.add(
                    du(SENTENCE, "empfindest",
                            "so große Müdigkeit, dass dich deine Glieder "
                                    + "kaum halten")
                            .schonLaenger()
                            .komma(),
                    du("kannst", "dich des Schlafes kaum wehren", PARAGRAPH)
                            .schonLaenger()
                    ,
                    du(PARAGRAPH, "fühlst", "dich wie eingeschläfert")
                            .schonLaenger()
            );
        }

        return res.build();
    }

    private int getMuedigkeitGemaessBiorhythmus() {
        return muedigkeitsBiorythmus.get(timeTaker.now().getTime());
    }
}
