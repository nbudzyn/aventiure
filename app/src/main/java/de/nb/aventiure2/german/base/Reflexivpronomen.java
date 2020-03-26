package de.nb.aventiure2.german.base;

public class Reflexivpronomen {
    public static final Reflexivpronomen P1_SG = new Reflexivpronomen("mir", "mich");
    public static final Reflexivpronomen P2_SG = new Reflexivpronomen("dir", "dich");
    public static final Reflexivpronomen P3_SG = new Reflexivpronomen("sich");

    private final String dativ;
    private final String akkusativ;

    public Reflexivpronomen(final String dativAkkusativ) {
        this(dativAkkusativ, dativAkkusativ);
    }

    public Reflexivpronomen(final String dativ, final String akkusativ) {
        this.dativ = dativ;
        this.akkusativ = akkusativ;
    }

    public String im(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return im((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
            final PraepositionMitKasus praepositionMitKasus =
                    (PraepositionMitKasus) kasusOderPraepositionalkasus;

            return praepositionMitKasus.getPraeposition() + " " + im(
                    praepositionMitKasus.getKasus());
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
    }

    public String im(final Kasus kasus) {
        switch (kasus) {
            case DAT:
                return dat();
            case AKK:
                return akk();
            default:
                throw new IllegalArgumentException(
                        "Unexpected kasus for Reflexivpronomen: " + kasus);
        }
    }

    public String dat() {
        return dativ;
    }

    public String akk() {
        return akkusativ;
    }

}
