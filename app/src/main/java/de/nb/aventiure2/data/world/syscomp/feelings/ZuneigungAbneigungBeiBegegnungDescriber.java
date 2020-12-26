package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.adjektiv.AdjPhrMitIndirektemFragesatzOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjPhrMitZuInfinitivOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivMitIndirektemFragesatz;
import de.nb.aventiure2.german.adjektiv.AdjektivMitZuInfinitiv;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.VerbSubjDatAkk;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

import static de.nb.aventiure2.german.base.Nominalphrase.FREUDE_OHNE_ART;
import static de.nb.aventiure2.german.base.Nominalphrase.WUT_OHNE_ART;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VOR;
import static de.nb.aventiure2.german.base.Wortfolge.w;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

/**
 * Beschreibt die Zuneigung oder Abneigung eines Feeling Beings
 * gegenüber dem Target, wenn die beiden sich begegnen.
 */
class ZuneigungAbneigungBeiBegegnungDescriber implements FeelingBeiBegegnungDescriber {
    @NonNull
    @Override
    public ImmutableList<Wortfolge> altFeelingBeiBegegnungPraedikativum(
            final Person gameObjectSubjektPerson, final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final VerbSubjObj sehenVerb = targetKnown ? VerbSubjObj.WIEDERSEHEN : SEHEN;

        if (feelingIntensity <= -FeelingIntensity.SEHR_STARK) {
            final String praepositionalphrase = "ganz außer "
                    + Reflexivpronomen.get(
                    gameObjectSubjektPerson, gameObjectSubjektNumerusGenus.getNumerus()).dat()
                    + " "
                    + VOR.mit(WUT_OHNE_ART).getDescription();

            return ImmutableList.of(
                    adjektivphraseMitAlsSiehtNebensatz(praepositionalphrase,
                            gameObjectSubjektPerson,
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
                    w("verwundert"), //AdjektivOhneErgaenzungen.VERWUNDERT
                    w("überrascht"),
                    w("etwas überrumpelt")
            );
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
            return ImmutableList.of(
                    w("überrascht und etwas verwirrt")
            );
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of();
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc))
                            .mitGraduativerAngabe("etwas")
                            .getPraedikativ(
                                    gameObjectSubjektPerson,
                                    gameObjectSubjektNumerusGenus.getNumerus()));
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            return ImmutableList.of();
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            return ImmutableList.of(
                    AdjektivMitZuInfinitiv.UEBERGLUECKLICH
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc))
                            .getPraedikativ(
                                    gameObjectSubjektPerson,
                                    gameObjectSubjektNumerusGenus.getNumerus()));
        }

        // TODO sein + Präpositionalphrase + adverbiale Angabe
        final String adjektivphrase = "außer "
                + Reflexivpronomen.get(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus.getNumerus()).dat()
                + " "
                + VOR.mit(FREUDE_OHNE_ART).getDescription();

        return ImmutableList.of(
                adjektivphraseMitAlsSiehtNebensatz(adjektivphrase, gameObjectSubjektPerson,
                        gameObjectSubjektNumerusGenus,
                        targetDesc)
        );
    }

    @NonNull
    @Override
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            final Person gameObjectSubjektPerson, final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final VerbSubjObj sehenVerb = targetKnown ? VerbSubjObj.WIEDERSEHEN : SEHEN;

        if (feelingIntensity <= -FeelingIntensity.STARK) {
            return ImmutableList.of();
        } else if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return ImmutableList.of(AdjektivOhneErgaenzungen.VERAERGERT);
        } else if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return ImmutableList.of(AdjektivOhneErgaenzungen.VERSTIMMT);
        } else if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.VERWUNDERT,
                    AdjektivOhneErgaenzungen.UEBERRASCHT,
                    AdjektivOhneErgaenzungen.UEBERRUMPELT
                            .mitGraduativerAngabe("etwas")
            );
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
            return ImmutableList.of(
                    new ZweiAdjPhrOhneLeerstellen(
                            AdjektivOhneErgaenzungen.UEBERRASCHT,
                            AdjektivOhneErgaenzungen.VERWIRRT
                                    .mitGraduativerAngabe("etwas")
                    )
            );
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    // "überrascht, dich [target] zu sehen"
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(
                                    sehenVerb
                                            .mit(targetDesc)
                            )
            );
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    // "überrascht, dich [target] zu sehen"
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(
                                    sehenVerb
                                            .mit(targetDesc)
                            )
                            .mitGraduativerAngabe("etwas")
            );
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            // "glücklich, dich [target] zu sehen"
            final AdjPhrMitZuInfinitivOhneLeerstellen gluecklichDichZuSehen =
                    AdjektivMitZuInfinitiv.GLUECKLICH
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc));

            // "gespannt, was du ihr zu berichten hast"
            final AdjPhrMitIndirektemFragesatzOhneLeerstellen gespanntWasZuBerichten =
                    AdjektivMitIndirektemFragesatz.GESPANNT // "gespannt"
                            .mitIndirektemFragesatz(
                                    VerbSubjDatAkk.BERICHTEN // "berichten"
                                            .mitDat(
                                                    Personalpronomen.get(
                                                            gameObjectSubjektPerson,
                                                            gameObjectSubjektNumerusGenus)
                                                    // "ihr"
                                            )
                                            .mitAkk(Interrogativpronomen.WAS) // "was"
                                            .zuHabenPraedikat()
                                            // "was ihr zu berichten haben"
                                            .alsSatzMitSubjekt(targetDesc.persPron())
                                    // "was du ihr zu berichten hast"
                            );

            // "glücklich, dich zu sehen, und gespannt, was du zu berichten hast"
            return ImmutableList.of(
                    gluecklichDichZuSehen,
                    gespanntWasZuBerichten,
                    new ZweiAdjPhrOhneLeerstellen(
                            gluecklichDichZuSehen, gespanntWasZuBerichten
                    )
            );
        }

        return ImmutableList.of();
    }

    private static Wortfolge adjektivphraseMitAlsSiehtNebensatz(
            final String adjektivphrase, final Person subjektPerson,
            final NumerusGenus subjektNumerusGenus,
            final SubstantivischePhrase objekt) {
        // "du sie siehst"
        return w(adjektivphrase
                        + ", als "
                        + SEHEN
                        .mit(objekt)
                        .alsSatzMitSubjekt(Personalpronomen.get(subjektPerson, subjektNumerusGenus))
                        .getVerbletztsatz().getString(),
                true);
    }
}
