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
                    "Zum Boden hinabklettern",
                    "Auf den Baum klettern",
                    "Zum Boden hinabklettern",
                    "Zur Vorderseite der Hütte gehen",
                    "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                    "Ein Nickerchen machen",
                    "Aufstehen",
                    "Die Hütte verlassen",
                    "Um die Hütte herumgehen",
                    "Auf den Baum klettern",
                    "Zum Boden hinabklettern",
                    "Auf den Baum klettern",
                    "Zum Boden hinabklettern",
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
                    "Den schmalen Pfad aufwärtsgehen",
                    "Um den Turm herumgehen",
                    "Die Kugel an den Stamm eines Baumes legen",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten",
                    "Die Kugel nehmen",
                    "Aus dem Schatten der Bäume treten",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "Den Pfad zurückgehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten", // Zauberin steigt die Zöpfe hoch
                    "Aus dem Schatten der Bäume treten",
                    "Rufen: „Lass dein Haar herunter.“", // Zauberin verzaubert SC
                    "Um den Turm herumgehen",
                    "Den Pfad zurückgehen",
                    "Tiefer in den Wald hineingehen",
                    "Den überwachsenen Abzweig nehmen",
                    "Um die Hütte herumgehen",
                    "Auf den Baum klettern",
                    "Zum Boden hinabklettern",
                    "Zur Vorderseite der Hütte gehen",
                    "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                    "Aufstehen",
                    "Die Hütte verlassen",
                    "Um die Hütte herumgehen",
                    "Auf den Baum klettern",
                    "Zum Boden hinabklettern",
                    "Zur Vorderseite der Hütte gehen",
                    "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                    "Aufstehen",
                    "Die Hütte verlassen",
                    "Auf den Waldweg zurückkehren",
                    "In Richtung Schloss gehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten", // Zauberin ruft Rapunzel
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten", // Zauberin geht
                    "Rasten",
                    "Rufen: „Lass dein Haar herunter.“"
            );


    // STORY Seide auf Markt bei Frau für ein Goldstück kaufen, das man vom Prinzen bekommen hat.
    //  Oder Sterntaler / Münzen in Lichtung im Wald, die man brauchen kann, um Seide für Rapunzel zu
    //  kaufen.

    public static final Walkthrough ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN =
            ANFANG_BIS_FROSCHVERSPRECHEN
                    .append(FROSCHVERSPRECHEN_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN);

    public static final Walkthrough ANFANG_BIS_PRINZABFAHRT =
            ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN
                    .append(SCHLOSSFEST_SCHLOSS_BETRETEN_BIS_PRINZABFAHRT);

    public static final Walkthrough FULL =
            ANFANG_BIS_PRINZABFAHRT.append(PRINZABFAHRT_BIS_ENDE);

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
        return new Walkthrough(steps.subList(0, numSteps));
    }

    public int numSteps() {
        return steps.size();
    }

    public String getStep(final int index) {
        return steps.get(index);
    }
}
