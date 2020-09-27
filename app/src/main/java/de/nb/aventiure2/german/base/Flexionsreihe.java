package de.nb.aventiure2.german.base;

class Flexionsreihe {
    static Flexionsreihe fr(
            final String nominativDativUndAkkusativ) {
        return fr(nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }

    static Flexionsreihe fr(
            final String nominativUndAkkusativ, final String dativ) {
        return fr(nominativUndAkkusativ, dativ, nominativUndAkkusativ);
    }

    static Flexionsreihe fr(
            final String nominativ, final String dativ, final String akkusativ) {
        return new Flexionsreihe(nominativ, dativ, akkusativ);
    }

    private Flexionsreihe(final String nominativ, final String dativ, final String akkusativ) {
        this.nominativ = nominativ;
        this.dativ = dativ;
        this.akkusativ = akkusativ;
    }

    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    public boolean hasWortform(final String string) {
        return string.equals(nominativ) ||
                string.equals(dativ) ||
                string.equals(akkusativ);
    }

    public String nom() {
        return nominativ;
    }

    public String dat() {
        return dativ;
    }

    public String akk() {
        return akkusativ;
    }
}
