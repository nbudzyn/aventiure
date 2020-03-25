package de.nb.aventiure2.data.world.counter;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Counter {
    @PrimaryKey
    @NonNull
    private final String id;

    private final int value;

    public Counter(@NonNull final String id, final int value) {
        this.id = id;
        this.value = value;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }
}
