package de.nb.aventiure2.scaction.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUSRUPFEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * Der Spielercharakter erzeugt ein neues Game Object und nimmt es an sich. (Beispiel: Er rupft
 * ein paar Binsen aus.)
 */
@ParametersAreNonnullByDefault
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class CreateNehmenAction
        <DESC_OBJ extends ILocatableGO & IDescribableGO>
        extends AbstractScAction {
    private final OnTheFlyGOFactory onTheFlyGOFactory;

    /**
     * Der Typ des Objekts, das erzeugt wird und das der SC an sich nimmt.
     */
    private final GameObjectType gameObjectType;

    /**
     * Die Objekte (nicht Kreaturen!), die der SC bereits bei sich trägt.
     */
    private final ImmutableList<? extends ILocatableGO> scInventoryObjects;

    public static <DESC_OBJ extends ILocatableGO & IDescribableGO>
    Collection<CreateNehmenAction<DESC_OBJ>> buildObjectActions(final AvDatabase db,
                                                                final TimeTaker timeTaker,
                                                                final Narrator n,
                                                                final World world,
                                                                final ImmutableList<DESC_OBJ> scInventoryObjects) {
        final ImmutableList.Builder<CreateNehmenAction<DESC_OBJ>> res =
                ImmutableList.builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(BINSENSUMPF)) {
            res.add(createBinsenAusrupfenAction(db, timeTaker, n, world, scInventoryObjects));

            // Sobald es hier weitere Dinge gibt, die im Binsensumpf erzeugt werden könnten,
            // muss buildMemorizedAction() angepasst werden, damit
            // isDefinitivFortsetzung() zu richtigen Ergebnissen kommt.
        }

        return res.build();
    }

    @NonNull
    private static <DESC_OBJ extends ILocatableGO & IDescribableGO>
    CreateNehmenAction<DESC_OBJ> createBinsenAusrupfenAction(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world,
            final ImmutableList<DESC_OBJ> scInventoryObjects) {
        return new CreateNehmenAction<>(db, timeTaker, n, world,
                GameObjectType.AUSGERUPFTE_BINSEN, scInventoryObjects);
    }

    private CreateNehmenAction(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n, final World world,
            final GameObjectType gameObjectType,
            final ImmutableList<DESC_OBJ> scInventoryObjects) {
        super(db.scActionStepCountDao(), timeTaker, n, world);

        onTheFlyGOFactory = new OnTheFlyGOFactory(db, timeTaker, world);
        this.gameObjectType = checkNotNull(gameObjectType, "gameObjectType");
        this.scInventoryObjects = checkNotNull(scInventoryObjects, "scInventoryObjects");
    }

    @Override
    public String getType() {
        return "actionNehmen";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        switch (gameObjectType) {
            case AUSGERUPFTE_BINSEN:
                return "Einige Binsen ausrupfen";
            default:
                throw new IllegalStateException("Unerwarteter Typ: " + gameObjectType);
        }
    }

    @Override
    protected void narrateAndDo() {
        switch (gameObjectType) {
            case AUSGERUPFTE_BINSEN:
                narrateAndDoBinsenAusrupfen();
                return;
            default:
                throw new IllegalStateException("Unerwarteter Typ: " + gameObjectType);
        }
    }

    private <GO extends GameObject & IDescribableGO & ILocatableGO>
    void narrateAndDoBinsenAusrupfen() {
        final GO neuAusgerupfteBinsen = (GO) onTheFlyGOFactory.createAusgerupfteBinsen();
        world.putOnTheFlyGameObject(neuAusgerupfteBinsen);

        world.narrateAndUpgradeScKnownAndAssumedState(neuAusgerupfteBinsen);

        narrateBinsenAusrupfen(neuAusgerupfteBinsen);

        neuAusgerupfteBinsen.locationComp()
                .narrateAndSetLocation(EINE_TASCHE_DES_SPIELER_CHARAKTERS);
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private <GO extends IDescribableGO & ILocatableGO>
    void narrateBinsenAusrupfen(final GO ausgerupfteBinsen) {
        final EinzelneSubstantivischePhrase objectDesc =
                world.getDescription(ausgerupfteBinsen, true);
        final PraedikatOhneLeerstellen praedikatMitObjekt = AUSRUPFEN.mit(objectDesc);

        if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
            n.narrate(du(praedikatMitObjekt.mitAdvAngabe(new AdvAngabeSkopusSatz("dann")))
                    .timed(secs(5))
                    .undWartest());
            return;
        }

        n.narrate(du(PARAGRAPH, praedikatMitObjekt)
                .timed(secs(5))
                .undWartest()
                .dann());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.CREATE_NEHMEN);
    }
}
