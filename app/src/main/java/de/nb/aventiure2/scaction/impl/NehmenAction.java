package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAusTimed;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.DISKONTINUITAET;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MITNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand (oder in Ausnahmefällen eine
 * Creature) an sich.
 */
@ParametersAreNonnullByDefault
public class NehmenAction
        <GO extends IDescribableGO & ILocatableGO,
                TARGET_LOC extends IDescribableGO & ILocationGO & ILocatableGO>
        extends AbstractScAction {
    @NonNull
    private final GO gameObject;

    private final TARGET_LOC targetLocation;

    public static <GO extends IDescribableGO & ILocatableGO,
            TARGET_LOC extends IDescribableGO & ILocationGO & ILocatableGO>
    Collection<NehmenAction<GO, TARGET_LOC>> buildObjectActions(final AvDatabase db,
                                                                final TimeTaker timeTaker,
                                                                final Narrator n,
                                                                final World world,
                                                                final GO object) {
        // IDEA Gegenstände verloren gehen lassen, wenn nicht mehr nötig?

        final ImmutableList.Builder<NehmenAction<GO, TARGET_LOC>> res = ImmutableList.builder();

        res.add(new NehmenAction<>(db.scActionStepCountDao(), timeTaker, n, world,
                object, EINE_TASCHE_DES_SPIELER_CHARAKTERS));

        return res.build();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TARGET_LOC extends IDescribableGO & ILocationGO & ILocatableGO>
    Collection<NehmenAction<LIVGO, TARGET_LOC>> buildCreatureActions(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n, final World world,
            final LIVGO creature) {
        if (world.isOrHasRecursiveLocation(creature, SPIELER_CHARAKTER)) {
            return ImmutableList.of();
        }

        if (!creature.locationComp().hasRecursiveLocation(
                world.loadSC().locationComp().getLocation())) {
            return ImmutableList.of();
        }

        if (creature.is(FROSCHPRINZ)) {
            return buildFroschprinzActions(db, timeTaker, n, world, creature);
        }

        return ImmutableList.of();
    }

    private static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TARGET_LOC extends IDescribableGO & ILocationGO & ILocatableGO>
    Collection<NehmenAction<LIVGO, TARGET_LOC>> buildFroschprinzActions(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n,
            final World world,
            final LIVGO froschprinz) {
        if (((IHasStateGO<FroschprinzState>) froschprinz).stateComp()
                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            return ImmutableList.of(
                    new NehmenAction<>(db.scActionStepCountDao(), timeTaker, n, world,
                            froschprinz, EINE_TASCHE_DES_SPIELER_CHARAKTERS));
        }

        if (((IHasStateGO<FroschprinzState>) froschprinz).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            if (world.loadSC().locationComp().hasLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
                return ImmutableList.of(
                        new NehmenAction<>(db.scActionStepCountDao(), timeTaker, n, world,
                                froschprinz, HAENDE_DES_SPIELER_CHARAKTERS));
            }
        }

        return ImmutableList.of();
    }

    /**
     * Erzeugt eine {@link NehmenAction}.
     *
     * @param gameObject       das genommene Objekt
     * @param targetLocationId der Ort am SC, wohin es genommen wird, z.B.
     *                         in die Hände o.Ä.
     */
    private NehmenAction(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            @NonNull final GO gameObject,
            @NonNull final GameObjectId targetLocationId) {
        this(scActionStepCountDao, timeTaker, n, world,
                gameObject, (TARGET_LOC) world.load(targetLocationId));
    }

    /**
     * Erzeugt eine {@link NehmenAction}.
     *
     * @param gameObject     das genommene Objekt
     * @param targetLocation der Ort am SC, wohin es genommen wird, z.B.
     *                       in die Hände o.Ä.
     */
    private NehmenAction(final SCActionStepCountDao scActionStepCountDao,
                         final TimeTaker timeTaker, final Narrator n, final World world,
                         @NonNull final GO gameObject, @NonNull final TARGET_LOC targetLocation) {
        super(scActionStepCountDao, timeTaker, n, world);

        checkArgument(gameObject.locationComp().getLocation() != null);
        checkArgument(targetLocation.locationComp().hasLocation(SPIELER_CHARAKTER),
                "Nehmen bedeutet: Zum Spielercharakter");

        this.gameObject = gameObject;
        this.targetLocation = targetLocation;
    }

    @Override
    public String getType() {
        return "actionNehmen";
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat = getPraedikatFuerName();

        return GermanUtil.capitalize(
                praedikat.mit(world.getDescription(gameObject, true))
                        // Relevant für etwas wie "Die Schale an *mich* nehmen"
                        .getInfinitiv(P1, SG).joinToString(
                ));
    }

    @NonNull
    private PraedikatMitEinerObjektleerstelle getPraedikatFuerName() {
        if (targetLocation.is(HAENDE_DES_SPIELER_CHARAKTERS)) {
            return NEHMEN
                    .mitAdverbialerAngabe(
                            new AdverbialeAngabeSkopusVerbWohinWoher(
                                    PraepositionMitKasus.IN_AKK
                                            .mit(targetLocation.descriptionComp()
                                                    .getDescription(true, true))
                                            .getDescription()
                            )); // "in die Hände nehmen"
        }

        return gameObject instanceof ILivingBeingGO ? MITNEHMEN : NEHMEN;
    }

    @Override
    public void narrateAndDo() {
        if (gameObject instanceof ILivingBeingGO) {
            narrateAndDoLivingBeing();
            return;
        }
        narrateAndDoObject();
    }

    private void narrateAndDoLivingBeing() {
        checkState(gameObject.is(FROSCHPRINZ),
                "Unexpected creature: " + gameObject);
        checkState(((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                HAT_HOCHHEBEN_GEFORDERT),
                "Unexpected state: " + gameObject);

        narrateAndDoFroschprinz();
    }

    private void narrateAndDoFroschprinz() {
        if (((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            narrateAndDoFroschprinz_HatHochhebenGefordert();
            return;
        }

        final Nominalphrase froschDesc = world.getDescription(gameObject, true);

        n.narrateAlt(secs(10),
                du(PARAGRAPH,
                        "ekelst",
                        "dich sehr, aber mit einiger Überwindung nimmst du "
                                + froschDesc.akkStr()
                                + " in "
                                + "die Hand")
                        .phorikKandidat(froschDesc, FROSCHPRINZ)
                        .undWartest()
                        .dann(),
                neuerSatz(PARAGRAPH,
                        froschDesc.akkStr()
                                + " in die Hand nehmen? – Wer hat dir bloß solche Flausen "
                                + "in den Kopf gesetzt! Kräftig packst du "
                                + froschDesc.akkStr())
                        .phorikKandidat(froschDesc, FROSCHPRINZ)
                        .undWartest()
                        .dann(),
                du(PARAGRAPH,
                        "erbarmst", "dich")
                        .undWartest()
        );

        gameObject.locationComp()
                .narrateAndSetLocation(targetLocation,
                        () -> {
                            world.loadSC().memoryComp().upgradeKnown(gameObject);
                            sc.feelingsComp().requestMood(NEUTRAL);

                            final SubstantivischePhrase froschDescOderAnapher =
                                    world.getAnaphPersPronWennMglSonstShortDescription(
                                            FROSCHPRINZ);

                            n.narrateAlt(
                                    neuerSatz(
                                            froschDescOderAnapher.nomStr()// "Er"
                                                    + " ist glibschig und "
                                                    + "schleimig – pfui-bäh! – schnell lässt du "
                                                    + froschDescOderAnapher.persPron().akkStr()
                                                    + " in "
                                                    + "eine Tasche gleiten. "
                                                    + GermanUtil.capitalize(
                                                    froschDescOderAnapher.possArt()
                                                            .vor(NumerusGenus.N).nomStr())
                                                    + " gedämpftes Quaken könnte "
                                                    + "wohlig sein oder "
                                                    + "genauso gut vorwurfsvoll", secs(10))
                                            .beendet(PARAGRAPH),
                                    du("versenkst",
                                            froschDescOderAnapher.akkStr() // "ihn"
                                                    + " tief in deine Tasche. Du "
                                                    + "versuchst, deine Hand an der "
                                                    + "Kleidung zu reinigen, aber der "
                                                    + "Schleim verteilt sich nur "
                                                    + "überall – igitt!",
                                            "tief in deine Tasche", secs(10))
                                            .beendet(PARAGRAPH),
                                    du("packst",
                                            froschDescOderAnapher.akkStr() // "ihn"
                                                    + " in deine Tasche. "
                                                    + GermanUtil.capitalize(
                                                    froschDesc.persPron().nomStr())
                                                    + " fasst "
                                                    + "sich sehr eklig an und du bist "
                                                    + "glücklich, als die Prozedur "
                                                    + "vorbei ist.", secs(10))
                                            .dann()
                            );
                        }
                );

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoFroschprinz_HatHochhebenGefordert() {
        narrateFroschprinz_HatHochhebenGefordert();

        gameObject.locationComp()
                .narrateAndSetLocation(
                        targetLocation,
                        () -> {
                            world.loadSC().memoryComp().upgradeKnown(gameObject);
                            sc.feelingsComp().requestMood(ANGESPANNT);
                        }
                );

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateFroschprinz_HatHochhebenGefordert() {
        if (isDefinitivDiskontinuitaet()) {
            if (n.dann()) {
                final SubstantivischePhrase froschDescOderAnapher =
                        world.getAnaphPersPronWennMglSonstShortDescription(
                                FROSCHPRINZ);

                n.narrate(neuerSatz(PARAGRAPH,
                        "Aber dann nimmst du " + froschDescOderAnapher.akkStr() +
                                " doch wieder",
                        secs(5))
                        .undWartest()
                        .phorikKandidat(froschDescOderAnapher, FROSCHPRINZ));
                return;
            }

            final Nominalphrase froschDesc = world.getDescription(gameObject, false);

            final ImmutableList.Builder<TimedDescription<?>> alt = builder();
            alt.addAll(drueckeAusTimed(DISKONTINUITAET,
                    du(PARAGRAPH,
                            "nimmst",
                            froschDesc.akkStr(),
                            secs(5))
                            .undWartest()
                            .phorikKandidat(froschDesc, FROSCHPRINZ),
                    du(PARAGRAPH,
                            NEHMEN.mit(froschDesc),
                            secs(5))
                            .undWartest()
                            .phorikKandidat(froschDesc, FROSCHPRINZ)));

            n.narrateAlt(alt);
            return;

        }
        n.narrateAlt(
                du(PARAGRAPH,
                        "zauderst", "und dein Herz klopft gewaltig, als du endlich "
                                + world.getDescription(gameObject, true).akkStr()
                                + " greifst",
                        secs(5))
                        .phorikKandidat(world.getDescription(gameObject, true), FROSCHPRINZ)
                        .komma()
                        .dann(),
                neuerSatz("Dir wird ganz angst, aber was man "
                                + "versprochen hat, das muss man auch halten! Du nimmst "
                                + world.getDescription(gameObject, true).akkStr()
                                + " in die Hände",
                        secs(15))
                        .phorikKandidat(world.getDescription(gameObject, true), FROSCHPRINZ)
                        .undWartest()
                        .dann());
    }

    private void narrateAndDoObject() {
        world.loadSC().memoryComp().upgradeKnown(gameObject);

        narrateObject();

        gameObject.locationComp().narrateAndSetLocation(targetLocation);
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }


    private void narrateObject() {
        final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat =
                gameObject.locationComp().getLocation()
                        .storingPlaceComp().getLocationMode().getMitnehmenPraedikat();

        if (isDefinitivDiskontinuitaet()) {
            narrateObjectDiskontinuitaet(mitnehmenPraedikat);
            return;
        }

        if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
            final Nominalphrase objectDesc = world.getDescription(gameObject, true);
            n.narrate(neuerSatz(
                    "Dann nimmst du " + objectDesc.akkStr(),
                    secs(5))
                    .undWartest()
                    .phorikKandidat(objectDesc.getNumerusGenus(), gameObject.getId()));
            return;
        }

        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    sc.feelingsComp().isEmotional()) {
                final Nominalphrase objectDesc = world.getDescription(gameObject, true);
                final PraedikatOhneLeerstellen praedikatMitObjekt =
                        mitnehmenPraedikat.mit(objectDesc);

                n.narrateAlt(
                        sc.feelingsComp().altAdverbialeAngabenSkopusSatz().stream()
                                .map(a ->
                                        du(PARAGRAPH,
                                                praedikatMitObjekt.mitAdverbialerAngabe(a),
                                                secs(5))
                                                .undWartest(
                                                        praedikatMitObjekt
                                                                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen())
                                                .phorikKandidat(objectDesc, gameObject.getId())
                                                .dann())
                                .collect(toImmutableList()));
                return;
            }
        }

        final PraedikatOhneLeerstellen praedikatMitObjekt =
                mitnehmenPraedikat.mit(world.getDescription(gameObject, true));
        n.narrate(
                du(PARAGRAPH, praedikatMitObjekt, secs(5))
                        .undWartest(
                                praedikatMitObjekt
                                        .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen())
                        .dann());
    }

    private void narrateObjectDiskontinuitaet(
            final PraedikatMitEinerObjektleerstelle nehmenPraedikat) {
        final Nominalphrase objectDesc = world.getDescription(gameObject);
        final Nominalphrase objectDescShort = world.getDescription(gameObject, true);

        final ImmutableList.Builder<TimedDescription<?>> alt = ImmutableList.builder();

        alt.addAll(drueckeAusTimed(DISKONTINUITAET,
                du(PARAGRAPH,
                        nehmenPraedikat.mit(objectDesc),
                        secs(5))
                        .undWartest()
                        .phorikKandidat(objectDesc, gameObject.getId()),
                du(PARAGRAPH,
                        nehmenPraedikat.mit(objectDescShort),
                        secs(5))
                        .undWartest()
                        .phorikKandidat(objectDescShort, gameObject.getId()))
        );

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(
                    ", nur um "
                            +
                            nehmenPraedikat
                                    .mit(world.getDescription(gameObject, true).persPron())
                                    .mitAdverbialerAngabe(
                                            new AdverbialeAngabeSkopusSatz("gleich erneut"))
                                    .getZuInfinitiv(P2, SG).joinToString(
                            ),
                    secs(5))
                    // "zu nehmen", "an dich zu nehmen", "aufzuheben"
                    .komma()
                    .dann());
        }

        n.narrateAlt(alt);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return !n.lastNarrationWasFromReaction() &&
                sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN) &&
                sc.memoryComp().getLastAction().hasObject(gameObject);
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.NEHMEN, gameObject,
                gameObject.locationComp().getLocation());
    }
}
