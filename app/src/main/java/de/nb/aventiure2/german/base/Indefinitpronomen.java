package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.leseratte.FederkielUtil;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.IndefinitpronomenFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.SubstantivPronomenUtil;
import de.nb.federkiel.deutsch.lexikon.GermanPOS;
import de.nb.federkiel.interfaces.IWordForm;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Ein Pronomen wie "alles", "nichts".
 */
@ParametersAreNonnullByDefault
public class Indefinitpronomen
        extends SubstantivischesPronomenMitVollerFlexionsreihe
        implements IErlaubtAttribute {
    public static final Indefinitpronomen ALLES =
            ip(N, Relativpronomen.Typ.WERWAS, fr("alles", "allem"));
    public static final Indefinitpronomen NICHTS =
            // Dativ: "Von NICHTS kommt nichts."
            ip(N, Relativpronomen.Typ.WERWAS, fr("nichts"));

    // REFACTOR Überlegen: Was ist die bessere Implementierung für "welcher"? Andere löschen.

    /**
     * "Welcher" / "Welcher" im Sinne von "etwas" / "einige", z.B.
     * - "Haben wir noch Wein? - Im Keller ist noch welcher." (nicht zählbar)
     * - "Wo liegen die Schwerter? - In der Waffenkammer sind welche." (zählbar)
     */
    public static final Map<NumerusGenus, Indefinitpronomen> WELCHER1 = ImmutableMap.of(
            M, ip(M, Relativpronomen.Typ.REGEL,
                    fr("welcher", "welchem", "welchen")),
            F, ip(F, Relativpronomen.Typ.REGEL,
                    fr("welche", "welcher")),
            N, ip(N, Relativpronomen.Typ.REGEL,
                    fr("welches", "welchem")),
            PL_MFN, ip(PL_MFN, Relativpronomen.Typ.REGEL,
                    fr("welche", "welchen")));

    static final Map<NumerusGenus, Indefinitpronomen> WELCHER3;

    static {
        final IndefinitpronomenFlektierer flekt = new IndefinitpronomenFlektierer();
        final String POS = GermanPOS.PIS.toString();

        final Collection<IWordForm> wortformen = flekt.typDieser(
                SubstantivPronomenUtil.createIndefinitpronomen(POS, "welches"),
                POS, false, // keine "Stärke"
                VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, true);

        final ImmutableMap.Builder<NumerusGenus, Indefinitpronomen> res = ImmutableMap.builder();

        for (final NumerusGenus numerusGenus : NumerusGenus.values()) {
            res.put(numerusGenus, ip(numerusGenus, Relativpronomen.Typ.REGEL,
                    extractFlexionsreihe(wortformen, numerusGenus)));
        }

        WELCHER3 = res.build();
    }

    /**
     * Mit welchem Typ von Relativpronomen steht das Indefinitpronomen?
     * ("alles, was"; "es gibt nichts, was mir fehlt")
     */
    private final Relativpronomen.Typ relPronTyp;

    public static Map<NumerusGenus, Indefinitpronomen> welch2() {
        return ImmutableMap.of(
                M, ip(M, Relativpronomen.Typ.REGEL,
                        fr("welcher", "welchem", "welchen")),
                F, ip(F, Relativpronomen.Typ.REGEL,
                        fr("welche", "welcher")),
                N, ip(N, Relativpronomen.Typ.REGEL,
                        fr("welches", "welchem")),
                PL_MFN, ip(PL_MFN, Relativpronomen.Typ.REGEL,
                        fr("welche", "welchen")));
    }

    private static Flexionsreihe extractFlexionsreihe(
            final Collection<IWordForm> wortformen, final NumerusGenus numerusGenus) {
        final BinaryOperator<IWordForm> chooseBest = Indefinitpronomen::chooseBest;
        return fr(
                FederkielUtil.extractFlextionsreihe(wortformen, numerusGenus, chooseBest));
    }

    private static IWordForm chooseBest(final IWordForm one, final IWordForm other) {
        throw new IllegalStateException("Unklar, welche Wortform zu wählen ist: " +
                one + " oder " + other);
    }

    private static Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                        final Relativpronomen.Typ relPronTyp,
                                        final Flexionsreihe flextionsreihe) {
        return new Indefinitpronomen(numerusGenus, relPronTyp, flextionsreihe);
    }

    private Indefinitpronomen(final NumerusGenus numerusGenus,
                              final Relativpronomen.Typ relPronTyp,
                              final Flexionsreihe flextionsreihe) {
        super(numerusGenus, flextionsreihe);
        this.relPronTyp = relPronTyp;
    }

    @Override
    public Personalpronomen persPron() {
        // "Ich habe mir alles angesehen. Es hat mir gefallen."
        return Personalpronomen.get(P3, getNumerusGenus());
    }

    @Override
    public Reflexivpronomen reflPron() {
        // "Alles ändert sich"
        return Reflexivpronomen.get(P3, getNumerusGenus().getNumerus());
    }

    @Override
    public Possessivartikel possArt() {
        // "[Haben wir noch Wein? - Im Keller ist noch welcher. ]Sein[ Geruch ist unverkennbar.]"
        return Possessivartikel.get(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(relPronTyp, P3, getNumerusGenus());
    }

    @Override
    public Person getPerson() {
        return P3;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Indefinitpronomen that = (Indefinitpronomen) o;
        return relPronTyp == that.relPronTyp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relPronTyp);
    }
}
