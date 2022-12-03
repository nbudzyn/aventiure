package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine Infinitivkonstruktion ("den Frosch ignorieren", "das Leben genießen") ohne "zu".
 */
public interface Infinitiv
        extends IInfinitesPraedikat, PartizipIIOderErsatzInfinitivPhrase {
    @Override
    Infinitiv mitKonnektorUndFallsKeinKonnektor();

    @Override
    Infinitiv mitKonnektor(
            @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor);

    @Override
    Infinitiv ohneKonnektor();

    @Override
    boolean finiteVerbformBeiVerbletztstellungImOberfeld();

    @NonNull
    @Override
    Perfektbildung getPerfektbildung();

    @Nonnull
    @NonNull
    @Override
    Nachfeld getNachfeld();

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

    @Nullable
    @Override
    Integer getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt();
}
