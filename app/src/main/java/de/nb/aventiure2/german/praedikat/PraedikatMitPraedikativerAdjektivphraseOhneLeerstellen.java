package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhraseOderReflexivpronomen;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat, bestehend aus einem Verb und einer prädikativen Adjektivphrase, in dem
 * alle Leerstellen besetzt sind.
 * <p>
 * Beispiele:
 * <ul>
 *     <li>glücklich wirken
 *     <li>glücklich wirken, Peter zu sehen
 * </ul>
 * <p>
 * Hier geht es nicht um Prädikative, vgl. {@link PraedikativumPraedikatOhneLeerstellen}.
 */
public class PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Die prädikative Adjektivphrase
     */
    @Komplement
    private final AdjPhrOhneLeerstellen adjektivphrase;

    @Valenz
    PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
            final Verb verb,
            final AdjPhrOhneLeerstellen adjektivphrase) {
        this(verb, adjektivphrase,
                ImmutableList.of(), null, null,
                null);
    }

    private PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
            final Verb verb,
            final AdjPhrOhneLeerstellen adjektivphrase,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.adjektivphrase = adjektivphrase;
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(), adjektivphrase,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                adjektivphrase,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                adjektivphrase,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                adjektivphrase,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }


    @Override
    public @Nullable
    Konstituente getSpeziellesVorfeldAlsWeitereOption(final Person person, final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        @Nullable final Konstituentenfolge konstituentenPraedAdjPhr =
                adjektivphrase.getPraedikativOhneAnteilKandidatFuerNachfeld(person, numerus);


        if (konstituentenPraedAdjPhr != null) {
            final Iterator<Konstituente> iterKonstituentenPraedAdjPhr =
                    konstituentenPraedAdjPhr.iterator();
            final Konstituente firstKonstituentePraedAdjPhr = iterKonstituentenPraedAdjPhr.next();
            if (!iterKonstituentenPraedAdjPhr.hasNext()) {
                // "Glücklich wirkt sie [, dich zu sehen].". Markiert - aber möglich.
                return firstKonstituentePraedAdjPhr;
            }

            // else : Die prädikative Adjektivphrase erzeugt mehrere Konstituenten. Dann wollen
            // wir sie nicht in das Vorfeld stellen. Dinge wie "Sehr glücklich wirkt
            // sie." sind zwar möglich, wirken aber schnell unnatürlich.
        }

        return null;
    }

    @Override
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "leider"
                kf(getModalpartikeln()), // "halt"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt),
                // "nach außen" (?)
                adjektivphrase.getPraedikativOhneAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt) // "glücklich"
        );
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    @Nullable
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                adjektivphrase.getPraedikativAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt), // ", dich zu sehen"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdverbialeAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt)
        );
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return null;
    }
}
