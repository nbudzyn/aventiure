package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
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
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
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
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MITNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand (oder in Ausnahmefällen eine
 * Creature) an sich.
 */
@ParametersAreNonnullByDefault
public class NehmenAction
        <GO extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    @NonNull
    private final GO gameObject;

    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<NehmenAction> buildObjectActions(final AvDatabase db,
                                                final StoryState initialStoryState,
                                                final GO object) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();


// TODO Der Spieler trägt zwei Dinge stets mit sich herum:
//  EINE TASCHE (in eine Tasche, in einer Tasche)
//
//  Nehmen / mitbehmen Action legt Gegenstände grundsätzlich IN EINE
//  TASCHE.
//                >
//  Die Prüfungen, ob sich etwas "beim SC" befindet,  müssen überprüft
//                >werden und im Zweifel einfach rekursiv prüfen.
//
//  Einzelne Dinge kann man in / auf die HAND nehmen, nämlich den Frosch.
//  Den kann man manchmal NICHTBmitnehmen. Man kann diese Dinge in die HAND
//                >nehmen, wenn sie sich in der TASCHE oder im selben RAUM befinden.
//
//  Wenn man etwas zb den Frosch in der HAND hat, kann man bestimmte
//  Aktionen nicht mehr tun, zb Essen.
//                >
//  Was man in der TASCHE oder in der HAND hat, kann man irgendwo im Raum
//  absetzen.
//                >
//  Hat man den Frosch in der HAND oder 8n der TASCHE und verlässt den
//  Tisch beim Schlossfest, hüpft der Frosch weg / hinaus. Der Spieler
//                >merkt gar nicht recht wohin.
//
//  Den Frosch auf den Tisch setzen
//
//  und setzt ihn auf den Tisch.
//
//  Wie er nun da sitzt glotzt er dich mit großen Glubschaugen an und
//                >spricht: Nun füll deine Holzschale auf, wir wollen zusammen essen.«
//
//  Eintopf essen
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
//  Am Tisch um euch herum entsteht Aufregung. Er erhebt sich und schickt
//                >sich an, die Halle zu verlassen.
//                >
//  Vom Tisch aufstehen.
//                >
//  Du stehst vom Tisch auf, aber die Menge hat dich schon von dem jungen
//  Königssohn getrennt.
//
//  Das Schloss verlassen
//                >
//  Du drängst dich durch das Eingangstor und siehst  noch einen Wagen
//                >davonfahren, mit acht weißen Pferden bespannt, jedes mit weißen
//  Straußfedern auf dem Kopf.

        res.add(new NehmenAction<>(db, initialStoryState, object));
        return res.build();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<NehmenAction> buildCreatureActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final LIVGO creature) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();
        if (creature.is(FROSCHPRINZ) &&
                ((IHasStateGO) creature).stateComp()
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                HAT_HOCHHEBEN_GEFORDERT)) {
            res.add(new NehmenAction<>(db, initialStoryState, creature));
        }
        return res.build();
    }

    private NehmenAction(final AvDatabase db, final StoryState initialStoryState,
                         @NonNull final GO gameObject) {
        super(db, initialStoryState);

        checkArgument(gameObject.locationComp().getLocation() != null);

        this.gameObject = gameObject;
    }

    @Override
    public String getType() {
        return "actionNehmen";
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                gameObject instanceof ILivingBeingGO ? MITNEHMEN : NEHMEN;

        return capitalize(praedikat.mitObj(getDescription(gameObject, true))
                // Relevant für etwas wie "Die Schale an *mich* nehmen"
                .getDescriptionInfinitiv(P1, SG));
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
                        .narrateAndSetLocation(SPIELER_CHARAKTER,
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
                        .narrateAndSetLocation(SPIELER_CHARAKTER,
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
                gameObject.locationComp().narrateAndSetLocation(SPIELER_CHARAKTER));
        sc.memoryComp().setLastAction(buildMemorizedAction());

        // STORY Die Schlosswache ist in die Köchin verliebt und bekommt von ihr Kekse. Das soll
        //  aber geheim bleiben. Der Spieler bekommt was mit. Daher darf er die Kugel mitnehmen...
        return timeElapsed;
    }


    @NonNull
    private AvTimeSpan narrateObject() {
        final PraedikatMitEinerObjektleerstelle nehmenPraedikat =
                gameObject.locationComp().getLocation()
                        .storingPlaceComp().getLocationMode().getNehmenPraedikat();

        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
                return narrateObjectNachAblegen(nehmenPraedikat);
            }

            final Mood mood = sc.feelingsComp().getMood();

            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    mood.isEmotional()) {
                // TODO Es wäre gut, wenn das nehmenPraedikat direkt
                //  eine DuDescription erzeugen könnte, gleich mit dann etc.
                //  Oder wenn man vielleicht etwas ähliches wie eine DuDescription
                //  erzeugen könnte, die intern das nehmenPraedikat enthält.
                //  Leider müssen wir bis dahin eine AllgDescription bauen. :-(
                final Nominalphrase objectDesc = getDescription(gameObject, true);
                return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                        nehmenPraedikat
                                .getDescriptionDuHauptsatz(
                                        objectDesc,
                                        mood.getAdverbialeAngabe()),
                        secs(5))
                        .undWartest(
                                nehmenPraedikat
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .phorikKandidat(objectDesc, gameObject.getId())
                        .dann());
            }
        }

        return n.add(
                neuerSatz(PARAGRAPH,
                        nehmenPraedikat
                                .getDescriptionDuHauptsatz(getDescription(gameObject, true)),
                        secs(5))
                        .undWartest(
                                nehmenPraedikat
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
