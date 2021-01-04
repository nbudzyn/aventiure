package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.IBezugsobjekt;
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

    public AbstractDescription(final StructuralElement startsNew) {
        this(startsNew, false, false);
    }

    protected AbstractDescription(final StructuralElement startsNew,
                                  final boolean woertlicheRedeNochOffen,
                                  final boolean kommaStehtAus) {
        this(new DescriptionParams(startsNew, woertlicheRedeNochOffen, kommaStehtAus));
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

    @NonNull
    @CheckReturnValue
    public TextDescription toTextDescription() {
        return toTextDescriptionKeepParams(getDescriptionHauptsatz());
    }


    /**
     * Gibt die Beschreibung zurück, in der Regel beginnend mit einem Hauptsatz;
     * handelt es sich bei dieser Description jedoch um eine kleinere Einheit,
     * wird der Text dieser Description zurückgegeben.
     */
    public abstract Wortfolge getDescriptionHauptsatz();

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    public abstract TextDescription
    toTextDescriptionMitKonjunktionaladverbWennNoetig(String konjunktionaladverb);

    @NonNull
    public TextDescription toTextDescriptionKeepParams(final Wortfolge wortfolge) {
        final DescriptionParams params = copyParams();
        params.woertlicheRedeNochOffen(wortfolge.woertlicheRedeNochOffen());
        params.komma(wortfolge.kommaStehtAus());

        return new TextDescription(params, wortfolge.getString());
    }

    public SELF woertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen(true);
    }

    public SELF woertlicheRedeNochOffen(final boolean woertlicheRedeNochOffen) {
        params.woertlicheRedeNochOffen(woertlicheRedeNochOffen);
        return (SELF) this;
    }

    public SELF komma() {
        return komma(true);
    }

    public SELF komma(final boolean kommaStehtAus) {
        params.komma(kommaStehtAus);
        return (SELF) this;
    }

    public boolean isWoertlicheRedeNochOffen() {
        return params.isWoertlicheRedeNochOffen();
    }

    public boolean isKommaStehtAus() {
        return params.isKommaStehtAus();
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public SELF undWartest() {
        return undWartest(true);
    }

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

    public SELF dann(final boolean dann) {
        params.dann(dann);
        return (SELF) this;
    }

    public boolean isDann() {
        return params.isDann();
    }

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
