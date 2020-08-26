package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Reagiert auf die Aktionen des SCs und managet dabei die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Im Wesentlichen gibt es drei Reaktionen:
 * <ul>
 * <li>Der Spieler erhält einen Tipp.
 * <li>Eine neue Story (ein neues Märchen) wird begonnen.
 * <li>(Es passiert nichts.)
 * </ul>
 */
public class StoryWebReactionsComp
        extends AbstractReactionsComp
        implements ISCActionReactions {
    public StoryWebReactionsComp(final AvDatabase db, final World world) {
        super(STORY_WEB, db, world);
    }

    @Override
    public AvTimeSpan afterScActionAndFirstWorldUpdate() {
        // STORY Es gibt Storys. Sie sind initial "nicht begonnen."
        //  Gewisse Trigger schalten eine neue Storys (z.B. ein neues Märchen)
        //  frei / starten sie: Gewisse Aktionen, sinnfreies
        //  Umherlaufen des Spielers, bestimmte Zeit, bestimmte Schrittzahl ohne Zustand...
        //  erreicht zu haben o.ä. Solche Triggerbedinungen werden in jedem Zug geprüft.
        //  Storys könnten eigene Klassen oder Enum-Werte sein.
        // STORY Wenn ein
        //  Story-Ziel erreicht wird, wird eine neue (möglichst abstrakte) Überschrift gesetzt und
        //  damit ein Kapitel begonnen. Die Überschrift bezieht sich lose auf einen der
        //  Storys, die der Spieler zuerst begonnen hat und die noch nicht abgeschlossen
        //  wurden. Für jede Story stehen mehrere Überschriften bereit, die in Reihenfolge
        //  gewählt werden.
        //  Storys einschließlich der Story Nodes könnten auch generiert werden,
        //  basierend auf Story-Telling-Theorien.

        // STORY Wenn alle Storys abgeschlossen sind, ist das spiel beendet ("lebst glücklich...")

        @Nullable final StoryNode openStoryNode = new StoryNode(db, world);

        if (openStoryNode == null) {
            return noTime();
        }

        return openStoryNode.narrateAndDoWhileOpen();
    }
}
