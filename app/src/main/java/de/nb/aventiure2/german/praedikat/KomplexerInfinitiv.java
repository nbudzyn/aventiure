package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine komplexe Infinitivkonstruktion ohne "zu":
 * <ul>
 * <li>"den Frosch ignoriert haben"
 * <li>"den Weg gelaufen sein"
 * <li>"den Weg laufen wollen" (mehrfach geschachtelt)
 * <li>"[du hast ]Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
 * <li>"laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven)
 * </ul>
 */
public class KomplexerInfinitiv extends AbstractKomplexerInfinitiv
        implements Infinitiv {
    private final Perfektbildung perfektbildung;

    KomplexerInfinitiv(final String infinitiv,
                       final Perfektbildung perfektbildung,
                       final IInfinitesPraedikat child) {
        this(infinitiv, perfektbildung, ImmutableList.of(child));
    }

    KomplexerInfinitiv(final String infinitiv,
                       final Perfektbildung perfektbildung,
                       final Collection<? extends IInfinitesPraedikat> children) {
        super(infinitiv, children);
        this.perfektbildung = perfektbildung;
    }

    @Override
    public KomplexerInfinitiv mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    @Override
    public KomplexerInfinitiv mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        return (KomplexerInfinitiv) super.mitKonnektor(konnektor);
    }

    @Override
    public KomplexerInfinitiv ohneKonnektor() {
        return (KomplexerInfinitiv) super.ohneKonnektor();
    }

    @NonNull
    @Override
    public Perfektbildung getPerfektbildung() {
        // Betrifft nur die Modalverben, wo der Infinitiv ein Ersatzinfinitiv ist.
        // "Er hat glücklich sein wollen"
        return perfektbildung;
    }

    @Override
    public boolean finiteVerbformBeiVerbletztstellungImOberfeld() {
        // "Zu der Abfolgeregel des Finitums am Ende gibt es folgende Ausnahme: Die finite
        // Form des Hilfsverbs haben steht - bei zwei oder drei Infinitiven - nicht am Ende,
        // sondern am
        // Anfang des gesamten Verbalkomplexes."
        // ( https://grammis.ids-mannheim.de/systematische-grammatik/1241 )

        return getHilfsverbFuerPerfekt().equals(HabenUtil.VERB)
                && Optional.ofNullable(
                getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt())
                .orElse(0) >= 2;
    }

    @Override
    public Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean nachfeldEingereiht) {
        Konstituentenfolge res = null;

        for (int i = 0; i < getChildren().size(); i++) {
            final IInfinitesPraedikat child = getChildren().get(i);

            @Nullable String verbformFuerOberfeld = finiteVerbformFuerOberfeld;
            boolean infinitivNachstellen = true;
            if (finiteVerbformFuerOberfeld == null) {
                if (child.finiteVerbformBeiVerbletztstellungImOberfeld()
                        && child instanceof EinfacherInfinitiv) {
                    // Sehr seltener Sonderfall:
                    // "(Das hatte er vorher nicht) haben wissen wollen.":  (Ersatzinfinitiv
                    // "wollen", außerdem Ersatzinfinitiv "haben". Dann wird "haben" -
                    // obwohl es keine finite Form ist! -  offenbar vorangestellt!)
                    verbformFuerOberfeld = getInfinitivOhneZu();
                    infinitivNachstellen = false;
                }
            }

            res = joinToKonstituentenfolge(
                    res,
                    child.toKonstituentenfolgeOhneNachfeld(verbformFuerOberfeld,
                            nachfeldEingereiht || i < getChildren().size() - 1), // "hat laufen"
                    infinitivNachstellen ? getInfinitivOhneZu()
                            // "wollen"
                            : null
            );
        }

        return res;
    }

    @Nullable
    @Override
    public Integer getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt() {
        Integer max = null;
        for (final IInfinitesPraedikat child : getChildren()) {
            if (child instanceof Infinitiv) {
                final Integer anz =
                        ((Infinitiv) child)
                                .getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt();
                if (anz != null) {
                    if (max == null) {
                        max = anz + 1;
                    } else {
                        max = Math.max(max, anz + 1);
                    }
                }
            }
        }

        return max;
    }

    @Override
    protected AbstractKomplexerInfinitiv mapFirst(
            final UnaryOperator<IInfinitesPraedikat> operator) {
        return new KomplexerInfinitiv(
                getInfinitivOhneZu(),
                getPerfektbildung(),
                ImmutableList.<IInfinitesPraedikat>builder()
                        .add(operator.apply(getChildren().get(0)))
                        .addAll(getChildren().subList(1, getChildren().size()))
                        .build());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final KomplexerInfinitiv that = (KomplexerInfinitiv) o;
        return perfektbildung == that.perfektbildung;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), perfektbildung);
    }
}
