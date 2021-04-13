package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;

/**
 * Wetter
 */
public class WetterComp extends AbstractStatefulComponent<WetterPCD> {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    public WetterComp(final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
                      final World world) {
        super(WETTER, db.wetterDao());
        this.db = db;
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
     */
    public void narrateWetterOrTageszeitIfNecessary() {
        // Ggf. scActionStepCountDao verwenden,
        // vgl. FeelingsComp#narrateScMuedigkeitIfNecessary.

        final ImmutableCollection<AbstractDescription<?>>
                altHinweise = requirePcd().altWetterHinweiseWennNoetig(
                timeTaker.now().getTime(),
                world.loadSC().locationComp().getLocation().storingPlaceComp()
                        .getLichtverhaeltnisse(),
                loadScDrinnenDraussen());

        if (!altHinweise.isEmpty()) {
            n.narrateAlt(altHinweise, NO_TIME);
        }
    }

    @CheckReturnValue
    public ImmutableCollection<EinzelnerSatz> altStatischeTemperaturBeschreibungSaetze() {
        return requirePcd()
                .altStatischeTemperaturBeschreibungSaetze(timeTaker.now().getTime(),
                        loadScDrinnenDraussen().isDraussen());
    }


    @NonNull
    public ImmutableSet<AbstractDescription<?>> altKommtNachDraussen(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        return requirePcd()
                .altKommtNachDraussen(
                        timeTaker.now().getTime(), lichtverhaeltnisseDraussen,
                        isScUnterOffenemHimmel())
                .build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
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
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        return requirePcd().altWohinHinaus(timeTaker.now().getTime(), lichtverhaeltnisseDraussen,
                isScUnterOffenemHimmel());
    }

    public ImmutableCollection<Praepositionalphrase> altUnterOffenemHimmel() {
        return requirePcd().altUnterOffenemHimmel(timeTaker.now().getTime());
    }

    public ImmutableSet<Praepositionalphrase> altBeiLichtImLicht() {
        return requirePcd().altBeiLichtImLicht(timeTaker.now().getTime(), isScUnterOffenemHimmel());
    }

    public ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht() {
        return requirePcd().altBeiTageslichtImLicht(timeTaker.now().getTime(),
                isScUnterOffenemHimmel());
    }

    public ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt() {
        return requirePcd().altLichtInDemEtwasLiegt(timeTaker.now().getTime(),
                isScUnterOffenemHimmel());
    }

    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        final ImmutableCollection<AbstractDescription<?>> alt =
                requirePcd().registerTimePassed(startTime, endTime,
                        loadScDrinnenDraussen());

        if (!alt.isEmpty()) {
            n.narrateAlt(alt, NO_TIME);
        }
    }

    /**
     * Gibt alternative Sätze <i>nur zur Temperatur</i> zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen.
     */
    @NonNull
    public Temperatur getTemperatur() {
        return requirePcd().getTemperatur(timeTaker.now().getTime());
    }

    private boolean isScUnterOffenemHimmel() {
        return loadScDrinnenDraussen() == DRAUSSEN_UNTER_OFFENEM_HIMMEL;
    }

    private boolean isScDraussen() {
        return loadScDrinnenDraussen().isDraussen();
    }

    private DrinnenDraussen loadScDrinnenDraussen() {
        return world.loadSC().locationComp().getLocation().storingPlaceComp().getDrinnenDraussen();
    }
}
