package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.GameObjectId;

@Entity(primaryKeys = {"feelingBeing", "target", "type"})
public class FeelingsTowardsInfo {
    @NonNull
    private GameObjectId feelingBeing;

    @NonNull
    private GameObjectId target;

    @NonNull
    private FeelingTowardsType type;

    /**
     * Die Intensität des Gefühls, -5 bis +5.
     * </p>
     * <ul>
     * <li>0 ist neutral.
     * <li>0.5 / -0.5 ist eine leichte Neigung, die einem gerade bewusst ist
     * <li>1.5 / -1.5 ist z.B. eine deutliche Zu- oder Abneigung
     * <li>3 / -3 bedeutet z.B. Liebe oder Hass
     * <li>Ein Gefühl jenseits von 4 / -4 tritt nur extremen Persönlichkeiten auf ("Erbösewichten")
     * und ist möglicherweise krankhaft
     * </ul>
     */
    private float intensity;

    public FeelingsTowardsInfo(final GameObjectId feelingBeing,
                               final GameObjectId target,
                               final FeelingTowardsType type, final float intensity) {
        this.feelingBeing = feelingBeing;
        this.target = target;
        this.type = type;
        this.intensity = intensity;
    }

    @NonNull
    public GameObjectId getFeelingBeing() {
        return feelingBeing;
    }

    public void setFeelingBeing(final GameObjectId feelingBeing) {
        this.feelingBeing = feelingBeing;
    }

    @NonNull
    public GameObjectId getTarget() {
        return target;
    }

    public void setTarget(final GameObjectId target) {
        this.target = target;
    }

    public FeelingTowardsType getType() {
        return type;
    }

    public void setType(final FeelingTowardsType type) {
        this.type = type;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
    }
}
