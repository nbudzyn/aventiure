package de.nb.aventiure2.scaction.devhelper.chooser.impl;

public class NurRapunzelWalkthrough {
    public static final Walkthrough RAPUNZEL_HOEREN_UND_IN_DEN_WALD =
            new Walkthrough(
                    "Die Kugel nehmen",
                    "Das Schloss verlassen",
                    "In den Wald gehen",
                    "Den schmalen Pfad aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Aus dem Schatten der Bäume treten",
                    "Den Pfad zurückgehen",
                    "Tiefer in den Wald hineingehen"
            );
    private static final Walkthrough IM_WALD_BIS_OBEN_BEI_RAPUNZEL =
            new Walkthrough(
                    "In Richtung Schloss gehen",
                    "Die Frau ansprechen",
                    "Die Frau nach ihrem Ziel fragen",
                    "Das Gespräch beenden",  // Frau geht weg Richtung Turm
                    "Den langen schmalen Pfad zum Turm aufwärtsgehen",
                    "Den Pfad zurückgehen",
                    "Den langen schmalen Pfad zum Turm aufwärtsgehen",
                    "In den Schatten der Bäume setzen",
                    "Rasten", // Frau steigt vom Turm herunter und geht
                    "Rasten",
                    "Auf die magere Frau warten",
                    "Auf die magere Frau warten",
                    "Auf die magere Frau warten",
                    "Auf die magere Frau warten",
                    "Auf die magere Frau warten",
                    "Auf die magere Frau warten", // Frau kommt und ruft
                    "Auf die magere Frau warten", // Frau steigt vom Turm herunter und geht
                    "Aus dem Schatten der Bäume treten",
                    "Rufen: „Lass dein Haar herunter“",
                    "An den Haaren hinaufsteigen"
            );
    public static final Walkthrough BIS_OBEN_BEI_RAPUNZEL =
            RAPUNZEL_HOEREN_UND_IN_DEN_WALD
                    .append(IM_WALD_BIS_OBEN_BEI_RAPUNZEL);
    private static final Walkthrough OBEN_BEI_RAPUNZEL_BIS_ENDE_RAPUNZEL =
            new Walkthrough(
                    "Die goldene Kugel hochwerfen",
                    "Der jungen Frau die goldene Kugel geben",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Dich mit der jungen Frau unterhalten",
                    "Die junge Frau bitten ihre Haare wieder hinunterzulassen",
                    "Die goldene Kugel hochwerfen",
                    "An den Haaren hinabsteigen",
                    "Rufen: „Lass dein Haar herunter“",
                    "An den Haaren hinaufsteigen",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Das Gespräch mit der jungen Frau fortsetzen",
                    "Dich mit der jungen Frau unterhalten",
                    "Der jungen Frau antworten: „Den Herbst“",
                    "Dich mit der jungen Frau unterhalten",
                    "Dich mit der jungen Frau unterhalten",
                    "Der jungen Frau die goldene Kugel geben",
                    "Die Wahrheit sagen",
                    "Die junge Frau nach ihrem Namen fragen",
                    "Rapunzel dein Herz ausschütten",
                    "Rapunzel fragen, wie du ihr helfen kannst",
                    "Rapunzel Rettung zusagen",
                    "Das Gespräch mit Rapunzel fortsetzen",
                    "Rapunzel nach der mageren Frau fragen",
                    // Kaum, dass man von ihr spricht,
                    // steht die Zauberin auf einmal unten vor dem Turm
                    // und ruft nach Rapunzel!
                    "Unter das Bett kriechen",
                    "Still daliegen",
                    "Still daliegen",
                    "Still daliegen",
                    "Unter dem Bett hervorkriechen",
                    "Ein Gespräch mit Rapunzel beginnen",
                    "Rapunzel bitten ihre Haare wieder hinunterzulassen",
                    "An den Haaren hinabsteigen",
                    "Den Pfad zurückgehen",
                    "Tiefer in den Wald hineingehen",
                    "Auf dem Hauptweg tiefer in den Wald gehen",
                    "Die goldene Kugel hochwerfen",
                    "Die goldene Kugel hochwerfen",
                    "Die goldene Kugel hochwerfen",
                    "Hinter dem Brunnen in die Wildnis schlagen",
                    "Früchte essen",
                    "Zum Brunnen gehen",
                    "Den Weg Richtung Schloss gehen",
                    "Den überwachsenen Abzweig nehmen",
                    "Um die Hütte herumgehen",
                    "Auf den Baum klettern",
                    "Zum Boden hinabklettern",
                    "Zur Vorderseite der Hütte gehen",
                    "Die Hütte betreten",
                    "In das Bett legen",
                    "Ein Nickerchen machen",
                    "Aufstehen",
                    "Die Hütte verlassen",
                    "Auf den Waldweg zurückkehren",
                    "In Richtung Schloss gehen",
                    "Den Wald verlassen und in den Schlossgarten gehen",
                    "Das Holz nehmen",
                    "Das Klaubholz in Stücke brechen",
                    "In den Wald gehen",
                    "Tiefer in den Wald hineingehen",
                    "Den überwachsenen Abzweig zur Hütte nehmen",
                    "In den Garten hinter der Hütte gehen",
                    "Hinter dem Garten weitergehen",
                    "Einige Binsen ausrupfen",
                    "Einige Binsen ausrupfen",
                    "Einige Binsen ausrupfen",
                    "Den Hang zum verwilderten Garten hinaufsteigen",
                    "Zur Vorderseite der Hütte gehen",
                    "Auf den Waldweg zurückkehren",
                    "In Richtung Schloss gehen",
                    "Den Wald verlassen und in den Schlossgarten gehen");

    public static final Walkthrough NUR_RAPUNZEL =
            BIS_OBEN_BEI_RAPUNZEL
                    .append(OBEN_BEI_RAPUNZEL_BIS_ENDE_RAPUNZEL);

    private NurRapunzelWalkthrough() {
    }
}
