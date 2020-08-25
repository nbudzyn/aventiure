package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
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

    protected final NarrationDao n;

    // STORY Jedes FeelingBeing kann zu jedem anderen Feeling Being Gefühle verschiedener
    //  Stärke aufbauen. Default ist 0 (oder je ein anderer Wert für in-Group / out-Group,
    //  die letzlich den Persönlichkeit des Beings wieder geben. Es gibt eine Methode, die
    //  ermittelt, wer in-group ist)
    //  Bei Interaktionen werden die Gefühle aktualisiert, entweder durch eine dauerhafte
    //  Verstärkung oder durch einen Reset (Enttäuschung oder "ich habe mich in ihm getäuscht").
    //  Dazu gibt es
    //  - die Anzahl der Einzahlungen auf das Gefühl
    //  - die Summe
    //  (oder so ähnlich)
    //  Gefühle könnten zb sein
    //  - Zuneigung / Abneigung
    //  - Dankbarkeit / Rachedurst
    //  - Vertrauen / Misstrauen

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
        this.initialMood = initialMood;
        this.initialHunger = initialHunger;
        this.initialZuletztGegessen = initialZuletztGegessen;
        this.zeitspanneNachEssenBisWiederHungrig = zeitspanneNachEssenBisWiederHungrig;

        n = db.narrationDao();
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

    public void setMoodMin(final Mood mood) {
        if (getMood().isTraurigerAls(mood)) {
            getPcd().setMood(mood);
        }
    }

    public void setMoodMax(final Mood mood) {
        if (!getMood().isTraurigerAls(mood)) {
            getPcd().setMood(mood);
        }
    }

    public void setMood(final Mood mood) {
        getPcd().setMood(mood);
    }

    @NonNull
    public Hunger getHunger() {
        return getPcd().getHunger();
    }

    public void setHunger(final Hunger hunger) {
        getPcd().setHunger(hunger);
    }

    public AvDateTime getWiederHungrigAb() {
        return getZuletztGegessen()
                .plus(getZeitspanneNachEssenBisWiederHungrig());
    }

    @NonNull
    private AvDateTime getZuletztGegessen() {
        return getPcd().getZuletztGegessen();
    }

    public void setZuletztGegessen(final AvDateTime zuletztGegessen) {
        getPcd().setZuletztGegessen(zuletztGegessen);
    }

    private AvTimeSpan getZeitspanneNachEssenBisWiederHungrig() {
        return zeitspanneNachEssenBisWiederHungrig;
    }
}
