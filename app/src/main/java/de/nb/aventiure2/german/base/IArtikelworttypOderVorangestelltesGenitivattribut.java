package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

/**
 * Ein Artikelwort (genauer gesagt: die Flexionsspalte eines Artikelworts  für einen gewissen
 * Numerus und ein gewisses Genus, "kein") oder Genitivattribut ("Peters"), das <i>vorangestellt</i>
 * wird.
 * <p>
 * Vorangestellte Genitivattribute und Artikelwörter (vgl. {@link ArtikelwortFlexionsspalte.Typ})
 * schließen einander aus.
 */
public interface IArtikelworttypOderVorangestelltesGenitivattribut {
    static boolean isNegativ(@Nullable final
                             IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut) {
        if (artikelworttypOderVorangestelltesGenitivattribut == null) {
            return false;
        }

        return artikelworttypOderVorangestelltesGenitivattribut.isNegativ();
    }

    @Nullable
    static IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut vorNumerusGenus(
            @Nullable final
            IArtikelworttypOderVorangestelltesGenitivattribut
                    artikelworttypOderVorangestelltesGenitivattribut,
            final NumerusGenus numerusGenus) {
        if (numerusGenus == null) {
            return null;
        }

        return artikelworttypOderVorangestelltesGenitivattribut.vor(numerusGenus);
    }

    @Nullable
    static IArtikelworttypOderVorangestelltesGenitivattribut getNegativeForm(
            @Nullable final
            IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut) {
        if (artikelworttypOderVorangestelltesGenitivattribut == null) {
            return null;
        }

        return artikelworttypOderVorangestelltesGenitivattribut.getNegativeForm();
    }

    IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut vor(
            IErlaubtAttribute phraseDieAttributeErlaubt);

    IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut vor(NumerusGenus numerusGenus);

    boolean erlaubtVerschmelzungMitPraeposition();

    boolean isNegativ();

    @Nullable
    IArtikelworttypOderVorangestelltesGenitivattribut getNegativeForm();
}
