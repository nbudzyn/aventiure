package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.description.ITextContext;

public interface VerbOhneLeerstellen
        extends VerbMitValenz, EinzelnesSemPraedikatOhneLeerstellen {
    @Override
    default SemPraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return toPraedikat().mitModalpartikeln(modalpartikeln);
    }

    @Override
    default SemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return toPraedikat().mitAdvAngabe(advAngabe);
    }

    @Override
    default SemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return toPraedikat().mitAdvAngabe(advAngabe);
    }

    @Override
    default SemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return toPraedikat().mitAdvAngabe(advAngabe);
    }

    @Override
    default ImmutableList<PartizipIIOderErsatzInfinitivPhrase> getPartizipIIOderErsatzInfinitivPhrasen(
            final ITextContext textContext, final boolean nachAnschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.copyOf(
                getPartizipIIPhrasen(textContext, nachAnschlusswort, praedRegMerkmale));
    }

    SemPraedikatOhneLeerstellen toPraedikat();

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
    default boolean hatAkkusativobjekt() {
        return false;
    }
}
