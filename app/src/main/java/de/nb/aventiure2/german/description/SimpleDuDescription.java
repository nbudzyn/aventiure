package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.StructuralElement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

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
                        @Nullable final Konstituente remainder) {
        super(startsNew, remainder != null ? remainder.getPhorikKandidat() : null);

        checkArgument(vorfeldSatzglied == null || remainder != null,
                "Kein remainder, aber ein vorfeldSatzglied? Unmöglich!");

        checkArgument(vorfeldSatzglied == null ||
                        remainder.getString().contains(vorfeldSatzglied),
                "vorfeldSatzglied nicht im remainder enthalten. Remainder: "
                        + "%s, vorfeldSatzglied: %s",
                remainder, vorfeldSatzglied);

        this.verb = verb;
        this.remainder = remainder;
    }

    @Override
    public ImmutableList<TextDescription> altTextDescriptions() {
        final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();

        res.add(toTextDescription()
                // Bei einer SimpleDuDescription ist der Hauptsatz-Standard ein echter
                // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                .beginntZumindestSentence());

        @Nullable final Konstituente hauptsatzMitSpeziellemVorfeld =
                toSingleKonstituenteMitSpeziellemVorfeldOrNull();

        if (hauptsatzMitSpeziellemVorfeld != null) {
            res.add(toTextDescriptionKeepParams(hauptsatzMitSpeziellemVorfeld)
                    // Bei einer SimpleDuDescription ist auch dieser Hauptsatz ein echter
                    // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                    .beginntZumindestSentence());
        }

        return res.build();
    }

    @Override
    public Konstituente toSingleKonstituenteMitVorfeld(final String vorfeld) {
        return joinToKonstituentenfolge(
                vorfeld, // "dann"
                verb, // "gehst"
                "du", // "du"
                remainder) // "den Fluss entlang"
                .joinToSingleKonstituente()
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    @Override
    public Konstituente toSingleKonstituente() {
        return joinToKonstituentenfolge(
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
                vorfeldSatzglied,
                verb,
                "du",
                remainder.cutFirst(vorfeldSatzglied))
                .joinToSingleKonstituente()
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    public Konstituente toSingleKonstituenteSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(verb, remainder)
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
