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
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

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
    public ImmutableCollection<AbstractDescription<?>> onTimePassed(final AvDateTime startTime,
                                                                    @NonNull
                                                                    final AvDateTime endTime) {
        if (endTime.minus(startTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return ImmutableSet.of();
        }

        // Es gab also potenziell einen (oder mehrere) Tageszeitenwechsel oder Wetterwechsel
        // während einer Zeit von weniger als einem Tag
        return onTimePassed(startTime.getTageszeit(), endTime.getTageszeit());
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass innerhalb maximal eines Tages
     * dieser Tageszeitenwechsel geschehen ist - bei gleicher Tageszeit leer.
     */
    private ImmutableCollection<AbstractDescription<?>> onTimePassed(final Tageszeit lastTageszeit,
                                                                     final Tageszeit currentTageszeit) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder mehr als ein Tag, dann hat die Action
            // sicher ohnehin erzählt, was passiert ist.

            // FIXME Über den Tag verteilen: die Sonne (WetterComp!) steht schon hoch.. weit nach
            //  Mittag....
            //  Generell nicht nur an den "Tageszeitengrenzen" Texte erzeugen, sondern abhängig von
            //  der Uhrzeit?
            //  - Hinweise, dass die Nacht allmählich naht.
            return ImmutableSet.of();
        }

        switch (lastTageszeit) {
            case NACHTS:
                return altTimePassedFromNachtsTo(currentTageszeit).build();
            case MORGENS:
                return altTimePassedFromMorgensTo(currentTageszeit).build();
            case TAGSUEBER:
                return altTimePassedFromTagsueberTo(currentTageszeit).build();
            case ABENDS:
                return altTimePassedFromAbendsTo(currentTageszeit).build();
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }
    }

    private static AltDescriptionsBuilder altTimePassedFromNachtsTo(
            @NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case MORGENS:
                return alt().add(neuerSatz("Allmählich ist es Morgen geworden"
                        // Der Tageszeitenwechsel ist parallel passiert.
                        ),
                        neuerSatz("Der nächste Tag ist angebrochen"),
                        neuerSatz("Langsam graut der Morgen"),
                        neuerSatz("Langsam wird es hell"),
                        neuerSatz("Unterdessen ist es hell geworden"),
                        neuerSatz("Die Sonne geht auf")

                        // FIXME So etwas ermöglichen, wenn der Spieler sich
                        //  DRAUSSEN mit Blick auf den Himmel aufhält
                        //  allg("Im Osten kündigt sich der neue Tag an")
                        //  "Die Sterne verblassen und die Sonne ist am Horizont zu sehen"
                );
            case TAGSUEBER:
                return alt().add(
                        // FIXME Wenn draußen, dann je nach Wetter!
                        neuerSatz("Inzwischen ist es hellichter Tag"),
                        neuerSatz("Der andere Tag hat begonnen"),
                        neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen")
                );
            case ABENDS:
                return alt().add(
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        neuerSatz("Inzwischen wird es schon wieder dunkel"),
                        neuerSatz("Der Tag ist schon fast vorüber"),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen")
                );
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        // FIXME WetterData in allen Folgemethoden ab hier berücksichtigen, ggf. verschieben oder
        //  verallgemeinern (Bewölkung, Temperatur)

        // FIXME Je nach Ort unterscheiden:
        //  - SC ist draußen / drinnen (z.B. im Wald)
        //  - SC ist unter offener Himmel (z.B. vor dem Schloss)
        //  Merken, wann der Benutzer den jeweiligen Status schon aktualisiert bekommen
        //  hat („Du trittst aus dem Wald hinaus. Rotes Abendrot erstreckt sich über den
        //  Horizont....“)

        // FIXME Tageszeitenübergänge generell nur schreiben, wenn der SC wieder draußen ist?!
    }

    private static AltDescriptionsBuilder altTimePassedFromMorgensTo(
            @NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case TAGSUEBER:
                return alt().add(neuerSatz("Die Sonne steigt langsam am Firmament "
                                + "empor"),
                        neuerSatz("Die Sonne ist aufgegangen und beginnt ihren Lauf")
                );
            case ABENDS:
                return alt().add(
                        // FIXME Bewölkung etc. berücksichtigen
                        neuerSatz("Währenddessen ist der Tag vergangen und die Sonne steht "
                                + "schon tief am Himmel"),
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        neuerSatz("Inzwischen wird es schon wieder dunkel"),
                        neuerSatz("Der Tag ist schon fast vorüber"),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen")
                );
            case NACHTS:
                return alt().add(neuerSatz("Die Sonne ist über die Zeit untergegangen"),
                        neuerSatz("Jetzt ist es dunkel"),
                        neuerSatz("Jetzt ist es Nacht und man sieht nur noch wenig"),
                        neuerSatz("Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH, "Darüber ist es vollständig dunkel "
                                + "geworden. Nur noch "
                                + "die Sterne und der Mond spenden ein wenig Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                + "so gut"));
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private AltDescriptionsBuilder altTimePassedFromTagsueberTo(
            @NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case ABENDS:
                return alt().add(neuerSatz(PARAGRAPH, "Allmählich wird es abendlich dunkel"),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und langsam beginnt der Abend"),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und allmählich bricht "
                                + "der Abend an"),
                        neuerSatz(PARAGRAPH, "Die Abenddämmerung beginnt"),
                        neuerSatz(PARAGRAPH, "Inzwischen steht die Sonne schon tief")
                        // STORY Noch ein paar Texte für den Beginn des Abends
                        // TODO WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes Abendrot zu
                        //  sehen"
                );
            case NACHTS:
                return alt().add(neuerSatz(PARAGRAPH, "Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH, "Es ist dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Die Sonne ist untergegangen"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es Nacht und man sieht nicht "
                                + "mehr so gut"),
                        neuerSatz(PARAGRAPH, "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                + "die Sterne und der Mond geben etwas Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                + "so gut")
                );
            case MORGENS:
                return alt().add(neuerSatz(PARAGRAPH, "Unterdessen hat der neue Tag begonnen",
                        PARAGRAPH),
                        neuerSatz(PARAGRAPH, "Es ist schon der nächste Morgen"),
                        neuerSatz(PARAGRAPH, "Die Nacht ist vorbei und es wird schon "
                                + "wieder hell"),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder hell geworden"),
                        neuerSatz("Die Sonne geht gerade auf")
                );
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private static AltDescriptionsBuilder altTimePassedFromAbendsTo(
            @NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                return alt().add(neuerSatz("Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH,
                                "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist die Nacht hereingebrochen"),
                        neuerSatz(PARAGRAPH,
                                "Es ist Nacht geworden und man sieht nicht "
                                        + "mehr so gut")
                        // FIXME wenn der SC draußen ist:
                        //  "Jetzt sind am Himmel die Sterne zu sehen. Es ist dunkel und in der
                        //  Ferne "
                        //  + "ruft ein Käuzchen"
                );
            case MORGENS:
                return alt().add(neuerSatz("Unterdessen hat der neue Tag begonnen", PARAGRAPH),
                        neuerSatz("Es ist schon der nächste Morgen"),
                        neuerSatz("Die Nacht ist vorbei und es wird schon "
                                + "wieder hell"),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder "
                                + "hell geworden"),
                        neuerSatz("Die Sonne geht gerade auf")
                );
            case TAGSUEBER:
                return alt().add(neuerSatz("Es ist schon wieder heller Tag"),
                        neuerSatz("Die Sonne ist schon wieder aufgegangen")
                );
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }


    // FIXME Grundsätzlich könnte man sich die höchste und die niedrigste "heute" schon
    //  berichtete Temperatur merken. Ändert sich die diese Temperatur (z.B. der
    //  SC geht aus dem kühlen Schloss in die Hitze oder die Temperatur steigt draußen
    //  über den Tag), könnte es eine Ausgabe geben.

    // FIXME Manche Wetterphänomene und der "tageszeitliche Himmel"
    //  ("du siehst ein schönes Abendrot") sollten nur dann erzählt werden, wenn der SC
    //  "draußen" ist bzw. sogar "einen Blick auf den Himmel hat" (drinnenDraussenSc).

    // FIXME Man könnte, wenn der Benutzer erstmals wieder nach draußen kommt, etwas
    //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
    //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
    //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
    //  werden. Oder es müsste bei einer Änderung notiert werden, dass später noch eine
    //  Information zu erfolgen hat (ggf. noch einer Prüfung).

    // FIXME Verschiedene Fälle unterscheiden:
    //  -SC ist draußen und die Tageszeit hat zwischen startTime und endTime gewechselt:
    //   "Langsam wird es hell", "Die Sonne geht auf", "Unterdessen ist es hell geworden", ...
    //  - SC ist draußen und die Tageszeit hat VOR startTime gewechselt:
    //   "Inzwischen ist es hell geworden", "Unterdessen ist es hell geworden",
    //   "Die Sonne ist aufgegangen", "Draußen ist es inzwischen hell geworden"
    //  - SC ist drinnen ohne Sicht nach draußen:
    //   "Dein Gefühl sagt dir: Allmählich ist es Morgen geworden"
    //   "Wahrscheinlich ist schon der nächste Tag angebrochen"
    //   "Ob wohl schon die Sonne aufgegangen ist?"

    // FIXME Weitere Formulierungen für Veränderungen, die man miterlebt
    //  "der erste Strahl der aufgehenden Sonne dringt am Himmel herauf"
    //  "Die Sonne geht auf"
    //  "Der erste Sonnenstrahl bricht hervor"
    //  "Nun kommt die Sonne"
    //  "Die Sonne geht unter"
    //  "Du siehst du die Sonne (hinter den Bergen) aufsteigen"
    //  "die Sonne sinkt und die Nacht bricht ein"
    //  "die Nacht bricht ein"

    // FIXME Veränderungen der Temperatur
    //  "es kühlt (deutlich) ab" (Temperatur)

    // FIXME Veränderungen der Bewölkung
    //  es klart auf / der Himmel bedeckt sich/ bezieht sich (Bewölkung)
    //  "Der Mond geht auf" / "Der Mond steigt (über dem Berg) auf"
    //  "Der Mond kommt"

    // FIXME Weitere Formulierungen für Veränderungen, die man erst danach bemerkt
    //  "es bricht eben der erste Sonnenstrahl hervor"
    //  "in dem Augenblick dringt der erste Strahl der aufgehenden Sonne am Himmel herauf"
    //  "Die Sonne will eben untergehen"
    //  "Die Sonne ist untergegangen"
    //  "Nun ist die Sonne unter"
    //  "die Sonne ist hinter (den Bergen) verschwunden"
    //  "du kommst aus (der Finsternis) heraus in das Tageslicht"
    //  "die Sonne ist (hinter die Berge) gesunken"

    // FIXME Nachträglich bemerkte Veränderungen der Bewölkung
    //  "Der Mond ist aufgegangen", "Der Mond ist schon aufgestiegen"
    //  (MACHT NUR SINN, WENN ES EINE ÄNDERUNG GEGENÜBER
    //  DEM LETZTEN INFORMATIONSSTAND IST)

    // FIXME Kombination: "Es hat deutlich abgekühlt und der Himmel bezieht sich."

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
