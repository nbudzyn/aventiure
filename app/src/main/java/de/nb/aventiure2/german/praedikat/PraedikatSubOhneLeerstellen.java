package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Ein Prädikat (Verb ggf. mit Präfix) bei dem das Verb mit einem Subjekt steht und keine
 * Leerstellen hat.
 */

public class PraedikatSubOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {

    @Valenz
    PraedikatSubOhneLeerstellen(final Verb verb) {
        this(verb,
                null, null,
                null);
    }

    private PraedikatSubOhneLeerstellen(
            final Verb verb,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
    }

    @Override
    public PraedikatSubOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatSubOhneLeerstellen(
                getVerb(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatSubOhneLeerstellen(
                getVerb(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatSubOhneLeerstellen(
                getVerb(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public Iterable<Konstituente> getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                                final Person personSubjekt,
                                                final Numerus numerusSubjekt) {
        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                GermanUtil.joinToNullString(modalpartikeln), // "mal eben"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription() // "in den Wald"
        );
    }

    @Override
    public Iterable<Konstituente> getNachfeld(final Person personSubjekt,
                                              final Numerus numerusSubjekt) {
        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung()
        );
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return null;
    }
}
