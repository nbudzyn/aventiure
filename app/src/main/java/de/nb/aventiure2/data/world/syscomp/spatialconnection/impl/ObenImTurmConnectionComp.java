package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.ANAPH_POSSESSIVARTIKEL_ODER_GENITIVATTRIBUT_ODER_NICHT_POSSESSIV;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ObenImTurmConnectionComp.Counter.HERABGESTIEGEN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HINABSTEIGEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#OBEN_IM_ALTEN_TURM}
 * room.
 */
@ParametersAreNonnullByDefault
public class ObenImTurmConnectionComp extends AbstractSpatialConnectionComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        HERABGESTIEGEN
    }

    public ObenImTurmConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n,
            final World world) {
        super(OBEN_IM_ALTEN_TURM, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return true;
    }

    @Override
    @NonNull
    @CheckReturnValue
    public List<SpatialConnection> getConnections() {
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();

        if (loadRapunzel().stateComp().hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            res.add(con(VOR_DEM_ALTEN_TURM,
                    "an den Zöpfen",
                    "An den Haaren hinabsteigen",
                    secs(90),
                    this::getDescTo_VorDemTurm));
        }

        return res.build();
    }

    @CheckReturnValue
    private TimedDescription<?> getDescTo_VorDemTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().get(HERABGESTIEGEN) % 2 == 1) {
            // 2.Mal, 4. Mal, ...
            return du(WORD, "bist", "schnell wieder hinab")
                    .mitVorfeldSatzglied("schnell")
                    .schonLaenger()
                    .timed(secs(30))
                    .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                    .undWartest()
                    .dann();
        }

        final ITextContext textContext = n;
        // FIXME Ist das richtig so?
        //  Kann man hier vermeiden, dass man das angeben muss?
        //  Kann nicht der TextContext beim Zusammenbau automatisch
        //  ermittelt werden - vor allem, wenn die Sprachkomponente
        //  den Text zusammenbaut?!
        //  Dazu müsste allerdings die Sprachzusammenbaukomponente
        //  wissen: "hier bitte Rapunzel einfügen" - und außerdem:
        //  Wie ist der initiale ITextContext?

        final SubstantivischePhrase anaphRapunzelsHaareMoeglichstPossessiv =
                anaphRapunzelsHaareMoeglichstPossessiv(
                        textContext,
                        ANAPH_POSSESSIVARTIKEL_ODER_GENITIVATTRIBUT_ODER_NICHT_POSSESSIV, true);
        // "sie" / "ihre Haare" / "Rapunzels Haare" / "die Haare"

        // "Du steigst daran hinab" / "Du steigst an ihren Haren hinab" /
        // "Du steigst an Rapunzels Haaren hinab" /
        // "Du steigst an den Haaren hinab"
        return du(WORD, HINABSTEIGEN
                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                        AN_DAT.mit(anaphRapunzelsHaareMoeglichstPossessiv))))
                // "an den Haaren" / "daran"
                .timed(mins(1))
                .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                .undWartest()
                .dann();
    }

    /**
     * Gibt etwas zurück wie "sie" (wenn anaphorischer Bezug auf Rapunzels Haare möglich ist),
     * "Rapunzels Haare" (wenn Rapunzel und ihr Name bekannt sind),
     * "ihre Haare" (wenn ein anaphorischer Bezug <i>auf Rapunzel</i> möglich ist) oder
     * "die Haare".
     *
     * @param descShortIfKnown Ob die Beschreibung (wenn kein Personalpronomen möglich ist)
     *                         kurz gehalten werden soll, falls Rapunzels Haare bereits
     *                         bekannt sind
     */
    private SubstantivischePhrase anaphRapunzelsHaareMoeglichstPossessiv(
            final ITextContext textContext,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean descShortIfKnown) {
        final GameObjectId describableId = RAPUNZELS_HAARE;

        @Nullable final Personalpronomen anaphPersPron =
                textContext.getAnaphPersPronWennMgl(describableId);
        if (anaphPersPron != null) {
            return anaphPersPron; // "sie" (die Haare)
        }

        return world.descriptionSystem
                .getPOVDescription(textContext, loadSC(), loadRequired(describableId),
                        possessivDescriptionVorgabe,
                        descShortIfKnown); // "ihre Haare" / "Rapunzels Haare"
    }

    // FIXME Alle Vorkommen von RAPUNZELS_HAARE suchen und prüfen, wie man
    //  etwas wie dies hier verwenden / das hier verallgemeinern kann.

    @NonNull
    private IHasStateGO<RapunzelState> loadRapunzel() {
        return loadRequired(RAPUNZEL);
    }
}