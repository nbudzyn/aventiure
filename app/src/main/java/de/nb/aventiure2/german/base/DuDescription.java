package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.base.Joiner;

import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehts in den Wald."
 */
public class DuDescription extends AbstractDescription<DuDescription> {
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


    public static DuDescription du(final String verb,
                                   final AvTimeSpan timeElapsed) {
        return du(verb, null, timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final String verb,
                                   final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, null, timeElapsed);
    }

    public static DuDescription du(final String verb,
                                   @Nullable final String remainder,
                                   final AvTimeSpan timeElapsed) {
        return du(verb, remainder, null, timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final String verb,
                                   @Nullable final String remainder,
                                   final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, remainder, null, timeElapsed);
    }

    public static DuDescription du(final String verb,
                                   @Nullable final String remainder,
                                   @Nullable final String vorfeldSatzglied,
                                   final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied, timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final String verb,
                                   @Nullable final String remainder,
                                   @Nullable final String vorfeldSatzglied,
                                   final AvTimeSpan timeElapsed) {
        return new DuDescription(startsNew, verb, remainder, vorfeldSatzglied, timeElapsed);
    }

    private DuDescription(final StructuralElement startsNew,
                          final String verb,
                          @Nullable final String remainder,
                          @Nullable final String vorfeldSatzglied,
                          final AvTimeSpan timeElapsed) {
        // TODO Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, timeElapsed);

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
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        if (vorfeldSatzglied != null) {
            return getDescriptionHauptsatzMitSpeziellemVorfeld();
        }

        // Konjunktionaladverb ist nötig:
        // Du gehst in den Wald. Dann gehst du den Fluss entlang.
        return buildHauptsatz(konjunktionaladverb, // "dann"
                verb, // "gehst"
                Joiner.on(" ").skipNulls()
                        .join("du", remainder)); // "du den Fluss entlang"
    }

    public String getDescriptionHauptsatzMitSpeziellemVorfeld() {
        if (vorfeldSatzglied == null) {
            return getDescriptionHauptsatz();
        }

        if (remainder == null) {
            throw new IllegalStateException(
                    "Keine remainder, aber ein Vorfeldsatzglied: " + vorfeldSatzglied);
        }

        @Nullable final String remainderWithoutVorfeldSatzglied =
                cutSatzglied(remainder, vorfeldSatzglied);

        return buildHauptsatz(vorfeldSatzglied,
                verb,
                Joiner.on(" ").skipNulls()
                        .join("du", remainderWithoutVorfeldSatzglied));
    }

    @Override
    public String getDescriptionHauptsatz() {
        return buildHauptsatz("du",
                verb,
                remainder);
    }

    private static @Nullable
    String cutSatzglied(final String text, final String satzglied) {
        final int startIndex = text.indexOf(satzglied);
        if (startIndex < 0) {
            throw new IllegalArgumentException("Satzglied \"" + satzglied + "\" not contained "
                    + "in \"" + text + "\"");
        }

        @Nullable final String charBefore = startIndex == 0 ?
                null :
                text.substring(startIndex - 1, startIndex);

        final int endIndex = startIndex + satzglied.length();
        @Nullable final String charAfter = endIndex >= text.length() ?
                null :
                text.substring(endIndex, startIndex + satzglied.length() + 1);

        if (charBefore == null) {
            if (charAfter == null) {
                return null;
            }

            if (charAfter.equals(" ")) {
                return text.substring(endIndex + 1);
            }

            return text.substring(endIndex);
        }

        // charBefore != null
        if (charBefore.equals(" ")) {
            if (charAfter == null) {
                return text.substring(0, startIndex - 1);
            }

            if (charAfter.equals(" ")) {
                return text.substring(0, startIndex - 1) + text.substring(endIndex);
            }

            return text.substring(0, startIndex - 1) + " " + text.substring(endIndex);
        }

        // charBefore != null, !charBefore.equals(" ")
        if (charAfter == null) {
            return text.substring(0, startIndex);
        }

        if (charAfter.equals(" ")) {
            return text.substring(0, startIndex) + text.substring(endIndex + 1);
        }

        return text.substring(0, startIndex) + " " + text.substring(endIndex);
    }

    private static String buildHauptsatz(final String vorfeld, final String verb,
                                         @Nullable final String mittelfeldEtc) {
        return Joiner.on(" ").skipNulls().join(
                capitalize(vorfeld),
                verb,
                mittelfeldEtc);
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    public String getDescriptionSatzanschlussOhneSubjekt() {
        return verb + " " + remainder;
    }

}
