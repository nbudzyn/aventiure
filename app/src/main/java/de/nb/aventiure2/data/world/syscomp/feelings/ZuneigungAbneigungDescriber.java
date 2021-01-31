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
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjDatAkk;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.Konditionalsatz;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity.NEUTRAL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BETRUEBT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ENTTAEUSCHT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ERLEICHTERT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.VERSCHUECHTERT;
import static de.nb.aventiure2.german.base.Nominalphrase.FREUDE_OHNE_ART;
import static de.nb.aventiure2.german.base.Nominalphrase.WUT_OHNE_ART;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUSSER_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VOR;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.ReflVerbZuInfSubjektkontrolle.SICH_FREUEN_ZU;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSTRAHLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MUSTERN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObjWoertlicheRede.ENTGEGENBLAFFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObjWoertlicheRede.ENTGEGENRUFEN;

/**
 * Beschreibt die Zuneigung oder Abneigung eines Feeling Beings
 * gegenüber dem Target, wenn die beiden sich begegnen.
 */
class ZuneigungAbneigungDescriber implements FeelingsDescriber {
    @Override
    @NonNull
    public ImmutableList<Satz> altReaktionBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        res.addAll(
                altFeelingBeiBegegnungPraedikativum(
                        gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                        targetDesc, feelingIntensity, targetKnown).stream()
                        .map(PraedikativumPraedikatOhneLeerstellen::praedikativumPraedikatMit)
                        .map(p -> p.alsSatzMitSubjekt(gameObjectSubjekt))
                        .collect(ImmutableList.toImmutableList())
        );

        if (feelingIntensity <= NEUTRAL) {
            //"Sie mustert dich misstrauisch"
            res.addAll(Satz.altSubjObjSaetze(gameObjectSubjekt, MUSTERN, targetDesc,
                    altEindruckBeiBegegnungAdvAngaben(gameObjectSubjekt, targetDesc,
                            feelingIntensity, targetKnown)));
        }

        final ImmutableList<VerbSubjObj> altSehenVerben =
                targetKnown ? ImmutableList.of(VerbSubjObj.WIEDERSEHEN, SEHEN) :
                        ImmutableList.of(SEHEN);

        if (feelingIntensity <= -FeelingIntensity.SEHR_STARK) {
            // "ganz außer sich vor Wut, als sie dich sieht"
            res.addAll(altSehenVerben.stream()
                    .map(v ->
                            praedikativumPraedikatMit(
                                    AUSSER_DAT.mit(gameObjectSubjekt.reflPron())
                                            .mitModAdverbOderAdjektiv("ganz"))
                                    .mitAdverbialerAngabe(new AdverbialeAngabeSkopusVerbAllg(
                                            VOR.mit(WUT_OHNE_ART)))
                                    .alsSatzMitSubjekt(gameObjectSubjekt)
                                    .mitAngabensatz(
                                            new Konditionalsatz("als",
                                                    v.mit(targetDesc)
                                                            .alsSatzMitSubjekt(gameObjectSubjekt))
                                    ))
                    .collect(ImmutableList.toImmutableList()));
        } else if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            if (targetKnown) {
                res.add(ENTGEGENRUFEN
                        .mitObjekt(targetDesc)
                        .mitWoertlicheRede("Du schon wieder!")
                        .alsSatzMitSubjekt(gameObjectSubjekt));
            }
        } else if (feelingIntensity == -FeelingIntensity.STARK) {
            res.addAll(altSehenVerben.stream()
                    .map(v ->
                            praedikativumPraedikatMit(
                                    AdjektivOhneErgaenzungen.ZORNIG
                                            .mitGraduativerAngabe("ganz"))
                                    .alsSatzMitSubjekt(gameObjectSubjekt)
                                    .mitAngabensatz(
                                            new Konditionalsatz("als",
                                                    v.mit(targetDesc)
                                                            .alsSatzMitSubjekt(gameObjectSubjekt))
                                    ))
                    .collect(ImmutableList.toImmutableList()));
            res.add(ENTGEGENBLAFFEN
                    .mitObjekt(targetDesc)
                    .mitWoertlicheRede("Lass mich in Frieden!")
                    .alsSatzMitSubjekt(gameObjectSubjekt));
            res.add(ENTGEGENBLAFFEN
                    .mitObjekt(targetDesc)
                    .mitWoertlicheRede("Hör auf, mich zu belästigen!")
                    .alsSatzMitSubjekt(gameObjectSubjekt));
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            res.addAll(altSehenVerben.stream()
                    .map(v ->
                            // "Sie freut sich, dich zu sehen"
                            SICH_FREUEN_ZU
                                    .mitLexikalischemKern(
                                            v.mit(targetDesc)
                                    )
                                    .alsSatzMitSubjekt(gameObjectSubjekt))
                    .collect(ImmutableList.toImmutableList()));
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            res.add(ANSTRAHLEN.mit(gameObjectSubjekt).alsSatzMitSubjekt(targetDesc));

            res.addAll(altSehenVerben.stream()
                    .map(v ->
                            // "Sie freut sich, dich zu sehen"
                            SICH_FREUEN_ZU
                                    .mitLexikalischemKern(
                                            v.mit(targetDesc)
                                    )
                                    .mitAdverbialerAngabe(
                                            new AdverbialeAngabeSkopusVerbAllg("sehr")
                                    )
                                    .alsSatzMitSubjekt(gameObjectSubjekt))
                    .collect(ImmutableList.toImmutableList()));
        } else if (feelingIntensity >= FeelingIntensity.SEHR_STARK) {
            // "außer sich vor Freude, als sie dich sieht"
            res.addAll(altSehenVerben.stream()
                    .map(v ->
                            praedikativumPraedikatMit(
                                    AUSSER_DAT.mit(gameObjectSubjekt.reflPron()))
                                    .mitAdverbialerAngabe(new AdverbialeAngabeSkopusVerbAllg(
                                            VOR.mit(FREUDE_OHNE_ART)))
                                    .alsSatzMitSubjekt(gameObjectSubjekt)
                                    .mitAngabensatz(
                                            new Konditionalsatz("als",
                                                    v.mit(targetDesc)
                                                            .alsSatzMitSubjekt(gameObjectSubjekt))
                                    ))
                    .collect(ImmutableList.toImmutableList()));
        }

        return res.build();
    }

    @NonNull
    @Override
    public ImmutableList<Praedikativum> altFeelingBeiBegegnungPraedikativum(
            final Person gameObjectSubjektPerson, final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final VerbSubjObj sehenVerb = targetKnown ? VerbSubjObj.WIEDERSEHEN : SEHEN;

        if (feelingIntensity <= -FeelingIntensity.MERKLICH) {
            return ImmutableList.of();
        } else if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.VERWUNDERT,
                    AdjektivOhneErgaenzungen.UEBERRASCHT,
                    AdjektivOhneErgaenzungen.UEBERRUMPELT.mitGraduativerAngabe("etwas")
            );
        } else if (feelingIntensity == NEUTRAL) {
            return ImmutableList.of(
                    new ZweiAdjPhrOhneLeerstellen(
                            AdjektivOhneErgaenzungen.UEBERRASCHT,
                            AdjektivOhneErgaenzungen.VERWIRRT.mitGraduativerAngabe("etwas")
                    ));
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of();
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc))
                            .mitGraduativerAngabe("etwas"));
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            return ImmutableList.of();
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            return ImmutableList.of(
                    AdjektivMitZuInfinitiv.UEBERGLUECKLICH
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc)));
        }

        return ImmutableList.of();
    }

    @NonNull
    @Override
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        final VerbSubjObj sehenVerb = targetKnown ? VerbSubjObj.WIEDERSEHEN : SEHEN;

        if (feelingIntensity <= -FeelingIntensity.STARK) {
            return ImmutableList.of();
        }

        if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return ImmutableList.of(AdjektivOhneErgaenzungen.VERAERGERT);
        }

        if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.VERSTIMMT,
                    AdjektivOhneErgaenzungen.MISSTRAUISCH);
        }

        if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.UEBERRASCHT,
                    AdjektivOhneErgaenzungen.UEBERRUMPELT
                            .mitGraduativerAngabe("etwas"),
                    AdjektivOhneErgaenzungen.VERWUNDERT);
        }

        if (feelingIntensity == NEUTRAL) {
            return ImmutableList.of(
                    new ZweiAdjPhrOhneLeerstellen(
                            AdjektivOhneErgaenzungen.UEBERRASCHT,
                            AdjektivOhneErgaenzungen.VERWIRRT.mitGraduativerAngabe("etwas")
                    )
            );
        }

        if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.UEBERRASCHT.mitGraduativerAngabe("etwas"),
                    // "überrascht, dich [target] zu sehen"
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc)));
        }

        if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.UEBERRASCHT.mitGraduativerAngabe("etwas"),
                    // "etwas überrascht, dich [target] zu sehen"
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc))
                            .mitGraduativerAngabe("etwas"));
        }

        if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            // "glücklich, dich [target] zu sehen"
            final AdjPhrMitZuInfinitivOhneLeerstellen gluecklichDichZuSehen =
                    AdjektivMitZuInfinitiv.GLUECKLICH
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc));

            // "gespannt, was du ihr zu berichten hast"
            final AdjPhrMitIndirektemFragesatzOhneLeerstellen gespanntWasZuBerichten =
                    AdjektivMitIndirektemFragesatz.GESPANNT // "gespannt"
                            .mitIndirektemFragesatz(
                                    VerbSubjDatAkk.BERICHTEN // "berichten"
                                            .mitDat(gameObjectSubjekt.persPron()
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

        if (feelingIntensity == FeelingIntensity.STARK) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.FROEHLICH.mitGraduativerAngabe("ganz")
            );
        }

        return ImmutableList.of();
    }

    @NonNull
    @Override
    public ImmutableList<String> altEindruckBeiBegegnungZusAdverbialeAngaben(
            final int feelingIntensity) {
        if (feelingIntensity <= -FeelingIntensity.SEHR_STARK) {
            return ImmutableList.of("mit bösen und giftigen Blicken");
        }

        if (feelingIntensity == -FeelingIntensity.STARK) {
            return ImmutableList.of("giftig");
        }

        if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return ImmutableList.of("missmutig", "verdrossen", "mürrisch", "verdrießlich",
                    "griesgrämig");
        }

        if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return ImmutableList.of("reserviert");
        }

        if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of("reserviert");
        }

        return ImmutableList.of();
    }

    @NonNull
    @Override
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckWennTargetGehenMoechteAdjPhr(
            final Person gameObjectSubjektPerson, final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> res = ImmutableList.builder();
        if (feelingIntensity >= -FeelingIntensity.DEUTLICH &&
                feelingIntensity <= -FeelingIntensity.NUR_LEICHT) {
            res.add(ERLEICHTERT);
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            res.add(VERSCHUECHTERT,
                    ENTTAEUSCHT.mitGraduativerAngabe("etwas"));
        } else if (feelingIntensity == FeelingIntensity.DEUTLICH) {
            res.add(ENTTAEUSCHT);
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            res.add(BETRUEBT.mitGraduativerAngabe("etwas"));
        }

        return res.build();
    }
}
