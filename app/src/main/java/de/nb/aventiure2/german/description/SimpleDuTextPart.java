package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;
import static de.nb.aventiure2.german.base.Wortfolge.w;

public class SimpleDuTextPart implements AbstractDuTextPart {
    /**
     * Something like "gehst"
     */
    private final String verb;
    /**
     * Something like "in den Wald"
     */
    @Nullable
    private final String remainder;

    /**
     * Ein Teil von {@link #remainder}, der statt "Du" das Vorfeld einnehmen kann.
     */
    @Nullable
    private final String vorfeldSatzglied;

    private SimpleDuTextPart(final String verb) {
        this(verb, null);
    }

    private SimpleDuTextPart(final String verb,
                             @Nullable final String remainder) {
        this(verb, remainder, null);
    }


    SimpleDuTextPart(final String verb,
                     @Nullable final String remainder,
                     @Nullable final String vorfeldSatzglied) {
        checkArgument(vorfeldSatzglied == null || remainder != null,
                "Kein remainder, aber ein vorfeldSatzglied? Unmöglich!");

        checkArgument(vorfeldSatzglied == null || remainder.contains(vorfeldSatzglied),
                "vorfeldSatzglied nicht im remainder enthalten. Remainder: ",
                remainder + ", vorfeldSatzglied: " + vorfeldSatzglied);

        this.verb = verb;
        this.remainder = remainder;
        this.vorfeldSatzglied = vorfeldSatzglied;
    }

    @Override
    public Wortfolge getDuHauptsatzMitVorfeld(final String vorfeld) {
        return w(GermanUtil.buildHauptsatz(vorfeld, // "dann"
                verb, // "gehst"
                GermanUtil.joinToNullString("du", remainder))); // "du den Fluss entlang"
    }

    @Override
    public Wortfolge getDuHauptsatzMitSpeziellemVorfeld() {
        if (vorfeldSatzglied == null) {
            return getDuHauptsatz();
        }

        if (remainder == null) {
            throw new IllegalStateException(
                    "Keine remainder, aber ein Vorfeldsatzglied: " + vorfeldSatzglied);
        }

        @Nullable final String remainderWithoutVorfeldSatzglied =
                GermanUtil.cutFirst(remainder, vorfeldSatzglied);

        return w(GermanUtil.buildHauptsatz(vorfeldSatzglied,
                verb,
                joinToNullString("du", remainderWithoutVorfeldSatzglied)));
    }

    @Override
    public Wortfolge getDuHauptsatz() {
        return w(GermanUtil.buildHauptsatz("du",
                verb,
                remainder));
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToNullWortfolge(verb, remainder);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleDuTextPart that = (SimpleDuTextPart) o;
        return verb.equals(that.verb) &&
                Objects.equals(remainder, that.remainder) &&
                Objects.equals(vorfeldSatzglied, that.vorfeldSatzglied);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verb, remainder, vorfeldSatzglied);
    }
}
