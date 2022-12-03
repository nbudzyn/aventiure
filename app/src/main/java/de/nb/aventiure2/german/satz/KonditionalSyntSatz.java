package de.nb.aventiure2.german.satz;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Ein ("syntaktischer") Nebensatz, der mit einer Kondition beginnt und in dem alle
 * Diskursreferenten
 * (Personen, Objekte etc.) auf jeweils eine konkrete sprachliche Repräsentation (z.B. ein
 * konkretes Nomen oder Personalpronomen) festgelegt sind. Beispiel:
 * "als du das hörst"
 */
@Immutable
public class KonditionalSyntSatz implements IKonstituentenfolgable {
    @Nonnull
    private final String kondition;

    @Nonnull
    private final ImmutableList<EinzelnerSyntSatz> saetze;

    public KonditionalSyntSatz(@Nonnull final String kondition,
                               @Nonnull final EinzelnerSyntSatz satz) {
        this(kondition, ImmutableList.of(satz));
    }

    KonditionalSyntSatz(@Nonnull final String kondition,
                        @Nonnull final ImmutableList<EinzelnerSyntSatz> saetze) {
        this.kondition = kondition;
        this.saetze = saetze;
    }

    /**
     * Gibt den eigentlichen Konditionalsatz zurück, allerdings <i>wird kein ausstehendes
     * Komma gefordert!</i> Das wird der Aufrufer in vielen Fällen selbst tun wollen.
     */
    @Override
    @Nonnull
    public Konstituentenfolge toKonstituentenfolge() {
        Konstituentenfolge res = joinToKonstituentenfolge(kondition); // "weil"
        for (int i = 0; i < saetze.size(); i++) {
            res = joinToKonstituentenfolge(
                    res,
                    saetze.get(i).getVerbletztsatz(true)    // "du etwas zu berichten hast"
            );
        }

        return res;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KonditionalSyntSatz that = (KonditionalSyntSatz) o;
        return Objects.equals(kondition, that.kondition) && Objects
                .equals(saetze, that.saetze);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kondition, saetze);
    }

    @NonNull
    @Override
    public String toString() {
        return toKonstituentenfolge().joinToSingleKonstituente().toTextOhneKontext();
    }
}
