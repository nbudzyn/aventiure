package de.nb.aventiure2.data.world.syscomp.story;

import java.util.EnumSet;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode;

/**
 * Eine Teil-Geschichte (z.B. ein einzelnes Märchen). Besteht aus einzelnen
 * Schritten ({@link IStoryNode}s).
 */
public enum Story {
    // IDEA Storys einschließlich der Story Nodes könnten auch generiert werden,
    //  basierend auf Story-Telling-Theorien.
    //  Aber dann können es natürlich keine Enums mehr sein!

    // IDEA Meine Story als "Front" (analog Dungeon World) betrachten?

    // IDEA Kernelemente der Geschichte sollten in der Environmet sichtbar sein. Schon zu 
    //  Beginn (2. Text o.ä.) und danach geändert.
    //  Zb: Flugblatt am Baum, dass den Verschwundenen Prinzen sucht. "Anschlag lesen".
    //  Wird erzeugt oncounterup (neues event oder parameterlambda)
    //  Oder Fußspuren unter Fenster oder es ist nass auf dem brunnen. Evtl. Über den
    //  Tipp-Mechanismus freischalten!!
    //  Verschwindet automatisch

    // IDEA Ein NSC könnte den SC darauf ansprechen, was er Tolles getan hat.
    //  Die Achievements des Spielers könnte GameObjects sein, die ein NSC
    //  wissen kann. Die NSC könnten dieses Wissen zu bestimmten Zeitpunkten
    //  erfahren - zum Beispiel, wenn sie jemand anderen treffen.
    //  Darauf ändern Sie die Kommunikation zum SC.

    FROSCHKOENIG(FroschkoenigStoryNode.class,
            (db, timeTaker, n, world) ->

            {
                return FroschkoenigStoryNode.checkAndAdvanceIfAppropriate(db, n, world);
            }),

    RAPUNZEL(RapunzelStoryNode.class, RapunzelStoryNode::checkAndAdvanceIfAppropriate);

    // IDEA Märchen zu 3 Act Structures adaptieren? (1. Akt nötig?)
    //  Inciting incident? Spieler bedrohen, dass "seine Welt" gefährdet
    //  sein könnte?
    //  Idee: Märchenelemente für ein neues Märchen in eine Plotstruktur
    //  (Plot Mountain, 8 Point Story Arc, Monomyth, ... beat Structure)
    //  verteilen, dann die Lücken finden und gemäß Plotstruktur füllen.
    //  Plotelemente stärker hervorheben, zb durch "sonst du wirklich" oder
    //  "aber sei gewarnt..."
    //  Am Anfang und dann regelmäßig "Mysteries" aufbringen, insbesondere dann,
    //  wenn eines gelöst wird. Der Spieler soll interessiert bleiben.
    //  Bewusst machen: was soll der Spieler letztlich aus der
    //  Geschichte mitnehmen? Call to action in der Welt??

    // IDEA Wie kommt man von einer Geschichte zu weiteren - ohne dass es langweilig wird?
    //  Wie kann man eine Geschichte verallgemeinern? Wie kann man eine
    //  Geschichte erzeugen?
    //  Sollte man eine Geschichte parametrisieren? Das Gemeinsame aus zwei Geschichten
    //  finden und A oder B oder etwas dazwischen wählen?
    //  Stories mit Storytelling Theorien in Bezug setzen (Schritt X = Phase Y). Wie
    //  könnte man eine Story aus einer Storytellingtheorie erzeugen? Was müsste man tun, um andere - ausreichend verschiedene Stories zu erzeugen?
    //  Zunächst so programmieren, dass neue Stories immer schneller programmiert
    //  sind (Blöcke / Wiederverwendung?)
    //  In Stories wichtige Punkte manuell austauschen (anderer NPC, amderer Ort,
    //  anderes Tier, andere Hintergrundgeschichte, andere Erlösung...). Dann
    //  automatisch wählen lassen?


    @FunctionalInterface
    interface IStoryAdvancer {
        boolean checkAndAdvanceIfAppropriate(
                final AvDatabase db,
                TimeTaker timeTaker, Narrator n,
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
    public static boolean checkAndAdvanceAStoryIfAppropriate(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n,
            final World world
    ) {
        for (final Story story : values()) {
            if (story.checkAndAdvanceIfAppropriate(db, timeTaker, n, world)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkAndAdvanceIfAppropriate(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n,
            final World world) {
        return advancer.checkAndAdvanceIfAppropriate(db, timeTaker, n, world);
    }

    public <N extends Enum<N> & IStoryNode> EnumSet<N> getNodes() {
        return (EnumSet<N>) EnumSet.allOf((Class<? extends Enum>) getNodeClass());
    }

    public <N extends Enum<?> & IStoryNode> Class<N> getNodeClass() {
        return (Class<N>) nodeClass;
    }
}
