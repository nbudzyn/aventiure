package de.nb.aventiure2.german.base;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.federkiel.FederkielUtil;
import de.nb.federkiel.deutsch.grammatik.kategorie.VorgabeFuerNachfolgendesAdjektiv;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.IndefinitpronomenFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.SubstantivPronomenUtil;
import de.nb.federkiel.deutsch.lexikon.GermanPOS;
import de.nb.federkiel.interfaces.IWordForm;

/**
 * Ein Pronomen wie "alles", "nichts". Zu Indefinit-Artikeln ("einige( Leute)", "etwas( Wein"), ...)
 * siehe {@link ArtikelwortFlexionsspalte.Typ}.
 */
@ParametersAreNonnullByDefault
public class Indefinitpronomen
        extends SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe
        implements IErlaubtAttribute {
    public static final Indefinitpronomen ALLES =
            ip(N, Relativpronomen.Typ.WERWAS, fr("alles", "allem"));
    public static final Indefinitpronomen NICHTS =
            // Dativ: "Von NICHTS kommt nichts."
            ip(N, Relativpronomen.Typ.WERWAS, fr("nichts"));

    private static final Map<NumerusGenus, Indefinitpronomen> WELCHER_UNBELEBT;

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

        WELCHER_UNBELEBT = res.build();
    }

    /**
     * Mit welchem Typ von Relativpronomen steht das Indefinitpronomen?
     * ("alles, was"; "es gibt nichts, was mir fehlt")
     */
    private final Relativpronomen.Typ relPronTyp;

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
        this(numerusGenus, null, relPronTyp, flextionsreihe, UNBELEBT);
    }

    private Indefinitpronomen(final NumerusGenus numerusGenus,
                              @Nullable final Negationspartikelphrase negationspartikelphrase,
                              final Relativpronomen.Typ relPronTyp,
                              final Flexionsreihe flextionsreihe,
                              final Belebtheit belebtheit) {
        super(numerusGenus, negationspartikelphrase, flextionsreihe, belebtheit, null);
        this.relPronTyp = relPronTyp;
    }

    public static Indefinitpronomen getWelcher(final NumerusGenus numerusGenus,
                                               final Belebtheit belebtheit) {
        return requireNonNull(WELCHER_UNBELEBT.get(numerusGenus)).mitBelebtheit(belebtheit);
    }

    /**
     * Die Fokuspartikel wird verworfen. Indefinitpronomen können wohl keine
     * Fokuspartikeln haben.
     */
    @Override
    public Indefinitpronomen mitFokuspartikel(@Nullable final String fokuspartikel) {
        return this;
    }

    private Indefinitpronomen mitBelebtheit(final Belebtheit belebtheit) {
        if (belebtheit == getBelebtheit()) {
            return this;
        }

        return new Indefinitpronomen(getNumerusGenus(),
                getNegationspartikelphrase(), relPronTyp, getFlexionsreihe(),
                belebtheit);
    }

    @Override
    public Indefinitpronomen ohneNegationspartikelphrase() {
        if (getNegationspartikelphrase() == null) {
            return this;
        }

        return new Indefinitpronomen(getNumerusGenus(), null, relPronTyp,
                getFlexionsreihe(), getBelebtheit());
    }

    @Override
    public Indefinitpronomen neg(final Negationspartikelphrase negationspartikelphrase,
                                 final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        return new Indefinitpronomen(getNumerusGenus(), negationspartikelphrase, relPronTyp,
                getFlexionsreihe(), getBelebtheit());
    }

    @Override
    public Personalpronomen persPron() {
        // "Ich habe mir alles angesehen. Es hat mir gefallen."
        return Personalpronomen.get(P3, getNumerusGenus(), getBelebtheit());
    }

    @Override
    public Reflexivpronomen reflPron() {
        // "Alles ändert sich"
        return Reflexivpronomen.get(
                new PraedRegMerkmale(
                        P3, getNumerusGenus().getNumerus(), getBelebtheit()));
    }

    @Override
    public IArtikelworttypOderVorangestelltesGenitivattribut possArt() {
        // "[Haben wir noch Wein? - Im Keller ist noch welcher. ]Sein[ Geruch ist unverkennbar.]"
        return ArtikelwortFlexionsspalte.getPossessiv(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(relPronTyp, P3, getBelebtheit(), getNumerusGenus());
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
    public boolean isUnbetontesPronomen() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relPronTyp);
    }
}
