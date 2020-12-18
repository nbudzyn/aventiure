package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;

/**
 * Builder für ein Prädikativum, das das Gefühl dieses Feeling Beings
 * gegenüber dem Target beschreibt, wenn die beiden sich begegnen.
 * Man kann das erzeugte Prädikativ in einer Konstruktion wie "Rapunzel ist ..." verwenden.
 */
@FunctionalInterface
public interface FeelingBeiBegegnungPraedikativumBuilder {
    /**
     * Gibt ein Prädikativum zurück, das das Gefühl dieses Feeling Beings
     * gegenüber dem Target beschreibt, wenn die beiden sich begegnen.
     * Man kann dieses Prädikativum in einer Konstruktion wie "Rapunzel ist ..." verwenden.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     */
    @NonNull
    AbstractDescription<?> getFeelingBeiBegegnungPraedikativ(
            Person gameObjectSubjektPerson, NumerusGenus gameObjectSubjektNumerusGenus,
            SubstantivischePhrase targetDesc, int feelingIntensity,
            final boolean targetKnown);
}
