package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

@Immutable
public class MuedigkeitsData {
    private final int muedigkeit;

    /**
     * Step Count (gerechnet in Spieler-Aktionen), zu dem als nächtes ein
     * "Müdigkeitshinweis" geschrieben werden soll (z.B. "Du bist sehr müde".)
     */
    private final int nextHinweisActionStepCount;

    @NonNull
    private final AvDateTime zuletztAusgeschlafen;

    @NonNull
    private final AvDateTime ausschlafenEffektHaeltVorBis;

    @NonNull
    private final AvDateTime temporaerMuedeBis;

    private final int temporaereMinimalmuedigkeit;

    public MuedigkeitsData(final int muedigkeit,
                           final int nextHinweisActionStepCount,
                           final AvDateTime zuletztAusgeschlafen,
                           final AvDateTime ausschlafenEffektHaeltVorBis,
                           final AvDateTime temporaerMuedeBis,
                           final int temporaereMinimalmuedigkeit) {
        FeelingIntensity.checkValue(muedigkeit);

        this.muedigkeit = muedigkeit;
        this.nextHinweisActionStepCount = nextHinweisActionStepCount;
        this.zuletztAusgeschlafen = zuletztAusgeschlafen;
        this.ausschlafenEffektHaeltVorBis = ausschlafenEffektHaeltVorBis;
        this.temporaerMuedeBis = temporaerMuedeBis;
        this.temporaereMinimalmuedigkeit = temporaereMinimalmuedigkeit;
    }

    public static int calcNextHinweisActionStepCount(
            final int scActionStepCount, final int muedigkeit) {
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                // fall-through
            case FeelingIntensity.NUR_LEICHT:
                return Integer.MAX_VALUE;
            case FeelingIntensity.MERKLICH:
                // FIXME Guter Wert?
                return 12;
            case FeelingIntensity.DEUTLICH:
                // FIXME Guter Wert?
                return 10;
            case FeelingIntensity.STARK:
                // FIXME Guter Wert?
                return 8;
            case FeelingIntensity.SEHR_STARK:
                // FIXME Guter Wert?
                return 7;
            case FeelingIntensity.PATHOLOGISCH:
                // FIXME Guter Wert?
                return 3;
            default:
                throw new IllegalStateException("Unexpected value: " + muedigkeit);
        }
    }

    public AdverbialeAngabeSkopusSatz getAdverbialeAngabe() {
        return new AdverbialeAngabeSkopusSatz(getAdverbialeAngabeString());
    }

    private String getAdverbialeAngabeString() {
        if (muedigkeit == FeelingIntensity.NEUTRAL) {
            return "mit voller Konzentration";
        }

        return getAdjektivphrasePraedikativ();
    }

    String getAdjektivphrasePraedikativ() {
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                return "wach";
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

    MuedigkeitsData withMuedigkeit(final int muedigkeit, final int nextHinweisActionStepCount) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withNextHinweisActionStepCount(final int nextHinweisActionStepCount) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withZuletztAusgeschlafen(final AvDateTime zuletztAusgeschlafen) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withAusschlafenEffektHaeltVorBis(
            final AvDateTime ausschlafenEffektHaeltVorBis) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withTemporaerMuedeBis(final AvDateTime temporaerMuedeBis) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    MuedigkeitsData withTemporaereMinimalmuedigkeit(final int temporaereMinimalmuedigkeit) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen, ausschlafenEffektHaeltVorBis,
                temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    /**
     * Gibt die aktuelle Müdigkeit
     * als positiven {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    int getMuedigkeit() {
        return muedigkeit;
    }

    int getNextHinweisActionStepCount() {
        return nextHinweisActionStepCount;
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

    boolean hinweisNoetig(final int scActionStepCount) {
        return scActionStepCount >= nextHinweisActionStepCount;
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
        return muedigkeit == that.muedigkeit &&
                nextHinweisActionStepCount == that.nextHinweisActionStepCount &&
                temporaereMinimalmuedigkeit == that.temporaereMinimalmuedigkeit &&
                zuletztAusgeschlafen.equals(that.zuletztAusgeschlafen) &&
                ausschlafenEffektHaeltVorBis.equals(that.ausschlafenEffektHaeltVorBis) &&
                temporaerMuedeBis.equals(that.temporaerMuedeBis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(muedigkeit, nextHinweisActionStepCount, zuletztAusgeschlafen,
                ausschlafenEffektHaeltVorBis, temporaerMuedeBis, temporaereMinimalmuedigkeit);
    }

    @Override
    public String toString() {
        return "MuedigkeitsData{" +
                "muedigkeit=" + muedigkeit +
                ", nextHinweisActionStepCount=" + nextHinweisActionStepCount +
                ", zuletztAusgeschlafen=" + zuletztAusgeschlafen +
                ", ausschlafenEffektHaeltVorBis=" + ausschlafenEffektHaeltVorBis +
                ", temporaerMuedeBis=" + temporaerMuedeBis +
                ", temporaereMinimalmuedigkeit=" + temporaereMinimalmuedigkeit +
                '}';
    }
}