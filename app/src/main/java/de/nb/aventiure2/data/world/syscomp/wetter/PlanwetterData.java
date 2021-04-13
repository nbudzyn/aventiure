package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.room.Embedded;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;

/**
 * Wetter, wie es bis zu einem Zeitpunkt werden soll(te).
 */
@Immutable
class PlanwetterData {
    private final AvDateTime planDateTime;

    @Embedded(prefix = "plan")
    private final WetterData wetter;

    // FIXME Wetterwege: Man weiß Planwetter und Planzeitpunkt.
    //  Man ermittelt immer - ausgehend vom aktuelken Wetter - den nächsten Schritt in Richtung
    //  Planwetter (programmatisch nach einfachen Regeln in der art: erst kalt, dann bewölkumg,
    //  dann sturm). Sowie den groben zeitanteil. Man speichert die zeit, bis zu der sich nichts
    //  ändern soll und ermittelt erst dann wieder neu. Ggf auch mehrere Schrittr - wie
    //  movementcomp!

    PlanwetterData(final AvDateTime planDateTime,
                   final WetterData wetter) {
        this.planDateTime = planDateTime;
        this.wetter = wetter;
    }

    @SuppressWarnings("WeakerAccess")
    AvDateTime getPlanDateTime() {
        return planDateTime;
    }

    @SuppressWarnings("WeakerAccess")
    WetterData getWetter() {
        return wetter;
    }
}
