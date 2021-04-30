package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine unflektierte Phrase mit Partizip II, einschließlich der Information,
 * welche Hilfsverb verlangt ist: "unten angekommen (sein)",
 * "die Kugel genommen (haben)".
 * <p>
 * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
 * eine Person und einen Numerus - Beispiel:
 * "[Ich habe] die Kugel an mich genommen"
 * (nicht *"[Ich habe] die Kugel an sich genommen")
 * <p>
 * Eine mehrteilige Partizip-II-Phrase kann nur gebildet werden, wenn alle Bestandteile
 * dasselbe Hilfsverb verlangen: <i>um die Ecke gekommen und seinen Freund gesehen</i>
 * ist keine zulässige Partizip-II-Phrase, da weder
 * <i>*er ist um die Ecke gekommen und seinen Freund gesehen</i> noch
 * <i>*er hat um die Ecke gekommen und seinen Freund gesehen</i> zulässig ist.
 */
@Immutable
public class PartizipIIPhrase {
    /**
     * Die eigenliche unflektierte Phrase: "unten angekommen",
     * "die Kugel genommen".
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat diese Phrase
     * eine Person und einen Numerus - Beispiel:
     * "[Ich habe] die Kugel an mich genommen"
     * (nicht *"[Ich habe] die Kugel an sich genommen")
     */
    private final Konstituentenfolge phrase;

    /**
     * Welche Hilfsverb ist verlangt - "(unten angekommen) sein" oder
     * "(die Kugel genommen) haben"? (Muss eindeutig sein).
     */
    private final Perfektbildung perfektbildung;

    public PartizipIIPhrase(final Konstituentenfolge phrase,
                            final Perfektbildung perfektbildung) {
        this.phrase = phrase;
        this.perfektbildung = perfektbildung;
    }

    @NonNull
    static PartizipIIPhrase joinBeiGleicherPerfektbildung(
            final ImmutableList.Builder<PartizipIIPhrase> res,
            @Nullable final PartizipIIPhrase tmp,
            final PartizipIIPhrase partizipIIPhrase) {
        if (tmp == null) {
            // "unten angekommen (sein)"
            return partizipIIPhrase;
        } else if (tmp.getPerfektbildung() == partizipIIPhrase.getPerfektbildung()) {
            // "unten angekommen und der erste gewesen (sein)"
            return new PartizipIIPhrase(
                    Konstituentenfolge.joinToKonstituentenfolge(
                            tmp,
                            "und",
                            partizipIIPhrase),
                    tmp.getPerfektbildung());
        } else {
            // "unten angekommen (sein) und die Kugel genommen (haben)"
            res.add(tmp);
            return partizipIIPhrase;
        }
    }

    Verb getHilfsverb() {
        return perfektbildung.getHilfsverb();
    }

    @NonNull
    public Konstituentenfolge getPhrase() {
        return phrase;
    }

    @NonNull
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
        final PartizipIIPhrase that = (PartizipIIPhrase) o;
        return phrase.equals(that.phrase) &&
                perfektbildung == that.perfektbildung;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phrase, perfektbildung);
    }
}
