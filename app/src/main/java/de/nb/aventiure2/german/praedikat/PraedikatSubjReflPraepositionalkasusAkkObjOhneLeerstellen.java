package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.VerbValenz;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat wie "die Kugel an sich nehmen", das mit einer
 * "reflexiven Präpositionalkonstruktion" und einem Akkusativobjet steht -
 * alle Leerstellen besetzt
 */
class PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen
        extends AbstractPraedikatOhneLeerstellen {
    private final VerbReflPraepositionalkasusAkkObj verbReflPraepositionalkasusAkkObj;

    @Argument
    private final SubstantivischePhrase akkObj;

    @VerbValenz
    PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final VerbReflPraepositionalkasusAkkObj verbReflPraepositionalkasusAkkObj,
            final SubstantivischePhrase akkObj) {
        this(verbReflPraepositionalkasusAkkObj, akkObj,
                null, null,
                null);
    }

    private PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final VerbReflPraepositionalkasusAkkObj verbReflPraepositionalkasusAkkObj,
            final SubstantivischePhrase akkObj,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verbReflPraepositionalkasusAkkObj.getVerb(),
                adverbialeAngabeSkopusSatz, adverbialeAngabeSkopusVerbAllg,
                adverbialeAngabeSkopusVerbWohinWoher);
        this.verbReflPraepositionalkasusAkkObj = verbReflPraepositionalkasusAkkObj;
        this.akkObj = akkObj;
    }

    @Override
    public PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                verbReflPraepositionalkasusAkkObj, akkObj,
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

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        @Nullable final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        final String akk = akkObj.akk();
        if (!"es" .equals(akk)) {
            // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
            // (Eisenberg Der Satz 5.4.2)
            return akk;  // "den Frosch"
        }

        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                final Person personSubjekt,
                                final Numerus numerusSubjekt) {
        checkKeinPartikelVerb();

        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                akkObj.akk(), // "die goldene Kugel"
                joinToNull(modalpartikeln), // "besser doch"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                Reflexivpronomen.get(personSubjekt, numerusSubjekt)
                        .im(verbReflPraepositionalkasusAkkObj
                                .getPrapositionMitKasus()), // "an dich",
                getAdverbialeAngabeSkopusVerbWohinWoher() // "in deine Jackentasche"
        );
    }

    @Override
    public String getNachfeld(final Person personSubjekt,
                              final Numerus numerusSubjekt) {
        return null;
    }

    private void checkKeinPartikelVerb() {
        if (verbReflPraepositionalkasusAkkObj.getVerb().getPartikel() != null) {
            throw new IllegalStateException("Reflexives Partikel Verb mit Präpositionalkasus? "
                    + "Unerwartet!");
        }
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return true;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return true;
    }
}
