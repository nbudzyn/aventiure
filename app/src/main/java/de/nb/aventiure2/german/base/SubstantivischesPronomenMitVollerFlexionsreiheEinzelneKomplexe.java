package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

import static de.nb.aventiure2.german.base.GermanUtil.joinToString;

public abstract class SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe
        extends EinzelneKomplexeSubstantivischePhrase {
    private final Flexionsreihe flexionsreihe;

    SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe(final NumerusGenus numerusGenus,
                                                                   @Nullable
                                                                   final Negationspartikelphrase negationspartikelphrase,
                                                                   final Flexionsreihe flexionsreihe,
                                                                   @Nullable
                                                                   final IBezugsobjekt bezugsobjekt) {
        this(null, negationspartikelphrase, numerusGenus, flexionsreihe, bezugsobjekt);
    }

    SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe(
            final @Nullable String fokuspartikel,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            final NumerusGenus numerusGenus,
            final Flexionsreihe flexionsreihe,
            @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, negationspartikelphrase, numerusGenus, bezugsobjekt);
        this.flexionsreihe = flexionsreihe;
    }

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc.
     */
    @Override
    public abstract SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe mitFokuspartikel(
            @Nullable final String fokuspartikel);

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return false;
    }

    @Override
    public String artikellosDatStr() {
        return joinToString(getFokuspartikel(), datStr());
    }

    @Override
    public String artikellosAkkStr() {
        return joinToString(getFokuspartikel(), akkStr());
    }

    @Override
    public String nomStr() {
        return joinToString(
                getFokuspartikel(),
                getNegationspartikelphrase(),
                flexionsreihe.nom());
    }

    @Override
    public String datStr() {
        return joinToString(getFokuspartikel(), getNegationspartikelphrase(),
                flexionsreihe.dat());
    }

    @Override
    public String akkStr() {
        return joinToString(
                getFokuspartikel(),
                getNegationspartikelphrase(),
                flexionsreihe.akk());
    }

    Flexionsreihe getFlexionsreihe() {
        return flexionsreihe;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe that =
                (SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe) o;
        return flexionsreihe.equals(that.flexionsreihe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flexionsreihe);
    }
}
