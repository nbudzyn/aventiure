package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.string.GermanStringUtil;
import de.nb.aventiure2.german.string.NoLetterException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.german.base.GermanUtil.endeDecktKommaAb;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static java.util.Objects.requireNonNull;

@Immutable
public class Konstituente implements IKonstituenteOrStructuralElement {
    /**
     * Die eigentlichen Wörter und Satzzeichen
     */
    @Nonnull
    private final String text;

    /**
     * Ob noch ein Komma <i>vor dieser Konstituente</i> nötig ist. Alternativ kann ein Punkt,
     * ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon vorangehen, der ebenfalls das Komma "abdeckt".
     */
    private final boolean vorkommaNoetig;

    /**
     * Ob noch ein Doppelpunkt <i>vor dieser Konstituente</i> nötig ist.
     * Alternativ kann ein Punkt, ein Ausrufezeichen oder ein Fragezeichen vorangehen, das
     * ebenfalls den Doppelpunkt "abdeckt".
     * <p>
     * Vordoppelpunkte sind nur in sehr seltenen Fällen nötig - etwa wenn eine wörtliche
     * Rede im Nachfeld steht. (<i>Peter sagt: "Genau!"</i>, nicht aber die
     * <i>"Genau", sagt Peter.</i>)
     * <p>
     * Es kann nur entweder ein Vorkomma oder ein Vordoppelpunkt nötig sein.
     */
    private final boolean vordoppelpunktNoetig;

    private final StructuralElement startsNew;

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
    private final boolean kommaStehtAus;

    private final StructuralElement endsThis;

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
     * <p>
     * Dieses Feld sollte nur gesetzt werden, wenn es keine
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
    private final IBezugsobjekt bezugsobjekt;

    /**
     * Gibt eine Kopie der Konstituente zurück - unverändert, falls
     * {@code vorkommaNoetigMin} {@code false} ist, sonst mit der Angabe, dass
     * ein Vorkomma nötig ist.
     */
    @NonNull
    public Konstituente withVorkommaNoetigMin(final boolean vorkommaNoetigMin) {
        if (!vorkommaNoetigMin) {
            return this;
        }

        return withVorkommaNoetig(true);
    }

    @CheckReturnValue
    public Konstituente withVorkommaNoetig(final boolean vorkommaNoetig) {
        return new Konstituente(text, vorkommaNoetig, vordoppelpunktNoetig,
                startsNew, woertlicheRedeNochOffen, kommaStehtAus,
                endsThis, kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    @CheckReturnValue
    public Konstituente withVordoppelpunktNoetig() {
        return new Konstituente(text, vorkommaNoetig,
                true,
                startsNew, woertlicheRedeNochOffen, kommaStehtAus,
                endsThis, kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    @CheckReturnValue
    Konstituente withKommaStehtAus() {
        return withKommaStehtAus(true);
    }

    @CheckReturnValue
    public Konstituente withKommaStehtAus(final boolean kommmaStehtAus) {
        return k(text, startsNew, woertlicheRedeNochOffen, kommmaStehtAus,
                endsThis, kannAlsBezugsobjektVerstandenWerdenFuer,
                bezugsobjekt
        );
    }

    /**
     * Erzeugt eine neue Kopie der Konstituente mit diesem Bezugsobjekt (oder keinem, wenn
     * nicht angegeben) und dieser Angabe, als was die Komponente verstanden werden könnte.
     *
     * @param kannAlsBezugsobjektVerstandenWerdenFuer Muss gesetzt sein, wenn
     *                                                <code>bezugsobjekt</code> gesetzt ist.
     */
    @CheckReturnValue
    Konstituente withBezugsobjektUndKannVerstandenWerdenAls(
            @Nullable final IBezugsobjekt bezugsobjekt,
            @Nullable final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer) {
        return new Konstituente(text, vorkommaNoetig, vordoppelpunktNoetig,
                startsNew, woertlicheRedeNochOffen, kommaStehtAus,
                endsThis,
                kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat. Die Konstituente enthält keinen
     * Phorik-Kandidaten und kann auch nicht als phorischer Bezug verstanden werden
     * (man kann also nicht nachfolgende "sie", "ihm" oder "es" schreiben und diese
     * Konsituente meinen).
     */
    @CheckReturnValue
    public static Konstituente k(final @Nonnull String text) {
        return k(text, null, null);
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat. Die Konstituente enthält keinen
     * Phorik-Kandidaten.
     */
    @CheckReturnValue
    public static Konstituente k(final @Nonnull String text,
                                 @Nullable
                                 final NumerusGenus koennteAlsBezugsobjektVerstandenWerdenFuer) {
        return k(text, koennteAlsBezugsobjektVerstandenWerdenFuer, null);
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat.
     */
    @CheckReturnValue
    public static Konstituente k(final @Nonnull String text,
                                 @Nullable
                                 final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer,
                                 @Nullable final IBezugsobjekt bezugsobjekt) {
        // Wenn der String mit Komma anfängt, lassen wir es in der Konsituente  stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()
        return k(text.trim(), WORD, false, false,
                WORD, kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt
        );
    }

    /**
     * Erzeugt eine Konstituente, die nicht als phorischer Bezug verstanden werden
     * kann (man kann also nicht nachfolgende "sie", "ihm" oder "es" schreiben und diese
     * Konsituente meinen).
     */
    @CheckReturnValue
    public static Konstituente k(final @Nonnull String text,
                                 final boolean woertlicheRedeNochOffen,
                                 final boolean kommaStehtAus) {
        return k(text, WORD, woertlicheRedeNochOffen, kommaStehtAus,
                WORD, null, null);
    }

    @CheckReturnValue
    private static Konstituente k(final @Nonnull String text,
                                  final StructuralElement startsNew,
                                  final boolean woertlicheRedeNochOffen,
                                  final boolean kommaStehtAus,
                                  final StructuralElement endsThis,
                                  @Nullable
                                  final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer,
                                  @Nullable final IBezugsobjekt bezugsobjekt) {
        return new Konstituente(text, false, false,
                startsNew, woertlicheRedeNochOffen, kommaStehtAus, endsThis,
                kannAlsBezugsobjektVerstandenWerdenFuer,
                bezugsobjekt);
    }

    Konstituente(final String text, final boolean vorkommaNoetig,
                 final boolean vordoppelpunktNoetig,
                 final StructuralElement startsNew,
                 final boolean woertlicheRedeNochOffen, final boolean kommaStehtAus,
                 final StructuralElement endsThis,
                 @Nullable final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer,
                 @Nullable final IBezugsobjekt bezugsobjekt) {
        checkArgument(!vorkommaNoetig || !vordoppelpunktNoetig,
                "Vorkomma und Vordoppelpunkt können nicht gleichzeitig nötig "
                        + "sein! String: %s", text);
        requireNonNull(text, "string");
        checkArgument(!text.isEmpty(), "String ist empty");
        checkArgument(!text.startsWith(" "));
        checkArgument(!text.endsWith(" "));
        checkArgument(bezugsobjekt == null ||
                        kannAlsBezugsobjektVerstandenWerdenFuer != null,
                "Bezugsobjekt angegeben, aber kein Numerus / Genus! Bezugsobjekt: %s",
                bezugsobjekt);

        this.text = text;
        this.vorkommaNoetig = vorkommaNoetig;
        this.startsNew = startsNew;
        this.vordoppelpunktNoetig = vordoppelpunktNoetig;
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        this.kommaStehtAus = kommaStehtAus;
        this.endsThis = endsThis;
        this.bezugsobjekt = bezugsobjekt;
        this.kannAlsBezugsobjektVerstandenWerdenFuer = kannAlsBezugsobjektVerstandenWerdenFuer;
    }

    @Override
    public ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(new Konstituentenfolge(this));
    }

    @CheckReturnValue
    public IKonstituenteOrStructuralElement cutFirst(final String subText) {
        @Nullable final String resultString = GermanUtil.cutFirst(text, subText);

        if (resultString == null) {
            return StructuralElement.max(startsNew, endsThis);
        }

        return new Konstituente(
                resultString, vorkommaNoetig,
                vordoppelpunktNoetig, startsNew, woertlicheRedeNochOffen, kommaStehtAus,
                endsThis, kannAlsBezugsobjektVerstandenWerdenFuer,
                bezugsobjekt);
    }

    /**
     * Gibt eine ganz grobe Angabe zurück, wieviele Wörter diese Konstituente enthaelt.
     */
    public int guessNumWords() {
        int res = 1;
        for (final char c : toTextOhneKontext().toCharArray()) {
            if (c == ' ' || c == '\n') {
                res++;
            }
        }

        return res;
    }

    @CheckReturnValue
    public Konstituente mitPhorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        if (phorikKandidat == null) {
            return ohneBezugsobjekt();
        }

        return new Konstituente(text,
                vorkommaNoetig, vordoppelpunktNoetig, startsNew, woertlicheRedeNochOffen,
                kommaStehtAus,
                endsThis, phorikKandidat.getNumerusGenus(), phorikKandidat.getBezugsobjekt());
    }

    @CheckReturnValue
    Konstituente ohneBezugsobjekt() {
        return new Konstituente(text,
                vorkommaNoetig, vordoppelpunktNoetig, startsNew, woertlicheRedeNochOffen,
                kommaStehtAus,
                endsThis, kannAlsBezugsobjektVerstandenWerdenFuer, null);
    }

    /**
     * Gibt den Text zurück, ohne den folgenden Text zu berücksichtigen.
     * Das kann leicht zu Zeichensetzungsfehlern führen, und man sollte diese
     * Methode nur verwenden, wenn man weiß, um was für einen Text es sich handelt
     * und was für ein Text noch folgt. Der Aufrufer muss Vorkomma, Vordoppelpunkt
     * {@link #startsNew}, Großschreibung sowie {@link #endsThis} und Folgekomma selbst
     * bearbeiten. Großschreibung geschieht hingegen automatisch und die wörtliche Rede wird
     * automatisch geschlossen. Es wird kein automatischer Punkt gesetzt.
     */
    @NonNull
    public String toTextOhneKontext() {
        final String res = woertlicheRedeNochOffen() ? getText() + "“" : getText();

        if (startsNew.isAtLeast(SENTENCE)) {
            try {
                return GermanStringUtil.capitalizeFirstLetter(res);
            } catch (final NoLetterException e) {
                return res;
            }
        }

        return res;
    }

    @CheckReturnValue
    Konstituente capitalizeFirstLetter() throws NoLetterException {
        return mitText(GermanStringUtil.capitalizeFirstLetter(text));
    }

    @CheckReturnValue
    private Konstituente mitText(final String text) {
        if (this.text.equals(text)) {
            // Speicher und Zeit sparen
            return this;
        }

        return new Konstituente(text,
                vorkommaNoetig, vordoppelpunktNoetig, startsNew, woertlicheRedeNochOffen,
                kommaStehtAus,
                endsThis, kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    @Nonnull
    public String getText() {
        return text;
    }

    public boolean vorkommaNoetig() {
        return vorkommaNoetig && startsNew == WORD && !GermanUtil.beginnDecktKommaAb(text);
    }

    public boolean vordoppelpunktNoetig() {
        return vordoppelpunktNoetig && !GermanUtil.beginnDecktDoppelpunktAb(text);
    }

    public boolean woertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen;
    }

    public boolean kommaStehtAus() {
        return kommaStehtAus && endsThis == WORD && !endeDecktKommaAb(toTextOhneKontext());
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
                        + "kannAlsBezugsobjektVerstandenWerdenFuer! Bezugsobjekt: %s",
                bezugsobjekt);

        return new PhorikKandidat(kannAlsBezugsobjektVerstandenWerdenFuer, bezugsobjekt);
    }

    boolean koennteAlsBezugsobjektVerstandenWerdenFuer(final NumerusGenus numerusGenus) {
        return kannAlsBezugsobjektVerstandenWerdenFuer == numerusGenus;
    }

    @Nullable
    @VisibleForTesting
    NumerusGenus getKannAlsBezugsobjektVerstandenWerdenFuer() {
        return kannAlsBezugsobjektVerstandenWerdenFuer;
    }

    public StructuralElement getStartsNew() {
        return startsNew;
    }

    public StructuralElement getEndsThis() {
        return endsThis;
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
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        // WICHTIG: Hier darf man nur den Text eingehen lassen, damit die Methode
        // konsistent mit equals arbeitet.
        return Objects.hash(text);
    }

    @NonNull
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + ": \""
                + (startsNew != WORD ? "[starts new " + startsNew + "]" : "")
                + (vorkommaNoetig() ? "[, ]" : "")
                + toTextOhneKontext()
                + (kommaStehtAus() ? "[, ]" : "")
                + (endsThis != WORD ? "[ends this " + endsThis + "]" : "")
                + "\"";
    }
}
