package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.StructuralElement;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

/**
 * A general description. The subject may be anything.
 */
@ParametersAreNonnullByDefault
public class TextDescription extends AbstractDescription<TextDescription> {
    /**
     * {@link Konstituente} für etwas wie "Der Weg führt weiter in den Wald hinein. Dann stehst
     * du vor einer Kirche"
     */
    private Konstituente konstituente;

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
        super(new DescriptionParams(konstituente.getPhorikKandidat()));

        this.konstituente = konstituente;
    }

    public TextDescription(final DescriptionParams descriptionParams,
                           final Konstituente konstituente) {
        super(descriptionParams);
        this.konstituente = konstituente;
    }

    @Override
    public StructuralElement getStartsNew() {
        return konstituente.getStartsNew();
    }

    @Override
    public StructuralElement getEndsThis() {
        return konstituente.getEndsThis();
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
    public TextDescription mitPraefix(final String praefix) {
        // FIXME Prüfen, ggf. ersetzen und möglichst ausbauen

        final Konstituente konstituenteMitPraefix = joinToKonstituentenfolge(
                getStartsNew(),
                praefix,
                konstituente).joinToSingleKonstituente();

        return new TextDescription(copyParams(), konstituenteMitPraefix);
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     */
    @NonNull
    @CheckReturnValue
    private TextDescription mitPraefix(final Konstituente praefixKonstituente) {
        // FIXME Prüfen, ggf. ersetzen und möglichst ausbauen

        final Konstituente konstituenteMitPraefix = joinToKonstituentenfolge(
                getStartsNew(),
                praefixKonstituente,
                konstituente).joinToSingleKonstituente();

        return new TextDescription(copyParams(), konstituenteMitPraefix);
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

        final Konstituente konstituenteMitPraefix = joinToKonstituentenfolge(
                getStartsNew(),
                praefix,
                SENTENCE,
                konstituente).joinToSingleKonstituente();

        return new TextDescription(copyParams(), konstituenteMitPraefix);
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

        konstituente = joinToKonstituentenfolge(
                zumindest,
                konstituente).joinToSingleKonstituente();

        return this;
    }

    @Override
    @CheckReturnValue
    @NonNull
    public Konstituente toSingleKonstituente() {
        return konstituente;
    }

    public String getText() {
        // FIXME Verwendungen prüfen. Ggf. toTextOhneKontext... oder toSingleKonstituente()
        return konstituente.getText();
    }

    @Override
    public TextDescription komma() {
        return komma(true);
    }

    @Override
    public TextDescription komma(final boolean kommaStehtAus) {
        konstituente = konstituente.withKommaStehtAus(kommaStehtAus);
        return this;
    }

    public boolean isKommaStehtAus() {
        return toSingleKonstituente().kommaStehtAus();
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
        return Objects.equals(konstituente, that.konstituente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), konstituente);
    }
}
