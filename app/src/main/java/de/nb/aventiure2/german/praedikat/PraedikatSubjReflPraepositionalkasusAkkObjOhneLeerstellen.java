package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;
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

    private final DescribableAsDeklinierbarePhrase akkObj;

    PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
            final ReflPraepositionalkasusVerbAkkObj reflPraepositionalkasusVerbAkkObj,
            final DescribableAsDeklinierbarePhrase akkObj) {

        this.reflPraepositionalkasusVerbAkkObj = reflPraepositionalkasusVerbAkkObj;
        this.akkObj = akkObj;
    }

    @Override
    public String getDescriptionHauptsatz() {
        return "Du "
                + reflPraepositionalkasusVerbAkkObj.getDuForm() // "nimmst"
                + " " + akkObj.akk(true) // "die goldene Kugel"
                + " " +
                Reflexivpronomen.P2_SG.im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus());// "an dich"
    }

    @Override
    public String getDescriptionHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                " " + reflPraepositionalkasusVerbAkkObj.getDuForm() // "nimmst"
                + " du "
                + akkObj.akk(true) // "die goldene Kugel"
                + " " +
                Reflexivpronomen.P2_SG.im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus());// "an dich"
    }

    @Override
    public String getDescriptionInfinitiv() {
        return capitalize(akkObj.akk(true)) // "die goldene Kugel"
                + " "
                + Reflexivpronomen.P1_SG.im(reflPraepositionalkasusVerbAkkObj.
                getPrapositionMitKasus()) // "an mich"
                + " "
                + reflPraepositionalkasusVerbAkkObj.getInfinitiv();// "nehmen"
    }
}
