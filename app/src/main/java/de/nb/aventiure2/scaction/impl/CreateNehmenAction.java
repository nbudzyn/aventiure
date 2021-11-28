package de.nb.aventiure2.scaction.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
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
import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * Der Spielercharakter erzeugt ein neues Game Object und nimmt es an sich. (Beispiel: Er rupft
 * ein paar Binsen aus.)
 */
@ParametersAreNonnullByDefault
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class CreateNehmenAction extends AbstractScAction {
    private final OnTheFlyGOFactory onTheFlyGOFactory;

    /**
     * Der Typ des Objekts, das erzeugt wird und das der SC an sich nimmt.
     */
    private final GameObjectType gameObjectType;

    public static Collection<CreateNehmenAction> buildObjectActions(final AvDatabase db,
                                                                    final TimeTaker timeTaker,
                                                                    final Narrator n,
                                                                    final World world) {
        final ImmutableList.Builder<CreateNehmenAction> res =
                ImmutableList.builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(BINSENSUMPF)) {
            res.add(createBinsenAusrupfenAction(db, timeTaker, n, world));

            // Sobald es hier weitere Dinge gibt, die im Binsensumpf erzeugt werden könnten,
            // muss buildMemorizedAction() angepasst werden, damit
            // isDefinitivFortsetzung() zu richtigen Ergebnissen kommt.
        }

        return res.build();
    }

    @NonNull
    private static CreateNehmenAction createBinsenAusrupfenAction(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        return new CreateNehmenAction(db, timeTaker, n, world,
                GameObjectType.AUSGERUPFTE_BINSEN);
    }

    private CreateNehmenAction(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n, final World world,
            final GameObjectType gameObjectType) {
        super(db.scActionStepCountDao(), timeTaker, n, world);

        onTheFlyGOFactory = world.onTheFlyGOFactory();
        this.gameObjectType = checkNotNull(gameObjectType, "gameObjectType");
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

    private <AUSGERUFPFTE_BINSEN extends GameObject & IDescribableGO & ILocatableGO & IAmountableGO>
    void narrateAndDoBinsenAusrupfen() {
        final AUSGERUFPFTE_BINSEN neuAusgerupfteBinsen =
                onTheFlyGOFactory.createEinigeAusgerupfteBinsen();
        world.attachNew(neuAusgerupfteBinsen);

        narrateBinsenAusrupfen(neuAusgerupfteBinsen);

        world.narrateAndUpgradeScKnownAndAssumedState(neuAusgerupfteBinsen);
        world.narrateAndSetLocationOrIncAmount(neuAusgerupfteBinsen,
                EINE_TASCHE_DES_SPIELER_CHARAKTERS);

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private <AUSGERUFPFTE_BINSEN extends IDescribableGO & ILocatableGO & IAmountableGO>
    void narrateBinsenAusrupfen(final AUSGERUFPFTE_BINSEN ausgerupfteBinsen) {
        final ImmutableList<? extends AUSGERUFPFTE_BINSEN> binsenInTascheList =
                world.loadTypedInventory(EINE_TASCHE_DES_SPIELER_CHARAKTERS,
                        GameObjectType.AUSGERUPFTE_BINSEN);
        final int amountBinsenInTasche =
                binsenInTascheList.isEmpty() ?
                        0 :
                        binsenInTascheList.iterator().next().amountComp().getAmount();

        if (amountBinsenInTasche == 2) {
            n.narrateAlt(secs(10),
                    neuerSatz(SENTENCE,
                            "Schnell noch ein paar – jetzt trägst du eine ganze Menge",
                            "Binsenhalme bei dir"));
            return;
        }

        if (amountBinsenInTasche > 0) {
            final AltDescriptionsBuilder alt = alt();

            alt.add(du(SENTENCE, "rupfst", "einige weitere Binsen aus")
                            .undWartest().dann(),
                    du(SENTENCE, "rupfst", "noch ein paar Binsen aus")
                            .undWartest().dann(),
                    du(SENTENCE, "rupfst", "noch weitere Binsen aus")
                            .undWartest().dann(),
                    du(SENTENCE, "brichst", "weitere Binsen ab")
                            .undWartest().dann());

            if (amountBinsenInTasche > 4) {
                alt.add(neuerSatz("Je mehr, desto besser, hm?"));
            }

            n.narrateAlt(alt, secs(30));
            return;
        }

        final EinzelneSubstantivischePhrase objectDesc =
                getDescription(textContext, ausgerupfteBinsen, true);
        final SemPraedikatOhneLeerstellen praedikatMitObjekt = AUSRUPFEN.mit(objectDesc);

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
        return false;
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
