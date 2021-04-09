package de.nb.aventiure2.data.world.syscomp.wetter;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
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
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEZOGEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUESTER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GANZ;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GETRUEBT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HELL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SANFT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TIEF;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TRUEB;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WOLKENVERHANGEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDDAEMMERUNG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DAEMMERLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUESTERNIS;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FIRMAMENT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HALBDUNKEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MOND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGENLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SCHUMMERLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STERNENLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKEN_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ZWIELICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;
import static de.nb.aventiure2.german.praedikat.Modalverb.WOLLEN;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BEGINNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EMPORSTEIGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERABSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.UNTERGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEDECKEN;
import static de.nb.aventiure2.util.StreamUtil.*;

@SuppressWarnings("DuplicateBranchesInSwitch")
public enum Bewoelkung implements Betweenable<Bewoelkung> {
    // Reihenfolge ist relevant, nicht ändern!
    WOLKENLOS,
    LEICHT_BEWOELKT,
    BEWOELKT,
    BEDECKT;

    /**
     * Erzeugt ALternativen wie "Die Sonne geht auf" - ggf. auch eine leere Collection.
     */
    @CheckReturnValue
    ImmutableCollection<Satz> altGestirnbewegungUndHimmelaenderungenBeiTageszeitenWechselSaetzeSofernSichtbar(
            final Tageszeit neueTageszeit, final boolean unterOffenemHimmel) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (neueTageszeit == MORGENS) {
            if (compareTo(Bewoelkung.BEWOELKT) <= 0) {
                alt.add(AUFGEHEN.alsSatzMitSubjekt(SONNE));
            }
        } else if (neueTageszeit == TAGSUEBER
                && compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0
                && unterOffenemHimmel) {
            alt.add(EMPORSTEIGEN
                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(AN_DAT.mit(FIRMAMENT)))
                    .alsSatzMitSubjekt(SONNE)
                    .mitAdvAngabe(new AdvAngabeSkopusSatz(LANGSAM)));
        } else if (neueTageszeit == ABENDS && unterOffenemHimmel) {
            if (compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
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

            if (compareTo(Bewoelkung.BEWOELKT) <= 0) {
                // "die Abenddämmerung beginnt"
                alt.add(BEGINNEN.alsSatzMitSubjekt(ABENDDAEMMERUNG));
            }
        } else if (neueTageszeit == NACHTS) {
            switch (this) {
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
                    throw new IllegalStateException("Unexpected Bewoelkung: " + this);
            }
        }

        return alt.build();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    ImmutableCollection<Satz> altScKommtUnterOffenenHimmel(final AvTime time) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altUnterOffenemHimmelStatischeBeschreibungSaetze(time),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        switch (this) {
            case WOLKENLOS:
                break;

            // FIXME "Der Mond ist indessen über dem Berg
            //  aufgestiegen, und es ist so hell, daß man eine Stecknadel finden könnte."

            // FIXME "Als der Mond kommt"

            //  FIXME "bis der Mond aufgeht"
            //   "bis der Mond aufgestiegen ist"
            //   "der Weiher liegt so ruhig wie zuvor, und nur der Mond glänzt darauf."

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
                alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                        // "Der Mond ist gerade von einer dunklen Wolke bedeckt")
                        praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BEDECKT
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                        "von einer dunklen Wolke")))
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))
                                .alsSatzMitSubjekt(gestirn)));
                break;
            case BEDECKT:
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    @CheckReturnValue
    ImmutableCollection<Satz> altUnterOffenemHimmelStatischeBeschreibungSaetze(
            final AvTime time) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        switch (this) {
            case WOLKENLOS:
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BLAU)
                        .alsSatzMitSubjekt(HIMMEL));
                alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                        // "die Sonne scheint auf dich herab"
                        HERABSCHEINEN
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbWohinWoher(
                                                AUF_AKK
                                                        .mit(duSc())))
                                .alsSatzMitSubjekt(gestirn)));
                if (time.getTageszeit() == Tageszeit.ABENDS) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
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
                }

                if (time.getTageszeit() == Tageszeit.NACHTS
                        || time.getTageszeit() == TAGSUEBER) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            SCHEINEN
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbAllg(
                                                    "ganz helle"))
                                    .alsSatzMitSubjekt(gestirn)));

                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            SCHEINEN
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("hell"))
                                    .alsSatzMitSubjekt(gestirn)));
                }

                if (time.getTageszeit() == Tageszeit.NACHTS) {
                    alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.ERHELLT)
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusVerbAllg("vom Mond"))
                            .alsSatzMitSubjekt(NACHT));
                }

                break;

            case LEICHT_BEWOELKT:
                alt.addAll(
                        mapToList(time.getTageszeit().altGestirn(),
                                SCHEINEN::alsSatzMitSubjekt));
                if (time.getTageszeit() == TAGSUEBER) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            VerbSubjObj.SCHEINEN
                                    .mit(duSc())
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbWohinWoher(
                                                    "ins Gesicht"))
                                    .alsSatzMitSubjekt(gestirn)));
                } else if (time.getTageszeit() == Tageszeit.ABENDS) {
                    // "die Sonne steht schon tief"
                    alt.add(VerbSubj.STEHEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusVerbAllg(
                                            TIEF.mitAdvAngabe(new AdvAngabeSkopusSatz("schon"))
                                    )));
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
                }

                break;

            case BEWOELKT:
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BEWOELKT)
                        .alsSatzMitSubjekt(HIMMEL));
                break;
            case BEDECKT:
                alt.add(
                        // "Düstere Wolken bedecken den ganzen Himmel"
                        BEDECKEN.mit(np(GANZ, HIMMEL))
                                .alsSatzMitSubjekt(np(DUESTER, WOLKEN_OHNE_ART)));
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.WOLKENVERHANGEN)
                        .alsSatzMitSubjekt(HIMMEL));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    boolean isUnauffaellig(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return compareTo(BEWOELKT) <= 0;
        }

        return compareTo(LEICHT_BEWOELKT) <= 0;
    }

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausUnterOffenenHimmel(
            final AvTime time) {
        return altWohinHinausUnterOffenenHimmel(time.getTageszeit());
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausUnterOffenenHimmel
            (
                    final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        alt.addAll(mapToList(altLichtInDemEtwasLiegt(tageszeit, true),
                licht -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(licht))));

        switch (this) {
            case WOLKENLOS:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in den klaren Morgen"));
                // fall-through
            case LEICHT_BEWOELKT:
                alt.addAll(mapToList(tageszeit.altGestirnschein(),
                        schein -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(schein))));

                if (tageszeit == Tageszeit.MORGENS) {
                    alt.add(new AdvAngabeSkopusVerbWohinWoher("in den hellen Morgen"));
                } else if (tageszeit == TAGSUEBER) {
                    alt.add(new AdvAngabeSkopusVerbWohinWoher("in den hellen Tag"));
                }
                break;
            case BEWOELKT:
                alt.addAll(altWohinHinausBewoelkt(tageszeit));
                break;
            case BEDECKT:
                alt.addAll(altWohinHinausBedeckt(tageszeit));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausBewoelkt(
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<String> res = ImmutableSet.builder();

        switch (tageszeit) {
            case MORGENS:
                res.add("in den schummrigen Morgen");
                break;
            case TAGSUEBER:
                res.add("in den Tag");
                break;
            case ABENDS:
                res.add("in den dunklen Abend");
                break;
            case NACHTS:
                res.add("in die dunkle Nacht");
                break;
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + this);
        }

        return mapToSet(res.build(), AdvAngabeSkopusVerbWohinWoher::new);
    }

    public static ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausBedeckt(
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<String> res = ImmutableSet.builder();

        switch (tageszeit) {
            case MORGENS:
                res.add("in den trüben Morgen", "in den düsteren Morgen",
                        "in den grauen Morgen", "in den lichtlosen Morgen",
                        "in den verhangenen Morgen");
                break;
            case TAGSUEBER:
                res.add("in den trüben Tag", "in den düsteren Tag",
                        "in den grauen Tag");
                break;
            case ABENDS:
                res.add("in den düsteren Abend",
                        "in den dunklen Abend", "in den schummrigen Abend");
                break;
            case NACHTS:
                res.add("in die dunkle Nacht", "in die stockdunkle Nacht");
                break;
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }

        return mapToSet(res.build(), AdvAngabeSkopusVerbWohinWoher::new);
    }

    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final Tageszeit tageszeit,
                                                          final boolean unterOffenemHimmel) {
        return altImLicht(tageszeit, unterOffenemHimmel, BEI_DAT.mit(LICHT_OHNE_ART));
    }

    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final Tageszeit tageszeit,
                                                               final boolean unterOffenemHimmel) {
        return altImLicht(tageszeit, unterOffenemHimmel, BEI_DAT.mit(TAGESLICHT_OHNE_ART));
    }

    private ImmutableSet<Praepositionalphrase> altImLicht(
            final Tageszeit tageszeit,
            final boolean unterOffenemHimmel,
            final Praepositionalphrase alternative) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();
        alt.add(alternative);
        alt.addAll(
                mapToSet(altLichtInDemEtwasLiegt(tageszeit, unterOffenemHimmel), IN_DAT::mit));
        return alt.build();
    }

    // FIXME "beim Mondschimmer"
    // FIXME "der Hügel liegt in einsamem Mondschein."

    ImmutableSet<Praepositionalphrase> altUnterOffenemHimmel(
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(altOffenerHimmel(tageszeit), UNTER_DAT::mit));
        alt.addAll(mapToSet(altLichtInDemEtwasLiegt(tageszeit, true), IN_DAT::mit));

        if (this == LEICHT_BEWOELKT) {
            alt.addAll(mapToList(tageszeit.altGestirnschein(), IN_DAT::mit));
        }

        return alt.build();
    }

    /**
     * Gibt alternative substantivische Phrasen zurück, die den (offenen) Himmel
     * beschreiben - <i>Ergebnis kann leer sein</i>.
     */
    ImmutableSet<EinzelneSubstantivischePhrase> altOffenerHimmel(
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(altAdjPhrHimmel(tageszeit), HIMMEL::mit));

        if (this == WOLKENLOS) {
            alt.addAll(tageszeit.altWolkenloserHimmel());
        }

        return alt.build();
    }


    /**
     * Gibt alternative Adjektivphrasen zurück, die den (offenen) Himmel
     * beschreiben - <i>Ergebnis kann leer sein</i>.
     */
    private ImmutableSet<AdjPhrOhneLeerstellen> altAdjPhrHimmel(
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<AdjPhrOhneLeerstellen> alt = ImmutableSet.builder();

        switch (this) {
            case WOLKENLOS:
                // kann leer sein
                alt.addAll(tageszeit.altAltAdjPhrWolkenloserHimmel());
                break;
            case LEICHT_BEWOELKT:
                // bleibt leer
                break;
            case BEWOELKT:
                alt.add(AdjektivOhneErgaenzungen.BEWOELKT);
                break;
            case BEDECKT:
                alt.add(AdjektivOhneErgaenzungen.BEDECKT,
                        BEZOGEN, WOLKENVERHANGEN);
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final Tageszeit tageszeit, final boolean unterOffenemHimmel) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        switch (this) {
            case WOLKENLOS:
                if (unterOffenemHimmel) {
                    if (tageszeit == TAGSUEBER) {
                        alt.add(np(HELL, TAGESLICHT));
                    } else if (tageszeit == NACHTS) {
                        alt.add(STERNENLICHT);
                    }
                }
                // fall-through
            case LEICHT_BEWOELKT:
                switch (tageszeit) {
                    case MORGENS:
                        alt.add(MORGENLICHT);
                        break;
                    case TAGSUEBER:
                        alt.add(TAGESLICHT);
                        break;
                    case ABENDS:
                        alt.add(ABENDLICHT);
                        break;
                    case NACHTS:
                        alt.add(MONDLICHT);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected tageszeit: " + tageszeit);
                }
                break;
            case BEWOELKT:
                alt.addAll(altLichtInDemEtwasLiegtBewoelkt(tageszeit));
                break;
            case BEDECKT:
                alt.addAll(altLichtInDemEtwasLiegtBedeckt(tageszeit));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    static ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegtBewoelkt(
            final Tageszeit tageszeit) {
        switch (tageszeit) {
            case MORGENS:
                return ImmutableSet.of(np(SANFT, MORGENLICHT));
            case TAGSUEBER:
                return ImmutableSet.of(np(SANFT, TAGESLICHT), np(GETRUEBT, TAGESLICHT));
            case ABENDS:
                return ImmutableSet.of(SCHUMMERLICHT, DAEMMERLICHT);
            case NACHTS:
                return ImmutableSet.of(np(SANFT, MONDLICHT));
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }
    }

    static ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegtBedeckt(
            final Tageszeit tageszeit) {
        switch (tageszeit) {
            case MORGENS:
                return ImmutableSet.of(np(TRUEB, DAEMMERLICHT));
            case TAGSUEBER:
                return ImmutableSet.of(np(TRUEB, LICHT));
            case ABENDS:
                return ImmutableSet.of(ZWIELICHT, HALBDUNKEL);
            case NACHTS:
                return ImmutableSet.of(DUNKEL, DUNKELHEIT, DUESTERNIS);
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }
    }
}