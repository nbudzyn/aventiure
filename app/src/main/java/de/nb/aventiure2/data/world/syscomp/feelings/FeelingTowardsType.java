package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;

import static java.util.stream.Collectors.toList;

/**
 * Ein Spektrum von Gefühlen, dass ein {@link IFeelingBeingGO} gegenüber jemandem oder
 * einer Sache haben kann. Z.B. könnte das Gefühlsspektrum ZUNEIGUNG_ABNEIGUNG zwischen den
 * Extremen abgrundtiefen Hasses und brennender Liebe ausgeprägt sein.
 */
public enum FeelingTowardsType {
    ZUNEIGUNG_ABNEIGUNG(new ZuneigungAbneigungDescriber());
    // Weitere Gefühle könnten zb sein
    //  - VERTRAUEN_MISSTRAUEN
    //  - Dankbarkeit / Rachedurst

    private final FeelingsDescriber feelingsDescriber;

    FeelingTowardsType(final FeelingsDescriber feelingsDescriber) {
        this.feelingsDescriber = feelingsDescriber;
    }

    /**
     * Gibt alternative Sätze zurück, die die durch dieses Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings auf das Target beschreiben, wenn die beiden sich begegnen.
     * Hier werden <i>keine Begrüßungen</i> beschrieben!
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return (gibt niemals eine leere Liste zurück !)
     */
    public ImmutableList<Satz> altReaktionBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        final ImmutableList<AdjPhrOhneLeerstellen> altEindruckAdjPhr =
                altEindruckBeiBegegnungAdjPhr(gameObjectSubjekt,
                        targetDesc, feelingIntensity,
                        targetKnown);

        final ImmutableList<AdvAngabeSkopusVerbAllg> advAngaben =
                altEindruckBeiBegegnungAdvAngaben(
                        gameObjectSubjekt, targetDesc, feelingIntensity, targetKnown);

        final ImmutableList.Builder<Satz> res = ImmutableList.builder();
        res.addAll(FeelingsSaetzeUtil.
                toReaktionSaetze(gameObjectSubjekt, targetDesc,
                        true, altEindruckAdjPhr,
                        advAngaben));

        res.addAll(feelingsDescriber.altReaktionBeiBegegnungSaetze(
                gameObjectSubjekt, targetDesc, feelingIntensity, targetKnown
        ));

        return res.build();
    }

    /**
     * Gibt alternative Sätze zurück, die die durch dieses Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings beschreiben, wenn das Target gehen möchte.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     * <p>
     * Die Methode geht davon aus, dass Subjekt und Feeling Target einander sehen.
     */
    public ImmutableList<Satz> altReaktionWennTargetGehenMoechteSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        final ImmutableList<AdjPhrOhneLeerstellen> altEindruckAdjPhr =
                altEindruckWennTargetGehenMoechteAdjPhr(
                        gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                        targetDesc, feelingIntensity, targetKnown
                );

        final ImmutableList<Satz> saetze = FeelingsSaetzeUtil.toReaktionSaetze(
                gameObjectSubjekt, targetDesc, true,
                altEindruckAdjPhr);

        res.addAll(saetze);

        res.addAll(saetze.stream()
                .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("auf einmal")))
                .collect(toList()));

        return res.build();
    }

    public ImmutableList<Satz> altEindruckBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc,
            final boolean targetKannSubjektSehen,
            final int feelingIntensity, final boolean targetKnown) {
        return FeelingsSaetzeUtil.altEindrueckSaetze(gameObjectSubjekt,
                targetKannSubjektSehen, altEindruckBeiBegegnungAdjPhr(gameObjectSubjekt,
                        targetDesc, feelingIntensity,
                        targetKnown));
    }

    /**
     * Gibt eventuell adverbiale Angaben zurück, die beschreiben, welchen Eindruck dieses
     * Feeling Being - basiert auf diesem {@link FeelingTowardsType} - auf das  Target
     * macht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste!
     */
    public ImmutableList<AdvAngabeSkopusVerbAllg> altEindruckBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt, final SubstantivischePhrase targetDesc,
            final int feelingIntensity, final boolean targetKnown) {
        return feelingsDescriber.altEindruckBeiBegegnungAdvAngaben(gameObjectSubjekt,
                targetDesc, feelingIntensity, targetKnown);
    }

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den Eindruck
     * beschreiben, den dieses Feeling Being auf das Target macht, wenn die beiden sich
     * begegnen. Die Phrasen können mit,
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Diese Sätze sind in
     * {@link #altReaktionBeiBegegnungSaetze(SubstantivischePhrase, SubstantivischePhrase, int, boolean)}
     * bereits enthalten.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        return feelingsDescriber.altEindruckBeiBegegnungAdjPhr(
                gameObjectSubjekt,
                targetDesc,
                feelingIntensity, targetKnown
        );
    }

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den Eindruck
     * beschreiben, den dieses Feeling Being auf das Target macht, wenn das Target gehen möchte.
     * Die Phrasen können mit <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste !
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckWennTargetGehenMoechteAdjPhr(
            final Person gameObjectSubjektPerson,
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        return feelingsDescriber.altEindruckWennTargetGehenMoechteAdjPhr(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc,
                feelingIntensity, targetKnown
        );
    }
}
