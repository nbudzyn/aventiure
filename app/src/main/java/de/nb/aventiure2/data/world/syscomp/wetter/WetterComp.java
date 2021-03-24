package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Wetter
 */
public class WetterComp extends AbstractStatefulComponent<WetterPCD> {
    private final TimeTaker timeTaker;
    protected final Narrator n;

    public WetterComp(final AvDatabase db, final TimeTaker timeTaker,
                      final Narrator n) {
        super(WETTER, db.wetterDao());
        this.timeTaker = timeTaker;
        this.n = n;
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

    @NonNull
    public ImmutableSet<AbstractDescription<?>> altScKommtNachDraussenInsWetter(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        return requirePcd()
                .altScKommtNachDraussenInsWetter(
                        timeTaker.now().getTime(), lichtverhaeltnisseDraussen)
                .build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    public ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennSinnvoll() {
        return requirePcd().altDescUeberHeuteOderDenTagWennSinnvoll(timeTaker.now().getTime());
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
        return requirePcd().altWohinHinaus(timeTaker.now().getTime(), lichtverhaeltnisseDraussen);
    }

    /**
     * Gibt alternative Sätze <i>nur zur Temperatur</i> zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen.
     */
    @NonNull
    public Temperatur getTemperatur() {
        return requirePcd().getTemperatur(timeTaker.now().getTime());
    }

    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
    }
}
