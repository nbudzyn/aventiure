package de.nb.aventiure2.data.world.syscomp.reaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;

/**
 * Component für ein {@link GameObject}: The game object might
 * react to certain events.
 */
public abstract class AbstractReactionsComp extends AbstractStatelessComponent
        implements IReactions {
    protected final AvDatabase db;
    protected final World world;

    protected final StoryStateDao n;

    // STORY Jeder Creature (jedem NPC) ein Ziel geben - oder in jedem NPC
    //  ein "Potenzial" anlegen (durch seine Werte oder Möglichkeiten),
    //  dass er im Rahmen seiner KI ausschöpfen möchte.

    // STORY Zu jedem Schritt einen Konflikt / ein Hindernis vorsehen: Gegenspieler, Rivale o.Ä.

    // STORY Der Wald kämpft (nachts) gegen den Spieler. Als wäre er böse.

    // STORY Player should care about their character / stuff / achievements / reputation.

    // STORY Es gibt Tasks / Quests / Geschichten. Sie sind initial "nicht begonnen."
    //  Gewisse Trigger schalten einen neuen Task (z.B. ein neues Märchen)
    //  frei / starten ihn: Gewisse Aktionen, sinnfreies
    //  Umherlaufen des Spielers, bestimmte Zeit, bestimmte Schrittzahl ohne Zustand...
    //  erreicht zu haben o.ä. Solche Triggerbedinungen werden in jedem Zug geprüft.
    //  Tasks / Quests / Geschichten könnten eigene Klassen oder Enum-Werte sein.
    //  Jeder Task besteht aus einzelnen Schritten / Steps / Geschichtsschritten /
    //  Geschichtsmeilensteinen (jeder Geschichtsmeilenstein gehört zu einer Geschichte).
    //  Wenn ein Step erreicht wird, wird "markiert", dass der Schritt erreicht wurde,
    //  mit Schrittzähler.
    //  Jeder Geschichtsmeilenstein kann Voraussetzungen haben. Voraussetzungen sind andere
    //  Geschichtsmeilensteine - oder alternativ (oder zusätzlich) könnten die Voraussetzungen für
    //  Geschichtsmeilensteine auch beliebige Prüfungen sein.
    //  Wenn ein
    //  Task-Ziel erreicht wird, wird eine neue (möglichst abstrakte) Überschrift gesetzt und
    //  damit ein Kapitel begonnen. Die Überschrift bezieht sich lose auf einen der
    //  Task, die der Spieler zuerst begonnen hat und die noch nicht abgeschlossen
    //  wurden. Für jeden Task stehen mehrere Überschriften bereit, die in Reihenfolge
    //  gewählt werden.
    //  Tasks einschließlich der Geschichtsmeilensteine könnten auch generiert werden,
    //  basierend auf Story-Telling-Theorien.

    // TODO Es könnte einen extra Knopf geben, unter dem der benutzer in einem Baum (oder
    //  einem Graphen) sieht,
    //  welche Meilensteine er bisher erreicht hat. Die baumstruktur ordnet sich nach den
    //  Voraussetzungen der meilensteine voneinander.
    // STORY Wenn alle meilensteine erreicht sind ist das spiel beendet ("lebst glücklich...")
    // TODO Das Programm kann oben in der leiste jederzeit einen prozentsatz anzeigen, wie
    //  viele meilensteine der benutzer schon erreicht hat.

    public AbstractReactionsComp(final GameObjectId id,
                                 final AvDatabase db,
                                 final World world) {
        super(id);
        this.db = db;
        this.world = world;

        n = db.storyStateDao();
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }
}
