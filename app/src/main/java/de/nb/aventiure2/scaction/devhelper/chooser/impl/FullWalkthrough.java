package de.nb.aventiure2.scaction.devhelper.chooser.impl;

public class FullWalkthrough {
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
                    "Die Frau ansprechen",
                    "Die Frau nach ihrem Ziel fragen",
                    "Das Gespräch beenden",
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
                    "Den Wald verlassen und in den Schlossgarten gehen",
                    "Das Schloss betreten");
    public static final Walkthrough ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN =
            ANFANG_BIS_FROSCHVERSPRECHEN
                    .append(FROSCHVERSPRECHEN_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN);
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
    public static final Walkthrough ANFANG_BIS_PRINZABFAHRT =
            ANFANG_BIS_SCHLOSSFEST_SCHLOSS_BETRETEN
                    .append(SCHLOSSFEST_SCHLOSS_BETRETEN_BIS_PRINZABFAHRT);
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
                    "Auf die magere Frau warten",
                    "Auf die magere Frau warten");
    public static final Walkthrough ANFANG_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG =
            ANFANG_BIS_PRINZABFAHRT
                    .append(PRINZABFAHRT_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG);
    private static final Walkthrough ERSTE_RAPUNZELRUF_BEOBACHTUNG_BIS_OBEN_BEI_RAPUNZEL =
            new Walkthrough(
                    "Auf die magere Frau warten",
                    // Zauberin steigt die Zöpfe hoch
                    "Auf die magere Frau warten",
                    "Rufen: „Lass dein Haar herunter“",
                    "Aus dem Schatten der Bäume treten",
                    "An den Haaren hinaufsteigen"
            );
    public static final Walkthrough ANFANG_BIS_OBEN_BEI_RAPUNZEL =
            ANFANG_BIS_ERSTE_RAPUNZELRUF_BEOBACHTUNG
                    .append(ERSTE_RAPUNZELRUF_BEOBACHTUNG_BIS_OBEN_BEI_RAPUNZEL);
    private static final Walkthrough OBEN_BEI_RAPUNZEL_BIS_RAPUNZEL_RETTUNG_ZUSAGEN =
            new Walkthrough(
                    "Ein Gespräch mit der jungen Frau beginnen",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Dich mit der jungen Frau unterhalten",
                    "Dich mit der jungen Frau unterhalten",
                    "Dich mit der jungen Frau unterhalten",
                    "Dich mit der jungen Frau unterhalten",
                    "Der jungen Frau antworten: „Den Winter“",
                    "Der jungen Frau die goldene Kugel geben",
                    "Der jungen Frau die goldene Kugel geben",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Dich mit der jungen Frau unterhalten",
                    "Der jungen Frau die goldene Kugel geben",
                    "Der jungen Frau die goldene Kugel geben",
                    "Dir eine kleine Notlüge erlauben",
                    "Der jungen Frau dein Herz ausschütten",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Rapunzel fragen, wie du ihr helfen kannst",
                    "Rapunzel Rettung zusagen"
            );
    public static final Walkthrough ANFANG_BIS_RAPUNZEL_RETTUNG_ZUSAGEN =
            ANFANG_BIS_OBEN_BEI_RAPUNZEL
                    .append(OBEN_BEI_RAPUNZEL_BIS_RAPUNZEL_RETTUNG_ZUSAGEN);

    private static final Walkthrough RAPUNZEL_RETTUNG_ZUSAGEN_BIS_ENDE =
            new Walkthrough(
                    "Rapunzel bitten ihre Haare wieder hinunterzulassen",
                    "An den Haaren hinabsteigen",
                    "Den Pfad zurückgehen",
                    "Den Wald verlassen und in den Schlossgarten gehen",
                    "In den Wald gehen",
                    "Den Wald verlassen und in den Schlossgarten gehen",
                    "Das Holz nehmen",
                    "Das Klaubholz in Stücke brechen"
            );
    public static final Walkthrough FULL =
            ANFANG_BIS_RAPUNZEL_RETTUNG_ZUSAGEN
                    .append(RAPUNZEL_RETTUNG_ZUSAGEN_BIS_ENDE);

    private FullWalkthrough() {
    }
}
