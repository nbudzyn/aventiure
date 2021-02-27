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
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription<SELF extends AbstractDescription<SELF>> {
    private final DescriptionParams params;

    protected AbstractDescription() {
        this(new DescriptionParams(false, false, false));
    }

    protected AbstractDescription(final DescriptionParams params) {
        this.params = params;
    }

    public abstract StructuralElement getStartsNew();

    public abstract StructuralElement getEndsThis();

    @NonNull
    @CheckReturnValue
    ImmutableList<TextDescription> altMitPraefix(final String praefix) {
        return altMitPraefix(k(praefix));
    }

    @NonNull
    @CheckReturnValue
    public ImmutableList<TextDescription> altMitPraefix(
            final Konstituentenfolge praefixKonstituentenfolge) {
        return altMitPraefix(praefixKonstituentenfolge.joinToSingleKonstituente());
    }

    @NonNull
    @CheckReturnValue
    ImmutableList<TextDescription> altMitPraefix(final Konstituente praefixKonstituente) {
        return altTextDescriptions().stream()
                .map(d -> d.mitPraefix(praefixKonstituente))
                .collect(toImmutableList());
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     */
    @NonNull
    @CheckReturnValue
    public TextDescription mitPraefix(final String praefix) {
        return mitPraefix(k(praefix));
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     */
    @NonNull
    @CheckReturnValue
    public TextDescription mitPraefix(final Konstituentenfolge praefixKonstituentenfolge) {
        return mitPraefix(praefixKonstituentenfolge.joinToSingleKonstituente());
    }

    /**
     * Gibt eine neue <code>TextDescription</code> zurück, die um dieses Präfix
     * ergänzt ist. Hier wird also keinesfalls ein Satzglied in das Vorfeld gestellt
     * oder Ähnliches, sondern es wird rein mechanisch ein Präfix vorangestellt.
     */
    @NonNull
    @CheckReturnValue
    TextDescription mitPraefix(final Konstituente praefixKonstituente) {
        return toTextDescription().mitPraefix(praefixKonstituente);
    }

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
    TextDescription toTextDescriptionKeepParams(final Konstituente konstituente) {
        return new TextDescription(params.copy(), konstituente);
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

    public SELF schonLaenger() {
        return schonLaenger(true);
    }

    @SuppressWarnings("unchecked")
    SELF schonLaenger(final boolean schonLaenger) {
        params.schonLaenger(schonLaenger);
        return (SELF) this;
    }

    public boolean isSchonLaenger() {
        return params.isSchonLaenger();
    }

    public SELF phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                               final IBezugsobjekt bezugsobjekt) {
        checkArgument(substantivischePhrase.getPerson() == P3,
                "Substantivische Phrase %s hat falsche "
                        + "Person: %s. Für Phorik-Kandiaten "
                        + "ist nur 3. Person zugelassen.", substantivischePhrase,
                substantivischePhrase.getPerson());
        return phorikKandidat(substantivischePhrase.getNumerusGenus(), bezugsobjekt);
    }

    public SELF phorikKandidat(final NumerusGenus numerusGenus,
                               final IGameObject gameObject) {
        return phorikKandidat(numerusGenus, gameObject.getId());
    }

    public SELF phorikKandidat(final NumerusGenus numerusGenus,
                               final IBezugsobjekt bezugsobjekt) {
        return phorikKandidat(new PhorikKandidat(numerusGenus, bezugsobjekt));
    }

    protected abstract SELF phorikKandidat(PhorikKandidat phorikKandidat);

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
