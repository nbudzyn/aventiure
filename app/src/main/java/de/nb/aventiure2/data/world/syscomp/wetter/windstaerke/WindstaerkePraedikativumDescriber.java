package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEWEGT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BRAUSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HART;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAFTVOLL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.PFEIFEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.RAUSCHEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SAUSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHWER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STEHEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STUERMEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TOBEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TOSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNBEWEGT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STURM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.UNWETTER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WETTER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.util.StreamUtil.*;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class WindstaerkePraedikativumDescriber {
    ImmutableSet<SubstantivischePhrase> altDraussenSubstPhr(
            final Windstaerke windstaerke, final AvTime time) {
        final ImmutableSet.Builder<SubstantivischePhrase> alt = ImmutableSet.builder();

        // "der Wind" (evtl. leer)
        alt.addAll(windstaerke.altSpNomenFlexionsspalte());

        // "der windige Morgen"
        alt.addAll(mapToSet(windstaerke.altAdjPhrWetter(),
                windig -> time.getTageszeit().getNomenFlexionsspalte().mit(windig)));

        switch (windstaerke) {
            case WINDSTILL:
                alt.add(LUFT.mit(STEHEND), LUFT.mit(UNBEWEGT));
                break;
            case LUEFTCHEN:
                alt.add(LUFT.mit(BEWEGT.mitGraduativerAngabe("kaum")));
                break;
            case WINDIG:
                alt.add(WIND.mit(SAUSEND));
                break;
            case KRAEFTIGER_WIND:
                alt.add(npArtikellos(WIND).und(npArtikellos(WETTER)),
                        WIND.mit(KRAEFTIG), WIND.mit(KRAFTVOLL), WIND.mit(HART),
                        WIND.mit(PFEIFEND),
                        WIND.mit(new ZweiAdjPhrOhneLeerstellen(
                                KRAEFTIG,
                                true,
                                UNANGENEHM)),
                        WIND.mit(RAUSCHEND));
                break;
            case STURM:
                alt.add(STURM, WIND.mit(STUERMEND));
                if (time.getTageszeit() == NACHTS) {
                    alt.add(STURM.mit(NAECHTLICH));
                }
                break;
            case SCHWERER_STURM:
                alt.add(UNWETTER.mit(TOBEND),
                        STURM.mit(BRAUSEND), STURM.mit(SCHWER),
                        STURM.mit(TOSEND));
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke: " + windstaerke);
        }

        // FIXME Idee: Bach / kleiner Fluss nach Trampelpfad hinter Hütte, dort wachsen
        //  die Binsen

        // FIXME Seilerin beobachten bei ihrer Tätigkeit
        //  "du schaust ihr genau dabei zu. So schwer sieht es eigentlich gar nicht aus.
        //  man legt einfach... und dann nimmt man wieder...  Mh-hm, gut zu wissen! / Interessant"
        // FIXME "Die Seilerin hat ihren Stand gut verschnürt und abgedeckt"

        return alt.build();
    }
}
