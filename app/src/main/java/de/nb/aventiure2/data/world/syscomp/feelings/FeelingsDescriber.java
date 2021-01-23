package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;

import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt das Gefühl eines Feeling Beings
 * gegenüber dem Target, wenn die beiden sich begegnen.
 */
public interface FeelingsDescriber {
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
    default ImmutableList<AdverbialeAngabeSkopusVerbAllg> altEindruckBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt, final SubstantivischePhrase targetDesc,
            final int feelingIntensity, final boolean targetKnown) {
        return ImmutableList.<AdverbialeAngabeSkopusVerbAllg>builder()
                .addAll(AdjPhrOhneLeerstellen.toAdvAngabenSkopusVerbAllg(gameObjectSubjekt,
                        altEindruckBeiBegegnungAdjPhr(gameObjectSubjekt,
                                targetDesc, feelingIntensity,
                                targetKnown)))
                .addAll(altEindruckBeiBegegnungZusAdverbialeAngaben(feelingIntensity).stream()
                        .map(AdverbialeAngabeSkopusVerbAllg::new)
                        .collect(toSet()))
                .build();
    }

    /**
     * Gibt eventuell alternative Sätze zurück, die auf Basis dieses Gefühls
     * die Reaktion dieses Feeling Beings auf das Target beschreiben, wenn die beiden sich begegnen.
     * Hier werden <i>keine Begrüßungen</i> beschrieben!
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     * <p>
     * Diese Methode soll zumindest immer dann eine nichtleere Liste zurückgeben, wenn
     * {@link #altEindruckBeiBegegnungAdjPhr(SubstantivischePhrase, SubstantivischePhrase, int, boolean)}
     * eine leere Liste zurückgibt, so dass man durch die Kombination beider Methoden immer
     * mindestens einen Satz erhält.
     *
     * @return Möglicherweise eine leere Liste
     */
    @NonNull
    ImmutableList<Satz> altReaktionBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);

    /**
     * Gibt reventuell alternative Prädikativa zurück, die das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreiben, wenn die beiden sich begegnen.
     * Man kann ein solches Prädikativum in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste
     */
    @NonNull
    ImmutableList<Praedikativum> altFeelingBeiBegegnungPraedikativum(
            Person gameObjectSubjektPerson, NumerusGenus gameObjectSubjektNumerusGenus,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den
     * Eindruck beschreiben, den dieses Feeling Being auf das Target macht, wenn die beiden sich
     * begegnen. Die Phrasen können mit
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    @NonNull
    ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            SubstantivischePhrase gameObjectSubjekt,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);

    /**
     * Gibt eventuell alternative Adverbien zurück, die den
     * Eindruck beschreiben, den dieses Feeling Being auf das Target macht, wenn die beiden sich
     * begegnen. Die Adjektivphrasen aus
     * {@link #altEindruckBeiBegegnungAdjPhr(SubstantivischePhrase, SubstantivischePhrase, int, boolean)}
     * werden hier <i>nicht</i> wiederholt.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    @NonNull
    ImmutableList<String>
    altEindruckBeiBegegnungZusAdverbialeAngaben(int feelingIntensity);

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den
     * Eindruck beschreiben, den dieses Feeling Being auf das Target macht, wenn das Targeter
     * gehen möchte. Die Phrasen können mit <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste!
     */
    @NonNull
    ImmutableList<AdjPhrOhneLeerstellen> altEindruckWennTargetGehenMoechteAdjPhr(
            Person gameObjectSubjektPerson, NumerusGenus gameObjectSubjektNumerusGenus,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);
}
