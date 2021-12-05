package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine Infinitivkonstruktion ("den Frosch ignorieren", "das Leben genießen") ohne "zu".
 */
public class Infinitiv implements IAlternativeKonstituentenfolgable {
    private final Konstituentenfolge infinitivOhneNachfeld;

    private final Nachfeld nachfeld;

    public Infinitiv(final Verb verb) {
        this(verb, TopolFelder.EMPTY);
    }

    /**
     * Schachtelt einen bestehenden Infinitiv in ein äußeres Verb (üblicherweise Modalverb)
     * ein (Schachtelt z.B. "Spannendes berichten: Odysseus ist zurück." ein in das
     * Modalverb "wollen": "Spannendes berichten wollen: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Infinitive wie "Spannendes berichten wollen",
     * "dich waschen wollen" oder "sagen wollen: „Hallo!“".
     */
    public Infinitiv(final Infinitiv lexikalischerKern,
                     final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes berichten"
                aeusseresVerb.getInfinitiv()), // wollen
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    /**
     * Schachtelt einen bestehenden zu-Infinitiv in ein äußeres Verb (üblicherweise Hilfsverb)
     * ein (Schachtelt z.B. "Spannendes zu berichten: Odysseus ist zurück." ein in das
     * Hilfsverb "haben": "Spannendes zu berichten haben: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Kostruktionen wie "Spannendes zu berichten haben",
     * "dich zu waschen haben" oder "zu sagen haben: „Hallo!“".
     */
    Infinitiv(final ZuInfinitiv lexikalischerKern,
              final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes zu berichten"
                aeusseresVerb.getInfinitiv()), // haben
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    public Infinitiv(final Verb verb, final TopolFelder topolFelder) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                topolFelder.getMittelfeld(), // "den Frosch"
                verb.getInfinitiv()), // "ignorieren"
                topolFelder.getNachfeld()); //"wegen seiner Hässlichkeit"
    }

    public Infinitiv(final Konstituentenfolge infinitivOhneNachfeld,
                     final Nachfeld nachfeld) {
        this.infinitivOhneNachfeld = infinitivOhneNachfeld;
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
                infinitivOhneNachfeld,
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
        final Infinitiv infinitiv = (Infinitiv) o;
        return infinitivOhneNachfeld.equals(infinitiv.infinitivOhneNachfeld) && nachfeld
                .equals(infinitiv.nachfeld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infinitivOhneNachfeld, nachfeld);
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
