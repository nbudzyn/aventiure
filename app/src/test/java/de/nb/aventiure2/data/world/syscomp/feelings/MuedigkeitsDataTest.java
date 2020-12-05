package de.nb.aventiure2.data.world.syscomp.feelings;

import org.junit.Test;

import de.nb.aventiure2.data.world.time.*;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.time.AvTime.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

public class MuedigkeitsDataTest {
    @Test
    public void createFromBiorhythmusFuerMenschen_UeberTag() {
        // GIVEN
        final MenschlicherMuedigkeitsBiorhythmus biorhythmus
                = new MenschlicherMuedigkeitsBiorhythmus();

        final AvDateTime now =
                new AvDateTime(1, oClock(14, 30));

        // WHEN
        final MuedigkeitsData actual =
                MuedigkeitsData.createFromBiorhythmusFuerMenschen(biorhythmus, now);

        // THEN
        assertThat(actual.getAusschlafenEffektHaeltVorBis().isBefore(now)).isTrue();
        assertThat(actual.getTemporaereMinimalmuedigkeitSofernRelevant(now))
                .isEqualTo(FeelingIntensity.NEUTRAL);
        assertThat(actual.getMuedigkeit()).isEqualTo(FeelingIntensity.NEUTRAL);
    }

    @Test
    public void calcAusschlafenEffektHaeltBeimMenschenVorFuer() {
        assertThat(MuedigkeitsData.calcAusschlafenEffektHaeltBeimMenschenVorFuer(mins(5)))
                .isEqualTo(noTime());
        assertThat(MuedigkeitsData.calcAusschlafenEffektHaeltBeimMenschenVorFuer(mins(30)))
                .isEqualTo(hours(2));
        assertThat(MuedigkeitsData.calcAusschlafenEffektHaeltBeimMenschenVorFuer(hours(7)))
                .isEqualTo(hours(4));
    }
}
