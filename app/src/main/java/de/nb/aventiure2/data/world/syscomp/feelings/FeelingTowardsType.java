package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AllgDescription;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HABEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

/**
 * Ein Spektrum von Gefühlen, dass ein {@link IFeelingBeingGO} gegenüber jemandem oder
 * einer Sache haben kann. Z.B. könnte das Gefühlsspektrum ZUNEIGUNG_ABNEIGUNG zwischen den
 * Extremen abgrundtiefen Hasses und brennender Liebe ausgeprägt sein.
 */
public enum FeelingTowardsType {
    ZUNEIGUNG_ABNEIGUNG(FeelingTowardsType::getZuneigungAbneigungBeiBegegnungPraedikativum);
    // STORY Weitere Gefühle könnten zb sein
    //  - VERTRAUEN_MISSTRAUEN
    //  - Dankbarkeit / Rachedurst

    /**
     * Builder für ein Prädikativum, das das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreibt, wenn die beiden sich begegnen.
     * Man kann das erzeugte Prädikativ in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     */
    private final FeelingBeiBegegnungPraedikativumBuilder feelingBeiBegegnungPraedikativumBuilder;

    FeelingTowardsType(
            final FeelingBeiBegegnungPraedikativumBuilder feelingBeiBegegnungPraedikativumBuilder) {
        this.feelingBeiBegegnungPraedikativumBuilder = feelingBeiBegegnungPraedikativumBuilder;
    }

    /**
     * Gibt ein Prädikativum zurück, das das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreibt, wenn die beiden sich begegnen.
     * Man kann dieses Prädikativum in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     */
    public AbstractDescription<?> getFeelingBeiBegegnungPraedikativum(
            final Person gameObjectSubjektPerson,
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        return feelingBeiBegegnungPraedikativumBuilder.getFeelingBeiBegegnungPraedikativ(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc,
                feelingIntensity, targetKnown
        );
    }

    /**
     * Gibt ein Prädikativum zurück, das die Zuneigung / Abneigung dieses
     * Game Objects gegenüber dem Target beschreibt, wenn die beiden sich begegnen.
     * Man kann dieses Prädikativum in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     */
    @NonNull
    // TODO Auch die Zauberin könnte irgendwie wirken, wenn sie
    //  den SC (wieder-) trifft...
    private static AbstractDescription<?> getZuneigungAbneigungBeiBegegnungPraedikativum(
            final Person gameObjectSubjektPerson,
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final VerbSubjObj sehenVerb = targetKnown ? VerbSubjObj.WIEDERSEHEN : SEHEN;

        if (feelingIntensity <= -FeelingIntensity.SEHR_STARK) {
            final String adjektivphrase = "ganz außer "
                    + Reflexivpronomen.get(
                    gameObjectSubjektPerson, gameObjectSubjektNumerusGenus.getNumerus()).dat()
                    + " vor Wut";

            return adjektivphraseMitAlsSiehtNebensatz(adjektivphrase, gameObjectSubjektPerson,
                    gameObjectSubjektNumerusGenus, targetDesc);
        } else if (feelingIntensity == -FeelingIntensity.STARK) {
            return adjektivphraseMitAlsSiehtNebensatz("ganz zornig",
                    gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc);
        } else if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return satzanschluss("offenkundig verärgert");
        } else if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return satzanschluss("sichtlich verstimmt");
        } else if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return satzanschluss("verwundert");
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
            return satzanschluss("überrascht und etwas verwirrt");
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return adjektivphraseMitZuSehen("offenbar überrascht", gameObjectSubjektPerson,
                    gameObjectSubjektNumerusGenus, sehenVerb, targetDesc);
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return adjektivphraseMitZuSehen("etwas überrascht", gameObjectSubjektPerson,
                    gameObjectSubjektNumerusGenus, sehenVerb, targetDesc);
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            // "offenbar glücklich, dich zu sehen"
            final AllgDescription gluecklichZuSehen =
                    adjektivphraseMitZuSehen("offenbar glücklich", gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus,
                            sehenVerb, targetDesc);

            // "gespannt, was du ihr zu berichten hast"
            final AbstractDescription<?> gespanntWasZuBerichten =
                    adjektivphraseMitWasZuBerichtenHastNebensatz("gespannt",
                            gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc);

            // "offenbar glücklich, dich zu sehen, und gespannt, was du zu berichten hast"
            return satzanschluss(gluecklichZuSehen.getDescriptionHauptsatz()
                    + (gluecklichZuSehen.isKommaStehtAus() ? ", und " : " und ")
                    + gespanntWasZuBerichten.getDescriptionHauptsatz())
                    .komma(gespanntWasZuBerichten.isKommaStehtAus());
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            return adjektivphraseMitZuSehen("überglücklich", gameObjectSubjektPerson,
                    gameObjectSubjektNumerusGenus,
                    sehenVerb, targetDesc);
        }

        final String adjektivphrase = "außer "
                + Reflexivpronomen.get(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus.getNumerus()).dat()
                + " vor Freude";

        return adjektivphraseMitAlsSiehtNebensatz(adjektivphrase, gameObjectSubjektPerson,
                gameObjectSubjektNumerusGenus,
                targetDesc);
    }

    private static AllgDescription adjektivphraseMitZuSehen(final String adjektivphrase,
                                                            final Person subjektPerson,
                                                            final NumerusGenus subjektNumerusGenus,
                                                            final VerbSubjObj sehenVerb,
                                                            final SubstantivischePhrase objekt) {
        return satzanschluss(adjektivphrase
                + ", "
                + sehenVerb
                .mitObj(objekt)
                .getZuInfinitiv(
                        subjektPerson, subjektNumerusGenus.getNumerus()))
                .komma();
    }

    private static AbstractDescription<?> adjektivphraseMitAlsSiehtNebensatz(
            final String adjektivphrase, final Person subjektPerson,
            final NumerusGenus subjektNumerusGenus,
            final SubstantivischePhrase objekt) {
        final String sehenVerbform = sehenVerbform(
                subjektPerson, subjektNumerusGenus.getNumerus());
        // "du sie siehst"
        final AbstractDescription<?> verbletztsatzanschluss =
                verbletztsatzanschlussMitSubjektPersonalpronomen(
                        subjektPerson, subjektNumerusGenus,
                        objekt.akk(),
                        sehenVerbform)
                        .undWartest(subjektPerson == P2);
        return satzanschluss(adjektivphrase
                + ", als "
                + verbletztsatzanschluss.getDescriptionHauptsatz())
                .komma()
                .undWartest(verbletztsatzanschluss
                        .isAllowsAdditionalDuSatzreihengliedOhneSubjekt());
    }

    private static AbstractDescription<?> adjektivphraseMitWasZuBerichtenHastNebensatz(
            final String adjektivphrase,
            final Person berichtempfaengerPerson,
            final NumerusGenus berichtempfaengerNumerusGenus,
            final SubstantivischePhrase berichtender) {
        final String habenVerbform = habenVerbform(
                berichtender.getPerson(), berichtender.getNumerus());
        // "du ihr zu berichten hast"
        final AbstractDescription<?> verbletztsatzanschluss =
                verbletztsatzanschlussMitSubjektPersonalpronomen(
                        berichtender.getPerson(), berichtender.getNumerusGenus(),
                        Personalpronomen.get(
                                berichtempfaengerPerson, berichtempfaengerNumerusGenus).dat(),
                        "zu berichten " + habenVerbform);
        return satzanschluss(adjektivphrase
                + ", was "
                + verbletztsatzanschluss.getDescriptionHauptsatz())
                .komma()
                .undWartest(verbletztsatzanschluss
                        .isAllowsAdditionalDuSatzreihengliedOhneSubjekt());
    }


    private static String sehenVerbform(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? "sehe" : "sehen";
            case P2:
                return numerus == SG ? SEHEN.getDuForm() : "seht";
            case P3:
                return numerus == SG ? "sieht" : "sehen";
            default:
                throw new IllegalStateException("Unexpected Person: " + person);
        }
    }

    private static String habenVerbform(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? "habe" : "haben";
            case P2:
                return numerus == SG ? HABEN.getDuForm() : "habt";
            case P3:
                return numerus == SG ? "hat" : "haben";
            default:
                throw new IllegalStateException("Unexpected Person: " + person);
        }
    }

    @NonNull
    private static AbstractDescription<?> verbletztsatzanschlussMitSubjektPersonalpronomen(
            final Person subjektPerson,
            final NumerusGenus subjektNumerusGenus,
            final String objekt,
            final String verbalkomplex) {
        return satzanschluss(
                Personalpronomen.get(subjektPerson, subjektNumerusGenus).nom()
                        + " "
                        + objekt
                        + " "
                        + verbalkomplex).komma();
    }
}
