package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.satz.ZweiSaetze;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur.RECHT_HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLIRREND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LEIB;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumMitDat;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumWerdenMitDat;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt die {@link Temperatur} jeweils als {@link de.nb.aventiure2.german.satz.Satz}.
 * <p>
 * Diese Sätze sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturSatzDescriber {
    private final TemperaturPraedikativumDescriber praedikativumDescriber;
    private final TageszeitPraedikativumDescriber tageszeitPraedikativumDescriber;

    public TemperaturSatzDescriber(
            final TageszeitPraedikativumDescriber tageszeitPraedikativumDescriber,
            final TemperaturPraedikativumDescriber praedikativumDescriber) {
        this.tageszeitPraedikativumDescriber = tageszeitPraedikativumDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    public ImmutableCollection<Satz> altKommtNachDraussen(
            final Temperatur temperatur,
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altStatisch(temperatur, tageszeit, true),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (temperatur.isBetweenIncluding(KNAPP_UEBER_DEM_GEFRIERPUNKT, RECHT_HEISS)) {
            alt.addAll(altStatisch(temperatur, tageszeit, true));
        }

        switch (temperatur) {
            case KLIRREND_KALT:
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(dativPraedikativumWerdenMitDat(duSc()) //
                        // "dir wird"
                        .mit(KALT)
                        .alsSatz());
                break;
            case KUEHL:
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    public ImmutableCollection<EinzelnerSatz> altStatisch(
            final Temperatur temperatur,
            final Tageszeit tageszeit,
            final boolean draussen) {
        final ImmutableList.Builder<EinzelnerSatz> alt = ImmutableList.builder();

        alt.addAll(mapToList(praedikativumDescriber.altStatisch(temperatur, draussen),
                Praedikativum::alsEsIstSatz));

        if (draussen) {
            // "die Luft ist kalt"
            alt.addAll(mapToList(
                    praedikativumDescriber.altStatischLuftAdjPhr(temperatur, tageszeit),
                    a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(LUFT)));
        }

        switch (temperatur) {
            case KLIRREND_KALT:
                alt.add(LIEGEN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("in der Luft"))
                                .alsSatzMitSubjekt(npArtikellos(KLIRREND, KAELTE)),
                        // "du frierst am ganzen Leibe"
                        VerbSubj.FRIEREN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(AN_DAT.mit(LEIB)))
                                .alsSatzMitSubjekt(duSc()));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(
                        // "es friert"
                        Witterungsverb.FRIEREN.alsSatz(),
                        // "du frierst"
                        VerbSubj.FRIEREN.alsSatzMitSubjekt(duSc()),
                        // "dir ist kalt"
                        dativPraedikativumMitDat(duSc()).mit(KALT).alsSatz());
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(// "dich friert"
                        VerbOhneSubjAusserOptionalemExpletivemEs.FRIEREN
                                .mit(duSc())
                                .alsSatzMitSubjekt(null));
                break;
            case KUEHL:
                alt.add(VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(duSc())
                                .alsSatzMitSubjekt(null),
                        VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(duSc())
                                .alsSatzMitSubjekt(null)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("ein wenig")));
                alt.add(FROESTELN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("ein wenig"))
                        .alsSatzMitSubjekt(duSc()));
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    public ImmutableCollection<Satz> altStatischMitTageszeitLichtverhaeltnissen(
            final Temperatur temperatur,
            final AvTime time) {
        final ImmutableCollection.Builder<Satz> alt = ImmutableSet.builder();

        // "schon dunkel" / "dunkel"
        final ImmutableCollection<AdjPhrOhneLeerstellen> altSchonBereitsNochDunkelAdjPhr =
                tageszeitPraedikativumDescriber.schonBereitsNochDunkelHellAdjPhr(time);

        alt.addAll(
                altStatisch(temperatur, time.getTageszeit(), true)
                        .stream()
                        .flatMap(zweiterSatz ->
                                altSchonBereitsNochDunkelAdjPhr.stream()
                                        .map(schonDunkel ->
                                                new ZweiSaetze(
                                                        schonDunkel.alsEsIstSatz(),
                                                        ";",
                                                        zweiterSatz)))
                        .collect(toSet()));

        // "es ist schon dunkel und ziemlich kühl"
        alt.addAll(praedikativumDescriber.altStatisch(temperatur, true).stream()
                .flatMap(tempAdjPhr ->
                        altSchonBereitsNochDunkelAdjPhr.stream()
                                .map(schonDunkel ->
                                        new ZweiPraedikativa<>(
                                                schonDunkel, tempAdjPhr)
                                                .alsEsIstSatz()))
                .collect(toSet()));

        return alt.build();
    }

    // IDEA Beschreibungen, die erst nach einer Weile Sinn ergeben:
    //  - Die ganze Zeit über ist dir kalt
    //  - du("schmachtest", "in der Hitze")

    /**
     * Gibt zurück, ob bei dieser Temperatur zu dieser Uhrzeit Sätze über "heute" oder "den Tag"
     * sinnvoll sind. - Generell wird es noch von anderen Kriterien abhängen, wann solche
     * Sätze sinnvoll sind, z.B. wohl nur draußen.
     */
    @CheckReturnValue
    public static boolean ueberHeuteOderDenTagSinnvoll(final Temperatur temperatur,
                                                       final AvTime time) {
        return
                // Abends zu sagen "der Tag ist recht heiß" wäre unnatürlich
                TagestemperaturverlaufUtil.saetzeUeberHeuteOderDenTagVonDerUhrzeitHerSinnvoll(time)
                        // Zu sagen "der Tag so warm oder kalt wie jeder andere auch" wäre
                        // unnatürlich
                        && !temperatur.isBetweenIncluding(
                        KNAPP_UEBER_DEM_GEFRIERPUNKT, Temperatur.WARM);
    }

    /**
     * Gibt alternative Sätze zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen. Solche Sätze sind nur in gewissen Kontexten sinnvoll, insbesondere
     * nur draußen.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> altDraussenUeberHeuteOderDenTag(
            final Temperatur temperatur) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Heute ist es heiß / schönes Wetter."
        alt.addAll(
                mapToList(praedikativumDescriber.altStatisch(
                        temperatur,
                        true // Drinnen sind solche Sätze
                        // nicht sinnvoll
                ), a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));

        alt.addAll(altDerTagIst(temperatur));

        return alt.build();
    }

    /**
     * Erzeugt Sätze in der Art "Der Tag ist sehr heiß" - nur unter gewissen
     * Umständen sinnvoll, z.B. nur draußen.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Satz> altDerTagIst(final Temperatur temperatur) {
        return praedikativumDescriber.altStatisch(temperatur, true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(TAG))
                .collect(toImmutableList());
    }

    /**
     * Gibt alternative Sätze für draußen zurück in der Art
     * "Es ist noch (sehr kalt / ziemlich warm / heißes Wetter)".
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> altEsIstNochDraussen(final Temperatur temperatur) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Es ist (noch (sehr kalt))."
        alt.addAll(praedikativumDescriber.altStatisch(temperatur, true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(
                        ((AdjPhrOhneLeerstellen) a)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")))
                        .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES))
                .collect(toImmutableList()));

        // "Es ist noch warmes Wetter."
        alt.addAll(praedikativumDescriber.altStatisch(temperatur, true).stream()
                .filter(obj -> !(obj instanceof AdjPhrOhneLeerstellen))
                .map(a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("noch")))
                .collect(toImmutableList()));

        return alt.build();
    }

}
