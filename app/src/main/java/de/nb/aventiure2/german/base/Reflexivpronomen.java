package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.annotation.CheckReturnValue;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.PL;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;

public class Reflexivpronomen implements SubstPhrOderReflexivpronomen {
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

    @Nullable
    @Override
    public String getFokuspartikel() {
        // Es wäre etwas wie "sogar sich selbst" möglich - aber nicht bei
        // echt reflexivem Gebrauch:
        // "Ich habe sogar mich selbst gewaschen.", aber
        // *"Ich habe das Buch sogar an mich selbst genommen."
        return null;
    }

    @Override
    public Reflexivpronomen ohneFokuspartikel() {
        return this;
    }

    public String imStr(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        // "sich" etc. etablieren wohl kaum einen Bezug auf Bezugsobjekt
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return imStr((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
            final PraepositionMitKasus praepositionMitKasus =
                    (PraepositionMitKasus) kasusOderPraepositionalkasus;

            return praepositionMitKasus.getPraeposition() + " " + imStr(
                    praepositionMitKasus.getKasus());
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
    }

    @Override
    public String imStr(final Kasus kasus) {
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

    @Override
    @CheckReturnValue
    public Konstituentenfolge imK(final Kasus kasus) {
        return joinToKonstituentenfolge(
                k(imStr(kasus), kannAlsBezugsobjektVerstandenWerdenFuer(),
                        getBezugsobjekt()));
    }

    @Nullable
    @Override
    public NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer() {
        // Ich glaube, ein Reflexivpronomen etabliert nicht wirklich einen Bezug
        // auf ein Bezugsobjekt.
        return null;
    }

    @Nullable
    @Override
    public IBezugsobjekt getBezugsobjekt() {
        // Ich glaube, ein Reflexivpronomen etabliert nicht wirklich einen Bezug
        // auf ein Bezugsobjekt.
        return null;
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
