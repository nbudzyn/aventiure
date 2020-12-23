package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.Person.P3;

public class Interrogativpronomen extends SubstantivischesPronomenMitVollerFlexionsreihe {
    public static final Interrogativpronomen WER =
            ip(M, fr("wer", "wem", "wen"));
    public static final Interrogativpronomen WAS =
            ip(N, fr("was", "wem"));

    private static Interrogativpronomen ip(final NumerusGenus numerusGenus,
                                           final Flexionsreihe flextionsreihe) {
        return new Interrogativpronomen(numerusGenus, flextionsreihe);
    }

    private Interrogativpronomen(final NumerusGenus numerusGenus,
                                 final Flexionsreihe flextionsreihe) {
        super(numerusGenus, flextionsreihe);
    }

    @Override
    public Personalpronomen persPron() {
        // "Was...? Es..."
        return Personalpronomen.get(P3, getNumerusGenus());
    }

    @Override
    public Reflexivpronomen reflPron() {
        // "Was ändert sich?"
        return Reflexivpronomen.get(P3, getNumerusGenus().getNumerus());
    }

    @Override
    public Possessivartikel possArt() {
        // "Was ist das? Sein..."
        return Possessivartikel.get(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        // "Wer, der... , würde...?
        // "Was, das... , würde...?
        return Relativpronomen.get(P3, getNumerusGenus());
    }

    @Override
    public Person getPerson() {
        return P3;
    }
}
