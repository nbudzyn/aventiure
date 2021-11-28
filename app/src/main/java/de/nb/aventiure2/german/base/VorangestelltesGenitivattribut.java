package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Ein Genitivattribut, das vorangestellt wird - in der Regel ein artikelloser
 * Eigenname im Genitiv ("Peters[ Haus]", "Hamburgs[ Schönheiten]") oder ein
 * wie ein Eigenname gebrauchtes Substantiv im Genitiv ("Vaters[ Schuhe]"),
 * selten andere Nominalphrasen im Genitiv ("der armen Bäuerin[ Haus]").
 * <p>
 * Vorangestellte Genitivattribute und Artikelwörter (vgl.
 * {@link ArtikelwortFlexionsspalte.Typ}) schließen einander aus.
 */
public class VorangestelltesGenitivattribut
        implements IArtikelworttypOderVorangestelltesGenitivattribut {
    private final String attribut;
    private final boolean negativ;

    public VorangestelltesGenitivattribut(final String attribut) {
        this(attribut, false);
    }

    private VorangestelltesGenitivattribut(final String attribut, final boolean negativ) {
        this.attribut = attribut;
        this.negativ = negativ;
    }

    @Override
    public VorangestelltesGenitivattributFlexionsspalte vor(
            final IErlaubtAttribute phraseDieAttributeErlaubt) {
        return vorIrgendwas();
    }

    @Override
    public VorangestelltesGenitivattributFlexionsspalte vor(final NumerusGenus numerusGenus) {
        return vorIrgendwas();
    }

    @NonNull
    private VorangestelltesGenitivattributFlexionsspalte vorIrgendwas() {
        // Egal, vor welchem Fall ein Genitivattribut wie "Rapunzels" steht, das Genitivattribut
        // ändert sich nicht.
        return new VorangestelltesGenitivattributFlexionsspalte();
    }

    @NonNull
    private String getDescription() {
        return (negativ ? "nicht " : "") + attribut;
    }

    @Override
    public boolean erlaubtVerschmelzungMitPraeposition() {
        return false;
    }

    @Override
    public boolean isNegativ() {
        return false;
    }

    @Nullable
    @Override
    public IArtikelworttypOderVorangestelltesGenitivattribut getNegativeForm() {
        // Die negative Form von "nicht Annas" ist "nicht Annas".
        // Das hat zur Folge, dass wir keine doppelten Verneinungen in der Art
        // "nicht kein Essen" erzeugen. Die "Negation" von "kein Essen" ist immer noch
        // "kein Essen".
        // Ich bin nicht sicher, warum wir das so machen.
        // Bei ArtikelwortFlexionsspalte.Typ#NEG_INDEF ist es auch so.
        return new VorangestelltesGenitivattribut(attribut, true);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VorangestelltesGenitivattribut that = (VorangestelltesGenitivattribut) o;
        return negativ == that.negativ && attribut.equals(that.attribut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribut, negativ);
    }

    @Override
    public String toString() {
        return "\"" + getDescription() + "\"";
    }

    private class VorangestelltesGenitivattributFlexionsspalte implements
            IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut {
        @Override
        public String nomStr() {
            // "Rapunzels Haus ist schön."
            return getDescription();
        }

        @Override
        public String datStr() {
            // "Wir helfen Rapunzels Mutter."
            return getDescription();
        }

        @Override
        public String akkStr() {
            // "Wir sehen Rapunzels Haus."
            return getDescription();
        }

        @Override
        public boolean traegtKasusendungFuerNominalphrasenkern(final Kasus kasus) {
            return false;
        }
    }
}
