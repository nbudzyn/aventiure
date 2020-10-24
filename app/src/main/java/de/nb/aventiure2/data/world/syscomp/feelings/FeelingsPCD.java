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

    public void setFeelingTowards(final GameObjectId target,
                                  final FeelingTowardsType type, final float intensity) {
        FeelingIntensity.checkValue(intensity);

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

    public void removeFeelingsTowards(final GameObjectId target) {
        @Nullable final Map<FeelingTowardsType, Float> oldFeelings =
                feelingsTowards.remove(target);

        if (oldFeelings == null) {
            return;
        }

        setChanged();
    }

    @CheckReturnValue
    @Nullable
    public Float getFeelingTowards(final GameObjectId target, final FeelingTowardsType type) {
        @Nullable final Map<FeelingTowardsType, Float> feelings = feelingsTowards.get(target);

        if (feelings == null) {
            return null;
        }

        return feelings.get(type);
    }

    public void requestMoodMin(final Mood mood) {
        if (getMood().isTraurigerAls(mood)) {
            requestMood(mood);
        }
    }

    public void requestMoodMax(final Mood mood) {
        if (!getMood().isTraurigerAls(mood)) {
            requestMood(mood);
        }
    }

    @NonNull
    public Mood getMood() {
        return mood;
    }

    public void requestMood(@NonNull final Mood mood) {
        if (this.mood == mood) {
            return;
        }

        this.mood = mood;

        restrictMood();

        setChanged();
    }

    void ausgeschlafen(
            final AvDateTime now,
            final AvTimeSpan ausschlafenEffektHaeltVorFuer,
            final int muedigkeitGemaessBiorhythmus) {
        setChanged();

        muedigkeitsData =
                muedigkeitsData
                        .withZuletztAusgeschlafen(now)
                        .withAusschlafenEffektHaeltVorBis(
                                now.plus(ausschlafenEffektHaeltVorFuer)
                        );

        updateMuedigkeit(now, muedigkeitGemaessBiorhythmus);
    }

    void upgradeTemporaereMinimalmuedigkeit(
            final AvDateTime now,
            final int temporaereMinimalmuedigkeit, final AvTimeSpan duration,
            final int muedigkeitGemaessBiorhythmus) {
        setChanged();

        muedigkeitsData =
                muedigkeitsData
                        .withTemporaereMinimalmuedigkeit(
                                Math.max(
                                        muedigkeitsData.getTemporaereMinimalmuedigkeit(),
                                        temporaereMinimalmuedigkeit))
                        .withTemporaerMuedeBis(now.plus(duration));

        updateMuedigkeit(now, muedigkeitGemaessBiorhythmus);
    }

    public int getMuedigkeit() {
        return muedigkeitsData.getMuedigkeit();
    }

    /**
     * Gibt die Müdigkeit zu diesem Zeitpunkt
     * als positiven {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public void updateMuedigkeit(final AvDateTime now, final int muedigkeitGemaessBiorhythmus) {
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
        // FIXME "Temporäre Müdigkeit": Der SC wird
        //  z.B. nach dem Essen etwas müde. Endet aber
        //  nach z.B. 90 Minuten.

        // FIXME alle "erschöpft / müde / schlaf / Nacht..." Texte finden - retrofitten auf das neue
        //  Konzept!

        setMuedigkeit(muedigkeit);
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

    public AvDateTime getAusschlafenEffektHaeltVorBis() {
        return muedigkeitsData.getAusschlafenEffektHaeltVorBis();
    }

    void setMuedigkeit(final int muedigkeit) {
        if (getMuedigkeit() == muedigkeit) {
            return;
        }

        muedigkeitsData = muedigkeitsData.withMuedigkeit(muedigkeit);

        restrictMood();

        setChanged();
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

    public void updateHunger(final AvDateTime now) {
        if (now.isEqualOrAfter(hungerData.getEssenHaeltVorBis())) {
            if (hungerData.getHunger() != HUNGRIG) {
                hungerData = hungerData.withHunger(HUNGRIG);

                restrictMood();

                setChanged();
            }
        }
    }

    public void saveSatt(final AvDateTime now,
                         final AvTimeSpan zeitspanneBisWiederHungrig) {
        final Hunger vorher = getHunger();

        hungerData = new HungerData(SATT, now.plus(zeitspanneBisWiederHungrig));

        if (vorher != SATT) {
            // Sich satt essen ist (für den Moment) gut für die Stimmung!
            requestMoodMin(Mood.BETRUEBT);
        }

        setChanged();
    }

    @NonNull
    HungerData getHungerData() {
        return hungerData;
    }

    @NonNull
    public Hunger getHunger() {
        return hungerData.getHunger();
    }
}
