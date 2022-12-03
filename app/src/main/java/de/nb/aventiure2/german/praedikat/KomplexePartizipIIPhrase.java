package de.nb.aventiure2.german.praedikat;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine komplexe unflektierte Phrase mit Partizip II, einschließlich der Information,
 * welche Hilfsverb verlangt ist: "zu verstehen gegeben",
 * "nachzudenken gehabt".
 */
public class KomplexePartizipIIPhrase implements PartizipIIPhrase {
    /**
     * Das unflektierte Partizip II ("gegeben", "gehabt", "gewesen" etc.).
     */
    private final String partizipII;

    /**
     * Welche Hilfsverb ist verlangt - "zu verstehen gegeben [haben]",
     * oder "[genervt gewesen ]sein"?
     */
    private final Perfektbildung perfektbildung;

    /**
     * Die Infinitive, Partizipien etc., die als nächste Ebene eingeschachtelt sind.
     */
    private final ImmutableList<IInfinitesPraedikat> children;


    /**
     * Schachtelt ein bestehende Partizip II in ein äußeres Partizip II
     * des entsprechenden Hilfsverbs ein.
     * Beispiele:
     * <ul>
     * <li>Schachtelt "ein guter Mensch geworden" ein in das
     *      Modalverb-Partizip "gewesen": "ein guter Mensch geworden gewesen".
     * </ul>
     * <p>
     * Man sollte schon einen sehr guten Grund haben, so etwas zu erzeugen.
     */
    static KomplexePartizipIIPhrase doppeltesPartizipII(
            final PartizipIIOderErsatzInfinitivPhrase lexikalischerKern) {
        // Schachtelt "ein guter Mensch geworden" ein in das
        // Modalverb-Partizip "gewesen": "ein guter Mensch geworden gewesen".
        return new KomplexePartizipIIPhrase(
                lexikalischerKern.getHilfsverbFuerPerfekt().getPartizipII(),// "gewesen",
                lexikalischerKern.getHilfsverbFuerPerfekt().getPerfektbildung(), // (sein)
                lexikalischerKern); // "ein guter Mensch geworden"
    }

    private KomplexePartizipIIPhrase(final String partizipII,
                                     final Perfektbildung perfektbildung,
                                     final IInfinitesPraedikat child) {
        this(partizipII, perfektbildung, ImmutableList.of(child));
    }

    KomplexePartizipIIPhrase(final String partizipII,
                             final Perfektbildung perfektbildung,
                             final Collection<? extends IInfinitesPraedikat> children) {
        this.partizipII = partizipII;
        this.perfektbildung = perfektbildung;

        checkArgument(!children.isEmpty(), "Keine Children");

        this.children = ImmutableList.copyOf(children);
    }

    @Override
    public KomplexePartizipIIPhrase mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    @Override
    public KomplexePartizipIIPhrase mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        return mapFirst(child -> child.mitKonnektor(konnektor));
    }

    @Override
    public KomplexePartizipIIPhrase ohneKonnektor() {
        return mapFirst(IInfinitesPraedikat::ohneKonnektor);
    }

    private KomplexePartizipIIPhrase mapFirst(
            final UnaryOperator<IInfinitesPraedikat> operator) {
        return new KomplexePartizipIIPhrase(
                partizipII, perfektbildung,
                ImmutableList.<IInfinitesPraedikat>builder()
                        .add(operator.apply(children.get(0)))
                        .addAll(children.subList(1, children.size()))
                        .build());
    }

    @Override
    public Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean nachfeldEingereiht) {
        Konstituentenfolge res = null;

        for (int i = 0; i < children.size(); i++) {
            final IInfinitesPraedikat child = children.get(i);

            res = joinToKonstituentenfolge(
                    res,
                    child.toKonstituentenfolgeOhneNachfeld(finiteVerbformFuerOberfeld,
                            nachfeldEingereiht || i < children.size() - 1),
                    // "nachzudenken"
                    partizipII
                    // "gehabt"
            );
        }

        return res;
    }

    @Override
    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor() {
        return getMapFirst(IInfinitesPraedikat::getKonnektor);
    }

    @Nonnull
    @Override
    @NonNull
    public Nachfeld getNachfeld() {
        return children.get(children.size() - 1).getNachfeld();
    }

    @Override
    @Nullable
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return getMapFirst(IInfinitesPraedikat::getSpeziellesVorfeldSehrErwuenscht);
    }

    @Override
    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return getMapFirst(IInfinitesPraedikat::getSpeziellesVorfeldAlsWeitereOption);
    }

    @Override
    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return getMapFirst(IInfinitesPraedikat::getRelativpronomen);
    }

    @Override
    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return getMapFirst(IInfinitesPraedikat::getErstesInterrogativwort);
    }

    @Nullable
    private <R> R getMapFirst(final Function<IInfinitesPraedikat, R> function) {
        return function.apply(children.get(0));
    }


    @NonNull
    @Override
    public Perfektbildung getPerfektbildung() {
        return perfektbildung;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KomplexePartizipIIPhrase that = (KomplexePartizipIIPhrase) o;
        return partizipII.equals(that.partizipII) && perfektbildung == that.perfektbildung
                && children
                .equals(that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partizipII, perfektbildung, children);
    }

    @NonNull
    @Override
    public String toString() {
        return requireNonNull(toKonstituentenfolge()).joinToSingleKonstituente()
                .toTextOhneKontext();
    }
}
