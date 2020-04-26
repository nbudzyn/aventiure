package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvDateTime;

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

    FeelingsPCD(@NonNull final GameObjectId gameObjectId,
                @NonNull final Mood mood, @NonNull final Hunger hunger,
                @NonNull final AvDateTime zuletztGegessen) {
        super(gameObjectId);
        this.mood = mood;
        this.hunger = hunger;
        this.zuletztGegessen = zuletztGegessen;
    }

    @NonNull
    public Mood getMood() {
        return mood;
    }

    public void setMood(@NonNull final Mood mood) {
        this.mood = mood;
    }

    @NonNull
    public Hunger getHunger() {
        return hunger;
    }

    public void setHunger(@NonNull final Hunger hunger) {
        this.hunger = hunger;
    }

    @NonNull
    public AvDateTime getZuletztGegessen() {
        return zuletztGegessen;
    }

    public void setZuletztGegessen(@NonNull final AvDateTime zuletztGegessen) {
        this.zuletztGegessen = zuletztGegessen;
    }
}
