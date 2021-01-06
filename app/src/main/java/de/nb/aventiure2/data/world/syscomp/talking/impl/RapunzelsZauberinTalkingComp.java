package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entrySt;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.FRAGEN_NACH;

/**
 * Component for {@link World#RAPUNZELS_ZAUBERIN}: Der Spieler
 * kann versuchen, mit Rapunzels Zauberin ein Gespräch zu führen.
 */
public class RapunzelsZauberinTalkingComp extends AbstractTalkingComp {
    private final LocationComp locationComp;
    private final RapunzelsZauberinStateComp stateComp;

    public RapunzelsZauberinTalkingComp(final AvDatabase db,
                                        final Narrator n,
                                        final World world,
                                        final LocationComp locationComp,
                                        final RapunzelsZauberinStateComp stateComp,
                                        final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZELS_ZAUBERIN, db, n, world, initialSchonBegruesstMitSC);
        this.locationComp = locationComp;
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        switch (stateComp.getState()) {
            case MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE:
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                // fall-through
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                if (locationComp.hasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                    // Hier bemerkt der SC die Zauberin nicht - bzw. sie
                    // "verschwindet" ohnehin gleich wieder.
                    return ImmutableList.of();
                }

                return ImmutableList.of(
                        entrySt(FRAGEN_NACH.mitPraep(
                                np(N, null, "ihr Ziel",
                                        "ihrem Ziel")),
                                this::frageNachZiel_zauberinReagiertAbweisend));
            case BEI_RAPUNZEL_OBEN_IM_TURM:
                // FIXME Kann man die Zauberin oben im Turm ansprechen? Wie reagiert
                //  sie?
                return ImmutableList.of();
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Man kann die  Zauberin ansprechen, nachdem Rapunzel befreit wurde
                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }

    private void frageNachZiel_zauberinReagiertAbweisend() {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        n.narrateAlt(secs(10),
                neuerSatz(PARAGRAPH, "„Wohin des Wegs, gute Frau“, sprichst du " +
                        anaphOderDesc.akkStr() +
                        " an. „Was geht es dich an?“, ist "
                        + anaphOderDesc.possArt().vor(F).nomStr() // "ihre"
                        + " abweisende Antwort")
                        .phorikKandidat(anaphOderDesc, getGameObjectId()),
                neuerSatz("„Ihr habt es wohl eilig?“, sprichst du " +
                        anaphOderDesc.akkStr() +
                        " an. „So ist es“, antwortet "
                        + anaphOderDesc.persPron().nomStr()
                        + " dir")
                        .phorikKandidat(anaphOderDesc, getGameObjectId())
        );

        // FIXME Begrüßen (wenn noch nicht begrüßt!) / Ansprechen der Hexe
        //  vom Fragen nach dem Weg trennen, so dass der SC mit der Hexe kurz
        //  im Gespräch ist.
        //  - Hexe Kugel geben (Diebesgut...) (beendet Gespräch)
        //  - Hexe nach Turm fragen (Abneigung steigt - danach "missteauisch...")
        //  - Nach-dem-Weg-fragen beendet das Gespräch.

        // FIXME Weitere Antworten.

        setSchonBegruesstMitSC(true);
        unsetTalkingTo();
    }
}
