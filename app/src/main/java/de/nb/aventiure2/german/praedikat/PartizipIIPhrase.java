package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine unflektierte Phrase mit Partizip II, einschließlich der Information,
 * welches Hilfsverb verlangt ist: "unten angekommen (sein)",
 * "die Kugel genommen (haben)".
 * <p>
 * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
 * eine Person und einen Numerus - Beispiel:
 * "[Ich habe] die Kugel an mich genommen"
 * (nicht *"[Ich habe] die Kugel an sich genommen")
 */
public interface PartizipIIPhrase extends PartizipIIOderErsatzInfinitivPhrase {
    @Override
    PartizipIIPhrase mitKonnektorUndFallsKeinKonnektor();

    @Override
    PartizipIIPhrase mitKonnektor(
            @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor);

    @Override
    PartizipIIPhrase ohneKonnektor();

    @NonNull
    default Konstituentenfolge getPartizipIIPhraseOhneNachfeld() {
        return toKonstituentenfolgeOhneNachfeld(null, false);
    }

    @Override
    @Nullable
    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor();

    @Nonnull
    @NonNull
    @Override
    Nachfeld getNachfeld();

    /**
     * Gibt die Perfektbildung zurück, die diese Partizip-II-Phrase verlangt.
     * Von der Perfektbildung hängt ab, ob Partizip-II-Phrasen gereiht werden können:
     * <i>um die Ecke gekommen und seinen Freund gesehen</i>
     * ist keine zulässige Partizip-II-Phrasen-Reihung, da weder
     * <i>*er ist um die Ecke gekommen und seinen Freund gesehen</i> noch
     * <i>*er hat um die Ecke gekommen und seinen Freund gesehen</i> zulässig ist.
     */
    @Override
    @NonNull
    Perfektbildung getPerfektbildung();

    @Override
    @Nullable
    Vorfeld getSpeziellesVorfeldSehrErwuenscht();

    @Override
    @Nullable
    Vorfeld getSpeziellesVorfeldAlsWeitereOption();

    @Override
    @Nullable
    Konstituentenfolge getRelativpronomen();

    @Override
    @Nullable
    Konstituentenfolge getErstesInterrogativwort();

    @Override
    @Nullable
    default Integer getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt() {
        // Enthält auch ein Partizip, also nicht nur reine Infinitive.
        return null;
    }

    @Override
    default boolean finiteVerbformBeiVerbletztstellungImOberfeld() {
        // In den ganz seltenen Fällen, wo etwas im Partizip-II-Umfeld ins Oberfeld gestellt wird
        // ("(Das hatte er vorher nicht) haben wissen wollen.")
        // wird nicht kein Partizip-II verwendet ("gehabt", sondern ein Ersatzinfinitiv ("haben").
        // Also hier immer:

        return false;
    }
}
