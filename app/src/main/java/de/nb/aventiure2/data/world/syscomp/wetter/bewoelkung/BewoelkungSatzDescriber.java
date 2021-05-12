package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellen;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Konditionalsatz;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.satz.ZweiSaetze;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEDECKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.WOLKENLOS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEDROHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLEIERN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLEIGRAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUESTER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUNKEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUNKLER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GANZ;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GRAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HELLICHT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HOCH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STERNENKLAR;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TIEF;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WEISS;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDDAEMMERUNG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FIRMAMENT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HORIZONT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MOND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TUPFEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKENDECKE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKENFRONT;
import static de.nb.aventiure2.german.base.Nominalphrase.BLICK_AUF_DEN_STERNENHIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.ERSTER_SONNENSTRAHL;
import static de.nb.aventiure2.german.base.Nominalphrase.ERSTER_STRAHL_DER_AUFGEHENDEN_SONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.LETZTE_ZIRREN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.HINTER_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.praedikat.Modalverb.WOLLEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_AUFBAUEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_BEWOELKEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_BEZIEHEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_VERDUESTERN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_VERDUNKELN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_ZUZIEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFREISSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFSTEIGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BEGINNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EINBRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EMPORSTEIGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERABSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERAUFDRINGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERVORBRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERVORLUGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.KOMMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SINKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.UNTERGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ZIEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEDECKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEKOMMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.FREIGEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERTREIBEN;
import static de.nb.aventiure2.german.praedikat.Witterungsverb.AUFKLAREN;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt die {@link Bewoelkung}, evtl. auch Tageszeit und tageszeitliche
 * Lichtverhältnisse, jeweils als {@link de.nb.aventiure2.german.satz.Satz}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class BewoelkungSatzDescriber {
    private final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber;
    private final BewoelkungPraedikativumDescriber praedikativumDescriber;

    public BewoelkungSatzDescriber(
            final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber,
            final BewoelkungPraedikativumDescriber praedikativumDescriber) {
        this.tageszeitAdvAngabeWannDescriber = tageszeitAdvAngabeWannDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie man einen (einmaligen) Tageszeitenwechsel
     * in der Bewölkung - ggf. eine leere Collection.
     * <p>
     * Dabei hat sich in der Regel der {@link Bewoelkung}swert gar nicht geändert,
     * aber trotzdem sieht man an der Bewölkung (oder den Gestirnen), dass die
     * Tageszeit gewechselt hat: "Die Sonne geht auf" o.Ä.
     */
    @CheckReturnValue
    ImmutableCollection<Satz> altTageszeitenwechsel(
            final Bewoelkung bewoelkung,
            final Tageszeit neueTageszeit, final boolean unterOffenemHimmel) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (neueTageszeit == MORGENS) {
            if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0 && unterOffenemHimmel) {
                alt.add(HERVORBRECHEN.alsSatzMitSubjekt(ERSTER_SONNENSTRAHL));
                alt.add(KOMMEN.alsSatzMitSubjekt(SONNE)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("nun")));
            }
            if (bewoelkung.compareTo(Bewoelkung.BEWOELKT) <= 0) {
                alt.add(AUFGEHEN.alsSatzMitSubjekt(SONNE));
            }
        } else if (neueTageszeit == TAGSUEBER
                && bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0
                && unterOffenemHimmel) {
            alt.add(EMPORSTEIGEN
                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(AN_DAT.mit(FIRMAMENT)))
                    .alsSatzMitSubjekt(SONNE)
                    .mitAdvAngabe(new AdvAngabeSkopusSatz(LANGSAM)));
        } else if (neueTageszeit == ABENDS && unterOffenemHimmel) {
            if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
                alt.add(// "inzwischen steht die Sonne schon tief"
                        VerbSubj.STEHEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbAllg(
                                                TIEF.mitAdvAngabe(
                                                        new AdvAngabeSkopusSatz(
                                                                "schon"))
                                        ))
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusSatz("inzwischen")));
            }

            if (bewoelkung.compareTo(Bewoelkung.BEWOELKT) <= 0) {
                // "die Abenddämmerung beginnt"
                alt.add(BEGINNEN.alsSatzMitSubjekt(ABENDDAEMMERUNG));
            }
        } else if (neueTageszeit == NACHTS) {
            switch (bewoelkung) {
                case WOLKENLOS:
                    // fall-through
                case LEICHT_BEWOELKT:
                    // fall-through
                    if (unterOffenemHimmel) {
                        alt.add(AUFGEHEN.alsSatzMitSubjekt(MOND));
                        alt.add(AUFSTEIGEN.alsSatzMitSubjekt(MOND));
                        // IDEA: "Der Mond steigt über dem Berg auf"
                        alt.add(KOMMEN.alsSatzMitSubjekt(MOND));
                        // IDEA: "bis der Mond aufgestiegen ist"
                    }


                    // fall-through
                case BEWOELKT:
                    alt.add(UNTERGEHEN.alsSatzMitSubjekt(SONNE));
                    alt.add(EINBRECHEN.alsSatzMitSubjekt(NACHT));
                    if (unterOffenemHimmel) {
                        alt.add(new ZweiSaetze(
                                SINKEN.alsSatzMitSubjekt(SONNE),
                                EINBRECHEN.alsSatzMitSubjekt(NACHT)));
                    }
                    break;
                case BEDECKT:
                    break;
                default:
                    throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
            }
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie die Bewölkung sich eine Stufe
     * (Bewölkungswechsel) oder mehrere Stufen (Bewölkungssprung) verändert hat.
     */
    public ImmutableCollection<Satz> altSprungOderWechselUnterOffenemHimmel(
            final AvTime lastTime, final AvDateTime time,
            final WetterParamChange<Bewoelkung> change, final boolean auchZeitwechselreferenzen) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        final int delta = change.getNachher().minus(change.getVorher());
        if (Math.abs(delta) <= 1) {
            // Die Bewölkung hat sich nur um eine Stufe verändert
            // ("Bewölkungswechsel").

            @Nullable final ImmutableSet<AdvAngabeSkopusSatz> altWann =
                    auchZeitwechselreferenzen ?
                            tageszeitAdvAngabeWannDescriber
                                    .altWannDraussen(lastTime, time.getTime()) :
                            null;

            @Nullable final ImmutableSet<Konditionalsatz> altWannSaetze =
                    auchZeitwechselreferenzen ?
                            tageszeitAdvAngabeWannDescriber.altWannKonditionalsaetzeDraussen(
                                    lastTime, time.getTime()) :
                            null;

            if (change.getVorher().hasNachfolger(change.getNachher())) {
                // Die Temperatur ist um eine Stufe angestiegen

                alt.addAll(altWechselAnstiegUnterOffenemHimmel(
                        time.getTageszeit(), change.getNachher()
                ));

                if (auchZeitwechselreferenzen) {
                    alt.addAll(altWann.stream()
                            .flatMap(gegenMitternacht ->
                                    altWechselAnstiegUnterOffenemHimmel(
                                            time.getTageszeit(), change.getNachher())
                                            .stream()
                                            .map(s -> s.mitAdvAngabe(gegenMitternacht)))
                            .collect(toSet()));
                    alt.addAll(altWannSaetze.stream()
                            .flatMap(alsDerTagAngebrochenIst ->
                                    altWechselAnstiegUnterOffenemHimmel(
                                            time.getTageszeit(), change.getNachher())
                                            .stream()
                                            .filter(s -> !s.hatAngabensatz())
                                            .map(s -> s
                                                    .mitAngabensatz(alsDerTagAngebrochenIst, true)))
                            .collect(toSet()));
                }
            } else {
                // Die Temperatur ist um eine Stufe gesunken

                alt.addAll(altWechselAbfallUnterOffenemHimmel(
                        time.getTageszeit(), change.getNachher(),
                        false));

                if (auchZeitwechselreferenzen) {
                    alt.addAll(altWann.stream()
                            .flatMap(gegenMitternacht ->
                                    altWechselAbfallUnterOffenemHimmel(
                                            time.getTageszeit(), change.getNachher(),
                                            true)
                                            .stream()
                                            .map(s -> s.mitAdvAngabe(gegenMitternacht)))
                            .collect(toSet()));
                    alt.addAll(altWannSaetze.stream()
                            .flatMap(alsDerTagAngebrochenIst ->
                                    altWechselAbfallUnterOffenemHimmel(
                                            time.getTageszeit(), change.getNachher(),
                                            true)
                                            .stream()
                                            .filter(s -> !s.hatAngabensatz())
                                            .map(s -> s
                                                    .mitAngabensatz(alsDerTagAngebrochenIst, true)))
                            .collect(toSet()));

                }
            }
        } else {
            // Es gab weitere Bewölkungsstufen dazwischen ("Bewölkungssprung")

            // "Jetzt ist der Himmel wieder wolkenlos"
            alt.addAll(mapToSet(praedikativumDescriber.altHimmelAdjPhr(
                    change.getNachher(), time.getTageszeit()),
                    a -> a.alsPraedikativumPraedikat()
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder"))
                            .alsSatzMitSubjekt(HIMMEL)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))));

            if (delta > 0) {
                if (change.getNachher() == WOLKENLOS
                        && time.getTageszeit() != NACHTS) {
                    alt.add(BLAU.mitGraduativerAngabe("vollständig").alsPraedikativumPraedikat()
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder"))
                            .alsSatzMitSubjekt(HIMMEL));
                }
                alt.addAll(mapToSet(altWechselAnstiegUnterOffenemHimmel(
                        time.getTageszeit(), change.getNachher()
                        ),
                        EinzelnerSatz::perfekt));
            } else {
                alt.addAll(mapToSet(altWechselAbfallUnterOffenemHimmel(
                        time.getTageszeit(), change.getNachher(),
                        false),
                        Satz::perfekt));

                if (change.getNachher() == BEWOELKT) {
                    // "Der Himmel hat sich stark bewölkt"
                    alt.add(SICH_BEWOELKEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg("stark"))
                            .alsSatzMitSubjekt(HIMMEL).perfekt());
                } else if (change.getNachher() == BEDECKT) {
                    // "Jetzt liegt alles unter einen bleiernen Wolkendecke"
                    alt.add(LIEGEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                    UNTER_DAT.mit(np(INDEF, BLEIERN, WOLKENDECKE))))
                                    .alsSatzMitSubjekt(Indefinitpronomen.ALLES)
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")),
                            //  "Der Himmel hat sich zugezogen"
                            SICH_ZUZIEHEN.alsSatzMitSubjekt(HIMMEL).perfekt());
                }
            }
        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Bewölkung um eine Stufe
     * gestiegen ist - unter offenem Himmel erlebt - evtl. leer.
     *
     * @param endBewoelkung Die Bewölkung nach dem Anstieg
     */
    private ImmutableCollection<EinzelnerSatz> altWechselAnstiegUnterOffenemHimmel(
            final Tageszeit tageszeit,
            final Bewoelkung endBewoelkung) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();
        switch (endBewoelkung) {
            case WOLKENLOS: // Kann gar nicht sein
                // fall-through
            case LEICHT_BEWOELKT:
                if (tageszeit != NACHTS) {
                    alt.add(BEKOMMEN.mit(np(INDEF, WEISS, TUPFEN)).perfekt()
                            .alsSatzMitSubjekt(HIMMEL));
                }
                break;
            case BEWOELKT:
                if (tageszeit != NACHTS) {
                    alt.add(DUNKLER.alsWerdenPraedikativumPraedikat().alsSatzMitSubjekt(WOLKEN));
                }

                alt.add(SICH_VERDUNKELN.alsSatzMitSubjekt(HIMMEL),
                        SICH_VERDUESTERN.alsSatzMitSubjekt(HIMMEL));
                break;
            case BEDECKT:
                alt.add(SICH_BEZIEHEN.alsSatzMitSubjekt(HIMMEL),
                        ZIEHEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                        "ganz nah über deinem Haupt hinweg"))
                                .alsSatzMitSubjekt(np(INDEF,
                                        DUNKEL.mitAdvAngabe(new AdvAngabeSkopusSatz(BEDROHLICH)),
                                        WOLKEN)));

                if (tageszeit != NACHTS) {
                    alt.add(SICH_BEZIEHEN
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                            MIT_DAT.mit(np(INDEF, GRAU, WOLKENDECKE))))
                                    .alsSatzMitSubjekt(HIMMEL),
                            SICH_BEZIEHEN
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                            MIT_DAT.mit(np(INDEF, BLEIGRAU, WOLKENDECKE))))
                                    .alsSatzMitSubjekt(HIMMEL),
                            SICH_AUFBAUEN
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz(AN_DAT.mit(HORIZONT)))
                                    .alsSatzMitSubjekt(np(INDEF, DUNKEL, WOLKENFRONT)));
                }

                if (tageszeit == NACHTS) {
                    alt.add(SICH_BEZIEHEN
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                    MIT_DAT.mit(np(INDEF, DUNKEL, WOLKENDECKE))))
                            .alsSatzMitSubjekt(HIMMEL));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Bewölkung: " + endBewoelkung);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Bewölkung um eine Stufe
     * gesunken ist - unter offenem Himmel erlebt.
     *
     * @param endBewoelkung                                           Die Bewölkung nach dem Abfall
     * @param nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete Gibt nur Sätze
     *                                                                zurück, die für eine
     *                                                                zusätztliche
     *                                                                Adverbiale Angabe mit
     *                                                                Skopus Sotz
     *                                                                (z.B. "gegen Mitternacht"
     *                                                                geeignet sind)
     */
    private ImmutableCollection<Satz> altWechselAbfallUnterOffenemHimmel(
            final Tageszeit tageszeit,
            final Bewoelkung endBewoelkung,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        switch (endBewoelkung) {
            case WOLKENLOS:
                if (tageszeit == NACHTS) {
                    alt.add(STERNENKLAR.alsWerdenPraedikativumPraedikat()
                            .alsSatzMitSubjekt(NACHT)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("jetzt")));
                } else {
                    alt.add(VERTREIBEN.mit(LETZTE_ZIRREN).alsSatzMitSubjekt(SONNE));
                }

                break;
            case LEICHT_BEWOELKT:
                alt.add(AUFKLAREN.alsSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("allmählich")));
                break;
            case BEWOELKT:
                // fall-through
            case BEDECKT: // Kann gar nicht sein
                //  "Vorsichtig lugt die Sonne / der Mond hinter einer dunklen Wolke hervor"
                alt.addAll(mapToSet(tageszeit.altGestirn(),
                        dieSonne -> HERVORLUGEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                HINTER_DAT.mit(np(INDEF, DUNKEL, WOLKE))))
                                .alsSatzMitSubjekt(dieSonne)));

                if (tageszeit == NACHTS) {
                    //  "Die düstere Wolkendecke reißt auf"
                    alt.add(AUFREISSEN.alsSatzMitSubjekt(WOLKENDECKE.mit(DUESTER)));
                    if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                        alt.add(new ZweiPraedikateOhneLeerstellen(
                                AUFREISSEN.mitAdvAngabe(
                                        new AdvAngabeSkopusVerbAllg("wieder")),
                                FREIGEBEN.mit(BLICK_AUF_DEN_STERNENHIMMEL)
                                        .mitAdvAngabe(
                                                new AdvAngabeSkopusSatz("hier und da")))
                                .alsSatzMitSubjekt(WOLKEN));
                    }
                } else {
                    alt.add(AUFREISSEN.alsSatzMitSubjekt(WOLKENDECKE.mit(GRAU)));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Bewölkung: " + endBewoelkung);
        }

        return alt.build();
    }


    /**
     * Gibt alternative Beschreibungen zurück, wenn der SC unter offenen Himmel gekommen ist
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen,
     *                                                                 die nur einmalig
     *                                                                 auftreten
     */
    @CheckReturnValue
    public ImmutableCollection<Satz> altKommtUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time, final boolean warVorherDrinnen,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // Wenn Inhalte für diesen Tageszeitenwechsel nicht mehrfach beschrieben werden
        // sollen (z.B. bei "schon", "inzwischen", "eben", "xyz ist passiert" etc.), dann
        // nur bei auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben schreiben!

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (warVorherDrinnen) {
            alt.addAll(mapToList(altUnterOffenemHimmel(bewoelkung, time,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));
        } else {
            alt.addAll(altUnterOffenemHimmel(
                    bewoelkung, time,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        //  IDEA Mondphasen?! Immer Vollmond? Nicht erwähnen? Oder "Zeitreisen"
        //   (langer Zauberschlaf, ...),
        //   so dass der SC schneller von einer Mondphase zu einer anderen kommt
        //   (falls z.B. etwas Bestimtes nur bei Vollmond geschieht)?
        //   "der volle Mond steht hoch"
        //   "der Vollmond scheint", "der Vollmond ist zu sehen"
        //   "Der Mond steht rund und groß über dem Berg, und es ist so hell, dass man
        //   eine Stecknadel finden könnte."
        //   "der Vollmond steht hoch oben am... Himmel"
        //   "Und als der volle Mond aufgestiegen ist,"
        //   "Sobald der Vollmond sich zeigt"
        //   "Der Mond ist indessen rund und groß über dem Berg
        //   aufgestiegen, und es ist so hell, daß man eine Stecknadel hätte
        //   finden können."
        //   "bis der Vollmond aufgestiegen ist"
        //   "der Weiher liegt so ruhig wie zuvor, und nur das Gesicht des
        //    Vollmondes glänzt darauf."
        //    gestirn, gestirnschein etc.  VOLLMOND

        return alt.build();
    }

    /**
     * Gibt alternative Beschreibungen unter offenem Himmel zurück.
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    @CheckReturnValue
    public ImmutableCollection<Satz> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // Wenn Inhalte für diesen Tageszeitenwechsel nicht mehrfach beschrieben werden
        // sollen (z.B. bei "schon", "inzwischen", "eben", "xyz ist passiert" etc.), dann
        // nur bei auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben schreiben!

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(altUnterOffenemHimmel(bewoelkung, time.getTageszeit()));

        switch (bewoelkung) {
            case WOLKENLOS:
                if (time.kurzVorSonnenuntergang()
                        && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            // "Die Sonne will eben untergehen"
                            WOLLEN.mitLexikalischemKern(
                                    UNTERGEHEN
                                            .mitAdvAngabe(
                                                    new AdvAngabeSkopusSatz("eben")
                                            )
                            ).alsSatzMitSubjekt(gestirn)));
                }
                if (time.mittenInDerNacht()) {
                    alt.addAll(mapToSet(time.getTageszeit().altGestirn(), gestirn ->
                            // "Der Mond steht hoch"
                            STEHEN.alsSatzMitSubjekt(gestirn)
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HOCH))));
                    alt.addAll(mapToSet(time.getTageszeit().altGestirn(), gestirn ->
                            // "Der Mond steht hoch oben am Himmel"
                            STEHEN.alsSatzMitSubjekt(gestirn)
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                            "hoch oben am Himmel"))));
                    // IDEA
                    //  - "Der Mond steht über dem Berg"
                    //    (Problem: Keine Inkonsistenzen erzeugen. Der Mond darf
                    //    nicht hüpfen - und die Sonne muss Mittags mit Süden stehen etc."
                    //  - "Der Mond steht hoch über dem Berg"
                }
                break;
            case LEICHT_BEWOELKT:
                if (time.kurzVorSonnenuntergang()
                        && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            // "Die Sonne will eben untergehen"
                            WOLLEN.mitLexikalischemKern(
                                    UNTERGEHEN
                                            .mitAdvAngabe(
                                                    new AdvAngabeSkopusSatz("eben")
                                            )
                            ).alsSatzMitSubjekt(gestirn)));
                }
                if (time.kurzNachSonnenaufgang()
                        && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
                    alt.add(HERAUFDRINGEN.alsSatzMitSubjekt(ERSTER_STRAHL_DER_AUFGEHENDEN_SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(AN_DAT.mit(HIMMEL)))
                            .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                    // Achtung: nicht "im Augenblick"!
                                    "in dem Augenblick")));
                }
                if (time.gegenMittag()) {
                    // "Die Sonne steht sehr hoch"
                    alt.add(STEHEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                    HOCH.mitGraduativerAngabe("sehr"))));
                    // "Die Sonne steht hoch am Firmament"
                    alt.add(STEHEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("hoch am Firmament")));
                    if (auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
                        // "Die Sonne steht schon hoch"
                        alt.add(STEHEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                        HOCH.mitGraduativerAngabe("sehr")
                                                .mitAdvAngabe(
                                                        new AdvAngabeSkopusSatz("schon")))));
                        // "Inzwischen steht die Sonne schon sehr hoch"
                        alt.add(STEHEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                        HOCH.mitGraduativerAngabe("sehr")
                                                .mitAdvAngabe(
                                                        new AdvAngabeSkopusSatz("schon"))))
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("inzwischen")));
                        // "Inzwischen steht die Sonne hoch am Firmament
                        alt.add(STEHEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("hoch am Firmament"))
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("inzwischen")));
                    }
                }
                break;
            case BEWOELKT:
                break;
            case BEDECKT:
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }
        return alt.build();
    }

    /**
     * Gibt alternative Beschreibungen unter offenem Himmel zurück.
     */
    @CheckReturnValue
    private ImmutableCollection<Satz> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        //  IDEA
        //   - "Der Mond glänzt auf dem Weiher"
        //   - "der Weiher liegt ruhig da und (nur) der Mond glänzt darauf."
        //   - "der Weiher liegt so ruhig wie zuvor, und nur der Mond glänzt darauf."
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "der Himmel ist wolkenlos"
        alt.addAll(mapToSet(praedikativumDescriber.altHimmelAdjPhr(bewoelkung, tageszeit),
                a -> a.alsPraedikativumPraedikat().alsSatzMitSubjekt(HIMMEL)));

        // "es ist ein grauer Morgen"
        // "ein schummriger Morgen"
        alt.addAll(mapToSet(praedikativumDescriber
                        .altStatischTageszeitUnterOffenenHimmelMitAdj(bewoelkung, tageszeit,
                                INDEF),
                Praedikativum::alsEsIstSatz));

        // IDEA: "Was (für) ein grauer Tag!"

        if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
            // "es ist hellichter Tag"
            alt.add(TAG.mit(HELLICHT).alsEsIstSatz());
            alt.addAll(tageszeit.altGestirn().stream()
                    .flatMap(gestirn ->
                            praedikativumDescriber.altOffenerHimmel(bewoelkung, tageszeit)
                                    .stream()
                                    .map(himmel ->
                                            // "die Sonne scheint von blauen Himmel herab"
                                            HERABSCHEINEN
                                                    .mitAdvAngabe(
                                                            new AdvAngabeSkopusVerbWohinWoher(
                                                                    VON.mit(himmel)))
                                                    .alsSatzMitSubjekt(gestirn)))
                    .collect(toSet()));
        }

        switch (bewoelkung) {
            case WOLKENLOS:
                if (tageszeit != NACHTS) {
                    alt.add(AdjektivOhneErgaenzungen.BLAU.alsPraedikativumPraedikat()
                            .alsSatzMitSubjekt(HIMMEL));
                }
                alt.addAll(mapToList(tageszeit.altGestirn(), gestirn ->
                        // "die Sonne scheint auf dich herab"
                        HERABSCHEINEN
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbWohinWoher(
                                                AUF_AKK
                                                        .mit(duSc())))
                                .alsSatzMitSubjekt(gestirn)));
                if (tageszeit == TAGSUEBER) {
                    alt.addAll(mapToSet(tageszeit.altGestirn(), gestirn ->
                            // "Die Sonne steht hoch am blauen Himmel"
                            STEHEN.alsSatzMitSubjekt(gestirn)
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                            "hoch am blauen Himmel"))));
                } else if (tageszeit == Tageszeit.ABENDS) {
                    alt.addAll(mapToList(tageszeit.altGestirn(), gestirn ->
                            // "es scheint noch die Sonne"
                            SCHEINEN
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbAllg("noch"))
                                    .alsSatzMitSubjekt(gestirn)));
                    // "die Sonne steht schon tief"
                    alt.add(VerbSubj.STEHEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusVerbAllg(
                                            TIEF.mitAdvAngabe(new AdvAngabeSkopusSatz("schon"))
                                    )));
                }

                if (tageszeit == Tageszeit.NACHTS
                        || tageszeit == TAGSUEBER) {
                    alt.addAll(mapToList(tageszeit.altGestirn(), gestirn ->
                            SCHEINEN
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbAllg(
                                                    "ganz helle"))
                                    .alsSatzMitSubjekt(gestirn)));

                    alt.addAll(mapToList(tageszeit.altGestirn(), gestirn ->
                            SCHEINEN
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("hell"))
                                    .alsSatzMitSubjekt(gestirn)));
                }

                if (tageszeit == Tageszeit.NACHTS) {
                    alt.add(AdjektivOhneErgaenzungen.ERHELLT.alsPraedikativumPraedikat()
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusVerbAllg("vom Mond"))
                            .alsSatzMitSubjekt(NACHT));
                }

                break;
            case LEICHT_BEWOELKT:
                alt.addAll(mapToList(tageszeit.altGestirn(), SCHEINEN::alsSatzMitSubjekt));
                if (tageszeit == TAGSUEBER) {
                    alt.addAll(mapToSet(tageszeit.altGestirn(), gestirn ->
                            // "Die Sonne steht hoch"
                            STEHEN.alsSatzMitSubjekt(gestirn)
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HOCH))));
                    alt.addAll(mapToList(tageszeit.altGestirn(), gestirn ->
                            VerbSubjObj.SCHEINEN
                                    .mit(duSc())
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbWohinWoher(
                                                    "ins Gesicht"))
                                    .alsSatzMitSubjekt(gestirn)));
                } else if (tageszeit == Tageszeit.ABENDS) {
                    // "die Sonne steht schon tief"
                    alt.add(VerbSubj.STEHEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusVerbAllg(
                                            TIEF.mitAdvAngabe(new AdvAngabeSkopusSatz("schon"))
                                    )));
                }

                break;

            case BEWOELKT:
                alt.add(AdjektivOhneErgaenzungen.BEWOELKT.alsPraedikativumPraedikat()
                        .alsSatzMitSubjekt(HIMMEL));
                alt.addAll(mapToList(tageszeit.altGestirn(),
                        gestirn -> AdjektivOhneErgaenzungen.BEDECKT
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                        "von einer dunklen Wolke")).alsPraedikativumPraedikat()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))
                                .alsSatzMitSubjekt(gestirn)));
                break;
            case BEDECKT:
                alt.add(
                        // "Düstere Wolken bedecken den ganzen Himmel"
                        BEDECKEN.mit(np(GANZ, HIMMEL))
                                .alsSatzMitSubjekt(np(INDEF, DUESTER, WOLKEN)));
                alt.add(AdjektivOhneErgaenzungen.WOLKENVERHANGEN.alsPraedikativumPraedikat()
                        .alsSatzMitSubjekt(HIMMEL));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }
}
