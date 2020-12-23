package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AllgDescription;

import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;

/**
 * Ein Spektrum von Gefühlen, dass ein {@link IFeelingBeingGO} gegenüber jemandem oder
 * einer Sache haben kann. Z.B. könnte das Gefühlsspektrum ZUNEIGUNG_ABNEIGUNG zwischen den
 * Extremen abgrundtiefen Hasses und brennender Liebe ausgeprägt sein.
 */
public enum FeelingTowardsType {
    ZUNEIGUNG_ABNEIGUNG(new ZuneigungAbneigungBeiBegegnungDescriber());
    // STORY Weitere Gefühle könnten zb sein
    //  - VERTRAUEN_MISSTRAUEN
    //  - Dankbarkeit / Rachedurst

    private final FeelingBeiBegegnungDescriber feelingBeiBegegnungDescriber;

    FeelingTowardsType(final FeelingBeiBegegnungDescriber feelingBeiBegegnungDescriber) {
        this.feelingBeiBegegnungDescriber = feelingBeiBegegnungDescriber;
    }

    /**
     * Gibt alternative Prädikativa zurück, die das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreiben, wenn die beiden sich begegnen.
     * Man kann ein solches Prädikativum in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return (gibt niemals eine leere Liste zurück !)
     */
    public ImmutableList<AllgDescription> altFeelingBeiBegegnungPraedikativum(
            final Person gameObjectSubjektPerson,
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        final ImmutableList.Builder<AllgDescription> res = ImmutableList.builder();

        final ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhrasen =
                altEindruckBeiBegegnungAdjPhr(
                        gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc,
                        feelingIntensity, targetKnown
                );

        final ImmutableList<AllgDescription> altEindruckBeiBegegnungPraedAdjPhrasen =
                altEindruckBeiBegegnungAdjPhrasen.stream()
                        .map(adjPhr ->
                                satzanschluss(
                                        adjPhr.getPraedikativ(
                                                gameObjectSubjektPerson,
                                                gameObjectSubjektNumerusGenus.getNumerus())))
                        .collect(ImmutableList.toImmutableList());

        // FIXME Hier Prädikat- oder Satz-Instanzen erzeugen und zurückgeben!

        res.addAll(mitPraefix(
                altEindruckBeiBegegnungPraedAdjPhrasen, "offenkundig",
                "sichtlich",
                "offenbar", "ganz offenbar"
        ));

        res.addAll(feelingBeiBegegnungDescriber.altFeelingBeiBegegnungPraedikativ(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc,
                feelingIntensity, targetKnown
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

    @CheckReturnValue
    private static Iterable<AllgDescription> mitPraefix(
            final Collection<? extends AllgDescription> altPraedAdjPhrasen,
            final String... prefixes) {
        return Stream.of(prefixes)
                .flatMap(
                        p -> altPraedAdjPhrasen.stream()
                                .map(phr -> mitPraefix(p, phr))
                                .collect(ImmutableList.toImmutableList())
                                .stream()
                )
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    private static AllgDescription mitPraefix(final String praefix,
                                              final AllgDescription desc) {
        final String hauptsatz = desc.getDescriptionHauptsatz();

        return satzanschluss(
                praefix
                        + " "
                        + hauptsatz)
                .komma(desc.isKommaStehtAus())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }
}
