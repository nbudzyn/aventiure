package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;


import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STECHEN;

/**
 * Beschreibt die {@link Temperatur} als {@link AbstractDescription}s.
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturDescDescriber {
    private final TemperaturSatzDescriber satzDescriber;

    public TemperaturDescDescriber(
            final TemperaturSatzDescriber satzDescriber) {
        this.satzDescriber = satzDescriber;
    }


    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die die Temperatur
     * beschreiben.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>> alt(
            final Temperatur temperatur, final AvTime time, final DrinnenDraussen drinnenDraussen) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        // "Es ist kühl"
        alt.addAll(satzDescriber.alt(temperatur, time, drinnenDraussen));

        if (drinnenDraussen.isDraussen()) {
            alt.addAll(altHeuteDerTagWennDraussenSinnvoll(
                    temperatur, time,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL));

            if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
                alt.addAll(satzDescriber
                        .altSonnenhitzeWennHeissUndNichtNachts(temperatur, time,
                                true));
            }
        }

        return alt.schonLaenger().build();
    }


    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä. beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>> altHeuteDerTagWennDraussenSinnvoll(
            final Temperatur temperatur, final AvTime time, final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();
        alt.addAll(altNeueSaetze(
                satzDescriber.altDraussenHeuteDerTagSofernSinnvoll(temperatur, time,
                        !unterOffenemHimmel)));

        if (unterOffenemHimmel && temperatur.compareTo(Temperatur.RECHT_HEISS) >= 0) {
            // "der Tag ist heiß, die Sonne sticht"
            final ImmutableCollection<Satz> heuteDerTagSaetze =
                    satzDescriber.altDraussenHeuteDerTagSofernSinnvoll(temperatur,
                            time, true);
            if (!heuteDerTagSaetze.isEmpty()) {
                alt.addAll(altNeueSaetze(
                        heuteDerTagSaetze,
                        ",",
                        STECHEN.alsSatzMitSubjekt(SONNE)));
            }
        }

        return alt.schonLaenger().build();
    }

}
