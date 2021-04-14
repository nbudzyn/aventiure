package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEWOELKT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HOCH;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als {@link AbstractDescription}s.
 * <p>
 * Diese {@link AbstractDescription}s sind für jede Temperatur sinnvoll (wobei manchmal die
 * Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Descriptions
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class BewoelkungDescDescriber {
    private final BewoelkungSatzDescriber satzDescriber;

    public BewoelkungDescDescriber(
            final BewoelkungSatzDescriber satzDescriber) {
        this.satzDescriber = satzDescriber;
    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie man einen "Tageszeitensprung"
     * (der SC hat z.B. eine Weile geschlafen oder Ähnliches, und die Tageszeit
     * hat in der Zeit mehrfach gewechselt - jedenfalls ist nicht mehr als
     * 1 Tag vergangen!) - oder auch einfach einen (einmaligen) Tageszeitenwechsel
     * in der Bewölkung erlebt.
     * <p>
     * Dabei hat sich in der Regel der {@link Bewoelkung}swert gar nicht geändert,
     * aber trotzdem sieht man an der Bewölkung (oder den Gestirnen), dass die
     * Tageszeit "gesprungen ist" oder gewechselt hat: "Die Sonne geht auf" o.Ä.
     */
    @NonNull
    @CheckReturnValue
    public AltDescriptionsBuilder
    altTageszeitenSprungOderWechsel(
            final Bewoelkung bewoelkung,
            final Tageszeit lastTageszeit,
            final Tageszeit currentTageszeit, final boolean unterOffenemHimmel) {
        checkArgument(lastTageszeit != currentTageszeit,
                "Kein Tageszeitensprung oder -Wechsel: " + lastTageszeit);

        if (lastTageszeit.hasNachfolger(currentTageszeit)) {
            // Es gab keine weiteren Tageszeiten dazwischen ("Tageszeitenwechsel")
            return altTageszeitenwechsel(bewoelkung, currentTageszeit, unterOffenemHimmel);
        }

        // Es gab weitere Tageszeiten dazwischen ("Tageszeitensprung")

        final AltDescriptionsBuilder alt = alt();

        // "Die Sonne geht gerade auf"
        alt.addAll(mapToSet(
                satzDescriber
                        .altTageszeitenwechsel(
                                bewoelkung, currentTageszeit, unterOffenemHimmel),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))));

        switch (lastTageszeit) {
            case NACHTS:
                alt.addAll(altTageszeitenSprungOderWechselFromNachtsTo(
                        bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            case MORGENS:
                alt.addAll(
                        altTageszeitenSprungOderWechselFromMorgensTo(
                                bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            case TAGSUEBER:
                alt.addAll(
                        altTageszeitenSprungOderWechselFromTagsueberTo(
                                bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            case ABENDS:
                alt.addAll(
                        altTageszeitenSprungOderWechselFromAbendsTo(
                                bewoelkung, currentTageszeit, unterOffenemHimmel));
                break;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder
    altTageszeitenSprungOderWechselFromNachtsTo(
            final Bewoelkung bewoelkung,
            final Tageszeit currentTageszeit, final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        switch (currentTageszeit) {
            case MORGENS:
                alt.addAll(altTageszeitenwechsel(bewoelkung, MORGENS, unterOffenemHimmel));
                break;
            case TAGSUEBER:
                if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen"));
                }
                if (unterOffenemHimmel
                        && bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("Inzwischen ist es hellichter Tag"));
                }
                break;
            case ABENDS:
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }

        return alt.schonLaenger();
    }

    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder
    altTageszeitenSprungOderWechselFromMorgensTo(
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
                            // FIXME generell in altNeueSaetze etc. erlauben, dass
                            //  eine empty Collection hineingegegeben wird.
                            //  Dann sollte bei addAll nichts hinzugefügt werden!
                            //  Alle Aufrufe prüfen und ggf. vereinfachen!
                            altTageszeitenwechsel.stream()
                                    .map(s -> s.mitAnschlusswort("und"))));
                }
                if (unterOffenemHimmel
                        && bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz("Die Sonne ist schon wieder am Untergehen"));
                }
                break;
            case NACHTS:
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
    private AltDescriptionsBuilder
    altTageszeitenSprungOderWechselFromTagsueberTo(
            final Bewoelkung bewoelkung,
            final Tageszeit currentTageszeit,
            final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();
        switch (currentTageszeit) {
            case ABENDS:
                alt.addAll(altTageszeitenwechsel(bewoelkung, ABENDS, unterOffenemHimmel));
                break;
            case NACHTS:
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
    private AltDescriptionsBuilder
    altTageszeitenSprungOderWechselFromAbendsTo(
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
                if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
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
    private AltDescriptionsBuilder
    altTageszeitenwechsel(final Bewoelkung bewoelkung, final Tageszeit newTageszeit,
                          final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altNeueSaetze(
                PARAGRAPH,
                satzDescriber
                        .altTageszeitenwechsel(
                                bewoelkung, newTageszeit, unterOffenemHimmel)));

        switch (newTageszeit) {
            case MORGENS:
                if (unterOffenemHimmel) {
                    if (bewoelkung == Bewoelkung.WOLKENLOS) {
                        alt.add(neuerSatz("Die Sterne verblassen und die Sonne ist",
                                "am Horizont zu sehen"));
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
                if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                    alt.add(neuerSatz(PARAGRAPH,
                            "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                    + "die Sterne und der Mond spenden ein wenig Licht"),
                            neuerSatz(PARAGRAPH, "Jetzt sind am Himmel die Sterne zu",
                                    "sehen. Es ist dunkel und in der Ferne ruft ein Käuzchen"));
                }

                if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) >= 0) {
                    alt.add(neuerSatz(PARAGRAPH,
                            "Es ist Nacht geworden und man sieht nicht mehr so gut"));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + newTageszeit);
        }

        return alt;
    }

    @CheckReturnValue
    public AltDescriptionsBuilder altUnterOffenemHimmelStatisch(
            final Bewoelkung bewoelkung,
            final AvTime time) {
        final AltDescriptionsBuilder alt = alt();
        alt.addAll(satzDescriber.altUnterOffenemHimmelStatisch(bewoelkung, time));
        if (bewoelkung == Bewoelkung.WOLKENLOS && time.mittenInDerNacht()) {
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


        if (time.kurzVorSonnenaufgang() && bewoelkung.compareTo(BEWOELKT) <= 0) {
            alt.add(neuerSatz("die Sonne ist noch nicht wieder hervorgekommen"));
        }

        return alt;
    }

    @CheckReturnValue
    public AltDescriptionsBuilder altKommtUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time, final boolean warVorherDrinnen) {
        final AltDescriptionsBuilder alt = alt();

        // "Draußen ist der Himmel bewölkt"
        alt.addAll(satzDescriber
                .altKommtUnterOffenenHimmel(bewoelkung, time, warVorherDrinnen));
        if (bewoelkung == Bewoelkung.WOLKENLOS && time.mittenInDerNacht()) {
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

        if (time.kurzVorSonnenaufgang() && bewoelkung.compareTo(BEWOELKT) <= 0) {
            alt.add(neuerSatz("die Sonne ist noch nicht wieder hervorgekommen"));
        }

        return alt;
    }
}
