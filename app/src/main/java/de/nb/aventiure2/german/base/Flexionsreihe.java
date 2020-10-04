package de.nb.aventiure2.german.base;

import java.util.Map;
import java.util.Objects;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;

class Flexionsreihe {
    /**
     * Erzeugt eine Flexionsriehe - Nominativ, Dativ und Akkusativ müssen
     * angegeben sein.
     */
    static Flexionsreihe fr(final Map<Kasus, String> wortformen) {
        return fr(wortformen.get(NOM),
                wortformen.get(DAT),
                wortformen.get(AKK));
    }

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
        this.nominativ = Objects.requireNonNull(nominativ, "nominativ");
        this.dativ = Objects.requireNonNull(dativ, "dativ");
        this.akkusativ = Objects.requireNonNull(akkusativ, "akkusativ");
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
