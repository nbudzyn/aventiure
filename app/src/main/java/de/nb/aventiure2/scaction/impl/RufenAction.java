package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.Kohaerenzrelation;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp.LASS_DEIN_HAAR_HERUNTER;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Wortfolge.uncapitalize;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAus;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.WIEDERHOLUNG;

/**
 * Der Spielercharakter ruft / stößt einen Ruf aus.
 */
public class RufenAction extends AbstractScAction {
    private final ILocationGO location;
    private final Ruftyp ruftyp;

    public static ImmutableList<AbstractScAction> buildActions(
            final AvDatabase db,
            final Narrator n, final World world,
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = builder();

        if (world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
            if (world.isOrHasRecursiveLocation(location, VOR_DEM_ALTEN_TURM) &&
                    !((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp()
                            .hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
                res.add(new RufenAction(db, n, world, location, LASS_DEIN_HAAR_HERUNTER));
            }
        }

        return res.build();
    }

    private RufenAction(final AvDatabase db, final Narrator n, final World world,
                        final ILocationGO location,
                        final Ruftyp ruftyp) {
        super(db, n, world);
        this.location = location;
        this.ruftyp = ruftyp;
    }

    @Override
    public String getType() {
        return "actionRedenRufen";
    }

    @Override
    @NonNull
    public String getName() {
        return
                GermanUtil.capitalize(
                        GermanUtil.joinToNullString(
                                ruftyp.getName().getInfinitiv(P1, SG)));
    }

    @Override
    public void narrateAndDo() {
        final Kohaerenzrelation kohaerenzrelation =
                sc.memoryComp().getLastAction().is(Action.Type.RUFEN) ?
                        WIEDERHOLUNG : VERSTEHT_SICH_VON_SELBST;

        if (kohaerenzrelation != VERSTEHT_SICH_VON_SELBST ||
                n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.narrateAlt(drueckeAus(kohaerenzrelation, du(ruftyp.getName())),
                    secs(30));
        } else {
            final Iterable<Konstituente> rufDuHaupsatz = ruftyp.getName().getDuHauptsatz();
            final Wortfolge rufDuHaupsatzWortfolge = Wortfolge.joinToNullWortfolge(rufDuHaupsatz);

            n.narrate(neuerSatz(
                    "Und "
                            + uncapitalize(rufDuHaupsatzWortfolge).getString(),
                    secs(30))
                    .komma(rufDuHaupsatzWortfolge.kommmaStehtAus()));
        }

        world.narrateAndDoReactions().onRuf(sc, Ruftyp.LASS_DEIN_HAAR_HERUNTER);

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Der Ruf-Typ wird derzeit nicht in der Aktion gespeichert (es gibt kein
        // Feld passenden Typs dafür, er hat ja keine GameObjectId).
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.RUFEN);
    }
}
