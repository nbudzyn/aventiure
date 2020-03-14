package de.nb.aventiure2.german;

public abstract class DekliniertePhrase {
    private final Genus genus;
    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    public DekliniertePhrase(final Genus genus,
                             final String nominativDativUndAkkusativ) {
        this(genus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }


    public DekliniertePhrase(final Genus genus,
                             final String nominativAkkusativ, final String dativ) {
        this(genus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public DekliniertePhrase(final Genus genus,
                             final String nominativ, final String dativ, final String akkusativ) {
        this.genus = genus;
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

    public Genus getGenus() {
        return genus;
    }
}
