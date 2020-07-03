package de.nb.aventiure2.scaction.devhelper.chooser.impl;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Eine vordefinierte Aktionsfolge
 */
public class Walkthrough {
    public static final Walkthrough ANFANG_BIS_FROSCHVERSPRECHEN =
            new Walkthrough(
                    "Die Kugel nehmen", "Das Schloss verlassen", "In den Wald gehen",
                    "Tiefer in den Wald hineingehen", "Auf dem Hauptweg tiefer in den Wald gehen",
                    "Die goldene Kugel hochwerfen", "Die goldene Kugel hochwerfen",
                    "Die goldene Kugel hochwerfen", "Heulen", "Heulen",
                    "Mit dem Frosch reden", "Dem Frosch Angebote machen",
                    "Dem Frosch alles versprechen");

    public static final Walkthrough FROSCHVERSPRECHEN_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN =
            new Walkthrough(
                    "Den Frosch mitnehmen",
                    "Die Kugel nehmen",
                    "Hinter dem Brunnen in die Wildnis schlagen",
                    "Früchte essen", "Die Kugel hinlegen", "Den Frosch absetzen",
                    "Mit dem Frosch reden",
                    "Den Frosch mitnehmen", "Die Kugel nehmen",
                    "Zum Brunnen gehen",
                    "Den Weg Richtung Schloss gehen",
                    "Den überwachsenen Abzweig nehmen",
                    "Um die Hütte herumgehen",
                    "Auf den Baum klettern",
                    "Zur Vorderseite der Hütte gehen",
                    "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                    "Ein Nickerchen machen",
                    "Aufstehen",
                    "Die Hütte verlassen",
                    "Um die Hütte herumgehen",
                    "Auf den Baum klettern",
                    "Auf den Baum klettern",
                    "Zur Vorderseite der Hütte gehen",
                    "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                    "Aufstehen",
                    "Die Hütte verlassen",
                    "Auf den Waldweg zurückkehren",
                    "In Richtung Schloss gehen",
                    "Den Wald verlassen",
                    "Das Schloss betreten");


    public static final Walkthrough SCHLOSSFEST_SCHLOSS_BETRETEN_BIS_PRINZABFAHRT =
            new Walkthrough("An einen Tisch setzen",
                    "Die Kugel auf den Tisch legen",
                    "Die Kugel nehmen",
                    "Eintopf essen",
                    "Den Frosch in die Hände nehmen",
                    "Den Frosch auf den Tisch setzen",
                    "Mit dem Frosch diskutieren",
                    "Eintopf essen",
                    "Vom Tisch aufstehen",
                    "Das Schloss verlassen");

    public static final Walkthrough PRINZABFAHRT_BIS_ENDE =
            new Walkthrough("In den Wald gehen",
                    "Den schmalen Pfad aufwärts gehen",
                    "Um den Turm herumgehen",
                    "Den Weg zurückgehen");
    // STORY Man kann sich am Turm (tagsüber) ausruhen,
    //  den Vögeln lauschen etc.

    // STORY tagsüber singt Rapinzel alle naselang: Rapunzel bekommt einen Status: STILL / SINGEND

    // STORY Konzept entwickeln, dass diese "Statusübergänge" realisiert:
    //  - Benutzer befindet sich sonstwo und Rapunzel beginnt zu singen
    //  - Benutzer befindet sich sonstwo und Rapunzel hört auf zu singen
    //  - Benutzer befindet sich wach an einem Ort und Rapunzel beginnt zu singen
    //  - Benutzer befindet sich wach an einem Ort und Rapunzel hört auf zu singen
    //  - Benutzer betritt einen Ort, wo Rapunzel bereits singt
    //  - Benutzer schläft ein, während Rapunzel singt und wacht auf und Rapunzel hat
    //    zwischenzeitlich aufgehört zu singen
    //  - Benutzer schläft ein, während Rapunzel nicht singt und wacht auf und Rapunzel hat
    //    zwischenzeitlich angefangen zu singen
    //  - Benutzer verlässt den Ort, während Rapunzel singt und kehrt zurück, wenn Rapunzel nicht
    //    mehr singt
    //  - Benutzer verlässt Rapunzel gut gelaunt und kehrt niedergeschlage zu Rapunzel zurück,
    //    Rapunzel reagiert auf den Wechsel

    // TODO Idee: Jede Reaktion speichert den letzten Zustand (PCD), auf Basis dessen sie einen
    //  Text gerendert hat sowie den Zeitpunkt dazu. Wenn wieder Gelegenheit ist, ein Text zu
    //  rendern, wird geprüft, ob sich der Status gegenüber dem Zeitpunkt geändert hat,
    //  außerdem wird geprüft, ob der Zeitpunkt Benutzer etwas versäumt hat oder die ganze
    //  Zeit anwesend und aufnahmefähig war - entsprechend etwas wie "Plötzlich endet der Gesang"
    //  oder "Es ist kein Gesang mehr zu hören" gerendert.

    // STORY Zum Beispiel wäre der Benutzer über alle Statusänderungen zu unterrichten,
    //  Die zwischenzeitlich passiert sind ("der Frosch ist verschwunden").

    // STORY Man könnte auch, wenn der Benutzer erstmals wieder nach draußem kommt, etwas
    //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
    //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
    //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
    //  werden.
    //  Man müsste also die Möglichkeit anbieten, jederzeit den Status eines bestimmten
    //  Game Objects unter einem "Label" zu persistieren (inkl. Zeitpunkt), so dass
    //  man ihn später wieder laden kann. Alternativ auch mehrere Game Objects,
    //  denn nur so kann man prüfen, was sich nach dem Schlafen an einem Ort verändert hat.

    // STORY Seide auf Markt bei Frau für ein Goldstück kaufen, das man vom Prinzen bekommen hat

    public static final Walkthrough ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN =
            ANFANG_BIS_FROSCHVERSPRECHEN
                    .append(FROSCHVERSPRECHEN_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN);

    public static final Walkthrough ANFANG_BIS_PRINZABFAHRT =
            ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN
                    .append(SCHLOSSFEST_SCHLOSS_BETRETEN_BIS_PRINZABFAHRT);

    public static final Walkthrough FULL =
            ANFANG_BIS_PRINZABFAHRT
                    .append(PRINZABFAHRT_BIS_ENDE);

    private final ImmutableList<String> steps;

    public Walkthrough(final String... steps) {
        this(asList(steps));
    }

    public Walkthrough(final List<String> steps) {
        this.steps = ImmutableList.copyOf(steps);
    }

    public Walkthrough append(final Walkthrough other) {
        return new Walkthrough(
                ImmutableList.<String>builder().addAll(steps).addAll(other.steps).build()
        );
    }

    public Walkthrough truncate(final int numSteps) {
        return new Walkthrough(steps.subList(0, numSteps()));
    }

    public int numSteps() {
        return steps.size();
    }

    public String getStep(final int index) {
        return steps.get(index);
    }
}
