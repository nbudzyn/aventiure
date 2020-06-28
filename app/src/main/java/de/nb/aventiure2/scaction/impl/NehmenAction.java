package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
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
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.EINE_TASCHE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.HAENDE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
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
                                                final StoryState initialStoryState,
                                                final GO object) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();

// STORY Hat man den Frosch in der HAND oder in der TASCHE und verlässt den
//  Tisch beim Schlossfest, hüpft der Frosch weg / hinaus. Der Spieler
//                >merkt gar nicht recht wohin.
//
// STORY Den Frosch auf den Tisch setzen
//
//  und setzt ihn auf den Tisch.
//
//  Wie er nun da sitzt glotzt er dich mit großen Glubschaugen an und
//                >spricht: Nun füll deine Holzschale auf, wir wollen zusammen essen.«
//
// STORY Eintopf essen
//
//  Was hatte deine Großmutter immer gesagt?
//  »Wer dir geholfen in der Not, den sollst du hernach nicht verachten.«
//  Du füllst deine Schale neu mit Eintopf, steckst deinen Holzlöffel
//  hinein... aber was ist das? Auch ein goldener Löffel fährt mit in die
//  Schale. Du schaust verwirrt auf - kein Frosch mehr auf dem Tisch, doch
//                >neben dir auf der Bank sitzt ein junger Mann mit schönen freundlichen
//                >Augen. In Samt und Seide ist er gekleidet, mit goldenen Ketten um den
//  Hals. "Ihr habt mich erlöst", sagt er, "ich danke euch!" Eine böse Hexe
//                >hätte ihn verwünscht. "Ich werde euch nicht vergessen!"
//                >
//  Am Tisch um euch herum entsteht Aufregung. Der schmucke Mann erhebt sich und schickt
//                >sich an, die Halle zu verlassen.
//                >
// STORY Vom Tisch aufstehen.
//                >
//  Du stehst vom Tisch auf, aber die Menge hat dich schon von dem jungen
//  Königssohn getrennt.
//
// STORY Das Schloss verlassen
//                >
//  Du drängst dich durch das Eingangstor und siehst  noch einen Wagen
//                >davonfahren, mit acht weißen Pferden bespannt, jedes mit weißen
//  Straußfedern auf dem Kopf.

        res.add(new NehmenAction<>(db, initialStoryState,
                object, EINE_TASCHE_DES_SPIELER_CHARAKTERS));

        return res.build();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<NehmenAction> buildCreatureActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final LIVGO creature) {
        if (creature.is(FROSCHPRINZ)) {
            return buildFroschprinzActions(db, initialStoryState, creature);
        }
        return ImmutableList.of();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<NehmenAction> buildFroschprinzActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final LIVGO froschprinz) {
        if (((IHasStateGO) froschprinz).stateComp()
                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            return ImmutableList.of(
                    new NehmenAction<>(db, initialStoryState,
                            froschprinz, EINE_TASCHE_DES_SPIELER_CHARAKTERS));
        }

        if (((IHasStateGO) froschprinz).stateComp()
                .hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return ImmutableList.of(
                    new NehmenAction<>(db, initialStoryState,
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
    private NehmenAction(final AvDatabase db, final StoryState initialStoryState,
                         @NonNull final GO gameObject,
                         @NonNull final GameObjectId targetLocationId) {
        this(db, initialStoryState, gameObject, (TARGET_LOC) load(db, targetLocationId));
    }

    /**
     * Erzeugt eine {@link NehmenAction}.
     *
     * @param gameObject     das genommene Objekt
     * @param targetLocation der Ort am SC, wohin es genommen wird, z.B.
     *                       in die Hände o.Ä.
     */
    private NehmenAction(final AvDatabase db, final StoryState initialStoryState,
                         @NonNull final GO gameObject, @NonNull final TARGET_LOC targetLocation) {
        super(db, initialStoryState);

        checkArgument(gameObject.locationComp().getLocation() != null);
        checkArgument(targetLocation.locationComp().getLocationId().equals(SPIELER_CHARAKTER),
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

        return capitalize(praedikat.mitObj(getDescription(gameObject, true))
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
        checkState(((IHasStateGO) gameObject).stateComp()
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                HAT_HOCHHEBEN_GEFORDERT),
                "Unexpected state: " + gameObject);

        return narrateAndDoFroschprinz();
    }

    private AvTimeSpan narrateAndDoFroschprinz() {
        if (((IHasStateGO) gameObject).stateComp().hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return narrateAndDoFroschprinz_HatHochhebenGefordert();
        }

        final Nominalphrase froschDesc = getDescription(gameObject, true);

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
                                    sc.memoryComp().upgradeKnown(gameObject, Known.getKnown(
                                            gameObject.locationComp().getLocation()
                                                    .storingPlaceComp().getLichtverhaeltnisse()));
                                    sc.feelingsComp().setMood(Mood.NEUTRAL);

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
        AvTimeSpan timeElapsed = n.addAlt(
                du(PARAGRAPH,
                        "zauderst", "und dein Herz klopft gewaltig, als du endlich "
                                + getDescription(gameObject, true).akk()
                                + " greifst",
                        secs(5))
                        .phorikKandidat(getDescription(gameObject, true), FROSCHPRINZ)
                        .komma()
                        .dann(),
                neuerSatz(SENTENCE,
                        "Dir wird ganz angst, aber was man "
                                + "versprochen hat, das muss man auch halten! Du nimmst "
                                + getDescription(gameObject, true).akk()
                                + " in die Hand",
                        secs(15))
                        .phorikKandidat(getDescription(gameObject, true), FROSCHPRINZ)
                        .undWartest()
                        .dann());

        timeElapsed = timeElapsed.plus(
                gameObject.locationComp()
                        .narrateAndSetLocation(
                                targetLocation,
                                () -> {
                                    sc.memoryComp().upgradeKnown(gameObject, Known.getKnown(
                                            gameObject.locationComp().getLocation()
                                                    .storingPlaceComp().getLichtverhaeltnisse()));
                                    sc.feelingsComp().setMood(Mood.ANGESPANNT);

                                    return noTime();
                                })
        );

        sc.memoryComp().setLastAction(buildMemorizedAction());
        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoObject() {
        sc.memoryComp().upgradeKnown(gameObject, Known.getKnown(
                gameObject.locationComp().getLocation().storingPlaceComp()
                        .getLichtverhaeltnisse()));

        AvTimeSpan timeElapsed = narrateObject();

        timeElapsed = timeElapsed.plus(
                gameObject.locationComp()
                        .narrateAndSetLocation(targetLocation));
        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeElapsed;
    }


    @NonNull
    private AvTimeSpan narrateObject() {
        final PraedikatMitEinerObjektleerstelle mitnehmenPraedikat =
                gameObject.locationComp().getLocation()
                        .storingPlaceComp().getLocationMode().getMitnehmenPraedikat();

        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
                return narrateObjectNachAblegen(mitnehmenPraedikat);
            }

            final Mood mood = sc.feelingsComp().getMood();

            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    mood.isEmotional()) {
                // TODO Es wäre gut, wenn das mitnehmenPraedikat direkt
                //  eine DuDescription erzeugen könnte, gleich mit dann etc.
                //  Oder wenn man vielleicht etwas ähliches wie eine DuDescription
                //  erzeugen könnte, die intern das mitnehmenPraedikat enthält.
                //  Leider müssen wir bis dahin eine AllgDescription bauen. :-(
                final Nominalphrase objectDesc = getDescription(gameObject, true);
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
                                .getDescriptionDuHauptsatz(getDescription(gameObject, true)),
                        secs(5))
                        .undWartest(
                                mitnehmenPraedikat
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .dann());
    }

    private AvTimeSpan narrateObjectNachAblegen(
            final PraedikatMitEinerObjektleerstelle nehmenPraedikat) {
        if (sc.memoryComp().getLastAction().is(
                Action.Type.ABLEGEN, Action.Type.HOCHWERFEN)) {
            if (initialStoryState.dann()) {
                final Nominalphrase objectDesc = getDescription(gameObject);
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
                                .mitObj(getDescription(gameObject, true).persPron())
                                .getDescriptionZuInfinitiv(
                                        P2, SG,
                                        new AdverbialeAngabe(
                                                "gleich erneut")),
                        secs(5))
                        // "zu nehmen", "an dich zu nehmen", "aufzuheben"
                        .komma()
                        .dann());
            }

            final Nominalphrase objectDesc = getDescription(gameObject, true);
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

        final Nominalphrase objectDesc = getDescription(gameObject, true);
        return n.add(neuerSatz(
                "Dann nimmst du " + objectDesc.akk(),
                secs(5))
                .undWartest()
                .phorikKandidat(objectDesc.getNumerusGenus(), gameObject.getId()));
    }


    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.NEHMEN, gameObject, gameObject.locationComp().getLocation());
    }
}
