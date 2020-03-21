package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die dekliniert werden kann, also insbesonder ein Pronomen ("sie") oder
 * eine (andere) Nominalphrase ("die goldene Kugel").
 */
public abstract class DeklinierbarePhrase implements DescribableAsDeklinierbarePhrase {
    private final NumerusGenus numerusGenus;
    private final String nominativ;
    private final String dativ;
    private final String akkusativ;

    public DeklinierbarePhrase(final NumerusGenus numerusGenus,
                               final String nominativDativUndAkkusativ) {
        this(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }


    public DeklinierbarePhrase(final NumerusGenus numerusGenus,
                               final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public DeklinierbarePhrase(final NumerusGenus numerusGenus,
                               final String nominativ, final String dativ, final String akkusativ) {
        this.numerusGenus = numerusGenus;
        this.nominativ = nominativ;
        this.dativ = dativ;
        this.akkusativ = akkusativ;
    }

    @Override
    public String im(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                     final boolean shortIfKnown) {
        return im(kasusOderPraepositionalkasus);
    }

    @Override
    public DeklinierbarePhrase getDescription(final boolean shortIfKnown) {
        return this;
    }

    @Override
    public String nom(final boolean shortIfKnown) {
        return nom();
    }

    @Override
    public String dat(final boolean shortIfKnown) {
        return dat();
    }

    @Override
    public String akk(final boolean shortIfKnown) {
        return akk();
    }

    public String im(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return im((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
            final PraepositionMitKasus praepositionMitKasus =
                    (PraepositionMitKasus) kasusOderPraepositionalkasus;

            return praepositionMitKasus.getDescription(this);
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
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

    @Override
    public String nom() {
        return nominativ;
    }

    @Override
    public String dat() {
        return dativ;
    }

    @Override
    public String akk() {
        return akkusativ;
    }

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }
}
