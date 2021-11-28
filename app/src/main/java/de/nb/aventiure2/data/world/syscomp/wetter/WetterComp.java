package de.nb.aventiure2.data.world.syscomp.wetter;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRINNEN;

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
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;
import de.nb.aventiure2.german.satz.SemSatz;

/**
 * Wetter
 */
public class WetterComp extends AbstractStatefulComponent<WetterPCD>
        implements IWorldLoaderMixin {
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
     *                             Wetteränderung beginnt also sofort (beim nächsten
     *                             {@link #onTimePassed(Change)})
     */
    private void setPlanwetter(@Nullable final WetterData planwetter,
                               final boolean firstStepTakesNoTime) {
        setPlanwetter(planwetter, firstStepTakesNoTime, null);
    }

    /**
     * Setzt das Planwetter.
     *
     * @param maxDuration Die Zeit bis das Planwetter spätestens eingetreten sein soll
     *                    (circa).
     */
    public void setPlanwetter(@Nullable final WetterData planwetter,
                              @Nullable final AvTimeSpan maxDuration) {
        setPlanwetter(planwetter, false, maxDuration);
    }

    /**
     * Setzt das Planwetter.
     *
     * @param firstStepTakesNoTime Bei <code>false</code> wird der erste Schritt
     *                             (Wetterwechsel) ganz normal ausgeführt - bei
     *                             <code>true</code>
     *                             wird der erste Schritt in 0 Sekunden ausgeführt - die
     *                             Wetteränderung beginnt also sofort (beim nächsten
     *                             {@link #onTimePassed(Change)}).
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

        final PlanwetterData newPlanwetterData = new PlanwetterData(relativeVelocity, planwetter);

        if (!firstStepTakesNoTime && newPlanwetterData.equals(requirePcd().getPlan())) {
            // Plan ändert sich nicht - einfach Plan fortsetzen!
            return;
        }

        requirePcd().setPlanwetter(newPlanwetterData);

        final AvDateTime now = timeTaker.now();

        setupNextWetterStepIfNecessary(now, firstStepTakesNoTime);

        // Die Wetteränderung beginnt beim nächsten onTimePassed(). Dort werden dann auch
        // sowohl die Wetteränderung als auch die Listener-Texte erzählt.
    }

    /**
     * Wenn ausreichend Zeit bis <code>now</code> vergangen ist, führt diese Methode einen
     * oder mehrere Wetter-Schritt aus.
     */
    private ImmutableList<WetterData> narrateAndDoWetterSteps(final AvDateTime now) {
        final ImmutableList.Builder<WetterData> wetterChangesBuilder = ImmutableList.builder();

        while (requirePcd().getCurrentWetterStep() != null
                && now.isEqualOrAfter(
                requireNonNull(requirePcd().getCurrentWetterStep()).getExpDoneTime())) {

            final WetterData oldWetter = getWetter();

            narrateAndDoWetterStep();

            if (!oldWetter.equals(getWetter())) {
                if (wetterChangesBuilder.build().isEmpty()) {
                    wetterChangesBuilder.add(oldWetter);
                }

                wetterChangesBuilder.add(getWetter());
            }

            setupNextWetterStepIfNecessary(
                    requireNonNull(requirePcd().getCurrentWetterStep()).getExpDoneTime(),
                    false);
        }

        return wetterChangesBuilder.build();
    }

    /**
     * Führt einen Wetter-Schritt aus, es findet also ein Wetterwechsel statt.
     */
    private void narrateAndDoWetterStep() {
        requireNonNull(requirePcd().getCurrentWetterStep(), "No current weather step");

        final WetterData wetter = requireNonNull(requirePcd().getCurrentWetterStep()).getWetterTo();

        requirePcd().setWetter(wetter);
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
        final ImmutableCollection<AbstractDescription<?>> altSp =
                requirePcd().altSpOnScEnter(
                        timeTaker.now(),
                        from != null ?
                                from.storingPlaceComp().getDrinnenDraussen() :
                                DRINNEN,
                        to.storingPlaceComp().getDrinnenDraussen(),
                        from != null ?
                                from.storingPlaceComp().getEffectiveTemperaturRange() :
                                null,
                        to.storingPlaceComp().getEffectiveTemperaturRange());

        if (!altSp.isEmpty()) {
            n.narrateAlt(altSp, NO_TIME);
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
                altSpHinweise = requirePcd().altSpWetterhinweiseWennNoetig(
                timeTaker.now(),
                location != null ? location.storingPlaceComp().getDrinnenDraussen() :
                        // Der SC hat die Welt verlassen? - Dann ist kann er wohl gerade nicht den
                        // irdischen Himmel sehen.
                        DRINNEN,
                location != null ? location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class));

        if (!altSpHinweise.isEmpty()) {
            n.narrateAlt(altSpHinweise, NO_TIME);
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
     * @see #altSpWetterhinweise(AvDateTime, DrinnenDraussen, EnumRange)
     */
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>>
    altSpWetterhinweiseFuerAktuellenZeitpunktAmOrtDesSC() {
        @Nullable final ILocationGO location = loadScLocation();
        return altSpWetterhinweise(timeTaker.now(), location);
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode - auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    private ImmutableCollection<AbstractDescription<?>> altSpWetterhinweise(
            final AvDateTime dateTime,
            @Nullable final ILocationGO location) {
        return altSpWetterhinweise(
                dateTime,
                location != null ?
                        location.storingPlaceComp().getDrinnenDraussen() : DRINNEN,
                location != null ?
                        location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class));
    }

    /**
     * Gibt alternative Sätze zum "Wetter" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode - auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    public ImmutableCollection<SemSatz> altSpWetterhinweisSaetze(
            final AvDateTime dateTime, @Nullable final GameObjectId locationId,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        return altSpWetterhinweisSaetze(dateTime, (ILocationGO) load(locationId),
                nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete);
    }

    /**
     * Gibt alternative Sätze zum "Wetter" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode - auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    private ImmutableCollection<SemSatz> altSpWetterhinweisSaetze(
            final AvDateTime dateTime, @Nullable final ILocationGO location,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        return altSpWetterhinweisSaetze(
                dateTime,
                location != null ?
                        location.storingPlaceComp().getDrinnenDraussen() : DRINNEN,
                location != null ?
                        location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class),
                nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete);
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode - auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>> altSpWetterhinweise(
            final AvDateTime time, final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange) {
        return requirePcd().altSpWetterhinweise(time, drinnenDraussen, locationTemperaturRange);
    }

    /**
     * Gibt alternative Sätze zum "Wetter" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode - auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    private ImmutableCollection<SemSatz> altSpWetterhinweisSaetze(
            final AvDateTime time, final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        return requirePcd()
                .altSpWetterhinweisSaetze(time, drinnenDraussen, locationTemperaturRange,
                        nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete);
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
    public ImmutableSet<AbstractDescription<?>> altSpWetterhinweiseKommtNachDraussen(
            final AvDateTime time, final GameObjectId locationId) {
        final ILocationGO location = loadRequired(locationId);
        return requirePcd().altSpWetterhinweiseKommtNachDraussen(
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
    altSpDescUeberHeuteOderDenTagWennDraussenSinnvoll() {
        if (!isScDraussen()) {
            return ImmutableList.of();
        }

        @Nullable final ILocationGO location = loadScLocation();

        // Der SC hat die Welt verlassen? - Dann ist kann er wohl gerade nicht den
        // irdischen Himmel sehen.
        return requirePcd()
                .altSpDescUeberHeuteOderDenTagWennDraussenSinnvoll(timeTaker.now().getTime(),
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
        final ILocationGO location = loadRequired(locationId);

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
        final ILocationGO location = loadRequired(locationId);

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
        final ImmutableList<WetterData> wetterSteps = narrateAndDoWetterSteps(change.getNachher());

        final ImmutableCollection<AbstractDescription<?>> altSp =
                requirePcd().altSpTimePassed(change, loadScLocation());

        if (!altSp.isEmpty()) {
            n.narrateAlt(altSp, NO_TIME);
        }

        if (!wetterSteps.isEmpty()) {
            world.narrateAndDoReactions().onWetterChanged(wetterSteps);
        }
    }

    /**
     * Gibt alternative Sätze zu Windgeräuschen zurück - kann leer sein.
     */
    public ImmutableCollection<EinzelnerSemSatz> altSpWindgeraeuscheSaetze(
            final boolean unterOffenemHimmel) {
        return requirePcd()
                .altSpWindgeraeuscheSaetze(timeTaker.now().getTime(), unterOffenemHimmel);
    }

    /**
     * Gibt die Windstärke zurück - für den Ort erzeugt, an dem sich der SC gerade befindet
     * (drinnen {@link Windstaerke#WINDSTILL}).
     * Die Methode ist also <i>nicht</i> geeignet, wenn der SC
     * gerade noch nicht das Ziel einer aktuellen Bewegung erreicht hat.
     */
    public Windstaerke getWindstaerkeAmOrtDesSc() {
        return getLokaleWindstaerke(loadScLocation());
    }

    public double getMovementSpeedFactor(@Nullable final ILocationGO from,
                                         @Nullable final ILocationGO to) {
        return (getMovementSpeedFactor(from) + getMovementSpeedFactor(to)) / 2;
    }

    private double getMovementSpeedFactor(@Nullable final ILocationGO location) {
        return getLokaleWindstaerke(location).getMovementSpeedFactor();
    }

    /**
     * Gibt die lokale "Windstärke" zurück - drinnen (oder bei Location
     * {@code null}) {@link Windstaerke#WINDSTILL}.
     */
    public Windstaerke getLokaleWindstaerke(@Nullable final ILocationGO location) {
        if (location == null) {
            return Windstaerke.WINDSTILL;
        }

        return requirePcd().getLokaleWindstaerke(location.storingPlaceComp().getDrinnenDraussen());
    }

    /**
     * Gibt die Windstaerke unter offenem Himmel zurück.
     *
     * @see #getLokaleWindstaerke(ILocationGO)
     * @see #getWindstaerkeAmOrtDesSc()
     */
    public Windstaerke getWindstaerkeUnterOffenemHimmel() {
        return requirePcd().getWetter().getWindstaerkeUnterOffenemHimmel();
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
        return loadSC().locationComp().getLocation();
    }

    @NonNull
    private WetterData getWetter() {
        return requirePcd().getWetter();
    }

    @Nullable
    private PlanwetterData getPlanwetter() {
        return requirePcd().getPlan();
    }

    @Override
    public World getWorld() {
        return world;
    }
}
