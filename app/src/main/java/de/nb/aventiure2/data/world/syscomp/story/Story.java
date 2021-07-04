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
 * <p>
 * Storys sind in gewisser Weise mit "Missionen" vergleichbar.
 */
public enum Story {
    // IDEA Charged Situations. Der SC muss interagieren, es könnte zu allen möglichen 
    //  Ergebnissen / Geschehnissen kommen (pirate ship crashed on mars). Auch, wenn der SC 
    //  passiv reagiert, hat das Auswirkungen (die er später zu sehen bekommt.

    // IDEA Der Spieler(charakter) als Plot-Magnet: Dinge so einrichten, dass sie
    //  - was ein Zufall! - genau dem Spieler passieren / dort passieren, wo der
    //  Spieler ist, dann passieren, wenn der Spieler es erlebt etc.
    //  Wenn das nicht möglich ist, dann Dinge (Veränderungen), die passieren,
    //  Auswirkungen haben lassen, die der SC (bald danach) mitbekommt.
    //  Die Ereignisse müssen bereits in Gang sein, wenn der SC daherkommt. Der SC kommt zufällig 
    //  gerade zum / kurz vor dem entscheidenden Zeitpunkt daher! ("Wir wollten gerade das Schloss
    //  stürmen und das Klnigreich übernehnen).

    // IDEA Die NSCs treiben die Handlung! Den SC nicht zur Geschichte zwingen - er tut, was er mag,
    //  hat aber manchmal nur Möglichkeiten im Rahmen der Handlung.

    // IDEA https://www.technicalgrimoire.com/downloads - alternative Ideen zum Tod

    // FIXME 1W6-Freunde Abenteuergenerator-Tabelle

    // FIXME Idee für Märchenumsetzung: von hinten Beginnen! Was ist das Ziel?
    //  Was sind die Zwischenziele? Was sind die Aktionen?
    //  Immer feiner, das aus dem Märchen nehmen, was geht, sonst improvisieren.

    // IDEA Alle Dinge sollten schiefgehen können.
    //  - Wenn etwas schiefgeht, was ist die (logsiche) Konsequenz (in der Welt)?
    //    (Kein "geht noch nicht" vs. "try again")
    //  - Wie entscheidet sich (ohne Zufall :-) ), wann etwas schief geht?

    // IDEA Storys einschließlich der Story Nodes könnten auch generiert werden,
    //  basierend auf Story-Telling-Theorien.
    //  Aber dann können es natürlich keine Enums mehr sein!

    // IDEA Kernelemente der Geschichte sollten im Environment sichtbar sein. Schon zu
    //  Beginn (2. Text o.ä.) und danach geändert.
    //  Zb: Flugblatt am Baum, dass den Verschwundenen Prinzen sucht. "Anschlag lesen".
    //  Wird erzeugt oncounterup (neues event oder parameterlambda)
    //  Oder Fußspuren unter Fenster oder es ist nass auf dem brunnen. Evtl. Über den
    //  Tipp-Mechanismus freischalten!!
    //  Verschwindet automatisch

    // IDEA Man könnte gewisse Spezialfertigkeiten erhalten. (Ähnlichr 6e kommen durch die ganze
    //  Welt o.ä.).

    // IDEA Ein NSC könnte den SC darauf ansprechen, was er Tolles getan hat.
    //  Die Achievements des Spielers könnte GameObjects sein, die ein NSC
    //  wissen kann. Die NSC könnten dieses Wissen zu bestimmten Zeitpunkten
    //  erfahren - zum Beispiel, wenn sie jemand anderen treffen.
    //  Darauf ändern Sie die Kommunikation zum SC.

    // TODO Anfrage nach Storytelling / Narrative Designer bei Github einstellen?
    //  Inhalt: Storytelling Grimms Märchen deutsch rein textbasiert, kein Zufall
    //  (kein Auswürfeln), aber simulierte Welt

    // FIXME Trophäen in Hütte sammeln? Goldene Kugel, sträne Rapunzel... Tischlein-Devk-Dich,
    //  Bettbecke,...
    //  Prinz schenkt Spieler etwas hübsches (Umhang?!), SC erhält dafür von Rapunzel ein
    //  Kompliment oder Rapunzel ist gleich zugeneigter...

    FROSCHKOENIG(FroschkoenigStoryNode.class,
            (db, timeTaker, n, world) -> FroschkoenigStoryNode.checkAndAdvanceIfAppropriate()),

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
    //  könnte man eine Story aus einer Storytellingtheorie erzeugen? Was müsste man tun, um
    //  andere - ausreichend verschiedene Stories zu erzeugen?
    //  Zunächst so programmieren, dass neue Stories immer schneller programmiert
    //  sind (Blöcke / Wiederverwendung?)
    //  In Stories wichtige Punkte manuell austauschen (anderer NPC, amderer Ort,
    //  anderes Tier, andere Hintergrundgeschichte, andere Erlösung...). Dann
    //  automatisch wählen lassen?

    // IDEA Märchen Nummer 6 ("Der treue Johannes") umsetzen
    //  - Der Königssohne ist der Froschkönig (er ruft z.B. den SC zu sich o.Ä.)
    //  - Der SC ist der dreue Johannes
    //  - Der SC geht mit der Goldenen Kugel zur Prinzessin
    //  - Er wird versteinert (entweder teilweise, wenn er Teilinformationen gibt,
    //   oder ganz am Ende auf dem Schaffot).
    //  - Er wacht wieder auf und sieht z.B. an einer verwelkten Blume, dass er
    //   wiedererweckt worden ist.

    // IDEA Märchen Nummer 29 ("Der Teufel mit den drei Goldenen Haaren"), aber nur
    //  die ersten 2 Rätsel, evtl die Wachen

    // IDEA Märchen Nummer 40: Schatz wurde geraubt, Alte gibt einem Tipps, hinterher wird
    //  der Räuberhauptmann irgendwo mit ser Geschichzhte enttarnt..
    //  Wenn man doch entdeckt wird: "Nur ein Dreikäaehoch, der ist es nicht wert, dass man
    //  ihn in den Suppentopf wirft", man wird laufen gelassen

    // FIXME Risiko angehen: Es gibt kaum geeignete Märchen
    //  Lösung: 2 weitere geeignete Märchen finden (ab Nummer 46),
    //  Umsetzung hier grob anskizzieren.

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <N extends Enum<N> & IStoryNode> EnumSet<N> getNodes() {
        return (EnumSet<N>) EnumSet.allOf((Class<? extends Enum>) getNodeClass());
    }

    @SuppressWarnings("unchecked")
    public <N extends Enum<?> & IStoryNode> Class<N> getNodeClass() {
        return (Class<N>) nodeClass;
    }
}
