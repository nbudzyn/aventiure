package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.Nullable;

import java.util.Set;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

/**
 * Ein einzelner Schritt, der im Rahmen einer Story (d.h. eines Mörchens o.Ä.)
 * erreicht werden kann.
 * <p>
 * Alle Implementierungen sollen {@link Enum}s sein.
 */
public interface IStoryNode {
    // STORY Neue Branches in Twine entwickeln

    // STORY Weitere Schritte in Twine modellieren, insbesondere mit dem Ziel, immer
    //  "neue Enden zu finden".
    //  Dazu weitere Teile aus den bestehenden Märchen ausschneiden als Twine-Knoten.
    //  Zusammenbinden der Knoten z.B. über die Charaktere: Was ist mit der Zauberin vorher
    //  passiert? Was passiert ihr nachher?
    //  Erst nur sehr grob, verfeinern / konsistent machen später.

    // IDEA Mein Herz hängt an der Erzählung durch die Maschine. Das sollte in jedem Schritt
    //  des Spiels enforcet werden. Es sollte also in jedem Schritt die Erzählung meaningful
    //   weitergehen. Der Benutzer sollte immer etwas tun können, was eine
    //   meaningful Erzählung fortsetzt (oder beginnt / beendet). (Das ist die Belohnung an den
    //   Spieler fürs Spielen.)
    //  Anschlussfragen:
    //  - Wie wird die Erzählung meaningful?
    //    - Braucht man einfach eine reiche Fülle von immer neuen Dingen, die geschehen?
    //    - Oder sollte der Spieler bei jedem neuen Erzählschritt immer mit den
    //      beschriebenen Elementen interagieren können? Und zwar so, dass die Interaktionen
    //      die Welt ändern? Oder spürbar Auswirkungen auf später haben?

    // IDEA Wie kann man eine meaningful Erzählung automatisch erzeugen?

    // IDEA "Fronten" ("Fronts") angelehnt an Dungeon World: Wenn der Spieler nicht
    //  interagiert, kommt es mehrstufig zu Eskalationen

    @FunctionalInterface
    interface IHinter {
        void narrateAndDoHintAction(final AvDatabase db,
                                    TimeTaker timeTaker, Narrator n,
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

    default void narrateAndDoHintAction(final AvDatabase db,
                                        final TimeTaker timeTaker,
                                        final Narrator n, final World world) {
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
            return;
        }
        hinter.narrateAndDoHintAction(db, timeTaker, n, world);
    }

    @Nullable
    IHinter getHinter();
}
