package de.nb.aventiure2.data.world.syscomp.story;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link StoryWebComp} component.
 */
@Dao
public abstract class StoryWebDao implements IComponentDao<StoryWebPCD> {
    @Override
    public void insert(final StoryWebPCD pcd) {
        insertInternal(pcd);

        deleteInternalStoryData(pcd.getGameObjectId());
        deleteInternalReachedStoryNodeData(pcd.getGameObjectId());

        for (final Map.Entry<Story, StoryData> entry :
                pcd.getStoryDataMap().entrySet()) {
            insert(pcd.getGameObjectId(), entry.getKey(), entry.getValue());
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertInternal(StoryWebPCD pcd);

    private void insert(final GameObjectId storyWeb, final Story story,
                        final StoryData storyData) {
        insert(new InternalStoryData(storyWeb, story, storyData.getState()));

        for (final Map.Entry<? extends IStoryNode, Integer> entry :
                storyData.getReachedNodes().entrySet()) {
            insert(new InternalReachedStoryNodeData(
                    storyWeb, story, entry.getKey().toString(), entry.getValue()));
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(InternalStoryData internalStoryData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(InternalReachedStoryNodeData internalReachedStoryNodeData);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM InternalStoryData WHERE :storyWeb = storyWeb")
    abstract void deleteInternalStoryData(GameObjectId storyWeb);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM InternalReachedStoryNodeData WHERE :storyWeb = storyWeb")
    abstract void deleteInternalReachedStoryNodeData(GameObjectId storyWeb);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an
     * dem Game Object gespeichert sind!
     */
    @Override
    public StoryWebPCD get(final GameObjectId storyWeb) {
        final StoryWebPCD res = getInternal(storyWeb);

        res.initStoryDataMap(toMap(
                getInternalStoryData(storyWeb),
                getInternalReachedStoryNodeData(storyWeb)));

        return res;
    }

    @Query("SELECT * from StoryWebPCD where :storyWeb = gameObjectId")
    public abstract StoryWebPCD getInternal(final GameObjectId storyWeb);

    private static Map<Story, StoryData> toMap(
            final List<InternalStoryData> internalStoryDataList,
            final List<InternalReachedStoryNodeData> internalReachedStoryNodeDataList) {
        final HashMap<Story, StoryData> res =
                new HashMap<>(internalStoryDataList.size());

        for (final InternalStoryData internalStoryData : internalStoryDataList) {
            final Story story = internalStoryData.getStory();
            final ImmutableMap.Builder<IStoryNode, Integer> reachedNodes =
                    ImmutableMap.builder();
            for (final InternalReachedStoryNodeData internalReachedStoryNodeData :
                    internalReachedStoryNodeDataList) {
                if (internalReachedStoryNodeData.getStory() == story) {
                    reachedNodes.put(
                            toStoryNode(
                                    story,
                                    internalReachedStoryNodeData.getStoryNode()),
                            internalReachedStoryNodeData.getStepReached()
                    );
                }
            }

            final StoryData storyData = new StoryData(
                    story,
                    internalStoryData.getState(),
                    reachedNodes.build());

            res.put(story, storyData);
        }

        return res;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static IStoryNode toStoryNode(final Story story, final String storyNodeName) {
        return (IStoryNode) Enum.valueOf(
                (Class<? extends Enum>) story.getNodeClass(),
                storyNodeName);
    }

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from InternalStoryData where :storyWeb = storyWeb")
    abstract List<InternalStoryData> getInternalStoryData(GameObjectId storyWeb);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from InternalReachedStoryNodeData where :storyWeb = storyWeb")
    abstract List<InternalReachedStoryNodeData> getInternalReachedStoryNodeData(
            GameObjectId storyWeb);
}
