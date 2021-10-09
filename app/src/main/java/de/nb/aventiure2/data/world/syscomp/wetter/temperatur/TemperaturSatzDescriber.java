package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.stream.Collectors.toSet;
import static de.nb.aventiure2.data.time.AvTimeSpan.ONE_DAY;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.base.Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT;
import static de.nb.aventiure2.data.world.base.Temperatur.RECHT_HEISS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUSZUHALTEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEISSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ERTRAEGLICHER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROSTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GLUEHEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLIRREND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KUEHLER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STARK;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNERWARTET;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WAERMER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WARM;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.ABER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FROST;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LEIB;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumMitDat;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumMitPraedikativum;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumWerdenMitDat;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_ABKUEHLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ABKUEHLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANBRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BEGINNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRENNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EINSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FRIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERUNTERSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.NACHLASSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Konditionalsatz;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.satz.Satzreihe;

/**
 * Beschreibt die {@link Temperatur} jeweils als {@link de.nb.aventiure2.german.satz.Satz}.
 * <p>
 * Diese Sätze sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturSatzDescriber {
    private final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber;
    private final TemperaturPraedikativumDescriber praedikativumDescriber;
    private final TageszeitPraedikativumDescriber tageszeitPraedikativumDescriber;

    public TemperaturSatzDescriber(
            final TageszeitPraedikativumDescriber tageszeitPraedikativumDescriber,
            final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber,
            final TemperaturPraedikativumDescriber praedikativumDescriber) {
        this.tageszeitPraedikativumDescriber = tageszeitPraedikativumDescriber;
        this.tageszeitAdvAngabeWannDescriber = tageszeitAdvAngabeWannDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Alternativen zurück, die einen Tageszeitenwechsel <i>draußen</i> in Kombination mit
     * einer Temperaturänderung beschreiben.
     *
     * @param currentTemperatur Die Temperatur nach  der Änderung.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<EinzelnerSatz> altTemperaturSprungOderWechselUndTageszeitenwechselDraussen(
            final Tageszeit newTageszeit,
            final Temperatur currentTemperatur) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        final ImmutableCollection<AdjPhrOhneLeerstellen>
                altAdjPhrTemperaturaenderungAuchAttributiv =
                praedikativumDescriber.altAdjPhrTemperaturanstieg(
                        currentTemperatur,
                        true);

        if (newTageszeit == TAGSUEBER) {
            // "Ein kühlerer Tag beginnt" ist missverständlich (= der Morgen? Der
            // Vormittag?)
            // "Der Morgen geht in einen kühleren Tag über"
            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchAttributiv,
                    a -> VerbSubjObj.UEBERGEHEN.mit(np(INDEF, a, TAG))
                            .alsSatzMitSubjekt(MORGEN)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz(a))));
        } else {
            // "Ein kühlerer Morgen bricht an"
            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchAttributiv,
                    a -> ANBRECHEN.alsSatzMitSubjekt(
                            np(INDEF, a, newTageszeit.getNomenFlexionsspalte()))));

            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchAttributiv,
                    a -> BEGINNEN.alsSatzMitSubjekt(
                            np(INDEF, a, newTageszeit.getNomenFlexionsspalte()))));

            final ImmutableCollection<AdjPhrOhneLeerstellen>
                    altAdjPhrTemperaturaenderungAuchNichtAttributiv =
                    praedikativumDescriber.altAdjPhrTemperaturanstieg(
                            currentTemperatur,
                            false);

            // "Der Morgen beginnt kühler"
            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchNichtAttributiv,
                    a -> BEGINNEN.alsSatzMitSubjekt(newTageszeit.getNomenFlexionsspalte())
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(a))));

            // "Allmählich hat ein immer noch kalter Morgen begonnen"
            alt.addAll(Stream.of("allmählich", "unterdessen", "inzwischen", "derweil")
                    .flatMap(allmaehlich ->
                            praedikativumDescriber.altAdjPhrTemperaturanstieg(
                                    currentTemperatur,
                                    true).stream()
                                    .map(immerNochKalt ->
                                            BEGINNEN.perfekt()
                                                    .alsSatzMitSubjekt(
                                                            np(INDEF, immerNochKalt,
                                                                    newTageszeit
                                                                            .getNomenFlexionsspalte()))
                                                    .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                                            allmaehlich))))
                    .collect(toImmutableSet()));
        }

        return alt.build();
    }


    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Temperatur sich eine Stufe
     * (Temperaturwechsel) oder mehrere Stufen (Temperaturspung) verändert hat.
     *
     * @param change Die Änderung der lokalen Temperatur vor der Änderung. Es kann hier zu
     *               seltenen Fällen kommen, dass der SC die vorherige Temperatur an
     *               diesem Ort noch gar nicht erlebt und auch gar nicht erwartet hat
     *               - z.B. wenn der SC den ganzen heißen Tag an einem kühlen Ort
     *               verbringt den kühlen Ort genau in dem Moment verlässt, wenn der
     *               Tag sich wieder abkühlt. Zurzeit berücksichtigen wir diese Fälle
     */
    public ImmutableCollection<Satz> altSprungOderWechsel(
            final Change<AvDateTime> dateTimeChange,
            final WetterParamChange<Temperatur> change,
            final DrinnenDraussen drinnenDraussen,
            final boolean auchZeitwechselreferenzen) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        final ImmutableCollection<EinzelnerSatz> altStatisch = alt(
                change.getNachher(), dateTimeChange.getNachher().getTageszeit(), drinnenDraussen,
                true);


        final int delta = change.delta();
        if (Math.abs(delta) <= 1) {
            // Die Temperatur hat sich nur um eine Stufe verändert
            // ("Temperaturwechsel").

            final ImmutableSet<AdvAngabeSkopusSatz> altSpWann;
            final ImmutableSet<Konditionalsatz> altSpWannSaetze;

            if (span(dateTimeChange).shorterThan(ONE_DAY)
                    && auchZeitwechselreferenzen && drinnenDraussen.isDraussen()) {
                final Change<AvTime> timeChange = dateTimeChange.map(AvDateTime::getTime);
                altSpWann = tageszeitAdvAngabeWannDescriber.altSpWannDraussen(timeChange);
                altSpWannSaetze = tageszeitAdvAngabeWannDescriber
                        .altSpWannKonditionalsaetzeDraussen(timeChange);
            } else {
                altSpWann = ImmutableSet.of();
                altSpWannSaetze = ImmutableSet.of();
            }

            // "Gegen Mitternacht ist es kalt"
            alt.addAll(altSpWann.stream().flatMap(gegenMitternacht -> altStatisch.stream()
                    .map(s -> s.mitAdvAngabe(gegenMitternacht)))
                    .collect(toSet()));
            // "Als der Tag angebrochen ist, ist es kalt"
            alt.addAll(altSpWannSaetze.stream()
                    .flatMap(alsDerTagAngebrochenIst -> altStatisch.stream()
                            .filter(s -> !s.hatAngabensatz())
                            .map(s -> s.mitAngabensatz(alsDerTagAngebrochenIst, true)))
                    .collect(toSet()));
            // "Gegen Mitternacht wird die Luft kalt"
            alt.addAll(altSpWann.stream().flatMap(gegenMitternacht ->
                    praedikativumDescriber
                            .altLuftAdjPhr(change.getNachher(),
                                    dateTimeChange.getNachher().getTageszeit())
                            .stream()
                            .map(a -> a.alsPraedikativumPraedikat()
                                    .mitAdvAngabe(gegenMitternacht)
                                    .alsSatzMitSubjekt(LUFT)))
                    .collect(toSet()));
            // "Als der Tag angebrochen ist, wird die Luft kalt"
            alt.addAll(altSpWannSaetze.stream().flatMap(alsDerTagAngebrochenIst ->
                    praedikativumDescriber
                            .altLuftAdjPhr(change.getNachher(),
                                    dateTimeChange.getNachher().getTageszeit())
                            .stream()
                            .map(a -> a.alsPraedikativumPraedikat()
                                    .alsSatzMitSubjekt(LUFT)
                                    .mitAngabensatz(alsDerTagAngebrochenIst, true)))
                    .collect(toSet()));

            if ((delta == -1 && change.getNachher().compareTo(Temperatur.KUEHL) <= 0)
                    || (delta == 1 && change.getNachher().compareTo(Temperatur.WARM) >= 0)) {
                // "es wird ein heißer Tag"
                alt.addAll(mapToList(
                        praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                                change.getNachher(), dateTimeChange.getNachher().getTageszeit(),
                                drinnenDraussen.isDraussen()),
                        Praedikativum::alsEsWirdSatz));

                // "eben wird es kalt"
                alt.addAll(mapToList(praedikativumDescriber.altAdjPhr(
                        change.getNachher(), false),
                        praedikativum -> praedikativum.alsEsWirdSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("eben"))));

                if (drinnenDraussen.isDraussen()) {
                    // "die Luft wird kalt"
                    alt.addAll(mapToList(
                            praedikativumDescriber
                                    .altLuftAdjPhr(change.getNachher(),
                                            dateTimeChange.getNachher().getTageszeit()),
                            a -> a.alsPraedikativumPraedikat().alsSatzMitSubjekt(LUFT)));
                }

                // "mit einem Mal friert dich"
                alt.addAll(mapToList(
                        altStatisch,
                        dichFriert -> dichFriert
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("mit einem Mal"))));
            }

            if ((delta == -1
                    && change.getNachher().compareTo(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT) <= 0)
                    || (delta == 1 && change.getNachher().compareTo(Temperatur.SEHR_HEISS) >= 0)) {
                // "es ist jetzt wirklich sehr kalt"
                alt.addAll(mapToList(
                        praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                                change.getNachher(), dateTimeChange.getNachher().getTageszeit(),
                                drinnenDraussen.isDraussen()),
                        praedikativum -> praedikativum.alsEsIstSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wirklich"))));
            }

            if (change.getVorher().hasNachfolger(change.getNachher())) {
                // Die Temperatur ist um eine Stufe angestiegen
                alt.addAll(altWechselAnstieg(
                        dateTimeChange.getNachher().getTageszeit(), change.getNachher(),
                        drinnenDraussen,
                        false
                ));

                alt.addAll(altSpWann.stream()
                        .flatMap(gegenMitternacht ->
                                altWechselAnstieg(
                                        dateTimeChange.getNachher().getTageszeit(),
                                        change.getNachher(),
                                        drinnenDraussen,
                                        true)
                                        .stream()
                                        .map(s -> s.mitAdvAngabe(gegenMitternacht)))
                        .collect(toSet()));

                alt.addAll(altSpWannSaetze.stream()
                        .flatMap(alsDerTagAngebrochenIst ->
                                altWechselAnstieg(
                                        dateTimeChange.getNachher().getTageszeit(),
                                        change.getNachher(),
                                        drinnenDraussen,
                                        true)
                                        .stream()
                                        .filter(s -> !s.hatAngabensatz())
                                        .map(s -> s
                                                .mitAngabensatz(alsDerTagAngebrochenIst, true)))
                        .collect(toSet()));
            } else {
                // Die Temperatur ist um eine Stufe gesunken
                alt.addAll(altWechselAbfall(
                        dateTimeChange.getNachher().getTageszeit(), change.getNachher(),
                        drinnenDraussen,
                        false
                ));

                if (auchZeitwechselreferenzen && drinnenDraussen.isDraussen()) {
                    alt.addAll(altSpWann.stream()
                            .flatMap(gegenMitternacht ->
                                    altWechselAbfall(
                                            dateTimeChange.getNachher().getTageszeit(),
                                            change.getNachher(),
                                            drinnenDraussen,
                                            true)
                                            .stream()
                                            .map(s -> s.mitAdvAngabe(gegenMitternacht)))
                            .collect(toSet()));

                    alt.addAll(altSpWannSaetze.stream()
                            .flatMap(alsDerTagAngebrochenIst ->
                                    altWechselAbfall(
                                            dateTimeChange.getNachher().getTageszeit(),
                                            change.getNachher(),
                                            drinnenDraussen,
                                            true)
                                            .stream()
                                            .filter(s -> !s.hatAngabensatz())
                                            .map(s -> s
                                                    .mitAngabensatz(alsDerTagAngebrochenIst, true)))
                            .collect(toSet()));
                }
            }
        } else {
            // Es gab weitere Temperaturen dazwischen ("Temperatursprung")
            alt.addAll(altSprung(dateTimeChange.getNachher(), change, drinnenDraussen));
        }

        return alt.build();
    }

    private ImmutableCollection<Satz> altSprung(final AvDateTime time,
                                                final WetterParamChange<Temperatur> change,
                                                final DrinnenDraussen drinnenDraussen) {
        final int delta = change.delta();

        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrTemperaturaenderung(
                change, false),
                Praedikativum::alsEsIstSatz));

        if ((delta < 0 && change.getNachher().compareTo(Temperatur.KUEHL) <= 0)
                || (delta > 0 && change.getNachher().compareTo(Temperatur.WARM) >= 0)) {
            // "jetzt ist es kalt"
            alt.addAll(mapToList(alt(
                    change.getNachher(), time.getTageszeit(), drinnenDraussen,
                    true),
                    satz -> satz.mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))));

            // "mittlerweile frierst du"
            alt.addAll(mapToList(alt(change.getNachher(), time.getTageszeit(), drinnenDraussen,
                    true),
                    satz -> satz.mitAdvAngabe(new AdvAngabeSkopusSatz("mittlerweile"))));

            // "längst frierst du"
            alt.addAll(mapToList(alt(change.getNachher(), time.getTageszeit(), drinnenDraussen,
                    true),
                    satz -> satz.mitAdvAngabe(new AdvAngabeSkopusSatz("längst"))));

            // "es ist ein kalt geworden", "es ist ein schöner Tag geworden"
            alt.addAll(mapToSet(
                    praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                            change.getNachher(), time.getTageszeit(),
                            drinnenDraussen.isDraussen()),
                    kalt -> kalt.alsEsWirdSatz().perfekt()));

            // "darüber ist es kalt geworden", "darüber ist es ein schöner Tag geworden"
            alt.addAll(mapToSet(
                    praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                            change.getNachher(), time.getTageszeit(),
                            drinnenDraussen.isDraussen()),
                    kalt -> kalt.alsEsWirdSatz().perfekt()
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("darüber"))));

            // "inzwischen ist es kalt geworden"
            alt.addAll(mapToSet(
                    praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                            change.getNachher(), time.getTageszeit(),
                            drinnenDraussen.isDraussen()),
                    kalt -> kalt.alsEsWirdSatz().perfekt()
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("inzwischen"))));

            if (drinnenDraussen.isDraussen()) {
                // "die Luft ist kalt geworden"
                alt.addAll(mapToSet(praedikativumDescriber.altLuftAdjPhr(
                        change.getNachher(), time.getTageszeit()),
                        kalt -> kalt.alsWerdenPraedikativumPraedikat().perfekt()
                                .alsSatzMitSubjekt(LUFT)));
            }
        } else {
            // "es ist deutlich kühler geworden, aber es ist immer noch ziemlich warm"
            alt.addAll(Stream.of("deutlich", "spürbar")
                    .flatMap(deutlich ->
                            praedikativumDescriber
                                    .altSofernExpletivesEsHoechstensImVorfeldSteht(
                                            change.getNachher(), time.getTageszeit(),
                                            drinnenDraussen.isDraussen()).stream()
                                    .map(ziemlichWarm ->
                                            new Satzreihe(
                                                    (delta < 0 ? KUEHLER : WAERMER)
                                                            .mitGraduativerAngabe(deutlich)
                                                            .alsEsWirdSatz().perfekt(),
                                                    ABER,
                                                    ziemlichWarm.alsEsIstSatz()
                                                            .mitAdvAngabe(
                                                                    new AdvAngabeSkopusVerbAllg(
                                                                            "immer noch")))))
                    .collect(toImmutableSet()));
        }

        if (delta < 0) {
            alt.add(ABKUEHLEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg("deutlich"))
                            .alsSatzMitSubjekt(EXPLETIVES_ES).perfekt(),
                    ABKUEHLEN.alsSatzMitSubjekt(LUFT).perfekt(),
                    ABKUEHLEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg("deutlich"))
                            .alsSatzMitSubjekt(NomenFlexionsspalte.WETTER).perfekt(),
                    KUEHLER.mitGraduativerAngabe("deutlich").alsEsWirdSatz().perfekt(),
                    KUEHLER.mitGraduativerAngabe("spürbar").alsEsWirdSatz().perfekt());

            if (change.getNachher().compareTo(Temperatur.WARM) >= 0) {
                // "es hat sich abgekühlt, aber es ist immer noch ziemlich warm"
                alt.addAll(mapToSet(
                        praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                                change.getNachher(), time.getTageszeit(),
                                drinnenDraussen.isDraussen()),
                        ziemlichWarm ->
                                new Satzreihe(
                                        SICH_ABKUEHLEN.perfekt()
                                                .alsSatzMitSubjekt(EXPLETIVES_ES),
                                        ABER,
                                        ziemlichWarm.alsEsIstSatz()
                                                .mitAdvAngabe(
                                                        new AdvAngabeSkopusVerbAllg(
                                                                "immer noch")))));
            }
        } else {
            alt.add(WAERMER.mitGraduativerAngabe("spürbar").alsEsWirdSatz().perfekt(),
                    WAERMER.mitGraduativerAngabe("spürbar").alsEsWirdSatz().perfekt()
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("inzwischen")),
                    WAERMER.mitGraduativerAngabe("ein gutes Stück").alsEsWirdSatz().perfekt());

            if (change.getNachher().compareTo(Temperatur.KUEHL) <= 0) {
                // "es ist ein gutes Stück wärmer geworden, aber es ist immer noch ziemlich
                // kühl"
                alt.addAll(mapToSet(
                        praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                                change.getNachher(), time.getTageszeit(),
                                drinnenDraussen.isDraussen()),
                        ziemlichKuehl ->
                                new Satzreihe(
                                        WAERMER.mitGraduativerAngabe("ein gutes Stück")
                                                .alsEsWirdSatz().perfekt(),
                                        ABER,
                                        ziemlichKuehl.alsEsIstSatz()
                                                .mitAdvAngabe(
                                                        new AdvAngabeSkopusVerbAllg(
                                                                "immer noch")))));
            }
        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Temperatur um eine Stufe
     * gestiegen ist.
     *
     * @param endTemperatur                                           Die lokale Temperatur nach
     *                                                                der Änderung
     * @param nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete Gibt nur Sätze
     *                                                                zurück, die für eine
     *                                                                zusätztliche
     *                                                                Adverbiale Angabe mit
     *                                                                Skopus Sotz
     *                                                                (z.B. "gegen Mitternacht"
     *                                                                geeignet sind)
     */
    private ImmutableCollection<Satz> altWechselAnstieg(
            final Tageszeit tageszeit,
            final Temperatur endTemperatur, final DrinnenDraussen drinnenDraussen,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrTemperaturanstieg(
                endTemperatur, false),
                Praedikativum::alsEsIstSatz));

        if (endTemperatur.compareTo(Temperatur.KUEHL) <= 0) {
            // "es wird etwas wärmer, aber es ist immer noch ziemlich kühl"
            alt.addAll(
                    altWirdEtwasWaermer().stream()
                            .flatMap(esWirdEtwasWaermer -> praedikativumDescriber
                                    .altSofernExpletivesEsHoechstensImVorfeldSteht(
                                            endTemperatur, tageszeit,
                                            drinnenDraussen.isDraussen()).stream()
                                    .map(praedikativum ->
                                            praedikativum.alsEsIstSatz()
                                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                            "immer noch")))
                                    .map(esIstImmerNochZiemlichKuehl ->
                                            new Satzreihe(
                                                    esWirdEtwasWaermer,
                                                    ABER,
                                                    esIstImmerNochZiemlichKuehl)))
                            .collect(toSet()));
        }

        switch (endTemperatur) {
            case KLIRREND_KALT:  // Kann gar nicht sein
                // fall-through
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(FRIEREN.alsSatzMitSubjekt(EXPLETIVES_ES)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wohl nicht mehr")));
                alt.add(NACHLASSEN.alsSatzMitSubjekt(FROST)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("allmählich")),
                        NACHLASSEN.perfekt()
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wohl"))
                                .alsSatzMitSubjekt(FROST));
                break;
            case KUEHL:
                alt.add(NACHLASSEN.alsSatzMitSubjekt(KAELTE.mit(UNANGENEHM)),
                        ERTRAEGLICHER.mitGraduativerAngabe("wieder")
                                .alsWerdenPraedikativumPraedikat()
                                .alsSatzMitSubjekt(KAELTE.mit(UNANGENEHM)),
                        SICH_ABKUEHLEN.alsSatzMitSubjekt(LUFT));
                if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                    alt.add(NACHLASSEN.alsSatzMitSubjekt(KAELTE.mit(UNANGENEHM)),
                            KALT.mitGraduativerAngabe("längst nicht mehr so")
                                    .alsEsIstSatz()
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")));
                }
                break;
            case WARM:
                alt.add(WARM.mitAdvAngabe(new AdvAngabeSkopusSatz(UNERWARTET)).alsEsWirdSatz(),
                        WARM.mitAdvAngabe(new AdvAngabeSkopusSatz(ANGENEHM)).alsEsWirdSatz(),
                        dativPraedikativumMitPraedikativum(WARM.mitGraduativerAngabe("wieder"))
                                .mit(duSc()).alsSatz(),
                        dativPraedikativumMitPraedikativum(WARM.mitGraduativerAngabe("wohlig"))
                                .mit(duSc())
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder")).alsSatz());
                if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                    alt.add(dativPraedikativumMitPraedikativum(WARM.mitGraduativerAngabe("wieder"))
                            .mit(duSc())
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("endlich"))
                            .alsSatz());
                }
                break;
            case RECHT_HEISS:
                alt.add(UNANGENEHM.alsWerdenPraedikativumPraedikat()
                        .alsSatzMitSubjekt(HITZE)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("allmählich")));
                break;
            case SEHR_HEISS:
                alt.add(HEFTIG.alsWerdenPraedikativumPraedikat().alsSatzMitSubjekt(HITZE),
                        AUSZUHALTEN.mitGraduativerAngabe("kaum mehr").alsPraedikativumPraedikat()
                                .alsSatzMitSubjekt(HITZE.mit(GLUEHEND))
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("jetzt")));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + endTemperatur);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Temperatur um eine Stufe
     * gesunken ist.
     *
     * @param endTemperatur                                           Die lokale Temperatur nach
     *                                                                der Änderung
     * @param nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete Gibt nur Sätze
     *                                                                zurück, die für eine
     *                                                                zusätztliche
     *                                                                Adverbiale Angabe mit
     *                                                                Skopus Sotz
     *                                                                (z.B. "gegen Mitternacht"
     *                                                                geeignet sind)
     */
    private ImmutableCollection<Satz> altWechselAbfall(
            final Tageszeit tageszeit,
            final Temperatur endTemperatur, final DrinnenDraussen drinnenDraussen,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrTemperaturabfall(
                endTemperatur, false),
                Praedikativum::alsEsIstSatz));

        if (endTemperatur.compareTo(Temperatur.WARM) >= 0) {
            // "es wird etwas kühler, aber es ist immer noch ziemlich warm"
            alt.addAll(
                    altWirdEtwasKuehler().stream()
                            .flatMap(esWirdEtwasKuehler -> praedikativumDescriber
                                    .altSofernExpletivesEsHoechstensImVorfeldSteht(
                                            endTemperatur, tageszeit,
                                            drinnenDraussen.isDraussen()).stream()
                                    .map(praedikativum ->
                                            praedikativum.alsEsIstSatz()
                                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                            "immer noch")))
                                    .map(esIstImmerNochZiemlichWarm ->
                                            new Satzreihe(
                                                    esWirdEtwasKuehler,
                                                    ABER,
                                                    esIstImmerNochZiemlichWarm)))
                            .collect(toSet()));
        }

        switch (endTemperatur) {
            case KLIRREND_KALT:
                // "eine beißende Kälte setzt ein"
                alt.add(EINSETZEN.alsSatzMitSubjekt(np(INDEF, BEISSEND, KAELTE)));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                // "eine frostige Kälte setzt ein"
                alt.add(EINSETZEN.alsSatzMitSubjekt(np(INDEF, FROSTIG, KAELTE)));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(VerbSubj.ABKUEHLEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("empfindlich"))
                                .alsSatzMitSubjekt(EXPLETIVES_ES),
                        dativPraedikativumWerdenMitDat(duSc()) //
                                // "dir wird"
                                .mit(KALT)
                                .alsSatz(),
                        // "Kälte setzt ein"
                        EINSETZEN.alsSatzMitSubjekt(npArtikellos(KAELTE)));
                if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                    if (tageszeit == NACHTS) {
                        // "Die nächtliche Kälte setzt ein"
                        alt.add(EINSETZEN.alsSatzMitSubjekt(KAELTE.mit(NAECHTLICH)));
                    }
                }
                break;
            case KUEHL:
                alt.add(VerbSubj.ABKUEHLEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("deutlich"))
                                .alsSatzMitSubjekt(EXPLETIVES_ES),
                        VerbSubj.ABKUEHLEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("spürbar"))
                                .alsSatzMitSubjekt(EXPLETIVES_ES));
                break;
            case WARM:
                alt.add(NACHLASSEN.alsSatzMitSubjekt(HITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")),
                        NACHLASSEN.alsSatzMitSubjekt(HITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("allmählich")),
                        HEISS.mitGraduativerAngabe("nicht mehr so")
                                .alsEsIstSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))
                );
                if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                    alt.add(HEISS.mitGraduativerAngabe("längst nicht mehr so")
                            .alsEsIstSatz()
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")));
                }
                break;
            case RECHT_HEISS:
                // fall-through
            case SEHR_HEISS: // Kann gar nicht sein
                alt.add(NACHLASSEN.alsSatzMitSubjekt(HITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("ein wenig")),
                        ERTRAEGLICHER.mitGraduativerAngabe("wieder")
                                .alsWerdenPraedikativumPraedikat()
                                .alsSatzMitSubjekt(HITZE)
                );
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + endTemperatur);
        }

        return alt.build();
    }

    private ImmutableCollection<EinzelnerSatz> altWirdEtwasWaermer() {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(TemperaturPraedikativumDescriber.altAdjPhrEtwasWaermer(),
                Praedikativum::alsEsWirdSatz));

        return alt.build();
    }

    private ImmutableCollection<EinzelnerSatz> altWirdEtwasKuehler() {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(TemperaturPraedikativumDescriber.altAdjPhrEtwasKuehler(),
                Praedikativum::alsEsWirdSatz));

        alt.add(VerbSubj.ABKUEHLEN.alsSatzMitSubjekt(EXPLETIVES_ES));

        return alt.build();
    }

    /**
     * Gibt Sätze zurück wie "draußen ist es kalt" - es in jedem Fall auch mindesten ein
     * {@link EinzelnerSatz} dabei.
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    public ImmutableCollection<Satz> altKommtNachDraussen(
            final Temperatur temperatur,
            final AvTime time,
            final boolean unterOffenenHimmel,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        final DrinnenDraussen kommtNachDrinnenDraussen =
                unterOffenenHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                        DRAUSSEN_GESCHUETZT;
        alt.addAll(
                mapToList(altOhneHeuteDerTagSonnenhitze(temperatur, time,
                        kommtNachDrinnenDraussen,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
                        true),
                        s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (temperatur.isBetweenIncluding(KNAPP_UEBER_DEM_GEFRIERPUNKT, RECHT_HEISS)) {
            alt.addAll(altOhneHeuteDerTagSonnenhitze(temperatur, time, kommtNachDrinnenDraussen,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
                    false));
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

    /**
     * Gibt Sätze zurück wie "es ist kalt" - es in jedem Fall auch mindesten ein
     * {@link EinzelnerSatz} dabei. Gibt ggf. auch Sätze zurück zu "heute", "der Tag"
     * oder zur Sonnenhitze.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> alt(
            final Temperatur temperatur,
            final AvTime time, final DrinnenDraussen drinnenDraussen,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
            final boolean auchHeuteDerTagWennSinnvoll) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        // "Es ist kühl", "es ist warmes Wetter"
        alt.addAll(altOhneHeuteDerTagSonnenhitze(temperatur, time, drinnenDraussen,
                auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
                false));

        if (drinnenDraussen.isDraussen()) {
            if (auchHeuteDerTagWennSinnvoll) {
                final boolean unterOffenemHimmel = drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL;
                alt.addAll(altSpDraussenHeuteDerTag(
                        temperatur,
                        time,
                        !unterOffenemHimmel));
            }

            if (drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL) {
                alt.addAll(altSpSonnenhitzeWennHeissUndNichtNachts(temperatur, time,
                        true));
            }
        }

        return alt.build();
    }

    /**
     * Gibt Sätze zurück wie "es ist kalt" - es in jedem Fall auch mindesten ein
     * {@link EinzelnerSatz} dabei. Gibt keine Sätze zurück zu "heute", "der Tag"
     * oder zur Sonnenhitze.
     *
     * @param nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete Ob der Satz auch für eine
     *                                                                zusätzliche  adverbiale
     *                                                                Angabe mit
     *                                                                Satzskopus geeignet
     *                                                                sein soll
     *                                                                (z.B.
     *                                                                "es ist kalt" -> "heute
     *                                                                ist es
     *                                                                kalt") -
     *                                                                oder es nicht sein muss
     *                                                                ("es ist warmes
     *                                                                Wetter", aber
     *                                                                ?"heute ist es warmes
     *                                                                Wetter").
     */
    @CheckReturnValue
    public ImmutableCollection<Satz> altOhneHeuteDerTagSonnenhitze(
            final Temperatur temperatur,
            final AvTime time,
            final DrinnenDraussen drinnenDraussen,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(alt(temperatur, time.getTageszeit(), drinnenDraussen,
                nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete));
        if (drinnenDraussen.isDraussen()) {
            // FIXME Alle Aufrufer prüfen - ist es gewünscht, dass hier die Tageszeit
            //  beschrieben wird? Manchmal kann es zu unerwünschten Doppelungen kommen:
            //  "Es ist dunkel und in der Ferne ruft ein Käuzchen. Es schon dunkel; die Luft ist
            //  warm"

            alt.addAll(altSpMitTageszeitLichtverhaeltnissen(temperatur, time,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                    auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben));
        }

        return alt.build();
    }

    /**
     * Erzeugt alternative Sätze zur Temperatur.
     *
     * @param nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete Ob der Satz auch für eine
     *                                                                zusätzliche  adverbiale
     *                                                                Angabe mit
     *                                                                Satzskopus geeignet sein soll
     *                                                                (z.B.
     *                                                                "es ist kalt" -> "heute ist es
     *                                                                kalt") -
     *                                                                oder es nicht sein muss
     *                                                                ("es ist warmes Wetter", aber
     *                                                                ?"heute ist es warmes
     *                                                                Wetter").
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    private ImmutableCollection<EinzelnerSatz> alt(
            final Temperatur temperatur,
            final Tageszeit tageszeit,
            final DrinnenDraussen drinnenDraussen,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        if (nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
            alt.addAll(mapToList(praedikativumDescriber.altAdjPhr(
                    temperatur, false),
                    Praedikativum::alsEsIstSatz));
        } else {
            alt.addAll(
                    mapToList(praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                            temperatur, tageszeit,
                            drinnenDraussen.isDraussen()),
                            Praedikativum::alsEsIstSatz));
        }

        if (drinnenDraussen.isDraussen()) {
            // "die Luft ist kalt"
            alt.addAll(mapToList(
                    praedikativumDescriber.altLuftAdjPhr(temperatur, tageszeit
                    ),
                    a -> a.alsPraedikativumPraedikat().alsSatzMitSubjekt(LUFT)));

            if (tageszeit == ABENDS
                    && drinnenDraussen != DRAUSSEN_UNTER_OFFENEM_HIMMEL
                    && temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
                alt.addAll(altDraussenNoch(temperatur, ABENDS));
            }
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
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück wie "es ist schon dunkel und ziemlich kühl" - oder eine leere
     * {@link java.util.Collection}.
     */
    private ImmutableCollection<Satz> altSpMitTageszeitLichtverhaeltnissen(
            final Temperatur temperatur,
            final AvTime time,
            final boolean unterOffenemHimmel,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableCollection.Builder<Satz> alt = ImmutableSet.builder();

        // "schon dunkel" / "dunkel"
        final ImmutableCollection<AdjPhrOhneLeerstellen> altSpSchonBereitsNochDunkelAdjPhr =
                tageszeitPraedikativumDescriber.altSpSchonBereitsNochDunkelHellAdjPhr(
                        time,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben
                                && (time.getTageszeit() == MORGENS
                                || time.getTageszeit() == NACHTS)
                );

        if (!altSpSchonBereitsNochDunkelAdjPhr.isEmpty()) {
            alt.addAll(
                    alt(temperatur, time.getTageszeit(),
                            unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                                    DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                            false)
                            .stream()
                            .flatMap(zweiterSatz ->
                                    altSpSchonBereitsNochDunkelAdjPhr.stream()
                                            .map(schonDunkel ->
                                                    new Satzreihe(
                                                            schonDunkel.alsEsIstSatz(),
                                                            true,
                                                            zweiterSatz)))
                            .collect(toSet()));

            // "es ist schon dunkel und ziemlich kühl"
            alt.addAll(praedikativumDescriber.altSofernExpletivesEsHoechstensImVorfeldSteht(
                    temperatur, time.getTageszeit(), true).stream()
                    .flatMap(tempAdjPhr ->
                            altSpSchonBereitsNochDunkelAdjPhr.stream()
                                    .map(schonDunkel ->
                                            new ZweiPraedikativa<>(
                                                    schonDunkel, tempAdjPhr)
                                                    .alsEsIstSatz()))
                    .collect(toSet()));
        }

        return alt.build();
    }

    // IDEA Beschreibungen, die erst nach einer Weile Sinn ergeben:
    //  - Die ganze Zeit über ist dir kalt
    //  - du("schmachtest", "in der Hitze")

    /**
     * Gibt alternative Sätze zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - sofern sinnvoll - sonst leer.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> altSpDraussenHeuteDerTagSofernSinnvoll(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time,
            final boolean sonneNichtErwaehnen) {
        if (!heuteOderDerTagSinnvoll(
                temperatur, generelleTemperaturOutsideLocationTemperaturRange, time)) {
            return ImmutableSet.of();
        }

        return altSpDraussenHeuteDerTag(temperatur, time, sonneNichtErwaehnen);
    }

    private ImmutableList<Satz> altSpDraussenHeuteDerTag(final Temperatur temperatur,
                                                         final AvTime time,
                                                         final boolean sonneNichtErwaehnen) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Heute ist es heiß."
        alt.addAll(
                mapToList(praedikativumDescriber.altAdjPhr(
                        temperatur, false), a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));

        // IDEA "Heute ist warmes Wetter", "Heute ist ein heißer Tag"
        //  "Draußen ist ein heißer Tag" (vgl.
        //  PrädikativumDescriber#altSofernExpletivesEsHoechstensImVorfeldSteht())
        //  (ohne "es"!", analog "Heute ist hitzefrei")

        alt.addAll(altDerTag(temperatur));

        if (!sonneNichtErwaehnen) {
            alt.addAll(mapToList(altSpSonnenhitzeWennHeissUndNichtNachts(temperatur, time,
                    false),
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));
        }

        return alt.build();
    }

    /**
     * Erzeugt Sätze in der Art "Der Tag ist sehr heiß" - nur unter gewissen
     * Umständen sinnvoll, z.B. nur draußen,
     * vgl. {@link #heuteOderDerTagSinnvoll(Temperatur, boolean, AvTime)}.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Satz> altDerTag(final Temperatur temperatur) {
        return praedikativumDescriber.altAdjPhr(temperatur, false).stream()
                .map(a -> a.alsPraedikativumPraedikat().alsSatzMitSubjekt(TAG))
                .collect(toImmutableList());
    }

    /**
     * Gibt zurück, ob bei dieser Temperatur zu dieser Uhrzeit Sätze über "heute" oder "den Tag"
     * sinnvoll sind. - Generell wird es noch von anderen Kriterien abhängen, wann solche
     * Sätze sinnvoll sind, z.B. wohl nur draußen.
     */
    @CheckReturnValue
    private boolean heuteOderDerTagSinnvoll(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time) {
        return // Wenn man das volle Extrem der Temperatur nicht spürt, kein "der Tag ist"!
                !generelleTemperaturOutsideLocationTemperaturRange
                        // Abends zu sagen "der Tag ist recht heiß" wäre unnatürlich
                        && TagestemperaturverlaufUtil
                        .saetzeUeberHeuteOderDenTagVonDerUhrzeitHerSinnvoll(time)
                        // Zu sagen "der Tag so warm oder kalt wie jeder andere auch" wäre
                        // unnatürlich
                        && !temperatur.isBetweenIncluding(
                        KNAPP_UEBER_DEM_GEFRIERPUNKT, Temperatur.WARM);
    }

    /**
     * Gibt alternative Sätze zurück mit kombinierte Adjektivphrasen, die auf die
     * schöne Tageszeit referenzieren: "Es ist ein schöner Abend und noch ziemlich warm",
     * "Es ist ein schöner Tag, aber eiskalt[,]" o.Ä.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableList<EinzelnerSatz> altSchoneTageszeitUndAberSchonNochAdjPhr(
            final Temperatur temperatur, final Tageszeit tageszeit) {
        return mapToList(praedikativumDescriber.altSchoneTageszeitUndAberSchonNochAdjPhr(
                temperatur, tageszeit),
                Praedikativum::alsEsIstSatz);
    }

    /**
     * Gibt alternative Sätze für draußen zurück in der Art
     * "Es ist noch (sehr kalt / ziemlich warm / heißes Wetter)".
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<EinzelnerSatz> altDraussenNoch(final Temperatur temperatur,
                                                              final Tageszeit tageszeit) {
        final ImmutableList.Builder<EinzelnerSatz> alt = ImmutableList.builder();

        // "Es ist (noch (sehr kalt))."
        alt.addAll(praedikativumDescriber
                .altSofernExpletivesEsHoechstensImVorfeldSteht(temperatur, tageszeit, true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> ((AdjPhrOhneLeerstellen) a)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")).alsPraedikativumPraedikat()
                        .alsSatzMitSubjekt(EXPLETIVES_ES))
                .collect(toImmutableList()));

        // "Es ist noch warmes Wetter."
        alt.addAll(praedikativumDescriber
                .altSofernExpletivesEsHoechstensImVorfeldSteht(temperatur, tageszeit, true).stream()
                .filter(obj -> !(obj instanceof AdjPhrOhneLeerstellen))
                .map(a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("noch")))
                .collect(toImmutableList()));

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück über die Sonnenhitze - wenn es entsprechend
     * heiß ist (und nicht nachts).
     * <p>
     * (Solche Sätze sind nur bei geringer Bewölkung sinnvoll, nur draußen und sicher
     * nicht nachts).
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<Satz> altSpSonnenhitzeWennHeissUndNichtNachts(
            final Temperatur temperatur,
            final AvTime time, final boolean auchMitBezugAufKonkreteTageszeit) {
        if (time.getTageszeit() == NACHTS) {
            return ImmutableSet.of();
        }

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
            alt.add(SCHEINEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                    WARM.mitGraduativerAngabe("sehr"))),
                    SCHEINEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)),
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

        return alt.build();
    }

    /**
     * Gibt Sätze zurück wie "hier ist es angenehm kühl".
     */
    @CheckReturnValue
    public ImmutableCollection<Satz> altDeutlicherUnterschiedZuVorLocation(
            final Temperatur temperatur, final int delta) {
        return mapToList(
                praedikativumDescriber
                        .altAdjPhrDeutlicherUnterschiedZuVorLocation(temperatur, delta),
                adjPhr -> adjPhr.alsPraedikativumPraedikat()
                        .alsSatzMitSubjekt(EXPLETIVES_ES)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("hier")));
    }
}
