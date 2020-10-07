package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

/**
 * Mutable - and therefore persistent - data of the {@link FeelingsComp} component.
 */
@Entity
public
class FeelingsPCD extends AbstractPersistentComponentData {
    @NonNull
    private Mood mood;

    @NonNull
    private Hunger hunger;

    @NonNull
    private AvDateTime zuletztGegessen;

    @NonNull
    @Ignore
    Map<GameObjectId, Map<FeelingTowardsType, Float>> feelingsTowards;

    FeelingsPCD(@NonNull final GameObjectId gameObjectId,
                @NonNull final Mood mood, @NonNull final Hunger hunger,
                @NonNull final AvDateTime zuletztGegessen) {
        this(gameObjectId, mood, hunger, zuletztGegessen, new HashMap<>());
    }

    @Ignore
    FeelingsPCD(@NonNull final GameObjectId gameObjectId,
                @NonNull final Mood mood, @NonNull final Hunger hunger,
                @NonNull final AvDateTime zuletztGegessen,
                final Map<GameObjectId, Map<FeelingTowardsType, Float>> feelingsTowards) {
        super(gameObjectId);
        this.mood = mood;
        this.hunger = hunger;
        this.zuletztGegessen = zuletztGegessen;
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

    @NonNull
    public Hunger getHunger() {
        return hunger;
    }

    public void setHunger(@NonNull final Hunger hunger) {
        setChanged();
        this.hunger = hunger;
    }

    @NonNull
    public AvDateTime getZuletztGegessen() {
        return zuletztGegessen;
    }

    public void setZuletztGegessen(@NonNull final AvDateTime zuletztGegessen) {
        setChanged();
        this.zuletztGegessen = zuletztGegessen;
    }
}
