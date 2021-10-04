package de.nb.aventiure2.scaction.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAusTimed;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.DISKONTINUITAET;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_ANFASSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MITNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import de.nb.aventiure2.data.world.syscomp.location.ILocatableLocationGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand (oder in Ausnahmefällen eine
 * Creature) an sich.
 */
@ParametersAreNonnullByDefault
public class NehmenAction
        <GO extends IDescribableGO & ILocatableGO,
                TARGET_LOC extends IDescribableGO & ILocatableLocationGO>
        extends AbstractScAction {
    @NonNull
    private final GO gameObject;

    private final TARGET_LOC targetLocation;

    public static <GO extends IDescribableGO & ILocatableGO,
            TARGET_LOC extends IDescribableGO & ILocatableLocationGO>
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
            TARGET_LOC extends IDescribableGO & ILocatableLocationGO>
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

    @SuppressWarnings("unchecked")
    private static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TARGET_LOC extends IDescribableGO & ILocatableLocationGO>
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
    @SuppressWarnings("unchecked")
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

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat = getPraedikatFuerName();

        return joinToKonstituentenfolge(
                SENTENCE,
                praedikat.mit(world.getDescription(gameObject, true))
                        // Relevant für etwas wie "Die Schale an *mich* nehmen"
                        .getInfinitiv(P2, SG))
                .joinToString();
    }

    @NonNull
    private PraedikatMitEinerObjektleerstelle getPraedikatFuerName() {
        if (targetLocation.is(HAENDE_DES_SPIELER_CHARAKTERS)) {
            return NEHMEN
                    .mitAdvAngabe(
                            new AdvAngabeSkopusVerbWohinWoher(
                                    PraepositionMitKasus.IN_AKK
                                            .mit(targetLocation.descriptionComp()
                                                    .getShortDescriptionWhenKnown())
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

    @SuppressWarnings("unchecked")
    private void narrateAndDoLivingBeing() {
        checkState(gameObject.is(FROSCHPRINZ),
                "Unexpected creature: %s", gameObject);
        checkState(((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                HAT_HOCHHEBEN_GEFORDERT),
                "Unexpected state: %s", gameObject);

        narrateAndDoFroschprinz();
    }

    @SuppressWarnings("unchecked")
    private void narrateAndDoFroschprinz() {
        if (((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            narrateAndDoFroschprinz_HatHochhebenGefordert();
            return;
        }

        final EinzelneSubstantivischePhrase froschDesc = world.getDescription(gameObject, true);

        n.narrateAlt(secs(10),
                du(PARAGRAPH,
                        "ekelst",
                        "dich sehr, aber mit einiger Überwindung nimmst du",
                        froschDesc.akkK(),
                        "in die Hand")
                        .schonLaenger()
                        .undWartest()
                        .dann(),
                neuerSatz(PARAGRAPH,
                        froschDesc.akkK(),
                        " in die Hand nehmen? – Wer hat dir bloß solche Flausen",
                        "in den Kopf gesetzt! Kräftig packst du",
                        froschDesc.akkK())
                        .undWartest()
                        .dann(),
                du(PARAGRAPH,
                        "erbarmst", "dich")
                        .schonLaenger()
                        .undWartest()
        );

        gameObject.locationComp()
                .narrateAndSetLocation(targetLocation,
                        () -> {
                            world.narrateAndUpgradeScKnownAndAssumedState(gameObject);
                            sc.feelingsComp().requestMood(NEUTRAL);

                            final SubstantivischePhrase anaph = world.anaph(FROSCHPRINZ);

                            n.narrateAlt(
                                    neuerSatz(anaph.nomK(),// "Er"
                                            SeinUtil.istSind(anaph),
                                            "glibschig und",
                                            "schleimig – pfui-bäh! – schnell lässt du",
                                            anaph.persPron().akkK(),
                                            "in eine Tasche gleiten",
                                            SENTENCE,
                                            anaph.possArt().vor(NumerusGenus.N).nomStr(),
                                            "gedämpftes Quaken könnte",
                                            "wohlig sein oder",
                                            "genauso gut vorwurfsvoll", PARAGRAPH)
                                            .timed(secs(10)),
                                    du("versenkst", anaph.akkK(), // "ihn"
                                            "tief in deine Tasche. Du",
                                            "versuchst, deine Hand an der",
                                            "Kleidung zu reinigen, aber der",
                                            "Schleim verteilt sich nur",
                                            "überall – igitt!", PARAGRAPH)
                                            .mitVorfeldSatzglied("tief in deine Tasche")
                                            .timed(secs(10)),
                                    du("packst",
                                            anaph.akkK(), // "ihn"
                                            "in deine Tasche",
                                            SENTENCE,
                                            froschDesc.persPron().nomK(),
                                            SICH_ANFASSEN.getPraesensOhnePartikel(anaph),
                                            "sich sehr eklig an und du bist",
                                            "glücklich, als die Prozedur",
                                            "vorbei ist.").timed(secs(10))
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
                            world.narrateAndUpgradeScKnownAndAssumedState(gameObject);
                            sc.feelingsComp().requestMood(ANGESPANNT);
                        }
                );

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateFroschprinz_HatHochhebenGefordert() {
        if (isDefinitivDiskontinuitaet()) {
            if (n.dann()) {
                final SubstantivischePhrase anaph = world.anaph(FROSCHPRINZ);

                n.narrate(neuerSatz(PARAGRAPH, "Aber dann nimmst du",
                        anaph.akkK(),
                        "doch wieder")
                        .timed(secs(5))
                        .undWartest());
                return;
            }

            final EinzelneSubstantivischePhrase froschDesc =
                    world.getDescription(gameObject, false);

            final AltTimedDescriptionsBuilder alt = altTimed();
            alt.addAll(drueckeAusTimed(DISKONTINUITAET,
                    du(PARAGRAPH, NEHMEN.mit(froschDesc))
                            .timed(secs(5))
                            .undWartest()));

            n.narrateAlt(alt);
            return;

        }
        n.narrateAlt(
                du(PARAGRAPH, "zauderst",
                        "und dein Herz klopft gewaltig, als du endlich",
                        world.getDescription(gameObject, true).akkK(),
                        "greifst")
                        .schonLaenger()
                        .timed(secs(5))
                        .komma()
                        .dann(),
                neuerSatz("Dir wird ganz angst, aber was man",
                        "versprochen hat, das muss man auch halten! Du nimmst",
                        world.getDescription(gameObject, true).akkK(),
                        "in die Hände")
                        .timed(secs(15))
                        .undWartest()
                        .dann());
    }

    private void narrateAndDoObject() {
        world.narrateAndUpgradeScKnownAndAssumedState(gameObject);

        narrateObject();

        gameObject.locationComp().narrateAndSetLocation(targetLocation);
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateObject() {
        final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat =
                requireNonNull(gameObject.locationComp().getMitnehmenPraedikat(
                        gameObject.locationComp().isVielteilig()));

        if (isDefinitivDiskontinuitaet()) {
            narrateObjectDiskontinuitaet(mitnehmenPraedikat);
            return;
        }

        if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
            final EinzelneSubstantivischePhrase objectDesc = world.getDescription(gameObject, true);
            n.narrate(neuerSatz("Dann nimmst du", objectDesc.akkK())
                    .timed(secs(5))
                    .undWartest());
            return;
        }

        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    sc.feelingsComp().isEmotional()) {
                final EinzelneSubstantivischePhrase
                        objectDesc = world.getDescription(gameObject, true);
                final PraedikatOhneLeerstellen praedikatMitObjekt =
                        mitnehmenPraedikat.mit(objectDesc);

                n.narrateAlt(
                        sc.feelingsComp().altAdvAngabenSkopusSatz().stream()
                                .map(a -> du(PARAGRAPH,
                                        praedikatMitObjekt.mitAdvAngabe(a))
                                        .timed(secs(5))
                                        .undWartest(
                                                praedikatMitObjekt
                                                        .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen())
                                        .dann()));
                return;
            }
        }

        final PraedikatOhneLeerstellen praedikatMitObjekt =
                mitnehmenPraedikat.mit(world.getDescription(gameObject, true));
        n.narrate(
                du(PARAGRAPH, praedikatMitObjekt)
                        .timed(secs(5))
                        .undWartest(
                                praedikatMitObjekt
                                        .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen())
                        .dann());
    }

    private void narrateObjectDiskontinuitaet(
            final PraedikatMitEinerObjektleerstelle nehmenPraedikat) {
        final EinzelneSubstantivischePhrase objectDesc = world.getDescription(gameObject);
        final EinzelneSubstantivischePhrase objectDescShort =
                world.getDescription(gameObject, true);

        final AltTimedDescriptionsBuilder alt = altTimed();

        alt.addAll(drueckeAusTimed(DISKONTINUITAET,
                du(PARAGRAPH, nehmenPraedikat.mit(objectDesc)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder")))
                        .timed(secs(5))
                        .undWartest(),
                du(PARAGRAPH, nehmenPraedikat.mit(objectDescShort)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder")))
                        .timed(secs(5))
                        .undWartest()));

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(", nur um",
                    nehmenPraedikat
                            .mit(world.getDescription(gameObject, true).persPron())
                            .mitAdvAngabe(
                                    new AdvAngabeSkopusSatz("gleich erneut"))
                            .getZuInfinitiv(P2, SG))
                    .timed(secs(5))
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
