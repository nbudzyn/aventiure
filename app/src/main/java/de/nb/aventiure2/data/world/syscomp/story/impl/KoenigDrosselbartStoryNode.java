package de.nb.aventiure2.data.world.syscomp.story.impl;

public class KoenigDrosselbartStoryNode {
    // FIXME: Zielidee:
    //  - Eine Story-Engine plant eine Story (oder mehrere) nach
    //   bewährten Patterns (z.B. Heldenreise). Es stehen verschiedene Patterns
    //   zur Auswahl. Die Story-Engine mappt die Patterns auf die Welt (oder
    //   verändert die Welt entsprechend - sofern der Spieler sie nicht schon
    //   kennengelernt hat).
    //  - Wenn der Spieler eine Aktion auswählt, prüft die Story-Engine,
    //   ob die Story besser angepasst werden sollte (weil der Spieler eher
    //   in eine andere Richtung spielt). Entweder wird nur das Mapping auf die
    //   Welt angepasst (der Spieler spricht mit X, also erhlält er den
    //   Auftrag von X, nicht von Y; Spieler geht in Richtung..., also
    //   liegt der Gegenstand A initial dort und nicht woanders) - oder die
    //   (schon teilweise erlebte) Geschichte
    //   wird angepasst (keine Heldenreise, sondern etwas anderes). In jedem
    //   Fall sieht die Story-Engine immer eine vollständige Geschichte
    //   vor, die der SC noch erleben kann.
    //   Denkbar wäre auch, dass der StoryGenerator immer nur 1 Schritt voraus ist und
    //   jeweils eine Anzahl an reachable Story Nodes erzeugt, die alle noch zu
    //   eine wirklichen Geschichte führen können. Sobald ein Story Node
    //   erreicht wird, ermittelt die Story Engine die nächsten möglichen Story Nodes
    //   (unter Berücksichtigung der schon erreichten). Die Story Nodes müssen also
    //   ausreichen Informationen enthalten, dass die Story Engine ermitteln kann,
    //   welche (konkret realisierten) Geschichten noch möglich sind:
    //   Hat der SC bereits von X den Auftrag zur Heldenreise ABC erhalten, muss
    //   er letztlich auch ABC erreichen (oder etwas Überraschendes geschieht aus
    //   gutem erzählerischen Grund).
    //  - Dinge, die zufällig passieren, sollten die Geschichtenauswahl nicht
    //   einschränken. Nur weil der SC zufällig X trifft, sollte nicht Geschichte
    //   B ausgeschlossen sein.
    //  - Klären: Kann der Spieler immer nur eine Geschichte zur Zeit erleben?
    //  - (Vielleicht als 0. Schritt die Rapunzel-Geschichte zu Ende schreiben.)
    //  - Wenn das geklärt ist, könnten die StoryNodes etc. dynamisch
    //   gemacht werden (new StoryNode(...)).
    //  - Der nächste Schritt wäre vermutlich das "Rückmapping" von Aktionen in der
    //   Welt auf StoryNodes. Das ist an zwei Stellen relevant: Bei der Erkennung,
    //   ob ein StoryNode erreicht wurde, und beim Geben von Tipps.

    // FIXME Grundidee: Das Märchen spielt sich parallel ab - der SC gibt hin und wieder Anstoß zu
    //  einzelnen Entwicklungen

    // FIXME Wie kommt das mit dem Schlossfest überein?
    //  (Welche Schritte setzen ein Schlossfest voraus - oder passen nicht zu einem Schlossfest?
    //  Festlegen, was genau gefeiert wird?)

    //  --- Story Node: König (der aus dem Schloss) hat schöne Tochter, unverheiratet
    //  ---- Woher weiß der SC das? (Play, dont't tell)
    // FIXME SC sieht Tochter vor dem Schloss in einer Kutsche vorbeifahren. Es gibt ein Raunen 
    //  in der Menge
    //  "Wie schön sie ist" - "Und immer noch unverheiratet"
    //  
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  --- Story Node: Tochter weist Freier stolz ab und macht sich über sie lustig,
    //  auch "König Drosselbart", allgemein hochmütig und spöttisch ist
    //  ---- Woher weiß der SC das?
    // FIXME SC trifft abgelehnten Freier (König aus einem fernen Land?), wie er sich
    //  umbringen will. SC überredet ihn, davon abzulassen.
    //  (wenn nicht, wiederholt sich das ganze mit einem anderen Freier, der eine ist dünn,
    //  der andere dick, ...) Erst am Ende stellt er sich als König vor ("König Weinfass",
    //  "König Drosselbart", ...) - Letztlich intern immer dieselbe Figur
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  --- Story Node: König schwört: Tochter soll nächstbesten Bettler zum Mann nehmen
    //  --- Spielmann bettelt unter dem Fenster
    //  ---- Woher weiß der SC das?
    // FIXME SC kommt zum Schloss, vor dem Fenster (nicht während des Sturms!) singt ein Bettler,
    //  SC kommt der Bettler bekannt vor (es ist König Drosselbart) - aber kommt nicht
    //  darauf, woher.
    //  Wachen rufen aus dem Fenster heraus und bitten den Bettler herein?
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  --- Story Node: Bettler erhält die Tochter zur Frau, Tochter verbannt
    //  ---- Woher weiß der SC das?
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  --- Story Node: Tochter verkauft Töpfe.
    //  ---- Woher weiß der SC das?
    //FIXME -> "Dich der schönen Frau mit den Töpfen zuwenden"
    //  Du wendest dich der schönen jungen Frau zu. Gerade bezahlt ihr jemand ein angeschlagenes
    //  Schüsselchen - mit zarten Händen nimmt sie ein paar Heller Kreuzer Pfennige Gulden...
    //  entgegen.
    //  ... Goldene Kugel...
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    // FIXME SC erkennt sie.

    // FIXME "In diesem Augenblick..."

    //  --- Story Node: Trunkener Husar reitet die Töpfe in Scherben. tausend Scherben.
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    // FIXME Sie sagt: Es ... ihr nicht anders... Sie hat es sich selbst
    //  zuzuschreiben...

    //  Königstochter.) 2. Mal Husar... pdercKutsche

    //  - Sie weint, weiß nicht, was tun.

    // FIXME SC erfährt im Schloss (Gespräch mit Diener auf Schlossfest), dass noch eine weitere
    //  Küchenmagd hilfreich wäre
    //  Diener stellt Teller hin
    //  "Auffülllen musst dir selber" tuft er dir im Vorbehasten zu.
    //  --> Viel zu tun, hm? ... Jaja, noch zwei, drei Küchenmädchen mehr, ...
    //  --> ,Nichtt reagieren. Der Diener hastet? Vorbei
    //   Danach kann man das der Töpferin empfehlen.

    //  --> SC empfiehlt ihr, sich als Küchenmagd zu bewerben
    //  --> SC empfiehlt etwas anderes (was? Gänse hüten, vgl. Gänsemagd?!)
    //  --> Alternative: SC schwert sich nicht drum
    //  - Läuft dann weg (nach Hause)

    //  --- Story Node: Tochter wird Küchenmagd (bei ihrem Vater?)
    //  ---- Woher weiß der SC das?
    //  - SC trifft sie später wieder vor dem Schloss, sie erzählt, sie möchte Küchenmagd werden.
    //   (wenn SC das empfohlen hatte)
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  - SC trifft sie später wieder im Schloss, bedient am Tisch (oder sie sie z.B. Wasser ins
    //   Schloss tragen o.Ä.).

    //  --- Story Node: Feier im Schloss, Tochter steht dabei
    //  ---- Woher weiß der SC das?
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  --- Story Node: "König Drosselbart" kommt, will mit ihr tanzen, kurze Verwirrung,
    //  ---- er gibt sich zu erkennen, sie ist wieder in edlen Kreisen. Ende.
    //  ---- Woher weiß der SC das?
    //  ---- Dramatische Frage:
    //  ____ Wie gibt der SC Anstoß zur weiteren Entwicklung?

    //  SC erlebt mit, wie Königssohn (?) kommt und sie heiratet.


}
