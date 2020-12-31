package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;

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

    /**
     * Erzeugt eine <code>MuedigkeitsData</code>, das davon ausgehen, dass das
     * {@link IFeelingBeingGO} sich in letzter Zeit seinem Biorhythmus gemäß verhalten
     * hat.
     */
    public static MuedigkeitsData createFromBiorhythmusFuerMenschen(
            final Biorhythmus biorhythmus, final AvDateTime now) {
        final int muedigkeitGemaessBiorhythmus = biorhythmus.get(now.getTime());

        final boolean sollteLautBiorhythmusSchlafen =
                muedigkeitGemaessBiorhythmus >= FeelingIntensity.STARK;
        if (sollteLautBiorhythmusSchlafen) {
            final AvTime zuletztSchlafenGegangenTime =
                    biorhythmus.getLastTimeWithIntensityLessThan(
                            now.getTime(), FeelingIntensity.STARK);

            final AvDateTime zuletztSchlafenGegangenDateTime =
                    now.goBackTo(zuletztSchlafenGegangenTime);

            final AvTimeSpan schlafdauer = now.minus(zuletztSchlafenGegangenDateTime);

            final AvTimeSpan ausschlafenEffektHaeltVorFuer =
                    calcAusschlafenEffektHaeltBeimMenschenVorFuer(schlafdauer);

            // Gehen wir mal davon aus, dass das IFeelingBeing gerade aufgewacht ist.
            // Ansonsten macht ein MuedigkeitsData ohnehin nur begrenzt Sinn.
            return new MuedigkeitsData(
                    FeelingIntensity.NEUTRAL, // gerade erwacht
                    Integer.MAX_VALUE,
                    now,
                    now.plus(ausschlafenEffektHaeltVorFuer),
                    now.minus(secs(1)),
                    FeelingIntensity.NUR_LEICHT);
        }

        final AvTime zuletztErwachtTime =
                biorhythmus.getLastTimeWithIntensityAtLeast(
                        now.getTime(), FeelingIntensity.STARK);

        final AvDateTime zuletztErwachtDateTime = now.goBackTo(zuletztErwachtTime);

        return new MuedigkeitsData(
                muedigkeitGemaessBiorhythmus,
                Integer.MAX_VALUE, // Kein "Wiederholungshinweis." Erst wenn der SC noch müder wird!
                zuletztErwachtDateTime,
                now.minus(secs(1)),
                now.minus(secs(1)),
                FeelingIntensity.NUR_LEICHT);
    }

    MuedigkeitsData(final int muedigkeit,
                    final int nextHinweisActionStepCount,
                    final AvDateTime zuletztAusgeschlafen,
                    final AvDateTime ausschlafenEffektHaeltVorBis,
                    final AvDateTime temporaerMuedeBis,
                    final int temporaereMinimalmuedigkeit) {
        FeelingIntensity.checkValuePositive(muedigkeit);

        this.muedigkeit = muedigkeit;
        this.nextHinweisActionStepCount = nextHinweisActionStepCount;
        this.zuletztAusgeschlafen = zuletztAusgeschlafen;
        this.ausschlafenEffektHaeltVorBis = ausschlafenEffektHaeltVorBis;
        this.temporaerMuedeBis = temporaerMuedeBis;
        this.temporaereMinimalmuedigkeit = temporaereMinimalmuedigkeit;
    }

    /**
     * Gibt zurück, wie stark man durch die Müdigkeit im Gehen verlangsamt wird.
     */
    double getMovementSpeedFactor() {
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                // fall-through
            case FeelingIntensity.NUR_LEICHT:
                return 1;
            case FeelingIntensity.MERKLICH:
                return 1.1;
            case FeelingIntensity.DEUTLICH:
                return 1.2;
            case FeelingIntensity.STARK:
                return 1.3;
            case FeelingIntensity.SEHR_STARK:
                return 1.5;
            case FeelingIntensity.PATHOLOGISCH:
                return 2;
            default:
                throw new IllegalStateException("Unexpected value: " + muedigkeit);
        }
    }

    static int calcNextHinweisActionStepCount(
            final int scActionStepCount, final int muedigkeit) {
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                // fall-through
            case FeelingIntensity.NUR_LEICHT:
                return Integer.MAX_VALUE;
            case FeelingIntensity.MERKLICH:
                // STORY Guter Wert?
                return scActionStepCount + 12;
            case FeelingIntensity.DEUTLICH:
                // STORY Guter Wert?
                return scActionStepCount + 10;
            case FeelingIntensity.STARK:
                // STORY Guter Wert?
                return scActionStepCount + 8;
            case FeelingIntensity.SEHR_STARK:
                // STORY Guter Wert?
                return scActionStepCount + 7;
            case FeelingIntensity.PATHOLOGISCH:
                // STORY Guter Wert?
                return scActionStepCount + 3;
            default:
                throw new IllegalStateException("Unexpected value: " + muedigkeit);
        }
    }

    ImmutableList<AdverbialeAngabeSkopusSatz> altAdverbialeAngaben() {
        return getAdverbialeAngabeString().stream()
                .map(AdverbialeAngabeSkopusSatz::new)
                .collect(toImmutableList());
    }

    private ImmutableList<String> getAdverbialeAngabeString() {
        if (muedigkeit == FeelingIntensity.NEUTRAL) {
            return ImmutableList.of("hellwach", "mit voller Konzentration");
        }

        return altAdjektivphrasePraedikativ();
    }

    ImmutableList<String> altAdjektivphrasePraedikativ() {
        switch (muedigkeit) {
            case FeelingIntensity.NEUTRAL:
                return ImmutableList.of("wach");
            case FeelingIntensity.NUR_LEICHT:
                return ImmutableList.of("leicht erschöpft");
            case FeelingIntensity.MERKLICH:
                return ImmutableList.of("erschöpft");
            case FeelingIntensity.DEUTLICH:
                return ImmutableList.of("müde");
            case FeelingIntensity.STARK:
                return ImmutableList.of("völlig übermüdet", "hundemüde");
            case FeelingIntensity.SEHR_STARK:
                return ImmutableList.of("todmüde");
            case FeelingIntensity.PATHOLOGISCH:
                return ImmutableList.of("benommen");
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

    MuedigkeitsData withTemporaereMinimalmuedigkeit(
            final AvDateTime now,
            final int temporaereMinimalmuedigkeit) {
        return new MuedigkeitsData(muedigkeit,
                nextHinweisActionStepCount,
                zuletztAusgeschlafen,
                // Der ausgeschlafen-Effekt ist damit verflogen.
                AvDateTime.earliest(
                        ausschlafenEffektHaeltVorBis,
                        now.minus(AvTimeSpan.secs(1))),
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

    static AvTimeSpan calcAusschlafenEffektHaeltBeimMenschenVorFuer(
            final AvTimeSpan schlafdauer) {
        if (schlafdauer.longerThan(hours(4))) {
            // Z.B. Nachtschlaf
            return hours(4);
        }

        if (schlafdauer.longerThan(mins(75))) {
            // Z.B. zu langer Mittagsschlaf
            return hours(1);
        }

        if (schlafdauer.longerThan(mins(20))) {
            // Powernap!
            return hours(2);
        }

        // Zu kurzer Schlaf
        return noTime();
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