package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

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
    default PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabe) {
        return toPraedikat().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    default PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabe) {
        return toPraedikat().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    default PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabe) {
        return toPraedikat().mitAdverbialerAngabe(adverbialeAngabe);
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
    default Konstituentenfolge getErstesInterrogativwort() {
        return null;
    }
}
