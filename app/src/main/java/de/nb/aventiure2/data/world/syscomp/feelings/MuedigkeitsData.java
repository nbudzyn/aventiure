package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

@Immutable
public class MuedigkeitsData {
    @NonNull
    private final AvDateTime zuletztAusgeschlafen;

    @NonNull
    private final AvDateTime ausschlafenEffektHaeltVorBis;
    // FIXME Z.B 2 Stunden (Je nachdem, wie lange man geschlafen hat. Aber irgendwann
    //  siegt der Biorythmus! Vermutlich nie mehr als 4 Stunden)

    @NonNull
    private final AvDateTime temporaerMuedeBis;

    private final int temporaereMinimalmuedigkeit;

    public MuedigkeitsData(final AvDateTime zuletztAusgeschlafen,
                           final AvDateTime ausschlafenEffektHaeltVorBis,
                           final AvDateTime temporaerMuedeBis,
                           final int temporaereMinimalmuedigkeit) {
        this.zuletztAusgeschlafen = zuletztAusgeschlafen;
        this.ausschlafenEffektHaeltVorBis = ausschlafenEffektHaeltVorBis;
        this.temporaerMuedeBis = temporaerMuedeBis;
        this.temporaereMinimalmuedigkeit = temporaereMinimalmuedigkeit;
    }

    /**
     * Gibt die Müdigkeit zu diesem Zeitpunkt
     * als positiven {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public int getMuedigkeit(final AvDateTime dateTime) {
        int res;

        if (dateTime.isEqualOrAfter(zuletztAusgeschlafen.plus(hours(28)))) {
            res = FeelingIntensity.SEHR_STARK;
        } else if (dateTime.isEqualOrAfter(zuletztAusgeschlafen.plus(hours(14)))) {
            res = FeelingIntensity.DEUTLICH;
        } else {
            res = FeelingIntensity.NEUTRAL;
        }

        res = Math.max(res, getTemporaereMinimalmuedigkeitSofernRelevant(dateTime));
        // FIXME "Temporäre Müdigkeit": Der SC wird
        //  z.B. nach dem Essen etwas müde. Endet aber
        //  nach z.B. 90 Minuten.

        return res;
    }

    /**
     * Gibt die temporäre Minimalmüdigkeit zurück - etwa Müdigkeit die man nur eine Zeitlang
     * nach dem Essen verspürt - als positiver {@link FeelingIntensity}-Wert - sofern
     * sie zu diesem Zeitpunkt relevant ist.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    private int getTemporaereMinimalmuedigkeitSofernRelevant(final AvDateTime dateTime) {
        if (dateTime.isEqualOrAfter(getTemporaerMuedeBis())) {
            // Temporäre Müdigkeit nicht mehr relevant
            return FeelingIntensity.NEUTRAL;
        }

        return temporaereMinimalmuedigkeit;
    }

    @NonNull
    AvDateTime getZuletztAusgeschlafen() {
        return zuletztAusgeschlafen;
    }

    @NonNull
    AvDateTime getAusschlafenEffektHaeltVorBis() {
        return ausschlafenEffektHaeltVorBis;
    }

    @NonNull
    AvDateTime getTemporaerMuedeBis() {
        return temporaerMuedeBis;
    }

    int getTemporaereMinimalmuedigkeit() {
        return temporaereMinimalmuedigkeit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MuedigkeitsData that = (MuedigkeitsData) o;
        return temporaereMinimalmuedigkeit == that.temporaereMinimalmuedigkeit &&
                zuletztAusgeschlafen.equals(that.zuletztAusgeschlafen) &&
                ausschlafenEffektHaeltVorBis.equals(that.ausschlafenEffektHaeltVorBis) &&
                temporaerMuedeBis.equals(that.temporaerMuedeBis);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @NonNull
    @Override
    public String toString() {
        return "MuedigkeitsData{" +
                "zuletztAusgeschlafen=" + zuletztAusgeschlafen +
                ", ausschlafenEffektHaeltVorBis=" + ausschlafenEffektHaeltVorBis +
                ", temporaerMuedeBis=" + temporaerMuedeBis +
                ", temporaereMinimalmuedigkeit=" + temporaereMinimalmuedigkeit +
                '}';
    }
}