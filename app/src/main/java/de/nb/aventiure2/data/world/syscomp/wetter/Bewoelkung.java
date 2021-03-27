package de.nb.aventiure2.data.world.syscomp.wetter;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUESTERNIS;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HALBDUNKEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGENLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SCHUMMERLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ZWIELICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.BEDECKTER_HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.BEWOELKTER_HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.BEZOGENER_HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.DAEMMERLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.DUESTERE_WOLKEN;
import static de.nb.aventiure2.german.base.Nominalphrase.GANZER_HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.GETRUEBTES_TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.HELLES_TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SANFTES_MONDLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SANFTES_MORGENLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SANFTES_TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.STERNENLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.TRUEBES_DAEMMERLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.TRUEBES_LICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.WOLKENVERHANGENER_HIMMEL;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Personalpronomen.get;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;
import static de.nb.aventiure2.german.praedikat.Modalverb.WOLLEN;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERABSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.UNTERGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEDECKEN;
import static de.nb.aventiure2.util.StreamUtil.*;

public enum Bewoelkung implements Betweenable<Bewoelkung> {
    // Reihenfolge ist relevant, nicht ändern!
    WOLKENLOS,
    LEICHT_BEWOELKT,
    BEWOELKT,
    BEDECKT;

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    ImmutableCollection<Satz> altScKommtNachDraussenSaetze(final AvTime time) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altStatischeBeschreibungSaetze(time),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        switch (this) {
            case WOLKENLOS:
                break;

            // FIXME MACHT NUR SINN, WENN ES EINE ÄNDERUNG GEGENÜBER
            //  DEM LETZTEN INFORMATIONSSTAND IST "Der Mond ist aufgegangen"
            //  "Der Mond ist schon aufgestiegen"

            // FIXME "Der Mond ist indessen rund und groß über dem Berg
            //  aufgestiegen, und es ist so hell, daß man eine Stecknadel hätte
            //  finden können."

            //  FIXME "Und als der volle Mond aufgestiegen ist,"
            //   "Als der Mond kommt"
            //   "Sobald der Vollmond sich zeigt"

            //  FIXME "bis der Mond aufgeht"
            //   "bis der Vollmond aufgestiegen ist"
            //   "der Weiher liegt so ruhig wie zuvor, und nur das Gesicht des
            //    Vollmondes glänzt darauf."
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
    ImmutableCollection<Satz> altStatischeBeschreibungSaetze(final AvTime time) {
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
                                                        .mit(get(P2, M))))
                                .alsSatzMitSubjekt(gestirn)));
                if (time.getTageszeit() == Tageszeit.ABENDS) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            // "noch scheint die Sonne"
                            SCHEINEN
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbAllg("noch"))
                                    .alsSatzMitSubjekt(gestirn)));
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
                                    .mitAdvAngabe(
                                            // FIXME Hier kann es zu
                                            //  widersprüchen zwischen
                                            //  nachts "dunkel" und Mond "hell"
                                            //  kommen.
                                            new AdvAngabeSkopusVerbAllg("hell"))
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
                        mapToList(time.getTageszeit().altGestirn(), SCHEINEN::alsSatzMitSubjekt));
                if (time.getTageszeit() == TAGSUEBER) {
                    alt.addAll(mapToList(time.getTageszeit().altGestirn(), gestirn ->
                            VerbSubjObj.SCHEINEN
                                    .mit(get(P2, M))
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbWohinWoher(
                                                    "ins Gesicht"))
                                    .alsSatzMitSubjekt(gestirn)));
                }
                break;

            case BEWOELKT:
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BEWOELKT)
                        .alsSatzMitSubjekt(HIMMEL));
                break;
            case BEDECKT:
                alt.add(
                        // "Düstere Wolken bedecken den ganzen Himmel"
                        BEDECKEN.mit(GANZER_HIMMEL).alsSatzMitSubjekt(DUESTERE_WOLKEN));
                alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.WOLKENVERHANGEN)
                        .alsSatzMitSubjekt(HIMMEL));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    // FIXME "im Mondschein" / "im Sonnenschein" (tageszeit.altGestirnschein()!)
    // FIXME "beim Mondschimmer"
    // FIXME "der Hügel liegt in einsamem Mondschein."

    boolean isUnauffaellig(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return compareTo(BEWOELKT) <= 0;
        }

        return compareTo(LEICHT_BEWOELKT) <= 0;
    }

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(final AvTime time) {
        return altWohinHinaus(time.getTageszeit());
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt = ImmutableList.builder();

        alt.addAll(mapToList(altLichtInDemEtwasLiegt(tageszeit),
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

    // FIXME es klart auf / der Himmel bedeckt sich/ bezieht sich

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

    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final Tageszeit tageszeit) {
        return altImLicht(tageszeit, BEI_DAT.mit(LICHT_OHNE_ART));
    }

    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final Tageszeit tageszeit) {
        return altImLicht(tageszeit, BEI_DAT.mit(TAGESLICHT_OHNE_ART));
    }

    private ImmutableSet<Praepositionalphrase> altImLicht(
            final Tageszeit tageszeit, final Praepositionalphrase alternative) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();
        alt.add(alternative);
        alt.addAll(mapToSet(altLichtInDemEtwasLiegt(tageszeit), IN_DAT::mit));
        return alt.build();
    }

    ImmutableSet<Praepositionalphrase> altUnterOffenemHimmel(
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        alt.addAll(mapToList(altLichtInDemEtwasLiegt(tageszeit), IN_DAT::mit));

        switch (this) {
            case WOLKENLOS:
                alt.addAll(mapToList(tageszeit.altWolkenloserHimmel(), UNTER_DAT::mit));
                break;
            case LEICHT_BEWOELKT:
                alt.addAll(mapToList(tageszeit.altGestirnschein(), IN_DAT::mit));
                break;
            case BEWOELKT:
                alt.add(UNTER_DAT.mit(BEWOELKTER_HIMMEL));
                break;
            case BEDECKT:
                alt.add(UNTER_DAT.mit(BEDECKTER_HIMMEL),
                        UNTER_DAT.mit(BEZOGENER_HIMMEL),
                        UNTER_DAT.mit(WOLKENVERHANGENER_HIMMEL));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + this);
        }

        return alt.build();
    }

    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt = ImmutableList.builder();

        switch (this) {
            case WOLKENLOS:
                if (tageszeit == TAGSUEBER) {
                    alt.add(HELLES_TAGESLICHT);
                } else if (tageszeit == NACHTS) {
                    alt.add(STERNENLICHT);
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
                return ImmutableSet.of(SANFTES_MORGENLICHT);
            case TAGSUEBER:
                return ImmutableSet.of(SANFTES_TAGESLICHT, GETRUEBTES_TAGESLICHT);
            case ABENDS:
                return ImmutableSet.of(SCHUMMERLICHT, DAEMMERLICHT);
            case NACHTS:
                return ImmutableSet.of(SANFTES_MONDLICHT);
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }
    }

    static ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegtBedeckt(
            final Tageszeit tageszeit) {
        switch (tageszeit) {
            case MORGENS:
                return ImmutableSet.of(TRUEBES_DAEMMERLICHT);
            case TAGSUEBER:
                return ImmutableSet.of(TRUEBES_LICHT);
            case ABENDS:
                return ImmutableSet.of(ZWIELICHT, HALBDUNKEL);
            case NACHTS:
                return ImmutableSet.of(DUNKEL, DUNKELHEIT, DUESTERNIS);
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }
    }
}