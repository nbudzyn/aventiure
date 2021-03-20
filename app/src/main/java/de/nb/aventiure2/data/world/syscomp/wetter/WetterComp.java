package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Wetter
 */
public class WetterComp extends AbstractStatefulComponent<WetterPCD> {
    private final AvDatabase db;
    private final World world;
    private final TimeTaker timeTaker;
    protected final Narrator n;

    public WetterComp(final AvDatabase db, final World world, final TimeTaker timeTaker,
                      final Narrator n) {
        super(WETTER, db.wetterDao());
        this.db = db;
        this.world = world;
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
    public ImmutableSet<AbstractDescription<?>> altScKommtNachDraussenInsWetter() {
        return requirePcd().altScKommtNachDraussenInsWetter(timeTaker.now().getTime())
                .build();
    }

    @NonNull
    public ImmutableSet<String> altWetterplauderrede() {
        return requirePcd().altWetterplauderrede(timeTaker.now().getTime());
    }

    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
    }
}
