package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHOEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SENGEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STARK;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WARM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABEND_EIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRENNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERUNTERSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.util.StreamUtil.*;

@Immutable
class WetterData {
    private final Temperatur tageshoechsttemperatur;

    private final Temperatur tagestiefsttemperatur;

    private final Windstaerke windstaerke;

    private final Bewoelkung bewoelkung;

    private final BlitzUndDonner blitzUndDonner;

    @SuppressWarnings("WeakerAccess")
    public WetterData(final Temperatur tageshoechsttemperatur,
                      final Temperatur tagestiefsttemperatur,
                      final Windstaerke windstaerke,
                      final Bewoelkung bewoelkung,
                      final BlitzUndDonner blitzUndDonner) {
        this.tageshoechsttemperatur = tageshoechsttemperatur;
        this.tagestiefsttemperatur = tagestiefsttemperatur;
        this.windstaerke = windstaerke;
        this.bewoelkung = bewoelkung;
        this.blitzUndDonner = blitzUndDonner;
    }

    AltDescriptionsBuilder altScKommtNachDraussenInsWetter(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (unterOffenenHimmel) {
            alt.addAll(altSonnenhitzeSaetzeWennUnterOffenemHimmelSinnvollMitAdvAngabe(
                    time, true, "draußen"));
        }

        if (unterOffenenHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
            // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
            // erwähnen.
            alt.addAll(bewoelkung.altScKommtUnterOffenenHimmel(time));
        }

        if (time.getTageszeit() == Tageszeit.NACHTS) {
            alt.addAll(altScKommtNachDraussenInsWetterSpeziellNachts(
                    time, lichtverhaeltnisseDraussen, unterOffenenHimmel));
        } else {
            alt.addAll(
                    altScKommtNachDraussenInsWetterSpeziellNichtNachts(time, unterOffenenHimmel));
        }
        return alt.schonLaenger();
    }

    private AltDescriptionsBuilder altScKommtNachDraussenInsWetterSpeziellNichtNachts(
            final AvTime time, final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altDescUeberHeuteOderDenTagWennDraussenSinnvoll(time, unterOffenenHimmel));

        if (!unterOffenenHimmel ||
                // Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(temperatur.altScKommtNachDraussenSaetze());
        }

        if (time.getTageszeit() == Tageszeit.TAGSUEBER
                && temperatur.saetzeUeberHeuteOderDenTagSinnvoll(time)) {
            if (unterOffenenHimmel) {
                alt.addAll(altNeueSaetze(
                        bewoelkung.altScKommtUnterOffenenHimmel(time),
                        ";",
                        temperatur.altDraussenSaetzeUeberHeuteOderDenTag()));
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            } else {
                alt.addAll(temperatur.altDraussenSaetzeUeberHeuteOderDenTag());
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            }
        } else if (time.getTageszeit() == Tageszeit.ABENDS) {
            alt.addAll(altScKommtNachDraussenInsWetterSpeziellAbends(time, unterOffenenHimmel));
        }

        return alt;
    }

    private AltDescriptionsBuilder altScKommtNachDraussenInsWetterSpeziellAbends(
            final AvTime time, final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
            if (unterOffenenHimmel) {
                alt.addAll(altNeueSaetze(
                        // FIXME Windstärke und Blitz / Donner berücksichtigen
                        bewoelkung.altScKommtUnterOffenenHimmel(time),
                        ";",
                        temperatur.altIstNochSaetze(true)));
            } else {
                alt.addAll(
                        // FIXME Windstärke und Blitz / Donner berücksichtigen
                        temperatur.altIstNochSaetze(true));
            }

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // FIXME Windstärke und Blitz / Donner berücksichtigen
                // "Es ist ein schöner Abend und noch ziemlich warm"
                alt.addAll(
                        temperatur.altAdjektivphrasen().stream()
                                .map(tempAdjPhr -> new ZweiPraedikativa<>(
                                        np(SCHOEN, ABEND_EIN),
                                        tempAdjPhr.mitAdvAngabe(
                                                new AdvAngabeSkopusSatz("noch")))
                                        .alsEsIstSatz()));
                if (temperatur == Temperatur.WARM) {
                    if (unterOffenenHimmel) {
                        // "Es ist ein schöner Abend, die Sonne scheint"
                        alt.addAll(altNeueSaetze(
                                praedikativumPraedikatMit(np(SCHOEN, ABEND_EIN))
                                        .alsSatzMitSubjekt(EXPLETIVES_ES),
                                ",",
                                bewoelkung.altUnterOffenemHimmelStatischeBeschreibungSaetze(time)));
                    } else {
                        // "Es ist ein schöner Abend"
                        alt.add(praedikativumPraedikatMit(np(SCHOEN, ABEND_EIN))
                                .alsSatzMitSubjekt(EXPLETIVES_ES));
                    }
                }
            }
        }

        return alt;
    }

    private AltDescriptionsBuilder altScKommtNachDraussenInsWetterSpeziellNachts(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (!unterOffenenHimmel || bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(
                    altScKommtNachDraussenInsWetterSpeziellNachtsBewoelkungUnauffaelligOderNichtOffenerHimmel(
                            time, lichtverhaeltnisseDraussen, unterOffenenHimmel));
        }

        if (unterOffenenHimmel) {
            // "Draußen ist es kühl und der Himmel ist bewälkt"
            alt.addAll(altNeueSaetze(
                    temperatur.altScKommtNachDraussenSaetze(),
                    "und",
                    bewoelkung.altUnterOffenemHimmelStatischeBeschreibungSaetze(time)));
        } else {
            // "Draußen ist es kühl"
            alt.addAll(temperatur.altScKommtNachDraussenSaetze());
        }

        if (time.kurzVorSonnenaufgang()
                && unterOffenenHimmel
                && temperatur.isUnauffaellig(time.getTageszeit())
                && bewoelkung.isUnauffaellig(time.getTageszeit())) {
            // Eine normale Temperatur und leichte Bewölkung braucht man nicht
            // unbedingt zu erwähnen.
            alt.add(neuerSatz("die Sonne geht bald auf"));
            alt.add(neuerSatz("die Sonne ist noch nicht wieder hervorgekommen"));
        }

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        return alt;
    }

    private AltDescriptionsBuilder
    altScKommtNachDraussenInsWetterSpeziellNachtsBewoelkungUnauffaelligOderNichtOffenerHimmel(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (lichtverhaeltnisseDraussen == HELL) {
            if (unterOffenenHimmel) {
                // "Draußen ist der Himmel bewälkt"
                alt.addAll(bewoelkung.altScKommtUnterOffenenHimmel(time));
            } else {
                // "draußen ist es ziemlich kühl"
                alt.addAll(temperatur.altScKommtNachDraussenSaetze());
            }
        } else {
            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                // Eine normale Nacht-Temperatur braucht man nicht unbedingt zu erwähnen -
                // selbst auf die Bewölkung kann man verzichten.
                alt.addAll(Lichtverhaeltnisse.altSCKommtNachDraussenInDunkelheit());
            }

            alt.addAll(altNeueSaetze(
                    Lichtverhaeltnisse.altSCKommtNachDraussenInDunkelheit().stream()
                            .map(AbstractDescription::toSingleKonstituente),
                    ";",
                    temperatur.altStatischeBeschreibungSaetze(true)));

            // "draußen ist es dunkel und ziemlich kühl"
            alt.addAll(
                    temperatur.altPraedikativa(true).stream()
                            .map(tempAdjPhr -> new ZweiPraedikativa<>(
                                    AdjektivOhneErgaenzungen.DUNKEL, tempAdjPhr)
                                    .alsEsIstSatz()
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));
        }

        return alt;
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
        if (endTime.minus(startTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return ImmutableSet.of();
        }

        // Es gab also potenziell einen (oder mehrere) Tageszeitenwechsel oder Wetterwechsel
        // während einer Zeit von weniger als einem Tag
        return altTimePassed(startTime.getTageszeit(), endTime.getTageszeit(), drinnenDraussen);
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass innerhalb maximal eines Tages
     * dieser Tageszeitenwechsel geschehen ist - bei gleicher Tageszeit leer.
     */
    @NonNull
    private ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final Tageszeit lastTageszeit,
            final Tageszeit currentTageszeit,
            final DrinnenDraussen drinnenDraussen) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder mehr als ein Tag, dann hat die Action
            // sicher ohnehin erzählt, was passiert ist.

            // FIXME Über den Tag weitere Texte verteilen, so dass die Zeit spürbarer vergeht:
            //  - die Sonne (draußen, Bewölkung) steht schon hoch
            //  - es ist schon weit nach Mittag
            return ImmutableSet.of();
        }

        final AltDescriptionsBuilder alt = alt();

        if (lastTageszeit.hasFolgetageszeit(currentTageszeit)) {
            // Es gab keine weiteren Tageszeiten dazwischen

            if (drinnenDraussen.isDraussen()) {
                // "Langsam wird es Morgen" / "hell"
                alt.addAll(
                        altLangsamBeginntTageszeitOderLichtverhaeltnisAenderungSaetze(lastTageszeit,
                                currentTageszeit));

                // TODO Wenn wir Perfektbildung könnten...!
                alt.addAll(altNeueSaetze(
                        ImmutableList.of("allmählich", "unterdessen"),
                        "ist es",
                        currentTageszeit.getEinzelneSubstantivischePhrase().nomK(), // "Morgen"
                        "geworden"
                        // Der Tageszeitenwechsel ist parallel passiert.
                ));

                // Die Sonne geht auf
                alt.addAll(altNeueSaetze(
                        PARAGRAPH,
                        bewoelkung
                                .altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar(
                                        currentTageszeit,
                                        drinnenDraussen ==
                                                DRAUSSEN_UNTER_OFFENEM_HIMMEL)));
            } else {
                alt.add(neuerSatz("Ob es wohl allmählich",
                        currentTageszeit.getEinzelneSubstantivischePhrase().nomK(), // "Morgen"
                        "geworden ist?"
                        // Der Tageszeitenwechsel ist parallel passiert.
                ));

                // "Ob es wohl langsam Morgen wird?"
                alt.addAll(altNeueSaetze(
                        altLangsamBeginntTageszeitOderLichtverhaeltnisAenderungSaetze(lastTageszeit,
                                currentTageszeit)
                                .stream()
                                .map(Satz::getIndirekteFrage),
                        "?"));
            }
        } else {
            if (drinnenDraussen.isDraussen()) {
                // "Die Sonne geht gerade auf"
                alt.addAll(mapToSet(
                        bewoelkung
                                .altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar(
                                        currentTageszeit,
                                        drinnenDraussen ==
                                                DRAUSSEN_UNTER_OFFENEM_HIMMEL),
                        s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))));
            }
        }

        switch (lastTageszeit) {
            case NACHTS:
                alt.addAll(altTimePassedFromNachtsTo(currentTageszeit, drinnenDraussen));
                break;
            case MORGENS:
                alt.addAll(altTimePassedFromMorgensTo(currentTageszeit, drinnenDraussen));
                break;
            case TAGSUEBER:
                alt.addAll(altTimePassedFromTagsueberTo(currentTageszeit, drinnenDraussen));
                break;
            case ABENDS:
                alt.addAll(altTimePassedFromAbendsTo(currentTageszeit, drinnenDraussen));
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }

        return alt.build();
    }

    private static ImmutableSet<Satz> altLangsamBeginntTageszeitOderLichtverhaeltnisAenderungSaetze(
            final Tageszeit lastTageszeit, final Tageszeit currentTageszeit) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        // "Langsam wird es Morgen", "Der Abend bricht an"
        alt.addAll(currentTageszeit.altLangsamBeginntSaetze());

        if (lastTageszeit.getLichtverhaeltnisseDraussen() != currentTageszeit
                .getLichtverhaeltnisseDraussen()) {
            // "langsam wird es hell"
            alt.addAll(currentTageszeit.getLichtverhaeltnisseDraussen().altLangsamWirdEsSaetze());
        }

        return alt.build();
    }

    private AltDescriptionsBuilder altTimePassedFromNachtsTo(
            @NonNull final Tageszeit currentTageszeit, final DrinnenDraussen drinnenDraussen) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case MORGENS:
                alt.add(neuerSatz("Der nächste Tag ist angebrochen"));
                // (Sowas hat man sogar drinnen im Gefühl - mindestens mal, wenn man
                // gut ausgeschlafen hat.)
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Langsam graut der Morgen")
                            // gilt vor allem bei bedecktem Himmel, kann aber wohl auch allgemein
                            // sagen
                    );

                    if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
                        if (bewoelkung == Bewoelkung.WOLKENLOS) {
                            alt.add(neuerSatz("Die Sterne verblassen und die Sonne ist",
                                    "am Horizont zu sehen"));
                        }

                        if (bewoelkung.compareTo(Bewoelkung.BEWOELKT) <= 0) {
                            alt.add(neuerSatz("Im Osten kündigt sich der neue Tag an"));
                        }
                    }
                }
                break;
            case TAGSUEBER:
                alt.add(neuerSatz("Der andere Tag hat begonnen"));
                if (drinnenDraussen.isDraussen()) {
                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen"));
                    }
                    if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                            && bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(neuerSatz("Inzwischen ist es hellichter Tag"));
                    }
                }

                break;
            case ABENDS:
                alt.add(neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        // Hat man so im Gefühl
                        neuerSatz("Der Tag ist schon fast vorüber")
                );
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Inzwischen wird es schon wieder dunkel"),
                            neuerSatz("Die Sonne ist schon wieder am Untergehen"));
                }
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        // FIXME Nach einem Tageszeitenwechsel, der nicht unter
        //  unter offenem Himmel stattgefunden hat: Bei nächstem Erreichen des offenen
        //  Himmels einen Text produzieren.
        //  Idee: Flag: wennWiederUnterOffenemHimmelDannStatischeTextSchreiben
        // FIXME analog:  Nach einem Tageszeitenwechsel, der drinnen stattgefunden hat:
        //  Beim nächsten mal draußen einen Text produzieren.
        //  Idee: Flag: wennWiederDraussenDannStatischeTextSchreiben

        return alt;
    }

    private AltDescriptionsBuilder altTimePassedFromMorgensTo(
            @NonNull final Tageszeit currentTageszeit, final DrinnenDraussen drinnenDraussen) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case TAGSUEBER:
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Die Sonne ist aufgegangen und beginnt ihren Lauf"));
                }
                break;
            case ABENDS:
                if (drinnenDraussen.isDraussen()) {
                    final ImmutableCollection<Satz>
                            altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar =
                            bewoelkung.
                                    altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar(
                                            currentTageszeit,
                                            drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                                    );
                    if (altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar
                            != null) {
                        alt.addAll(altNeueSaetze("währenddessen ist der Tag vergangen und",
                                altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar));
                    }
                    alt.add(neuerSatz("Inzwischen wird es schon wieder dunkel",
                            neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                            neuerSatz("Der Tag ist schon fast vorüber")));
                    if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL
                            && bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(neuerSatz("Die Sonne ist schon wieder am Untergehen"));
                    }
                } else {
                    alt.add(neuerSatz("Wahrscheinlich ist der Tag schon fast vorüber"));
                }
                break;
            case NACHTS:
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Die Sonne ist über die Zeit untergegangen"),
                            neuerSatz("Jetzt ist es dunkel"),
                            neuerSatz("Die Sonne ist jetzt untergegangen"),
                            neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"));

                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(
                                neuerSatz(PARAGRAPH, "Darüber ist es vollständig dunkel "
                                        + "geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht"));

                    }

                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) >= 0) {
                        alt.add(neuerSatz(PARAGRAPH,
                                "Es ist Nacht geworden und man sieht nicht mehr so gut"));
                    }
                    if (drinnenDraussen != DRAUSSEN_UNTER_OFFENEM_HIMMEL
                            || bewoelkung.compareTo(Bewoelkung.BEWOELKT) >= 0) {
                        alt.add(
                                neuerSatz("Jetzt ist es Nacht und man sieht nur noch wenig")
                        );
                    }
                } else {
                    alt.add(neuerSatz("Ob wohl die Sonne schon untergegangen ist?"),
                            neuerSatz("Jetzt ist es sicher schon dunkel!"),
                            neuerSatz(PARAGRAPH, "Gewiss ist schon Nacht"));
                }
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt;
    }

    private AltDescriptionsBuilder altTimePassedFromTagsueberTo(
            @NonNull final Tageszeit currentTageszeit, final DrinnenDraussen drinnenDraussen) {
        final AltDescriptionsBuilder alt = alt();
        switch (currentTageszeit) {
            case ABENDS:
                // Diese Dinge spürt man sogar drinnen:
                // "Der Abend bricht an"
                alt.addAll(currentTageszeit.altLangsamBeginntSaetze());
                alt.addAll(altNeueSaetze(
                        PARAGRAPH,
                        "Der Tag neigt sich und",
                        // "allmählich bricht der Abend an"
                        currentTageszeit.altLangsamBeginntSaetze()));
                // FIXME WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes
                //  Abendrot zu sehen"
                break;
            case NACHTS:
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz(PARAGRAPH, "Die Sonne ist jetzt untergegangen"),
                            neuerSatz(PARAGRAPH, "Es ist dunkel geworden"),
                            neuerSatz(PARAGRAPH, "Die Sonne ist untergegangen"),
                            neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"));

                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(neuerSatz(PARAGRAPH,
                                "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond geben etwas Licht"));
                    }

                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) >= 0) {
                        alt.add(neuerSatz(PARAGRAPH, "Inzwischen ist es Nacht und man",
                                "sieht nicht mehr so gut"),
                                neuerSatz(PARAGRAPH,
                                        "Es ist Nacht geworden und man sieht nicht mehr so gut"));
                    }
                } else {
                    alt.add(neuerSatz(PARAGRAPH, "Ob wohl die Sonne schon untergegangen ist"),
                            neuerSatz(PARAGRAPH, "Draußen ist es sicher schon dunkel"),
                            neuerSatz(PARAGRAPH, "Es wird wohl die Nacht schon", ""
                                    + "angebrochen sein"));
                }
                break;
            case MORGENS:
                alt.add(paragraph("Unterdessen hat der neue Tag begonnen")
                        // Das hat man so im Gefühl
                );

                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz(PARAGRAPH, "Es ist schon der nächste Morgen"),
                            // Das hat man so im Gefühl
                            neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder",
                                    "hell geworden"));
                } else {
                    alt.add(neuerSatz(PARAGRAPH, "Es ist gewiss schon der nächste Morgen"));
                }
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt;
    }

    private AltDescriptionsBuilder altTimePassedFromAbendsTo(
            @NonNull final Tageszeit currentTageszeit, final DrinnenDraussen drinnenDraussen) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case NACHTS:
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Die Sonne ist jetzt untergegangen"),
                            neuerSatz(PARAGRAPH, "Inzwischen ist die Nacht hereingebrochen")
                            // FIXME wenn der SC draußen ist:
                            //  "Jetzt sind am Himmel die Sterne zu sehen. Es ist dunkel und in der
                            //  Ferne ruft ein Käuzchen"
                    );

                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(neuerSatz(PARAGRAPH,
                                "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht"));
                    }

                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) >= 0) {
                        neuerSatz(PARAGRAPH,
                                "Es ist Nacht geworden und man sieht nicht mehr so gut");
                    }
                }
                break;
            case MORGENS:
                alt.add(neuerSatz("Unterdessen hat der neue Tag begonnen", PARAGRAPH),
                        neuerSatz("Es ist schon der nächste Morgen"));
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Die Nacht ist vorbei und es wird schon wieder hell"),
                            neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder",
                                    "hell geworden")
                    );
                }
                break;
            case TAGSUEBER:
                if (drinnenDraussen.isDraussen()) {
                    alt.add(neuerSatz("Die Sonne ist schon wieder aufgegangen"));
                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        alt.add(neuerSatz("Es ist schon wieder heller Tag"));
                    }
                } else {
                    alt.add(neuerSatz("Es ist sicher schon der nächste Tag"));
                }
                break;
            default:
                throw new IllegalStateException(
                        "Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt;
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
    //  „Du trittst aus dem Wald hinaus. Purpurnes Abendrot erstreckt sich über den
    //  Horizont....“

    // FIXME Kombination: "Es hat deutlich abgekühlt und der Himmel bezieht sich."

    @NonNull
    private List<Satz> altSonnenhitzeSaetzeWennUnterOffenemHimmelSinnvollMitAdvAngabe(
            final AvTime time,
            final boolean b,
            final String advAngabeText) {
        return mapToList(
                altStatischeBeschreibungSaetzeSonnenhitzeWennUnterOffenemHimmelSinnvoll(time,
                        b),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz(advAngabeText)));
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll(final AvTime time,
                                                    final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getTemperatur(time);

        if (temperatur.saetzeUeberHeuteOderDenTagSinnvoll(time)
                && time.getTageszeit() != Tageszeit.NACHTS) {
            if (unterOffenemHimmel) {
                alt.addAll(
                        altSonnenhitzeSaetzeWennUnterOffenemHimmelSinnvollMitAdvAngabe(time,
                                false,
                                "heute"));
            }

            if (!unterOffenemHimmel
                    // Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                    || bewoelkung.isUnauffaellig(time.getTageszeit())
                    // Und nachts kann man die Bewölkung ebenfalls ignorieren
                    || time.getTageszeit() != Tageszeit.NACHTS) {
                alt.addAll(altNeueSaetze(temperatur.altDraussenSaetzeUeberHeuteOderDenTag()));
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            }
            if (time.getTageszeit() == Tageszeit.TAGSUEBER
                    && temperatur.compareTo(Temperatur.RECHT_HEISS) >= 0
                    && unterOffenemHimmel) {
                // "der Tag ist warm, die Sonne sticht"
                alt.addAll(altNeueSaetze(
                        temperatur.altDraussenSaetzeUeberHeuteOderDenTag(),
                        ", die Sonne sticht"));
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            }
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt alternative Sätze zurück über die Sonnenhitze - wenn sinnvoll, sonst eine leere
     * {@link java.util.Collection}.
     */
    @CheckReturnValue
    private ImmutableCollection<Satz>
    altStatischeBeschreibungSaetzeSonnenhitzeWennUnterOffenemHimmelSinnvoll(
            final AvTime time, final boolean auchMitBezugAufKonkreteTageszeit) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                alt.add(SCHEINEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                        WARM.mitGraduativerAngabe("sehr"))),
                        HERUNTERSCHEINEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)));
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(BRENNEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)),
                        BRENNEN.alsSatzMitSubjekt(SONNENHITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(STARK))
                );

                if (auchMitBezugAufKonkreteTageszeit && time.gegenMittag()) {
                    alt.add(BRENNEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("zu Mittag"))
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)));
                }
            }
        }

        return alt.build();
    }

    @NonNull
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        if (temperatur == Temperatur.KLIRREND_KALT
                || temperatur == Temperatur.SEHR_HEISS
                || bewoelkung == Bewoelkung.BEDECKT) {
            return ImmutableSet
                    .of("Was ein Wetter!", "Was für ein Wetter!", "Welch ein Wetter!");
        }

        if (bewoelkung == Bewoelkung.BEWOELKT) {
            return ImmutableSet.of("Das Wetter war ja auch schon mal besser.");
        }

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        return ImmutableSet.of("Schönes Wetter heut!", "Schönes Wetter heut.");
    }

    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
            final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        if (unterOffenenHimmel) {
            alt.addAll(altInSonnenhitzeHinausWennUnterOffenemHimmelSinnvoll(time));
        }

        if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
            // erwähnen.
            if (unterOffenenHimmel) {
                alt.addAll(bewoelkung.altWohinHinausUnterOffenenHimmel(time));
            } else {
                alt.add(new AdvAngabeSkopusVerbWohinWoher(
                        IN_AKK.mit(time.getTageszeit().getEinzelneSubstantivischePhrase())));
            }
        }

        if (time.getTageszeit() == Tageszeit.NACHTS) {
            alt.addAll(altWohinHinausSpeziellNachts(time, lichtverhaeltnisseDraussen));
        } else {
            alt.addAll(altWohinHinausSpeziellNichtNachts(time, unterOffenenHimmel));
        }

        return alt.build();
    }

    private Iterable<AdvAngabeSkopusVerbWohinWoher>
    altInSonnenhitzeHinausWennUnterOffenemHimmelSinnvoll(
            final AvTime time) {
        return mapToSet(altSonnenhitzeWennUnterOffenemHimmelSinnvoll(time),
                sonnenhitze -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(sonnenhitze)));
    }

    private Iterable<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSpeziellNichtNachts(
            final AvTime time, final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (!unterOffenenHimmel
                // Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                || bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(temperatur.altWohinHinaus(time));

            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.add(new AdvAngabeSkopusVerbWohinWoher(
                        IN_AKK.mit(time.getTageszeit().getEinzelneSubstantivischePhrase())));
            }
        }

        return alt.build();
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSpeziellNachts(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Eine normale Nacht-Temperatur braucht man nicht unbedingt zu erwähnen -
            // selbst auf die Bewölkung kann man verzichten.
            alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Nacht"));

            if (time.kurzNachSonnenuntergang()) {
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die einbrechende Nacht"));
            }
        }

        if (lichtverhaeltnisseDraussen == Lichtverhaeltnisse.DUNKEL) {
            alt.addAll(temperatur.altWohinHinausDunkelheit());
        } else {
            alt.addAll(temperatur.altWohinHinaus(time));
        }

        return alt.build();
    }

    ImmutableSet<Praepositionalphrase> altUnterOffenemHimmel(final AvTime time) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen?
        // FIXME Blitz und Donner berücksichtigen?

        alt.addAll(getBewoelkung().altUnterOffenemHimmel(time.getTageszeit()));

        final ImmutableList<EinzelneSubstantivischePhrase> altSonnenhitzeWennSinnvoll =
                altSonnenhitzeWennUnterOffenemHimmelSinnvoll(time);

        alt.addAll(mapToSet(altSonnenhitzeWennSinnvoll, IN_DAT::mit));
        alt.addAll(mapToSet(altSonnenhitzeWennSinnvoll,
                substPhrOderReflexivpronomen ->
                        IN_DAT.mit(substPhrOderReflexivpronomen)
                                .mitModAdverbOderAdjektiv("mitten")));

        return alt.build();
    }

    private ImmutableList<EinzelneSubstantivischePhrase>
    altSonnenhitzeWennUnterOffenemHimmelSinnvoll(
            final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                alt.add(np(HEISS, SONNENSCHEIN));
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(SONNENHITZE);
                alt.add(np(SENGEND, SONNE));

                if (time.gegenMittag()) {
                    alt.add(MITTAGSSONNE);
                }
            }
        }

        return alt.build();
    }


    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvTime time,
                                                          final boolean unterOffenemHimmel) {
        return getBewoelkung().altBeiLichtImLicht(time.getTageszeit(), unterOffenemHimmel);
    }

    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final AvTime time,
                                                               final boolean unterOffenemHimmel) {
        return getBewoelkung().altBeiTageslichtImLicht(time.getTageszeit(), unterOffenemHimmel);
    }

    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvTime time,
            final boolean unterOffenemHimmel) {
        return getBewoelkung().altLichtInDemEtwasLiegt(time.getTageszeit(), unterOffenemHimmel);
    }

    Temperatur getTemperatur(final AvTime time) {
        return TagestemperaturverlaufUtil
                .calcTemperatur(tageshoechsttemperatur, tagestiefsttemperatur, time);
    }

    // FIXME Wind in Kombination: "Der Himmel ist blau und eine frische Luft weht dir entgegen"
    //  - "Der Himmel ist blau, die Luft mild"

    // FIXME Verknüpfungen bei Änderungen:
    //  "Als ... steht die Sonne schon hoch am Himmel und scheint heiß herunter."
    //  "Sobald die Sonne untergegangen ist, " (Uhrzeit berücksichtigen:
    //  time.kurzNachSonnenuntergang())

    // FIXME Sonne:
    //  "mit Sonnenaufgang (machts du dich auch den Weg...)"
    //  "Als aber die ersten Sonnenstrahlen in den Garten fallen, so..."
    //  "und als du erwachst und wieder zu dir selber kommst, bist
    //   du auf einer schönen Wiese, wo die Sonne scheint"
    //  "als du siehst, wie die Sonnenstrahlen durch die Bäume hin- und hertanzen"
    //  "du liegst in der Sonne ausgestreckt"
    //  "Als aber die Sonne bald untergehen will, "
    //  "Bei Sonnenaufgang kommt schon..."
    //  "Bei Sonnenuntergang kommst du zu..."
    //  "Du kommst in den Wald, und da es darin kühl und lieblich ist und die Sonne heiß
    //  brennt, so..."
    //  "Die Sonne geht auf, und ..."
    //  "Du ... noch immer, als es schon hoher Tag ist"
    //  "Noch halb steht die Sonne über (dem Berg) und halb ist sie unter."
    //  "Nun ist die Sonne unter:"
    //  "Als nun die Sonne durchs Fensterlein scheint und..."
    //  "Wie du nun (dies und jenes tust) und zu Mittag die Sonne heiß brennt, wird dir so
    //  warm und verdrießlich zumut:"
    //  "Als / wie nun die Sonne über dir steht, "
    //  "Wie nun die Sonne kommt und du aufwachst..."
    //  "durch die dichtbelaubten Äste dringt kein Sonnenstrahl"
    //  "Als die Sonne untergeht..."
    //  "Es dauert nicht lange, so siehst du die Sonne (hinter den Bergen) aufsteigen"
    //  "du bist von der Sonnenhitze müde"
    //  "(Schwerter) blitzen in der Sonne"
    //  du legst dich "in die Sonne"
    //  "Die Sonne hat die Erde aufgetaut"
    //  "Die Abendsonne scheint über (die
    //  glänzenden Steine), sie schimmerten und leuchteten so prächtig
    //  in allen Farben, daß..."
    //  "aber was tust du die Augen auf, als du aus (der Finsternis)
    //   heraus in das Tageslicht kommst, und den grünen Wald,
    //   Blumen und Vögel und die Morgensonne am Himmel erblickst"
    //  "Als nun die Sonne mitten über dem Walde steht..."
    //  ", bis die Sonne sinkt und die Nacht einbricht."
    //  "Als du aber am Morgen bei hellem Sonnenschein aufwachst, "
    //  "die Sonne ist hinter (den Bergen) verschwunden"
    //  "mittendurch rauscht ein klarer Bach, auf dem die Sonne glitzert"
    //  "es bricht eben der erste Sonnenstrahl hervor"
    //  "gegen Abend, als die Sonne (hinter die Berge) gesunken ist"
    //  "Du erwachst vor Sonnenuntergang"
    //  "Die Sonne will eben untergehen, als du erwachst"
    //  "Du (bleibst unter der Linde sitzen), bis die Sonne untergeht"
    //  "Als die Sonne aufgeht, ..."
    //  "in dem Augenblick dringt der erste Strahl der aufgehenden Sonne am Himmel herauf"
    //  "der Mond lässt sein Licht über alle Felder leuchten"
    //  "Sobald die Sonne wieder warm scheint, gehst du..."

    // FIXME Nacht:
    //  "Bei einbrechender Nacht"
    //  "Der Mond scheint über..."

    // FIXME Wind / Sturm
    //  "ein kühles Lüftchen streicht durch das Laub"
    //  Der Wind wird stärker
    //  Der Wind pfeift dir ums Gesicht
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?
    //  "der Regen schlägt dir ins Gesicht und der Wind zaust dein Haar"
    //  Ein Sturm zieht auf
    //  Hoffentlich bleibt es wenigstens trocken
    //  (Kein Regen - keine nassen Klamotten o.Ä.)
    //  "Die Äste biegen sich"
    //  "das Gezweig"
    //  "es kommt ein starker Wind"
    //  "es weht beständig ein harter Wind"
    //  "der Wind raschelt in den Bäumen, und die Wolken ziehen ganz nah über deinem Haupt weg"
    //  "der Wind saust"
    //  Der Wind ist jetzt sehr kräftig und angenehm. Kalt ist es geworden.
    //  Der Sturm biegt die Bäume.
    //  "darin bist du vor Wind und Wetter geschützt"
    //  "Um Mitternacht geht der Wind so kalt, dass dir nicht warm werden will"
    //  "Die Hitze wird drückender, je näher der Mittag kommt" (KEIN WIND)
    //  "Sturm"
    //  "es stürmt", "du findest darin Schutz"
    //  "der Wind rauscht draußen in den Bäumen"
    //  "Weil aber das Wetter so schlecht geworden, und Wind und Regen stürmte,
    //   kannst du nicht weiter und kehrst [...] ein."
    //  Ein ziemlicher Krach (Hexe geht nicht mehr spazieren. Schlossfest?!)
    //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
    //  wäre schön.
    //  Langsam scheint sich das Wetter wieder zu bessern / der Sturm flaut allmählich ab.
    //  "Der Wind legt sich, und auf den Bäumen vor [...] regt sich kein Blättchen mehr"
    //  "Es geht kein Wind, und bewegt sich kein Blättchen"
    //  "Kein Wind weht"

    // FIXME Aufwärmen
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "dich wärmen"
    //  "du erwärmst dich"
    //  Geschlossenheit?
    //   Drinnen umbeheizt
    //   Draußen geschuetzt
    //   Max temoeratur...

    // FIXME
    //  Fürs Wetter lässt sich wohl einiges von Hunger oder Müdigkeit übernehmen.

    // FIXME
    //  Man braucht regelmäßige Hinweise (je nach Dramatik des Wettes).

    // FIXME Plan-Wetter nur dramaturgisch geändert, nicht automatisch? Oder
    //  zwei Plan-Wetter, dramaturgisch und automatisch? Oder Plan-Wetter-Priorität?!

    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept!)
    //  "von der Hitze des Tages ermüdet"
    //  "du bist von der Sonnenhitze müde"

    @SuppressWarnings("WeakerAccess")
    @NonNull
    Bewoelkung getBewoelkung() {
        return bewoelkung;
    }

    @NonNull
    BlitzUndDonner getBlitzUndDonner() {
        return blitzUndDonner;
    }

    @NonNull
    Temperatur getTageshoechsttemperatur() {
        return tageshoechsttemperatur;
    }

    @NonNull
    Temperatur getTagestiefsttemperatur() {
        return tagestiefsttemperatur;
    }

    @NonNull
    Windstaerke getWindstaerke() {
        return windstaerke;
    }

    // FIXME Silbentrennung macht Fehler - Silbentrennung auf seltener einstellen?
    //  Oder anders korrigieren?

}
