package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituentenfolge;

public interface VerbOhneLeerstellen
        extends VerbMitValenz, PraedikatOhneLeerstellen {
    @Override
    default PraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return toPraedikat().mitModalpartikeln(modalpartikeln);
    }

    @Override
    default PraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return toPraedikat().mitAdvAngabe(advAngabe);
    }

    @Override
    default PraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return toPraedikat().mitAdvAngabe(advAngabe);
    }

    @Override
    default PraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return toPraedikat().mitAdvAngabe(advAngabe);
    }

    PraedikatOhneLeerstellen toPraedikat();

    @Override
    default boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        return true;
    }

    @Override
    default boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    default boolean umfasstSatzglieder() {
        return false;
    }

    @Override
    default boolean bildetPerfektMitSein() {
        return getVerb().getPerfektbildung() == Perfektbildung.SEIN;
    }

    @Override
    default boolean hatAkkusativobjekt() {
        return false;
    }

    @Override
    @Nullable
    @CheckReturnValue
    default Konstituentenfolge getErstesInterrogativwort() {
        return null;
    }

    @Nullable
    @Override
    @CheckReturnValue
    default Konstituentenfolge getRelativpronomen() {
        return null;
    }
}
