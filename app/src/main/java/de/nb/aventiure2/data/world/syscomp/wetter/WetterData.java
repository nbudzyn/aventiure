package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STARK;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WARM;
import static de.nb.aventiure2.german.base.Nominalphrase.EIN_SCHOENER_ABEND;
import static de.nb.aventiure2.german.base.Nominalphrase.SONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.SONNENHITZE;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRENNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERUNTERSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static java.util.stream.Collectors.toList;

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
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altSonnenhitzeSaetzeWennSinnvollMitAdvAngabe(
                time, true, "draußen"));

        if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
            // erwähnen.
            alt.addAll(bewoelkung.altScKommtNachDraussenSaetze(time));
        }

        if (time.getTageszeit() == Tageszeit.NACHTS) {
            alt.addAll(altScKommtNachDraussenInsWetterSpeziellNachts(
                    time, lichtverhaeltnisseDraussen));
        } else {
            alt.addAll(altScKommtNachDraussenInsWetterSpeziellNichtNachts(time));
        }
        return alt.schonLaenger();
    }

    private AltDescriptionsBuilder altScKommtNachDraussenInsWetterSpeziellNichtNachts(
            final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altDescUeberHeuteOderDenTagWennSinnvoll(time));

        if (// Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(temperatur.altScKommtNachDraussenSaetze());
        }

        if (time.getTageszeit() == Tageszeit.TAGSUEBER
                && temperatur.saetzeUeberHeuteOderDenTagSinnvoll(time)) {
            alt.addAll(altNeueSaetze(
                    bewoelkung.altScKommtNachDraussenSaetze(time),
                    ";",
                    temperatur.altSaetzeUeberHeuteOderDenTag()));
            // FIXME Windstärke berücksichtigen
            // FIXME Blitz und Donner berücksichtigen
        } else if (time.getTageszeit() == Tageszeit.ABENDS) {
            alt.addAll(altScKommtNachDraussenInsWetterSpeziellAbends(time));
        }

        return alt;
    }

    private AltDescriptionsBuilder altScKommtNachDraussenInsWetterSpeziellAbends(
            final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
            alt.addAll(altNeueSaetze(
                    // FIXME Windstärke und Blitz / Donner berücksichtigen
                    bewoelkung.altScKommtNachDraussenSaetze(time),
                    ";",
                    temperatur.altIstNochSaetze()));

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // FIXME Windstärke und Blitz / Donner berücksichtigen
                // "Es ist ein schöner Abend und noch ziemlich warm"
                alt.addAll(
                        temperatur.altAdjektivphrasen().stream()
                                .map(tempAdjPhr -> new ZweiPraedikativa<>(
                                        EIN_SCHOENER_ABEND,
                                        tempAdjPhr.mitAdvAngabe(
                                                new AdvAngabeSkopusSatz("noch")))
                                        .alsEsIstSatz()));
                if (temperatur == Temperatur.WARM) {
                    // "Es ist ein schöner Abend, die Sonne scheint"
                    alt.addAll(altNeueSaetze(
                            praedikativumPraedikatMit(EIN_SCHOENER_ABEND)
                                    .alsSatzMitSubjekt(EXPLETIVES_ES),
                            ",",
                            bewoelkung.altStatischeBeschreibungSaetze(time)));
                }
            }
        }

        return alt;
    }

    private AltDescriptionsBuilder altScKommtNachDraussenInsWetterSpeziellNachts(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(
                    altScKommtNachDraussenInsWetterSpeziellNachtsBewoelkungUnauffaellig(
                            time, lichtverhaeltnisseDraussen));
        }

        // "Draußen ist es kühl und der Himmel ist bewälkt"
        alt.addAll(altNeueSaetze(
                temperatur.altScKommtNachDraussenSaetze(),
                "und",
                bewoelkung.altStatischeBeschreibungSaetze(time)));

        if (time.kurzVorSonnenaufgang()
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
    altScKommtNachDraussenInsWetterSpeziellNachtsBewoelkungUnauffaellig(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (lichtverhaeltnisseDraussen == HELL) {
            // "Draußen ist der Himmel bewälkt"
            alt.addAll(bewoelkung.altScKommtNachDraussenSaetze(time));
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
                    temperatur.altStatischeBeschreibungSaetze()));

            // "draußen ist es dunkel und ziemlich kühl"
            alt.addAll(
                    temperatur.altPraedikativa().stream()
                            .map(tempAdjPhr -> new ZweiPraedikativa<>(
                                    AdjektivOhneErgaenzungen.DUNKEL, tempAdjPhr)
                                    .alsEsIstSatz()
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));
        }

        return alt;
    }

    @NonNull
    private List<Satz> altSonnenhitzeSaetzeWennSinnvollMitAdvAngabe(final AvTime time,
                                                                    final boolean b,
                                                                    final String advAngabeText) {
        return altStatischeBeschreibungSaetzeSonnenhitzeWennSinnvoll(time, b).stream()
                .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz(advAngabeText)))
                .collect(toList());
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - soweit sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennSinnvoll(final AvTime time) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getTemperatur(time);

        if (temperatur.saetzeUeberHeuteOderDenTagSinnvoll(time)
                && time.getTageszeit() != Tageszeit.NACHTS) {
            alt.addAll(altSonnenhitzeSaetzeWennSinnvollMitAdvAngabe(time, false, "heute"));

            if (// Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                    bewoelkung.isUnauffaellig(time.getTageszeit())
                            // Und nachts kann man die Bewölkung ebenfalls ignorieren
                            || time.getTageszeit() != Tageszeit.NACHTS) {
                alt.addAll(altNeueSaetze(temperatur.altSaetzeUeberHeuteOderDenTag()));
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            }
            if (time.getTageszeit() == Tageszeit.TAGSUEBER
                    && temperatur.compareTo(Temperatur.RECHT_HEISS) >= 0) {
                // FIXME  "der Tag ist warm, die Sonne sticht"
                alt.addAll(altNeueSaetze(
                        temperatur.altSaetzeUeberHeuteOderDenTag(),
                        ", die Sonne sticht"));
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            }
        }

        return alt.schonLaenger().build();
    }

    // FIXME Überall nach licht, hell, dunkel, warm, wärme, wolk, kühl,
    //  schatt, wölk, Wetter, Himmel etc. suchen
    //  und 1. Widersprüche verhindern 2. Wetter hier zentralisieren.

    /**
     * Gibt alternative Sätze zurück über die Sonnenhitze - wenn sinnvoll, sonst eine leere
     * {@link java.util.Collection}.
     */
    @CheckReturnValue
    private ImmutableCollection<Satz> altStatischeBeschreibungSaetzeSonnenhitzeWennSinnvoll(
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
            return ImmutableSet.of("Was ein Wetter!", "Was für ein Wetter!", "Welch ein Wetter!");
        }

        if (bewoelkung == Bewoelkung.BEWOELKT) {
            return ImmutableSet.of("Das Wetter war ja auch schon mal besser.");
        }

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        return ImmutableSet.of("Schönes Wetter heut!", "Schönes Wetter heut.");
    }

    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time, final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        alt.addAll(altInSonnenhitzeHinausWennSinnvoll(time));

        if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
            // erwähnen.
            alt.addAll(bewoelkung.altWohinHinaus(time));
        }

        if (time.getTageszeit() == Tageszeit.NACHTS) {
            alt.addAll(altWohinHinausSpeziellNachts(time, lichtverhaeltnisseDraussen));
        } else {
            alt.addAll(altWohinHinausSpeziellNichtNachts(time));
        }

        return alt.build();
    }

    private Iterable<AdvAngabeSkopusVerbWohinWoher> altInSonnenhitzeHinausWennSinnvoll(
            final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt = ImmutableList.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in den heißen Sonnenschein"));
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Sonnenhitze"));
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die sengende Sonne"));

                if (time.gegenMittag()) {
                    alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Mittagshitze"));
                }
            }
        }

        return alt.build();
    }

    private Iterable<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSpeziellNichtNachts(
            final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (// Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(temperatur.altWohinHinaus(time));

            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.add(new AdvAngabeSkopusVerbWohinWoher(
                        PraepositionMitKasus.IN_AKK.mit(
                                time.getTageszeit().getNominalphrase())));
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

    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvTime time) {
        return getBewoelkung().altBeiLichtImLicht(time.getTageszeit());
    }

    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final AvTime time) {
        return getBewoelkung().altBeiTageslichtImLicht(time.getTageszeit());
    }

    ImmutableCollection<Nominalphrase> altLichtInDemEtwasLiegt(final AvTime time) {
        return getBewoelkung().altLichtInDemEtwasLiegt(time.getTageszeit());
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
    //  "Als der Tag anbricht, noch ehe die Sonne aufgegangen ist"

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
    //  sitzt "in der Sonne"
    //  "du bist von der Sonnenhitze müde"
    //  liegst "mitten im heißen Sonnenschein"
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

    // FIXME Nacht:
    // "Bei einbrechender Nacht"
    // "Der Mond scheint über..."

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
    //  Wetterschutzprofil?
    //   Drinnen umbeheizt
    //   Draußen geschuetzt
    //   Max temoeratur...

    // FIXME
    //  "Sobald die Sonne wieder warm scheint, gehst du..."

    // FIXME
    //  Fürs Wetter lässt sich wohl einiges von Hunger oder Müdigkeit übernehmen.

    // FIXME
    //  Man braucht regelmäßige Hinweise (je nach Dramatik des Wettes).

    // FIXME Plan-Wetter nur dramaturgisch geändert, nicht automatisch? Oder
    //  zwei Plan-Wetter, dramaturgisch und automatisch? Oder Plan-Wetter-Priorität?!

    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept!)
    //  "von der Hitze des Tages ermüdet"

    //  "du bist von der Sonnenhitze müde"

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
