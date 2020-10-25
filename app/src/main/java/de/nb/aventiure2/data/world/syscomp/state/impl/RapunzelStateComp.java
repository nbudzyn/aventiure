package de.nb.aventiure2.data.world.syscomp.state.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public class RapunzelStateComp extends AbstractStateComp<RapunzelState> {
    public RapunzelStateComp(final AvDatabase db, final Narrator n, final World world) {
        super(RAPUNZEL, db, n, world, RapunzelState.class, STILL);
    }

    public void rapunzelLaesstHaareZumAbstiegHerunter() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (!loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                n.narrate(du(PARAGRAPH, "siehst", " über dir eine Bewegung: "
                                + "Aus dem Turmfenster fallen auf einmal lange, golden "
                                + "glänzende Haare bis zum Boden herab",
                        secs(10))
                        .dann());
            } else {
                n.narrate(du(PARAGRAPH, "siehst", " über dir eine Bewegung: "
                                + "Aus dem Turmfenster fallen wieder die "
                                + "langen, golden glänzenden Haare bis zum Boden herab",
                        secs(10))
                        .dann());
            }
            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_HAARE);
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            final Nominalphrase rapunzelDesc = getDescription(true);

            final ImmutableList.Builder<AbstractDescription<?>> alt =
                    ImmutableList.builder();

            alt.add(
                    neuerSatz(rapunzelDesc.nom() +
                            // STORY nur verschüchtert, wenn man Rapunzel noch nicht
                            //  viel Zuneigung entwickelt hat
                            " schaut dich verschüchtert an, dann bindet "
                            + rapunzelDesc.persPron().nom() //"sie"
                            + " "
                            + rapunzelDesc.possArt().vor(PL_MFN).akk() // "ihre"
                            + " Haare wieder um den Haken am Fenster")
                            .phorikKandidat(PL_MFN, RAPUNZELS_HAARE),
                    neuerSatz(rapunzelDesc.nom() +
                            " schaut dich an, dann knotet "
                            + rapunzelDesc.persPron().nom() //"sie"
                            + " "
                            + rapunzelDesc.possArt().vor(PL_MFN).akk() // "ihre"
                            + " Haare wieder um den Fensterhaken")
                            .phorikKandidat(PL_MFN, RAPUNZELS_HAARE),
                    neuerSatz(rapunzelDesc.nom() +
                            " wickelt "
                            + rapunzelDesc.possArt().vor(PL_MFN).akk() // "ihre"
                            + " Haare wieder an den Fensterhaken")
                            .phorikKandidat(PL_MFN, RAPUNZELS_HAARE)
            );

            // STORY Nur wenn Rapunzel schon etwas Zuneigung entwickelt hat:
//                    neuerSatz(rapunzelDesc.nom() +
//                                    " schaut auf einmal etwas enttäuscht drein. Dann bindet "
//                                    + rapunzelDesc.persPron().nom() //"sie"
//                                    + " "
//                                    + rapunzelDesc.possArt().vor(PL_MFN).akk() // "ihre"
//                                    + " Haare wieder beim Fenster fest",
//                            secs(10))
//                            .phorikKandidat(PL_MFN, RAPUNZELS_HAARE),

            //  STORY "Oh, ich wünschte, ihr könntet noch einen Moment bleiben!" antwortet RAPUNZEL.
            //    Aber sie knotet doch ihrer Haare wieder über den Haken am Fenster"

            n.narrateAlt(alt, secs(10));
        }

        narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
        // Ggf. steigt die Zauberin als Reaktion daran herunter
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }


    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    private Nominalphrase getDescription(final boolean shortIfKnown) {
        return world.getDescription(getGameObjectId(), shortIfKnown);
    }
}
