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
import de.nb.aventiure2.data.time.AvTime;
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


    /**
     * Beschreibt - sofern nötig - das aktuelle Wetter
     * (oder auch einen Tageszeitenwechsel, wenn der SC drinnen ist).
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    public void narrateWetterOrTageszeitIfNecessary() {
        // Ggf. scActionStepCountDao verwenden,
        // vgl. FeelingsComp#narrateScMuedigkeitIfNecessary.

        final ImmutableCollection<AbstractDescription<?>>
                altHinweise = requirePcd().altWetterHinweiseWennNoetig(
                timeTaker.now().getTime(),
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
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     *
     * @see #altWetterHinweise(AvTime, DrinnenDraussen)
     */
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>>
    altWetterHinweiseFuerAktuellenZeitpunktAmOrtDesSC() {
        return altWetterHinweise(timeTaker.now().getTime(), loadScDrinnenDraussen());
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>> altWetterHinweise(
            final AvTime time, final DrinnenDraussen drinnenDraussen) {
        return requirePcd().altWetterHinweise(time, drinnenDraussen);
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie
     * man das Wetter erlebt, wenn man nach draußen kommt, - ggf. eine leere Menge.
     *
     * @param time               Die Zeit, zu der der SC draußen angekommen ist
     * @param unterOffenenHimmel Ob der SC unter offenen Himmel kommt
     */
    @NonNull
    public ImmutableSet<AbstractDescription<?>> altKommtNachDraussen(
            final AvDateTime time, final boolean unterOffenenHimmel) {
        return requirePcd().altKommtNachDraussen(time, unterOffenenHimmel);
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
     * Gibt alternative Beschreibungen zurück in der Art "in den Sonnenschein" o.Ä., die mit
     * "hinaus" verknüpft werden können.
     *
     * @param time               Die Zeit, zu der der SC draußen angekommen ist
     * @param unterOffenenHimmel Ob der SC unter offenen Himmel kommt
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvDateTime time, final boolean unterOffenenHimmel) {
        return requirePcd().altWohinHinaus(time, unterOffenenHimmel);
    }

    /**
     * Gibt alternative wetterbezogene Ortsbeschreibungen zurück
     *
     * @param time Die Zeit, zu der der SC draußen (angekommen) ist
     */
    public ImmutableCollection<Praepositionalphrase> altUnterOffenemHimmel(
            final AvDateTime time) {
        // FIXME Hier - und in allen anderen Methoden der Klasse - gibt es ein Problem:
        //  Es könnte sein, dass sich die Tageszeit inzwischen geändert hat!
        //  Das kann dann zu etwas führen wie "Du kommst hinaus ins Sternenlicht.
        //  Allmählich sinkt die Sonne und die ersten Sterne erscheinen am Himmel." - dass
        //  also das statische Ergebnis schon *vor* der dynamischen Beschreibung
        //  beschrieben wird. Das wäre sehr unschön.
        //  Eine Lösung könnte sein, dass generell *jede* Methode statische oder aber
        //  dynamische Ausgaben liefern kann. Die als erstes aufgerufene Methode beschreibt
        //  die Dynamik ("das Licht der ersten Sterne") und setzt ggf. die Flags - die Folge-
        //  methoden müssten eine Referenz-Uhrzeit überprüfen (Zeit, bis zu der schon beschrieben
        //  wurde) und erkennen, dass *kein* Tageszeitenwechsel mehr beschrieben werden muss.
        //  Allerdings führte das zu Problemen, wenn
        //  - mehrere Alternativen erzeugt werden und nur eine der Alternativen die WetterComp
        //   auftruft und diese am Ende gar nicht geschrieben wird -
        //  - oder mehrere Alternativen die WetterComp aufrufen und von denen nur die erste
        //    den Tageszeitenwechsel erhalten wird.
        //  Eine alternative Lösung könnte sein: Die Componente hält den letzten
        //  "erzählten" Zeitpunkt vor. Der Narrator ruft ein Callback auf (definiert im
        //  Narrator) in der Art onWasNarrated() - der Callback (hier definiert) setzt dann
        //  den erzählten Zeitpunkt weiter. (Das könnte man auch für das Hochzählen der Counter
        //  verwenden). Es gäbe wohl Schwierigkeiten, weil man so einen Callback nicht in die
        //  Datenbank persistieren kann (theoretisch egal, weil zwischen narrate() und
        //  erzählen nicht in die Datenbank gespeichert wird). Es gibt allerdings immer noch
        //  Probleme, wenn mehrere Texte für dieselbe Ausgabe kombiniert werden. - Das sollte
        //  eher selten vorkommen.
        return requirePcd().altUnterOffenemHimmel(time);
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
