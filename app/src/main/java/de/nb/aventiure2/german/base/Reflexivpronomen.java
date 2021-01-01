package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static de.nb.aventiure2.german.base.Numerus.PL;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;

public class Reflexivpronomen implements SubstantivischePhraseOderReflexivpronomen {
    private static final Map<Numerus, Map<Person, Reflexivpronomen>> ALL = ImmutableMap.of(
            SG, ImmutableMap.of(
                    P1, new Reflexivpronomen("mir", "mich"),
                    P2, new Reflexivpronomen("dir", "dich"),
                    P3, new Reflexivpronomen("sich")
            ),
            PL, ImmutableMap.of(
                    P1, new Reflexivpronomen("uns"),
                    P2, new Reflexivpronomen("euch"),
                    P3, new Reflexivpronomen("sich")
            ));

    private final String dativ;
    private final String akkusativ;

    public static boolean isReflexivpronomen(final String string) {
        return ALL.values().stream()
                .flatMap(m -> m.values().stream())
                .anyMatch(p -> p.isWortform(string));
    }

    private boolean isWortform(final String string) {
        return dativ.equals(string) || akkusativ.equals(string);
    }

    public static Reflexivpronomen get(final Person person, final Numerus numerus) {
        return ALL.get(numerus).get(person);
    }

    private Reflexivpronomen(final String dativAkkusativ) {
        this(dativAkkusativ, dativAkkusativ);
    }

    private Reflexivpronomen(final String dativ, final String akkusativ) {
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

    @Override
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

    @Override
    public boolean isUnbetontesPronomen() {
        return true;
    }
}
