package de.nb.aventiure2.data.world.base;

/**
 * Android ROOM DAO for the mutable state of an {@link IComponent}.
 */
public interface IComponentDao<PCD extends AbstractPersistentComponentData> {
    void insert(PCD pcd);

    PCD get(GameObjectId gameObjectId);
}
