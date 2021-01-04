package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.Wortfolge.w;

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
     * Something like "in den Wald"
     */
    @Nullable
    private final String remainder;

    /**
     * Ein Teil von {@link #remainder}, der statt "Du" das Vorfeld einnehmen kann.
     */
    @Nullable
    private final String vorfeldSatzglied;

    SimpleDuDescription(final StructuralElement startsNew,
                        final String verb,
                        @Nullable final String remainder,
                        @Nullable final String vorfeldSatzglied,
                        final boolean woertlicheRedeNochOffen,
                        final boolean kommaStehtAus) {
        // FIXME Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, woertlicheRedeNochOffen, kommaStehtAus);
        this.verb = verb;
        this.remainder = remainder;
        this.vorfeldSatzglied = vorfeldSatzglied;

        checkArgument(vorfeldSatzglied == null || remainder != null,
                "Kein remainder, aber ein vorfeldSatzglied? Unmöglich!");

        checkArgument(vorfeldSatzglied == null || remainder.contains(vorfeldSatzglied),
                "vorfeldSatzglied nicht im remainder enthalten. Remainder: ",
                remainder + ", vorfeldSatzglied: " + vorfeldSatzglied);
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
    public Wortfolge toWortfolgeMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // FIXME Derzeit ist die Sache mit dem Komma nicht einheitlich gelöst.
        //  Gut wäre es wohl, wenn die DesriptionParams KEIN isKommaStehtAus
        //  hätten, sondern wenn diese Informatoion hier on-the-fly ermittelt würde.
        //  In der TextDescription müsste man die Information dann zusätzlich speichern,
        //  damit der Benutzer sie (nur dort?!) angeben kann.
        @Nullable final Wortfolge wortfolgeMitSpeziellemVorfeldOrNull =
                toWortfolgeMitSpeziellemVorfeldOrNull();

        if (wortfolgeMitSpeziellemVorfeldOrNull != null) {
            return wortfolgeMitSpeziellemVorfeldOrNull;
        }

        return toWortfolgeMitVorfeld(konjunktionaladverb);
    }

    @Override
    public Wortfolge toWortfolgeMitVorfeld(final String vorfeld) {
        return w(GermanUtil.buildHauptsatz(vorfeld, // "dann"
                verb, // "gehst"
                joinToString("du", remainder))); // "du den Fluss entlang"
    }

    @Override
    public Wortfolge toWortfolgeMitSpeziellemVorfeld() {
        final Wortfolge duHauptsatzMitSpeziellemVorfeldOrNull =
                toWortfolgeMitSpeziellemVorfeldOrNull();

        if (duHauptsatzMitSpeziellemVorfeldOrNull == null) {
            return toWortfolge();
        }

        return duHauptsatzMitSpeziellemVorfeldOrNull;
    }

    @Override
    public Wortfolge toWortfolge() {
        return w(GermanUtil.buildHauptsatz("du",
                verb,
                remainder), false,
                copyParams().isKommaStehtAus());
    }

    @Nullable
    private Wortfolge toWortfolgeMitSpeziellemVorfeldOrNull() {
        if (vorfeldSatzglied == null) {
            return null;
        }

        if (remainder == null) {
            throw new IllegalStateException(
                    "Kein remainder, aber ein Vorfeldsatzglied: " + vorfeldSatzglied);
        }

        @Nullable final String remainderWithoutVorfeldSatzglied =
                GermanUtil.cutFirst(remainder, vorfeldSatzglied);

        return w(GermanUtil.buildHauptsatz(vorfeldSatzglied,
                verb,
                joinToString("du", remainderWithoutVorfeldSatzglied)));
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    public Wortfolge toWortfolgeSatzanschlussOhneSubjekt() {
        return Wortfolge.joinToWortfolge(verb, remainder);
    }

    @Override
    public boolean hasSubjektDu() {
        return true;
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
        final SimpleDuDescription that = (SimpleDuDescription) o;
        return verb.equals(that.verb) &&
                Objects.equals(remainder, that.remainder) &&
                Objects.equals(vorfeldSatzglied, that.vorfeldSatzglied);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), verb, remainder, vorfeldSatzglied);
    }
}
