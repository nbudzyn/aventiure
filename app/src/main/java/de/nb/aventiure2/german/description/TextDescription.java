package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.string.GermanStringUtil;

import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static de.nb.aventiure2.german.base.Wortfolge.w;

/**
 * A general description. The subject may be anything.
 */
@ParametersAreNonnullByDefault
public class TextDescription extends AbstractDescription<TextDescription> {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private String text;

    /**
     * Ob die wörtliche Rede noch "offen" ist.  Es steht also noch ein schließendes
     * Anführungszeichen aus. Wenn der Satz beendet wird, muss vielleicht außerdem
     * noch ein Punkt nach dem Anführungszeitchen gesetzt werden.
     */
    private final boolean woertlicheRedeNochOffen;

    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private boolean kommaStehtAus;

    // IDEA Das Konzept könnte man verallgemeinern: Die TextDescription könnte am Ende
    //  Koordination (d.h. und-Verbindungen) auf verschiedenen Ebenen erlauben:
    //  - Du nimmst die Lampe UND DAS GLAS: Koordination im AkkObj des NEHMEN-Prädikat
    //  - Den Weg hinunter kommen eine Frau UND EIN MANN: Koordination im Subj des KOMMEN-Prädikats
    //  - Du hast gute Laune und GEHST WEITER: Koordination zweier Verben zum selben Subj (P2)
    //  - Die Frau hat gute Laune und GEHT WEITER: Koordination zweier Verben zum selben Subj (P3)
    //  - Die Frau geht den Weg hinunten UND DU GEHST HINTERHER: Koordination zweier Hauptsätze
    //  Dazu bräuchte man wohl eine Kontextinfo in der Art "Womit endet die TextDescription?"
    //  Das könnte allerdings auch über die Prädikate... gelöst werden...

    TextDescription(final StructuralElement startsNew,
                    final Wortfolge wortfolge) {
        this(new DescriptionParams(startsNew, wortfolge.getPhorikKandidat()),
                wortfolge.getString(), wortfolge.woertlicheRedeNochOffen(),
                wortfolge.kommaStehtAus());
    }

    public TextDescription(final DescriptionParams descriptionParams,
                           final String description, final boolean woertlicheRedeNochOffen,
                           final boolean kommaStehtAus) {
        super(descriptionParams);
        this.kommaStehtAus = kommaStehtAus;
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        text =
                // Wenn eine Description als "neuer Satz" (oder Paragraph...) gekenzeichnet
                // wurde, darf man ihr nichts mehr voranstellen - außer andere vollständige
                // Sätze. (uncapitalize() wäre sicher ein Bug, weil man nicht weiß,
                // ob die Description mit einem Nomen anfängt oder nicht). Dann können wir
                // sie auch jetzt gleich capitalizen!
                StructuralElement.min(descriptionParams.getStartsNew(), SENTENCE) == SENTENCE ?
                        GermanStringUtil.capitalize(description) : description;
    }

    @Override
    public ImmutableList<TextDescription> altTextDescriptions() {
        return ImmutableList.of(toTextDescription());
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     * Wird dazwischen ein Leerzeichen erwartet, so muss das Präfix mit diesem Leerzeichen enden.
     */
    @NonNull
    @CheckReturnValue
    public TextDescription mitPraefix(final String praefix) {
        return new TextDescription(
                copyParams(),
                praefix + text, woertlicheRedeNochOffen, kommaStehtAus);
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist, wobei die alte <code>TextDescription</code> großgeschrieben
     * (capitalized) wird. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird ein Präfix vorangestellt, z.B. ein ganz neuer
     * Satz.
     * Wird dazwischen ein Leerzeichen erwartet, so muss das Präfix mit diesem Leerzeichen enden.
     */
    @NonNull
    @CheckReturnValue
    public TextDescription mitPraefixCapitalize(final String praefix) {
        return new TextDescription(
                copyParams(),
                praefix + GermanStringUtil.capitalize(text), woertlicheRedeNochOffen,
                kommaStehtAus);
    }

    @Override
    @NonNull
    public Wortfolge toWortfolgeMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // Konjunktionaladverb ist in diesen Fällen nicht nötig:
        // "Du gehst in den Wald. Der Weg führt an einem Bach entlang."
        return toWortfolge();
    }

    @NonNull
    public TextDescription beginntZumindestParagraph() {
        beginntZumindest(PARAGRAPH);
        text = GermanStringUtil.capitalize(text);
        return this;
    }

    @NonNull
    public TextDescription beginntZumindestSentence() {
        beginntZumindest(SENTENCE);
        text = GermanStringUtil.capitalize(text);
        return this;
    }

    private void beginntZumindest(final StructuralElement zumindest) {
        Preconditions.checkArgument(SENTENCE == StructuralElement.min(SENTENCE, zumindest),
                // Hier muss man bedenken, dass neuerSatz() ein capitalize() des übergebenen
                // Textes macht. Den kann man auch nachträglich nicht mehr zurückdrehen,
                // weil man nicht weiß, ob die description mit einem Nomen oder z.B. einem
                // Adjektiv beginnt. Also darf man bei einer mit neuerSatz() erzeugten
                // AbstractDescription nicht einfach das startsNew nachträglich wieder
                // auf WORD setzen.
                "Hier ist unbekannt, ob die description nicht vielleicht schon " +
                        "großgeschrieben wurde. Man kann nicht auf WORD zurücksetzen. Man "
                        + "sollte auch keinen Code schreiben, der das versucht"
        );

        getParamsMutable().setStartsNew(
                max(copyParams().getStartsNew(), zumindest));
    }

    @Override
    @CheckReturnValue
    @NonNull
    public Wortfolge toWortfolge() {
        return w(text, woertlicheRedeNochOffen, isKommaStehtAus(),
                copyParams().getPhorikKandidat());
    }

    public String getText() {
        return text;
    }

    public boolean isWoertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen;
    }

    @Override
    public TextDescription komma() {
        return komma(true);
    }

    @Override
    public TextDescription komma(final boolean kommaStehtAus) {
        this.kommaStehtAus = kommaStehtAus;
        return this;
    }

    public boolean isKommaStehtAus() {
        return kommaStehtAus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final TextDescription that = (TextDescription) o;
        return woertlicheRedeNochOffen == that.woertlicheRedeNochOffen &&
                kommaStehtAus == that.kommaStehtAus &&
                text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }
}
