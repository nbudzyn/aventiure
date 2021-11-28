package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Belebtheit.BELEBT;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
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
            ip(M, fr("wer", "wem", "wen"), BELEBT);
    public static final Interrogativpronomen WAS =
            ip(N, fr("was", "wem"), UNBELEBT);

    private static Interrogativpronomen ip(final NumerusGenus numerusGenus,
                                           final Flexionsreihe flextionsreihe,
                                           final Belebtheit belebtheit) {
        return new Interrogativpronomen(numerusGenus, flextionsreihe, belebtheit);
    }

    private Interrogativpronomen(final NumerusGenus numerusGenus,
                                 final Flexionsreihe flextionsreihe,
                                 final Belebtheit belebtheit) {
        this(numerusGenus, null, flextionsreihe, belebtheit);
    }

    private Interrogativpronomen(final NumerusGenus numerusGenus,
                                 @Nullable final Negationspartikelphrase negationspartikelphrase,
                                 final Flexionsreihe flextionsreihe,
                                 final Belebtheit belebtheit) {
        super(numerusGenus, negationspartikelphrase, flextionsreihe, belebtheit, null);
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
                getFlexionsreihe(), getBelebtheit());
    }

    @Override
    public SubstantivischePhrase neg(final Negationspartikelphrase negationspartikelphrase,
                                     final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        return new Interrogativpronomen(getNumerusGenus(), negationspartikelphrase,
                getFlexionsreihe(), getBelebtheit());
    }

    @Override
    public Personalpronomen persPron() {
        // "Was...? Es..."
        return Personalpronomen.get(P3, getNumerusGenus(), getBelebtheit());
    }

    @Override
    public Reflexivpronomen reflPron() {
        // "Was ändert sich?"
        return Reflexivpronomen.get(new PraedRegMerkmale(
                P3, getNumerusGenus().getNumerus(), getBelebtheit()));
    }

    @Override
    public IArtikelworttypOderVorangestelltesGenitivattribut possArt() {
        // "Was ist das? Sein..."
        return ArtikelwortFlexionsspalte.getPossessiv(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        // "Wer, der... , würde...?
        // "Was, das... , würde...?
        return Relativpronomen.get(P3, getNumerusGenus(), getBelebtheit());
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
