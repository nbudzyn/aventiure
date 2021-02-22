package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.string.GermanStringUtil;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;

/**
 * A general description. The subject may be anything.
 */
@ParametersAreNonnullByDefault
public class TextDescription extends AbstractDescription<TextDescription> {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private final String text;

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

    public TextDescription(final Konstituente konstituente) {
        this(new DescriptionParams(konstituente.getStartsNew(), konstituente.getEndsThis(),
                        konstituente.getPhorikKandidat()),
                konstituente.getText(), konstituente.woertlicheRedeNochOffen(),
                konstituente.kommaStehtAus());
    }

    public TextDescription(final DescriptionParams descriptionParams,
                           final String text, final boolean woertlicheRedeNochOffen,
                           final boolean kommaStehtAus) {
        super(descriptionParams);
        this.kommaStehtAus = kommaStehtAus;
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        this.text = text;
    }

    @Override
    public ImmutableList<TextDescription> altTextDescriptions() {
        return ImmutableList.of(toTextDescription());
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     */
    @NonNull
    @CheckReturnValue
    public TextDescription mitPraefix(final Konstituentenfolge praefixKonstituentenfolge) {
        return mitPraefix(praefixKonstituentenfolge.joinToSingleKonstituente());
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     */
    @NonNull
    @CheckReturnValue
    private TextDescription mitPraefix(final Konstituente praefixKonstituente) {
        final Konstituente konstituente = joinToKonstituentenfolge(
                getStartsNew(),
                praefixKonstituente,
                text,
                getEndsThis()).joinToSingleKonstituente();

        final DescriptionParams newParams = copyParams();
        newParams.setStartsNew(konstituente.getStartsNew());
        newParams.setEndsThis(konstituente.getEndsThis());

        return new TextDescription(
                newParams,
                konstituente.getText(),
                woertlicheRedeNochOffen,
                kommaStehtAus);
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
        // FIXME Prüfen, ggf. ersetzen und möglichst ausbauen
        return new TextDescription(
                copyParams(),
                praefix + GermanStringUtil.capitalize(text), woertlicheRedeNochOffen,
                kommaStehtAus);
    }

    @Override
    @NonNull
    public Konstituente toSingleKonstituenteMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // Konjunktionaladverb ist in diesen Fällen nicht nötig:
        // "Du gehst in den Wald. Der Weg führt an einem Bach entlang."
        return toSingleKonstituente();
    }

    public TextDescription beginntZumindest(final StructuralElement zumindest) {
        // FIXME Verwendungen suchen und ggf. umbauen

        if (zumindest == WORD) {
            return this;
        }

        getParamsMutable().setStartsNew(max(getStartsNew(), zumindest));
        return this;
    }

    @Override
    @CheckReturnValue
    @NonNull
    public Konstituente toSingleKonstituente() {
        @Nullable final PhorikKandidat phorikKandidat = copyParams().getPhorikKandidat();
        return Konstituente.k(text, getStartsNew(),
                woertlicheRedeNochOffen, isKommaStehtAus(),
                getParamsMutable().getEndsThis(),
                phorikKandidat != null ? phorikKandidat.getNumerusGenus() : null,
                phorikKandidat != null ? phorikKandidat.getBezugsobjekt() : null);
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
    public boolean equals(@Nullable final Object o) {
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
