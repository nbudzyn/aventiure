package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Component for a {@link GameObject}: The game object
 * has needs and feelings.
 */
public class FeelingsComp extends AbstractStatefulComponent<FeelingsPCD> {
    private final AvDatabase db;
    @NonNull
    private final Mood initialMood;
    @NonNull
    private final Hunger initialHunger;
    @NonNull
    private final AvDateTime initialZuletztGegessen;
    /**
     * Zeit, die es braucht, bis das FeelingBeing nach dem Essen wieder hungrig wird
     */
    private final AvTimeSpan zeitspanneNachEssenBisWiederHungrig;

    /**
     * Constructor for {@link FeelingsComp}.
     */
    public FeelingsComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        @NonNull final Mood initialMood,
                        @NonNull final Hunger initialHunger,
                        @NonNull final AvDateTime initialZuletztGegessen,
                        final AvTimeSpan zeitspanneNachEssenBisWiederHungrig) {
        super(gameObjectId, db.feelingsDao());
        this.db = db;
        this.initialMood = initialMood;
        this.initialHunger = initialHunger;
        this.initialZuletztGegessen = initialZuletztGegessen;
        this.zeitspanneNachEssenBisWiederHungrig = zeitspanneNachEssenBisWiederHungrig;
    }

    @Override
    @NonNull
    protected FeelingsPCD createInitialState() {
        return new FeelingsPCD(getGameObjectId(), initialMood, initialHunger,
                initialZuletztGegessen);
    }

    public boolean hasMood(final Mood mood) {
        return getMood() == mood;
    }

    @NonNull
    public Mood getMood() {
        return getPcd().getMood();
    }

    @NonNull
    public void setMood(final Mood mood) {
        getPcd().setMood(mood);
    }

    @NonNull
    public Hunger getHunger() {
        return getPcd().getHunger();
    }

    @NonNull
    public void setHunger(final Hunger hunger) {
        getPcd().setHunger(hunger);
    }

    public AvDateTime getWiederHungrigAb() {
        return getZuletztGegessen()
                .plus(getZeitspanneNachEssenBisWiederHungrig());
    }

    @NonNull
    public AvDateTime getZuletztGegessen() {
        return getPcd().getZuletztGegessen();
    }

    @NonNull
    public void setZuletztGegessen(final AvDateTime zuletztGegessen) {
        getPcd().setZuletztGegessen(zuletztGegessen);
    }

    public AvTimeSpan getZeitspanneNachEssenBisWiederHungrig() {
        return zeitspanneNachEssenBisWiederHungrig;
    }
}
