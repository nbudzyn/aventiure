package de.nb.aventiure2.data.world.base;

/**
 * Android ROOM DAO for the mutable state of an {@link IComponent}.
 */
public interface IComponentDao<PCD extends AbstractPersistentComponentData> {
    void insert(PCD pcd);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    PCD get(GameObjectId gameObjectId);

    void delete(GameObjectId gameObjectId);
}
