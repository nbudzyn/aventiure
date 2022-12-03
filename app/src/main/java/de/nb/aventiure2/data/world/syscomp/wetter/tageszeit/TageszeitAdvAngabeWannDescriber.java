package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

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

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.KonditionalSemSatz;

/**
 * Erzeugt tageszeitliche oder untertageszeitliche temporale Beschreibungen als
 * adverbiale Angaben.
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class TageszeitAdvAngabeWannDescriber {

    public ImmutableSet<AdvAngabeSkopusSatz> altSpWannDraussen(final Change<AvTime> change) {

        // Untertageszeitliche Angaben (werden bevorzugt)
        if (change.wasntBeforeButIsAfter(AvTime::kurzVorSonnenaufgang)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("vor Sonnenaufgang"),
                    new AdvAngabeSkopusSatz("vor Tagesanbruch"),
                    new AdvAngabeSkopusSatz("früh vor Tag"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::kurzNachSonnenaufgang)) {
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

        if (change.wasntBeforeButIsAfter(AvTime::vorMittag)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("vor Mittag"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::gegenMittag)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("gegen Mittag"),
                    new AdvAngabeSkopusSatz("zu Mittag"),
                    new AdvAngabeSkopusSatz("mittags"),
                    new AdvAngabeSkopusSatz("um Mittagszeit"),
                    new AdvAngabeSkopusSatz("zur Mittagszeit"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::nachmittags)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("nachmittags"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::kurzNachSonnenuntergang)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("vor Sonnenuntergang"),
                    new AdvAngabeSkopusSatz("vor Einbruch der Nacht"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::kurzNachSonnenuntergang)) {
            return ImmutableSet.of(
                    new AdvAngabeSkopusSatz("bei Sonnenuntergang"),
                    new AdvAngabeSkopusSatz("nach Sonnenuntergang"),
                    new AdvAngabeSkopusSatz("bei einbrechender Nacht"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::spaetInDerNacht)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("spät in der Nacht"));
        }

        if (change.wasntBeforeButIsAfter(AvTime::umMitternacht)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("um Mitternacht"),
                    new AdvAngabeSkopusSatz("gegen Mitternacht"));
            // IDEA "Geisterstunde"?
        }

        if (change.wasntBeforeButIsAfter(AvTime::mittenInDerNacht)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("mitten in der Nacht"));
        }

        // Tageszeiten
        if (change.wasntBeforeButIsAfter(t -> t.getTageszeit() == Tageszeit.MORGENS)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("am andern Morgen"),
                    new AdvAngabeSkopusSatz("am Morgen"),
                    new AdvAngabeSkopusSatz("morgens"),
                    new AdvAngabeSkopusSatz("am nächsten Morgen"));
        }

        if (change.wasntBeforeButIsAfter(t -> t.getTageszeit() == Tageszeit.ABENDS)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("abends"),
                    new AdvAngabeSkopusSatz("am Abend"),
                    new AdvAngabeSkopusSatz("gegen Abend"),
                    new AdvAngabeSkopusSatz("zur Abendzeit"));
        }

        if (change.wasntBeforeButIsAfter(t -> t.getTageszeit() == Tageszeit.NACHTS)) {
            return ImmutableSet.of(new AdvAngabeSkopusSatz("nachts"),
                    new AdvAngabeSkopusSatz("in der Nacht"));
        }

        return ImmutableSet.of();
    }

    public ImmutableSet<KonditionalSemSatz> altSpWannKonditionalsaetzeDraussen(
            final Change<AvTime> change) {

        // Untertageszeitliche Angaben (werden bevorzugt)
        if (change.wasntBeforeButIsAfter(AvTime::kurzVorSonnenaufgang)) {
            return ImmutableSet.of(
                    // "noch ehe die Sonne aufgegangen ist
                    new KonditionalSemSatz(
                            "noch ehe", AUFGEHEN.alsSatzMitSubjekt(SONNE).perfekt()),
                    // "bevor der Tag anbricht
                    new KonditionalSemSatz(
                            "bevor", ANBRECHEN.alsSatzMitSubjekt(TAG)));
        }

        if (change.wasntBeforeButIsAfter(AvTime::kurzNachSonnenaufgang)) {
            return ImmutableSet.of(
                    // "als die Sonne aufgeht
                    new KonditionalSemSatz(
                            "als", AUFGEHEN.alsSatzMitSubjekt(SONNE)),
                    // "als der Tag angebrochen ist
                    new KonditionalSemSatz(
                            "als", ANBRECHEN.alsSatzMitSubjekt(TAG).perfekt()));
        }

        if (change.wasntBeforeButIsAfter(AvTime::vorMittag)) {
            return ImmutableSet.of(
                    // "noch ehe es Mittag ist"
                    new KonditionalSemSatz("noch ehe", npArtikellos(MITTAG).alsEsIstSatz()),
                    // "als es bald Mittag werden will"
                    new KonditionalSemSatz("als",
                            WOLLEN.mitLexikalischemKern(npArtikellos(MITTAG)
                                    .alsWerdenPraedikativumPraedikat()
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("bald")))
                                    .alsSatzMitSubjekt(EXPLETIVES_ES)));
        }

        if (change.wasntBeforeButIsAfter(AvTime::gegenMittag)) {
            return ImmutableSet.of(
                    // "als es Mittag ist"
                    new KonditionalSemSatz(
                            "als", npArtikellos(MITTAG).alsEsIstSatz()));
        }

        // IDEA: ::nachmittags
        // IDEA: Als der Abend herankommt

        if (change.wasntBeforeButIsAfter(AvTime::kurzVorSonnenuntergang)) {
            return ImmutableSet.of(
                    // "als die Sonne bald untergehen will"
                    new KonditionalSemSatz(
                            "als",
                            WOLLEN.mitLexikalischemKern(UNTERGEHEN)
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("bald"))
                                    .alsSatzMitSubjekt(SONNE)),
                    // "ehe es Nacht wird"
                    new KonditionalSemSatz("ehe", NACHT.alsEsWirdSatz()),
                    // "als die Nacht herankommt"
                    new KonditionalSemSatz("als", HERANKOMMEN.alsSatzMitSubjekt(NACHT)));
        }

        if (change.wasntBeforeButIsAfter(AvTime::kurzNachSonnenuntergang)) {
            return ImmutableSet.of(
                    // "als die Sonne untergeht"
                    new KonditionalSemSatz("als", UNTERGEHEN.alsSatzMitSubjekt(SONNE)),
                    // "als die Nacht eingebrochen ist"
                    new KonditionalSemSatz(
                            "als", EINBRECHEN.alsSatzMitSubjekt(NACHT).perfekt()));
        }

        // IDEA: ::spaetInDerNacht

        if (change.wasntBeforeButIsAfter(AvTime::umMitternacht)) {
            return ImmutableSet.of(
                    // "als es Mitternacht ist"
                    new KonditionalSemSatz("als", npArtikellos(MITTERNACHT).alsEsIstSatz()));
            // IDEA: "wies Mitternacht ist"
        }

        // IDEA: ::mittenInDerNacht

        // Tageszeiten
        if (change.wasntBeforeButIsAfter(t -> t.getTageszeit() == Tageszeit.MORGENS)) {
            return ImmutableSet.of(
                    // "wie der Morgen anbricht"
                    new KonditionalSemSatz(
                            "wie", ANBRECHEN
                            .alsSatzMitSubjekt(MORGEN)),
                    // "wie nun der Morgen anbricht"
                    new KonditionalSemSatz(
                            "wie", ANBRECHEN
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("nun"))
                            .alsSatzMitSubjekt(MORGEN)));
        }

        if (change.wasntBeforeButIsAfter(t -> t.getTageszeit() == Tageszeit.ABENDS)) {
            return ImmutableSet.of(
                    // "als es Abend ist"
                    new KonditionalSemSatz("als", npArtikellos(ABEND).alsEsIstSatz()),
                    // IDEA "wies nun Abend ist"
                    // "als es Abend wird"
                    new KonditionalSemSatz("als", npArtikellos(ABEND).alsEsWirdSatz()),
                    // "als es Abend geworden ist"
                    new KonditionalSemSatz("als",
                            npArtikellos(ABEND).alsEsWirdSatz().perfekt()),
                    // IDEA "wies Abend wird"
                    // "als der Abend kommt"
                    new KonditionalSemSatz("als", KOMMEN.alsSatzMitSubjekt(ABEND)),
                    // "als der Abend einbricht"
                    new KonditionalSemSatz("als", EINBRECHEN.alsSatzMitSubjekt(ABEND)));
        }

        return ImmutableSet.of();
    }
}