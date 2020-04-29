package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Reflexivpronomen;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

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
    public String getDescriptionDuHauptsatz(
            final Collection<Modalpartikel> modalpartikeln) {
        checkKeinPartikelVerb();

        return joinToNull(
                "Du",
                reflPraepositionalkasusVerbAkkObj.getVerb().getDuForm(), // "nimmst"
                akkObj.akk(), // "die goldene Kugel"
                joinToNull(modalpartikeln), // "besser doch"
                Reflexivpronomen.get(P2, SG).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus())// "an dich"
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public String getDescriptionDuHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        checkKeinPartikelVerb();

        return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                " " + reflPraepositionalkasusVerbAkkObj.getVerb().getDuForm() // "nimmst"
                + " du "
                + akkObj.akk() // "die goldene Kugel"
                + " " +
                Reflexivpronomen.get(P2, SG).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus());// "an dich"
    }

    @Override
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus) {
        checkKeinPartikelVerb();

        return akkObj.akk() // "die goldene Kugel"
                + " "
                + Reflexivpronomen.get(person, numerus).im(reflPraepositionalkasusVerbAkkObj.
                getPrapositionMitKasus()) // "an mich"
                + " "
                + reflPraepositionalkasusVerbAkkObj.getVerb().getInfinitiv();// "nehmen"
    }

    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus) {
        return getDescriptionZuInfinitiv(person, numerus, null);
    }

    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            @Nullable final AdverbialeAngabe adverbialeAngabe) {
        checkKeinPartikelVerb();

        return joinToNull(akkObj.akk(), // "die goldene Kugel"
                adverbialeAngabe, // "erneut"
                Reflexivpronomen.get(person, numerus).im(reflPraepositionalkasusVerbAkkObj.
                        getPrapositionMitKasus()), // "an mich"
                reflPraepositionalkasusVerbAkkObj.getVerb().getZuInfinitiv());// "zu nehmen"
    }

    private void checkKeinPartikelVerb() {
        if (reflPraepositionalkasusVerbAkkObj.getVerb().getPartikel() != null) {
            throw new IllegalStateException("Reflexives Partikel Verb mit Präpositionalkasus? "
                    + "Unerwartet!");
        }
    }
}
