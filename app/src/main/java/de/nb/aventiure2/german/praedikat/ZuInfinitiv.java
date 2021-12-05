package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine Infinitivkonstruktion mit "zu" ("den Frosch zu ignorieren", "das Leben zugenießen").
 */
public class ZuInfinitiv implements IAlternativeKonstituentenfolgable {
    private final Konstituentenfolge zuInfinitivOhneNachfeld;

    private final Nachfeld nachfeld;

    ZuInfinitiv(final Verb verb) {
        this(verb, TopolFelder.EMPTY);
    }

    /**
     * Schachtelt einen bestehenden Infinitiv in ein äußeres Verb (üblicherweise Modalverb)
     * ein (Schachtelt z.B. "Spannendes berichten: Odysseus ist zurück." ein in das
     * Modalverb "wollen": "Spannendes berichten zu wollen: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Kostruktionen wie "Spannendes berichten zu wollen",
     * "dich waschen zu wollen" oder "sagen zu wollen: „Hallo!“".
     */
    ZuInfinitiv(final Infinitiv lexikalischerKern,
                final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes berichten"
                aeusseresVerb.getZuInfinitiv()), // zu wollen
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    /**
     * Schachtelt einen bestehenden zu-Infinitiv in ein äußeres Verb (üblicherweise Hilfsverb)
     * ein (Schachtelt z.B. "Spannendes zu berichten: Odysseus ist zurück." ein in das
     * Hilfsverb "haben": "Spannendes zu berichten zu haben: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Kostruktionen wie "Spannendes zu berichten zu haben",
     * "dich zu waschen zu haben" oder "zu sagen zu haben: „Hallo!“".
     */
    ZuInfinitiv(final ZuInfinitiv lexikalischerKern,
                final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes zu berichten"
                aeusseresVerb.getZuInfinitiv()), // zu haben
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    ZuInfinitiv(final Verb verb, final TopolFelder topolFelder) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                topolFelder.getMittelfeld(), // "den Frosch"
                verb.getZuInfinitiv()), // "zu ignorieren"
                topolFelder.getNachfeld()); //"wegen seiner Hässlichkeit"
    }

    ZuInfinitiv(final Konstituentenfolge zuInfinitivOhneNachfeld,
                final Nachfeld nachfeld) {
        this.zuInfinitivOhneNachfeld = zuInfinitivOhneNachfeld;
        this.nachfeld = nachfeld;
    }

    @Override
    public Collection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return Collections.singleton(toKonstituentenfolge(true));
    }

    @Nullable
    Konstituentenfolge toKonstituentenfolgeOhneNachfeld() {
        return toKonstituentenfolge(true);
    }

    @Nullable
    private Konstituentenfolge toKonstituentenfolge(final boolean mitNachfeld) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                zuInfinitivOhneNachfeld,
                mitNachfeld ? nachfeld : null);
    }

    public Nachfeld getNachfeld() {
        return nachfeld;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ZuInfinitiv infinitiv = (ZuInfinitiv) o;
        return zuInfinitivOhneNachfeld.equals(infinitiv.zuInfinitivOhneNachfeld) && nachfeld
                .equals(infinitiv.nachfeld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zuInfinitivOhneNachfeld, nachfeld);
    }

    @NonNull
    @Override
    public String toString() {
        @Nullable final Konstituentenfolge konstituentenfolge =
                toAltKonstituentenfolgen().iterator().next();
        if (konstituentenfolge == null) {
            return "";
        }

        return konstituentenfolge.joinToSingleKonstituente().toTextOhneKontext();
    }
}
