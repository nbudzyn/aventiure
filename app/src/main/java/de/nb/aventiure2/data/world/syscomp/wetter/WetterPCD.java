package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitSatzDescriber;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRINNEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FRISCH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.OFFEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;

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

    @Nullable
    private Temperatur lastAktuelleGenerelleTemperatur;

    @Nullable
    private AvDateTime timeLetzterBeschriebenerTageszeitensprungOderWechsel;

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
                null,
                null,
                true,
                true,
                null);
    }

    @SuppressWarnings("WeakerAccess")
    public WetterPCD(final GameObjectId gameObjectId,
                     final WetterData wetter,
                     @Nullable final Temperatur lastAktuelleGenerelleTemperatur,
                     @Nullable
                     final AvDateTime timeLetzterBeschriebenerTageszeitensprungOderWechsel,
                     final boolean wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel,
                     final boolean wennWiederUnterOffenemHimmelWetterBeschreiben,
                     @Nullable final PlanwetterData plan) {
        super(gameObjectId);
        this.wetter = wetter;
        this.lastAktuelleGenerelleTemperatur = lastAktuelleGenerelleTemperatur;
        this.timeLetzterBeschriebenerTageszeitensprungOderWechsel =
                timeLetzterBeschriebenerTageszeitensprungOderWechsel;
        this.wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel =
                wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel;
        this.wennWiederUnterOffenemHimmelWetterBeschreiben =
                wennWiederUnterOffenemHimmelWetterBeschreiben;
        this.plan = plan;
    }

    /**
     * Muss aufgerufen werden, wenn der SC einen Raum betritt. Vermerkt die Temperatur des
     * Raums und gibt außerdem wenn der SC z.B. zum kühlen Brunnen kommt, alternative
     * Texte zurück in der Art "hier ist es angenehm kühl" - oder eine leere
     * {@link java.util.Collection}.
     */
    ImmutableCollection<AbstractDescription<?>> altOnScEnter(
            final AvDateTime time,
            @Nullable final EnumRange<Temperatur> locationTemperaturRangeFrom,
            final EnumRange<Temperatur> locationTemperaturRangeTo) {
        if (locationTemperaturRangeFrom == null) {
            // Der SC ist aus dem "Nichts" aufgetaucht.
            return ImmutableList.of();
        }

        // Wir ermitteln die Temperatur der vorigen Location und die Temperatur dieser
        // Location - beide für denselben Zeitpunkt! (Wir wollen hier keine Sätze
        // erzeugen, wenn es einen allgemeinen Temperatursturz gab - sondern nur, wenn
        // der SC z.B. von einem warmen in in einen kühlen Raum kommt.
        final Temperatur temperaturFrom = getLokaleTemperatur(time, locationTemperaturRangeFrom);
        final Temperatur temperaturTo = getLokaleTemperatur(time, locationTemperaturRangeTo);

        final int delta = temperaturTo.minus(temperaturFrom);

        if (Math.abs(delta) < 2) {
            return ImmutableList.of();
        }

        if ( // Es ist wärmer als an der Location zu vor - aber nicht zu warm...
                (delta > 0 && temperaturTo.compareTo(Temperatur.WARM) <= 0)
                        // ...oder es ist kälter als an der Location zu vor - aber nicht zu kalt:
                        || (delta < 0 && temperaturTo.compareTo(Temperatur.KUEHL) >= 0)) {
            // Die Temperatur ist angenehmer als an der Location zuvor.
            return wetter.altTemperaturUnterschiedZuVorLocation(
                    time.getTime(), locationTemperaturRangeTo, delta);
        }

        // Die Temperatur ist (wieder) unangenehmer als an der Location zuvor.

        if (!temperaturTo.isUnauffaellig(time.getTageszeit())) {
            // Wir merken uns, dass später noch einmal auf das unangenehme Wetter
            // hingewiesen werden soll. Und zwar, wenn der SC wieder unter offenen Himmel
            // kommt - dann ist das Wetter sicher am auffälligsten.
            setWennWiederUnterOffenemHimmelWetterBeschreiben(true);
        }

        return ImmutableList.of();
    }

    /**
     * Gibt - wenn nötig - alternative Wetterhinweise zurück.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    ImmutableCollection<AbstractDescription<?>> altWetterhinweiseWennNoetig(
            final AvDateTime time, final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if ((drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                && wennWiederUnterOffenemHimmelWetterBeschreiben)
                || (drinnenDraussen.isDraussen()
                && wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel)) {
            return altWetterhinweise(time, drinnenDraussen, locationTemperaturRange);
        }

        return ImmutableSet.of();
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" als {@link AbstractDescription}s zurück, wie
     * man es drinnen oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>> altWetterhinweise(
            final AvDateTime time,
            final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableList.of();
        }

        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altWetterhinweise(time.getTime(),
                        drinnenDraussen,
                        locationTemperaturRange,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel);

        if (!alt.isEmpty()) {
            saveWetterhinweisGegeben(drinnenDraussen);
        }

        return alt;
    }

    /**
     * Gibt alternative Beschreibungen des Wetters als {@link AbstractDescription}s zurück, wie
     * man das Wetter erlebt, wenn man nach draußen kommt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    @NonNull
    ImmutableSet<AbstractDescription<?>> altWetterhinweiseKommtNachDraussen(
            final AvDateTime time,
            final boolean unterOffenenHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableSet.of();
        }

        final ImmutableSet<AbstractDescription<?>> alt = wetter.altKommtNachDraussen(
                time.getTime(),
                unterOffenenHimmel,
                locationTemperaturRange,
                wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel)
                .build();

        if (!alt.isEmpty()) {
            saveWetterhinweisDraussenGegeben(unterOffenenHimmel);
        }

        return alt;
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer. Außerdem wird ggf. auch gespeichert, dass, wenn der Spieler das nächste
     * Mal nach draußen oder unter den offenen Himmel kommt, das Wetter beschrieben werden soll.
     * <p>
     * Wenn es hier Beschreibungen gab und keine von ihnen ausgegeben wird, wird der
     * Tageszeitensprung oder -wechsel nicht mehr beschrieben werden.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final AvDateTime startTime,
            final AvDateTime endTime,
            @Nullable final ILocationGO location) {
        final DrinnenDraussen drinnenDraussen =
                location != null ? location.storingPlaceComp().getDrinnenDraussen() :
                        // SC hat die Welt verlassen? Dann hat er wohl keinen Blick auf den
                        // weltlichen
                        // Himmel.
                        DRINNEN;

        @Nullable final EnumRange<Temperatur> locationTemperaturRange =
                location != null ?
                        location.storingPlaceComp().getEffectiveTemperaturRange() :
                        EnumRange.all(Temperatur.class);

        final Temperatur aktuelleGenerelleTemperatur = getAktuelleGenerelleTemperatur(endTime);
        Temperatur previousLokaleTemperaturBeiRelevanterAenderung = null;
        Temperatur currentLokaleTemperaturBeiRelevanterAenderung = null;
        if ( // Die generelle aktuelle Temperatur hat sich gerade geändert...
                lastAktuelleGenerelleTemperatur != null
                        && aktuelleGenerelleTemperatur != lastAktuelleGenerelleTemperatur
                        // ...und die Temperatur ist oder war auffällig...
                        && (
                        !lastAktuelleGenerelleTemperatur.isUnauffaellig(startTime.getTageszeit())
                                || !aktuelleGenerelleTemperatur
                                .isUnauffaellig(endTime.getTageszeit()))) {
            final Temperatur previousLokaleTemperatur =
                    locationTemperaturRange.clamp(lastAktuelleGenerelleTemperatur);
            final Temperatur currentLokaleTemperatur =
                    locationTemperaturRange.clamp(aktuelleGenerelleTemperatur);
            if ( // Der SC hat die Veränderung auch merken können...
                    previousLokaleTemperatur != currentLokaleTemperatur
                            // ...und sie passiert über eine eher kurze Zeit
                            && endTime.minus(startTime).shorterThan(AvTimeSpan.hours(2))) {
                previousLokaleTemperaturBeiRelevanterAenderung = previousLokaleTemperatur;
                currentLokaleTemperaturBeiRelevanterAenderung = currentLokaleTemperatur;
            } else {
                // Der SC hat die Veränderung nicht merken können (er war gerade an einem
                // kühlen Ort, im beheizten Schloss o.Ä.) - oder die Temperaturänderung passierte
                // über lange Zeit (wir wollen ein "auf einmal ist es eiskalt geworden"
                // vermeiden, wenn es in Wirklichkeit über 5 Stunden allmählich kühler und
                // kühler geworden ist). Später soll es einen Wetterhinweis geben.
                setGgfSpaeterWetterBeschreiben(drinnenDraussen);
                // IDEA Man könnte auch später etwas schreiben wie "Draußen hat sich das Wetter
                //  verändert. Es hat deutlich abgekühlt.", "Draußen hat sich das Wetter
                //  verändert. Es hat deutlich abgekühlt und der Himmel bezieht sich." oder
                //  "Inzwischen ist es ziemlich warm / ziemlich kalt, ..." Das alles
                //  ergibt aber nur Sinn, wenn man weiß, welche (Temperatur, ...)-Werte der Benutzer
                //  an diesem Ort tatsächlich erwartet hätte. Da jeder Ort seine eigenen
                //  Minimal- / Maximaltemperatur hat, ist das (für den allgemeinen Fall)
                //  etwas mühevoll.
            }
        }

        final ImmutableCollection<AbstractDescription<?>> alt =
                altTimePassed(startTime, endTime,
                        !locationTemperaturRange.isInRange(aktuelleGenerelleTemperatur),
                        previousLokaleTemperaturBeiRelevanterAenderung,
                        currentLokaleTemperaturBeiRelevanterAenderung,
                        drinnenDraussen);

        setLastAktuelleGenerelleTemperatur(aktuelleGenerelleTemperatur);

        return alt;
    }


    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer. Außerdem wird ggf. auch gespeichert, dass, wenn der Spieler das nächste
     * Mal nach draußen oder unter den offenen Himmel kommt, das Wetter beschrieben werden soll.
     * <p>
     * Wenn es hier Beschreibungen gab und keine von ihnen ausgegeben wird, wird der
     * Tageszeitensprung oder -wechsel nicht mehr beschrieben werden.
     *
     * @param lastLokaleTemperaturBeiRelevanterAenderung    Falls eine Temperaturänderung
     *                                                      beschrieben werden soll, so steht
     *                                                      hier die lokale Temperatur vor der
     *                                                      Änderung. Es kann hier zu seltenen
     *                                                      Fällen kommen, dass der SC diese
     *                                                      vorherige Temperatur an diesem Ort
     *                                                      noch gar nicht erlebt und auch gar
     *                                                      nicht erwartet hat - z.B. wenn der SC
     *                                                      den ganzen heißen Tag an einem kühlen
     *                                                      Ort verbringt den kühlen Ort genau in
     *                                                      dem Moment verlässt, wenn der Tag sich
     *                                                      wieder abkühlt. Zurzeit berücksichtigen
     *                                                      wir diese Fälle nicht.
     * @param currentLokaleTemperaturBeiRelevanterAenderung Falls eine Temperaturänderung
     *                                                      beschrieben werden soll, so steht
     *                                                      hier die lokale Temperatur nach  der
     *                                                      Änderung; muss und darf nur
     *                                                      angegeben sein, wenn
     *                                                      lastLokaleTemperaturBeiRelevanterAenderung
     *                                                      angegeben ist und muss dann
     *                                                      unterschiedlich sein.
     */
    @NonNull
    private ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final AvDateTime lastTime,
            final AvDateTime currentTime,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final Temperatur lastLokaleTemperaturBeiRelevanterAenderung,
            @Nullable final Temperatur currentLokaleTemperaturBeiRelevanterAenderung,
            // FIXME Hier auch Bewölkungsänderungen etc. einarbeiten
            final DrinnenDraussen drinnenDraussen) {
        final boolean tageszeitaenderungSollBeschriebenWerden =
                !currentTime.minus(lastTime).longerThan(AvTimeSpan.ONE_DAY) &&
                        lastTime.getTageszeit() != currentTime.getTageszeit();

        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altTimePassed(lastTime, currentTime,
                        tageszeitaenderungSollBeschriebenWerden,
                        generelleTemperaturOutsideLocationTemperaturRange,
                        lastLokaleTemperaturBeiRelevanterAenderung,
                        currentLokaleTemperaturBeiRelevanterAenderung,
                        drinnenDraussen
                );

        if (tageszeitaenderungSollBeschriebenWerden) {
            if (timeLetzterBeschriebenerTageszeitensprungOderWechsel == null
                    || currentTime
                    .isAfter(timeLetzterBeschriebenerTageszeitensprungOderWechsel)) {
                setTimeLetzterBeschriebenerTageszeitensprungOderWechsel(currentTime);
                // Damit befinden wir uns nicht mehr im "schwebenden Tageszeitenwechsel",
                // sondern können uns bei weiteren Ausgaben auf die aktuelle Tageszeit beziehen.
            }

            setGgfSpaeterWetterBeschreiben(drinnenDraussen);
        }

        return alt;
    }

    private void setGgfSpaeterWetterBeschreiben(final DrinnenDraussen drinnenDraussen) {
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
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     * (Dies ist kein Wetterhinweis. Wenn noch ein Wetterhinweis nötig ist, wird noch einer
     * folgen.
     * Der Text kann erzählt werden - oder auch nicht.)
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll(
            final AvTime time,
            final boolean unterOffenemHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der
            //  Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableSet.of();
        }

        return wetter.altHeuteDerTagWennDraussenSinnvoll(
                time, unterOffenemHimmel, locationTemperaturRange);

        // Kein "vollwertiger Wetterhinweis" - Flags bleiben unverändert.
    }

    private void saveWetterhinweisDraussenGegeben(final boolean unterOffenemHimmel) {
        saveWetterhinweisGegeben(
                unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL : DRAUSSEN_GESCHUETZT);
    }

    /**
     * Gibt alternative Wetterhinweis in Form  adverbialer Angaben zurück.
     * <p>
     * Der Aufrufer muss dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert
     * wurde.
     */
    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWetterhinweiseWohinHinaus(
            final AvDateTime time,
            final boolean unterOffenenHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - möglichst defensiv formulieren!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der
            //  Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            if (unterOffenenHimmel) {
                return ImmutableSet
                        .of(new AdvAngabeSkopusVerbWohinWoher(
                                UNTER_AKK.mit(HIMMEL.mit(OFFEN))));
            } else {
                return ImmutableSet
                        .of(new AdvAngabeSkopusVerbWohinWoher(AN_AKK.mit(LUFT.mit(FRISCH))));
            }
        }

        final ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> alt =
                wetter.altWetterhinweiseWohinHinaus(time.getTime(), unterOffenenHimmel,
                        locationTemperaturRange,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel);

        saveWetterhinweisDraussenGegeben(unterOffenenHimmel);

        return alt;
    }

    /**
     * Gibt alternative wetterbezogene Ortsbeschreibungen für draußen zurück.
     * <p>
     * Der Aufrufer muss dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Leser über das aktuelle Wetter informiert
     * wurde.
     *
     * @param time               Die Zeit, zu der der SC dort (angekommen) ist
     * @param unterOffenemHimmel Ob der SC (dann) unter offenen Himmel ist
     */
    ImmutableCollection<AdvAngabeSkopusVerbAllg> altWetterhinweiseWoDraussen(
            final AvDateTime time,
            final boolean unterOffenemHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - möglichst defensiv formulieren!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der
            //  Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            if (unterOffenemHimmel) {
                return ImmutableSet
                        .of(new AdvAngabeSkopusVerbAllg(UNTER_DAT.mit(HIMMEL.mit(OFFEN))));
            } else {
                return ImmutableSet
                        .of(new AdvAngabeSkopusVerbAllg(AN_DAT.mit(LUFT.mit(FRISCH))));
            }
        }

        final ImmutableCollection<AdvAngabeSkopusVerbAllg> alt =
                wetter.altWetterhinweisWoDraussen(time.getTime(), unterOffenemHimmel,
                        locationTemperaturRange,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel);

        saveWetterhinweisDraussenGegeben(unterOffenemHimmel);

        return alt;
    }

    /**
     * Gibt {@link Praepositionalphrase}n zurück wie "bei Licht" "bei Tageslicht",
     * "im Morgenlicht" o.Ä. Bewölkung, Temperatur und Tageszeit werden nur ansatzweise
     * beschrieben. (Dies ist kein Wetterhinweis. Wenn noch ein Wetterhinweis nötig ist, wird
     * noch einer folgen.)
     */
    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvDateTime time,
                                                          final boolean unterOffenemHimmel) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - möglichst defensiv formulieren!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der
            //  Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableSet.of(BEI_DAT.mit(Nominalphrase.npArtikellos(LICHT)));
        }

        return wetter.altBeiLichtImLicht(time.getTime(), unterOffenemHimmel);
    }

    /**
     * Gibt alternativen Beschreibungen des Lichts zurück, in dem etwas liegt
     * ("Morgenlicht" o.Ä.). Bewölkung, Temperatur und Tageszeit werden nur ansatzweise
     * beschrieben. (Dies ist kein Wetterhinweis. Wenn noch ein Wetterhinweis nötig ist, wird
     * noch einer folgen.)
     */
    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvDateTime time, final boolean unterOffenemHimmel) {
        if (timeLetzterBeschriebenerTageszeitensprungOderWechsel != null &&
                timeLetzterBeschriebenerTageszeitensprungOderWechsel.getTageszeit() != time
                        .getTageszeit()) {
            // Schwebender Tageszeitenwechsel - möglichst defensiv formulieren!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der
            //  Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableList.of(LICHT);
        }

        return wetter.altLichtInDemEtwasLiegt(
                time.getTime(), unterOffenemHimmel);
    }

    @NonNull
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        return wetter.altWetterplauderrede(time);
    }

    @NonNull
    Temperatur getLokaleTemperatur(final AvDateTime time,
                                   final EnumRange<Temperatur> locationTemperaturRange) {
        // Nur weil die Temperatur abgefragt wird, gehen wir nicht davon aus, dass ein
        // "qualifizierter" Wetterhinweis gegeben wurde

        return wetter.getLokaleTemperatur(time.getTime(), locationTemperaturRange);
    }

    @NonNull
    private Temperatur getAktuelleGenerelleTemperatur(final AvDateTime time) {
        // Nur weil die aktuelle generelle Temperatur abgefragt wird, gehen wir nicht davon aus,
        // dass ein
        // "qualifizierter" Wetterhinweis gegeben wurde

        return wetter.getAktuelleGenerelleTemperatur(time.getTime());
    }

    /**
     * Soll nur von ROOM aufgerufen werden. (Sonst wäre nicht klar, ob schon Wetterhinweise
     * gegeben wurden.)
     */
    @NonNull
    WetterData getWetter() {
        return wetter;
    }

    private void setLastAktuelleGenerelleTemperatur(
            @Nullable final Temperatur lastAktuelleGenerelleTemperatur) {
        if (this.lastAktuelleGenerelleTemperatur ==
                lastAktuelleGenerelleTemperatur) {
            return;
        }

        setChanged();

        this.lastAktuelleGenerelleTemperatur = lastAktuelleGenerelleTemperatur;
    }

    @Nullable
    Temperatur getLastAktuelleGenerelleTemperatur() {
        return lastAktuelleGenerelleTemperatur;
    }

    @Nullable
    PlanwetterData getPlan() {
        return plan;
    }

    private void setTimeLetzterBeschriebenerTageszeitensprungOderWechsel(
            @Nullable final AvDateTime timeLetzterBeschriebenerTageszeitensprungOderWechsel) {
        if (this.timeLetzterBeschriebenerTageszeitensprungOderWechsel ==
                timeLetzterBeschriebenerTageszeitensprungOderWechsel) {
            return;
        }

        setChanged();
        this.timeLetzterBeschriebenerTageszeitensprungOderWechsel
                = timeLetzterBeschriebenerTageszeitensprungOderWechsel;
    }

    /**
     * Nur für das ROOM-Framework.
     */
    @Nullable
    AvDateTime getTimeLetzterBeschriebenerTageszeitensprungOderWechsel() {
        return timeLetzterBeschriebenerTageszeitensprungOderWechsel;
    }

    /**
     * Vermerkt, dass gerade ein Wetterhinweis gegeben wird
     */
    private void saveWetterhinweisGegeben(final DrinnenDraussen drinnenDraussen) {
        if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
            setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
                    false);
            setWennWiederUnterOffenemHimmelWetterBeschreiben(false);
        } else if (drinnenDraussen.isDraussen()) {
            setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
                    false);
        }
    }

    private void
    setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
            final boolean
                    wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel
    ) {
        if (this.wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel
                ==
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

    boolean isWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel
            () {
        return wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel;
    }

    boolean isWennWiederUnterOffenemHimmelWetterBeschreiben() {
        return wennWiederUnterOffenemHimmelWetterBeschreiben;
    }
}
