package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.ReflVerbSubj;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.ONE_DAY;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.LUEFTCHEN;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.WINDIG;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.WINDSTILL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHWAECHER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STAERKER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WINDGESCHUETZT;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KRAFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFTHAUCH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STURM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.Nominalphrase.DEIN_HAAR;
import static de.nb.aventiure2.german.base.Nominalphrase.KEIN_WIND;
import static de.nb.aventiure2.german.base.Nominalphrase.SCHUTZ_VOR_DEM_AERGSTEN_STURM;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ABFLAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.AUFZIEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BLASEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRAUSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.NACHLASSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.RAUSCHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SAUSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.WEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ZUNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.FINDEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.PFEIFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SPUEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERLIEREN_AN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZAUSEN;
import static de.nb.aventiure2.german.praedikat.Witterungsverb.STUERMEN;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class WindstaerkeSatzDescriber {
    private final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber;

    public WindstaerkeSatzDescriber(
            final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber) {
        this.tageszeitAdvAngabeWannDescriber = tageszeitAdvAngabeWannDescriber;
    }

    /**
     * Gibt Sätze zurück, die beschreiben, wie die Windstaerke sich um eine Stufe
     * (Windstärkewechsel) oder um mehrere Stufen (Windstärkesprung) verändert hat.*
     */
    public ImmutableCollection<EinzelnerSatz> altSprungOderWechsel(
            final Change<AvDateTime> dateTimeChange,
            final WetterParamChange<Windstaerke> change,
            final boolean auchZeitwechselreferenzen) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        final int delta = change.delta();

        final ImmutableCollection<EinzelnerSatz> altStatisch =
                alt(dateTimeChange.getNachher().getTime(), change.getNachher(),
                        true, false);

        if (Math.abs(delta) <= 1) {
            alt.addAll(altWechsel(change));

            if (span(dateTimeChange).shorterThan(ONE_DAY) && auchZeitwechselreferenzen) {
                final Change<AvTime> timeChange = dateTimeChange.map(AvDateTime::getTime);

                alt.addAll(tageszeitAdvAngabeWannDescriber
                        .altWannDraussen(timeChange).stream()
                        .flatMap(gegenMitternacht ->
                                altWechsel(change).stream()
                                        .map(s -> s.mitAdvAngabe(gegenMitternacht)))
                        .collect(toSet()));

                alt.addAll(tageszeitAdvAngabeWannDescriber
                        .altWannKonditionalsaetzeDraussen(timeChange)
                        .stream()
                        .flatMap(alsDerTagAngebrochenIst ->
                                altWechsel(change).stream()
                                        .filter(s -> !s.hatAngabensatz())
                                        .map(s -> s.mitAngabensatz(alsDerTagAngebrochenIst, true)))
                        .collect(toSet()));
            }
        } else {
            // "inzwischen ist es windig"
            alt.addAll(mapToSet(altStatisch,
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("inzwischen"))));

            // "jetzt ist es windig"
            alt.addAll(mapToSet(altStatisch,
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))));

            // "es ist windig geworden"
            alt.addAll(mapToSet(change.getNachher().altAdjPhrWetter(),
                    windig -> windig.alsPraedikativumPraedikat()
                            .alsSatzMitSubjekt(EXPLETIVES_ES).perfekt()));

            if (delta > 0) {
                if (change.getNachher() == Windstaerke.STURM) {
                    //  "Ein Sturm zieht auf"
                    alt.add(AUFZIEHEN.alsSatzMitSubjekt(np(INDEF, STURM)));
                }
            } else {
                //  "Der Wind hat deutlich nachgelassen"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        subjekt -> NACHLASSEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("deutlich"))
                                .alsSatzMitSubjekt(subjekt).perfekt()));
                //  "Endlich hat der Wind nachgelassen"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        wind -> NACHLASSEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("endlich"))
                                .alsSatzMitSubjekt(wind).perfekt()));
                if (change.getVorher().compareTo(Windstaerke.STURM) >= 0
                        && change.getNachher().compareTo(LUEFTCHEN) >= 0) {
                    alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                            wind -> ABFLAUEN
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("allmählich"))
                                    .alsSatzMitSubjekt(wind)));
                }
                if (change.getNachher().isBetweenIncluding(WINDIG, Windstaerke.KRAEFTIGER_WIND)
                        && change.getNachher() == WINDSTILL) {
                    alt.add(ReflVerbSubj.SICH_LEGEN.alsSatzMitSubjekt(WIND));
                }

                if (change.getNachher().compareTo(Windstaerke.STURM) >= 0
                        && change.getNachher().compareTo(LUEFTCHEN) <= 0) {
                    alt.add(ReflVerbSubj.SICH_LEGEN.alsSatzMitSubjekt(STURM));
                }
            }
        }

        return alt.build();
    }

    private ImmutableSet<EinzelnerSatz> altWechsel(final WetterParamChange<Windstaerke> change) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        final int delta = change.getNachher().minus(change.getVorher());

        if (Math.abs(delta) != 1) {
            throw new IllegalStateException("Kein Wechsel - delta = " + delta);
        }

        if (delta == 1) {
            if (change.getVorher() == WINDSTILL) {
                //  "Du spürst einen Lufthauch"
                alt.add(SPUEREN.mit(np(INDEF, LUFTHAUCH))
                        .alsSatzMitSubjekt(duSc()));
            }
            if (change.getVorher().compareTo(Windstaerke.LUEFTCHEN) >= 0) {
                //  "Der Wind wird stärker"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        wind -> STAERKER.alsWerdenPraedikativumPraedikat()
                                .alsSatzMitSubjekt(wind)));
            }
            if (change.getVorher().compareTo(Windstaerke.STURM) >= 0) {
                //  "Der Sturm wird sogar noch stärker"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        wind -> STAERKER.mitGraduativerAngabe("sogar noch")
                                .alsWerdenPraedikativumPraedikat()
                                .alsSatzMitSubjekt(wind)));
            }
            if (change.getVorher().compareTo(Windstaerke.LUEFTCHEN) >= 0) {
                //  "Der Wind nimmt zu"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        ZUNEHMEN::alsSatzMitSubjekt));

            }
            // "jetzt wird es stürmisch"
            alt.addAll(mapToSet(change.getNachher().altAdjPhrWetter(),
                    stuermisch -> stuermisch.alsEsWirdSatz()
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))));
        } else if (delta == -1) {
            // "Es wird windstill"
            if (change.getNachher() == WINDSTILL) {
                alt.add(AdjektivOhneErgaenzungen.WINDSTILL.alsEsWirdSatz());
            }

            if (change.getNachher() == WINDIG || change.getNachher() == Windstaerke.STURM) {
                //  "Der kräftige Wind lässt etwas nach"
                alt.addAll(mapToSet(change.getNachher().altNomenFlexionsspalte(),
                        wind -> NACHLASSEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg("etwas"))
                                .alsSatzMitSubjekt(wind.mit(KRAEFTIG))));

                //  "Der kräftige Wind lässt ein wenig nach"
                alt.addAll(mapToSet(change.getNachher().altNomenFlexionsspalte(),
                        wind -> NACHLASSEN.mitAdvAngabe(
                                new AdvAngabeSkopusVerbAllg("ein wenig"))
                                .alsSatzMitSubjekt(wind.mit(KRAEFTIG))));
            }

            //  "Der Wind lässt nach"
            alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                    NACHLASSEN::alsSatzMitSubjekt));

            if (change.getVorher().isBetweenIncluding(WINDIG, Windstaerke.STURM)) {
                //  "Der Wind wird schwächer"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        wind -> SCHWAECHER.alsWerdenPraedikativumPraedikat()
                                .alsSatzMitSubjekt(wind)));
            }

            if (change.getVorher().compareTo(WINDIG) >= 0) {
                //  "Der Wind verliert an Kraft"
                alt.addAll(mapToSet(change.getVorher().altNomenFlexionsspalte(),
                        wind -> VERLIEREN_AN.mit(npArtikellos(KRAFT)).alsSatzMitSubjekt(wind)));
            }
        }

        return alt.build();
    }

    /**
     * Gibt Sätze zurück, wenn der SC in einen windgeschützteren Bereich kommt -
     * je nach Windstärke oft leer.
     */
    public ImmutableCollection<EinzelnerSatz> altAngenehmerAlsVorLocation(
            final Windstaerke windstaerkeFrom,
            final Windstaerke windstaerkeTo) {
        checkArgument(windstaerkeFrom.compareTo(windstaerkeTo) > 0);

        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        if (windstaerkeFrom.compareTo(Windstaerke.WINDIG) >= 0) {
            alt.add(WINDGESCHUETZT.mitGraduativerAngabe("etwas").alsEsIstSatz()
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("hier")));
        }

        if (windstaerkeFrom.compareTo(Windstaerke.STURM) >= 0) {
            // "Hier bläst der Sturm nicht gar so stark"
            alt.add(BLASEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg("nicht ganz so stark"))
                            .alsSatzMitSubjekt(STURM)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("hier")),
                    FINDEN.mit(SCHUTZ_VOR_DEM_AERGSTEN_STURM)
                            .alsSatzMitSubjekt(duSc())
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("hier")));

        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zur Windstärke zurück
     */
    public ImmutableCollection<EinzelnerSatz> altKommtNachDraussen(
            final AvTime time, final Windstaerke windstaerke) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(
                mapToList(alt(time, windstaerke, true, false),
                        s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (windstaerke.compareTo(Windstaerke.WINDIG) > 0) {
            alt.addAll(alt(time, windstaerke, false, false));
        }


        return alt.build();
    }

    /**
     * Gibt alternative Sätze zur Windstärke zurück - kann leer sein, falls
     * {@code ausschliesslichHoerbares true} ist.
     *
     * @param ausschliesslichHoerbares Ob nur Sätze zurückgegeben werden, die ausschließlich
     *                                 Dinge beschreiben, die hörbar sind.
     */
    public ImmutableCollection<EinzelnerSatz> alt(
            final AvTime time, final Windstaerke windstaerke,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete,
            final boolean ausschliesslichHoerbares) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        if (!ausschliesslichHoerbares) {
            if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                // "der Morgen ist windig"
                alt.addAll(mapToSet(windstaerke.altAdjPhrWetter(),
                        windig -> windig.alsPraedikativumPraedikat()
                                .alsSatzMitSubjekt(time.getTageszeit().getNomenFlexionsspalte())));
            }

            // "es ist windig"
            alt.addAll(mapToSet(windstaerke.altAdjPhrWetter(),
                    windig -> windig.alsPraedikativumPraedikat().alsSatzMitSubjekt(EXPLETIVES_ES)));
        }

        switch (windstaerke) {
            case WINDSTILL:
                if (!ausschliesslichHoerbares) {
                    alt.add(STEHEN.alsSatzMitSubjekt(LUFT),
                            WEHEN.alsSatzMitSubjekt(KEIN_WIND));
                }
                break;
            case LUEFTCHEN:
                break;
            case WINDIG:
                alt.add(SAUSEN.alsSatzMitSubjekt(WIND));
                break;
            case KRAEFTIGER_WIND:
                if (!ausschliesslichHoerbares) {
                    alt.add(PFEIFEN.mit(duSc())
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("ums Gesicht"))
                                    .alsSatzMitSubjekt(WIND),
                            PFEIFEN.mit(duSc())
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("ums Gesicht"))
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz(KRAEFTIG))
                                    .alsSatzMitSubjekt(WIND),
                            ZAUSEN.mit(DEIN_HAAR).alsSatzMitSubjekt(WIND),
                            new ZweiAdjPhrOhneLeerstellen(KRAEFTIG.mitGraduativerAngabe("sehr"),
                                    true,
                                    UNANGENEHM).alsPraedikativumPraedikat()
                                    .alsSatzMitSubjekt(WIND));
                }

                alt.add(RAUSCHEN.alsSatzMitSubjekt(WIND));
                break;
            case STURM:
                alt.add(STUERMEN.alsSatz(),
                        VerbSubj.STUERMEN.alsSatzMitSubjekt(WIND));
                alt.addAll(mapToSet(windstaerke.altNomenFlexionsspalte(),
                        BRAUSEN::alsSatzMitSubjekt));
                break;
            case SCHWERER_STURM:
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke");
        }

        return alt.build();
    }
}
