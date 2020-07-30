package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.time.AvDateTime;

/**
 * Base implementation for any object within in the game. An <i>entity</i> in the
 * entity-component-system pattern.
 */
public class GameObject implements IGameObject {
    private enum InternalState {
        NOT_LOADED, LOADED
    }

    private final GameObjectId id;

    private InternalState internalState = InternalState.NOT_LOADED;

    private final List<IComponent> components = new LinkedList<>();

    public GameObject(final GameObjectId id) {
        this.id = id;
    }

    /**
     * During construction, each {@link IComponent} must be added here.
     */
    protected <C extends IComponent> C addComponent(final C component) {
        components.add(component);
        return component;
    }

    /**
     * Speichert die initialen Daten des Game Objects (d.h. seiner Koponenten) in die Datenbank.
     */
    public void saveInitialState() {
        for (final IComponent component : components) {
            component.saveInitialState();
        }
    }

    public void scActionDone(final AvDateTime startTimeOfUserAction) {
        for (final IComponent component : components) {
            if (component instanceof ISCActionDoneListenerComponent) {
                load();

                ((ISCActionDoneListenerComponent) component)
                        .onSCActionDone(startTimeOfUserAction);
            }
        }
    }

    /**
     * Lädt alle Daten für dieses Game Object (d.h. die Daten aller seiner Komponenten)
     * aus der Datenbank - <i>es sei denn, sie wurden schon geladen</i>.
     */
    public void load() {
        if (internalState != InternalState.NOT_LOADED) {
            return;
        }

        for (final IComponent component : components) {
            component.load();
        }

        internalState = InternalState.LOADED;
    }

    /**
     * Speichert alle Daten für dieses Game Obect (d.h. die Daten aller seiner Komponenten)
     * in die Datenbank. Wenn das Objekt gar nicht geladen wurde, passiert nichts.
     */
    public void save(final boolean unload) {
        if (internalState == InternalState.NOT_LOADED) {
            return;
        }

        for (final IComponent component : components) {
            if (component.isChanged()) {
                component.save(unload);
            }
        }

        if (unload) {
            internalState = InternalState.NOT_LOADED;
        }
    }

    @Override
    public boolean is(final IGameObject... someAlternatives) {
        return Arrays.asList(someAlternatives).contains(this);
    }

    @Override
    public boolean is(final GameObjectId... someIdAlternatives) {
        return Arrays.asList(someIdAlternatives).contains(getId());
    }

    @Override
    public GameObjectId getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameObject that = (GameObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        if (internalState == InternalState.NOT_LOADED) {
            return id.toString();
        }

        if (this instanceof IDescribableGO) {
            return ((IDescribableGO) this).descriptionComp().toString();
        }

        return id.toString();
    }
}
