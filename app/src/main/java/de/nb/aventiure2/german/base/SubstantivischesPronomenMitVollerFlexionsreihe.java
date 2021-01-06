package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

public abstract class SubstantivischesPronomenMitVollerFlexionsreihe
        extends SubstantivischePhrase {
    private final Flexionsreihe flexionsreihe;

    /**
     * Erzeugt ein substantivisches Pronomen mit voller Flexionsreihe ohne Bezugsobjekt.
     */
    SubstantivischesPronomenMitVollerFlexionsreihe(final NumerusGenus numerusGenus,
                                                   final Flexionsreihe flexionsreihe) {
        this(numerusGenus, flexionsreihe, null);
    }

    SubstantivischesPronomenMitVollerFlexionsreihe(final NumerusGenus numerusGenus,
                                                   final Flexionsreihe flexionsreihe,
                                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        super(numerusGenus, bezugsobjekt);
        this.flexionsreihe = flexionsreihe;
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return false;
    }

    boolean isWortform(final String string) {
        return flexionsreihe.hasWortform(string);
    }

    @Override
    public String artikellosDatStr() {
        return datStr();
    }

    @Override
    public String nomStr() {
        return flexionsreihe.nom();
    }

    @Override
    public String datStr() {
        return flexionsreihe.dat();
    }

    @Override
    public String akkStr() {
        return flexionsreihe.akk();
    }

    Flexionsreihe getFlexionsreihe() {
        return flexionsreihe;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SubstantivischesPronomenMitVollerFlexionsreihe
                that = (SubstantivischesPronomenMitVollerFlexionsreihe) o;
        return flexionsreihe.equals(that.flexionsreihe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flexionsreihe);
    }
}
