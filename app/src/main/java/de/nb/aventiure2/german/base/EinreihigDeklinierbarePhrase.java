package de.nb.aventiure2.german.base;

import java.util.Objects;

/**
 * Eine Phrase, die auf Basis einer einzigen Formenreihe dekliniert werden kann.
 */
public abstract class EinreihigDeklinierbarePhrase {
    private final NumerusGenus numerusGenus;
    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    public EinreihigDeklinierbarePhrase(final NumerusGenus numerusGenus,
                                        final String nominativDativUndAkkusativ) {
        this(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }


    public EinreihigDeklinierbarePhrase(final NumerusGenus numerusGenus,
                                        final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public EinreihigDeklinierbarePhrase(final NumerusGenus numerusGenus,
                                        final String nominativ, final String dativ,
                                        final String akkusativ) {
        this.numerusGenus = numerusGenus;
        this.nominativ = nominativ;
        this.dativ = dativ;
        this.akkusativ = akkusativ;
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
                throw new IllegalArgumentException("Unexpected kasus: " + kasus);
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

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinreihigDeklinierbarePhrase that = (EinreihigDeklinierbarePhrase) o;
        return numerusGenus == that.numerusGenus &&
                Objects.equals(nominativ, that.nominativ) &&
                Objects.equals(dativ, that.dativ) &&
                Objects.equals(akkusativ, that.akkusativ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus, nominativ, dativ, akkusativ);
    }
}
