package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Wetter
 */
public class WetterComp extends AbstractStatefulComponent<WetterPCD> {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    protected final Narrator n;
    private final World world;

    public WetterComp(final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
                      final World world) {
        super(WETTER, db.wetterDao());
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    @Override
    protected WetterPCD createInitialState() {
        final WetterData wetterData =
                new WetterData(
                        Temperatur.RECHT_HEISS, Temperatur.KUEHL,
                        Windstaerke.WINDSTILL,
                        Bewoelkung.WOLKENLOS,
                        BlitzUndDonner.KEIN_BLITZ_ODER_DONNER);
        return new WetterPCD(WETTER, wetterData);
    }

    @NonNull
    public ImmutableSet<AbstractDescription<?>> altScKommtNachDraussenInsWetter(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        return requirePcd()
                .altScKommtNachDraussenInsWetter(
                        timeTaker.now().getTime(), lichtverhaeltnisseDraussen,
                        isScUnterOffenemHimmel())
                .build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    public ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll() {
        if (!isScDraussen()) {
            return ImmutableList.of();
        }

        return requirePcd()
                .altDescUeberHeuteOderDenTagWennDraussenSinnvoll(timeTaker.now().getTime(),
                        isScUnterOffenemHimmel());
    }

    @NonNull
    public ImmutableSet<String> altWetterplauderrede() {
        return requirePcd().altWetterplauderrede(timeTaker.now().getTime());
    }

    /**
     * Gibt alternative Beschreibungen zurück in der Art "in den Sonnenschein" o.Ä., die mit
     * "hinaus" verknüpft werden können.
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        return requirePcd().altWohinHinaus(timeTaker.now().getTime(), lichtverhaeltnisseDraussen,
                isScUnterOffenemHimmel());
    }

    public ImmutableCollection<Praepositionalphrase> altUnterOffenemHimmel() {
        return requirePcd().altUnterOffenemHimmel(timeTaker.now().getTime());
    }

    public ImmutableSet<Praepositionalphrase> altBeiLichtImLicht() {
        return requirePcd().altBeiLichtImLicht(timeTaker.now().getTime(), isScUnterOffenemHimmel());
    }

    public ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht() {
        return requirePcd().altBeiTageslichtImLicht(timeTaker.now().getTime(),
                isScUnterOffenemHimmel());
    }

    public ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt() {
        return requirePcd().altLichtInDemEtwasLiegt(timeTaker.now().getTime(),
                isScUnterOffenemHimmel());
    }

    public void onTimePassed(final AvDateTime startTime, @NonNull final AvDateTime endTime) {
        if (endTime.minus(startTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return;
        }

        // Es gab also potenziell einen (oder mehrere) Tageszeitenwechsel oder Wetterwechsel
        // während einer Zeit von weniger als einem Tag
        onTimePassed(startTime.getTageszeit(), endTime.getTageszeit());
    }

    private void onTimePassed(final Tageszeit lastTageszeit,
                              final Tageszeit currentTageszeit) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder mehr als ein Tag, dann hat die Action
            // sicher ohnehin erzählt, was passiert ist.

            // FIXME Über den Tag verteilen: die Sonne (WetterComp!) steht schon hoch.. weit nach
            //  Mittag....
            //  Generell nicht nur an den "Tageszeitengrenzen" Texte erzeugen, sondern abhängig von
            //  der Uhrzeit?
            //  - Hinweise, dass die Nacht allmählich naht.
            return;
        }

        final DrinnenDraussen drinnenDraussenSc = loadScDrinnenDraussen();

        // FIXME Grundsätzlich könnte man sich die höchste und die niedrigste "heute" schon
        //  berichtete Temperatur merken. Ändert sich die diese Temperatur (z.B. der
        //  SC geht aus dem kühlen Schloss in die Hitze oder die Temperatur steigt draußen
        //  über den Tag). Könnte es eine Ausgabe geben.

        // FIXME Manche Wetterphänomene und der "tageszeitliche Himmel"
        //  ("du siehst ein schönes Abendrot") sollten nur dann erzählt werden, wenn der SC
        //  "draußen" ist bzw. sogar "einen Blick auf den Himmel hat" (drinnenDraussenSc).

        // FIXME Man könnte, wenn der Benutzer erstmals wieder nach draußen kommt, etwas
        //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
        //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
        //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
        //  werden.

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
        switch (lastTageszeit) {
            case NACHTS:
                onTimePassedFromNachtsTo(currentTageszeit);
                return;
            case MORGENS:
                onTimePassedFromMorgensTo(currentTageszeit);
                return;
            case TAGSUEBER:
                onTimePassedFromTagsueberTo(currentTageszeit);
                return;
            case ABENDS:
                onTimePassedFromAbendsTo(currentTageszeit);
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }
    }


    private void onTimePassedFromNachtsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case MORGENS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Allmählich ist es Morgen geworden"
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
                return;
            case TAGSUEBER:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        // FIXME Wenn draußen, dann je nach Wetter!
                        neuerSatz("Inzwischen ist es hellichter Tag"),
                        neuerSatz("Der andere Tag hat begonnen"),
                        neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen")
                );
                return;
            case ABENDS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        neuerSatz("Inzwischen wird es schon wieder dunkel"),
                        neuerSatz("Der Tag ist schon fast vorüber"),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen")
                );
                return;
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

    private void onTimePassedFromMorgensTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case TAGSUEBER:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Die Sonne steigt langsam am Firmament "
                                + "empor"),
                        neuerSatz("Die Sonne ist aufgegangen und beginnt ihren Lauf")
                );
                return;
            case ABENDS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        // FIXME Nach WetterComp zentralisieren, Bewölkung etc. berücksichtigen
                        neuerSatz("Währenddessen ist der Tag vergangen und die Sonne steht "
                                + "schon tief am Himmel"),
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        neuerSatz("Inzwischen wird es schon wieder dunkel"),
                        neuerSatz("Der Tag ist schon fast vorüber"),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen")
                );
                return;
            case NACHTS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Die Sonne ist über die Zeit untergegangen"),
                        neuerSatz("Jetzt ist es dunkel"),
                        neuerSatz("Jetzt ist es Nacht und man sieht nur noch wenig"),
                        neuerSatz("Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH, "Darüber ist es vollständig dunkel "
                                + "geworden. Nur noch "
                                + "die Sterne und der Mond spenden ein wenig Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                + "so gut"));
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromTagsueberTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case ABENDS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz(PARAGRAPH, "Allmählich wird es abendlich dunkel"),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und langsam beginnt der Abend"),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und allmählich bricht "
                                + "der Abend an"),
                        neuerSatz(PARAGRAPH, "Die Abenddämmerung beginnt"),
                        neuerSatz(PARAGRAPH, "Inzwischen steht die Sonne schon tief")
                        // STORY Noch ein paar Texte für den Beginn des Abends
                        // TODO WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes Abendrot zu
                        //  sehen"
                );
                return;
            case NACHTS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz(PARAGRAPH, "Die Sonne ist jetzt untergegangen"),
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
                return;
            case MORGENS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz(PARAGRAPH, "Unterdessen hat der neue Tag begonnen",
                                PARAGRAPH),
                        neuerSatz(PARAGRAPH, "Es ist schon der nächste Morgen"),
                        neuerSatz(PARAGRAPH, "Die Nacht ist vorbei und es wird schon "
                                + "wieder hell"),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder hell geworden"),
                        neuerSatz("Die Sonne geht gerade auf")
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromAbendsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Die Sonne ist jetzt untergegangen"),
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
                return;
            case MORGENS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Unterdessen hat der neue Tag begonnen", PARAGRAPH),
                        neuerSatz("Es ist schon der nächste Morgen"),
                        neuerSatz("Die Nacht ist vorbei und es wird schon "
                                + "wieder hell"),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder "
                                + "hell geworden"),
                        neuerSatz("Die Sonne geht gerade auf")
                );
                return;
            case TAGSUEBER:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Es ist schon wieder heller Tag"),
                        neuerSatz("Die Sonne ist schon wieder aufgegangen")
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    /**
     * Gibt alternative Sätze <i>nur zur Temperatur</i> zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen.
     */
    @NonNull
    public Temperatur getTemperatur() {
        return requirePcd().getTemperatur(timeTaker.now().getTime());
    }

    private boolean isScUnterOffenemHimmel() {
        return loadScDrinnenDraussen() == DRAUSSEN_UNTER_OFFENEM_HIMMEL;
    }

    private boolean isScDraussen() {
        return loadScDrinnenDraussen().isDraussen();
    }

    private DrinnenDraussen loadScDrinnenDraussen() {
        return world.loadSC().locationComp().getLocation().storingPlaceComp().getDrinnenDraussen();
    }
}
