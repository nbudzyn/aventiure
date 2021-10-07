package de.nb.aventiure2.data.world.syscomp.wetter;

import static de.nb.aventiure2.data.time.AvTimeSpan.span;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;

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
    private WetterData wetter;

    @Embedded(prefix = "currentStep")
    @Nullable
    private WetterStep currentWetterStep;

    @Nullable
    private Temperatur lastGenerelleTemperatur;

    @Nullable
    private Bewoelkung lastBewoelkung;

    @Nullable
    private Windstaerke lastWindstaerkeUnterOffenemHimmel;

    private AvDateTime timeLetzteBeschriebeneTageszeit;

    /**
     * Wenn die Veränderung eines Wetterparameters erzählt wurde, wird das hier "angekreuzt".
     * Das verhindert, dass unmittelbar nach einer Änderung ("der Wind wird stärker") derselbe
     * Wetterparameter noch einmal beschrieben wird ("der Wind ist stark").
     */
    @Embedded(prefix = "changeGeradeErzaehlt")
    private WetterParamFlags wetterChangeGeradeErzaehlt;

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
    private PlanwetterData plan;

    @Ignore
    WetterPCD(final GameObjectId gameObjectId,
              final WetterData wetter,
              final AvDateTime timeLetzteBeschriebeneTageszeit) {
        this(gameObjectId, wetter,
                null, null,
                null, null,
                timeLetzteBeschriebeneTageszeit,
                WetterParamFlags.keine(),
                true,
                true,
                null);
    }

    @SuppressWarnings("WeakerAccess")
    public WetterPCD(final GameObjectId gameObjectId,
                     final WetterData wetter,
                     @Nullable final WetterStep currentWetterStep,
                     @Nullable final Temperatur lastGenerelleTemperatur,
                     @Nullable final Bewoelkung lastBewoelkung,
                     @Nullable final Windstaerke lastWindstaerkeUnterOffenemHimmel,
                     final AvDateTime timeLetzteBeschriebeneTageszeit,
                     final WetterParamFlags wetterChangeGeradeErzaehlt,
                     final boolean wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel,
                     final boolean wennWiederUnterOffenemHimmelWetterBeschreiben,
                     @Nullable final PlanwetterData plan) {
        super(gameObjectId);
        this.wetter = wetter;
        this.currentWetterStep = currentWetterStep;
        this.lastGenerelleTemperatur = lastGenerelleTemperatur;
        this.lastBewoelkung = lastBewoelkung;
        this.lastWindstaerkeUnterOffenemHimmel = lastWindstaerkeUnterOffenemHimmel;
        this.timeLetzteBeschriebeneTageszeit =
                timeLetzteBeschriebeneTageszeit;
        this.wetterChangeGeradeErzaehlt = wetterChangeGeradeErzaehlt;
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
    ImmutableCollection<AbstractDescription<?>> altSpOnScEnter(
            final AvDateTime time,
            final DrinnenDraussen drinnenDraussenFrom,
            final DrinnenDraussen drinnenDraussenTo,
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

        final int deltaTemperatur = temperaturTo.minus(temperaturFrom);

        @Nullable final Windstaerke windstaerkeFrom =
                drinnenDraussenFrom.isDraussen() ?
                        getLokaleWindstaerkeDraussen(
                                drinnenDraussenFrom == DRAUSSEN_UNTER_OFFENEM_HIMMEL) :
                        null;
        @Nullable final Windstaerke windstaerkeTo =
                drinnenDraussenTo.isDraussen() ?
                        getLokaleWindstaerkeDraussen(
                                drinnenDraussenTo == DRAUSSEN_UNTER_OFFENEM_HIMMEL) :
                        null;

        final boolean temperaturAuffaelligAngenehmerInToAlsInFrom = Math.abs(deltaTemperatur) >= 2
                && (
                // Es ist wärmer als an der Location zu vor - aber nicht zu warm...
                (deltaTemperatur > 0 && temperaturTo.compareTo(Temperatur.WARM) <= 0)
                        // ...oder es ist kälter als an der Location zu vor - aber nicht zu kalt:
                        || (deltaTemperatur < 0
                        && temperaturTo.compareTo(Temperatur.KUEHL) >= 0));

        final boolean windverhaeltnisseAuffaelligAngenehmerInToAlsInFrom =
                windstaerkeFrom != null
                        && windstaerkeFrom.compareTo(Windstaerke.KRAEFTIGER_WIND) >= 0
                        && (windstaerkeTo == null || windstaerkeFrom.compareTo(windstaerkeTo) > 0);

        if (temperaturAuffaelligAngenehmerInToAlsInFrom
                || windverhaeltnisseAuffaelligAngenehmerInToAlsInFrom) {
            // Die Temperatur oder Windverhältnisse sind auffällig angenehmer als an der Location
            // zuvor.
            return wetter.altSpAngenehmereTemperaturOderWindAlsVorLocation(
                    time.getTime(), locationTemperaturRangeTo,
                    temperaturAuffaelligAngenehmerInToAlsInFrom ? deltaTemperatur : 0,
                    windverhaeltnisseAuffaelligAngenehmerInToAlsInFrom ?
                            windstaerkeFrom : null,
                    windverhaeltnisseAuffaelligAngenehmerInToAlsInFrom ? windstaerkeTo : null);
        }

        // Die Temperatur ist (wieder) unangenehmer als an der Location zuvor.
        final boolean temperaturAuffaelligUnangenehmerInToAlsInFrom =
                Math.abs(deltaTemperatur) >= 2
                        && !temperaturTo.isUnauffaellig(time.getTageszeit());

        if (temperaturAuffaelligUnangenehmerInToAlsInFrom) {
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
    ImmutableCollection<AbstractDescription<?>> altSpWetterhinweiseWennNoetig(
            final AvDateTime time, final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if ((drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                && wennWiederUnterOffenemHimmelWetterBeschreiben)
                || (drinnenDraussen.isDraussen()
                && wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel)) {

            return altSpWetterhinweise(time, drinnenDraussen, locationTemperaturRange);
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
    ImmutableCollection<AbstractDescription<?>> altSpWetterhinweise(
            final AvDateTime time,
            final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableList.of();
        }

        final ImmutableCollection<AbstractDescription<?>> alt =
                wetter.altSpWetterhinweise(time.getTime(),
                        drinnenDraussen,
                        locationTemperaturRange,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel,
                        wetterChangeGeradeErzaehlt);

        resetWetterChangeGeradeErzaehlt();

        if (!alt.isEmpty()) {
            saveWetterhinweisGegeben(drinnenDraussen);
        }

        return alt;
    }


    /**
     * Gibt alternative Sätze zum "Wetter" zurück, wie
     * man es drinnen oder draußen erlebt - oder eine leere Menge.
     * <p>
     * Sofern keine leere Menge zurückgegeben wurde, muss der  Aufrufer dafür sorgen, dass einer
     * dieser Wetterhinweise - oder ein Wetterhinweis
     * aus einer anderen <code>...Wetterhinweis...</code>-Methode auch ausgegeben wird!
     * Denn diese Methode vermerkt i.A., dass der Spieler über das aktuelle Wetter informiert wurde.
     */
    @CheckReturnValue
    ImmutableCollection<Satz> altSpWetterhinweisSaetze(
            final AvDateTime time,
            final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableList.of();
        }

        final ImmutableCollection<Satz> alt =
                wetter.altSpWetterhinweisSaetze(time.getTime(),
                        drinnenDraussen,
                        locationTemperaturRange,
                        wennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel,
                        wetterChangeGeradeErzaehlt,
                        nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete);

        resetWetterChangeGeradeErzaehlt();

        if (!alt.isEmpty()) {
            saveWetterhinweisGegeben(drinnenDraussen);
        }

        return alt;
    }

    /**
     * Gibt alternative Sätze zu Windgeräuschen zurück - kann leer sein.
     */
    ImmutableCollection<EinzelnerSatz> altSpWindgeraeuscheSaetze(
            final AvTime time, final boolean unterOffenemHimmel) {
        return wetter.altSpWindgeraeuscheSaetze(time, unterOffenemHimmel);
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
    ImmutableSet<AbstractDescription<?>> altSpWetterhinweiseKommtNachDraussen(
            final AvDateTime time,
            final boolean unterOffenenHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableSet.of();
        }

        final ImmutableSet<AbstractDescription<?>> alt = wetter.altSpKommtNachDraussen(
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
    ImmutableCollection<AbstractDescription<?>> altSpTimePassed(
            final Change<AvDateTime> change,
            @Nullable final ILocationGO location) {
        final DrinnenDraussen drinnenDraussen =
                location != null ? location.storingPlaceComp().getDrinnenDraussen() :
                        // SC hat die Welt verlassen? Dann hat er wohl keinen Blick auf den
                        // weltlichen
                        // Himmel.
                        DRINNEN;

        final EnumRange<Temperatur> locationTemperaturRange = getTemperaturRange(location);

        final Temperatur currentGenerelleTemperatur =
                getCurrentGenerelleTemperatur(change.getNachher());

        @Nullable final WetterParamChange<Windstaerke> windstaerkeChangeSofernRelevant =
                handleWindstaerkeChange(change, drinnenDraussen);

        @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant =
                handleTemperaturChange(change, drinnenDraussen, locationTemperaturRange,
                        currentGenerelleTemperatur);

        @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant =
                handleBewoelkungChange(change, drinnenDraussen);

        setGgfSpaeterWetterBeschreibenWegenWind(drinnenDraussen);

        final ImmutableCollection<AbstractDescription<?>> altSp = altSpTimePassed(
                change,
                !locationTemperaturRange.isInRange(currentGenerelleTemperatur),
                windstaerkeChangeSofernRelevant, temperaturChangeSofernRelevant,
                bewoelkungChangeSofernRelevant,
                drinnenDraussen);

        setLastGenerelleTemperatur(currentGenerelleTemperatur);
        setLastBewoelkung(wetter.getBewoelkung());
        setLastWindstaerkeUnterOffenemHimmel(wetter.getWindstaerkeUnterOffenemHimmel());

        setWetterChangeGeradeErzaehlt(
                new WetterParamFlags(temperaturChangeSofernRelevant != null,
                        windstaerkeChangeSofernRelevant != null,
                        bewoelkungChangeSofernRelevant != null,
                        // FIXME Blitz-und-Donner-Änderung erzählen. Oder alternativ es
                        //  immer mal wieder blitzen und donnern lassen.
                        false));

        return altSp;
    }

    private void setGgfSpaeterWetterBeschreibenWegenWind(final DrinnenDraussen drinnenDraussen) {
        // Selbst wenn es keine Veränderung gab, soll besonderer Wind
        // beschrieben werden, wenn der SC später nach draußen kommt oder einen
        // geschützten Bereich verlässt

        if (!drinnenDraussen.isDraussen()
                && !getLokaleWindstaerkeDraussen(true).isUnauffaellig()) {
            // Vermerken: Es soll einen Wetterhinweis geben, wenn der SC wieder
            // raus kommt. Auch "einmalige Erlebnisse nach Tageszeitenwechsel"
            // (erster Sonnenstrahl o.Ä.) sollen (einmalig :-) ) erzählt werden.
            setWennWiederDraussenWetterBeschreibenAuchEinmaligeErlebnisseNachTageszeitenwechsel(
                    true);
        }

        if (drinnenDraussen != DRAUSSEN_UNTER_OFFENEM_HIMMEL
                && getLokaleWindstaerkeDraussen(false) !=
                getLokaleWindstaerkeDraussen(true)) {
            // Der SC ist an einem windgeschützten Ort.
            // Wenn er dann wieder unter offenen Himmel kommt, soll der Wind-ohne-Schutz
            // beschrieben werden.
            setWennWiederUnterOffenemHimmelWetterBeschreiben(true);
        }
    }

    @Nullable
    private WetterParamChange<Temperatur> handleTemperaturChange(
            final Change<AvDateTime> timeChange,
            final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange,
            final Temperatur currentGenerelleTemperatur) {
        if ( // Die generelle aktuelle Temperatur hat sich gerade gar nicht geändert...
                lastGenerelleTemperatur == null
                        || currentGenerelleTemperatur == lastGenerelleTemperatur
                        // ...oder die Temperatur ist und war unauffällig...
                        || (
                        lastGenerelleTemperatur
                                .isUnauffaellig(timeChange.getVorher().getTageszeit())
                                && currentGenerelleTemperatur
                                .isUnauffaellig(timeChange.getNachher().getTageszeit()))) {
            return null;
        }

        final Temperatur lastLokaleTemperatur =
                locationTemperaturRange.clamp(lastGenerelleTemperatur);
        final Temperatur currentLokaleTemperatur =
                locationTemperaturRange.clamp(currentGenerelleTemperatur);
        if ( // Der SC hat die Veränderung auch merken können...
                lastLokaleTemperatur != currentLokaleTemperatur
                        // ...und sie passiert über eine eher kurze Zeit
                        && span(timeChange).shorterThan(AvTimeSpan.hours(2))) {
            return new WetterParamChange<>(lastLokaleTemperatur, currentLokaleTemperatur);
        }

        // Der SC hat die Veränderung nicht merken können (er war gerade an einem
        // kühlen Ort, im beheizten Schloss o.Ä.) - oder die Temperaturänderung passierte
        // über lange Zeit (wir wollen ein "auf einmal ist es eiskalt geworden"
        // vermeiden, wenn es in Wirklichkeit über 5 Stunden allmählich kühler und
        // kühler geworden ist). Dann geben wir einfach später mal einen Wetterhinweis.
        setGgfSpaeterWetterBeschreiben(drinnenDraussen);
        // IDEA Man könnte auch *später* etwas schreiben wie "Draußen hat sich das Wetter
        //  verändert. Es hat deutlich abgekühlt.", "Draußen hat sich das Wetter
        //  verändert. Es hat deutlich abgekühlt und der Himmel bezieht sich." oder
        //  "Inzwischen ist es ziemlich warm / ziemlich kalt, ..." Das alles
        //  ergibt aber nur Sinn, wenn man weiß, welche (Temperatur, ...)-Werte der Benutzer
        //  an diesem Ort tatsächlich erwartet hätte. Da jeder Ort seine eigenen
        //  Minimal- / Maximaltemperatur hat, ist das (für den allgemeinen Fall)
        //  etwas mühevoll.

        return null;
    }

    @Nullable
    private WetterParamChange<Bewoelkung> handleBewoelkungChange(
            final Change<AvDateTime> timeChange,
            final DrinnenDraussen drinnenDraussen) {
        final Bewoelkung currentBewoelkung = wetter.getBewoelkung();

        if ( // Die Bewölkung hat sich gerade gar nicht geändert...
                lastBewoelkung == null
                        || currentBewoelkung == lastBewoelkung
                        // ...oder die Bewoelkung ist und war unauffällig...
                        || (
                        lastBewoelkung.isUnauffaellig(timeChange.getVorher().getTageszeit())
                                && currentBewoelkung
                                .isUnauffaellig(timeChange.getNachher().getTageszeit()))) {
            return null;
        }

        if ( // Der SC hat die Veränderung auch merken können...
                drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                        // ...und sie passiert über eine eher kurze Zeit
                        && span(timeChange).shorterThan(AvTimeSpan.hours(2))) {
            return new WetterParamChange<>(lastBewoelkung, currentBewoelkung);
        }

        if ( // Es gab eine kleine Veränderung, die der SC kaum mitbekommen hat...
                drinnenDraussen.isDraussen()
                        && (lastBewoelkung == Bewoelkung.BEDECKT
                        || currentBewoelkung == Bewoelkung.BEDECKT)
                        // ...und sie passiert über eine eher kurze Zeit
                        && span(timeChange).shorterThan(AvTimeSpan.hours(2))) {
            // Wir geben später einen Wetterhinweis
            setWennWiederUnterOffenemHimmelWetterBeschreiben(true);

            return new WetterParamChange<>(lastBewoelkung, currentBewoelkung);
        }

        // Der SC hat die Veränderung kaum merken können (er hatte gar keinen Blick auf den
        // offenen Himmel) - oder die Bewölkungsänderung passierte
        // über lange Zeit (wir wollen ein "auf einmal ist es bedeckt"
        // vermeiden, wenn es sich in Wirklichkeit über 5 Stunden allmählich immer mehr und mehr
        // bezogen hat). Dann geben wir einfach später mal einen Wetterhinweis.
        setGgfSpaeterWetterBeschreiben(drinnenDraussen);
        // IDEA Man könnte auch *später* etwas schreiben wie "Draußen hat sich das Wetter
        //  verändert. Es hat deutlich abgekühlt und der Himmel bezieht sich. Dazu müsste man
        //  aber eigentlich wissen, welche Bewölkung der SC als letztes unter freiem Himmel
        //  erlebt hat.

        return null;
    }

    @Nullable
    private WetterParamChange<Windstaerke> handleWindstaerkeChange(
            final Change<AvDateTime> timeChange,
            final DrinnenDraussen drinnenDraussen) {
        final Windstaerke currentWindstaerkeUnterOffenemHimmel =
                wetter.getWindstaerkeUnterOffenemHimmel();

        if ( // Die Windstärke unter offenem Himmel hat sich gerade gar nicht geändert...
                lastWindstaerkeUnterOffenemHimmel == null
                        || currentWindstaerkeUnterOffenemHimmel == lastWindstaerkeUnterOffenemHimmel
                        // ...oder die Windstärke ist und war unauffällig...
                        || (
                        lastWindstaerkeUnterOffenemHimmel.isUnauffaellig()
                                && currentWindstaerkeUnterOffenemHimmel.isUnauffaellig())) {
            return null;
        }

        if (drinnenDraussen.isDraussen()) {
            // Der SC hat die Veränderung vielleicht merken können, jedenfalls ist er draußen...

            final Windstaerke lastLokaleWindstaerke =
                    lastWindstaerkeUnterOffenemHimmel.getLokaleWindstaerkeDraussen(
                            drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL);

            final Windstaerke currentLokaleWindstaerke =
                    currentWindstaerkeUnterOffenemHimmel.getLokaleWindstaerkeDraussen(
                            drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL);

            if ( // Der SC hat die Veränderung auch merken können...
                    lastLokaleWindstaerke != currentLokaleWindstaerke
                            // ...und sie passiert über eine eher kurze Zeit
                            && span(timeChange).shorterThan(AvTimeSpan.hours(2))) {
                return new WetterParamChange<>(lastLokaleWindstaerke, currentLokaleWindstaerke);
            }
        }

        // Der SC hat die Veränderung nicht merken können (er war gerade drinnen
        // oder an einem wingeschützten Ort) - oder die Windveränderung passierte
        // über lange Zeit (wir wollen ein "auf einmal ist ein Sturm hereingebrochen"
        // vermeiden, wenn es in Wirklichkeit über 5 Stunden allmählich immer windiger
        // geworden ist). Dann geben wir einfach später mal einen Wetterhinweis.
        setGgfSpaeterWetterBeschreiben(drinnenDraussen);
        // IDEA Man könnte auch *später* etwas schreiben wie "Draußen hat sich das Wetter
        //  verändert. Ein Sturm ist hereingebrochen.", "Draußen hat sich das Wetter
        //  verändert. Der Sturm ist vorbei" Das  ergibt aber nur Sinn, wenn man weiß,
        //  welche (Windstärke, ...)-Werte der Benutzer
        //  an diesem Ort tatsächlich erwartet hätte. Da manche Orte geschützt sind, andere
        //  nicht, könnte das (für den allgemeinen Fall) etwas mühevoll sein.

        return null;
    }


    private static EnumRange<Temperatur> getTemperaturRange(@Nullable final ILocationGO location) {
        return location != null ?
                location.storingPlaceComp().getEffectiveTemperaturRange() :
                EnumRange.all(Temperatur.class);
    }


    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer.
     * <p>
     * Wenn es hier Beschreibungen gab und keine von ihnen ausgegeben wird, wird der
     * Tageszeitensprung oder -wechsel nicht mehr beschrieben werden.
     *
     * @param windstaerkeChangeSofernRelevant Die Änderung der Windstärke, falls eine beschrieben
     *                                        werden soll, sonst {@code null}
     * @param temperaturChangeSofernRelevant  Die  Temperaturänderung, falls eine       beschrieben
     *                                        werden soll, sonst {@code null}
     * @param bewoelkungChangeSofernRelevant  Die Änderung der Bewölkung, falls eine beschrieben
     *                                        werden soll, sonst {@code null}
     */
    @NonNull
    private ImmutableCollection<AbstractDescription<?>> altSpTimePassed(
            final Change<AvDateTime> change,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final WetterParamChange<Windstaerke> windstaerkeChangeSofernRelevant,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen) {
        final boolean tageszeitaenderungSollBeschriebenWerden =
                !span(change).longerThanOrEqual(AvTimeSpan.ONE_DAY) &&
                        change.getVorher().getTageszeit() != change.getNachher().getTageszeit();

        final ImmutableCollection<AbstractDescription<?>> altSp =
                wetter.altSpTimePassed(change,
                        tageszeitaenderungSollBeschriebenWerden,
                        generelleTemperaturOutsideLocationTemperaturRange,
                        windstaerkeChangeSofernRelevant, temperaturChangeSofernRelevant,
                        bewoelkungChangeSofernRelevant,
                        drinnenDraussen
                );

        if (tageszeitaenderungSollBeschriebenWerden) {
            if (change.getNachher().isAfter(timeLetzteBeschriebeneTageszeit)) {
                setTimeLetzteBeschriebeneTageszeit(change.getNachher());
                // Damit befinden wir uns nicht mehr im "schwebenden Tageszeitenwechsel",
                // sondern können uns bei weiteren Ausgaben auf die aktuelle Tageszeit beziehen.
            }

            setGgfSpaeterWetterBeschreiben(drinnenDraussen);
        }

        return altSp;
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
    altSpDescUeberHeuteOderDenTagWennDraussenSinnvoll(
            final AvTime time,
            final boolean unterOffenemHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
            // Schwebender Tageszeitenwechsel - keinen Wetterhinweis geben!
            // (Sonst könnte es zu etwas kommen wie "Im Morgenlicht siehst du...
            //  Langsam geht die Nacht in den Morgen über." - also erst der
            //  Zustandsbeschreibung,
            //  dann der Beschreibung des Übergangs.)
            return ImmutableSet.of();
        }

        return wetter.altSpHeuteDerTagWennDraussenSinnvoll(
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
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
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
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
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
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
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
        if (timeLetzteBeschriebeneTageszeit.getTageszeit() != time.getTageszeit()) {
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


    /**
     * Gibt die "lokale Windstärke" zurück - drinnen {@link Windstaerke#WINDSTILL}.
     */
    Windstaerke getLokaleWindstaerke(final DrinnenDraussen drinnenDraussen) {
        if (!drinnenDraussen.isDraussen()) {
            return Windstaerke.WINDSTILL;
        }

        return getLokaleWindstaerkeDraussen(
                drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL);
    }

    @NonNull
    private Windstaerke getLokaleWindstaerkeDraussen(final boolean unterOffenemHimmel) {
        // Nur weil die Windstärke abgefragt wird, gehen wir nicht davon aus, dass ein
        // "qualifizierter" Wetterhinweis gegeben wurde

        return wetter.windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenemHimmel);
    }

    @NonNull
    private Temperatur getCurrentGenerelleTemperatur(final AvDateTime time) {
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

    void setWetter(final WetterData wetter) {
        if (this.wetter.equals(wetter)) {
            return;
        }

        setChanged();
        this.wetter = wetter;
    }


    void updateCurrentWetterStep(@Nullable final WetterStep wetterStep) {
        if (currentWetterStep == null && wetterStep == null) {
            // Weiterhin keine Wetterparameteränderung gewünscht
            return;
        }

        if (currentWetterStep != null && wetterStep != null
                && currentWetterStep.getWetterTo().equals(wetterStep.getWetterTo())
                && currentWetterStep.getExpDoneTime()
                .isEqualOrBefore(wetterStep.getExpDoneTime())) {
            // Gewünschte Wetterparameteränderung ist bereits in Arbeit und wird auch
            // rechtzeitig (oder sogar früher) fertig
            return;
        }

        setChanged();
        currentWetterStep = wetterStep;
    }

    @Nullable
    WetterStep getCurrentWetterStep() {
        return currentWetterStep;
    }

    private void setLastGenerelleTemperatur(@Nullable final Temperatur lastGenerelleTemperatur) {
        if (this.lastGenerelleTemperatur == lastGenerelleTemperatur) {
            return;
        }

        setChanged();

        this.lastGenerelleTemperatur = lastGenerelleTemperatur;
    }

    @Nullable
    Temperatur getLastGenerelleTemperatur() {
        return lastGenerelleTemperatur;
    }

    private void setLastBewoelkung(@Nullable final Bewoelkung lastBewoelkung) {
        if (this.lastBewoelkung == lastBewoelkung) {
            return;
        }

        setChanged();

        this.lastBewoelkung = lastBewoelkung;
    }

    @Nullable
    Bewoelkung getLastBewoelkung() {
        return lastBewoelkung;
    }

    private void setLastWindstaerkeUnterOffenemHimmel(
            @Nullable final Windstaerke lastWindstaerkeUnterOffenemHimmel) {
        if (this.lastWindstaerkeUnterOffenemHimmel == lastWindstaerkeUnterOffenemHimmel) {
            return;
        }

        setChanged();

        this.lastWindstaerkeUnterOffenemHimmel = lastWindstaerkeUnterOffenemHimmel;
    }

    @Nullable
    Windstaerke getLastWindstaerkeUnterOffenemHimmel() {
        return lastWindstaerkeUnterOffenemHimmel;
    }

    @Nullable
    PlanwetterData getPlan() {
        return plan;
    }

    void setPlanwetter(@Nullable final PlanwetterData planwetterData) {
        if (Objects.equal(plan, planwetterData)) {
            return;
        }

        if (plan == null && getWetter().equals(planwetterData.getWetter())) {
            // Das Wetter ist eh schon das Planwetter - und es ist auch kein
            // abweichender Plan gespeichert.
            return;
        }

        setChanged();
        plan = planwetterData;

        if (plan == null) {
            updateCurrentWetterStep(null);
        }
    }

    private void setTimeLetzteBeschriebeneTageszeit(
            final AvDateTime timeLetzteBeschriebeneTageszeit) {
        if (this.timeLetzteBeschriebeneTageszeit.equals(timeLetzteBeschriebeneTageszeit)) {
            return;
        }

        setChanged();
        this.timeLetzteBeschriebeneTageszeit = timeLetzteBeschriebeneTageszeit;
    }

    /**
     * Nur für das ROOM-Framework.
     */
    AvDateTime getTimeLetzteBeschriebeneTageszeit() {
        return timeLetzteBeschriebeneTageszeit;
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

    private void setWetterChangeGeradeErzaehlt(
            final WetterParamFlags wetterChangeGeradeErzaehlt) {
        if (this.wetterChangeGeradeErzaehlt.equals(wetterChangeGeradeErzaehlt)) {
            return;
        }

        setChanged();

        this.wetterChangeGeradeErzaehlt = wetterChangeGeradeErzaehlt;
    }

    private void resetWetterChangeGeradeErzaehlt() {
        setWetterChangeGeradeErzaehlt(WetterParamFlags.keine());
    }

    WetterParamFlags getWetterChangeGeradeErzaehlt() {
        return wetterChangeGeradeErzaehlt;
    }
}
