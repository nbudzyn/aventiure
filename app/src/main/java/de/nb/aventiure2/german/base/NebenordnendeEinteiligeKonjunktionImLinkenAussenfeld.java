package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Einteilige nebenordnende Konjunktionen ("Konnektoren", "Konjunktoren"), die im linken
 * Außenfeld von Verbzweitsätzen stehen können (im "Anschlussfeld", "Kjk", in der
 * "Anschlussposition"): "Und Peter ging."
 * <p>
 * Alle diese Konjunktionen können sowohl Verbzweitsätze verbinden als auch Verbletztsätze,
 * Verberstsätze, Prädikate, Nominalphrasen und Adjektivphrasen.
 */
@Immutable
public enum NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld
        implements IKonstituentenfolgable {
    // Die Konnektoren müssen nach "Aussagekraft" geordnet sein, beginnend mit der stärksten.
    ODER(false, "oder"),
    SONDERN(true, "sondern"),
    ABER(true, "aber"),
    UND(false, "und");

    // Hierzu würden eigentlich auch "doch" und "denn" gehören. Hier gibt es
    // allerdings Einschränkungen. Vergleiche
    // "Er ging vorsichtig und hatte Angst" mit
    // *"Er ging vorsichtig denn hatte Angst" und
    // ?"Er ging vorsichtig doch hatte Angst"
    // Außerdem kann "doch" nicht nur im linken Außenfeld stehen ("ich wartete lange, doch er
    // kam nicht"), sondern alternativ im Vorfeld ("ich wartete lange, doch kam er nicht").

    // Außerdem können die beiden Elemente einiger zweiteiliger Konjunkionen
    // im linken Außenfeld stehen:
    // - "Entweder kommt Peter oder Paul kommt" (Kommasetzung?)
    // - "Nicht nur Peter kommt, sondern auch Paul kommt"
    // - "Weder Peter kommt, noch Paul kommt" (Kommasetzung?)

    // "das heißt", "das bedeutet", "es scheint", ...  sind wohl nicht
    // wirklich Konjunktionen, sondern einfach Verben, die einen Verbzweitsatz als Ergänzung
    // fordern.

    // Es gibt einige weitere Konjunktionen, die nicht vor Verbzweitsätzen möglich sind:
    // WENNGLEICH(true, "wenngleich"), "wenn auch" ("wenn er auch keine Schmerzen spürt") etc.
    // Komparative Konjunktion "als": "Lieber fahre ich mit Paula, als ich mit Petra fahre."
    // (Kommasetzung)
    // Komparative Konjunktion "wie": "Ich fahre mit Paula genauso gern, wie ich mit Petra fahre
    // ." (Kommasetzung)

    private final boolean vorkommaNoetig;

    private final String text;

    public static boolean traegtBedeutung(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return anschlusswort != null && anschlusswort.traegtBedeutung();
    }

    /**
     * Gibt den "stärkeren" Konnektor der beiden zurück, also den Konnektor mit der
     * "stärkeren Aussagekraft": "Aber" ist beispielsweise stärker als "und".
     */
    @Nullable
    public static NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getStrongest(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld one,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld other) {
        if (one == null || one.compareTo(other) >= 0) {
            return other;
        }

        return other;
    }

    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld(
            final boolean vorkommaNoetig, final String text) {
        this.vorkommaNoetig = vorkommaNoetig;
        this.text = text;
    }

    /**
     * Gibt die eigentliche Konjunktion zurück - kleingeschrieben, ohne Vorkomma; der Aufrufer
     * muss sich darum kümmern, dass ein eventuell nötiges Vorkomma richtig gesetzt wird.
     */
    public String getText() {
        return text;
    }

    private boolean traegtBedeutung() {
        return compareTo(UND) < 0;
    }

    @Override
    @NonNull
    public Konstituentenfolge toKonstituentenfolge() {
        return joinToKonstituentenfolge(k(text).withVorkommaNoetig(vorkommaNoetig));
    }
}
