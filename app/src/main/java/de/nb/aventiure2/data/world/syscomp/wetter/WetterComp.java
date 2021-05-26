package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRINNEN;

/**
 * Wetter
 */
public class WetterComp extends AbstractStatefulComponent<WetterPCD> {
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    public WetterComp(final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
                      final World world) {
        super(WETTER, db.wetterDao());
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    @Override
    protected WetterPCD createInitialState() {
        final WetterData wetterData = WetterData.getDefault();
        return new WetterPCD(WETTER, wetterData,
                // Der Spieler wird implizit davon ausgehen, dass es wohl tagsüber ist
                timeTaker.now());
    }

    /**
     * Setzt das Planwetter.
     */
    public void setPlanwetter(@Nullable final WetterData planwetter) {
        setPlanwetter(planwetter, false);
    }

    /**
     * Setzt das Planwetter.
     *
     * @param firstStepTakesNoTime Bei <code>false</code> wird der erste Schritt
     *                             (Wetterwechsel) ganz normal ausgeführt - bei <code>true</code>
     *                             wird der erste Schritt in 0 Sekunden ausgeführt - die
     *                             Wetteränderung beginnt also sofort.
     */
    private void setPlanwetter(@Nullable final WetterData planwetter,
                               final boolean firstStepTakesNoTime) {
        setPlanwetter(planwetter, firstStepTakesNoTime, null);
    }

    /**
     * Setzt das Planwetter.
     *
     * @param firstStepTakesNoTime Bei <code>false</code> wird der erste Schritt
     *                             (Wetterwechsel) ganz normal ausgeführt - bei <code>true</code>
     *                             wird der erste Schritt in 0 Sekunden ausgeführt - die
     *                             Wetteränderung beginnt also sofort.
     * @param maxDuration          Die Zeit bis das Planwetter spätestens eingetreten sein soll
     *                             (circa).
     */
    public void setPlanwetter(@Nullable final WetterData planwetter,
                              final boolean firstStepTakesNoTime,
                              @Nullable final AvTimeSpan maxDuration) {
        if (planwetter == null) {
            requirePcd().setPlanwetter(null);
            return;
        }

        final float relativeVelocity;
        if (maxDuration != null) {
            final AvTimeSpan duration = WetterPathfinder.getStandardDuration(
                    getWetter(), planwetter, firstStepTakesNoTime);
            if (duration.longerThan(maxDuration)) {
                relativeVelocity = (float) maxDuration.dividedBy(duration);
            } else {
                relativeVelocity = 1f;
            }
        } else {
            relativeVelocity = 1f;
        }

        requirePcd().setPlanwetter(new PlanwetterData(relativeVelocity, planwetter));

        final AvDateTime now = timeTaker.now();

        setupNextWetterStepIfNecessary(now, firstStepTakesNoTime);

        doWetterSteps(now);
    }

    /**
     * Wenn ausreichend Zeit bis <code>now</code> vergangen ist, führt diese Methode einen
     * oder mehrere Wetter-Schritt aus.
     */
    private void doWetterSteps(final AvDateTime now) {
        while (requirePcd().getCurrentWetterStep() != null
                && now.isEqualOrAfter(requirePcd().getCurrentWetterStep().getExpDoneTime())) {
            doWetterStep();
            setupNextWetterStepIfNecessary(requirePcd().getCurrentWetterStep().getExpDoneTime(),
                    false);
        }
    }

    /**
     * Führt einen Wetter-Schritt aus, es findet also ein Wetterwechsel statt.
     */
    private void doWetterStep() {
        checkNotNull(requirePcd().getCurrentWetterStep(), "No current weather step");

        requirePcd().setWetter(requirePcd().getCurrentWetterStep().getWetterTo());
    }

    /**
     * Plant den nächsten Wetter-Schritt ein - sofern noch nötig und überhaupt möglich.
     *
     * @param stepTakesNoTime Bei <code>false</code> wird der Schritt
     *                        ganz normal ausgeführt - bei <code>true</code> wird der
     *                        Schritt in 0 Sekunden ausgeführt - der Wetterwechsel findet
     *                        sofort statt.
     */
    private void setupNextWetterStepIfNecessary(
            final AvDateTime startTime, final boolean stepTakesNoTime) {
        if (getPlanwetter() == null || getPlanwetter().getWetter().equals(getWetter())) {
            setPlanwetter(null);
            return;
        }

        requirePcd().updateCurrentWetterStep(calculateWetterStep(startTime, stepTakesNoTime));
    }

    /**
     * Berechnet den nächsten Wetter-Schritt.
     *
     * @param stepTakesNoTime Bei <code>false</code> wird der Schritt
     *                        ganz normal ausgeführt - bei <code>true</code> wird der
     *                        Schritt in 0 Sekunden ausgeführt - der Wetterwechsel findet
     *                        sofort statt.
     */
    @Nullable
    private WetterStep calculateWetterStep(
            final AvDateTime startTime, final boolean stepTakesNoTime) {
        if (getPlanwetter() == null) {
            return null;
        }

        @Nullable final StandardWetterStep firstStep =
                WetterPathfinder.findFirstStep(getWetter(), getPlanwetter().getWetter());

        return toWetterStep(firstStep, startTime, stepTakesNoTime);
    }

    /**
     * Erzeugt den nächsten Wetter-Schritt aus diesem {@link StandardWetterStep} -
     * {@code null}-safe.
     *
     * @param takesNoTime Bei <code>false</code> wird der Schritt
     *                    ganz normal erzeugt - bei <code>true</code> werden für den
     *                    Schritt 0 Sekunden eingeplant - der Wetterwechsel wird sofort
     *                    stattfinden.
     */
    @Nullable
    private WetterStep toWetterStep(@Nullable final StandardWetterStep standardWetterStep,
                                    final AvDateTime startTime,
                                    final boolean takesNoTime) {
        if (standardWetterStep == null) {
            return null;
        }

        return new WetterStep(
                standardWetterStep.getWetterTo(),
                startTime,
                calcExpectedDuration(standardWetterStep.getStandardDuration(), takesNoTime));
    }

    /**
     * Berechnet die erwartete Dauer für einen Wetter-Schritt auf Basis der Standard-Dauer und
     * der im Planwetter angegebenen relativen Geschwindigkeit.
     *
     * @param takesNoTime Bei <code>false</code> wird die Dauer
     *                    ganz normal berechnet - bei <code>true</code> werden
     *                    0 Sekunden eingeplant. Der Wetterwechsel wird also sofort stattfinden.
     */
    private AvTimeSpan calcExpectedDuration(final AvTimeSpan standardDuration,
                                            final boolean takesNoTime) {
        if (takesNoTime) {
            return NO_TIME;
        }

        if (getPlanwetter() == null) {
            return standardDuration;
        }

        return standardDuration.times(getPlanwetter().getRelativeVelocity());
    }

    public void onScEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        final ImmutableCollection<AbstractDescription<?>> alt =
                requirePcd().altOnScEnter(
                        timeTaker.now(),
                        from != null ?
                                from.storingPlaceComp().getDrinnenDraussen() :
                                DRINNEN,
                        to.storingPlaceComp().getDrinnenDraussen(),
                        from != null ?
                                from.storingPlaceComp().getEffectiveTemperaturRange() :
                                null,
                        to.storingPlaceComp().getEffectiveTemperaturRange());

        if (!alt.isEmpty()) {
            n.narrateAlt(alt, NO_TIME);
        }
    }

    /**
     * Beschreibt - sofern nötig - das aktuelle "Wetter" (z.B. die Temperatur, wenn der SC drinnen)
     * und vermerkt dann i.A. auch, dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    public void narrateWetterhinweisWennNoetig() {
        // Ggf. scActionStepCountDao verwenden,
        // vgl. FeelingsComp#narrateScMuedigkeitIfNecessary.

        @Nullable final ILocationGO location = loadScLocation();

        final ImmutableCollection<AbstractDescription<?>>
                altHinweise = requirePcd().altWetterhinweiseWennNoetig(
                timeTaker.now(),
                location != null ? location.storingPlaceComp().getDrinnenDraussen() :
                        // Der SC hat die Welt verlassen? - Dann ist kann er wohl gerade nicht den
                        // irdischen Himmel sehen.
                        DRINNEN,
                location != null ? location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class));

        if (!altHinweise.isEmpty()) {
            n.narrateAlt(altHinweise, NO_TIME);
        }
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge. Die Wetterhinweise werden für den
     * Ort erzeugt, an dem sich der SC gerade befindet - und für den aktuellen
     * Zeitpunkt. Die Methode ist also <i>nicht</i> geeignet, wenn der SC
     * also gerade noch nicht das Ziel einer aktuellen Bewegung erreicht hat - oder wenn
     * er gerade eine längere Aktion durchführt, deren Zeit noch nicht verstrichen ist.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     *
     * @see #altWetterhinweise(AvDateTime, DrinnenDraussen, EnumRange)
     */
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>>
    altWetterhinweiseFuerAktuellenZeitpunktAmOrtDesSC() {
        @Nullable final ILocationGO location = loadScLocation();
        return altWetterhinweise(
                timeTaker.now(),
                location != null ?
                        location.storingPlaceComp().getDrinnenDraussen() : DRINNEN,
                location != null ?
                        location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class));
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>> altWetterhinweise(
            final AvDateTime time, final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange) {
        return requirePcd().altWetterhinweise(time, drinnenDraussen, locationTemperaturRange);
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie
     * man das Wetter erlebt, wenn man nach draußen kommt, - ggf. eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert wurde.
     *
     * @param time Die Zeit, zu der der SC draußen angekommen ist
     */
    @NonNull
    public ImmutableSet<AbstractDescription<?>> altWetterhinweiseKommtNachDraussen(
            final AvDateTime time, final GameObjectId locationId) {
        @Nullable final ILocationGO location = (ILocationGO) world.load(locationId);
        return requirePcd().altWetterhinweiseKommtNachDraussen(
                time,
                location.storingPlaceComp().getDrinnenDraussen() ==
                        DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                location.storingPlaceComp().getEffectiveTemperaturRange());
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     * <p>
     * Die Wetterhinweise werden für den
     * Ort erzeugt, an dem sich der SC gerade befindet - und für den aktuellen
     * Zeitpunkt / Tag. Die Methode ist also <i>nicht</i> geeignet, wenn der SC
     * gerade noch nicht das Ziel einer aktuellen Bewegung erreicht hat - oder wenn
     * er gerade eine längere Aktion durchführt, deren Zeit noch nicht verstrichen ist.
     */
    @NonNull
    public ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll() {
        if (!isScDraussen()) {
            return ImmutableList.of();
        }

        @Nullable final ILocationGO location = loadScLocation();

        // Der SC hat die Welt verlassen? - Dann ist kann er wohl gerade nicht den
        // irdischen Himmel sehen.
        return requirePcd()
                .altDescUeberHeuteOderDenTagWennDraussenSinnvoll(timeTaker.now().getTime(),
                        location != null
                                && location.storingPlaceComp().getDrinnenDraussen()
                                == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                        location != null ?
                                location.storingPlaceComp().getEffectiveTemperaturRange() :
                                EnumRange.all(Temperatur.class));
    }

    @NonNull
    public ImmutableSet<String> altWetterplauderrede() {
        return requirePcd().altWetterplauderrede(timeTaker.now().getTime());
    }

    /**
     * Gibt alternative Wetterhinweise zurück in der Art "in den Sonnenschein" o.Ä., die mit
     * "hinaus" verknüpft werden können.
     * <p>
     * Der Aufrufer muss dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert wurde.
     *
     * @param time Die Zeit, zu der der SC draußen angekommen ist
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWetterhinweiseWohinHinaus(
            final AvDateTime time, final GameObjectId locationId) {
        final ILocationGO location = (ILocationGO) world.load(locationId);

        return requirePcd().altWetterhinweiseWohinHinaus(
                time,
                location.storingPlaceComp().getDrinnenDraussen() == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                location.storingPlaceComp().getEffectiveTemperaturRange());

    }

    /**
     * Gibt alternative wetterbezogene Ortsbeschreibungen für draußen zurück.
     * <p>
     * Der Aufrufer muss dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert wurde.
     *
     * @param time Die Zeit, zu der der SC dort (angekommen) ist
     */
    public ImmutableCollection<AdvAngabeSkopusVerbAllg> altWetterhinweiseWoDraussen(
            final AvDateTime time, final GameObjectId locationId) {
        final ILocationGO location = (ILocationGO) world.load(locationId);

        return requirePcd().altWetterhinweiseWoDraussen(time,
                location.storingPlaceComp().getDrinnenDraussen() ==
                        DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                location.storingPlaceComp().getEffectiveTemperaturRange());
    }

    /**
     * Gibt alternative Beschreibungen zurück in der Art "bei Licht",
     * "im Mondlicht", o.Ä.
     *
     * @param time               Die Zeit des Lichts
     * @param unterOffenemHimmel Ob der SC dann unter offenem Himmel ist
     */
    public ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(
            final AvDateTime time, final boolean unterOffenemHimmel) {
        return requirePcd().altBeiLichtImLicht(time, unterOffenemHimmel);
    }

    /**
     * Gibt alternative Beschreibungen zurück in der Art "das Mondlicht" o.Ä.
     *
     * @param time               Die Zeit des Lichts
     * @param unterOffenemHimmel Ob der SC dann unter offenem Himmel ist
     */
    public ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvDateTime time, final boolean unterOffenemHimmel) {
        return requirePcd().altLichtInDemEtwasLiegt(time, unterOffenemHimmel);
    }

    public void onTimePassed(final Change<AvDateTime> change) {
        doWetterSteps(change.getNachher());

        final ImmutableCollection<AbstractDescription<?>> alt =
                requirePcd().altTimePassed(change, loadScLocation());

        if (!alt.isEmpty()) {
            n.narrateAlt(alt, NO_TIME);
        }
    }

    /**
     * Gibt die Windstärke zurück - für den Ort erzeugt, an dem sich der SC gerade befindet.
     * Die Methode ist also <i>nicht</i> geeignet, wenn der SC
     * gerade noch nicht das Ziel einer aktuellen Bewegung erreicht hat.
     */
    @NonNull
    public Windstaerke getWindstaerkeAmOrtDesSc() {
        return getLokaleWindstaerke(loadScLocation());
    }

    /**
     * Gibt die lokale Windstärke zurück.
     */
    @Nullable
    public Windstaerke getLokaleWindstaerke(@Nullable final ILocationGO location) {
        if (location == null) {
            return null;
        }

        return requirePcd().getLokaleWindstaerke(location.storingPlaceComp().getDrinnenDraussen());
    }

    /**
     * Gibt die Temperatur zurück - für den Ort erzeugt, an dem sich der SC gerade befindet, und
     * für den aktuellen
     * Zeitpunkt / Tag. Die Methode ist also <i>nicht</i> geeignet, wenn der SC
     * gerade noch nicht das Ziel einer aktuellen Bewegung erreicht hat - oder wenn
     * er gerade eine längere Aktion durchführt, deren Zeit noch nicht verstrichen ist.
     */
    @NonNull
    public Temperatur getTemperaturFuerAktuellenZeitpunktAmOrtDesSc() {
        return getLokaleTemperatur(timeTaker.now(), loadScLocation());
    }

    /**
     * Gibt die Temperatur</i> zurück.
     */
    @NonNull
    private Temperatur getLokaleTemperatur(final AvDateTime time,
                                           @Nullable final ILocationGO location) {
        return requirePcd().getLokaleTemperatur(time,
                location != null ? location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class));
    }

    private boolean isScDraussen() {
        return loadScDrinnenDraussen().isDraussen();
    }

    private DrinnenDraussen loadScDrinnenDraussen() {
        @Nullable final ILocationGO location = loadScLocation();
        if (location == null) {
            // Der SC hat die Welt verlassen? - Dann ist kann er wohl gerade nicht den
            // irdischen Himmel sehen.
            return DRINNEN;
        }

        return location.storingPlaceComp().getDrinnenDraussen();
    }

    @Nullable
    private ILocationGO loadScLocation() {
        return world.loadSC().locationComp().getLocation();
    }

    @NonNull
    private WetterData getWetter() {
        return requirePcd().getWetter();
    }

    @Nullable
    private PlanwetterData getPlanwetter() {
        return requirePcd().getPlan();
    }
}
