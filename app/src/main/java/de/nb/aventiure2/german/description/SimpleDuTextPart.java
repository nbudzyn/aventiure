package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.praedikat.AbstractDuTextPart;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;

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
    public String getDuHauptsatzMitVorfeld(final String vorfeld) {
        return GermanUtil.buildHauptsatz(vorfeld, // "dann"
                verb, // "gehst"
                GermanUtil.joinToNullString("du", remainder)); // "du den Fluss entlang"
    }

    @Override
    public String getDuHauptsatzMitSpeziellemVorfeld() {
        if (vorfeldSatzglied == null) {
            return getDuHauptsatz();
        }

        if (remainder == null) {
            throw new IllegalStateException(
                    "Keine remainder, aber ein Vorfeldsatzglied: " + vorfeldSatzglied);
        }

        @Nullable final String remainderWithoutVorfeldSatzglied =
                GermanUtil.cutSatzglied(remainder, vorfeldSatzglied);

        return GermanUtil.buildHauptsatz(vorfeldSatzglied,
                verb,
                joinToNullString("du", remainderWithoutVorfeldSatzglied));
    }

    @Override
    public String getDuHauptsatz() {
        return GermanUtil.buildHauptsatz("du",
                verb,
                remainder);
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    public String getDuSatzanschlussOhneSubjekt() {
        return GermanUtil.joinToNullString(verb, remainder);
    }
}
