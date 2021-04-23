package de.nb.aventiure2.german.base;

import java.util.Objects;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;

public class Endungen {
    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    public Endungen(final String nominativDativUndAkkusativ) {
        this(nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }

    public Endungen(final String nominativAkkusativ, final String dativ) {
        this(nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public Endungen(final String nominativ, final String dativ, final String akkusativ) {
        this.nominativ = nominativ;
        this.dativ = dativ;
        this.akkusativ = akkusativ;
    }

    public Flexionsreihe buildFlexionsreihe(final String stamm) {
        return fr(
                stamm + nominativ,
                stamm + dativ,
                stamm + akkusativ
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Endungen endungen = (Endungen) o;
        return nominativ.equals(endungen.nominativ) &&
                dativ.equals(endungen.dativ) &&
                akkusativ.equals(endungen.akkusativ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nominativ, dativ, akkusativ);
    }
}
