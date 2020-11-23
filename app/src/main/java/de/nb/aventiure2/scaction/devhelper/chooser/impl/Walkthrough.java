package de.nb.aventiure2.scaction.devhelper.chooser.impl;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Eine vordefinierte Aktionsfolge
 */
public class Walkthrough {
    // STORY Walkthrough anpassen!

    public static final Walkthrough ANFANG_BIS_FROSCHVERSPRECHEN =
            new Walkthrough(
                    "Die Kugel nehmen", "Das Schloss verlassen", "In den Wald gehen",
                    "Tiefer in den Wald hineingehen", "Auf dem Hauptweg tiefer in den Wald gehen",
                    "Die goldene Kugel hochwerfen", "Die goldene Kugel hochwerfen",
                    "Die goldene Kugel hochwerfen", "Heulen", "Heulen",
                    "Mit dem Frosch reden", "Dem Frosch Angebote machen",
                    "Dem Frosch alles versprechen");

    private static final Walkthrough FROSCHVERSPRECHEN_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN =
            new Walkthrough(
                    "Mit dem Frosch reden",
                    "Den Frosch mitnehmen",
                    "Die Kugel nehmen",
                    "Hinter dem Brunnen in die Wildnis schlagen",
                    "Früchte essen", "Die Kugel hinlegen", "Den Frosch absetzen",
                    "Mit dem Frosch reden",
                    "Den Frosch mitnehmen", "Die Kugel nehmen",
                    "Zum Brunnen gehen",
                    "Den Weg Richtung Schloss gehen",
                    "In Richtung Schloss gehen",
                    "Den schmalen Pfad aufwärtsgehen", // Frau!
                    "Die Frau nach ihrem Ziel fragen",
                    "Um den Turm herumgehen",
                    "Die Kugel an den Stamm eines Baumes legen",
                    "Den Pfad zurückgehen",
                    "Tiefer in den Wald hineingehen",
                    "In Richtung Schloss gehen",
                    "Den langen schmalen Pfad zum Turm aufwärtsgehen", // Frau
                    "Den Pfad zurückgehen",
                    "Den langen schmalen Pfad zum Turm aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Die Kugel nehmen",
                    "Aus dem Schatten der Bäume treten",
                    "Den Pfad zurückgehen",
                    "Tiefer in den Wald hineingehen",
                    "Den überwachsenen Abzweig nehmen",
                    "Die Kugel hinlegen", "Die Kugel nehmen",
                    "Auf den Waldweg zurückkehren",
                    "Den überwachsenen Abzweig zur Hütte nehmen",
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
                    "Auf den Waldweg zurückkehren",
                    "In Richtung Schloss gehen",
                    "Den Wald verlassen und in den Schloßgarten gehen",
                    "Das Schloss betreten");


    private static final Walkthrough SCHLOSSFEST_SCHLOSS_BETRETEN_BIS_PRINZABFAHRT =
            new Walkthrough("An einen Tisch setzen",
                    "Die Kugel auf den Tisch legen",
                    "Die Kugel nehmen",
                    "Eintopf essen",
                    "Den Frosch in die Hände nehmen",
                    "Den Frosch auf den Tisch setzen",
                    "Mit dem Frosch diskutieren",
                    "Eintopf essen",
                    "Vom Tisch aufstehen",
                    "Das Schloss verlassen und in den Schlossgarten gehen");

    private static final Walkthrough PRINZABFAHRT_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG =
            new Walkthrough("In den Wald gehen",
                    "Den langen schmalen Pfad zum Turm aufwärtsgehen",
                    "Um den Turm herumgehen",
                    "Die Kugel an den Stamm eines Baumes legen",
                    "Den Pfad zurückgehen",
                    "Den langen schmalen Pfad zum Turm aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten",
                    "Die Kugel nehmen",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten");

    private static final Walkthrough ERSTE_RAPUNZELRUF_BEOBACHTUNG_BIS_OBEN_BEI_RAPUNZEL =
            new Walkthrough(
                    "Rasten",
                    // Zauberin steigt die Zöpfe hoch
                    "Auf die magere Frau warten",
                    "Rufen: „Lass dein Haar herunter.“",
                    "Aus dem Schatten der Bäume treten",
                    "An den Haaren hinaufsteigen"
            );

    private static final Walkthrough OBEN_BEI_RAPUNZEL_BIS_ENDE =
            new Walkthrough(
                    "Die junge Frau bitten ihre Haare wieder hinunterzulassen",
                    "An den Haaren hinabsteigen",
                    "Rufen: „Lass dein Haar herunter.“",
                    "An den Haaren hinaufsteigen",
                    "Die goldene Kugel hochwerfen",
                    "Der jungen Frau die goldene Kugel geben",
                    "Der jungen Frau dein Herz ausschütten",
                    "Die junge Frau bitten ihre Haare wieder hinunterzulassen",
                    "An den Haaren hinabsteigen");

    public static final Walkthrough ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN =
            ANFANG_BIS_FROSCHVERSPRECHEN
                    .append(FROSCHVERSPRECHEN_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN);

    public static final Walkthrough ANFANG_BIS_PRINZABFAHRT =
            ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN
                    .append(SCHLOSSFEST_SCHLOSS_BETRETEN_BIS_PRINZABFAHRT);

    public static final Walkthrough ANFANG_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG =
            ANFANG_BIS_PRINZABFAHRT
                    .append(PRINZABFAHRT_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG);

    public static final Walkthrough ANFANG_BIS_OBEN_BEI_RAPUNZEL =
            ANFANG_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG
                    .append(ERSTE_RAPUNZELRUF_BEOBACHTUNG_BIS_OBEN_BEI_RAPUNZEL);

    public static final Walkthrough FULL =
            ANFANG_BIS_OBEN_BEI_RAPUNZEL
                    .append(OBEN_BEI_RAPUNZEL_BIS_ENDE);

    //  -- separate Walkthroughs --
    public static final Walkthrough SEP_ANFANG_BIS_MIT_FROSCH_BEI_RAPUNZEL =
            ANFANG_BIS_FROSCHVERSPRECHEN.append(new Walkthrough(
                    "Den Frosch mitnehmen", "Die Kugel nehmen",
                    "Den Weg Richtung Schloss gehen",
                    "In Richtung Schloss gehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten", // Frau kommt
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Rasten",
                    "Aus dem Schatten der Bäume treten",
                    "Rufen: „Lass dein Haar herunter.“",
                    "An den Haaren hinaufsteigen"
            ));

    private final ImmutableList<String> steps;

    private Walkthrough(final String... steps) {
        this(asList(steps));
    }

    private Walkthrough(final List<String> steps) {
        this.steps = ImmutableList.copyOf(steps);
    }

    private Walkthrough append(final Walkthrough other) {
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

    String getStep(final int index) {
        return steps.get(index);
    }
}
