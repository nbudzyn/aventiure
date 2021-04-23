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
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

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
        final WetterData wetterData =
                new WetterData(
                        Temperatur.RECHT_HEISS, Temperatur.KUEHL,
                        Windstaerke.WINDSTILL,
                        Bewoelkung.WOLKENLOS,
                        BlitzUndDonner.KEIN_BLITZ_ODER_DONNER);
        return new WetterPCD(WETTER, wetterData);
    }

    public void onScEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        // FIXME Ggf. etwas schreiben wie "– hier ist es angenehm kühl"
        //  ("Angenehm kühl ist es hier", ...)
        //  automatisch erzeugen, wenn man an einen Raum kommt, dessen
        //  Maximaltemperatur unter der zuletzt beschriebenen oder üblichen...
        //  Temperatur liegt.
        //  Außerdem solche Texte entfernen, wo sie derzeit
        //  erzeugt werden - damit zumindest nichts doppelt kommt.
    }

    /**
     * Beschreibt - sofern nötig - das aktuelle "Wetter" (z.B. die Temperatur, wenn der SC drinnen)
     * und vermerkt dann i.A. auch, dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    public void narrateWetterhinweisWennNoetig() {
        // Ggf. scActionStepCountDao verwenden,
        // vgl. FeelingsComp#narrateScMuedigkeitIfNecessary.

        final ImmutableCollection<AbstractDescription<?>>
                altHinweise = requirePcd().altWetterhinweiseWennNoetig(
                timeTaker.now(),
                loadScDrinnenDraussen());

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
     * @see #altWetterhinweise(AvDateTime, DrinnenDraussen)
     */
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>>
    altWetterhinweiseFuerAktuellenZeitpunktAmOrtDesSC() {
        return altWetterhinweise(timeTaker.now(), loadScDrinnenDraussen());
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
            final AvDateTime time, final DrinnenDraussen drinnenDraussen) {
        return requirePcd().altWetterhinweise(time, drinnenDraussen);
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
     * @param time               Die Zeit, zu der der SC draußen angekommen ist
     * @param unterOffenenHimmel Ob der SC unter offenen Himmel kommt
     */
    @NonNull
    public ImmutableSet<AbstractDescription<?>> altWetterhinweiseKommtNachDraussen(
            final AvDateTime time, final boolean unterOffenenHimmel) {
        return requirePcd().altWetterhinweiseKommtNachDraussen(time, unterOffenenHimmel);
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

        return requirePcd()
                .altDescUeberHeuteOderDenTagWennDraussenSinnvoll(timeTaker.now().getTime(),
                        isScUnterOffenemHimmel());
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
     * @param time               Die Zeit, zu der der SC draußen angekommen ist
     * @param unterOffenenHimmel Ob der SC unter offenen Himmel kommt
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWetterhinweiseWohinHinaus(
            final AvDateTime time, final boolean unterOffenenHimmel) {
        return requirePcd().altWetterhinweiseWohinHinaus(time, unterOffenenHimmel);
    }

    /**
     * Gibt alternative wetterbezogene Ortsbeschreibungen für draußen zurück.
     * <p>
     * Der Aufrufer muss dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert wurde.
     *
     * @param time               Die Zeit, zu der der SC dort (angekommen) ist
     * @param unterOffenemHimmel Ob der SC (dann) unter offenen Himmel ist
     */
    public ImmutableCollection<AdvAngabeSkopusVerbAllg> altWetterhinweiseWoDraussen(
            final AvDateTime time, final boolean unterOffenemHimmel) {
        return requirePcd().altWetterhinweiseWoDraussen(time, unterOffenemHimmel);
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

    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        final ImmutableCollection<AbstractDescription<?>> alt =
                requirePcd().onTimePassed(startTime, endTime, loadScDrinnenDraussen());

        if (!alt.isEmpty()) {
            n.narrateAlt(alt, NO_TIME);
        }
    }

    /**
     * Gibt die Temperatur</i> zurück - für den Ort erzeugt, an dem sich der SC gerade befindet, und
     * für den aktuellen
     * Zeitpunkt / Tag. Die Methode ist also <i>nicht</i> geeignet, wenn der SC
     * gerade noch nicht das Ziel einer aktuellen Bewegung erreicht hat - oder wenn
     * er gerade eine längere Aktion durchführt, deren Zeit noch nicht verstrichen ist.
     */
    @NonNull
    public Temperatur getTemperaturFuerAktuellenZeitpunktAmOrtDesSC() {
        return getTemperatur(timeTaker.now());
    }

    /**
     * Gibt die Temperatur</i> zurück.
     */
    @NonNull
    public Temperatur getTemperatur(final AvDateTime time) {
        return requirePcd().getTemperatur(time);
    }

    private boolean isScUnterOffenemHimmel() {
        return loadScDrinnenDraussen() == DRAUSSEN_UNTER_OFFENEM_HIMMEL;
    }

    private boolean isScDraussen() {
        return loadScDrinnenDraussen().isDraussen();
    }

    private DrinnenDraussen loadScDrinnenDraussen() {
        @Nullable final ILocationGO location = world.loadSC().locationComp().getLocation();
        if (location == null) {
            // Der SC hat die Welt verlassen? - Dann ist kann er wohl gerade nicht den
            // irdischen Himmel sehen.
            return DRINNEN;
        }

        return location.storingPlaceComp().getDrinnenDraussen();
    }
}
