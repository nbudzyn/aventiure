package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.SpatialConnection.conAltDesc;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.SOUTH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MARKT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MARKTSTAENDE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERLASSEN;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.description.AbstractDescription;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#BAUERNMARKT}
 * room.
 */
public class BauernmarktConnectionComp extends AbstractSpatialConnectionComp {

    public BauernmarktConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(BAUERNMARKT, db, timeTaker, n, world);
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
        return ImmutableList.of(
                conAltDesc(DRAUSSEN_VOR_DEM_SCHLOSS,
                        "vor dem Schloss",
                        SOUTH,
                        this::getActionNameTo_DraussenVorDemSchloss,
                        mins(3),
                        this::altDescTo_DraussenVorDemSchloss));
    }

    private String getActionNameTo_DraussenVorDemSchloss() {
        if (loadSchlossfest().stateComp().hasState(
                NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN)) {
            return "Den Bauernmarkt verlassen";
        }
        return "Die Marktstände verlassen";
    }

    private ImmutableList<AbstractDescription<?>> altDescTo_DraussenVorDemSchloss(
            final Known known,
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final ImmutableList.Builder<AbstractDescription<?>> res = ImmutableList.builder();

        switch (loadSchlossfest().stateComp().getState()) {
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN:
                res.addAll(altDescTo_DraussenVorDemSchloss_Offen());
                break;
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN:
                res.addAll(altDescTo_DraussenVorDemSchloss_Geschlossen());
                break;
            default:
                throw new IllegalStateException("Unerwarteter Zustand: "
                        + loadSchlossfest().stateComp().getState()
                        + ", erwartet: Markt");
        }

        loadSC().mentalModelComp().setAssumedStateToActual(SCHLOSSFEST);

        return res.build();
    }

    private static Iterable<? extends AbstractDescription<?>> altDescTo_DraussenVorDemSchloss_Offen() {
        return ImmutableList.of(
                du(VERLASSEN.mit(MARKT)).undWartest().dann(),
                du(VERLASSEN.mit(MARKT.mit(KLEIN))).undWartest().dann(),
                du(VERLASSEN.mit(NomenFlexionsspalte.BAUERNMARKT.mit(KLEIN))).undWartest().dann()
        );
    }

    private static Iterable<? extends AbstractDescription<?>>
    altDescTo_DraussenVorDemSchloss_Geschlossen() {
        return ImmutableList.of(
                du(VERLASSEN.mit(MARKTSTAENDE)).undWartest().dann(),
                du("kehrst", "den Marktständen den Rücken zu").undWartest().dann()
        );
    }

    @NonNull
    private IHasStateGO<SchlossfestState> loadSchlossfest() {
        return loadRequired(SCHLOSSFEST);
    }
}



