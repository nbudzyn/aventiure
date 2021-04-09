package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

/**
 * Veränderliche (und daher persistente) Daten der {@link WetterComp}-Komponente.
 */
@Entity
public class WetterPCD extends AbstractPersistentComponentData {
    /**
     * Das aktuelle Wetter
     */
    @Embedded
    @NonNull
    public final WetterData wetter;

    /**
     * Das Wetter, wie es bis zu einem gewissen (in aller Regel
     * zukünftigen) Zeitpunkt werden soll.
     */
    @Embedded
    @Nullable
    final PlanwetterData plan;

    @Ignore
    WetterPCD(final GameObjectId gameObjectId,
              final WetterData wetter) {
        this(gameObjectId, wetter, null);
    }

    @SuppressWarnings("WeakerAccess")
    public WetterPCD(final GameObjectId gameObjectId,
                     final WetterData wetter,
                     @Nullable final PlanwetterData plan) {
        super(gameObjectId);
        this.wetter = wetter;
        this.plan = plan;
    }

    @NonNull
    AltDescriptionsBuilder altScKommtNachDraussenInsWetter(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        return wetter.altScKommtNachDraussenInsWetter(time, lichtverhaeltnisseDraussen,
                unterOffenenHimmel);
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final AvDateTime startTime,
            final AvDateTime endTime,
            final DrinnenDraussen drinnenDraussen) {
        return wetter.altTimePassed(startTime, endTime, drinnenDraussen);
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll(final AvTime time,
                                                    final boolean unterOffenemHimmel) {
        return wetter.altDescUeberHeuteOderDenTagWennDraussenSinnvoll(time, unterOffenemHimmel);
    }


    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        return wetter.altWohinHinaus(time, lichtverhaeltnisseDraussen, unterOffenenHimmel);
    }

    ImmutableCollection<Praepositionalphrase> altUnterOffenemHimmel(final AvTime time) {
        return wetter.altUnterOffenemHimmel(time);
    }

    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvTime time,
                                                          final boolean unterOffenemHimmel) {
        return wetter.altBeiLichtImLicht(time, unterOffenemHimmel);
    }

    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final AvTime time,
                                                               final boolean unterOffenemHimmel) {
        return wetter.altBeiTageslichtImLicht(time, unterOffenemHimmel);
    }

    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvTime time, final boolean unterOffenemHimmel) {
        return wetter.altLichtInDemEtwasLiegt(time, unterOffenemHimmel);
    }

    @NonNull
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        return wetter.altWetterplauderrede(time);
    }

    @NonNull
    Temperatur getTemperatur(final AvTime time) {
        return wetter.getTemperatur(time);
    }
}
