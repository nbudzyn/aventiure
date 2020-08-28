package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.Nullable;

import java.util.Set;

import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Ein einzelner Schritt, der im Rahmen einer Story (d.h. eines Mörchens o.Ä.)
 * erreicht werden kann.
 * <p>
 * Alle Implementierungen sollen {@link Enum}s sein.
 */
public interface IStoryNode {
    static int calcExpAchievementSteps(final Iterable<? extends IStoryNode> reachableStoryNodes) {
        int res = 0;

        for (final IStoryNode node : reachableStoryNodes) {
            res = Math.max(res, node.getExpAchievementSteps());
        }

        return res;
    }

    Story getStory();

    @Nullable
    GameObjectId getLocationId();

    Set<? extends IStoryNode> getPreconditions();

    /**
     * Wie viele Schritte (Aktionen) der Spieler brauchen sollte, bis er - ausgehend von der
     * letzten erreichten Story Node - diese Story Node erreicht hat.
     * Danach erhält der Spieler Tipps etc.
     * <p>
     * Der Wert sollte sich an drei Dingen orientieren:
     * <ul>
     * <li> An der Minimalzahl an Schritten (Aktionen), die man braucht, bis die Node seit
     * der letzten vorhergehenden Node erreicht ist
     * <li> An der Schwierigkeit des "Rätsels", das diese Node darstellt (Wie lange ist es
     * für den Spieler anregend, darüber zu grübeln?)
     * <li> An den Dingen, die der Spieler (seit der letzen Node) sonst so
     * Neues tun kann (vor allem für den Anfang relevant - oder wenn sich nach einer
     * Aktion neue Möglichkeiten in der Welt auftun)
     * </ul>
     */
    int getExpAchievementSteps();

    boolean beendetStory();
}
