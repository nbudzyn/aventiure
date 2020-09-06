package de.nb.aventiure2.german.base;

import java.util.Objects;

public abstract class PronomenMitVollerFlexionsreihe
        extends SubstantivischePhrase {
    private final Flexionsreihe flexionsreihe;

    public PronomenMitVollerFlexionsreihe(final NumerusGenus numerusGenus,
                                          final Flexionsreihe flexionsreihe) {
        super(numerusGenus);
        this.flexionsreihe = flexionsreihe;
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return false;
    }

    @Override
    public String artikellosDat() {
        return dat();
    }

    @Override
    public String nom() {
        return flexionsreihe.nom();
    }

    @Override
    public String dat() {
        return flexionsreihe.dat();
    }

    @Override
    public String akk() {
        return flexionsreihe.akk();
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
        final PronomenMitVollerFlexionsreihe that = (PronomenMitVollerFlexionsreihe) o;
        return flexionsreihe.equals(that.flexionsreihe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flexionsreihe);
    }
}
