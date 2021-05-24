package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Konditionalsatz;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.ONE_DAY;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEDECKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.WOLKENLOS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUNKEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HOCH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHOEN;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als {@link AbstractDescription}s.
 * <p>
 * Diese {@link AbstractDescription}s sind für jede Temperatur sinnvoll (wobei manchmal die
 * Temperatur oder andere Wetteraspekte wichtiger sind und man dann diese Descriptions
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class BewoelkungDescDescriber {
    private final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber;
    private final BewoelkungSatzDescriber satzDescriber;

    public BewoelkungDescDescriber(
            final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber,
            final BewoelkungSatzDescriber satzDescriber) {
        this.tageszeitAdvAngabeWannDescriber = tageszeitAdvAngabeWannDescriber;
        this.satzDescriber = satzDescriber;
    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie die Bewölkung sich eine Stufe
     * (Bewölkungswechsel) oder mehrere Stufen (Bewölkungssprung) verändert hat.*
     */
    public ImmutableCollection<AbstractDescription<?>> altSprungOderWechselDraussen(
            final Change<AvDateTime> dateTimeChange,
            final WetterParamChange<Bewoelkung> change,
            final boolean unterOffenemHimmel, final boolean auchZeitwechselreferenzen) {
        if (unterOffenemHimmel) {
            return altSprungOderWechselUnterOffenemHimmel(
                    dateTimeChange, change, auchZeitwechselreferenzen);
        }

        return altSprungOderWechselDraussenGeschuetzt(
                dateTimeChange.getNachher(), change);
    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie die Bewölkung sich eine Stufe
     * (Bewölkungswechsel) oder mehrere Stufen (Bewölkungssprung) verändert hat - erlebt
     * unter offenem Himmel
     */
    public ImmutableCollection<AbstractDescription<?>> altSprungOderWechselUnterOffenemHimmel(
            final Change<AvDateTime> dateTimeChange,
            final WetterParamChange<Bewoelkung> change, final boolean auchZeitwechselreferenzen) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.addAll(satzDescriber.altSprungOderWechselUnterOffenemHimmel(dateTimeChange,
                change, auchZeitwechselreferenzen));

        final ImmutableSet<Konstituente> altWann;
        final ImmutableSet<Konstituentenfolge> altWannSaetze;

        if (span(dateTimeChange).shorterThan(ONE_DAY) && auchZeitwechselreferenzen) {
            final Change<AvTime> timeChange = dateTimeChange.map(AvDateTime::getTime);

            altWann = mapToSet(tageszeitAdvAngabeWannDescriber.altWannDraussen(timeChange),
                    gegenMitternacht -> gegenMitternacht.getDescription(EXPLETIVES_ES));
            altWannSaetze = mapToSet(tageszeitAdvAngabeWannDescriber
                            .altWannKonditionalsaetzeDraussen(timeChange),
                    Konditionalsatz::getDescription);
        } else {
            altWann = ImmutableSet.of();
            altWannSaetze = ImmutableSet.of();
        }

        final int delta = change.delta();

        if (Math.abs(delta) <= 1) {
            if (change.getVorher().hasNachfolger(change.getNachher())) {
                if (change.getNachher() == LEICHT_BEWOELKT) {
                    if (dateTimeChange.getNachher().getTageszeit() == NACHTS) {
                        alt.add(neuerSatz(
                                "Die Sterne leuchten nicht mehr so klar wie zuvor"));
                        if (!altWann.isEmpty()) {
                            alt.addAll(altNeueSaetze(
                                    altWann,
                                    "leuchten die Sterne nicht mehr so klar wie zuvor"));
                        }
                        if (!altWannSaetze.isEmpty()) {
                            alt.addAll(altNeueSaetze(
                                    altWannSaetze,
                                    ", leuchten die Sterne nicht mehr so klar wie zuvor"));
                        }
                    } else {
                        alt.add(neuerSatz(
                                "Hier und da sind jetzt einzelne Schäfchenwolken zu sehen"));
                    }
                } else if (change.getNachher() == BEDECKT) {
                    alt.addAll(altNeueSaetze(
                            dateTimeChange.getNachher().getTageszeit().altGestirn().stream()
                                    .filter(s -> s.getNumerus() == SG)
                                    .map(SubstantivischePhrase::nomK), // "die Sonne"
                            "hat sich versteckt und will hinter den Wolken",
                            "gar nicht mehr hervorkommen"));
                    if (dateTimeChange.getNachher().getTageszeit() != NACHTS) {
                        alt.add(neuerSatz("Mächtige dunkle Wolken beginnen sich am Himmel",
                                "aufzutürmen"));
                        if (!altWann.isEmpty()) {
                            alt.addAll(altNeueSaetze(altWann,
                                    "beginnen mächtige dunkle Wolken sich am Himmel aufzutürmen"));
                        }
                        if (!altWannSaetze.isEmpty()) {
                            alt.addAll(altNeueSaetze(
                                    altWannSaetze,
                                    ", beginnen mächtige dunkle Wolken sich am Himmel "
                                            + "aufzutürmen"));
                        }
                    } else {
                        alt.add(neuerSatz("Die Nacht wird immer dunkler, Sterne sind",
                                "keine mehr zu sehen"));
                    }
                }
            } else {
                if (change.getNachher() == WOLKENLOS
                        && dateTimeChange.getNachher().getTageszeit() != NACHTS) {
                    alt.add(neuerSatz("Die letzten weißen",
                            "Schäfchenwolken haben sich verzogen und der ganze Himmel",
                            "strahlt blau über dir"));
                    if (!altWann.isEmpty()) {
                        alt.addAll(altNeueSaetze(altWann,
                                "haben sich die letzten weißen",
                                "Schäfchenwolken verzogen und der ganze Himmel",
                                "strahlt blau über dir"));
                    }
                    if (!altWannSaetze.isEmpty()) {
                        alt.addAll(altNeueSaetze(
                                altWannSaetze,
                                ", haben sich die letzten weißen",
                                "Schäfchenwolken verzogen und der ganze Himmel",
                                "strahlt blau über dir"));
                    }
                } else if (change.getNachher() == LEICHT_BEWOELKT) {
                    if (dateTimeChange.getNachher().getTageszeit() != NACHTS) {
                        alt.add(neuerSatz("Die Wolken reißen wieder auf und geben hier und da",
                                "den Blick auf den blauen Himmel frei"));
                        if (!altWann.isEmpty()) {
                            alt.addAll(altNeueSaetze(altWann,
                                    "reißen die Wolken wieder auf und geben hier und da",
                                    "den Blick auf den blauen Himmel frei"));
                        }
                        if (!altWannSaetze.isEmpty()) {
                            alt.addAll(altNeueSaetze(
                                    altWannSaetze,
                                    ", reißen die Wolken wieder auf und geben hier und da",
                                    "den Blick auf den blauen Himmel frei"));
                        }
                    } else {
                        alt.add(neuerSatz("Die Wolken reißen wieder auf und geben hier und da",
                                "den Blick auf den Sternenhimmel frei"));
                        if (!altWann.isEmpty()) {
                            alt.addAll(altNeueSaetze(altWann,
                                    "reißen die Wolken wieder auf und geben hier und da",
                                    "den Blick auf den Sternenhimmel frei"));
                        }
                        if (!altWannSaetze.isEmpty()) {
                            alt.addAll(altNeueSaetze(
                                    altWannSaetze,
                                    ", reißen die Wolken wieder auf und geben hier und da",
                                    "den Blick auf den Sternenhimmel frei"));
                        }
                    }
                }
            }
        }

        if (delta > 1 && change.getNachher() == LEICHT_BEWOELKT
                && dateTimeChange.getNachher().getTageszeit() != NACHTS) {
            alt.add(neuerSatz("Inwischen scheint die Sonne wieder hell"));
        }

        return alt.schonLaenger().build();
    }

    private ImmutableCollection<AbstractDescription<?>> altSprungOderWechselDraussenGeschuetzt(
            final AvDateTime time,
            final WetterParamChange<Bewoelkung> change) {
        if (change.getVorher() == BEDECKT) {
            return altSprungOderWechselDraussenGeschuetztVorherBedeckt(time.getTageszeit());
        }

        if (change.getNachher() == BEDECKT) {
            return altSprungOderWechselDraussenGeschuetztNachherBedeckt(time.getTageszeit());
        }

        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.add(neuerSatz("Ob sich das Licht verändert hat?"),
                neuerSatz("Jede Tageszeit, jedes Wetter hat doch sein eigenes Licht!"));

        return alt.schonLaenger().build();
    }

    private ImmutableCollection<AbstractDescription<?>>
    altSprungOderWechselDraussenGeschuetztVorherBedeckt(final Tageszeit tageszeit) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.add(neuerSatz(
                tageszeit.getNomenFlexionsspalte().nomK(), // "der Abend"
                "scheint heller geworden zu sein"),
                neuerSatz("Alles liegt in einem angenehmen Licht"),
                neuerSatz("Es ist nicht mehr so dunkel wie zuvor"));

        return alt.schonLaenger().build();
    }

    private ImmutableCollection<AbstractDescription<?>>
    altSprungOderWechselDraussenGeschuetztNachherBedeckt(final Tageszeit tageszeit) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.add(neuerSatz(
                tageszeit.getNomenFlexionsspalte().nomK(), // "der Abend"
                "scheint dunkler geworden zu sein"),
                neuerSatz("Das Licht ist schlechter als noch zuvor"),
                neuerSatz("Es ist",
                        np(INDEF, DUNKEL.mitGraduativerAngabe("besonders"),
                                tageszeit.getNomenFlexionsspalte())
                                .nomK())); // "eine besonders dunkle Nacht"

        return alt.schonLaenger().build();
    }


    /**
     * Gibt Alternativen zurück, die beschreiben, wie man einen "Tageszeitensprung"
     * (der SC hat z.B. eine Weile geschlafen oder Ähnliches, und die Tageszeit
     * hat in der Zeit mehrfach gewechselt - jedenfalls ist nicht mehr als
     * 1 Tag vergangen!) - oder auch einfach einen (einmaligen) Tageszeitenwechsel
     * in der Bewölkung erlebt - gibt eventuell eine leere Collection zurück.
     * <p>
     * Dabei hat sich in der Regel der {@link Bewoelkung}swert gar nicht geändert,
     * aber trotzdem sieht man an der Bewölkung (oder den Gestirnen), dass die
     * Tageszeit "gesprungen ist" oder gewechselt hat: "Die Sonne geht auf" o.Ä.
     */
    @NonNull
    @CheckReturnValue
    public AltDescriptionsBuilder
    altTageszeitensprungOderWechsel(
            final Bewoelkung bewoelkung,
            final Tageszeit lastTageszeit,
            final Tageszeit currentTageszeit, final boolean unterOffenemHimmel) {
        checkArgument(lastTageszeit != currentTageszeit,
                "Kein Tageszeitensprung oder -Wechsel: %s", lastTageszeit);

        if (lastTageszeit.hasNachfolger(currentTageszeit)) {
            // Es gab keine weiteren Tageszeiten dazwischen ("Tageszeitenwechsel")
            return altTageszeitenwechsel(bewoelkung, currentTageszeit, unterOffenemHimmel);
        }

        // Es gab weitere Tageszeiten dazwischen ("Tageszeitensprung")
        return altTageszeitensprung(bewoelkung, lastTageszeit, currentTageszeit,
                unterOffenemHimmel);
    }

    private AltDescriptionsBuilder altTageszeitensprung(final Bewoelkung bewoelkung,
                                                        final Tageszeit lastTageszeit,
                                                        final Tageszeit currentTageszeit,
                                                        final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        // "Die Sonne geht gerade auf"
        alt.addAll(mapToSet(
                satzDescriber
                        .altTageszeitenwechsel(
                                bewoelkung, currentTageszeit, unterOffenemHimmel),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))));

        switch (lastTageszeit) {
            case NACHTS:
                alt.addAll(altTageszeitensprungOderWechselFromNachtsTo(
                        bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            case MORGENS:
                alt.addAll(altTageszeitensprungOderWechselFromMorgensTo(
                        bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            case TAGSUEBER:
                alt.addAll(altTageszeitensprungOderWechselFromTagsueberTo(
                        bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            case ABENDS:
                alt.addAll(altTageszeitensprungOderWechselFromAbendsTo(
                        bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altTageszeitensprungOderWechselFromNachtsTo(
            final Bewoelkung bewoelkung,
            final Tageszeit currentTageszeit, final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case MORGENS:
                alt.addAll(altTageszeitenwechsel(bewoelkung, MORGENS, unterOffenemHimmel));
                break;
            case TAGSUEBER:
                if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("derweil ist die Sonne aufgegangen"));
                }
                if (unterOffenemHimmel
                        && bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("Inzwischen ist es hellichter Tag"));
                }
                break;
            case ABENDS:
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altTageszeitensprungOderWechselFromMorgensTo(
            final Bewoelkung bewoelkung,
            final Tageszeit currentTageszeit, final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case TAGSUEBER:
                alt.addAll(altTageszeitenwechsel(bewoelkung, TAGSUEBER, unterOffenemHimmel));
                break;
            case ABENDS:
                final ImmutableCollection<Satz>
                        altTageszeitenwechsel =
                        satzDescriber.altTageszeitenwechsel(
                                bewoelkung, currentTageszeit, unterOffenemHimmel);
                if (!altTageszeitenwechsel.isEmpty()) {
                    alt.addAll(altNeueSaetze("währenddessen ist der Tag vergangen",
                            altTageszeitenwechsel.stream()
                                    .map(Satz::mitAnschlusswortUndFallsKeinAnschlusswortUndKeineSatzreihungMitUnd)));
                }
                if (unterOffenemHimmel
                        && bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("Die Sonne ist schon wieder am Untergehen"));
                }
                break;
            case NACHTS:
                // Man sieht nicht mehr so gut, weil die Augen sich erst
                // anpassen müssen. Eigentlich ist es für Nachtverhältnisse
                // sogar eher hell - zumindest draußen.
                if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(
                            neuerSatz(PARAGRAPH, "Darüber ist es vollständig dunkel "
                                    + "geworden. Nur noch "
                                    + "die Sterne und der Mond spenden ein wenig Licht"));
                }

                if (bewoelkung.compareTo(LEICHT_BEWOELKT) >= 0) {
                    alt.add(neuerSatz(PARAGRAPH,
                            "Es ist Nacht geworden und man sieht nicht mehr so gut"));
                }

                if (!unterOffenemHimmel
                        || bewoelkung.compareTo(Bewoelkung.BEWOELKT) >= 0) {
                    alt.add(neuerSatz("Jetzt ist es Nacht und man sieht nur noch wenig"));
                }
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();

    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altTageszeitensprungOderWechselFromTagsueberTo(
            final Bewoelkung bewoelkung,
            final Tageszeit currentTageszeit,
            final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();
        switch (currentTageszeit) {
            case ABENDS:
                alt.addAll(altTageszeitenwechsel(bewoelkung, ABENDS, unterOffenemHimmel));
                break;
            case NACHTS:
                if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz(PARAGRAPH,
                            "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                    + "die Sterne und der Mond geben etwas Licht"));
                }

                if (bewoelkung.compareTo(LEICHT_BEWOELKT) >= 0) {
                    // Man sieht nicht mehr so gut, weil die Augen sich erst
                    // anpassen müssen. Eigentlich ist es für Nachtverhältnisse
                    // sogar eher hell - zumindest draußen.
                    alt.add(neuerSatz(PARAGRAPH, "Inzwischen ist es Nacht und man",
                            "sieht nicht mehr so gut"),
                            neuerSatz(PARAGRAPH,
                                    "Es ist Nacht geworden und man sieht nicht mehr so gut"));
                }

                break;
            case MORGENS:
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altTageszeitensprungOderWechselFromAbendsTo(
            final Bewoelkung bewoelkung,
            final Tageszeit currentTageszeit,
            final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case NACHTS:
                alt.addAll(altTageszeitenwechsel(bewoelkung, NACHTS, unterOffenemHimmel));
                break;
            case MORGENS:
                break;
            case TAGSUEBER:
                if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("Es ist schon wieder heller Tag"));
                }
                break;
            default:
                throw new IllegalStateException(
                        "Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();

    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie man einen (einmaligen) Tageszeitenwechsel
     * in der Bewölkung erlebt - ggf. eine leere Collection.
     * <p>
     * Dabei hat sich in der Regel der {@link Bewoelkung}swert gar nicht geändert,
     * aber trotzdem sieht man an der Bewölkung (oder den Gestirnen), dass die
     * Tageszeit gewechselt hat: "Die Sonne geht auf" o.Ä.
     */
    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altTageszeitenwechsel(
            final Bewoelkung bewoelkung, final Tageszeit newTageszeit,
            final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altNeueSaetze(
                PARAGRAPH,
                satzDescriber
                        .altTageszeitenwechsel(bewoelkung, newTageszeit, unterOffenemHimmel)));

        switch (newTageszeit) {
            case MORGENS:
                if (unterOffenemHimmel) {
                    if (bewoelkung == Bewoelkung.WOLKENLOS) {
                        alt.add(neuerSatz("Die Sterne verblassen und die Sonne ist",
                                "am Horizont zu sehen"));
                    }
                    if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                        alt.add(du(SENTENCE, "siehst", "die Sonne aufsteigen"));
                    }

                    if (bewoelkung.compareTo(Bewoelkung.BEWOELKT) <= 0) {
                        alt.add(neuerSatz("Im Osten kündigt sich der neue Tag an"));
                    }
                }
                break;
            case TAGSUEBER:
                break;
            case ABENDS:
                break;
            case NACHTS:
                if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz(PARAGRAPH,
                            // Eigentlich fühlt es sich nur "dunkel" an! Weil
                            // vorher einmal hell war. In derselben Nacht könnte später
                            // etwas "vom Mond hell erleuchtet" o.Ä. sein.
                            "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                    + "die Sterne und der Mond spenden ein wenig Licht"),
                            neuerSatz(PARAGRAPH, "Jetzt sind am Himmel die Sterne zu",
                                    "sehen. Es ist dunkel und in der Ferne ruft ein Käuzchen"));
                }

                if (bewoelkung.compareTo(Bewoelkung.BEWOELKT) <= 0 && unterOffenemHimmel) {
                    alt.add(neuerSatz(PARAGRAPH, "Nun ist die Sonne unter"));
                }

                if (bewoelkung.compareTo(LEICHT_BEWOELKT) >= 0) {
                    alt.add(neuerSatz(PARAGRAPH,
                            // Man sieht nicht mehr so gut, weil die Augen sich erst
                            // anpassen müssen.
                            "Es ist Nacht geworden und man sieht nicht mehr so gut"));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + newTageszeit);
        }

        return alt;
    }

    /**
     * Gibt alternative Wetter-Beschreibungen zurück, wenn der SC unter offenen Himmel
     * gekommen ist
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    @CheckReturnValue
    public AltDescriptionsBuilder altKommtUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time, final boolean warVorherDrinnen,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // Wenn Inhalte für diesen Tageszeitenwechsel nicht mehrfach beschrieben werden
        // sollen (z.B. bei "schon", "inzwischen", "eben", "xyz ist passiert" etc.), dann
        // nur bei auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben schreiben!

        final AltDescriptionsBuilder alt = alt();

        // "Draußen ist der Himmel bewölkt"
        alt.addAll(satzDescriber.altKommtUnterOffenenHimmel(bewoelkung, time, warVorherDrinnen,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        alt.addAll(altSpezGestirnUnterOffenemHimmel(bewoelkung, time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        return alt;
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie man sie unter offenem Himmel
     * erlebt
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    @CheckReturnValue
    public AltDescriptionsBuilder altUnterOffenemHimmel(
            final Bewoelkung bewoelkung, final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        alt.addAll(satzDescriber.altUnterOffenemHimmel(
                bewoelkung, time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        alt.addAll(altSpezGestirnUnterOffenemHimmel(
                bewoelkung, time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        return alt;
    }

    /**
     * Gibt spezielle Beschreibungen der Gestirne zurück, wie man sie unter offenem Himmel
     * erlebt
     * - evtl. leer.
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen,
     *                                                                 die nach einem
     *                                                                 Tageszweitenwechsel nur
     *                                                                 einmalig
     *                                                                 auftreten
     */
    private AltDescriptionsBuilder altSpezGestirnUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // Wenn Inhalte für diesen Tageszeitenwechsel nicht mehrfach beschrieben werden
        // sollen (z.B. bei "schon", "inzwischen", "eben", "xyz ist passiert" etc.), dann
        // nur bei auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben schreiben!

        final AltDescriptionsBuilder alt = alt();

        if (time.kurzVorSonnenaufgang() && bewoelkung.isUnauffaellig(NACHTS)) {
            alt.addAll(mapToSet(
                    satzDescriber.altTageszeitenwechsel
                            (bewoelkung, Tageszeit.MORGENS, true),
                    sonneGehtAuf -> sonneGehtAuf.mitAdvAngabe(
                            new AdvAngabeSkopusVerbAllg("bald"))));

            alt.add(neuerSatz("die Sonne ist noch nicht wieder hervorgekommen"));
        }

        if (time.kurzNachSonnenaufgang()
                && bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            alt.add(neuerSatz(SENTENCE, "es bricht eben der erste Sonnenstrahl hervor"));
        }

        if (time.getTageszeit() == NACHTS
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            alt.add(
                    // IDEA: "die Sonne ist hinter den Bergen verschwunden"
                    //  "die Sonne ist hinter die Berge gesunken"
                    neuerSatz("Der Mond ist aufgegangen"));
        }

        if (time.mittenInDerNacht() && bewoelkung == Bewoelkung.WOLKENLOS) {
            if (auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
                alt.add(neuerSatz("Der Mond ist schon aufgestiegen"));
                alt.add(neuerSatz("Der Mond ist indessen aufgestiegen, und es ist so hell,",
                        "dass man eine Stecknadel finden könnte"));
                // IDEA: "Der Mond ist indessen über dem Berg
                // aufgestiegen, und es ist so hell, dass man eine Stecknadel finden könnte."
            }

            alt.addAll(
                    altNeueSaetze(
                            time.getTageszeit().altGestirn().stream()
                                    .map(gestirn ->
                                            // "Der Mond steht hoch"
                                            STEHEN.alsSatzMitSubjekt(gestirn)
                                                    .mitAdvAngabe(
                                                            new AdvAngabeSkopusVerbAllg(
                                                                    HOCH))),
                            "und es ist so hell, dass man eine Stecknadel finden könnte"));
        }

        return alt;
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie man sie unter offenem Himmel
     * erlebt,
     * verbunden mit einem Hinweis auf einen "schönen Morgen" oder Ähnlichem.
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    public AltDescriptionsBuilder altSchoeneTageszeit(
            final Bewoelkung bewoelkung,
            final AvTime time,
            final boolean unterOffenemHimmel,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        if (unterOffenemHimmel) {
            // "Es ist ein schöner Abend, die Sonne scheint"
            alt.addAll(altNeueSaetze(
                    np(INDEF, SCHOEN, time.getTageszeit().getNomenFlexionsspalte())
                            .alsPraedikativumPraedikat()
                            .alsSatzMitSubjekt(EXPLETIVES_ES),
                    ",",
                    satzDescriber
                            .altUnterOffenemHimmel(
                                    bewoelkung, time,
                                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)));
        } else {
            // "Es ist ein schöner Abend"
            alt.add(np(INDEF, SCHOEN, time.getTageszeit().getNomenFlexionsspalte())
                    .alsPraedikativumPraedikat()
                    .alsSatzMitSubjekt(EXPLETIVES_ES));
        }

        return alt.schonLaenger();
    }
}
