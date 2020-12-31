package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;

@Immutable
public class HungerData {
    @NonNull
    private final Hunger hunger;

    @NonNull
    private final AvDateTime essenHaeltVorBis;

    public HungerData(final Hunger hunger, final AvDateTime essenHaeltVorBis) {
        this.hunger = hunger;
        this.essenHaeltVorBis = essenHaeltVorBis;
    }

    @NonNull
    public Hunger getHunger() {
        return hunger;
    }

    HungerData withHunger(final Hunger hunger) {
        return new HungerData(hunger, essenHaeltVorBis);
    }

    @NonNull
    AvDateTime getEssenHaeltVorBis() {
        return essenHaeltVorBis;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HungerData that = (HungerData) o;
        return hunger == that.hunger &&
                essenHaeltVorBis.equals(that.essenHaeltVorBis);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @NonNull
    @Override
    public String toString() {
        return "HungerData{" +
                "hunger=" + hunger +
                ", essenHaeltVorBis=" + essenHaeltVorBis +
                '}';
    }
}
