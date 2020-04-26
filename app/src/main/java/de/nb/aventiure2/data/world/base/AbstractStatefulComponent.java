package de.nb.aventiure2.data.world.base;

/**
 * Abstract super-class for {@link AbstractComponent}s that have mutable - and therefore
 * persistent - data.
 */
public abstract class AbstractStatefulComponent<PCD extends AbstractPersistentComponentData>
        extends AbstractComponent {
    private enum InternalState {
        NOT_LOADED, LOADED
    }

    private InternalState internalState = InternalState.NOT_LOADED;

    private PCD pcd;

    private final IComponentDao<PCD> dao;

    protected AbstractStatefulComponent(final GameObjectId gameObjectId,
                                        final IComponentDao<PCD> dao) {
        super(gameObjectId);
        this.dao = dao;
    }

    @Override
    public void saveInitialState() {
        pcd = createInitialState();
        doSave();
    }

    protected abstract PCD createInitialState();

    @Override
    public final void load() {
        if (internalState == InternalState.NOT_LOADED) {
            doLoad();
        }
        internalState = InternalState.LOADED;
    }

    /**
     * Lädt die Daten dieser Komponente (neu) aus der Datenbank.
     */
    private void doLoad() {
        pcd = dao.get(getGameObjectId());
    }

    @Override
    public void save() {
        if (internalState != InternalState.NOT_LOADED) {
            doSave();
        }
        internalState = InternalState.NOT_LOADED;
    }

    /**
     * Speichert die Daten der Komponente in die Datenbank und löscht veränderliche Daten
     * aus dem Speicher.
     */
    private void doSave() {
        dao.insert(pcd);
        pcd = null;
    }

    public PCD getPcd() {
        return pcd;
    }
}
