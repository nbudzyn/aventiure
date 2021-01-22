package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.german.base.Wortfolge.joinToWortfolge;

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
     * Wortfolge für etwas wie "in den Wald"
     */
    @Nullable
    private Wortfolge remainder;

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
                        @Nullable final Wortfolge remainder) {
        super(startsNew, remainder != null ? remainder.getPhorikKandidat() : null);

        checkArgument(vorfeldSatzglied == null || remainder != null,
                "Kein remainder, aber ein vorfeldSatzglied? Unmöglich!");

        checkArgument(vorfeldSatzglied == null ||
                        remainder.getString().contains(vorfeldSatzglied),
                "vorfeldSatzglied nicht im remainder enthalten. Remainder: ",
                remainder + ", vorfeldSatzglied: " + vorfeldSatzglied);

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

        @Nullable final Wortfolge hauptsatzMitSpeziellemVorfeld =
                toWortfolgeMitSpeziellemVorfeldOrNull();

        if (hauptsatzMitSpeziellemVorfeld != null) {
            res.add(toTextDescriptionKeepParams(hauptsatzMitSpeziellemVorfeld)
                    // Bei einer SimpleDuDescription ist auch dieser Hauptsatz ein echter
                    // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                    .beginntZumindestSentence());
        }

        return res.build();
    }

    @Override
    public Wortfolge toWortfolgeMitVorfeld(final String vorfeld) {
        return joinToWortfolge(
                vorfeld, // "dann"
                verb, // "gehst"
                "du", // "du"
                remainder) // "den Fluss entlang"
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    @Override
    public Wortfolge toWortfolge() {
        return joinToWortfolge(
                "du",
                toWortfolgeSatzanschlussOhneSubjekt());
    }

    @Override
    @Nullable
    protected Wortfolge toWortfolgeMitSpeziellemVorfeldOrNull() {
        if (vorfeldSatzglied == null) {
            return null;
        }

        if (remainder == null) {
            throw new IllegalStateException(
                    "Kein remainder, aber ein Vorfeldsatzglied: " + vorfeldSatzglied);
        }

        return joinToWortfolge(
                vorfeldSatzglied,
                verb,
                "du",
                remainder.cutFirst(vorfeldSatzglied))
                .mitPhorikKandidat(copyParams().getPhorikKandidat());
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    public Wortfolge toWortfolgeSatzanschlussOhneSubjekt() {
        return joinToWortfolge(
                verb,
                remainder)
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
            remainder = remainder.mitKommaStehtAus(kommaStehtAus);
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
