package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.world.gameobject.World.EINE_TASCHE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobject.World.HAENDE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.NEHMEN_IN;
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

    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<NehmenAction> buildObjectActions(final AvDatabase db,
                                                final World world,
                                                final StoryState initialStoryState,
                                                final GO object) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();

        res.add(new NehmenAction<>(db, world, initialStoryState,
                object, EINE_TASCHE_DES_SPIELER_CHARAKTERS));

        return res.build();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<NehmenAction> buildCreatureActions(
            final AvDatabase db,
            final World world,
            final StoryState initialStoryState,
            final LIVGO creature) {
        if (creature.is(FROSCHPRINZ)) {
            return buildFroschprinzActions(db, world, initialStoryState, creature);
        }
        return ImmutableList.of();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<NehmenAction> buildFroschprinzActions(
            final AvDatabase db,
            final World world,
            final StoryState initialStoryState,
            final LIVGO froschprinz) {
        if (((IHasStateGO<FroschprinzState>) froschprinz).stateComp()
                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            return ImmutableList.of(
                    new NehmenAction<>(db, world, initialStoryState,
                            froschprinz, EINE_TASCHE_DES_SPIELER_CHARAKTERS));
        }

        if (((IHasStateGO<FroschprinzState>) froschprinz).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return ImmutableList.of(
                    new NehmenAction<>(db, world, initialStoryState,
                            froschprinz, HAENDE_DES_SPIELER_CHARAKTERS));
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
    private NehmenAction(final AvDatabase db, final World world,
                         final StoryState initialStoryState,
                         @NonNull final GO gameObject,
                         @NonNull final GameObjectId targetLocationId) {
        this(db, world, initialStoryState, gameObject, (TARGET_LOC) world.load(targetLocationId));
    }

    /**
     * Erzeugt eine {@link NehmenAction}.
     *
     * @param gameObject     das genommene Objekt
     * @param targetLocation der Ort am SC, wohin es genommen wird, z.B.
     *                       in die Hände o.Ä.
     */
    private NehmenAction(final AvDatabase db, final World world,
                         final StoryState initialStoryState,
                         @NonNull final GO gameObject, @NonNull final TARGET_LOC targetLocation) {
        super(db, world, initialStoryState);

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

        return capitalize(praedikat.mitObj(world.getDescription(gameObject, true))
                // Relevant für etwas wie "Die Schale an *mich* nehmen"
                .getDescriptionInfinitiv(P1, SG));
    }

    @NonNull
    private PraedikatMitEinerObjektleerstelle getPraedikatFuerName() {
        if (targetLocation.is(HAENDE_DES_SPIELER_CHARAKTERS)) {
            return NEHMEN_IN.mitPraep(
                    targetLocation.descriptionComp()
                            .getDescription(true, true)); // "in die Hände nehmen"
        }

        return gameObject instanceof ILivingBeingGO ? MITNEHMEN : NEHMEN;
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        if (gameObject instanceof ILivingBeingGO) {
            return narrateAndDoLivingBeing();
        }
        return narrateAndDoObject();
    }

    private AvTimeSpan narrateAndDoLivingBeing() {
        checkState(gameObject.is(FROSCHPRINZ),
                "Unexpected creature: " + gameObject);
        checkState(((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                HAT_HOCHHEBEN_GEFORDERT),
                "Unexpected state: " + gameObject);

        return narrateAndDoFroschprinz();
    }

    private AvTimeSpan narrateAndDoFroschprinz() {
        if (((IHasStateGO<FroschprinzState>) gameObject).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return narrateAndDoFroschprinz_HatHochhebenGefordert();
        }

        final Nominalphrase froschDesc = world.getDescription(gameObject, true);

        AvTimeSpan timeElapsed = n.addAlt(
                du(PARAGRAPH,
                        "ekelst",
                        "dich sehr, aber mit einiger Überwindung nimmst du "
                                + froschDesc.akk()
                                + " in "
                                + "die Hand", secs(10))
                        .phorikKandidat(froschDesc, FROSCHPRINZ)
                        .undWartest()
                        .dann(),
                neuerSatz(PARAGRAPH,
                        capitalize(froschDesc.akk())
                                + " in die Hand nehmen?? – Wer hat dir bloß solche Flausen "
                                + "in den Kopf gesetzt! Kräftig packst du "
                                + froschDesc.akk(), secs(10))
                        .phorikKandidat(froschDesc, FROSCHPRINZ)
                        .undWartest()
                        .dann(),
                du(PARAGRAPH,
                        "erbarmst", "dich", secs(10))
                        .undWartest()
        );

        timeElapsed = timeElapsed.plus(
                gameObject.locationComp()
                        .narrateAndSetLocation(targetLocation,
                                () -> {
                                    world.upgradeKnownToSC(gameObject);
                                    sc.feelingsComp().setMood(NEUTRAL);

                                    final SubstantivischePhrase froschDescOderAnapher =
                                            getAnaphPersPronWennMglSonstShortDescription(
                                                    FROSCHPRINZ);

                                    return n.addAlt(
                                            neuerSatz(
                                                    capitalize(
                                                            froschDescOderAnapher.nom()) // "er"
                                                            + " ist glibschig und "
                                                            + "schleimig – pfui-bäh! – schnell lässt du "
                                                            + froschDescOderAnapher.persPron().akk()
                                                            + " in "
                                                            + "eine Tasche gleiten. "
                                                            + capitalize(
                                                            froschDescOderAnapher.possArt()
                                                                    .vor(NumerusGenus.N).nom())
                                                            + " gedämpftes Quaken könnte "
                                                            + "wohlig sein oder "
                                                            + "genauso gut vorwurfsvoll", secs(10))
                                                    .beendet(PARAGRAPH),
                                            du("versenkst",
                                                    froschDescOderAnapher.akk() // "ihn"
                                                            + " tief in deine Tasche. Du "
                                                            + "versuchst, deine Hand an der "
                                                            + "Kleidung zu reinigen, aber der "
                                                            + "Schleim verteilt sich nur "
                                                            + "überall – igitt!",
                                                    "tief in deine Tasche", secs(10))
                                                    .beendet(PARAGRAPH),
                                            du("packst",
                                                    froschDescOderAnapher.akk() // "ihn"
                                                            + " in deine Tasche. "
                                                            + capitalize(
                                                            froschDesc.persPron().nom())
                                                            + " fasst "
                                                            + "sich sehr eklig an und du bist "
                                                            + "glücklich, als die Prozedur "
                                                            + "vorbei ist.", secs(10))
                                                    .dann()
                                    );
                                })
        );

        sc.memoryComp().setLastAction(buildMemorizedAction());
        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoFroschprinz_HatHochhebenGefordert() {
        AvTimeSpan timeElapsed = narrateFroschprinz_HatHochhebenGefordert();

        timeElapsed = timeElapsed.plus(
                gameObject.locationComp()
                        .narrateAndSetLocation(
                                targetLocation,
                                () -> {
                                    world.upgradeKnownToSC(gameObject);
                                    sc.feelingsComp().setMood(ANGESPANNT);

                                    return noTime();
                                })
        );

        sc.memoryComp().setLastAction(buildMemorizedAction());
        return timeElapsed;
    }

    private AvTimeSpan narrateFroschprinz_HatHochhebenGefordert() {
        if (isDefinitivDiskontinuitaet()) {
            if (initialStoryState.dann()) {
                final SubstantivischePhrase froschDescOderAnapher =
                        getAnaphPersPronWennMglSonstShortDescription(FROSCHPRINZ);

                return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                        "Aber dann nimmst du " + froschDescOderAnapher.akk() +
                                " doch wieder",
                        secs(5))
                        .undWartest()
                        .phorikKandidat(froschDescOderAnapher, FROSCHPRINZ));
            }

            final Nominalphrase froschDesc = world.getDescription(gameObject, false);

            return n.add(du(StructuralElement.PARAGRAPH,
                    "nimmst",
                    froschDesc.akk() + " noch einmal",
                    "noch einmal",
                    secs(5))
                    .undWartest()
                    .phorikKandidat(froschDesc, FROSCHPRINZ));

        }
        return n.addAlt(
                du(PARAGRAPH,
                        "zauderst", "und dein Herz klopft gewaltig, als du endlich "
                                + world.getDescription(gameObject, true).akk()
                                + " greifst",
                        secs(5))
                        .phorikKandidat(world.getDescription(gameObject, true), FROSCHPRINZ)
                        .komma()
                        .dann(),
                neuerSatz(SENTENCE,
                        "Dir wird ganz angst, aber was man "
                                + "versprochen hat, das muss man auch halten! Du nimmst "
                                + world.getDescription(gameObject, true).akk()
                                + " in die Hände",
                        secs(15))
                        .phorikKandidat(world.getDescription(gameObject, true), FROSCHPRINZ)
                        .undWartest()
                        .dann());
    }

    private AvTimeSpan narrateAndDoObject() {
        world.upgradeKnownToSC(gameObject, gameObject.locationComp().getLocation());

        AvTimeSpan timeElapsed = narrateObject();

        timeElapsed = timeElapsed.plus(
                gameObject.locationComp().narrateAndSetLocation(targetLocation));
        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeElapsed;
    }


    @NonNull
    private AvTimeSpan narrateObject() {
        final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat =
                gameObject.locationComp().getLocation()
                        .storingPlaceComp().getLocationMode().getMitnehmenPraedikat();

        if (isDefinitivDiskontinuitaet()) {
            return narrateObjectDiskontinuitaet(mitnehmenPraedikat);
        }

        if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
            final Nominalphrase objectDesc = world.getDescription(gameObject, true);
            return n.add(neuerSatz(
                    "Dann nimmst du " + objectDesc.akk(),
                    secs(5))
                    .undWartest()
                    .phorikKandidat(objectDesc.getNumerusGenus(), gameObject.getId()));
        }

        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            final Mood mood = sc.feelingsComp().getMood();

            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    mood.isEmotional()) {
                // TODO Es wäre gut, wenn das mitnehmenPraedikat direkt
                //  eine DuDescription erzeugen könnte, gleich mit dann etc.
                //  Oder wenn man vielleicht etwas ähliches wie eine DuDescription
                //  erzeugen könnte, die intern das mitnehmenPraedikat enthält.
                //  Leider müssen wir bis dahin eine AllgDescription bauen. :-(
                final Nominalphrase objectDesc = world.getDescription(gameObject, true);
                return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                        mitnehmenPraedikat
                                .getDescriptionDuHauptsatz(
                                        objectDesc,
                                        mood.getAdverbialeAngabe()),
                        secs(5))
                        .undWartest(
                                mitnehmenPraedikat
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .phorikKandidat(objectDesc, gameObject.getId())
                        .dann());
            }
        }

        return n.add(
                neuerSatz(PARAGRAPH,
                        mitnehmenPraedikat
                                .getDescriptionDuHauptsatz(world.getDescription(gameObject, true)),
                        secs(5))
                        .undWartest(
                                mitnehmenPraedikat
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .dann());
    }

    private AvTimeSpan narrateObjectDiskontinuitaet(
            final PraedikatMitEinerObjektleerstelle nehmenPraedikat) {
        if (initialStoryState.dann()) {
            final Nominalphrase objectDesc = world.getDescription(gameObject);
            return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                    "Dann nimmst du " + objectDesc.akk() +
                            " erneut",
                    secs(5))
                    .undWartest()
                    .phorikKandidat(objectDesc, gameObject.getId()));
        }

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            return n.add(satzanschluss(
                    ", nur um "
                            + nehmenPraedikat
                            .mitObj(world.getDescription(gameObject, true).persPron())
                            .getDescriptionZuInfinitiv(
                                    P2, SG,
                                    new AdverbialeAngabe(
                                            "gleich erneut")),
                    secs(5))
                    // "zu nehmen", "an dich zu nehmen", "aufzuheben"
                    .komma()
                    .dann());
        }

        final Nominalphrase objectDesc = world.getDescription(gameObject, true);
        return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                "Ach nein, "
                        // du nimmst die Kugel besser doch
                        + uncapitalize(nehmenPraedikat
                        .mitObj(objectDesc)
                        .getDescriptionDuHauptsatz(
                                new Modalpartikel("besser"),
                                new Modalpartikel("doch"))),
                secs(5))
                .undWartest()
                .dann()
                .phorikKandidat(objectDesc.getNumerusGenus(), gameObject.getId()));
    }


    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
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
