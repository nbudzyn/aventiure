package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat wie "die Kugel an sich nehmen", das mit einer
 * "reflexiven Präpositionalkonstruktion" und einem Akkusativobjet steht -
 * alle Leerstellen besetzt
 */
class PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    private final VerbReflPraepositionalkasusAkkObj verbReflPraepositionalkasusAkkObj;

    @Komplement
    private final SubstantivischePhrase akkObj;

    @Valenz
    PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final VerbReflPraepositionalkasusAkkObj verbReflPraepositionalkasusAkkObj,
            final SubstantivischePhrase akkObj) {
        this(verbReflPraepositionalkasusAkkObj, akkObj,
                ImmutableList.of(),
                null, null,
                null);
    }

    private PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final VerbReflPraepositionalkasusAkkObj verbReflPraepositionalkasusAkkObj,
            final SubstantivischePhrase akkObj,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verbReflPraepositionalkasusAkkObj.getVerb(),
                modalpartikeln, adverbialeAngabeSkopusSatz, adverbialeAngabeSkopusVerbAllg,
                adverbialeAngabeSkopusVerbWohinWoher);
        this.verbReflPraepositionalkasusAkkObj = verbReflPraepositionalkasusAkkObj;
        this.akkObj = akkObj;
    }

    @Override
    public PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
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

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                             final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        final Konstituente akk = akkObj.akkK();
        if (!"es".equals(akk.getString())) {
            // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
            // (Eisenberg Der Satz 5.4.2)
            return akk;  // "den Frosch"
        }

        return null;
    }

    @Override
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        checkKeinPartikelVerb();

        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                akkObj.akkK(), // "die goldene Kugel"
                kf(getModalpartikeln()), // "besser doch"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                Reflexivpronomen.get(personSubjekt, numerusSubjekt)
                        .im(verbReflPraepositionalkasusAkkObj
                                .getPrapositionMitKasus()), // "an dich",
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt)
                // "in deine Jackentasche"
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return akkObj;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Override
    @Nullable
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdverbialeAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt)

        );
    }

    private void checkKeinPartikelVerb() {
        if (verbReflPraepositionalkasusAkkObj.getVerb().getPartikel() != null) {
            throw new IllegalStateException("Reflexives Partikel-Verb mit Präpositionalkasus? "
                    + "Unerwartet!");
        }
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return true;
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        if (akkObj instanceof Interrogativpronomen) {
            return akkObj.akkK();
        }

        return null;
    }
}
