package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link AbstractDescription}.
 * <p>
 * Diese Phrasen sind für jede Temperatur und Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen werden).
 */
@SuppressWarnings({"MethodMayBeStatic"})
public class TageszeitDescDescriber {

    private final TageszeitSatzDescriber satzDescriber;

    public TageszeitDescDescriber(
            final TageszeitSatzDescriber satzDescriber) {
        this.satzDescriber = satzDescriber;
    }


    /**
     * Gibt Alternativen zurück, die innerhalb einer Tageszeit einen "zwischentageszeitlichen
     * Wechsel" beschreiben - ggf. leer.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>>
    altZwischentageszeitlicherWechsel(final Change<AvTime> change,
                                      final boolean draussen) {
        if (oClock(15).isWithin(change)) {
            return altNachmittagsWechsel(draussen);
        }

        return ImmutableSet.of();
    }

    /**
     * Gibt Alternativen zurück, die den "Nachmittags-Wechsel" beschreiben.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>> altNachmittagsWechsel(
            final boolean draussen) {
        final AltDescriptionsBuilder alt = alt();

        if (draussen) {
            alt.add(neuerSatz(PARAGRAPH, "Es ist schon weit nach Mittag"))
                    .add(neuerSatz(PARAGRAPH, "Die Mittagsstunde ist schon",
                            "lange herum"))
                    .add(neuerSatz(PARAGRAPH, "Die Mittagsstunde ist schon",
                            "lange vorbei"))
                    .add(neuerSatz(PARAGRAPH, "Längst ist es nach Mittag"));
        } else {
            alt.add(neuerSatz(PARAGRAPH, "Es ist sicher schon nach Mittag"))
                    .add(neuerSatz(PARAGRAPH, "Es wird wohl schon nach Mittag sein"))
                    .add(neuerSatz(PARAGRAPH,
                            "Die Mittagsstunde ist sicher schon lang vorbei"));
        }

        return alt.schonLaenger().build();
    }

    @NonNull
    @CheckReturnValue
    public AltDescriptionsBuilder altSprungOderWechsel(
            final Change<Tageszeit> change, final boolean draussen) {
        switch (change.getVorher()) {
            case NACHTS:
                return altSprungOderWechselFromNachtsTo(change.getNachher(), draussen);
            case MORGENS:
                return altSprungOderWechselFromMorgensTo(change.getNachher(), draussen);
            case TAGSUEBER:
                return altSprungOderWechselFromTagsueberTo(change.getNachher(), draussen);
            case ABENDS:
                return altSprungOderWechselFromAbendsTo(change.getNachher(), draussen);
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + change.getVorher());
        }
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altSprungOderWechselFromNachtsTo(
            final Tageszeit currentTageszeit, final boolean draussen) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case MORGENS:
                alt.addAll(altWechsel(MORGENS, draussen));
                break;
            case TAGSUEBER:
                alt.add(neuerSatz("Der andere Tag hat begonnen"));
                break;
            case ABENDS:
                alt.add(neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        // Hat man so im Gefühl
                        neuerSatz("Der Tag ist schon fast vorüber")
                );
                if (draussen) {
                    alt.add(neuerSatz("Inzwischen wird es schon wieder dunkel"),
                            neuerSatz("Die Sonne ist schon wieder am Untergehen"));
                }
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altSprungOderWechselFromMorgensTo(
            final Tageszeit currentTageszeit, final boolean draussen) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case TAGSUEBER:
                alt.addAll(altWechsel(TAGSUEBER, draussen));
                break;
            case ABENDS:
                if (draussen) {
                    alt.add(neuerSatz("Inzwischen wird es schon wieder dunkel",
                            neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                            neuerSatz("Der Tag ist schon fast vorüber")));
                } else {
                    alt.add(neuerSatz("Wahrscheinlich ist der Tag schon fast vorüber"));
                }
                break;
            case NACHTS:
                if (draussen) {
                    alt.add(neuerSatz("Die Sonne ist über die Zeit untergegangen"),
                            neuerSatz("Jetzt ist es dunkel"),
                            neuerSatz("Die Sonne ist jetzt untergegangen"),
                            neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"));
                } else {
                    alt.add(neuerSatz("Ob wohl die Sonne schon untergegangen ist?"),
                            neuerSatz("Jetzt ist es sicher schon dunkel!"),
                            neuerSatz(PARAGRAPH, "Gewiss ist schon Nacht"));
                }
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();

    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altSprungOderWechselFromTagsueberTo(
            final Tageszeit currentTageszeit, final boolean draussen) {
        final AltDescriptionsBuilder alt = alt();
        switch (currentTageszeit) {
            case ABENDS:
                alt.addAll(altWechsel(ABENDS, draussen));
                break;
            case NACHTS:
                if (draussen) {
                    alt.add(neuerSatz(PARAGRAPH, "Die Sonne ist jetzt untergegangen"),
                            neuerSatz(PARAGRAPH, "Es ist dunkel geworden"),
                            neuerSatz(PARAGRAPH, "Die Sonne ist untergegangen"),
                            neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"));
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

                if (draussen) {
                    alt.add(neuerSatz(PARAGRAPH, "Es ist schon der nächste Morgen"),
                            // Das hat man so im Gefühl
                            neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder",
                                    "hell geworden"));
                } else {
                    alt.add(neuerSatz(PARAGRAPH, "Es ist gewiss schon der nächste Morgen"));
                    alt.add(neuerSatz(PARAGRAPH,
                            "Wahrscheinlich ist schon der nächste Tag angebrochen"));
                }
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altSprungOderWechselFromAbendsTo(
            final Tageszeit currentTageszeit, final boolean draussen) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case NACHTS:
                alt.addAll(altWechsel(NACHTS, draussen));
                break;
            case MORGENS:
                alt.add(neuerSatz("Unterdessen hat der neue Tag begonnen", PARAGRAPH),
                        neuerSatz("Es ist schon der nächste Morgen"));
                if (draussen) {
                    alt.add(neuerSatz("Die Nacht ist vorbei und es wird schon wieder hell"),
                            neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder",
                                    "hell geworden")
                    );
                }
                break;
            case TAGSUEBER:
                if (draussen) {
                    alt.add(neuerSatz("Die Sonne ist schon wieder aufgegangen"));
                } else {
                    alt.add(neuerSatz("Es ist sicher schon der nächste Tag"));
                }
                break;
            default:
                throw new IllegalStateException(
                        "Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();

    }

    /**
     * Gibt Alternativen zurück, die den Tageszeitenwechsel beschreiben.
     */
    @NonNull
    @CheckReturnValue
    public AltDescriptionsBuilder altWechsel(
            final Tageszeit newTageszeit, final boolean draussen) {
        final AltDescriptionsBuilder alt = alt();

        if (draussen) {
            // "Langsam wird es Morgen" / "hell"
            alt.addAll(TageszeitSatzDescriber.altWechselDraussen(newTageszeit));

            alt.addAll(altNeueSaetze(
                    ImmutableList.of("allmählich", "unterdessen", "inzwischen", "derweil"),
                    "ist es",
                    npArtikellos(newTageszeit.getNomenFlexionsspalte())
                            .nomK(), // "Morgen"
                    "geworden"
                    // Der Tageszeitenwechsel ist parallel passiert.
            ));

            if (newTageszeit.getLichtverhaeltnisseDraussen() !=
                    newTageszeit.getVorgaenger().getLichtverhaeltnisseDraussen()) {
                alt.addAll(altNeueSaetze(
                        ImmutableList.of("unterdessen", "inzwischen", "derweil"),
                        "ist es",
                        newTageszeit.getLichtverhaeltnisseDraussen().getAdjektiv()
                                .getPraedikativ(EXPLETIVES_ES), // "hell"
                        "geworden"
                        // Der Tageszeitenwechsel ist parallel passiert.
                ));
            }
        } else {
            if (newTageszeit != TAGSUEBER) {
                alt.add(neuerSatz("Es dürfte wohl inzwischen",
                        npArtikellos(newTageszeit.getNomenFlexionsspalte())
                                .nomStr(), // "Morgen"
                        "geworden sein"));
            }

            alt.addAll(altNeueSaetze(PARAGRAPH, "Dein Gefühl sagt dir: ", SENTENCE,
                    TageszeitSatzDescriber.altWechselDraussen(newTageszeit)
            ));

            alt.add(neuerSatz("Ob es wohl allmählich",
                    npArtikellos(newTageszeit.getNomenFlexionsspalte()).nomK(), // "Morgen"
                    "geworden ist?"
                    // Der Tageszeitenwechsel ist parallel passiert.
            ));

            // "Ob es langsam Morgen wird?"
            alt.addAll(altNeueSaetze(
                    TageszeitSatzDescriber.altWechselDraussen(newTageszeit).stream()
                            .map(Satz::getIndirekteFrage),
                    "?"));
        }

        switch (newTageszeit) {
            case MORGENS:
                alt.add(neuerSatz("Der nächste Tag ist angebrochen"));
                // (Sowas hat man sogar drinnen im Gefühl - mindestens mal, wenn man
                // gut ausgeschlafen hat.)
                if (draussen) {
                    alt.add(neuerSatz("Langsam graut der Morgen")
                            // gilt vor allem bei bedecktem Himmel, kann aber wohl auch
                            // allgemein
                            // sagen
                    );
                } else {
                    alt.add(neuerSatz("Wahrscheinlich ist schon der nächste Tag angebrochen"));
                }

                break;
            case TAGSUEBER:
                if (draussen) {
                    alt.add(neuerSatz("Die Sonne ist aufgegangen und beginnt ihren Lauf"));
                } else {
                    alt.add(paragraph("Ob wohl schon die Sonne aufgegangen ist?"));
                }
                break;
            case ABENDS:
                // Diese Dinge spürt man sogar drinnen:
                // "Der Abend bricht an"
                alt.addAll(altNeueSaetze(
                        PARAGRAPH,
                        "Der Tag neigt sich",
                        // "und allmählich bricht der Abend an"
                        mapToSet(newTageszeit.altLangsamBeginntSaetze(),
                                s -> s.mitAnschlusswort(UND))));
                break;
            case NACHTS:
                if (draussen) {
                    alt.add(neuerSatz("Die Sonne ist jetzt untergegangen"),
                            neuerSatz(PARAGRAPH, "Inzwischen ist die Nacht hereingebrochen")
                    );
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + newTageszeit);
        }

        return alt.schonLaenger();
    }

    /**
     * Gibt Alternativen zurück wie "draußen ist es schon dunkel" - oder eine leere
     * {@link java.util.Collection}.
     */
    public ImmutableCollection<AbstractDescription<?>> altKommtNachDraussen(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        if ((time.getTageszeit() == MORGENS || time.getTageszeit() == NACHTS)
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            alt.add(paragraph("Draußen ist es derweil",
                    time.getTageszeit().getLichtverhaeltnisseDraussen().getAdjektiv()
                            .getPraedikativ(EXPLETIVES_ES), // "hell" / "dunkel"
                    "geworden"));
        }

        // "draußen ist es schon dunkel"
        alt.addAll(mapToSet(satzDescriber.altSchonBereitsNochDunkelHellDraussen(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (time.getTageszeit() == NACHTS) {
            alt.add(neuerSatz("draußen herrscht Dunkelheit"));
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt Alternativen zurück wie "draußen ist es schon dunkel" - oder eine leere
     * {@link java.util.Collection}.
     */
    public ImmutableCollection<AbstractDescription<?>> altDraussen(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        // "es ist schon dunkel", "es ist Abend"
        alt.addAll(satzDescriber.altDraussen(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        if (time.getTageszeit() == NACHTS) {
            alt.add(neuerSatz("es herrscht Dunkelheit"));
        }

        return alt.build();
    }
}