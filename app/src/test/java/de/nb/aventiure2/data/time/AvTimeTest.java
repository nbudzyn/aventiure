package de.nb.aventiure2.data.time;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;

public class AvTimeTest {
    @Test
    public void rotateMinus_Standard() {
        // GIVEN
        final AvTime underTest = oClock(15);

        // WHEN
        final AvTime actual = underTest.rotateMinus(mins(30));

        // THEN
        assertThat(actual.getHour()).isEqualTo(14);
        assertThat(actual.getMin()).isEqualTo(30);
    }

    @Test
    public void rotateMinus_Underflow() {
        // GIVEN
        final AvTime underTest = oClock(1);

        // WHEN
        final AvTime actual = underTest.rotateMinus(mins(75));

        // THEN
        assertThat(actual.getHour()).isEqualTo(23);
        assertThat(actual.getMin()).isEqualTo(45);
    }
}
