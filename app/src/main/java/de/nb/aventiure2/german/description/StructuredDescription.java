package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

/**
 * A description based on a structured data structure: A {@link de.nb.aventiure2.german.satz.Satz}.
 * Somehting like "Du gehst in den Wald" or "Peter liebt Petra"
 */
public class StructuredDescription extends AbstractFlexibleDescription<StructuredDescription> {
    /**
     * This {@link Narration} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;
    /**
     * This {@link Narration} ends this ... (paragraph, e.g.)
     */
    private final StructuralElement endsThis;

    private final Satz satz;

    StructuredDescription(final StructuralElement startsNew,
                          final Satz satz,
                          final StructuralElement endsThis) {
        super(
                // Das hier ist eine automatische Vorbelegung auf Basis des Satzes.
                // Bei Bedarf kann man das in den Params noch überschreiben.
                guessPhorikKandidat(satz));
        this.startsNew = startsNew;
        this.satz = satz;
        this.endsThis = endsThis;
    }

    private static PhorikKandidat guessPhorikKandidat(final Satz satz) {
        return satz.getVerbzweitsatzStandard().joinToSingleKonstituente()
                .getPhorikKandidat();
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
    public StructuralElement getStartsNew() {
        return startsNew;
    }

    @Override
    public StructuralElement getEndsThis() {
        return endsThis;
    }

    @Override
    public ImmutableList<TextDescription> altTextDescriptions() {
        return satz.altVerzweitsaetze().stream()
                .map(kf ->
                        joinToKonstituentenfolge(
                                getStartsNew(),
                                kf,
                                getEndsThis()
                        )
                )
                .map(Konstituentenfolge::joinToSingleKonstituente)
                .map(this::toTextDescriptionKeepParams)
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
    public Konstituente getDescriptionPartizipIIPhrase(final Person person,
                                                       final Numerus numerus) {
        return getPraedikat().getPartizipIIPhrase(person, numerus).joinToSingleKonstituente();
    }

    @CheckReturnValue
    private StructuredDescription copy(final Satz satz) {
        return new StructuredDescription(getStartsNew(), satz, getEndsThis());
    }

    @Override
    public boolean hasSubjektDu() {
        return getSatz().hasSubjektDu();
    }

    @Override
    public Konstituente toSingleKonstituenteMitVorfeld(final String vorfeld) {
        return
                joinToKonstituentenfolge(
                        getStartsNew(),
                        satz.getVerbzweitsatzMitVorfeld(vorfeld),
                        getEndsThis()
                )
                        .joinToSingleKonstituente();
    }

    @Override
    public Konstituente toSingleKonstituente() {
        return
                joinToKonstituentenfolge(
                        getStartsNew(),
                        satz.getVerbzweitsatzStandard(),
                        getEndsThis()
                )
                        .joinToSingleKonstituente();
    }

    @Override
    @Nullable
    protected Konstituente toSingleKonstituenteMitSpeziellemVorfeldOrNull() {
        @Nullable final Konstituentenfolge speziellesVorfeld =
                satz.getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
        if (speziellesVorfeld == null) {
            return null;
        }

        return joinToKonstituentenfolge(
                getStartsNew(),
                speziellesVorfeld,
                getEndsThis()
        ).joinToSingleKonstituente();
    }

    @Override
    public Konstituente toSingleKonstituenteSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(
                getPraedikat().getVerbzweit(satz.getSubjekt()),
                getEndsThis())
                .joinToSingleKonstituente();
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
        // (Aber die Methode erleichert das Handling, sodass z.B. die
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
