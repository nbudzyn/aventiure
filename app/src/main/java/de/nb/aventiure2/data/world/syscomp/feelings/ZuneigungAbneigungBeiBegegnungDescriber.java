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
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

/**
 * Beschreibt die Zuneigung oder Abneigung eines Feeling Beings
 * gegenüber dem Target, wenn die beiden sich begegnen.
 */
class ZuneigungAbneigungBeiBegegnungDescriber implements FeelingBeiBegegnungDescriber {
    @Override
    @NonNull
    public ImmutableList<Satz> altReaktionBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        // Damit niemals etwas wie "du, der du" erzeugt wird:
        // Keine Relativpronomen von targetDesc erzeugen - jedenfalls nicht solche
        // im Nominativ!

        ImmutableList.Builder<Satz> res = ImmutableList.builder();

        res.addAll(
                altFeelingBeiBegegnungPraedikativum(
                        gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                        targetDesc, feelingIntensity, targetKnown).stream()
                        .map(PraedikativumPraedikatOhneLeerstellen::praedikativumPraedikatMit)
                        .map(p -> p.alsSatzMitSubjekt(gameObjectSubjekt))
                        .collect(ImmutableList.toImmutableList())
        );

        final ImmutableList<VerbSubjObj> altSehenVerben =
                targetKnown ? ImmutableList.of(VerbSubjObj.WIEDERSEHEN, SEHEN) :
                        ImmutableList.of(SEHEN);

        if (feelingIntensity <= -FeelingIntensity.SEHR_STARK) {
            // "ganz außer sich vor Wut, als sie dich sieht"
            return altSehenVerben.stream()
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
                    .collect(ImmutableList.toImmutableList());
        } else if (feelingIntensity == -FeelingIntensity.STARK) {
            return altSehenVerben.stream()
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
                    .collect(ImmutableList.toImmutableList());
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return altSehenVerben.stream()
                    .map(v ->
                            // "Sie freut sich, dich zu sehen"
                            SICH_FREUEN_ZU
                                    .mitLexikalischemKern(
                                            v.mit(targetDesc)
                                    )
                                    .alsSatzMitSubjekt(gameObjectSubjekt))
                    .collect(ImmutableList.toImmutableList());
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            res = ImmutableList.builder();

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

            return res.build();
        } else if (feelingIntensity >= FeelingIntensity.SEHR_STARK) {
            // "außer sich vor Freude, als sie dich sieht"
            return altSehenVerben.stream()
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
                    .collect(ImmutableList.toImmutableList());
        }

        return ImmutableList.of();

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
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
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
        } else if (feelingIntensity == -FeelingIntensity.DEUTLICH) {
            return ImmutableList.of(AdjektivOhneErgaenzungen.VERAERGERT);
        } else if (feelingIntensity == -FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.VERSTIMMT,
                    AdjektivOhneErgaenzungen.RESERVIERT);
        } else if (feelingIntensity == -FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.RESERVIERT,
                    AdjektivOhneErgaenzungen.UEBERRASCHT,
                    AdjektivOhneErgaenzungen.UEBERRUMPELT
                            .mitGraduativerAngabe("etwas"),
                    AdjektivOhneErgaenzungen.VERWUNDERT
            );
        } else if (feelingIntensity == FeelingIntensity.NEUTRAL) {
            return ImmutableList.of(
                    new ZweiAdjPhrOhneLeerstellen(
                            AdjektivOhneErgaenzungen.UEBERRASCHT,
                            AdjektivOhneErgaenzungen.VERWIRRT.mitGraduativerAngabe("etwas")
                    )
            );
        } else if (feelingIntensity == FeelingIntensity.NUR_LEICHT) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.UEBERRASCHT.mitGraduativerAngabe("etwas"),
                    // "überrascht, dich [target] zu sehen"
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc)));
        } else if (feelingIntensity == FeelingIntensity.MERKLICH) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.UEBERRASCHT.mitGraduativerAngabe("etwas"),
                    // "etwas überrascht, dich [target] zu sehen"
                    AdjektivMitZuInfinitiv.UEBERRASCHT
                            .mitLexikalischerKern(sehenVerb.mit(targetDesc))
                            .mitGraduativerAngabe("etwas"));
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
        } else if (feelingIntensity == FeelingIntensity.STARK) {
            return ImmutableList.of(
                    AdjektivOhneErgaenzungen.FROEHLICH.mitGraduativerAngabe("ganz")
            );
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
