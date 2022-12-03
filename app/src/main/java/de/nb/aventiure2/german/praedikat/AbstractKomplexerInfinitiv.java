package de.nb.aventiure2.german.praedikat;

import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Abstrakte Oberklasse für komplexe Infinitivkonstruktion mit oder ohne "zu":
 * <ul>
 * <li>"den Frosch ignoriert haben"
 * <li>"den Weg gelaufen sein"
 * <li>"den Weg laufen wollen" (mehrfach geschachtelt)
 * <li>"[du hast ]Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
 * <li>"laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven)
 * </ul>
 */
public abstract class AbstractKomplexerInfinitiv implements IKonstituentenfolgable {
    /**
     * Die Infinitiv-Verbform ("wollen", "haben", "sein").
     */
    private final String infinitivOhneZu;

    /**
     * Die Infinitive, Partizipien etc., die als nächste Ebene eingeschachtelt sind.
     */
    private final ImmutableList<IInfinitesPraedikat> children;


    AbstractKomplexerInfinitiv(final String infinitivOhneZu,
                               final Collection<? extends IInfinitesPraedikat> children) {
        this.infinitivOhneZu = infinitivOhneZu;
        this.children = ImmutableList.copyOf(children);
    }

    public AbstractKomplexerInfinitiv mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        return mapFirst(child -> child.mitKonnektor(konnektor));
    }

    public AbstractKomplexerInfinitiv ohneKonnektor() {
        return mapFirst(IInfinitesPraedikat::ohneKonnektor);
    }

    protected abstract AbstractKomplexerInfinitiv mapFirst(
            final UnaryOperator<IInfinitesPraedikat> operator);

    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor() {
        return getMapFirst(IInfinitesPraedikat::getKonnektor);
    }

    @Nonnull
    @NonNull
    public Nachfeld getNachfeld() {
        return getChildren().get(getChildren().size() - 1).getNachfeld();
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return getMapFirst(IInfinitesPraedikat::getSpeziellesVorfeldSehrErwuenscht);
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return getMapFirst(IInfinitesPraedikat::getSpeziellesVorfeldAlsWeitereOption);
    }

    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return getMapFirst(IInfinitesPraedikat::getRelativpronomen);
    }

    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return getMapFirst(IInfinitesPraedikat::getErstesInterrogativwort);
    }

    String getInfinitivOhneZu() {
        return infinitivOhneZu;
    }

    @Nullable
    private <R> R getMapFirst(final Function<IInfinitesPraedikat, R> function) {
        return function.apply(children.get(0));
    }

    ImmutableList<IInfinitesPraedikat> getChildren() {
        return children;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractKomplexerInfinitiv that = (AbstractKomplexerInfinitiv) o;
        return infinitivOhneZu.equals(that.infinitivOhneZu) && children.equals(that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infinitivOhneZu, children);
    }

    @NonNull
    @Override
    public String toString() {
        return requireNonNull(toKonstituentenfolge()).joinToSingleKonstituente()
                .toTextOhneKontext();
    }
}
