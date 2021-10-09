package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.Nullable;

public class Interrogativpronomen
        extends SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe
        implements IInterrogativwort {
    @SuppressWarnings({"unused", "RedundantSuppression"})
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
        this(numerusGenus, null, flextionsreihe);
    }

    private Interrogativpronomen(final NumerusGenus numerusGenus,
                                 @Nullable final Negationspartikelphrase negationspartikelphrase,
                                 final Flexionsreihe flextionsreihe) {
        super(numerusGenus, negationspartikelphrase, flextionsreihe, null);
    }

    /**
     * Die Fokuspartikel wird verworfen. Interrogativpronomen können wohl keine
     * Fokuspartikeln haben.
     */
    @Override
    public Interrogativpronomen mitFokuspartikel(
            @Nullable final String fokuspartikel) {
        return this;
    }

    @Override
    public SubstantivischePhrase ohneNegationspartikelphrase() {
        if (getNegationspartikelphrase() == null) {
            return this;
        }

        return new Interrogativpronomen(getNumerusGenus(), null,
                getFlexionsreihe());
    }

    @Override
    public SubstantivischePhrase neg(final Negationspartikelphrase negationspartikelphrase,
                                     final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        return new Interrogativpronomen(getNumerusGenus(), negationspartikelphrase,
                getFlexionsreihe());
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
    public ArtikelFlexionsspalte.Typ possArt() {
        // "Was ist das? Sein..."
        return ArtikelFlexionsspalte.getPossessiv(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        // "Wer, der... , würde...?
        // "Was, das... , würde...?
        return Relativpronomen.get(P3, getNumerusGenus());
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return false;
    }

    @Override
    public Person getPerson() {
        return P3;
    }
}
