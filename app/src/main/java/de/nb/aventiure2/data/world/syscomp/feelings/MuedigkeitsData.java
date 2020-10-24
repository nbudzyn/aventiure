package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

@Immutable
public class MuedigkeitsData {
    private final int muedigkeit;

    @NonNull
    private final AvDateTime zuletztAusgeschlafen;

    @NonNull
    private final AvDateTime ausschlafenEffektHaeltVorBis;

    @NonNull
    private final AvDateTime temporaerMuedeBis;

    private final int temporaereMinimalmuedigkeit;

    public MuedigkeitsData(final int muedigkeit,
                           final AvDateTime zuletztAusgeschlafen,
                           final AvDateTime ausschlafenEffektHaeltVorBis,
                           final AvDateTime temporaerMuedeBis,
                           final int temporaereMinimalmuedigkeit) {
        FeelingIntensity.checkValue(muedigkeit);

        this.muedigkeit = muedigkeit;
        this.zuletztAusgeschlafen = zuletztAusgeschlafen;
        this.ausschlafenEffektHaeltVorBis = ausschlafenEffektHaeltVorBis;
        this.temporaerMuedeBis = temporaerMuedeBis;
        this.temporaereMinimalmuedigkeit = temporaereMinimalmuedigkeit;
    }

    public AdverbialeAngabeSkopusSatz getAdverbialeAngabe() {
        return new AdverbialeAngabeSkopusSatz(getAdverbialeAngabeString());
    }

    private String getAdverbialeAngabeString() {
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                return "mit voller Konzentration";
            case FeelingIntensity.NUR_LEICHT:
                return "leicht erschöpft";
            case FeelingIntensity.MERKLICH:
                return "erschöpft";
            case FeelingIntensity.DEUTLICH:
                return "müde";
            case FeelingIntensity.STARK:
                return "völlig übermüdet";
            case FeelingIntensity.SEHR_STARK:
                // STORY Alternative: hundemüde
                return "todmüde";
            case FeelingIntensity.PATHOLOGISCH:
                return "benommen";
            default:
                throw new IllegalStateException("Müdigkeit: " + muedigkeit);
        }
    }

    MuedigkeitsData withMuedigkeit(final int muedigkeit) {
        return new MuedigkeitsData(muedigkeit,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withZuletztAusgeschlafen(final AvDateTime zuletztAusgeschlafen) {
        return new MuedigkeitsData(muedigkeit,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withAusschlafenEffektHaeltVorBis(
            final AvDateTime ausschlafenEffektHaeltVorBis) {
        return new MuedigkeitsData(muedigkeit,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withTemporaerMuedeBis(final AvDateTime temporaerMuedeBis) {
        return new MuedigkeitsData(muedigkeit,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withTemporaereMinimalmuedigkeit(final int temporaereMinimalmuedigkeit) {
        return new MuedigkeitsData(muedigkeit,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    /**
     * Gibt die aktuelle Müdigkeit
     * als positiven {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public int getMuedigkeit() {
        return muedigkeit;
    }

    /**
     * Gibt die temporäre Minimalmüdigkeit zurück - etwa Müdigkeit die man nur eine Zeitlang
     * nach dem Essen verspürt - als positiver {@link FeelingIntensity}-Wert - sofern
     * sie zu diesem Zeitpunkt relevant ist.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    int getTemporaereMinimalmuedigkeitSofernRelevant(final AvDateTime dateTime) {
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