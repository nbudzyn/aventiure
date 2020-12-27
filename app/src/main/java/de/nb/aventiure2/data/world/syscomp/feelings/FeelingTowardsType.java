package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;

/**
 * Ein Spektrum von Gefühlen, dass ein {@link IFeelingBeingGO} gegenüber jemandem oder
 * einer Sache haben kann. Z.B. könnte das Gefühlsspektrum ZUNEIGUNG_ABNEIGUNG zwischen den
 * Extremen abgrundtiefen Hasses und brennender Liebe ausgeprägt sein.
 */
public enum FeelingTowardsType {
    ZUNEIGUNG_ABNEIGUNG(new ZuneigungAbneigungBeiBegegnungDescriber());
    // Weitere Gefühle könnten zb sein
    //  - VERTRAUEN_MISSTRAUEN
    //  - Dankbarkeit / Rachedurst

    private final FeelingBeiBegegnungDescriber feelingBeiBegegnungDescriber;

    FeelingTowardsType(final FeelingBeiBegegnungDescriber feelingBeiBegegnungDescriber) {
        this.feelingBeiBegegnungDescriber = feelingBeiBegegnungDescriber;
    }

    /**
     * Gibt alternative Sätze zurück, die das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreiben, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return (gibt niemals eine leere Liste zurück !)
     */
    public ImmutableList<Satz> altFeelingBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        res.addAll(altEindruckBeiBegegnungAdjPhr(
                gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                targetDesc, feelingIntensity, targetKnown
        ).stream()
                .flatMap(adjPhr ->
                        Stream.of("offenkundig", "sichtlich", "offenbar", "ganz offenbar")
                                .map(
                                        advAng ->
                                                praedikativumPraedikatMit(adjPhr)
                                                        .mitAdverbialerAngabe(
                                                                new AdverbialeAngabeSkopusSatz(
                                                                        advAng))
                                                        .alsSatzMitSubjekt(gameObjectSubjekt)
                                )
                ).collect(ImmutableList.toImmutableList()));

        res.addAll(feelingBeiBegegnungDescriber.altFeelingBeiBegegnungSaetze(
                gameObjectSubjekt, targetDesc, feelingIntensity, targetKnown
        ));

        return res.build();
    }


    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den Eindruck
     * beschreiben, den dieses Feeling Being auf das Target macht, wenn die beiden sich
     * begegnen. Die Phrasen können mit,
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            final Person gameObjectSubjektPerson,
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        return feelingBeiBegegnungDescriber.altEindruckBeiBegegnungAdjPhr(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc,
                feelingIntensity, targetKnown
        );
    }
}
