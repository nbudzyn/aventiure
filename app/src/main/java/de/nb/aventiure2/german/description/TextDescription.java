package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
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

    // IDEA Verkürzungen automatisch erzeugen, z.B. erzeugen eines Nachfelds nach einem
    //  Prädikat: "und weiter in Richtung Schloss".

    public TextDescription(final Konstituente konstituente) {
        this(new DescriptionParams(), konstituente);
    }

    public TextDescription(final DescriptionParams descriptionParams,
                           final Konstituente konstituente) {
        super(descriptionParams);

        checkArgument(!descriptionParams.isAllowsAdditionalDuSatzreihengliedOhneSubjekt()
                        || konstituente.getEndsThis() == WORD,
                "!allowsAdditionalDuSatzreihengliedOhneSubjekt "
                        + "|| endsThis == StructuralElement.WORD verletzt");

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
    @Override
    @NonNull
    @CheckReturnValue
    TextDescription mitPraefix(final Konstituente praefixKonstituente) {
        final Konstituente konstituenteMitPraefix = joinToKonstituentenfolge(
                getStartsNew(),
                // Erzeugt das Maximum aus getStartsNew und praefixKonstituente.getStartsNew().
                // Wenn also this einen neuen PARAGRAPH fordert, fordert auch das Ergebnis
                // einen neuen PARAGRAPH. Und wenn die praefixKonstituente einen neuen
                // PARAGRAPH fordert, fordert auch das Ergebnis einen neuen
                // PARAGRAPH.
                praefixKonstituente,
                konstituente).joinToSingleKonstituente();

        return toTextDescriptionKeepParams(konstituenteMitPraefix);
    }

    @Override
    @NonNull
    public Konstituente toSingleKonstituenteMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // Konjunktionaladverb ist in diesen Fällen nicht nötig:
        // "Du gehst in den Wald. Der Weg führt an einem Bach entlang."
        return toSingleKonstituente();
    }

    @CanIgnoreReturnValue
    public TextDescription beginntZumindest(final StructuralElement zumindest) {
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

    public String getTextOhneKontext() {
        return konstituente.toTextOhneKontext();
    }

    public String getText() {
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
    public TextDescription phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        konstituente = konstituente.mitPhorikKandidat(phorikKandidat);
        return this;
    }

    @Override
    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return konstituente.getPhorikKandidat();
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
        // TODO Prüfen: Konstituente ist nicht final - könnte
        //  probleme mit Sets o.Ä. ergeben... Vgl. auch hashCode()!
        return Objects.equals(konstituente, that.konstituente);
    }

    @Override
    public int hashCode() {
        // TODO Prüfen: Konstiuente einbeziehen? Könnte aber Probleme
        //  mit Sets ergeben...
        return super.hashCode();
    }
}
