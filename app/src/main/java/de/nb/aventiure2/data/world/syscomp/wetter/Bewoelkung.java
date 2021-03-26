package de.nb.aventiure2.data.world.syscomp.wetter;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.german.base.Nominalphrase.ABENDLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.DAEMMERLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.DUESTERE_WOLKEN;
import static de.nb.aventiure2.german.base.Nominalphrase.DUESTERNIS;
import static de.nb.aventiure2.german.base.Nominalphrase.DUNKEL;
import static de.nb.aventiure2.german.base.Nominalphrase.DUNKELHEIT;
import static de.nb.aventiure2.german.base.Nominalphrase.GANZER_HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.GETRUEBTES_TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.HALBDUNKEL;
import static de.nb.aventiure2.german.base.Nominalphrase.HELLES_TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.LICHT_OHNE_ART;
import static de.nb.aventiure2.german.base.Nominalphrase.MONDLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.MORGENLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.NACHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SANFTES_MONDLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SANFTES_MORGENLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SANFTES_TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SCHUMMERLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.STERNENLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.TAGESLICHT_OHNE_ART;
import static de.nb.aventiure2.german.base.Nominalphrase.TRUEBES_DAEMMERLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.TRUEBES_LICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.ZWIELICHT;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.praedikat.Modalverb.WOLLEN;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERABSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.UNTERGEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEDECKEN;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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

        alt.addAll(altStatischeBeschreibungSaetze(time).stream()
                .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen")))
                .collect(toList()));

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
                alt.addAll(time.getTageszeit().altGestirn().stream()
                        .map(gestirn ->
                                // "Der Mond ist gerade von einer dunklen Wolke bedeckt")
                                praedikativumPraedikatMit(AdjektivOhneErgaenzungen.BEDECKT
                                        .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                                "von einer dunklen Wolke")))
                                        .mitAdvAngabe(new AdvAngabeSkopusSatz("gerade"))
                                        .alsSatzMitSubjekt(gestirn))
                        .collect(toImmutableList()));
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
                alt.addAll(time.getTageszeit().altGestirn().stream()
                        .map(gestirn ->
                                // "die Sonne scheint auf dich herab"
                                HERABSCHEINEN
                                        .mitAdvAngabe(
                                                new AdvAngabeSkopusVerbWohinWoher(
                                                        PraepositionMitKasus.AUF_AKK
                                                                .mit(Personalpronomen.get(P2, M))))
                                        .alsSatzMitSubjekt(gestirn))
                        .collect(toImmutableList()));
                if (time.getTageszeit() == Tageszeit.ABENDS) {
                    alt.addAll(time.getTageszeit().altGestirn().stream()
                            .map(gestirn ->
                                    // "noch scheint die Sonne"
                                    SCHEINEN
                                            .mitAdvAngabe(
                                                    new AdvAngabeSkopusVerbAllg("noch"))
                                            .alsSatzMitSubjekt(gestirn))
                            .collect(toImmutableList()));
                    if (time.kurzVorSonnenuntergang()) {
                        alt.addAll(time.getTageszeit().altGestirn().stream()
                                .map(gestirn ->
                                        // "Die Sonne will eben untergehen"
                                        WOLLEN.mitLexikalischemKern(
                                                UNTERGEHEN
                                                        .mitAdvAngabe(
                                                                new AdvAngabeSkopusSatz("eben")
                                                        )
                                        ).alsSatzMitSubjekt(gestirn))
                                .collect(toImmutableList()));
                    }
                }

                if (time.getTageszeit() == Tageszeit.NACHTS
                        || time.getTageszeit() == TAGSUEBER) {
                    alt.addAll(time.getTageszeit().altGestirn().stream()
                            .map( // "der Mond scheint ganz helle"
                                    gestirn ->
                                            SCHEINEN
                                                    .mitAdvAngabe(
                                                            new AdvAngabeSkopusVerbAllg(
                                                                    "ganz helle"))
                                                    .alsSatzMitSubjekt(gestirn))
                            .collect(toImmutableList()));

                    alt.addAll(time.getTageszeit().altGestirn().stream()
                            .map( // "die Sonne scheint hell"
                                    gestirn ->
                                            SCHEINEN
                                                    .mitAdvAngabe(
                                                            // FIXME Hier kann es zu
                                                            //  widersprüchen zwischen
                                                            //  nachts "dunkel" und Mond "hell"
                                                            //  kommen.
                                                            new AdvAngabeSkopusVerbAllg("hell"))
                                                    .alsSatzMitSubjekt(gestirn))
                            .collect(toImmutableList()));
                }

                if (time.getTageszeit() == Tageszeit.NACHTS) {
                    alt.add(praedikativumPraedikatMit(AdjektivOhneErgaenzungen.ERHELLT)
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusVerbAllg("vom Mond"))
                            .alsSatzMitSubjekt(NACHT));
                }

                break;

            case LEICHT_BEWOELKT:
                alt.addAll(time.getTageszeit().altGestirn().stream()
                        .map( // "die Sonne scheint"
                                SCHEINEN::alsSatzMitSubjekt)
                        .collect(toImmutableList()));
                if (time.getTageszeit() == TAGSUEBER) {
                    alt.addAll(time.getTageszeit().altGestirn().stream()
                            .map( // "die Sonne scheint dir ins Gesicht"
                                    gestirn ->
                                            VerbSubjObj.SCHEINEN
                                                    .mit(Personalpronomen.get(P2, M))
                                                    .mitAdvAngabe(
                                                            new AdvAngabeSkopusVerbWohinWoher(
                                                                    "ins Gesicht"))
                                                    .alsSatzMitSubjekt(gestirn))
                            .collect(toImmutableList()));
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

        alt.addAll(altLichtInDemEtwasLiegt(tageszeit).stream()
                // "in das helle Tageslicht", "in das trübe Dämmerlicht"
                .map(licht -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(licht)))
                .collect(toList()));

        alt.addAll(tageszeit.altGestirnschein().stream()
                // "in den Abendsonnenschein"
                .map(schein -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(schein)))
                .collect(toList()));

        switch (this) {
            case WOLKENLOS:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in den klaren Morgen"));
                // fall-through
            case LEICHT_BEWOELKT:
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

        return res.build().stream()
                .map(AdvAngabeSkopusVerbWohinWoher::new)
                .collect(toImmutableSet());
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

        return res.build().stream()
                .map(AdvAngabeSkopusVerbWohinWoher::new)
                .collect(toImmutableSet());
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
        alt.addAll(altLichtInDemEtwasLiegt(tageszeit).stream()
                .map(IN_DAT::mit)
                .collect(toSet()));
        return alt.build();
    }

    ImmutableCollection<Nominalphrase> altLichtInDemEtwasLiegt(final Tageszeit tageszeit) {
        final ImmutableList.Builder<Nominalphrase> alt = ImmutableList.builder();

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

    static ImmutableCollection<Nominalphrase> altLichtInDemEtwasLiegtBewoelkt(
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

    static ImmutableCollection<Nominalphrase> altLichtInDemEtwasLiegtBedeckt(
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