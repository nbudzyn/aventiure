package de.nb.aventiure2.data.world.syscomp.feelings;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;

public class BiorhythmusTest {
    @Test
    public void getLastTimeWithIntensityLessThan() {
        // GIVEN
        final MenschlicherMuedigkeitsBiorhythmus biorhythmus
                = new MenschlicherMuedigkeitsBiorhythmus();

        // WHEN, THEN
        assertThat(biorhythmus.getLastTimeWithIntensityLessThan(
                oClock(0, 30),
                FeelingIntensity.STARK))
                .isEqualTo(oClock(21, 59, 59));

        assertThat(biorhythmus.getLastTimeWithIntensityLessThan(
                oClock(14, 30),
                FeelingIntensity.STARK))
                .isEqualTo(oClock(14, 30));

        assertThat(biorhythmus.getLastTimeWithIntensityLessThan(
                oClock(22, 30), FeelingIntensity.STARK))
                .isEqualTo(oClock(21, 59, 59));
    }

    @Test
    public void getLastTimeWithIntensityAtLeast() {
        // GIVEN
        final MenschlicherMuedigkeitsBiorhythmus biorhythmus
                = new MenschlicherMuedigkeitsBiorhythmus();

        // WHEN, THEN
        assertThat(biorhythmus.getLastTimeWithIntensityAtLeast(
                oClock(0, 30),
                FeelingIntensity.STARK))
                .isEqualTo(oClock(0, 30));

        assertThat(biorhythmus.getLastTimeWithIntensityAtLeast(
                oClock(14, 30),
                FeelingIntensity.STARK))
                .isEqualTo(oClock(6, 59, 59));

        assertThat(biorhythmus.getLastTimeWithIntensityAtLeast(
                oClock(21), FeelingIntensity.STARK))
                .isEqualTo(oClock(6, 59, 59));
    }
}
