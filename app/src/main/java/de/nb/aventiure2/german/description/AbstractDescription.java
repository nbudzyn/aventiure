package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription<SELF extends AbstractDescription<SELF>> {
    private final DescriptionParams params;

    protected AbstractDescription(final StructuralElement startsNew,
                                  final StructuralElement endsThis,
                                  @Nullable final PhorikKandidat phorikKandidat) {
        this(new DescriptionParams(startsNew, endsThis, phorikKandidat));
    }

    protected AbstractDescription(final DescriptionParams params) {
        this.params = params;
    }

    public DescriptionParams copyParams() {
        return params.copy();
    }

    public StructuralElement getStartsNew() {
        return params.getStartsNew();
    }

    public StructuralElement getEndsThis() {
        return params.getEndsThis();
    }

    public abstract ImmutableList<TextDescription> altTextDescriptions();

    @NonNull
    @CheckReturnValue
    final TextDescription toTextDescription() {
        return toTextDescriptionKeepOtherParams(toSingleKonstituente());
    }

    /**
     * Gibt die Beschreibung zurück, in der Regel beginnend mit einem Hauptsatz;
     * handelt es sich bei dieser Description jedoch um eine kleinere Einheit,
     * wird der Text dieser Description zurückgegeben.
     */
    public abstract Konstituente toSingleKonstituente();

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        return toTextDescriptionKeepOtherParams(
                toSingleKonstituenteMitKonjunktionaladverbWennNoetig(konjunktionaladverb));
    }

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    abstract Konstituente
    toSingleKonstituenteMitKonjunktionaladverbWennNoetig(String konjunktionaladverb);

    @NonNull
    public TextDescription toSatzanschlussTextDescriptionKeepOtherParams(
            final Konstituente konstituente) {
        final DescriptionParams newParams = copyParams();
        if (konstituente.getPhorikKandidat() != null) {
            newParams.phorikKandidat(konstituente.getPhorikKandidat());
        }
        newParams.setStartsNew(WORD);
        newParams.setEndsThis(konstituente.getEndsThis());

        return new TextDescription(
                newParams,
                konstituente.getText(),
                konstituente.woertlicheRedeNochOffen(), konstituente.kommaStehtAus());
    }

    @NonNull
    TextDescription toTextDescriptionKeepOtherParams(final Konstituente konstituente) {
        final DescriptionParams newParams = copyParams();
        if (konstituente.getPhorikKandidat() != null) {
            newParams.phorikKandidat(konstituente.getPhorikKandidat());
        }
        newParams.setStartsNew(konstituente.getStartsNew());
        newParams.setEndsThis(konstituente.getEndsThis());
        return new TextDescription(
                newParams,
                konstituente.getText(),
                konstituente.woertlicheRedeNochOffen(), konstituente.kommaStehtAus());
    }

    @SuppressWarnings("unchecked")
    public TimedDescription<SELF> timed(final AvTimeSpan timeElapsed) {
        return new TimedDescription<>((SELF) this, timeElapsed);
    }

    @SuppressWarnings("unchecked")
    public SELF komma() {
        komma(true);
        return (SELF) this;
    }

    public abstract SELF komma(final boolean kommaStehtAus);

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public SELF undWartest() {
        return undWartest(true);
    }

    @SuppressWarnings("unchecked")
    public SELF undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        params.undWartest(
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt);
        return (SELF) this;
    }

    public boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return params.isAllowsAdditionalDuSatzreihengliedOhneSubjekt();
    }

    public SELF dann() {
        return dann(true);
    }

    @SuppressWarnings("unchecked")
    public SELF dann(final boolean dann) {
        params.dann(dann);
        return (SELF) this;
    }

    public boolean isDann() {
        return params.isDann();
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     *
     * @param substantivischePhrase Substantivische Phrase in der dritten Person
     */
    public SELF phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                               final IBezugsobjekt bezugsobjekt) {
        checkArgument(substantivischePhrase.getPerson() == P3,
                "Substantivische Phrase %s hat falsche "
                        + "Person: %s. Für Phorik-Kandiaten "
                        + "ist nur 3. Person zugelassen.", substantivischePhrase,
                substantivischePhrase.getPerson());
        return phorikKandidat(substantivischePhrase.getNumerusGenus(), bezugsobjekt);
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public SELF phorikKandidat(final NumerusGenus numerusGenus,
                               final IBezugsobjekt bezugsobjekt) {
        return phorikKandidat(new PhorikKandidat(numerusGenus, bezugsobjekt));
    }

    @SuppressWarnings("unchecked")
    public SELF phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        params.phorikKandidat(phorikKandidat);
        return (SELF) this;
    }

    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return params.getPhorikKandidat();
    }

    /**
     * Gibt die Parameter veränderbar zurück. Das hier  wird man selten
     * aufrufen!
     */
    DescriptionParams getParamsMutable() {
        return params;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractDescription<?> that = (AbstractDescription<?>) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
