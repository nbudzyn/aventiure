package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
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
        super(reflPraepositionalkasusVerbAkkObj.getVerb());
        this.reflPraepositionalkasusVerbAkkObj = reflPraepositionalkasusVerbAkkObj;
        this.akkObj = akkObj;
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        return akkObj.akk();
    }

    @Override
    public String getMittelfeld(
            final Collection<Modalpartikel> modalpartikeln) {
        checkKeinPartikelVerb();

        return joinToNull(
                akkObj.akk(), // "die goldene Kugel"
                joinToNull(modalpartikeln), // "besser doch"
                Reflexivpronomen.get(P2, SG).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus())// "an dich"
        );
    }


    @Override
    public String getInfinitiv(final Person person, final Numerus numerus,
                               @Nullable final AdverbialeAngabe adverbialeAngabe) {
        checkKeinPartikelVerb();

        return joinToNull(akkObj.akk(), // "die goldene Kugel"
                adverbialeAngabe, // "erneut"
                Reflexivpronomen.get(person, numerus).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus()), // "an mich"
                reflPraepositionalkasusVerbAkkObj.getVerb().getInfinitiv());// "nehmen"
    }

    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus,
                                 @Nullable final AdverbialeAngabe adverbialeAngabe) {
        checkKeinPartikelVerb();

        return joinToNull(akkObj.akk(), // "die goldene Kugel"
                adverbialeAngabe, // "erneut"
                Reflexivpronomen.get(person, numerus).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus()), // "an mich"
                reflPraepositionalkasusVerbAkkObj.getVerb().getZuInfinitiv());// "zu nehmen"
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
