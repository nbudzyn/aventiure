package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.string.GermanStringUtil;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

@Immutable
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
     * ein Semikolon, das ebenfalls das Komma "abdeckt".
     */
    private final boolean kommmaStehtAus;

    /**
     * Wenn nicht <code>null</code>, dann kann diese Konstituente also Bezugsobjekt
     * (oder ihrerseits als anaphorischer Bezug auf ein Bezugsobjekt) verstanden werden,
     * dass sich in diesem Numerus und Genus befindet.
     * <p>
     * Diese Angabe ist wichtige, damit doppeldeutige / falsche Bezüge verhindert werden können.
     */
    @Nullable
    private final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer;

    /**
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen. Dazu muss dieses Pronomen (in aller Regel)
     * den Numerus und Genus besitzen, die in {@link #kannAlsBezugsobjektVerstandenWerdenFuer}
     * angegeben sind. - Jedenfalls muss <code>kannAlsBezugsobjektVerstandenWerdenFuer</code>
     * gesetzt sein, wenn <code>bezugsobjekt</code> gesetzt ist.
     */
    @Nullable
    private final IBezugsobjekt bezugsobjekt;

    public Konstituente withVorkommaNoetig(final boolean vorkommaNoetig) {
        return new Konstituente(string, vorkommaNoetig, woertlicheRedeNochOffen, kommmaStehtAus,
                kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    Konstituente withKommaStehtAus() {
        return k(string, woertlicheRedeNochOffen, true, kannAlsBezugsobjektVerstandenWerdenFuer,
                bezugsobjekt
        );
    }

    /**
     * Erzeugt eine Konstituente gemäß dieser Wortfolge. Dabei wird davon ausgegangen:
     * Wenn in der Wortfolge kein Bezugsobjekt angegeben ist, dann enthält sie auch
     * keine Objekte, die als Bezugsobjekt verstanden werden könnten.
     */
    public static Konstituente k(final @Nonnull Wortfolge wortfolge) {
        return k(wortfolge,
                wortfolge.getPhorikKandidat() != null ?
                        wortfolge.getPhorikKandidat().getNumerusGenus() :
                        null);
    }

    /**
     * Erzeugt eine Konstituente gemäß dieser Wortfolge.
     *
     * @param kannAlsBezugsobjektVerstandenWerdenFuer Wenn in der Wortfolge ein Bezugsobjekt
     *                                                angegeben ist, muss dessen Numerus und Kasus
     *                                                mit dieser Angabe übereinstimmen.
     */
    public static Konstituente k(final @Nonnull Wortfolge wortfolge,
                                 @Nullable
                                 final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer) {
        // Wenn die Wortfolge mit Komma anfängt, lassen wir es in der Konsituente stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()

        return new Konstituente(
                wortfolge.getString().trim(),
                false,
                wortfolge.woertlicheRedeNochOffen(),
                wortfolge.kommaStehtAus(),
                kannAlsBezugsobjektVerstandenWerdenFuer,
                wortfolge.getPhorikKandidat() != null ?
                        wortfolge.getPhorikKandidat().getBezugsobjekt() :
                        null
        );
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat. Die Konstituente enthält keinen
     * Phorik-Kandidaten und kann auch nicht als phorischer Bezug verstanden werden
     * (man kann also nicht nachfolgende "sie", "ihm" oder "es" schreiben und diese
     * Konsituente meinen).
     */
    public static Konstituente k(final @Nonnull String string) {
        return k(string, null, null);
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat. Die Konstituente enthält keinen
     * Phorik-Kandidaten.
     */
    public static Konstituente k(final @Nonnull String string,
                                 @Nullable
                                 final NumerusGenus koennteAlsBezugsobjektVerstandenWerdenFuer) {
        return k(string, koennteAlsBezugsobjektVerstandenWerdenFuer, null);
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat.
     */
    public static Konstituente k(final @Nonnull String string,
                                 @Nullable
                                 final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer,
                                 @Nullable final IBezugsobjekt bezugsobjekt) {
        // Wenn der String mit Komma anfängt, lassen wir es in der Konsituente  stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()
        return k(string.trim(), false, false, kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt
        );
    }

    /**
     * Erzeugt eine Konstituente, die nicht als phorischer Bezug verstanden werden
     * kann (man kann also nicht nachfolgende "sie", "ihm" oder "es" schreiben und diese
     * Konsituente meinen).
     */
    public static Konstituente k(final @Nonnull String string,
                                 final boolean woertlicheRedeNochOffen,
                                 final boolean kommaStehtAus) {
        return k(string, woertlicheRedeNochOffen, kommaStehtAus,
                null, null);
    }

    public static Konstituente k(final @Nonnull String string,
                                 final boolean woertlicheRedeNochOffen,
                                 final boolean kommaStehtAus,
                                 @Nullable
                                 final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer,
                                 @Nullable final IBezugsobjekt bezugsobjekt) {
        return new Konstituente(string, kommaStehtAus, woertlicheRedeNochOffen, false,
                kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    public static Konstituente k(final @Nonnull String string,
                                 final boolean woertlicheRedeNochOffen,
                                 final boolean kommaStehtAus,
                                 @Nullable final
                                 NumerusGenus koennteAlsBezugsobjektVerstandenWerdenFuer) {
        return new Konstituente(string, kommaStehtAus, woertlicheRedeNochOffen, false,
                koennteAlsBezugsobjektVerstandenWerdenFuer, null);
    }

    Konstituente(final String string, final boolean vorkommaNoetig,
                 final boolean woertlicheRedeNochOffen, final boolean kommmaStehtAus,
                 @Nullable final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer,
                 @Nullable final IBezugsobjekt bezugsobjekt) {
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        requireNonNull(string, "string");
        checkArgument(!string.isEmpty(), "String ist empty");
        checkArgument(!string.startsWith(" "));
        checkArgument(!string.endsWith(" "));
        checkArgument(bezugsobjekt == null ||
                        kannAlsBezugsobjektVerstandenWerdenFuer != null,
                "Bezugsobjekt angegeben, aber kein Numerus / Genus! Bezugsobjekt: "
                        + bezugsobjekt);

        this.string = string;
        this.vorkommaNoetig = vorkommaNoetig;
        this.kommmaStehtAus = kommmaStehtAus;
        this.bezugsobjekt = bezugsobjekt;
        this.kannAlsBezugsobjektVerstandenWerdenFuer =
                kannAlsBezugsobjektVerstandenWerdenFuer;
    }

    public Konstituente capitalize() {
        return new Konstituente(GermanStringUtil.capitalize(string),
                // Wenn großgeschrieben werden soll, wäre es sinnlos, ein Komma zuvor
                // setzen zu vollen.
                false, woertlicheRedeNochOffen, kommmaStehtAus,
                kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    Konstituente ohneBezugsobjekt() {
        return new Konstituente(string,
                vorkommaNoetig, woertlicheRedeNochOffen, kommmaStehtAus,
                kannAlsBezugsobjektVerstandenWerdenFuer, null);
    }

    @Nonnull
    public String getString() {
        return string;
    }

    boolean vorkommaNoetig() {
        return vorkommaNoetig;
    }

    boolean woertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen;
    }

    public boolean kommaStehtAus() {
        return kommmaStehtAus;
    }

    /**
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen. Dazu müssen (in aller Regel) die grammatischen
     * Merkmale übereinstimmen und es muss mit dem Pronomen dieses Bezugsobjekt
     * gemeint sein.
     * <p>
     * Ein solcher Bezug sollte nur hergestellt werden, wenn es keine
     * Fehlreferenzierungen, Doppeldeutigkeiten
     * oder unerwünschten Wiederholungen geben kann. Typische Fälle wären "Du nimmst die Lampe und
     * zündest sie an." oder "Du stellst die Lampe auf den Tisch und zündest sie an."
     * <p>
     * Negatitvbeispiele wäre:
     * <ul>
     *     <li>"Du stellst die Lampe auf die Theke und zündest sie an." (Fehlreferenzierung)
     *     <li>"Du nimmst den Ball und den Schuh und wirfst ihn in die Luft." (Doppeldeutigkeit)
     *     <li>"Du nimmst die Lampe und zündest sie an. Dann stellst du sie wieder ab,
     *     schaust sie dir aber dann noch einmal genauer an: Sie ... sie ... sie" (Unerwünschte
     *     Wiederholung)
     *     <li>"Du stellst die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
     *     schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder in die Hand."
     *     (Referenziertes Objekt zu weit entfernt.)
     * </ul>
     */
    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        if (bezugsobjekt == null) {
            return null;
        }

        checkState(kannAlsBezugsobjektVerstandenWerdenFuer != null,
                "Bezugsobjekt gesetzt, aber kein "
                        + "kannAlsBezugsobjektVerstandenWerdenFuer! Bezugsobjekt: "
                        + bezugsobjekt);

        return new PhorikKandidat(kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    boolean koennteAlsBezugsobjektVerstandenWerdenFuer(final NumerusGenus numerusGenus) {
        return kannAlsBezugsobjektVerstandenWerdenFuer == numerusGenus;
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
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + ": \""
                + (vorkommaNoetig ? "[, ]" : "")
                + string
                + (kommmaStehtAus ? "[, ]" : "")
                + "\"";
    }
}
