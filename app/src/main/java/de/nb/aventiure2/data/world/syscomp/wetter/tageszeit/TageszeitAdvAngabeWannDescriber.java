package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Konditionalsatz;

import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABEND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTERNACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.praedikat.Modalverb.WOLLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANBRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EINBRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERANKOMMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.KOMMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.UNTERGEHEN;

/**
 * Erzeugt tageszeitliche oder untertageszeitliche temporale Beschreibungen als
 * adverbiale Angaben.
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class TageszeitAdvAngabeWannDescriber {
    /**
     * Erzeugt alternative eine tageszeitliche oder untertageszeitliche temporale Beschreibungen als
     * adverbiale Angaben (vorausgesetzt der SC ist draußen) - oft leer.
     *
     * @param timeVorher Zeitpunkt <i>vor</i> dieser Tageszeit oder untertageszeitlichen Angabe
     * @param time       Zeitpunkt
     */
    // FIXME Einbinden, wenn SC hungrig wird und die Aktion einige Zeit gedauert hat?
    //  (Dann dafür sorgen, dass nicht derselbe Zeitübergang noch einmal z.B. bei altTimePassed()
    //  verwendet wird!).

    // FIXME Einbinden, wenn SC müder wird und die Aktion einige Zeit gedauert hat? (Dann dafür
    //  sorgen, dass nicht derselbe Zeitübergang noch einmal bei z.B. altTimePassed() verwendet
    //  wird!).

    // FIXME Automatisch bei Sätzen einbinden, wenn die Aktion einige Zeit gedauert hat? (Dann
    //  dafür
    //  sorgen, dass nicht derselbe Zeitübergang noch einmal bei z.B. altTimePassed() verwendet
    //  wird!).
    public ImmutableSet<AdvAngabeSkopusSatz> altWannDraussen(final AvTime timeVorher,
                                                             final AvTime time) {
        // Untertageszeitliche Angaben (werden bevorzugt)
        if (!timeVorher.kurzVorSonnenaufgang() && time.kurzVorSonnenuntergang()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("vor Sonnenaufgang"),
                    new AdvAngabeSkopusSatz("vor Tagesanbruch"),
                    new AdvAngabeSkopusSatz("früh vor Tag"));
        }

        if (!timeVorher.kurzNachSonnenaufgang() && time.kurzNachSonnenuntergang()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("nach Sonnenaufgang"),
                    new AdvAngabeSkopusSatz("frühmorgens"),
                    new AdvAngabeSkopusSatz("bei Anbruch des Tages"),
                    new AdvAngabeSkopusSatz("mit Anbruch des Tages"),
                    new AdvAngabeSkopusSatz("bei Sonnenaufgang"),
                    new AdvAngabeSkopusSatz("in aller Frühe"),
                    new AdvAngabeSkopusSatz("am frühen Morgen"),
                    new AdvAngabeSkopusSatz("am frühsten Morgen"),
                    new AdvAngabeSkopusSatz("mit dem frühsten Morgen"),
                    new AdvAngabeSkopusSatz("am Morgen in der Frühe"),
                    new AdvAngabeSkopusSatz("morgens in aller Frühe"),
                    new AdvAngabeSkopusSatz("des Morgens früh"));
        }

        if (!timeVorher.vorMittag() && time.vorMittag()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("vor Mittag"));
        }

        if (!timeVorher.gegenMittag() && time.gegenMittag()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("gegen Mittag"),
                    new AdvAngabeSkopusSatz("zu Mittag"),
                    new AdvAngabeSkopusSatz("mittags"),
                    new AdvAngabeSkopusSatz("um Mittagszeit"),
                    new AdvAngabeSkopusSatz("zur Mittagszeit"));
        }

        if (!timeVorher.nachmittags() && time.nachmittags()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("nachmittags"));
        }

        if (!timeVorher.kurzVorSonnenuntergang() && time.kurzVorSonnenuntergang()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("vor Sonnenuntergang"),
                    new AdvAngabeSkopusSatz("vor Einbruch der Nacht"));
        }

        if (!timeVorher.kurzNachSonnenuntergang() && time.kurzNachSonnenuntergang()) {
            return ImmutableSet.of(
                    new AdvAngabeSkopusSatz("bei Sonnenuntergang"),
                    new AdvAngabeSkopusSatz("nach Sonnenuntergang"),
                    new AdvAngabeSkopusSatz("bei einbrechender Nacht"));
        }

        if (!timeVorher.spaetInDerNacht() && time.spaetInDerNacht()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("spät in der Nacht"));
        }

        if (!timeVorher.umMitternacht() && time.umMitternacht()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("um Mitternacht"),
                    new AdvAngabeSkopusSatz("gegen Mitternacht"));
            // IDEA "Geisterstunde"?
        }

        if (!timeVorher.mittenInDerNacht() && time.mittenInDerNacht()) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("mitten in der Nacht"));
        }

        // Tageszeiten
        if (timeVorher.getTageszeit() != Tageszeit.MORGENS
                && time.getTageszeit() == Tageszeit.MORGENS) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("am andern Morgen"),
                    new AdvAngabeSkopusSatz("am Morgen"),
                    new AdvAngabeSkopusSatz("morgens"),
                    new AdvAngabeSkopusSatz("am nächsten Morgen"));
        }

        if (timeVorher.getTageszeit() != Tageszeit.ABENDS
                && time.getTageszeit() == Tageszeit.ABENDS) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("abends"),
                    new AdvAngabeSkopusSatz("am Abend"),
                    new AdvAngabeSkopusSatz("gegen Abend"),
                    new AdvAngabeSkopusSatz("zur Abendzeit"));
        }

        if (timeVorher.getTageszeit() != Tageszeit.NACHTS
                && time.getTageszeit() == Tageszeit.NACHTS) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("nachts"),
                    new AdvAngabeSkopusSatz("in der Nacht"));
        }

        return ImmutableSet.of();
    }

    /**
     * Erzeugt alternative Konditionalsätze, mit einer tageszeitliche oder untertageszeitliche
     * temporalen Beschreibungen (vorausgesetzt der SC ist draußen), z.B. "als die Mitternacht
     * kommt"  - oft leer.
     *
     * @param timeVorher Zeitpunkt <i>vor</i> dieser Tageszeit oder untertageszeitlichen Angabe
     * @param time       Zeitpunkt
     */
    // FIXME Einbinden, wenn SC hungrig wird und die Aktion einige Zeit gedauert hat?
    //  (Dann dafür sorgen, dass nicht derselbe Zeitübergang noch einmal z.B. bei altTimePassed()
    //  verwendet wird!).

    // FIXME Einbinden, wenn SC müder wird und die Aktion einige Zeit gedauert hat? (Dann dafür
    //  sorgen, dass nicht derselbe Zeitübergang noch einmal bei z.B. altTimePassed() verwendet
    //  wird!).

    // FIXME Automatisch bei Sätzen einbinden, wenn die Aktion einige Zeit gedauert hat? (Dann
    //  dafür
    //  sorgen, dass nicht derselbe Zeitübergang noch einmal bei z.B. altTimePassed() verwendet
    //  wird!).
    public ImmutableSet<Konditionalsatz> altWannKonditionalsaetzeDraussen(final AvTime timeVorher,
                                                                          final AvTime time) {
        // Untertageszeitliche Angaben (werden bevorzugt)
        if (!timeVorher.kurzVorSonnenaufgang() && time.kurzVorSonnenuntergang()) {
            return ImmutableSet.of(
                    // "noch ehe die Sonne aufgegangen ist
                    new Konditionalsatz(
                            "noch ehe", AUFGEHEN.alsSatzMitSubjekt(SONNE).perfekt()),
                    // "bevor der Tag anbricht
                    new Konditionalsatz(
                            "bevor", ANBRECHEN.alsSatzMitSubjekt(TAG)));
        }

        if (!timeVorher.kurzNachSonnenaufgang() && time.kurzNachSonnenuntergang()) {
            return ImmutableSet.of(
                    // "als die Sonne aufgeht
                    new Konditionalsatz(
                            "als", AUFGEHEN.alsSatzMitSubjekt(SONNE)),
                    // "als der Tag angebrochen ist
                    new Konditionalsatz(
                            "als", ANBRECHEN.alsSatzMitSubjekt(TAG).perfekt()));
        }

        if (!timeVorher.vorMittag() && time.vorMittag()) {
            return ImmutableSet.of(
                    // "noch ehe es Mittag ist"
                    new Konditionalsatz("noch ehe", npArtikellos(MITTAG).alsEsIstSatz()),
                    // "als es bald Mittag werden will"
                    new Konditionalsatz("als",
                            WOLLEN.mitLexikalischemKern(npArtikellos(MITTAG)
                                    .alsWerdenPraedikativumPraedikat()
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("bald")))
                                    .alsSatzMitSubjekt(EXPLETIVES_ES)));
        }

        if (!timeVorher.gegenMittag() && time.gegenMittag()) {
            return ImmutableSet.of(
                    // "als es Mittag ist"
                    new Konditionalsatz(
                            "als", npArtikellos(MITTAG).alsEsIstSatz()));
        }

        // IDEA: timeVorher.nachmittags()
        // IDEA: Als der Abend herankommt

        if (!timeVorher.kurzVorSonnenuntergang() && time.kurzVorSonnenuntergang()) {
            return ImmutableSet.of(
                    // "als die Sonne bald untergehen will"
                    new Konditionalsatz(
                            "als",
                            WOLLEN.mitLexikalischemKern(UNTERGEHEN)
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("bald"))
                                    .alsSatzMitSubjekt(SONNE)),
                    // "ehe es Nacht wird"
                    new Konditionalsatz("ehe", NACHT.alsEsWirdSatz()),
                    // "als die Nacht herankommt"
                    new Konditionalsatz("als", HERANKOMMEN.alsSatzMitSubjekt(NACHT)));
        }

        if (!timeVorher.kurzNachSonnenuntergang() && time.kurzNachSonnenuntergang()) {
            return ImmutableSet.of(
                    // "als die Sonne untergeht"
                    new Konditionalsatz("als", UNTERGEHEN.alsSatzMitSubjekt(SONNE)),
                    // "als die Nacht eingebrochen ist"
                    new Konditionalsatz(
                            "als", EINBRECHEN.alsSatzMitSubjekt(NACHT).perfekt()));
        }

        // IDEA: timeVorher.spaetInDerNacht()

        if (!timeVorher.umMitternacht() && time.umMitternacht()) {
            return ImmutableSet.of(
                    // "als es Mitternacht ist"
                    new Konditionalsatz("als", npArtikellos(MITTERNACHT).alsEsIstSatz()));
            // IDEA: "wies Mitternacht ist"
        }

        // IDEA: mittenInDerNacht()

        // Tageszeiten
        if (timeVorher.getTageszeit() != Tageszeit.MORGENS
                && time.getTageszeit() == Tageszeit.MORGENS) {
            return ImmutableSet.of(
                    // "wie der Morgen anbricht"
                    new Konditionalsatz(
                            "wie", ANBRECHEN
                            .alsSatzMitSubjekt(MORGEN)),
                    // "wie nun der Morgen anbricht"
                    new Konditionalsatz(
                            "wie", ANBRECHEN
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("nun"))
                            .alsSatzMitSubjekt(MORGEN)));
        }

        if (timeVorher.getTageszeit() != Tageszeit.ABENDS
                && time.getTageszeit() == Tageszeit.ABENDS) {
            return ImmutableSet.of(
                    // "als es Abend ist"
                    new Konditionalsatz("als", npArtikellos(ABEND).alsEsIstSatz()),
                    // IDEA "wies nun Abend ist"
                    // "als es Abend wird"
                    new Konditionalsatz("als", npArtikellos(ABEND).alsEsWirdSatz()),
                    // "als es Abend geworden ist"
                    new Konditionalsatz("als",
                            npArtikellos(ABEND).alsEsWirdSatz().perfekt()),
                    // IDEA "wies Abend wird"
                    // "als der Abend kommt"
                    new Konditionalsatz("als", KOMMEN.alsSatzMitSubjekt(ABEND)),
                    // "als der Abend einbricht"
                    new Konditionalsatz("als", EINBRECHEN.alsSatzMitSubjekt(ABEND)));
        }

        return ImmutableSet.of();
    }
}