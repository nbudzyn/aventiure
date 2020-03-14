package de.nb.aventiure2.german;

public abstract class DekliniertePhrase {
    private final NumerusGenus numerusGenus;
    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    public DekliniertePhrase(final NumerusGenus numerusGenus,
                             final String nominativDativUndAkkusativ) {
        this(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }


    public DekliniertePhrase(final NumerusGenus numerusGenus,
                             final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public DekliniertePhrase(final NumerusGenus numerusGenus,
                             final String nominativ, final String dativ, final String akkusativ) {
        this.numerusGenus = numerusGenus;
        this.nominativ = nominativ;
        this.dativ = dativ;
        this.akkusativ = akkusativ;
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

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }
}
