package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;

import com.google.common.collect.Iterables;

import java.util.Objects;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class Konstituente {
    /**
     * Die eigentlichen Wörter und Satzzeichen
     */
    @Nonnull
    private final String string;

    /**
     * Ob noch ein Komma <i>vor dieser Konstituente</i> nötig ist. Alternativ kann ein Punkt,
     * ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon vorangehen, der ebenfalls das Komma "abdeckt".
     */
    private final boolean vorkommaNoetig;

    /**
     * Ob die wörtliche Rede noch "offen" ist.  Es steht also noch ein schließendes
     * Anführungszeichen aus. Wenn der Satz beendet wird, muss vielleicht außerdem
     * noch ein Punkt nach dem Anführungszeitchen gesetzt werden.
     */
    private final boolean woertlicheRedeNochOffen;

    /**
     * Ob noch ein Komma aussteht. Das Komma wird entweder unmittelbar folgen müssen -
     * oder es folgt ein Punkt, ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon, der ebenfalls das Komma "abdeckt".
     */
    private final boolean kommmaStehtAus;

    public static boolean woertlicheRedeNochOffen(final Iterable<Konstituente> konstituenten) {
        if (Iterables.isEmpty(konstituenten)) {
            return false;
        }
        return Iterables.getLast(konstituenten).woertlicheRedeNochOffen;
    }

    public static boolean kommaStehtAus(final Iterable<Konstituente> konstituenten) {
        if (Iterables.isEmpty(konstituenten)) {
            return false;
        }

        return Iterables.getLast(konstituenten).kommmaStehtAus;
    }

    public Konstituente withVorkommaNoetig(final boolean vorkommaNoetig) {
        return new Konstituente(string, vorkommaNoetig, woertlicheRedeNochOffen, kommmaStehtAus);
    }

    Konstituente withKommaStehtAus() {
        return k(string, woertlicheRedeNochOffen, true);
    }

    /**
     * Erzeugt eine Konstituente gemäß dieser Wortfolge.
     */
    public static Konstituente k(final @Nonnull Wortfolge wortfolge) {
        // Wenn die Wortfolge mit Komma anfängt, lassen wir es in der Konsituente stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()

        return new Konstituente(
                wortfolge.getString().trim(),
                false,
                wortfolge.woertlicheRedeNochOffen(),
                wortfolge.kommaStehtAus());
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat.
     */
    public static Konstituente k(final @Nonnull String string) {
        // Wenn der String mit Komma anfängt, lassen wir es in der Konsituente  stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()
        return k(string.trim(), false, false);
    }

    public static Konstituente k(final @Nonnull String string,
                                 final boolean woertlicheRedeNochOffen,
                                 final boolean kommaStehtAus) {
        return new Konstituente(string, kommaStehtAus, woertlicheRedeNochOffen, false);
    }

    private Konstituente(final String string, final boolean vorkommaNoetig,
                         final boolean woertlicheRedeNochOffen, final boolean kommmaStehtAus) {
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        requireNonNull(string, "string");
        checkArgument(!string.isEmpty(), "String ist empty");
        checkArgument(!string.startsWith(" "));
        checkArgument(!string.endsWith(" "));

        this.string = string;
        this.vorkommaNoetig = vorkommaNoetig;
        this.kommmaStehtAus = kommmaStehtAus;
    }

    public Konstituente capitalize() {
        return new Konstituente(GermanUtil.capitalize(string),
                // Wenn großgeschrieben werden soll, wäre es sinnlos, ein Komma zuvor
                // setzen zu vollen.
                false, woertlicheRedeNochOffen, kommmaStehtAus);
    }

    @Nonnull
    public String getString() {
        return string;
    }

    boolean vorkommaNoetig() {
        return vorkommaNoetig;
    }

    public boolean woertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen;
    }

    public boolean kommaStehtAus() {
        return kommmaStehtAus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Konstituente that = (Konstituente) o;
        // WICHTIG: Hier darf man nur den Text vergleichen, nicht die Kommata!
        // Ansonsten funktioniert das Ausschneiden nicht mehr richtig
        return string.equals(that.string);
    }

    @Override
    public int hashCode() {
        // WICHTIG: Hier darf man nur den Text eingehen lassen, damit die Methode
        // konsistent mit equals arbeitet.
        return Objects.hash(string);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()
                + ": \""
                + (vorkommaNoetig ? "[, ]" : "")
                + string
                + (kommmaStehtAus ? "[, ]" : "")
                + "\"";
    }
}
