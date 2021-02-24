package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription<SELF extends AbstractDescription<SELF>> {
    private final DescriptionParams params;

    protected AbstractDescription() {
        this(new DescriptionParams(false, false));
    }

    protected AbstractDescription(final DescriptionParams params) {
        this.params = params;
    }

    public DescriptionParams copyParams() {
        return params.copy();
    }

    public abstract StructuralElement getStartsNew();

    public abstract StructuralElement getEndsThis();

    public abstract ImmutableList<TextDescription> altTextDescriptions();

    @NonNull
    @CheckReturnValue
    final TextDescription toTextDescription() {
        return toTextDescriptionKeepParams(toSingleKonstituente());
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
        return toTextDescriptionKeepParams(
                toSingleKonstituenteMitKonjunktionaladverbWennNoetig(konjunktionaladverb));
    }

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    abstract Konstituente
    toSingleKonstituenteMitKonjunktionaladverbWennNoetig(String konjunktionaladverb);

    @NonNull
    public TextDescription toSatzanschlussTextDescriptionKeepParams(
            final Konstituente konstituente) {
        // FIXME Prüfen: Funktioniert das noch wie gewünscht?
        //  Ggf. entfernen
        return new TextDescription(copyParams(), konstituente);
    }

    @NonNull
    TextDescription toTextDescriptionKeepParams(final Konstituente konstituente) {
        return new TextDescription(copyParams(), konstituente);
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
    public abstract SELF phorikKandidat(SubstantivischePhrase substantivischePhrase,
                                        IBezugsobjekt bezugsobjekt);

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public abstract void phorikKandidat(NumerusGenus numerusGenus,
                                        IGameObject gameObject);

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public abstract SELF phorikKandidat(NumerusGenus numerusGenus,
                                        IBezugsobjekt bezugsobjekt);

    public abstract SELF phorikKandidat(@Nullable PhorikKandidat phorikKandidat);

    @Nullable
    public abstract PhorikKandidat getPhorikKandidat();

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
