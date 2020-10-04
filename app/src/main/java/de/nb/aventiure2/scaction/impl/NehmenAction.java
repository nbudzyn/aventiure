package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAus;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;
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
                                                                final World world,
                                                                final GO object) {
        // STORY Gegenstände verloren gehen lassen, wenn nicht mehr nötig?

        final ImmutableList.Builder<NehmenAction<GO, TARGET_LOC>> res = ImmutableList.builder();

        res.add(new NehmenAction<>(db, world,
                object, EINE_TASCHE_DES_SPIELER_CHARAKTERS));

        return res.build();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TARGET_LOC extends IDescribableGO & ILocationGO & ILocatableGO>
    Collection<NehmenAction<LIVGO, TARGET_LOC>> buildCreatureActions(
            final AvDatabase db,
            final World world,
            final LIVGO creature) {
        if (world.isOrHasRecursiveLocation(creature, SPIELER_CHARAKTER)) {
            return ImmutableList.of();
        }

        if (!creature.locationComp().hasRecursiveLocation(
                world.loadSC().locationComp().getLocation())) {
            return ImmutableList.of();
        }

        if (creature.is(FROSCHPRINZ)) {
            return buildFroschprinzActions(db, world, creature);
        }

        return ImmutableList.of();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TARGET_LOC extends IDescribableGO & ILocationGO & ILocatableGO>
    Collection<NehmenAction<LIVGO, TARGET_LOC>> buildFroschprinzActions(
            final AvDatabase db,
            final World world,
            final LIVGO froschprinz) {
        if (((IHasStateGO<FroschprinzState>) froschprinz).stateComp()
                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            return ImmutableList.of(
                    new NehmenAction<>(db, world,
                            froschprinz, EINE_TASCHE_DES_SPIELER_CHARAKTERS));
        }

        if (((IHasStateGO<FroschprinzState>) froschprinz).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            if (world.loadSC().locationComp().hasLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
                return ImmutableList.of(
                        new NehmenAction<>(db, world,
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
    private NehmenAction(final AvDatabase db, final World world,
                         @NonNull final GO gameObject,
                         @NonNull final GameObjectId targetLocationId) {
        this(db, world, gameObject, (TARGET_LOC) world.load(targetLocationId));
    }

    /**
     * Erzeugt eine {@link NehmenAction}.
     *
     * @param gameObject     das genommene Objekt
     * @param targetLocation der Ort am SC, wohin es genommen wird, z.B.
     *                       in die Hände o.Ä.
     */
    private NehmenAction(final AvDatabase db, final World world,
                         @NonNull final GO gameObject, @NonNull final TARGET_LOC targetLocation) {
        super(db, world);

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
                .getInfinitiv(P1, SG));
    }

    @NonNull
    private PraedikatMitEinerObjektleerstelle getPraedikatFuerName() {
        if (targetLocation.is(HAENDE_DES_SPIELER_CHARAKTERS)) {
            return NEHMEN
                    .mitAdverbialerAngabe(
                            new AdverbialeAngabeSkopusVerbWohinWoher(
                                    "in " +
                                            targetLocation.descriptionComp()
                                                    .getDescription(true, true)
                                                    .im(AKK)
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

        n.addAlt(
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

        gameObject.locationComp()
                .narrateAndSetLocation(targetLocation,
                        () -> {
                            world.loadSC().memoryComp().upgradeKnown(gameObject);
                            sc.feelingsComp().setMood(NEUTRAL);

                            final SubstantivischePhrase froschDescOderAnapher =
                                    getAnaphPersPronWennMglSonstShortDescription(
                                            FROSCHPRINZ);

                            n.addAlt(
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
                            sc.feelingsComp().setMood(ANGESPANNT);
                        }
                );

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateFroschprinz_HatHochhebenGefordert() {
        if (isDefinitivDiskontinuitaet()) {
            if (n.requireNarration().dann()) {
                final SubstantivischePhrase froschDescOderAnapher =
                        getAnaphPersPronWennMglSonstShortDescription(FROSCHPRINZ);

                n.add(neuerSatz(PARAGRAPH,
                        "Aber dann nimmst du " + froschDescOderAnapher.akk() +
                                " doch wieder",
                        secs(5))
                        .undWartest()
                        .phorikKandidat(froschDescOderAnapher, FROSCHPRINZ));
                return;
            }

            final Nominalphrase froschDesc = world.getDescription(gameObject, false);

            final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
            alt.addAll(drueckeAus(DISKONTINUITAET,
                    du(PARAGRAPH,
                            "nimmst",
                            froschDesc.akk(),
                            secs(5))
                            .undWartest()
                            .phorikKandidat(froschDesc, FROSCHPRINZ),
                    du(PARAGRAPH,
                            NEHMEN.mitObj(froschDesc),
                            secs(5))
                            .undWartest()
                            .phorikKandidat(froschDesc, FROSCHPRINZ)));

            n.addAlt(alt);
            return;

        }
        n.addAlt(
                du(PARAGRAPH,
                        "zauderst", "und dein Herz klopft gewaltig, als du endlich "
                                + world.getDescription(gameObject, true).akk()
                                + " greifst",
                        secs(5))
                        .phorikKandidat(world.getDescription(gameObject, true), FROSCHPRINZ)
                        .komma()
                        .dann(),
                neuerSatz("Dir wird ganz angst, aber was man "
                                + "versprochen hat, das muss man auch halten! Du nimmst "
                                + world.getDescription(gameObject, true).akk()
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
            n.add(neuerSatz(
                    "Dann nimmst du " + objectDesc.akk(),
                    secs(5))
                    .undWartest()
                    .phorikKandidat(objectDesc.getNumerusGenus(), gameObject.getId()));
            return;
        }

        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            final Mood mood = sc.feelingsComp().getMood();

            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    mood.isEmotional()) {
                // TODO Es wäre gut, wenn das mitnehmenPraedikat direkt
                //  eine AbstractDuDescription erzeugen könnte, gleich mit dann etc.
                //  Oder wenn man vielleicht etwas ähliches wie eine AbstractDuDescription
                //  erzeugen könnte, die intern das mitnehmenPraedikat enthält.
                //  Leider müssen wir bis dahin eine AllgDescription bauen. :-(
                final Nominalphrase objectDesc = world.getDescription(gameObject, true);
                final PraedikatOhneLeerstellen praedikatMitObjekt =
                        mitnehmenPraedikat.mitObj(objectDesc);
                // STORY Neues Praedikat mit integrierter adverbialer Angabe in
                //  du(...) übergegben
                n.add(du(PARAGRAPH,
                        praedikatMitObjekt.mitAdverbialerAngabe(mood.getAdverbialeAngabe()),
                        secs(5))
                        .undWartest(
                                praedikatMitObjekt
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .phorikKandidat(objectDesc, gameObject.getId())
                        .dann());
                return;
            }
        }

        final PraedikatOhneLeerstellen praedikatMitObjekt =
                mitnehmenPraedikat.mitObj(world.getDescription(gameObject, true));
        n.add(
                du(PARAGRAPH, praedikatMitObjekt, secs(5))
                        // TODO Kann das .undWartest() bei Prädikat automatisch gesetzt werden?
                        .undWartest(
                                praedikatMitObjekt
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .dann());
    }

    private void narrateObjectDiskontinuitaet(
            final PraedikatMitEinerObjektleerstelle nehmenPraedikat) {
        final Nominalphrase objectDesc = world.getDescription(gameObject);
        final Nominalphrase objectDescShort = world.getDescription(gameObject, true);

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.addAll(drueckeAus(DISKONTINUITAET,
                du(PARAGRAPH,
                        nehmenPraedikat.mitObj(objectDesc),
                        secs(5))
                        .undWartest()
                        .phorikKandidat(objectDesc, gameObject.getId()),
                du(PARAGRAPH,
                        nehmenPraedikat.mitObj(objectDescShort),
                        secs(5))
                        .undWartest()
                        .phorikKandidat(objectDescShort, gameObject.getId()))
        );

        if (n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(
                    ", nur um "
                            + nehmenPraedikat
                            .mitObj(world.getDescription(gameObject, true).persPron())
                            .mitAdverbialerAngabe(
                                    new AdverbialeAngabeSkopusSatz("gleich erneut"))
                            .getZuInfinitiv(P2, SG),
                    secs(5))
                    // "zu nehmen", "an dich zu nehmen", "aufzuheben"
                    .komma()
                    .dann());
        }

        n.addAlt(alt);
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
