package de.nb.aventiure2.german.base;

import java.util.Map;
import java.util.Objects;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static java.util.Objects.requireNonNull;

public class Flexionsreihe {

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
        this.nominativ = requireNonNull(nominativ, "nominativ");
        this.dativ = requireNonNull(dativ, "dativ");
        this.akkusativ = requireNonNull(akkusativ, "akkusativ");
    }

    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    boolean hasWortform(final String string) {
        return string.equals(nominativ) ||
                string.equals(dativ) ||
                string.equals(akkusativ);
    }

    public String im(final Kasus kasus) {
        switch (kasus) {
            case NOM:
                return nom();
            case DAT:
                return dat();
            case AKK:
                return akk();
            default:
                throw new IllegalStateException("Unexpected value: " + kasus);
        }
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Flexionsreihe that = (Flexionsreihe) o;
        return nominativ.equals(that.nominativ) &&
                dativ.equals(that.dativ) &&
                akkusativ.equals(that.akkusativ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nominativ, dativ, akkusativ);
    }
}
