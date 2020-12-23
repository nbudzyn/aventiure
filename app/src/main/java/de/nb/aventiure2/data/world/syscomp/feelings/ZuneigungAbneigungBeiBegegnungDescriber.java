package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

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
 * Beschreibt das Zuneigung oder Abneigung eines Feeling Beings
 * gegenüber dem Target, wenn die beiden sich begegnen.
 */
class ZuneigungAbneigungBeiBegegnungDescriber implements FeelingBeiBegegnungDescriber {
    @NonNull
    @Override
    public ImmutableList<AllgDescription> altFeelingBeiBegegnungPraedikativ(
            final Person gameObjectSubjektPerson, final NumerusGenus gameObjectSubjektNumerusGenus,
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

            return ImmutableList.of(
                    adjektivphraseMitAlsSiehtNebensatz(adjektivphrase, gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus, targetDesc)
            );
        } else if (feelingIntensity == -FeelingIntensity.STARK) {
            return ImmutableList.of(
                    adjektivphraseMitAlsSiehtNebensatz("ganz zornig",
                            gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc)
            );
        } else if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return ImmutableList.of();
        } else if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return ImmutableList.of();
        } else if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    satzanschluss("verwundert"),
                    satzanschluss("überrascht"),
                    satzanschluss("etwas überrumpelt")
            );
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
            return ImmutableList.of(
                    satzanschluss("überrascht und etwas verwirrt")
            );
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of();
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    adjektivphraseMitZuSehen("etwas überrascht", gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus, sehenVerb, targetDesc)
            );
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            return ImmutableList.of();
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            return ImmutableList.of(
                    adjektivphraseMitZuSehen("überglücklich", gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus,
                            sehenVerb, targetDesc)
            );
        }

        final String adjektivphrase = "außer "
                + Reflexivpronomen.get(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus.getNumerus()).dat()
                + " vor Freude";

        return ImmutableList.of(
                adjektivphraseMitAlsSiehtNebensatz(adjektivphrase, gameObjectSubjektPerson,
                        gameObjectSubjektNumerusGenus,
                        targetDesc)
        );
    }

    @NonNull
    @Override
    public ImmutableList<AllgDescription> altEindruckBeiBegegnungPraedAdjPhrase(
            final Person gameObjectSubjektPerson, final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // FIXME Adjektivphrasen zurückgeben, die ggf.auch diskontinuierlich ausgegeben werden!

        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final VerbSubjObj sehenVerb = targetKnown ? VerbSubjObj.WIEDERSEHEN : SEHEN;

        if (feelingIntensity <= -FeelingIntensity.STARK) {
            return ImmutableList.of();
        } else if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return ImmutableList.of(
                    satzanschluss("verärgert")
            );
        } else if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    satzanschluss("verstimmt")
            );
        } else if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    satzanschluss("verwundert"),
                    satzanschluss("überrascht"),
                    // FIXME Graduative Angabe
                    satzanschluss("etwas überrumpelt")
            );
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
            return ImmutableList.of(
                    satzanschluss("überrascht und etwas verwirrt")
            );
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    adjektivphraseMitZuSehen("überrascht", gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus, sehenVerb, targetDesc)
            );
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    adjektivphraseMitZuSehen("etwas überrascht", gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus, sehenVerb, targetDesc)
            );
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            // "glücklich, dich zu sehen"
            final AllgDescription gluecklichZuSehen =
                    adjektivphraseMitZuSehen("glücklich", gameObjectSubjektPerson,
                            gameObjectSubjektNumerusGenus,
                            sehenVerb, targetDesc);

            // "gespannt, was du ihr zu berichten hast"
            // FIXME Adjektiv mit indirektem Akkusativ-Fragesatz - fürs erste muss man
            //  das SUBJEKT des Fragesatzes ("du") und das "Genus" des
            //  Erfragten ("was" sv. "wen") in der Akkusativ-Phrase
            //  speichern.
            //  Der indirekte Fragesatz ist quasi ein lexikalischer Kern, in dem
            //  das Akkusativ-Objekt durch ein Fragewort repräsentiert ist.
            final AllgDescription gespanntWasZuBerichten =
                    adjektivphraseMitWasZuBerichtenHastNebensatz("gespannt",
                            gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc);

            // "glücklich, dich zu sehen, und gespannt, was du zu berichten hast"
            return ImmutableList.of(
                    gluecklichZuSehen,
                    gespanntWasZuBerichten,
                    // "glücklich, dich zu sehen, und gespannt, was du zu berichten hast"
                    // FIXME ZweiAdjPhr...
                    satzanschluss(gluecklichZuSehen.getDescriptionHauptsatz()
                            + (gluecklichZuSehen.isKommaStehtAus() ? ", und " : " und ")
                            + gespanntWasZuBerichten.getDescriptionHauptsatz())
                            .komma(gespanntWasZuBerichten.isKommaStehtAus())
            );
        }

        return ImmutableList.of();
    }

    private static AllgDescription adjektivphraseMitZuSehen(final String adjektivphrase,
                                                            final Person subjektPerson,
                                                            final NumerusGenus subjektNumerusGenus,
                                                            final VerbSubjObj sehenVerb,
                                                            final SubstantivischePhrase objekt) {
        return satzanschluss(adjektivphrase
                + ", "
                + sehenVerb
                .mit(objekt)
                .getZuInfinitiv(
                        subjektPerson, subjektNumerusGenus.getNumerus()))
                .komma();
    }

    private static AllgDescription adjektivphraseMitAlsSiehtNebensatz(
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

    private static AllgDescription adjektivphraseMitWasZuBerichtenHastNebensatz(
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
