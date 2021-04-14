package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.WOLKENLOS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUESTER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GANZ;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HELLICHT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HOCH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TIEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDDAEMMERUNG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FIRMAMENT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MOND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKEN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.praedikat.Modalverb.WOLLEN;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BEGINNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EMPORSTEIGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERABSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.UNTERGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEDECKEN;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt die {@link Bewoelkung} jeweils als {@link de.nb.aventiure2.german.satz.Satz}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class BewoelkungSatzDescriber {
    private final BewoelkungPraedikativumDescriber praedikativumDescriber;

    public BewoelkungSatzDescriber(
            final BewoelkungPraedikativumDescriber praedikativumDescriber) {
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
    public ImmutableCollection<Satz>
    altTageszeitenwechsel(
            final Bewoelkung bewoelkung,
            final Tageszeit neueTageszeit, final boolean unterOffenemHimmel) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (neueTageszeit == MORGENS) {
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
                    }
                case BEWOELKT:
                    alt.add(UNTERGEHEN.alsSatzMitSubjekt(SONNE));
                    break;
                case BEDECKT:
                    break;
                default:
                    throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
            }
        }

        return alt.build();
    }

    @CheckReturnValue
    public ImmutableCollection<Satz> altKommtUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time, final boolean warVorherDrinnen) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (warVorherDrinnen) {
            alt.addAll(mapToList(altUnterOffenemHimmel(bewoelkung, time),
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));
        } else {
            alt.addAll(altUnterOffenemHimmel(bewoelkung, time));
        }

        switch (bewoelkung) {
            case WOLKENLOS:
                break;

            // FIXME "Der Mond ist indessen über dem Berg
            //  aufgestiegen, und es ist so hell, daß man eine Stecknadel finden könnte."

            // FIXME "Als der Mond kommt"

            //  IDEA "bis der Mond aufgeht", "bis der Mond aufgestiegen ist"

            //  FIXME Mondphasen?! Immer Vollmond? Nicht erwähnen? Oder Zeitreisen?
            //   "Und als der volle Mond aufgestiegen ist,"
            //   "Sobald der Vollmond sich zeigt"
            //   "Der Mond ist indessen rund und groß über dem Berg
            //   aufgestiegen, und es ist so hell, daß man eine Stecknadel hätte
            //   finden können."
            //   "bis der Vollmond aufgestiegen ist"
            //   "der Weiher liegt so ruhig wie zuvor, und nur das Gesicht des
            //    Vollmondes glänzt darauf."
            //    gestirn, gestirnschein etc.  VOLLMOND

            case LEICHT_BEWOELKT:
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


    @CheckReturnValue
    public ImmutableCollection<Satz> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        //  FIXME Mondphasen?! Immer Vollmond? Nicht erwähnen? Oder Zeitreisen?
        //   "der volle Mond steht hoch"
        //   "der Vollmond scheint", "der Vollmond ist zu sehen"
        //   "Der Mond steht rund und groß über dem Berg, und es ist so hell, dass man
        //   eine Stecknadel finden könnte."
        //   "der Vollmond steht hoch oben am... Himmel"
        //   "der Weiher liegt so ruhig wie zuvor, und nur das Gesicht des
        //    Vollmondes glänzt darauf."
        //    gestirn, gestirnschein etc.  VOLLMOND

        alt.addAll(altUnterOffenemHimmel(bewoelkung, time.getTageszeit()));

        switch (bewoelkung) {
            case WOLKENLOS:
                if (time.kurzVorSonnenuntergang()) {
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
                if (time.kurzVorSonnenuntergang()) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            // "Die Sonne will eben untergehen"
                            WOLLEN.mitLexikalischemKern(
                                    UNTERGEHEN
                                            .mitAdvAngabe(
                                                    new AdvAngabeSkopusSatz("eben")
                                            )
                            ).alsSatzMitSubjekt(gestirn)));
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
                a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(HIMMEL)));

        // "es ist ein grauer Morgen"
        // "ein schummriger Morgen"
        alt.addAll(mapToSet(praedikativumDescriber
                        .altStatischTageszeitUnterOffenenHimmelMitAdj(bewoelkung, tageszeit, INDEF),
                Praedikativum::alsEsIstSatz));

        if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
            // "es ist hellichter Tag"
            alt.add(TAG.mit(HELLICHT).alsEsIstSatz());
        }

        // IDEA: "Was (für) ein grauer Tag!"

        if (bewoelkung == WOLKENLOS || bewoelkung == LEICHT_BEWOELKT) {
            alt.addAll(tageszeit.altGestirn().stream()
                    .flatMap(gestirn ->
                            praedikativumDescriber.altOffenerHimmel(bewoelkung, tageszeit).stream()
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
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BLAU)
                        .alsSatzMitSubjekt(HIMMEL));
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
                    alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.ERHELLT)
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
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BEWOELKT)
                        .alsSatzMitSubjekt(HIMMEL));
                alt.addAll(mapToList(tageszeit.altGestirn(), gestirn ->
                        // "Der Mond ist gerade von einer dunklen Wolke bedeckt")
                        praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BEDECKT
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                        "von einer dunklen Wolke")))
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))
                                .alsSatzMitSubjekt(gestirn)));
                break;
            case BEDECKT:
                alt.add(
                        // "Düstere Wolken bedecken den ganzen Himmel"
                        BEDECKEN.mit(np(GANZ, HIMMEL))
                                .alsSatzMitSubjekt(np(INDEF, DUESTER, WOLKEN)));
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.WOLKENVERHANGEN)
                        .alsSatzMitSubjekt(HIMMEL));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }
}
