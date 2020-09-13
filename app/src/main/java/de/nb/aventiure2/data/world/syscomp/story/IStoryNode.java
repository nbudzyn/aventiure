package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.Nullable;

import java.util.Set;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Ein einzelner Schritt, der im Rahmen einer Story (d.h. eines Mörchens o.Ä.)
 * erreicht werden kann.
 * <p>
 * Alle Implementierungen sollen {@link Enum}s sein.
 */
public interface IStoryNode {
    @FunctionalInterface
    interface IHinter {
        AvTimeSpan narrateAndDoHintAction(final AvDatabase db,
                                          NarrationDao n,
                                          final World world);
    }

    static int calcExpAchievementSteps(final Iterable<? extends IStoryNode> storyNodes) {
        int res = 0;

        for (final IStoryNode node : storyNodes) {
            @Nullable final Integer expAchievementSteps = node.getExpAchievementSteps();
            if (expAchievementSteps != null) {
                res = Math.max(res, expAchievementSteps);
            }
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
    @Nullable
    Integer getExpAchievementSteps();

    boolean beendetStory();

    default AvTimeSpan narrateAndDoHintAction(final AvDatabase db, final World world) {
        // Ziele:
        // - Spieler soll Interesse behalten
        // - Spieler soll einen Tipp fürs weitere Vorgehen gekommen
        // - Erwartungsmanagement für den Spieler
        // - Es soll Spannung aufgebaut werden

        // Grundideen für Tipps im weiteren Sinne:
        // - Der SC erinnert sich an etwas vor dem Spiel
        // - SC erinnert sich an etwas, das er erlebt hat
        // - Der SC hat einen Traum
        // - Dem SC fällt etwas an den Gegenständen auf, die er bei sich trägt
        // - Dem SC sieht oder riecht etwas an seinem konkreten Ort
        // - Dem SC fällt etwas an der Tageszeit auf
        // - Dem SC fällt etwas auf Basis seines Hungers oder seiner Müdigkeit auf
        // - Dem SC fällt etwas auf Basis seiner Laune (Mood) auf
        // - Vage emotionale Andeutungen (SC hat ein schlechtes Gefühl, wenn er an... denkt)
        // - Es kommt jemand vorbei (oder ist am Ort), der den SC auf einen Gedanken bringt
        // - Es kommen Leute vorbei (oder sind am Ort), bei denen der SC etwas mithört
        // - Es kommt jemand vorbei (oder ist am Ort), mit dem sich der SC unterhält
        // - Dem Spieler widerfährt ein Ereignis, dass zu weiteren Schritten motiviert
        // - Die Welt verändert sich

        // Inhaltlich:
        // - Inciting Incident (Spieler verliert etwas, Spieler / Welt ist bedroht...)
        // - Foreshadowing: Am Horizont braut sich der Konflikt zusammen,
        // Das Handeln des SCs wird dringender...
        // - Vage Drohungen: Mit ... wird es noch ein böses Ende nehmen
        // - Problem / Konflikt verbalisieren, vor dem der SC steht
        // - Dem Spieler ein Ziel anbieten.
        // - Dem Spieler eine moralische Verpflichtung unterschieben
        // - Auf den Ort, den Gegenstand oder die Person für den nächsten Schritt hindeuten
        // - Andeutung für den Lösungsansatz

        // Allgemeinere Ideen:
        // - Die Welt wird modifiziert, sodass eine andere Geschichte gestartet wird oder
        // weiterläuft, dass es also mehr zu erleben gibt
        // STORY Alternativ: Nichts tun, vielleicht passt es im nächsten Schritt besser

        @Nullable final IHinter hinter = getHinter();
        if (hinter == null) {
            return noTime();
        }
        return hinter.narrateAndDoHintAction(db, db.narrationDao(), world);
    }

    @Nullable
    IHinter getHinter();
}
