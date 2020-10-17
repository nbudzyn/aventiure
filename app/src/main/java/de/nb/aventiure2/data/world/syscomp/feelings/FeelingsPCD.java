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

    @NonNull
    public Mood getMood() {
        return mood;
    }

    public void setMood(@NonNull final Mood mood) {
        setChanged();
        this.mood = mood;
    }

    /**
     * Gibt die Müdigkeit zu diesem Zeitpunkt
     * als positiven {@link FeelingIntensity}-Wert zurück.
     * {@link FeelingIntensity#NEUTRAL} meint <i>wach</i>.
     */
    public int getMuedigkeit(final AvDateTime dateTime) {
        return muedigkeitsData.getMuedigkeit(dateTime);
    }

    public AvDateTime getAusschlafenEffektHaeltVorBis() {
        return muedigkeitsData.getAusschlafenEffektHaeltVorBis();
    }

    // FIXME Sind diese Getter und Setter nötig für ROOM?
    //  Sonst kann sie löschen...
    @Nonnull
    public MuedigkeitsData getMuedigkeitsData() {
        return muedigkeitsData;
    }

    public void setMuedigkeitsData(final MuedigkeitsData muedigkeitsData) {
        this.muedigkeitsData = muedigkeitsData;

        setChanged();
    }

    public void updateHunger(final AvDateTime now) {
        if (now.isEqualOrAfter(hungerData.getEssenHaeltVorBis())) {
            if (hungerData.getHunger() != HUNGRIG) {
                hungerData = hungerData.withHunger(HUNGRIG);
                setChanged();
            }
        }
    }

    public void saveSatt(final AvDateTime now,
                         final AvTimeSpan zeitspanneBisWiederHungrig) {
        hungerData = new HungerData(SATT, now.plus(zeitspanneBisWiederHungrig));

        setChanged();
    }

    @NonNull
    public HungerData getHungerData() {
        return hungerData;
    }

    public void setHungerData(final HungerData hungerData) {
        this.hungerData = hungerData;

        setChanged();
    }

    @NonNull
    public Hunger getHunger() {
        return hungerData.getHunger();
    }
}
