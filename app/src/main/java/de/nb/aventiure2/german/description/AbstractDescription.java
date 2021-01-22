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
import de.nb.aventiure2.german.base.Wortfolge;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription<SELF extends AbstractDescription<SELF>> {
    private final DescriptionParams params;

    protected AbstractDescription(final StructuralElement startsNew,
                                  @Nullable final PhorikKandidat phorikKandidat) {
        this(new DescriptionParams(startsNew, phorikKandidat));
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

    public abstract ImmutableList<TextDescription> altTextDescriptions();

    @NonNull
    @CheckReturnValue
    final TextDescription toTextDescription() {
        return toTextDescriptionKeepParams(toWortfolge());
    }

    /**
     * Gibt die Beschreibung zurück, in der Regel beginnend mit einem Hauptsatz;
     * handelt es sich bei dieser Description jedoch um eine kleinere Einheit,
     * wird der Text dieser Description zurückgegeben.
     */
    public abstract Wortfolge toWortfolge();

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    @NonNull
    @CheckReturnValue
    public final TextDescription toTextDescriptionMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        return toTextDescriptionKeepParams(
                toWortfolgeMitKonjunktionaladverbWennNoetig(konjunktionaladverb));
    }

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    abstract Wortfolge
    toWortfolgeMitKonjunktionaladverbWennNoetig(String konjunktionaladverb);

    @NonNull
    public TextDescription toTextDescriptionKeepParams(final Konstituente konstituente) {
        // IDEA Hier den PhorikKandidaten aus der Konstituente
        //  übernehmen und hier separat speichern?
        //  Aus den params entfernen und params unverändert übergeben?
        final DescriptionParams newParams = copyParams();
        if (konstituente.getPhorikKandidat() != null) {
            newParams.phorikKandidat(konstituente.getPhorikKandidat());
        }
        return new TextDescription(
                newParams,
                konstituente.getString(),
                konstituente.woertlicheRedeNochOffen(), konstituente.kommaStehtAus());
    }

    @NonNull
    public TextDescription toTextDescriptionKeepParams(final Wortfolge wortfolge) {
        // IDEA Hier den PhorikKandidaten aus der Wortfolge
        //  übernehmen und hier separat speichern?
        //  Aus den params entfernen und params unverändert übergeben?
        final DescriptionParams newParams = copyParams();
        if (wortfolge.getPhorikKandidat() != null) {
            newParams.phorikKandidat(wortfolge.getPhorikKandidat());
        }
        return new TextDescription(
                newParams,
                wortfolge.getString(),
                wortfolge.woertlicheRedeNochOffen(), wortfolge.kommaStehtAus());
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

    @SuppressWarnings("unchecked")
    public SELF beendet(final StructuralElement structuralElement) {
        params.beendet(structuralElement);
        return (SELF) this;
    }

    public StructuralElement getEndsThis() {
        return params.getEndsThis();
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
                "Substantivische Phrase " + substantivischePhrase + " hat falsche "
                        + "Person: " + substantivischePhrase.getPerson() + ". Für Phorik-Kandiaten "
                        + "ist nur 3. Person zugelassen.");
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
