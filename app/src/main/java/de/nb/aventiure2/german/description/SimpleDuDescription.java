package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import de.nb.aventiure2.german.base.IKonstituenteOrStructuralElement;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.StructuralElement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehst in den Wald."
 */
public class SimpleDuDescription extends AbstractFlexibleDescription<SimpleDuDescription> {
    /**
     * Something like "gehst"
     */
    private final String verb;
    /**
     * Konstituente für etwas wie "in den Wald"
     */
    @Nullable
    private Konstituente remainder;

    /**
     * Ein Teil des {@link #remainder}-Strings, der statt "Du" das Vorfeld einnehmen kann.
     */
    @Nullable
    private String vorfeldSatzglied;

    /**
     * Erzeugt eine {@link SimpleDuDescription} ohne Vorfeld-Satzglied.
     *
     * @see #mitVorfeldSatzglied(String)
     */
    SimpleDuDescription(final StructuralElement startsNew,
                        final String verb,
                        @Nullable final IKonstituenteOrStructuralElement remainder) {
        this(startsNew, verb,
                remainder instanceof Konstituente ? (Konstituente) remainder : null,
                toEndsThis(remainder));
    }

    /**
     * Erzeugt eine {@link SimpleDuDescription} ohne Vorfeld-Satzglied.
     *
     * @see #mitVorfeldSatzglied(String)
     */
    private SimpleDuDescription(final StructuralElement startsNew,
                                final String verb,
                                @Nullable final Konstituente remainder,
                                final StructuralElement endsThis) {
        super(startsNew, endsThis,
                remainder != null ? remainder.getPhorikKandidat() : null);

        checkArgument(vorfeldSatzglied == null || remainder != null,
                "Kein remainder, aber ein vorfeldSatzglied? Unmöglich!");

        checkArgument(vorfeldSatzglied == null ||
                        remainder.getText().contains(vorfeldSatzglied),
                "vorfeldSatzglied nicht im remainder enthalten. Remainder: "
                        + "%s, vorfeldSatzglied: %s",
                remainder, vorfeldSatzglied);

        this.verb = verb;
        this.remainder = remainder;
    }

    private static StructuralElement toEndsThis(
            @Nullable final IKonstituenteOrStructuralElement remainder) {
        if (remainder == null) {
            return WORD;
        }

        if (remainder instanceof StructuralElement) {
            return (StructuralElement) remainder;
        }

        if (remainder instanceof Konstituente) {
            return ((Konstituente) remainder).getEndsThis();
        }

        throw new IllegalStateException("Unexpected remainder: " + remainder);
    }

    @Override
    public ImmutableList<TextDescription> altTextDescriptions() {
        final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();

        // Bei einer SimpleDuDescription ist der Hauptsatz-Standard ein echter
        // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
        res.add(toTextDescription().beginntZumindest(SENTENCE));

        @Nullable final Konstituente hauptsatzMitSpeziellemVorfeld =
                toSingleKonstituenteMitSpeziellemVorfeldOrNull();

        if (hauptsatzMitSpeziellemVorfeld != null) {
            // Bei einer SimpleDuDescription ist auch dieser Hauptsatz ein echter
            // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
            res.add(toTextDescriptionKeepOtherParams(hauptsatzMitSpeziellemVorfeld)
                    .beginntZumindest(SENTENCE));
        }

        return res.build();
    }

    @Override
    public Konstituente toSingleKonstituenteMitVorfeld(final String vorfeld) {
        return joinToKonstituentenfolge(
                getStartsNew(),
                vorfeld, // "dann"
                verb, // "gehst"
                "du", // "du"
                remainder,  // "den Fluss entlang"
                getEndsThis())
                .joinToSingleKonstituente()
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    @Override
    public Konstituente toSingleKonstituente() {
        return joinToKonstituentenfolge(
                getStartsNew(),
                "du",
                toSingleKonstituenteSatzanschlussOhneSubjekt())
                .joinToSingleKonstituente();
    }

    @Override
    @Nullable
    protected Konstituente toSingleKonstituenteMitSpeziellemVorfeldOrNull() {
        if (vorfeldSatzglied == null) {
            return null;
        }

        if (remainder == null) {
            throw new IllegalStateException(
                    "Kein remainder, aber ein Vorfeldsatzglied: " + vorfeldSatzglied);
        }

        return joinToKonstituentenfolge(
                getStartsNew(),
                vorfeldSatzglied,
                verb,
                "du",
                remainder.cutFirst(vorfeldSatzglied),
                getEndsThis())
                .joinToSingleKonstituente()
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    public Konstituente toSingleKonstituenteSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(verb, remainder, getEndsThis())
                .joinToSingleKonstituente()
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    public SimpleDuDescription mitVorfeldSatzglied(@Nullable final String vorfeldSatzglied) {
        this.vorfeldSatzglied = vorfeldSatzglied;
        return this;
    }

    @Override
    public SimpleDuDescription komma() {
        return komma(true);
    }

    @Override
    public SimpleDuDescription komma(final boolean kommaStehtAus) {
        checkState(!kommaStehtAus || remainder != null,
                "Es soll ein Komma ausstehen, aber ohne remainder?");

        if (remainder != null) {
            remainder = remainder.withKommaStehtAus(kommaStehtAus);
        }

        return this;
    }

    @Override
    public boolean hasSubjektDu() {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
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
        final SimpleDuDescription that = (SimpleDuDescription) o;
        return verb.equals(that.verb) &&
                Objects.equals(remainder, that.remainder) &&
                Objects.equals(vorfeldSatzglied, that.vorfeldSatzglied);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), verb, remainder);
    }
}
