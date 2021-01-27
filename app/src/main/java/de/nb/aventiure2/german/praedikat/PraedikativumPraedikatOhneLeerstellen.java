package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * Ein Prädikat, bestehend aus <i>sein</i> und einem Prädikativum - alle Leerstellen sind besetzt.
 */
public class PraedikativumPraedikatOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Das Prädikativum
     */
    @Komplement
    private final Praedikativum praedikativum;

    public static PraedikativumPraedikatOhneLeerstellen praedikativumPraedikatWerdenMit(
            final Praedikativum praedikativum) {
        return new PraedikativumPraedikatOhneLeerstellen(WerdenUtil.VERB,
                praedikativum);
    }

    public static PraedikativumPraedikatOhneLeerstellen praedikativumPraedikatMit(
            final Praedikativum praedikativum) {
        return new PraedikativumPraedikatOhneLeerstellen(SeinUtil.VERB,
                praedikativum);
    }

    @Valenz
    private PraedikativumPraedikatOhneLeerstellen(final Verb verb,
                                                  final Praedikativum praedikativum) {
        this(verb, praedikativum,
                ImmutableList.of(),
                null, null,
                null);
    }

    private PraedikativumPraedikatOhneLeerstellen(
            final Verb verb,
            final Praedikativum praedikativum,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.praedikativum = praedikativum;
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(), praedikativum,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Diese Position kann wohl nicht besetzt sein.
        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // ?"Endlich wieder einmal glücklich gewesen verlässt du das Theater."
        return false;

        // (Möglich wäre auf jeden Fall die Negation:
        //  "Lange nicht mehr so glücklich gewesen verlässt du das Theater.")
    }

    @Override
    public @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                            final Numerus numerus) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "Ein Esel ist Peter", "Glücklich ist Peter" - das wäre stark markiert, wir lassen
        // es hier aus.

        return null;
    }

    @Override
    @Nullable
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "plötzlich"
                kf(getModalpartikeln()), // "halt"
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt),
                // (kann wohl nicht besetzt sein?)
                praedikativum.getPraedikativOhneAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt)
                // "glücklich", "ein Esel", "sich ihrer selbst gewiss", "sehr glücklich
                // [, dich zu sehen]"
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    @Nullable
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                praedikativum.getPraedikativAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt), // "dich zu sehen"
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
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
    public Konstituentenfolge getErstesInterrogativpronomen() {
        if (praedikativum instanceof Interrogativpronomen) {
            return praedikativum.getPraedikativ(
                    // Person und Numerus spielen beim Interrogativpronomen keine Rolle:
                    // "Sie ist interessiert, wer Peter ist",
                    // "Sie ist interessiert, wer du bist",
                    // "Sie ist interessiert, wer wir sind"
                    P2, SG);
        }

        return null;
    }
}
