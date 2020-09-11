package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * Ein Prädikat wie "die Kugel an sich nehmen", das mit einer
 * "reflexiven Präpositionalkonstruktion" und einem Akkusativobjet steht -
 * alle Leerstellen besetzt
 */
class PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen
        extends AbstractPraedikatOhneLeerstellen {
    private final ReflPraepositionalkasusVerbAkkObj reflPraepositionalkasusVerbAkkObj;

    private final SubstantivischePhrase akkObj;

    PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final ReflPraepositionalkasusVerbAkkObj reflPraepositionalkasusVerbAkkObj,
            final SubstantivischePhrase akkObj) {
        this(reflPraepositionalkasusVerbAkkObj, akkObj,
                null, null,
                null);
    }

    private PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final ReflPraepositionalkasusVerbAkkObj reflPraepositionalkasusVerbAkkObj,
            final SubstantivischePhrase akkObj,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(reflPraepositionalkasusVerbAkkObj.getVerb(),
                adverbialeAngabeSkopusSatz, adverbialeAngabeSkopusVerbAllg,
                adverbialeAngabeSkopusVerbWohinWoher);
        this.reflPraepositionalkasusVerbAkkObj = reflPraepositionalkasusVerbAkkObj;
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
                reflPraepositionalkasusVerbAkkObj, akkObj,
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
                reflPraepositionalkasusVerbAkkObj, akkObj,
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
                reflPraepositionalkasusVerbAkkObj, akkObj,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }


    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        return akkObj.akk();
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        checkKeinPartikelVerb();

        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                akkObj.akk(), // "die goldene Kugel"
                joinToNull(modalpartikeln), // "besser doch"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                Reflexivpronomen.get(P2, SG).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus()), // "an dich",
                getAdverbialeAngabeSkopusVerbWohinWoher() // "in deine Jackentasche"
        );
    }

    @Override
    public String getNachfeld() {
        return null;
    }

    private void checkKeinPartikelVerb() {
        if (reflPraepositionalkasusVerbAkkObj.getVerb().getPartikel() != null) {
            throw new IllegalStateException("Reflexives Partikel Verb mit Präpositionalkasus? "
                    + "Unerwartet!");
        }
    }
}
