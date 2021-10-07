package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;


import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.ONE_DAY;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STECHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.satz.Konditionalsatz;
import de.nb.aventiure2.german.satz.Satz;

/**
 * Beschreibt die {@link Temperatur} als {@link AbstractDescription}s.
 */
@SuppressWarnings({"DuplicateBranchesInSwitch"})
public class TemperaturDescDescriber {
    private final TageszeitDescDescriber tageszeitDescDescriber;
    private final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber;
    private final TemperaturSatzDescriber satzDescriber;
    private final TemperaturPraedikativumDescriber praedikativumDescriber;

    public TemperaturDescDescriber(
            final TageszeitDescDescriber tageszeitDescDescriber,
            final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber,
            final TemperaturPraedikativumDescriber praedikativumDescriber,
            final TemperaturSatzDescriber satzDescriber) {
        this.tageszeitDescDescriber = tageszeitDescDescriber;
        this.tageszeitAdvAngabeWannDescriber = tageszeitAdvAngabeWannDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
        this.satzDescriber = satzDescriber;
    }


    /**
     * Gibt Alternativen zurück, die eventuell einen "Temperatursprung" oder "-wechsel"
     * beschreiben, auf jeden Fall aber einen "Tageszeitensprung"
     * (der Benutzer hat z.B. eine Weile geschlafen oder Ähnliches, und die Tageszeit
     * hat in der Zeit mehrfach gewechselt - jedenfalls ist nicht mehr als
     * 1 Tag vergangen!) - oder einen (normalen, einmaligen) Tageszeitenwechsel.
     *
     * @param currentTemperaturBeiRelevanterAenderung Falls eine Temperaturänderung
     *                                                beschrieben werden soll, so steht
     *                                                hier die lokale Temperatur nach  der
     *                                                Änderung; muss und darf nur
     *                                                angegeben sein, wenn
     *                                                lastTemperaturBeiRelevanterAenderung
     *                                                angegeben ist und muss dann
     *                                                unterschiedlich sein.
     */
    @NonNull
    @CheckReturnValue
    public AltDescriptionsBuilder altTemperaturUndTageszeitenSprungOderWechsel(
            final Tageszeit lastTageszeit,
            final AvTime currentTime,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final Temperatur currentTemperaturBeiRelevanterAenderung,
            final DrinnenDraussen drinnenDraussen) {

        checkArgument(lastTageszeit != currentTime.getTageszeit(),
                "Kein Tageszeitensprung oder -Wechsel: %s", lastTageszeit);

        if (lastTageszeit.hasNachfolger(currentTime.getTageszeit())) {
            // Es gab keine weiteren Tageszeiten dazwischen ("Tageszeitenwechsel")
            if (currentTemperaturBeiRelevanterAenderung != null) {
                return altTemperaturSprungOderWechselUndTageszeitenwechsel(
                        currentTime.getTageszeit(),
                        currentTemperaturBeiRelevanterAenderung,
                        drinnenDraussen.isDraussen());
            }

            return tageszeitDescDescriber.altWechsel(currentTime.getTageszeit(),
                    drinnenDraussen.isDraussen());
        }

        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        // Es gab weitere Tageszeiten dazwischen ("Tageszeitensprung")
        final Change<Tageszeit> tageszeitChange =
                new Change<>(lastTageszeit, currentTime.getTageszeit());
        if (currentTemperaturBeiRelevanterAenderung != null) {
            alt.addAll(altNeueSaetze(
                    tageszeitDescDescriber.altSprungOderWechsel(
                            tageszeitChange,
                            drinnenDraussen.isDraussen()),
                    SENTENCE,
                    alt(currentTemperaturBeiRelevanterAenderung,
                            generelleTemperaturOutsideLocationTemperaturRange,
                            currentTime, drinnenDraussen,
                            true)));
        } else {
            alt.addAll(tageszeitDescDescriber.altSprungOderWechsel(
                    tageszeitChange,
                    drinnenDraussen.isDraussen()));
        }

        return alt.schonLaenger();
    }

    /**
     * Gibt Alternativen zurück, die ggf. einen "Temperaturwechsel"
     * beschreiben, auf jeden Fall aber einen (normalen, einmaligen) "Tageszeitenwechsel".
     *
     * @param currentTemperatur Die  Temperatur nach  der Änderung; muss
     *                          unterschiedlich sein.
     */
    @NonNull
    @CheckReturnValue
    private AltDescriptionsBuilder altTemperaturSprungOderWechselUndTageszeitenwechsel(
            final Tageszeit newTageszeit,
            final Temperatur currentTemperatur,
            final boolean draussen) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        if (draussen) {
            alt.addAll(satzDescriber.altTemperaturSprungOderWechselUndTageszeitenwechselDraussen(
                    newTageszeit, currentTemperatur));
        } else {
            // "Ob es wohl allmählich Morgen geworden ist? Kalt ist es."
            alt.addAll(altNeueSaetze(
                    tageszeitDescDescriber.altWechsel(newTageszeit, false),
                    SENTENCE,
                    praedikativumDescriber.altAdjPhr(
                            currentTemperatur,
                            false).stream()
                            .map(a -> a.getPraedikativ(EXPLETIVES_ES)),
                    "ist es"));
        }

        return alt;
    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie die Temperatur sich eine Stufe
     * (Temperaturwechsel) oder mehrere Stufen (Temperaturspung) verändert hat.
     *
     * @param change Die Änderung der lokalen Temperatur. Es kann hier zu
     *               seltenen Fällen kommen, dass der SC diese vorherige
     *               Temperatur an
     *               diesem Ort noch gar nicht erlebt und auch gar nicht erwartet hat
     *               - z.B. wenn der SC den ganzen heißen Tag an einem kühlen Ort
     *               verbringt den kühlen Ort genau in dem Moment verlässt, wenn der
     *               Tag sich wieder abkühlt. Zurzeit berücksichtigen wir diese Fälle
     */
    public ImmutableCollection<AbstractDescription<?>> altSprungOderWechsel(
            final Change<AvDateTime> dateTimeChange,
            final WetterParamChange<Temperatur> change,
            final DrinnenDraussen drinnenDraussen,
            final boolean auchZeitwechselreferenzen) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.addAll(altNeueSaetze(
                PARAGRAPH,
                satzDescriber.altSprungOderWechsel(
                        dateTimeChange, change, drinnenDraussen, auchZeitwechselreferenzen)));

        final ImmutableSet<Konstituente> altSpWann;
        final ImmutableSet<Konstituentenfolge> altSpWannSaetze;

        if (span(dateTimeChange).shorterThan(ONE_DAY) && auchZeitwechselreferenzen) {
            final Change<AvTime> timeChange = dateTimeChange.map(AvDateTime::getTime);

            altSpWann =
                    drinnenDraussen.isDraussen() ?
                            mapToSet(
                                    tageszeitAdvAngabeWannDescriber.altSpWannDraussen(timeChange),
                                    gegenMitternacht -> gegenMitternacht
                                            .getDescription(EXPLETIVES_ES)) :
                            ImmutableSet.of();


            altSpWannSaetze = mapToSet(
                    tageszeitAdvAngabeWannDescriber
                            .altSpWannKonditionalsaetzeDraussen(timeChange),
                    Konditionalsatz::getDescription);
        } else {
            altSpWann = ImmutableSet.of();
            altSpWannSaetze = ImmutableSet.of();
        }

        if (change.getVorher().hasNachfolger(change.getNachher())) {
            // Die Temperatur ist um eine Stufe angestiegen

            switch (change.getNachher()) {
                case KLIRREND_KALT: // Kann gar nicht sein
                    // fall-through
                case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                    break;
                case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                    alt.add(neuerSatz("es hat wohl aufgehört zu frieren"));
                    break;
                case KUEHL:
                    break;
                case WARM:
                    alt.add(du("spürst", ", wie es allmählich wärmer wird"));
                    alt.addAll(altSpWann.stream()
                            .map(gegenMitternacht ->
                                    du("spürst",
                                            joinToString(gegenMitternacht),
                                            ", wie es allmählich wärmer wird")
                                            .mitVorfeldSatzglied(
                                                    joinToString(gegenMitternacht))));
                    if (!altSpWannSaetze.isEmpty()) {
                        alt.addAll(altNeueSaetze(
                                PARAGRAPH,
                                altSpWannSaetze,
                                ", spürst du, wie es allmählich wärmer wird"));
                    }
                    break;
                case RECHT_HEISS:
                    break;
                case SEHR_HEISS:
                    break;
                default:
                    throw new IllegalStateException(
                            "Unexpected Temperatur: " + change.getNachher());
            }
        } else if (change.getVorher().hasNachfolger(change.getVorher())) {
            // Die Temperatur ist um eine Stufe gesunken


            switch (change.getNachher()) {
                case KLIRREND_KALT:
                    break;
                case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                    alt.add(neuerSatz("es fängt an zu frieren"),
                            neuerSatz("es beginnt zu frieren"),
                            neuerSatz("dir beginnt der Atem zu frieren, so kalt ist es",
                                    "geworden"));
                    if (!altSpWann.isEmpty()) {
                        alt.addAll(
                                altNeueSaetze(
                                        altSpWann,
                                        ImmutableList.of(
                                                "fängt es an zu frieren",
                                                "beginnt es zu frieren",
                                                "beginnt dir der Atem zu frieren, so kalt ist es "
                                                        + "geworden")));
                    }
                    if (!altSpWannSaetze.isEmpty()) {
                        alt.addAll(
                                altNeueSaetze(
                                        PARAGRAPH,
                                        altSpWannSaetze,
                                        ",",
                                        ImmutableList.of(
                                                "fängt es an zu frieren",
                                                "beginnt es zu frieren",
                                                "beginnt dir der Atem zu frieren, so kalt ist es "
                                                        + "geworden")));

                    }
                    break;
                case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                    alt.add(neuerSatz("die Luft kühlt sich deutlich ab"));
                    if (!altSpWann.isEmpty()) {
                        alt.addAll(altNeueSaetze(altSpWann, "kühlt die Luft sich deutlich ab"));
                    }
                    if (!altSpWannSaetze.isEmpty()) {
                        alt.addAll(altNeueSaetze(
                                PARAGRAPH,
                                altSpWannSaetze,
                                ", kühlt die Luft sich deutlich ab"));
                    }
                    break;
                case KUEHL:
                    break;
                case WARM:
                    alt.add(du("spürst", ", wie es allmählich kühler wird"));
                    alt.addAll(altSpWann.stream()
                            .map(gegenMitternacht ->
                                    du("spürst",
                                            joinToString(gegenMitternacht),
                                            ", wie es allmählich kühler wird")
                                            .mitVorfeldSatzglied(
                                                    joinToString(gegenMitternacht))));
                    if (!altSpWannSaetze.isEmpty()) {
                        alt.addAll(altNeueSaetze(
                                PARAGRAPH,
                                altSpWannSaetze,
                                ", spürst du, wie es allmählich kühler wird"));
                    }
                    break;
                case RECHT_HEISS:
                    // fall-through
                case SEHR_HEISS: // Kann gar nicht sein
                    break;
                default:
                    throw new IllegalStateException(
                            "Unexpected Temperatur: " + change.getNachher());
            }
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die die Temperatur
     * beschreiben.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>> altKommtNachDraussen(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time, final boolean unterOffenenHimmel,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        // "Draußen ist es kühl"
        alt.addAll(satzDescriber.altKommtNachDraussen(temperatur, time,
                unterOffenenHimmel, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        // IDEA "es ist draußen so kalt, dass dir der Atem friert"

        alt.addAll(altSpHeuteDerTagWennDraussenSinnvoll(
                temperatur, generelleTemperaturOutsideLocationTemperaturRange,
                time, unterOffenenHimmel));

        if (unterOffenenHimmel) {
            alt.addAll(mapToList(
                    satzDescriber
                            .altSpSonnenhitzeWennHeissUndNichtNachts(temperatur, time,
                                    true),
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die die Temperatur
     * beschreiben.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>> alt(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time, final DrinnenDraussen drinnenDraussen,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        // "Es ist kühl", "es ist warmes Wetter"
        alt.addAll(satzDescriber.altOhneHeuteDerTagSonnenhitze(temperatur, time, drinnenDraussen,
                auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
                false));

        if (drinnenDraussen.isDraussen()) {
            alt.addAll(altSpHeuteDerTagWennDraussenSinnvoll(
                    temperatur, generelleTemperaturOutsideLocationTemperaturRange, time,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL));

            if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
                alt.addAll(satzDescriber
                        .altSpSonnenhitzeWennHeissUndNichtNachts(temperatur, time,
                                true));
            }
        }

        return alt.schonLaenger().build();
    }


    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä. beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AbstractDescription<?>> altSpHeuteDerTagWennDraussenSinnvoll(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time, final boolean unterOffenemHimmel) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();
        alt.addAll(altNeueSaetze(
                satzDescriber.altSpDraussenHeuteDerTagSofernSinnvoll(
                        temperatur,
                        generelleTemperaturOutsideLocationTemperaturRange,
                        time,
                        !unterOffenemHimmel)));

        if (unterOffenemHimmel && temperatur.compareTo(Temperatur.RECHT_HEISS) >= 0) {
            // "Heute ist es heiß / schönes Wetter."
            final ImmutableCollection<Satz> heuteDerTagSaetze =
                    satzDescriber.altSpDraussenHeuteDerTagSofernSinnvoll(
                            temperatur,
                            generelleTemperaturOutsideLocationTemperaturRange,
                            time, true);
            if (!heuteDerTagSaetze.isEmpty()) {
                // "Heute ist es heiß, die Sonne sticht"
                alt.addAll(altNeueSaetze(
                        heuteDerTagSaetze,
                        ",",
                        STECHEN.alsSatzMitSubjekt(SONNE)));
            }
        }

        return alt.schonLaenger().build();
    }
}
