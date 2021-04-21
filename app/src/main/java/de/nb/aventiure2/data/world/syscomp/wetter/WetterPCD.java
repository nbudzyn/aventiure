package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;

/**
 * Veränderliche (und daher persistente) Daten der {@link WetterComp}-Komponente.
 */
@Entity
public class WetterPCD extends AbstractPersistentComponentData {
    private static final TageszeitPraedikativumDescriber TAGESZEIT_PRAEDIKATIVUM_DESCRIBER =
            new TageszeitPraedikativumDescriber();

    private static final TageszeitSatzDescriber TAGESZEIT_SATZ_DESCRIBER =
            new TageszeitSatzDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitDescDescriber TAGESZEIT_DESC_DESCRIBER =
            new TageszeitDescDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER, TAGESZEIT_SATZ_DESCRIBER);

    /**
     * Das aktuelle Wetter
     */
    @Embedded
    @NonNull
    private final WetterData wetter;

    /**
     * Wenn der SC wieder draußen ist, soll das Wetter beschrieben werden - und zwar auch
     * Erlebnisse, die nach einem Tageszeitenwechsel <i>draußen</i> nur einmalig auftreten (z.B.
     * "Der erste Strahl der aufgehenden Sonne dringt gerade am Himmel herauf" o.Ä.).
     */
    private boolean
            wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel;

    /**
     * Wenn der SC wieder unter offenem Himmel ist, soll das Wetter beschrieben werden.
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
                     final boolean wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel,
                     final boolean wennWiederUnterOffenemHimmelWetterBeschreiben,
                     @Nullable final PlanwetterData plan) {
        super(gameObjectId);
        this.wetter = wetter;
        this.wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel =
                wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel;
        this.wennWiederUnterOffenemHimmelWetterBeschreiben =
                wennWiederUnterOffenemHimmelWetterBeschreiben;
        this.plan = plan;
    }

    /**
     * Gibt - wenn nötig - alternative Wetterhinweise zurück.
     * Die Methode geht in diesem Fall davon aus, dass einer der Wetterhinweise auch
     * <i>ausgegeben</i> wird (und vermerkt entsprechend i.A., dass nicht gleich wieder ein
     * Wetterhinweis nötig sein wird).
     */
    ImmutableCollection<AbstractDescription<?>> altWetterHinweiseWennNoetig(
            final AvTime time, final DrinnenDraussen drinnenDraussen) {
        if ((drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                && wennWiederUnterOffenemHimmelWetterBeschreiben)
                || (drinnenDraussen.isDraussen()
                && wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel)) {
            return altWetterHinweise(time, drinnenDraussen);
        }

        return ImmutableSet.of();
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * oder draußen erlebt - oder eine leere Menge.
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>> altWetterHinweise(
            final AvTime time,
            final DrinnenDraussen drinnenDraussen) {
        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altWetterHinweise(time,
                        drinnenDraussen,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel);

        resetWetterHinweiseFlags(drinnenDraussen);

        return alt;
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie
     * man das Wetter erlebt, wenn man nach draußen kommt, - ggf. eine leere Menge.
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    @NonNull
    ImmutableSet<AbstractDescription<?>> altKommtNachDraussen(
            final AvDateTime time,
            final boolean unterOffenenHimmel) {
        final ImmutableSet<AbstractDescription<?>> alt = wetter.altKommtNachDraussen(time.getTime(),
                unterOffenenHimmel,
                wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel)
                .build();

        if (alt.isEmpty()) {
            return ImmutableSet.of();
        }

        resetWetterHinweiseFlagsDraussen(unterOffenenHimmel);

        return alt;
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer. Falls aber doch nicht leer, so wird ggf. außerdem gespeichert, dass,
     * wenn der Spieler das nächste Mal nach draußen oder unter den offenen Himmel kommt,
     * das Wetter beschrieben werden soll.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>> onTimePassed(
            final AvDateTime startTime,
            final AvDateTime endTime,
            final DrinnenDraussen drinnenDraussen) {
        if (endTime.minus(startTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Dann hat die Spieler-Action sicher ohnehin erzählt, was passiert ist.
            return ImmutableSet.of();
        }

        if (startTime.getTageszeit() == endTime.getTageszeit()) {
            return onTimePassedZwischentageszeitlicherWechsel(
                    startTime.getTime(), endTime.getTime(), drinnenDraussen);
        }

        // Es gab also einen (oder mehrere) Tageszeitenwechsel während einer Zeit
        // von weniger als einem Tag
        return onTimePassedTageszeitensprungOderWechsel(
                startTime.getTageszeit(), endTime.getTageszeit(), drinnenDraussen);
    }

    /**
     * Erzeugt ggf. ein paar Basis-Hinweise, um dem Spieler zu
     * vergegenwärtigen, dass auch über den Tag die Zeit vergeht - zumeist eine leere
     * {@link java.util.Collection}.
     */
    @NonNull
    private static ImmutableCollection<AbstractDescription<?>> onTimePassedZwischentageszeitlicherWechsel(
            final AvTime before,
            final AvTime after,
            final DrinnenDraussen drinnenDraussen) {
        return TAGESZEIT_DESC_DESCRIBER.altZwischentageszeitlicherWechsel(
                before, after, drinnenDraussen.isDraussen());
    }

    /**
     * Gibt alternative Beschreibungen zurück, dass dieser Tageszeitenwechsel
     * geschehen ist; außerdem wird ggf. gespeichert, dass,wenn der Spieler das nächste Mal nach
     * draußen oder unter den offenen Himmel kommt,
     * das Wetter beschrieben werden soll.
     */
    @NonNull
    private ImmutableCollection<AbstractDescription<?>> onTimePassedTageszeitensprungOderWechsel(
            final Tageszeit lastTageszeit,
            final Tageszeit currentTageszeit,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(lastTageszeit != currentTageszeit,
                "Unveränderte Tageszeit: " + lastTageszeit);

        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altTageszeitensprungOderWechsel(lastTageszeit,
                        currentTageszeit, drinnenDraussen);

        resetWetterHinweiseFlags(drinnenDraussen);

        if (!drinnenDraussen.isDraussen()) {
            // Vermerken: Es soll einen Wetterhinweis geben, wenn der SC wieder
            // raus kommt. Auch "einmalige Erlebnisse nach Tageszeitenwechsel"
            // (erster Sonnenstrahl o.Ä.) sollen (einmalig :-) ) erzählt werden.
            setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
                    true);
        }

        if (drinnenDraussen != DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
            // Vermerken: Es soll einen Wetterhinweis geben, wenn der SC unter
            // offenen Himmel tritt.
            setWennWiederUnterOffenemHimmelWetterBeschreiben(true);
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
        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altHeuteDerTagWennDraussenSinnvoll(time, unterOffenemHimmel);

        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);

        return alt;
    }

    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvDateTime time,
            final boolean unterOffenenHimmel) {
        final ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> alt =
                wetter.altWohinHinaus(time.getTime(), unterOffenenHimmel,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel);

        resetWetterHinweiseFlagsDraussen(unterOffenenHimmel);

        return alt;
    }

    ImmutableCollection<AdvAngabeSkopusVerbAllg> altWoDraussen(final AvDateTime time,
                                                               final boolean unterOffenemHimmel) {
        final ImmutableCollection<AdvAngabeSkopusVerbAllg> alt =
                wetter.altWoDraussen(time.getTime(), unterOffenemHimmel,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel);

        // FIXME Geben derzeit alle Aufrufer verlässlich die Info raus?
        //  Vielleicht Methodennamen verändern in der Art "Wetterhinweis..."?
        //  Dann könnte man fordern, dass entweder alle Alternativen einen Wetterhinweis
        //  (Desc, Satz, SubstPhr, ...) enthalten - oder keine?

        resetWetterHinweiseFlagsDraussen(unterOffenemHimmel);

        return alt;
    }

    /**
     * Gibt {@link Praepositionalphrase}n zurück wie "bei Licht" "bei Tageslicht",
     * "im Morgenlicht" o.Ä. Bewölkung, Temperatur und Tageszeit werden nur ansatzweise
     * beschrieben.
     */
    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvDateTime time,
                                                          final boolean unterOffenemHimmel) {
        return wetter.altBeiLichtImLicht(time.getTime(), unterOffenemHimmel);
    }

    /**
     * Gibt alternativen Beschreibungen des Lichts zurück, in dem etwas liegt
     * ("Morgenlicht" o.Ä.). Bewölkung, Temperatur und Tageszeit werden nur ansatzweise
     * beschrieben.
     */
    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvDateTime time, final boolean unterOffenemHimmel) {
        return wetter.altLichtInDemEtwasLiegt(time.getTime(), unterOffenemHimmel);
    }

    @NonNull
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        return wetter.altWetterplauderrede(time);
    }

    @NonNull
    Temperatur getTemperatur(final AvDateTime time) {
        // Nur weil die Temperatur abgefragt wird, gehen wir nicht davon aus, dass ein
        // "qualifizierter" Wetterhinweis gegeben wurde

        return wetter.getTemperatur(time.getTime());
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
            setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
                    false);
            setWennWiederUnterOffenemHimmelWetterBeschreiben(false);
        } else if (drinnenDraussen.isDraussen()) {
            setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
                    false);
        }
    }

    private void setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
            final boolean wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel
    ) {
        if (this.wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel ==
                wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel) {
            return;
        }

        setChanged();
        this.wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel =
                wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel;
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

    boolean isWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel() {
        return wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel;
    }

    boolean isWennWiederUnterOffenemHimmelWetterBeschreiben() {
        return wennWiederUnterOffenemHimmelWetterBeschreiben;
    }
}
