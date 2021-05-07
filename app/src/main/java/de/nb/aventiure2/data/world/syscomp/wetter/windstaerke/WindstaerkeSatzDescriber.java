package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WINDGESCHUETZT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STURM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.Nominalphrase.DEIN_HAAR;
import static de.nb.aventiure2.german.base.Nominalphrase.KEIN_WIND;
import static de.nb.aventiure2.german.base.Nominalphrase.SCHUTZ_VOR_DEM_AERGSTEN_STURM;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BLASEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRAUSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.RAUSCHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SAUSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.WEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.FINDEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.PFEIFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZAUSEN;
import static de.nb.aventiure2.german.praedikat.Witterungsverb.STUERMEN;
import static de.nb.aventiure2.util.StreamUtil.*;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class WindstaerkeSatzDescriber {

    public WindstaerkeSatzDescriber() {

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
                mapToList(alt(time, windstaerke, true),
                        s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (windstaerke.compareTo(Windstaerke.WINDIG) > 0) {
            alt.addAll(alt(time, windstaerke, false));
        }


        return alt.build();
    }

    /**
     * Gibt alternative Sätze zur Windstärke zurück
     */
    public ImmutableCollection<EinzelnerSatz> alt(
            final AvTime time, final Windstaerke windstaerke,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
            // "der Morgen ist windig"
            alt.addAll(mapToSet(windstaerke.altAdjPhrWetter(),
                    windig -> windig.alsPraedikativumPraedikat()
                            .alsSatzMitSubjekt(time.getTageszeit().getNomenFlexionsspalte())));
        }

        // "es ist windig"
        alt.addAll(mapToSet(windstaerke.altAdjPhrWetter(),
                windig -> windig.alsPraedikativumPraedikat().alsSatzMitSubjekt(EXPLETIVES_ES)));

        switch (windstaerke) {
            case WINDSTILL:
                alt.add(STEHEN.alsSatzMitSubjekt(LUFT),
                        WEHEN.alsSatzMitSubjekt(KEIN_WIND));
                break;
            case LUEFTCHEN:
                break;
            case WINDIG:
                alt.add(SAUSEN.alsSatzMitSubjekt(WIND));
                break;
            case KRAEFTIGER_WIND:
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
                                UNANGENEHM).alsPraedikativumPraedikat().alsSatzMitSubjekt(WIND),
                        RAUSCHEN.alsSatzMitSubjekt(WIND));
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
