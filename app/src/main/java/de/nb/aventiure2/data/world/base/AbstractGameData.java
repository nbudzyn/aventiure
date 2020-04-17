package de.nb.aventiure2.data.world.base;

import java.util.List;
import java.util.Objects;

/**
 * Data for an {@link GameObject} referenced by its {@link GameObjectId}.
 */
public abstract class AbstractGameData {
    private GameObjectId gameObjectId;

    protected AbstractGameData(final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    public static boolean contains(
            final List<? extends AbstractGameData> gameDataList,
            final GameObjectId gameObjectId) {
        for (final AbstractGameData gameData : gameDataList) {
            if (gameData.getGameObjectId().equals(gameObjectId)) {
                return true;
            }
        }

        return false;
    }

    public GameObjectId getGameObjectId() {
        return gameObjectId;
    }

    /**
     * @Deprecated Only for Android ROOM
     */
    @Deprecated
    public void setGameObjectId(final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractGameData that = (AbstractGameData) o;
        return Objects.equals(gameObjectId, that.gameObjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameObjectId);
    }

    @Override
    public String toString() {
        return "Data for game object " + gameObjectId;
    }
}