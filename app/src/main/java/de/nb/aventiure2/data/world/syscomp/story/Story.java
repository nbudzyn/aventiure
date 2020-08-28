package de.nb.aventiure2.data.world.syscomp.story;

import java.util.EnumSet;

import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode;

/**
 * Eine Teil-Geschichte (z.B. ein einzelnes Märchen). Besteht aus einzelnen
 * Schritten ({@link IStoryNode}s).
 */
public enum Story {
    // STORY Storys einschließlich der Story Nodes könnten auch generiert werden,
    //  basierend auf Story-Telling-Theorien.
    //  Aber dann können es natürlich keine Enums mehr sein!

    FROSCHKOENIG(FroschkoenigStoryNode.class),
    RAPUNZEL(RapunzelStoryNode.class);

    private final Class<? extends IStoryNode> nodeClass;

    Story(final Class<? extends IStoryNode> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public <N extends Enum<N> & IStoryNode> EnumSet<N> getNodes() {
        return (EnumSet<N>) EnumSet.allOf((Class<? extends Enum>) getNodeClass());
    }

    public <N extends Enum<?> & IStoryNode> Class<N> getNodeClass() {
        return (Class<N>) nodeClass;
    }
}
