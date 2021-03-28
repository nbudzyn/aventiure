package de.nb.aventiure2.german.base;

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
}
