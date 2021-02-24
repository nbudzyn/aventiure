package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.IKonstituenteOrStructuralElement;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehst in den Wald."
 */
public class SimpleDuDescription extends AbstractFlexibleDescription<SimpleDuDescription> {
    /**
     * This {@link Narration} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;

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
        this(startsNew,
                verb,
                remainder instanceof Konstituente ? (Konstituente) remainder : null);
    }

    /**
     * Erzeugt eine {@link SimpleDuDescription} ohne Vorfeld-Satzglied.
     *
     * @see #mitVorfeldSatzglied(String)
     */
    private SimpleDuDescription(final StructuralElement startsNew,
                                final String verb,
                                @Nullable final Konstituente remainder) {
        super();
        this.startsNew = startsNew;

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

    @Override
    public StructuralElement getStartsNew() {
        return startsNew;
    }

    @Override
    public StructuralElement getEndsThis() {
        return remainder == null ? WORD : remainder.getEndsThis();
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
            res.add(toTextDescriptionKeepParams(hauptsatzMitSpeziellemVorfeld)
                    .beginntZumindest(SENTENCE));
        }

        return res.build();
    }

    @Override
    @CheckReturnValue
    public Konstituente toSingleKonstituenteMitVorfeld(final String vorfeld) {
        return joinToKonstituentenfolge(
                getStartsNew(),
                vorfeld, // "dann"
                verb, // "gehst"
                "du", // "du"
                remainder,  // "den Fluss entlang"
                getEndsThis())
                .joinToSingleKonstituente()
                .mitPhorikKandidat(getPhorikKandidat());
    }

    @Override
    @CheckReturnValue
    public Konstituente toSingleKonstituente() {
        return joinToKonstituentenfolge(
                getStartsNew(),
                "du",
                toSingleKonstituenteSatzanschlussOhneSubjekt())
                .joinToSingleKonstituente();
    }

    @Override
    @Nullable
    @CheckReturnValue
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
                .mitPhorikKandidat(getPhorikKandidat());
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    @Override
    @CheckReturnValue
    public Konstituente toSingleKonstituenteSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(verb, remainder, getEndsThis())
                .joinToSingleKonstituente()
                .mitPhorikKandidat(getPhorikKandidat());
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

    @Override
    public SimpleDuDescription phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                                              final IBezugsobjekt bezugsobjekt) {
        checkArgument(substantivischePhrase.getPerson() == P3,
                "Substantivische Phrase %s hat falsche "
                        + "Person: %s. Für Phorik-Kandiaten "
                        + "ist nur 3. Person zugelassen.", substantivischePhrase,
                substantivischePhrase.getPerson());
        return phorikKandidat(substantivischePhrase.getNumerusGenus(), bezugsobjekt);
    }

    @Override
    public void phorikKandidat(final NumerusGenus numerusGenus,
                               final IGameObject gameObject) {
        phorikKandidat(numerusGenus, gameObject.getId());
    }

    @Override
    public SimpleDuDescription phorikKandidat(final NumerusGenus numerusGenus,
                                              final IBezugsobjekt bezugsobjekt) {
        return phorikKandidat(new PhorikKandidat(numerusGenus, bezugsobjekt));
    }

    @Override
    public SimpleDuDescription phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        if (remainder != null) {
            remainder = remainder.mitPhorikKandidat(phorikKandidat);
        }
        return this;
    }

    @Override
    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return remainder != null ? remainder.getPhorikKandidat() : null;
    }
}
