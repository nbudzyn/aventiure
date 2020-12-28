package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import org.junit.Test;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

public class FeelingsPCDTest {
    @Test
    public void saveSatt__vorherHungrig_EsssenMachtMuede() {
        // GIVEN
        final FeelingsPCD feelingsPCD = feelingsPCDNichtMuedeAberHungrig();

        // WHEN
        final AvDateTime now =
                new AvDateTime(1, 0, 0);
        final AvTimeSpan zeitspanneBisWiederHungrig = mins(90);
        feelingsPCD.saveSatt(now, zeitspanneBisWiederHungrig,
                10, FeelingIntensity.NEUTRAL);

        // THEN
        assertThat(feelingsPCD.getMuedigkeit()).isGreaterThan(FeelingIntensity.NEUTRAL);
    }

    @Test
    public void saveSatt__vorherSatt_EsssenMachtNichtMuede() {
        // GIVEN
        final FeelingsPCD feelingsPCD = feelingsPCDWederMuedeNochHungrig();

        // WHEN
        final AvDateTime now =
                new AvDateTime(1, 0, 0);
        final AvTimeSpan zeitspanneBisWiederHungrig = mins(90);
        feelingsPCD.saveSatt(now, zeitspanneBisWiederHungrig,
                10, FeelingIntensity.NEUTRAL);

        // THEN
        assertThat(feelingsPCD.getMuedigkeit()).isEqualTo(FeelingIntensity.NEUTRAL);
    }

    @NonNull
    private static FeelingsPCD feelingsPCDNichtMuedeAberHungrig() {
        return new FeelingsPCD(
                gameObjectId(),
                mood(),
                muedigkeitsDataNichtMuede(),
                hungerDataHungrig());
    }

    @NonNull
    private static FeelingsPCD feelingsPCDWederMuedeNochHungrig() {
        return new FeelingsPCD(
                gameObjectId(),
                mood(),
                muedigkeitsDataNichtMuede(),
                hungerDataSatt());
    }

    private static GameObjectId gameObjectId() {
        return new GameObjectId(1);
    }

    private static Mood mood() {
        return Mood.NEUTRAL;
    }

    private static MuedigkeitsData muedigkeitsDataNichtMuede() {
        return new MuedigkeitsData(
                FeelingIntensity.NEUTRAL,
                Integer.MAX_VALUE,
                new AvDateTime(0, 0, 0),
                new AvDateTime(0, 0, 0),
                new AvDateTime(0, 0, 0),
                FeelingIntensity.NUR_LEICHT
        );
    }

    private static HungerData hungerDataHungrig() {
        return hungerData(Hunger.HUNGRIG);
    }

    private static HungerData hungerDataSatt() {
        return hungerData(Hunger.SATT);
    }

    @NonNull
    private static HungerData hungerData(final Hunger hunger) {
        return new HungerData(hunger, new AvDateTime(100,
                0, 0));
    }
}