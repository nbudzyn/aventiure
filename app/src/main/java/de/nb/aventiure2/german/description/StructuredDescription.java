package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

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

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * A description based on a structured data structure: A {@link de.nb.aventiure2.german.satz.Satz}.
 * Somehting like "Du gehst in den Wald" or "Peter liebt Petra"
 */
public class StructuredDescription extends AbstractFlexibleDescription<StructuredDescription> {
    private final Satz satz;

    StructuredDescription(final StructuralElement startsNew,
                          final Satz satz) {
        super(startsNew);
        this.satz = satz;
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
    public ImmutableList<TextDescription> altTextDescriptions() {
        return satz.altVerzweitsaetze().stream()
                .map(Wortfolge::joinToWortfolge)
                .map(this::toTextDescriptionKeepParams)
                .map(TextDescription::beginntZumindestSentence)
                .collect(toImmutableList());
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
    public Wortfolge getDescriptionPartizipIIPhrase(final Person person,
                                                    final Numerus numerus) {
        return Wortfolge.joinToWortfolge(
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
    public Wortfolge toWortfolgeMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        @Nullable final Wortfolge wortfolgeMitSpeziellemVorfeldOrNull =
                toWortfolgeMitSpeziellemVorfeldOrNull();

        if (wortfolgeMitSpeziellemVorfeldOrNull != null) {
            return wortfolgeMitSpeziellemVorfeldOrNull;
        }

        return toWortfolgeMitVorfeld(konjunktionaladverb);
    }

    @Override
    public Wortfolge toWortfolgeMitVorfeld(final String vorfeld) {
        return Wortfolge.joinToWortfolge(satz.getVerbzweitsatzMitVorfeld(vorfeld));
    }

    @Override
    public Wortfolge toWortfolgeMitSpeziellemVorfeld() {
        @Nullable final Wortfolge hauptsatzMitSpeziellemVorfeldOrNull =
                toWortfolgeMitSpeziellemVorfeldOrNull();
        if (hauptsatzMitSpeziellemVorfeldOrNull == null) {
            return toWortfolge();
        }

        return hauptsatzMitSpeziellemVorfeldOrNull;
    }

    @Override
    public Wortfolge toWortfolge() {
        return Wortfolge.joinToWortfolge(satz.getVerbzweitsatzStandard());
    }

    @Nullable
    private Wortfolge toWortfolgeMitSpeziellemVorfeldOrNull() {
        @Nullable final Iterable<Konstituente> speziellesVorfeld =
                satz.getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
        if (speziellesVorfeld == null) {
            return null;
        }

        return Wortfolge.joinToNullWortfolge(speziellesVorfeld);
    }

    @Override
    public Wortfolge toWortfolgeSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToWortfolge(getPraedikat().getVerbzweit(satz.getSubjekt()));
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return satz.getPraedikat();
    }

    public Satz getSatz() {
        return satz;
    }

    @Override
    public StructuredDescription komma(final boolean kommaStehtAus) {
        // Bewirkt nichts. Der Satz weiß schon selbst, wann ein Komma nötig ist.
        // (Aber die Methode erleichert das Handling, so dass z.B. die
        // TimedDescription problemlos komma() implementieren kann etc.)
        return this;
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
