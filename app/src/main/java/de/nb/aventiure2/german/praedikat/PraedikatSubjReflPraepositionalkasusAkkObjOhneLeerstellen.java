package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Reflexivpronomen;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * Ein Prädikat wie "die Kugel an sich nehmen", das mit einer
 * "reflexiven Präpositionalkonstruktion" und einem Akkusativobjet steht -
 * alle Leerstellen besetzt
 */
class PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    private final ReflPraepositionalkasusVerbAkkObj reflPraepositionalkasusVerbAkkObj;

    private final DeklinierbarePhrase akkObj;

    PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final ReflPraepositionalkasusVerbAkkObj reflPraepositionalkasusVerbAkkObj,
            final DeklinierbarePhrase akkObj) {

        this.reflPraepositionalkasusVerbAkkObj = reflPraepositionalkasusVerbAkkObj;
        this.akkObj = akkObj;
    }

    @Override
    public String getDescriptionDuHauptsatz() {
        return "Du "
                + reflPraepositionalkasusVerbAkkObj.getDuForm() // "nimmst"
                + " " + akkObj.akk() // "die goldene Kugel"
                + " " +
                Reflexivpronomen.P2_SG.im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus());// "an dich"
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public String getDescriptionDuHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                " " + reflPraepositionalkasusVerbAkkObj.getDuForm() // "nimmst"
                + " du "
                + akkObj.akk() // "die goldene Kugel"
                + " " +
                Reflexivpronomen.P2_SG.im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus());// "an dich"
    }

    @Override
    public String getDescriptionInfinitiv() {
        return capitalize(akkObj.akk()) // "die goldene Kugel"
                + " "
                + Reflexivpronomen.P1_SG.im(reflPraepositionalkasusVerbAkkObj.
                getPrapositionMitKasus()) // "an mich"
                + " "
                + reflPraepositionalkasusVerbAkkObj.getInfinitiv();// "nehmen"
    }
}
