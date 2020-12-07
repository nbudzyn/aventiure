package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

/**
 * Mutable - and therefore persistent - data of the {@link FeelingsComp} component.
 */
@Entity
public
class FeelingsPCD extends AbstractPersistentComponentData {
    @NonNull
    private Mood mood;

    @Embedded
    @NonNull
    private MuedigkeitsData muedigkeitsData;

    @Embedded
    @NonNull
    private HungerData hungerData;

    @NonNull
    @Ignore
    private final
    Map<GameObjectId, Map<FeelingTowardsType, Float>> feelingsTowards;

    FeelingsPCD(final GameObjectId gameObjectId,
                final Mood mood,
                final MuedigkeitsData muedigkeitsData,
                final HungerData hungerData) {
        this(gameObjectId, mood, muedigkeitsData, hungerData, new HashMap<>());
    }

    @Ignore
    FeelingsPCD(final GameObjectId gameObjectId,
                final Mood mood,
                final MuedigkeitsData muedigkeitsData,
                final HungerData hungerData,
                final Map<GameObjectId, Map<FeelingTowardsType, Float>> feelingsTowards) {
        super(gameObjectId);
        this.mood = mood;
        this.muedigkeitsData = muedigkeitsData;
        this.hungerData = hungerData;
        this.feelingsTowards = feelingsTowards;
    }

    @NonNull
    ImmutableMap<GameObjectId, Map<FeelingTowardsType, Float>> getFeelingsMap() {
        return ImmutableMap.copyOf(feelingsTowards);
    }

    /**
     * Darf nur zur Initialisierung aufgerufen werden, nicht zur Änderung!
     */
    void initFeelingTowardsInfos(final Map<GameObjectId, Map<FeelingTowardsType, Float>> map) {
        Preconditions.checkState(feelingsTowards.isEmpty(), "Already initialized!");

        // Kein setChanged() !
        feelingsTowards.putAll(map);
    }

    void setFeelingTowards(final GameObjectId target,
                           final FeelingTowardsType type, final float intensity) {
        FeelingIntensity.checkValueAbsolute(intensity);

        final Float oldValue = getFeelingTowards(target, type);
        if (oldValue != null && oldValue == intensity) {
            return;
        }

        @Nullable Map<FeelingTowardsType, Float> innerMap = feelingsTowards.get(target);

        if (innerMap == null) {
            innerMap = new HashMap<>(FeelingTowardsType.values().length);
            feelingsTowards.put(target, innerMap);
        }

        innerMap.put(type, intensity);

        if (intensity <= -FeelingIntensity.STARK) {
            // Wer merkt, dass er jemanden hasst, hat keine gute Laune.
            requestMoodMax(Mood.AUFGEDREHT);
        }

        if (intensity >= -FeelingIntensity.STARK) {
            // Wer sich in jemanden verliebt, ist nicht betrübt.
            requestMoodMin(Mood.ETWAS_GEKNICKT);
        }

        setChanged();
    }

    void removeFeelingsTowards(final GameObjectId target) {
        @Nullable final Map<FeelingTowardsType, Float> oldFeelings =
                feelingsTowards.remove(target);

        if (oldFeelings == null) {
            return;
        }

        setChanged();
    }

    @CheckReturnValue
    @Nullable
    Float getFeelingTowards(final GameObjectId target, final FeelingTowardsType type) {
        @Nullable final Map<FeelingTowardsType, Float> feelings = feelingsTowards.get(target);

        if (feelings == null) {
            return null;
        }

        return feelings.get(type);
    }

    void requestMoodMin(final Mood mood) {
        if (getMood().isTraurigerAls(mood)) {
            requestMood(mood);
        }
    }

    void requestMoodMax(final Mood mood) {
        if (!getMood().isTraurigerAls(mood)) {
            requestMood(mood);
        }
    }

    @NonNull
    Mood getMood() {
        return mood;
    }

    void requestMood(@NonNull final Mood mood) {
        if (this.mood == mood) {
            return;
        }

        this.mood = mood;

        restrictMood();

        setChanged();
    }

    /**
     * Gibt zurück, wie stark man durch die aktuelle Müdigkeit im Gehen verlangsamt wird.
     */
    double getMovementSpeedFactor() {
        return muedigkeitsData.getMovementSpeedFactor();
    }

    void ausgeschlafen(
            final AvDateTime now,
            final int scActionStepCount,
            final AvTimeSpan ausschlafenEffektHaeltVorFuer,
            final int muedigkeitGemaessBiorhythmus) {
        setChanged();

        muedigkeitsData =
                muedigkeitsData
                        .withTemporaerMuedeBis(now.minus(secs(1)))
                        .withZuletztAusgeschlafen(now)
                        .withAusschlafenEffektHaeltVorBis(
                                now.plus(ausschlafenEffektHaeltVorFuer)
                        );

        updateMuedigkeit(now, scActionStepCount, muedigkeitGemaessBiorhythmus);
    }

    void upgradeTemporaereMinimalmuedigkeit(
            final AvDateTime now,
            final int scActionStepCount,
            final int temporaereMinimalmuedigkeit, final AvTimeSpan duration,
            final int muedigkeitGemaessBiorhythmus) {
        setChanged();

        final int maxTemporaereMinimalmuedigkeit = Math.max(
                getTemporaereMinimalmuedigkeitSofernRelevant(now),
                temporaereMinimalmuedigkeit);
        final AvDateTime maxTemporaerMuedeBis =
                AvDateTime.latest(
                        now.plus(duration),
                        muedigkeitsData.getTemporaerMuedeBis());
        muedigkeitsData =
                muedigkeitsData
                        .withTemporaereMinimalmuedigkeit(
                                now,
                                maxTemporaereMinimalmuedigkeit)
                        .withTemporaerMuedeBis(maxTemporaerMuedeBis);

        updateMuedigkeit(now, scActionStepCount, muedigkeitGemaessBiorhythmus);
    }

    int getMuedigkeit() {
        return muedigkeitsData.getMuedigkeit();
    }

    /**
     * Gibt die Müdigkeit zu diesem Zeitpunkt
     * als positiven {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    void updateMuedigkeit(final AvDateTime now, final int scActionStepCount,
                          final int muedigkeitGemaessBiorhythmus) {
        int muedigkeit = FeelingIntensity.NEUTRAL;
        if (!geradeAusgeschlafen(now)) {
            muedigkeit = muedigkeitGemaessBiorhythmus;
        }

        if (now.isEqualOrAfter(getZuletztAusgeschlafen().plus(hours(28)))) {
            muedigkeit = Math.max(muedigkeit, FeelingIntensity.SEHR_STARK);
        } else if (now.isEqualOrAfter(getZuletztAusgeschlafen().plus(hours(14)))) {
            muedigkeit = Math.max(muedigkeit, FeelingIntensity.DEUTLICH);
        }

        muedigkeit = Math.max(muedigkeit, getTemporaereMinimalmuedigkeitSofernRelevant(now));

        setMuedigkeit(scActionStepCount, muedigkeit);
    }

    private int getTemporaereMinimalmuedigkeitSofernRelevant(final AvDateTime dateTime) {
        return muedigkeitsData.getTemporaereMinimalmuedigkeitSofernRelevant(dateTime);
    }

    private AvDateTime getZuletztAusgeschlafen() {
        return muedigkeitsData.getZuletztAusgeschlafen();
    }

    /**
     * Gibt zurück, ob das Feeling Being gerade ausgeschlafen hat. In dieser Zeit
     * greifen weder biorythmische noch temporäre Müdigkeit.
     */
    private boolean geradeAusgeschlafen(final AvDateTime now) {
        return now.isBefore(getAusschlafenEffektHaeltVorBis());
    }

    private AvDateTime getAusschlafenEffektHaeltVorBis() {
        return muedigkeitsData.getAusschlafenEffektHaeltVorBis();
    }

    private void setMuedigkeit(final int scActionStepCount, final int muedigkeit) {
        if (getMuedigkeit() == muedigkeit) {
            return;
        }

        muedigkeitsData =
                muedigkeitsData.withMuedigkeit(muedigkeit,
                        MuedigkeitsData.calcNextHinweisActionStepCount(
                                scActionStepCount, muedigkeit));

        restrictMood();

        setChanged();
    }

    void resetNextMuedigkeitshinweisActionStepCount(final int scActionStepCount) {
        muedigkeitsData =
                muedigkeitsData.withNextHinweisActionStepCount(
                        MuedigkeitsData.calcNextHinweisActionStepCount(
                                scActionStepCount, getMuedigkeit()));
    }

    boolean muedigkeitshinweisNoetig(final int scActionStepCount) {
        return muedigkeitsData.hinweisNoetig(scActionStepCount);
    }

    /**
     * Schränkt den {@link Mood} ein, basierend auf Hunger und Müdigkeit: Wer
     * extrem müde ist, kann nicht superglücklich sein.
     */
    private void restrictMood() {
        if (getHunger() == HUNGRIG && !mood.isSehrEmotional()) {
            // Hunger ist dauerhaft schlecht für die Stimmung - außer man wäre gerade
            // sehr emotional!

            requestMoodMax(Mood.ZUFRIEDEN);
        }

        if (getMuedigkeit() >= FeelingIntensity.DEUTLICH) {
            requestMoodMax(Mood.GLUECKLICH);
            requestMoodMin(Mood.TRAURIG);
        }

        if (getMuedigkeit() >= FeelingIntensity.SEHR_STARK) {
            requestMoodMax(Mood.BEWEGT);
            requestMoodMin(Mood.BETRUEBT);
        }
    }

    @Nonnull
    MuedigkeitsData getMuedigkeitsData() {
        return muedigkeitsData;
    }

    void updateHunger(final AvDateTime now) {
        if (now.isEqualOrAfter(hungerData.getEssenHaeltVorBis())) {
            if (hungerData.getHunger() != HUNGRIG) {
                hungerData = hungerData.withHunger(HUNGRIG);

                restrictMood();

                setChanged();
            }
        }
    }

    /**
     * Speichert, dass das {@link IFeelingBeingGO} satt ist. Hat ggf. auch Auswirkungen
     * auf Laune und Müdigkeit.
     */
    void saveSatt(final AvDateTime now,
                  final AvTimeSpan zeitspanneBisWiederHungrig,
                  final int scActionStepCount,
                  final int muedigkeitGemaessBiorhythmus) {
        final Hunger vorher = getHunger();

        hungerData = new HungerData(SATT, now.plus(zeitspanneBisWiederHungrig));

        if (vorher != SATT) {
            // Sich satt essen ist (für den Moment) gut für die Stimmung!
            requestMoodMin(Mood.BETRUEBT);
            // Aber man wird etwas müde
            upgradeTemporaereMinimalmuedigkeit(now, scActionStepCount,
                    FeelingIntensity.NUR_LEICHT, mins(90), muedigkeitGemaessBiorhythmus);
        }

        setChanged();
    }

    @NonNull
    HungerData getHungerData() {
        return hungerData;
    }

    @NonNull
    Hunger getHunger() {
        return hungerData.getHunger();
    }
}
