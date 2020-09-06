package de.nb.aventiure2.german.base;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.N;

/**
 * Ein Pronomen wie "alles", "nichts".
 */
@ParametersAreNonnullByDefault
public class Indefinitpronomen extends PronomenMitVollerFlexionsreihe {
    public static final Indefinitpronomen ALLES =
            ip(N, Relativpronomen.Typ.WERWAS, fr("alles", "allem"));
    public static final Indefinitpronomen NICHTS =
            // Dativ: "Von NICHTS kommt nichts."
            ip(N, Relativpronomen.Typ.WERWAS, fr("nichts"));

    /**
     * Mit welchem Typ von Relativpronomen steht das Indefinitpronomen?
     * ("alles, was"; "es gibt nichts, was mir fehlt")
     */
    private final Relativpronomen.Typ relPronTyp;

    public static Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                       final Relativpronomen.Typ relPronTyp,
                                       final Flexionsreihe flextionsreihe) {
        return new Indefinitpronomen(numerusGenus, relPronTyp, flextionsreihe);
    }

    public Indefinitpronomen(final NumerusGenus numerusGenus,
                             final Relativpronomen.Typ relPronTyp,
                             final Flexionsreihe flextionsreihe) {
        super(numerusGenus, flextionsreihe);
        this.relPronTyp = relPronTyp;
    }

    @Override
    public Personalpronomen persPron() {
        // "Ich habe mir alles angesehen. Es hat mir gefallen."
        return Personalpronomen.get(getNumerusGenus());
    }

    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(relPronTyp, getNumerusGenus());
    }

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
        final Indefinitpronomen that = (Indefinitpronomen) o;
        return relPronTyp == that.relPronTyp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relPronTyp);
    }
}
