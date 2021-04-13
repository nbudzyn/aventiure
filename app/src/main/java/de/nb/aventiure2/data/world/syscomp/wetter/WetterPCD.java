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
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;

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
    private final WetterData wetter;

    /**
     * Wenn der SC wieder draußen ist, soll das Wetter (erneut) beschrieben werden.
     */
    private boolean wennWiederDraussenWetterBeschreiben;

    /**
     * Wenn der SC wieder unter offenem Himmel ist, soll das Wetter (erneut) beschrieben werden.
     */
    private boolean wennWiederUnterOffenemHimmelWetterBeschreiben;

    /**
     * Das Wetter, wie es bis zu einem gewissen (in aller Regel
     * zukünftigen) Zeitpunkt werden soll.
     */
    @Embedded
    @Nullable
    private final PlanwetterData plan;

    @Ignore
    WetterPCD(final GameObjectId gameObjectId,
              final WetterData wetter) {
        this(gameObjectId, wetter,
                true,
                true,
                null);
    }

    @SuppressWarnings("WeakerAccess")
    public WetterPCD(final GameObjectId gameObjectId,
                     final WetterData wetter,
                     final boolean wennWiederDraussenWetterBeschreiben,
                     final boolean wennWiederUnterOffenemHimmelWetterBeschreiben,
                     @Nullable final PlanwetterData plan) {
        super(gameObjectId);
        this.wetter = wetter;
        this.wennWiederDraussenWetterBeschreiben = wennWiederDraussenWetterBeschreiben;
        this.wennWiederUnterOffenemHimmelWetterBeschreiben =
                wennWiederUnterOffenemHimmelWetterBeschreiben;
        this.plan = plan;
    }

    /**
     * Gibt - wenn nötig - alterantive Wetterhinweise zurück.
     * Die Methode geht in diesem Fall davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    ImmutableCollection<AbstractDescription<?>> altWetterHinweiseWennNoetig(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisse,
            final DrinnenDraussen drinnenDraussen) {
        if ((drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                && wennWiederUnterOffenemHimmelWetterBeschreiben)
                || (drinnenDraussen.isDraussen()
                && wennWiederDraussenWetterBeschreiben)) {
            return altWetterHinweiseFuerDraussen(time, lichtverhaeltnisse,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL);
        }

        return ImmutableSet.of();
    }

    /**
     * Gibt alterantive Wetterhinweise zurück (für draußen).
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    private ImmutableCollection<AbstractDescription<?>> altWetterHinweiseFuerDraussen(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisse,
            final boolean unterOffenemHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);
        return wetter.altWetterHinweiseFuerDraussen(time, lichtverhaeltnisse, unterOffenemHimmel);
    }

    @NonNull
    AltDescriptionsBuilder altScKommtNachDraussenInsWetter(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenenHimmel);

        return wetter.altScKommtNachDraussenInsWetter(time, lichtverhaeltnisseDraussen,
                unterOffenenHimmel);
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer. Falls aber doch nicht leer, so wird außerdem gespeichert, dass,
     * wenn der Spieler das nächste Mal nach draußen oder unter den offenen Himmel kommt,
     * das Wetter beschrieben werden soll.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>> registerTimePassed(
            final AvDateTime startTime,
            final AvDateTime endTime,
            final DrinnenDraussen drinnenDraussen) {
        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altTimePassed(startTime, endTime, drinnenDraussen);

        if (alt != null) {
            resetWetterHinweiseFlags(drinnenDraussen);

            if (!drinnenDraussen.isDraussen()) {
                setWennWiederDraussenWetterBeschreiben(true);
            }

            if (drinnenDraussen != DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
                setWennWiederUnterOffenemHimmelWetterBeschreiben(true);
            }
        }

        return alt;
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll(final AvTime time,
                                                    final boolean unterOffenemHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);

        return wetter.altDescUeberHeuteOderDenTagWennDraussenSinnvoll(time, unterOffenemHimmel);
    }


    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenenHimmel);

        return wetter.altWohinHinaus(time, lichtverhaeltnisseDraussen, unterOffenenHimmel);
    }

    ImmutableCollection<Praepositionalphrase> altUnterOffenemHimmel(final AvTime time) {
        resetWetterHinweiseFlagsDraussen(true);

        return wetter.altUnterOffenemHimmel(time);
    }

    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvTime time,
                                                          final boolean unterOffenemHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);

        return wetter.altBeiLichtImLicht(time, unterOffenemHimmel);
    }

    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final AvTime time,
                                                               final boolean unterOffenemHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);

        return wetter.altBeiTageslichtImLicht(time, unterOffenemHimmel);
    }

    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvTime time, final boolean unterOffenemHimmel) {
        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);

        return wetter.altLichtInDemEtwasLiegt(time, unterOffenemHimmel);
    }

    @NonNull
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        return wetter.altWetterplauderrede(time);
    }

    @NonNull
    Temperatur getTemperatur(final AvTime time) {
        // Nur weil die Temperatur abgefragt wird, gehen wir nicht davon aus, dass ein
        // "qualifizierter" Wetterhinweis gegeben wurde

        return wetter.getTemperatur(time);
    }

    /**
     * Soll nur von ROOM aufgerufen werden. (Sonst wäre nicht klar, ob schon Wetterhinweise
     * gegeben wurden.)
     */
    @NonNull
    WetterData getWetter() {
        return wetter;
    }

    @Nullable
    PlanwetterData getPlan() {
        return plan;
    }

    /**
     * Vermerkt, dass gerade draußen ein Wetterhinweis gegeben wird.
     */
    private void resetWetterHinweiseFlagsDraussen(final boolean unterOffenemHimmel) {
        resetWetterHinweiseFlags(unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                DRAUSSEN_GESCHUETZT);
    }

    /**
     * Vermerkt, dass gerade ein Wetterhinweis gegeben wird.
     */
    private void resetWetterHinweiseFlags(final DrinnenDraussen drinnenDraussen) {
        if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
            setWennWiederDraussenWetterBeschreiben(false);
            setWennWiederUnterOffenemHimmelWetterBeschreiben(false);
        } else if (drinnenDraussen.isDraussen()) {
            setWennWiederDraussenWetterBeschreiben(false);
        }
    }

    private void setWennWiederDraussenWetterBeschreiben(
            final boolean wennWiederDraussenWetterBeschreiben
    ) {
        if (this.wennWiederDraussenWetterBeschreiben ==
                wennWiederDraussenWetterBeschreiben) {
            return;
        }

        setChanged();
        this.wennWiederDraussenWetterBeschreiben = wennWiederDraussenWetterBeschreiben;
    }

    private void setWennWiederUnterOffenemHimmelWetterBeschreiben(
            final boolean wennWiederUnterOffenemHimmelWetterBeschreiben) {
        if (this.wennWiederUnterOffenemHimmelWetterBeschreiben ==
                wennWiederUnterOffenemHimmelWetterBeschreiben) {
            return;
        }

        setChanged();
        this.wennWiederUnterOffenemHimmelWetterBeschreiben
                = wennWiederUnterOffenemHimmelWetterBeschreiben;
    }

    boolean isWennWiederDraussenWetterBeschreiben() {
        return wennWiederDraussenWetterBeschreiben;
    }

    boolean isWennWiederUnterOffenemHimmelWetterBeschreiben() {
        return wennWiederUnterOffenemHimmelWetterBeschreiben;
    }
}
