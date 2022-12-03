package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine komplexe Infinitivkonstruktion mit "zu":
 * <ul>
 * <li>"den Frosch ignoriert zu haben"
 * <li>"den Weg gelaufen zu sein"
 * <li>"zu arbeiten zu haben" (in Schache)
 * <li>"laufen und sich abreagieren zu wollen" (Reihung von reinen Infinitiven)
 * </ul>
 */
public class KomplexerZuInfinitiv extends AbstractKomplexerInfinitiv
        implements ZuInfinitiv {

    public KomplexerZuInfinitiv(final String infinitiv,
                                final IInfinitesPraedikat child) {
        this(infinitiv, ImmutableList.of(child));
    }

    KomplexerZuInfinitiv(final String infinitiv,
                         final Collection<? extends IInfinitesPraedikat> children) {
        super(infinitiv, children);
    }

    @Override
    public KomplexerZuInfinitiv mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    @Override
    public KomplexerZuInfinitiv mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        return (KomplexerZuInfinitiv) super.mitKonnektor(konnektor);
    }

    @Override
    public KomplexerZuInfinitiv ohneKonnektor() {
        return (KomplexerZuInfinitiv) super.ohneKonnektor();
    }

    @Override
    public boolean finiteVerbformBeiVerbletztstellungImOberfeld() {
        return false;
    }

    @Override
    public Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean nachfeldEingereiht) {
        Konstituentenfolge res = null;

        for (int i = 0; i < getChildren().size(); i++) {
            final IInfinitesPraedikat child = getChildren().get(i);

            res = joinToKonstituentenfolge(
                    res,
                    child.toKonstituentenfolgeOhneNachfeld(finiteVerbformFuerOberfeld,
                            nachfeldEingereiht || i < getChildren().size() - 1), // "laufen"
                    "zu",
                    getInfinitivOhneZu() // "wollen"
            );
        }

        return res;
    }

    @Override
    protected AbstractKomplexerInfinitiv mapFirst(
            final UnaryOperator<IInfinitesPraedikat> operator) {
        return new KomplexerZuInfinitiv(
                getInfinitivOhneZu(),
                ImmutableList.<IInfinitesPraedikat>builder()
                        .add(operator.apply(getChildren().get(0)))
                        .addAll(getChildren().subList(1, getChildren().size()))
                        .build());
    }
}
