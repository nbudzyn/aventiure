package de.nb.aventiure2.data.world.base;

import static java.util.Objects.requireNonNull;

import androidx.annotation.Nullable;

/**
 * Abstract super-class for {@link AbstractComponent}s that have mutable - and therefore
 * persistent - data.
 */
public abstract class AbstractStatefulComponent<PCD extends AbstractPersistentComponentData>
        extends AbstractComponent {
    private PCD pcd;

    private final IComponentDao<PCD> dao;

    protected AbstractStatefulComponent(final GameObjectId gameObjectId,
                                        final IComponentDao<PCD> dao) {
        super(gameObjectId);
        this.dao = dao;
    }

    @Override
    public void saveInitialState(final boolean unload) {
        pcd = createInitialState();
        doSave();

        if (unload) {
            pcd = null;
        }
    }

    protected abstract PCD createInitialState();

    @Override
    public final void load() {
        if (pcd == null) {
            doLoad();
        }
    }

    /**
     * Lädt die Daten dieser Komponente (neu) aus der Datenbank.
     */
    private void doLoad() {
        pcd = dao.get(getGameObjectId());
    }

    private void setUnchanged() {
        if (pcd == null) {
            return;
        }

        pcd.setChanged(false);
    }

    private boolean isChanged() {
        if (pcd == null) {
            return false;
        }

        return pcd.isChanged();
    }

    @Override
    public void saveIfChanged(final boolean unload) {
        if (isChanged()) {
            doSave();
            setUnchanged();
        }

        if (unload) {
            pcd = null;
        }
    }

    @Override
    public void delete() {
        doDelete();

        pcd = null;
    }

    /**
     * Speichert die Daten der Komponente in die Datenbank und löscht veränderliche Daten
     * aus dem Speicher.
     */
    private void doSave() {
        dao.insert(pcd);
    }

    /**
     * Löscht alle Daten dieser Komponente aus der Datenbank und aus dem Speicher.
     */
    private void doDelete() {
        dao.delete(getGameObjectId());
    }

    protected PCD requirePcd() {
        return requireNonNull(getPcd());
    }

    @Nullable
    private PCD getPcd() {
        return pcd;
    }
}
