package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.Nullable;

import java.util.EnumSet;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Eine Teil-Geschichte (z.B. ein einzelnes Märchen). Besteht aus einzelnen
 * Schritten ({@link IStoryNode}s).
 */
public enum Story {
    // STORY Storys einschließlich der Story Nodes könnten auch generiert werden,
    //  basierend auf Story-Telling-Theorien.
    //  Aber dann können es natürlich keine Enums mehr sein!

    FROSCHKOENIG(FroschkoenigStoryNode.class,
            FroschkoenigStoryNode::checkAndAdvanceIfAppropriate),
    RAPUNZEL(RapunzelStoryNode.class,
            RapunzelStoryNode::checkAndAdvanceIfAppropriate);

    @FunctionalInterface
    interface IStoryAdvancer {
        @Nullable
        AvTimeSpan checkAndAdvanceIfAppropriate(
                final AvDatabase db,
                NarrationDao n,
                final World world);
    }

    private final Class<? extends IStoryNode> nodeClass;

    private final IStoryAdvancer advancer;

    Story(final Class<? extends IStoryNode> nodeClass,
          final IStoryAdvancer advancer) {
        this.nodeClass = nodeClass;
        this.advancer = advancer;
    }

    /**
     * Nicht alle Stories sind von Anfang an "verfügbar", und manchmal
     * kann der Spieler sie auch nur bis zu einem bestimmten Punkt spielen.
     * <ul>
     * <li>Diese Methode prüft, ob schon sehr häufig Tipps nötig waren, der Spieler also
     * trotz Tipps nicht oder nur langsam weiterkommt.
     * <li>Ist das der Fall, dann findet diese Methode eine passende Story
     * und "startet sie" oder "setzt sie weiter", sodass der SC in dieser anderen
     * Geschichte wieder Aktionsmöglichkeiten hat.
     * </ul>
     */
    @Nullable
    public static AvTimeSpan checkAndAdvanceAStoryIfAppropriate(
            final AvDatabase db,
            final NarrationDao n,
            final World world
    ) {
        for (final Story story : values()) {
            @Nullable final AvTimeSpan timeElapsedIfAppropriate =
                    story.checkAndAdvanceIfAppropriate(db, n, world);
            if (timeElapsedIfAppropriate != null) {
                return timeElapsedIfAppropriate;
            }
        }

        return null;
    }

    private AvTimeSpan checkAndAdvanceIfAppropriate(
            final AvDatabase db,
            final NarrationDao n,
            final World world) {
        return advancer.checkAndAdvanceIfAppropriate(db, n, world);
    }

    public <N extends Enum<N> & IStoryNode> EnumSet<N> getNodes() {
        return (EnumSet<N>) EnumSet.allOf((Class<? extends Enum>) getNodeClass());
    }

    public <N extends Enum<?> & IStoryNode> Class<N> getNodeClass() {
        return (Class<N>) nodeClass;
    }
}
