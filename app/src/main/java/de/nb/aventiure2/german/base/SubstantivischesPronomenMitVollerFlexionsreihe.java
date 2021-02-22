package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

import static de.nb.aventiure2.german.base.GermanUtil.joinToString;

public abstract class SubstantivischesPronomenMitVollerFlexionsreihe
        extends EinzelneSubstantivischePhrase {
    private final Flexionsreihe flexionsreihe;

    SubstantivischesPronomenMitVollerFlexionsreihe(final NumerusGenus numerusGenus,
                                                   final Flexionsreihe flexionsreihe,
                                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        this(null, numerusGenus, flexionsreihe, bezugsobjekt);
    }

    SubstantivischesPronomenMitVollerFlexionsreihe(final @Nullable String fokuspartikel,
                                                   final NumerusGenus numerusGenus,
                                                   final Flexionsreihe flexionsreihe,
                                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, numerusGenus, bezugsobjekt);
        this.flexionsreihe = flexionsreihe;
    }

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc.
     */
    @Override
    public abstract SubstantivischesPronomenMitVollerFlexionsreihe mitFokuspartikel(
            @Nullable final String fokuspartikel);

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return false;
    }

    boolean isWortform(final String string) {
        return flexionsreihe.hasWortform(string);
    }

    @Override
    public String artikellosDatStr() {
        return joinToString(
                getFokuspartikel(),
                datStr());
    }

    @Override
    public String nomStr() {
        return joinToString(
                getFokuspartikel(),
                flexionsreihe.nom());
    }

    @Override
    public String datStr() {
        return joinToString(getFokuspartikel(), flexionsreihe.dat());
    }

    @Override
    public String akkStr() {
        return joinToString(
                getFokuspartikel(),
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
        final SubstantivischesPronomenMitVollerFlexionsreihe that =
                (SubstantivischesPronomenMitVollerFlexionsreihe) o;
        return flexionsreihe.equals(that.flexionsreihe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flexionsreihe);
    }
}
