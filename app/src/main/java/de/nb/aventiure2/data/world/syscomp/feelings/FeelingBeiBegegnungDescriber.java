package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AllgDescription;

/**
 * Beschreibt das Gefühl eines Feeling Beings
 * gegenüber dem Target, wenn die beiden sich begegnen.
 */
public interface FeelingBeiBegegnungDescriber {
    /**
     * Gibt reventuell alternative Prädikativa zurück, die das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreiben, wenn die beiden sich begegnen.
     * Man kann ein solches Prädikativum in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     * <p>
     * Diese Methode gibt zumindest immer dann eine nichtleere Liste zurück, wenn
     * {@link #altFeelingBeiBegegnungPraedAdjPhrase(Person, NumerusGenus, SubstantivischePhrase, int, boolean)}
     * eine leere Liste zurückgibt. Durch die Kombination bei der Methoden erhält man
     * also immer mindestens eine Beschreibung.
     *
     * @return Möglicherweise eine leere Liste
     */
    @NonNull
    ImmutableList<AllgDescription> altFeelingBeiBegegnungPraedikativ(
            Person gameObjectSubjektPerson, NumerusGenus gameObjectSubjektNumerusGenus,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);

    /**
     * Gibt eventuell alternative prädikative Adjektivphrasen zurück, die das
     * Gefühl dieses Feeling Beings gegenüber dem Target beschreiben, wenn die beiden sich
     * begegnen, und die mit
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden können.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    @NonNull
    // TODO Auch die Zauberin könnte irgendwie wirken, wenn sie
    //  den SC (wieder-) trifft...
    ImmutableList<AllgDescription> altFeelingBeiBegegnungPraedAdjPhrase(
            Person gameObjectSubjektPerson, NumerusGenus gameObjectSubjektNumerusGenus,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);
}
