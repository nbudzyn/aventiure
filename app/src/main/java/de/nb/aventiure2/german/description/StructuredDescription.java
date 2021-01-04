package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Wortfolge.w;

/**
 * A description based on a structured data structure: A {@link de.nb.aventiure2.german.satz.Satz}.
 * Somehting like "Du gehst in den Wald" or "Peter liebt Petra"
 */
public class StructuredDescription extends AbstractFlexibleDescription<StructuredDescription> {
    private final Satz satz;

    StructuredDescription(final StructuralElement startsNew,
                          final Satz satz) {
        super(startsNew,
                guessWoertlicheRedeNochOffen(satz),
                guessKommaStehtAus(satz));
        this.satz = satz;
    }

    private static boolean guessWoertlicheRedeNochOffen(final Satz satz) {
        // FIXME Hier gibt es ein Problem: Ob die wörtliche Rede noch offen ist, hängt von der
        //  konkreten Realisierung des Prädikats ab (was steht im Vorfeld / Nachfeld etc.).
        //  Dies hier ist nur eine grobe Richtschnur.
        return Konstituente.woertlicheRedeNochOffen(satz.getVerbzweitsatzStandard());
    }

    private static boolean guessKommaStehtAus(final Satz satz) {
        // FIXME Hier gibt es ein Problem: Ob ein Komma aussteht, hängt von der
        //  konkreten Realisierung des Prädikats ab (was steht im Vorfeld / Nachfeld etc.).
        //  Dies hier ist nur eine grobe Richtschnur.
        return Konstituente.kommaStehtAus(satz.getVerbzweitsatzStandard());
    }

    @CheckReturnValue
    public StructuredDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return copy(getSatz().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @CheckReturnValue
    public StructuredDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return copy(getSatz().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @CheckReturnValue
    public StructuredDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return copy(getSatz().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Override
    public ImmutableList<TextDescription> altDescriptionHaupsaetze() {
        final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();

        res.add(toTextDescription()
                // Bei einer StructuredDescription ist der Hauptsatz-Standard ein echter
                // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                .beginntZumindestSentence());

        @Nullable final Wortfolge hauptsatzMitSpeziellemVorfeld =
                getHauptsatzMitSpeziellemVorfeldOrNull();

        if (hauptsatzMitSpeziellemVorfeld != null) {
            res.add(toTextDescriptionKeepParams(hauptsatzMitSpeziellemVorfeld)
                    // Bei einer StructuredDescription ist auch dieser Hauptsatz ein echter
                    // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                    .beginntZumindestSentence());
        }

        return res.build();
    }


    /**
     * Gibt das Prädikat als eine unflektierte Phrase mit Partizip II zurück: "unten angekommen",
     * "die Kugel genommen"
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat Phrase
     * eine Person und einen Numerus - Beispiel:
     * "[Ich habe] die Kugel an mich genommen"
     * (nicht *"[Ich habe] die Kugel an sich genommen")
     */
    public String getDescriptionPartizipIIPhrase(final Person person,
                                                 final Numerus numerus) {
        return GermanUtil.joinToString(
                getPraedikat().getPartizipIIPhrase(person, numerus));
    }

    @CheckReturnValue
    private StructuredDescription copy(final Satz satz) {
        return new StructuredDescription(getStartsNew(), satz);
    }

    @Override
    public boolean hasSubjektDu() {
        return getSatz().hasSubjektDu();
    }

    @Override
    public TextDescription toTextDescriptionMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // FIXME Derzeit ist die Sache mit dem Komma nicht einheitlich gelöst.
        //  Gut wäre es wohl, wenn die DesriptionParams KEIN isKommaStehtAus
        //  hätten, sondern wenn diese Informatoion hier on-the-fly ermittelt würde.
        //  In der TextDescription müsste man die Information dann zusätzlich speichern,
        //  damit der Benutzer sie (nur dort?!) angeben kann.
        @Nullable final Wortfolge hauptsatzMitSpeziellemVorfeldOrNull =
                getHauptsatzMitSpeziellemVorfeldOrNull();

        if (hauptsatzMitSpeziellemVorfeldOrNull != null) {
            return toTextDescriptionKeepParams(hauptsatzMitSpeziellemVorfeldOrNull);
        }

        return toTextDescriptionMitVorfeld(konjunktionaladverb);
    }

    @Override
    public Wortfolge getDescriptionHauptsatzMitVorfeld(final String vorfeld) {
        return Wortfolge.joinToWortfolge(satz.getVerbzweitsatzMitVorfeld(vorfeld));
    }

    @Override
    public Wortfolge getDescriptionHauptsatzMitSpeziellemVorfeld() {
        return getHauptsatzMitSpeziellemVorfeldOrStandard();
    }

    @Override
    public Wortfolge getDescriptionHauptsatz() {
        return Wortfolge.joinToWortfolge(satz.getVerbzweitsatzStandard());
    }

    private Wortfolge getHauptsatzMitSpeziellemVorfeldOrStandard() {
        @Nullable final Wortfolge hauptsatzMitSpeziellemVorfeldOrNull =
                getHauptsatzMitSpeziellemVorfeldOrNull();
        if (hauptsatzMitSpeziellemVorfeldOrNull == null) {
            final TextDescription textDescription = toTextDescription();
            return w(textDescription.getText(), textDescription.isWoertlicheRedeNochOffen(),
                    textDescription.isKommaStehtAus());
        }

        return hauptsatzMitSpeziellemVorfeldOrNull;
    }

    @Nullable
    private Wortfolge getHauptsatzMitSpeziellemVorfeldOrNull() {
        @Nullable final Iterable<Konstituente> speziellesVorfeld =
                satz.getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
        if (speziellesVorfeld == null) {
            return null;
        }

        return Wortfolge.joinToNullWortfolge(speziellesVorfeld);
    }

    @Override
    public Wortfolge getDescriptionSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToWortfolge(getPraedikat().getVerbzweit(satz.getSubjekt()));
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return satz.getPraedikat();
    }

    public Satz getSatz() {
        return satz;
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
        final StructuredDescription that = (StructuredDescription) o;
        return satz.equals(that.satz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), satz);
    }
}
