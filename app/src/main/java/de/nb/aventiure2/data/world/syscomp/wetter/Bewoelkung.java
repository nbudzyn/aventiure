package de.nb.aventiure2.data.world.syscomp.wetter;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Nominalphrase.DUESTERE_WOLKEN;
import static de.nb.aventiure2.german.base.Nominalphrase.GANZER_HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.HIMMEL;
import static de.nb.aventiure2.german.base.Nominalphrase.NACHT;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERABSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEDECKEN;
import static java.util.stream.Collectors.toList;

public enum Bewoelkung implements Betweenable<Bewoelkung> {
    // Reihenfolge ist relevant, nicht ändern!
    WOLKENLOS,
    LEICHT_BEWOELKT,
    BEWOELKT,
    BEDECKT;

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
                }

                if (time.getTageszeit() == Tageszeit.NACHTS
                        || time.getTageszeit() == Tageszeit.TAGSUEBER) {
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

            // FIXME "im Mondschein" / "im Sonnenschein"
            // FIXME "beim Mondschimmer"
            // FIXME "der Hügel liegt in einsamem Mondschein."

            case LEICHT_BEWOELKT:
                alt.addAll(time.getTageszeit().altGestirn().stream()
                        .map( // "die Sonne scheint"
                                SCHEINEN::alsSatzMitSubjekt)
                        .collect(toImmutableList()));
                if (time.getTageszeit() == Tageszeit.TAGSUEBER) {
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
}