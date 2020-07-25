package de.nb.aventiure2.scaction.impl;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobject.World.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONLY_WAY;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.buildAufzaehlung;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static java.util.stream.Collectors.toList;

/**
 * Der Spielercharakter bewegt sich in einen anderen Raum.
 */
public class BewegenAction<R extends ISpatiallyConnectedGO & ILocationGO,
        LOC_DESC extends ILocatableGO & IDescribableGO>
        extends AbstractScAction {

    private final R oldRoom;

    private final SpatialConnection spatialConnection;
    private final NumberOfWays numberOfWays;

    public static <R extends ISpatiallyConnectedGO & ILocationGO>
    ImmutableList<AbstractScAction> buildActions(
            final AvDatabase db,
            final World world,
            final StoryState currentStoryState,
            @NonNull final R room) {
        final ImmutableList.Builder<AbstractScAction> res = builder();


        // STORY Spieler ist Rapunzels Königssohn: Rapunzel: Man muss eine Strickleiter
        //  besorgen - oder Seide kaufen und etwas zum Stricken??? Gold gabs vielleicht
        //  vom Froschprinzen?

// STORY Spieler ist Rapunzel: In Hinterhaus ein kleines Fenster, daraus konnte man in einen
//  prächtigen Garten sehen, der voll der schönsten Blumen und Kräuter stand; er war aber von
//  einer hohen Mauer umgeben,.  stand an die- sem Fenster und sah in den Garten hinab, da
//  erblickte sie ein Beet, das mit den schönsten Rapunzeln bepflanzt war: und sie sahen so
//  frisch und grün aus, daß sie lüstern ward und das größte Verlangen empfand, von den Rapunzeln
//  zu essen.  In der Abenddämmerung stieg er also über die Mauer in den Garten die Mauer
//  herabgeklettert stach in aller Eile eine Handvoll Rapunzeln als er aber  erschrak er gewaltig,
//  denn er sah die Zauberin vor sich stehen. »Wie kannst du es wagen,« sprach sie mit zornigem
//  Blick, die Zaube- rin, die ihn mit bösen und giftigen Blicken ansah  »in meinen Garten zu
//  steigen und wie ein Dieb mir meine Rapunzeln zu stehlen?
//        das soll dir schlecht bekommen.«
//        In ihrem Zorne
//        Schloß es die Zauberin in einen Turm, der in einem Walde lag, und weder Treppe noch
//        Türe hatte, nur ganz oben war ein kleines Fensterchen.
//        Es soll ihm gut gehen, und ich will für es sorgen stellte  sich unten hin und rief
//        den Wald als es anfing dunkel zu werden  in ihrer Einsamkeit Türe des Turms, keine
//        ich weiß nicht, wie ich herabkommen kann. einen Strang Seide mit, daraus will ich
//        eine Leiter flechten, und wenn die fertig ist, so steige ich herunter wickelte sie
//        oben um einen Fenster- haken, und dann fielen 20 Ellen herunter laß herunter Leiter,
//        auf welcher man  sprang aber die Dornen, in die er fiel,

        final List<SpatialConnection> spatialConnections =
                room.spatialConnectionComp().getConnections();

        final NumberOfWays numberOfWays = NumberOfWays.get(spatialConnections.size());

        for (final SpatialConnection spatialConnection : spatialConnections) {
            res.add(new BewegenAction<>(db, world, currentStoryState, room,
                    spatialConnection, numberOfWays));
        }
        return res.build();
    }

    /**
     * Creates a new {@link BewegenAction}.
     */
    private BewegenAction(final AvDatabase db,
                          final World world,
                          final StoryState initialStoryState,
                          @NonNull final R oldRoom,
                          @NonNull final SpatialConnection spatialConnection,
                          final NumberOfWays numberOfWays) {
        super(db, world, initialStoryState);
        this.numberOfWays = numberOfWays;
        this.oldRoom = oldRoom;
        this.spatialConnection = spatialConnection;
    }

    @Override
    public String getType() {
        return "actionBewegen";
    }

    @Override
    @NonNull
    public String getName() {
        return spatialConnection.getActionName();
    }

    @Override
    public AvTimeSpan narrateAndDo() {

        AvTimeSpan elapsedTime = narrateAndDoRoomOnly(loadTo());

        upgradeRoomOnlyKnown();

        // Die nicht-movable Objekte sollten in der Raumbeschreibung
        // alle enthalten gewesen sein (mindestens implizit), auch rekursiv.
        // Sie müssen auf Known gesetzt werden, damit gleich etwas wie
        // "Auf dem Tisch liegt eine Kugel" geschrieben werden kann.
        // Dabei gehen wir mal davon aus, dass alle diese nicht movablen-
        // Objekte auch bei Dunkelheit erkennbar sind - andererseits
        // hätten wir zumindest bei den nicht-movablen Locations das Problem,
        // dass man z.B. den Tisch bei Dunkelheit nicht sieht, aber
        // trotzdem die Kugel, die drauf liegt?!

        // Lebende Dinge, sind hier ausgeschlossen, sie müssen sich ggf. in einer
        // ReactionsComp selbst beschreiben.
        upgradeNonLivingNonMovableRecursiveInventoryKnown(loadTo());

        if (scWirdMitEssenKonfrontiert()) {
            elapsedTime = elapsedTime.plus(narrateAndDoSCMitEssenKonfrontiert());
        }

        updateMood();

        return elapsedTime.plus(sc.locationComp()
                .narrateAndSetLocation(spatialConnection.getTo(),
                        this::narrateNonLivingMovableObjectsAndUpgradeKnown));
    }

    private void upgradeRoomOnlyKnown() {
        world.upgradeKnownToSC(spatialConnection.getTo());
    }

    private void upgradeNonLivingNonMovableRecursiveInventoryKnown(
            @NonNull final ILocationGO location) {
        final Known minimalKnown = world.getKnown(location);

        final ImmutableList<? extends IGameObject> directlyContainedNonLivingNonMovables =
                world.loadDescribableNonLivingNonMovableInventory(location.getId());
        sc.memoryComp().upgradeKnown(directlyContainedNonLivingNonMovables, minimalKnown);

        for (final IGameObject directlyContainedNonLivingNonMovable : directlyContainedNonLivingNonMovables) {
            if (directlyContainedNonLivingNonMovable instanceof ILocationGO) {
                upgradeNonLivingNonMovableRecursiveInventoryKnown(
                        (ILocationGO) directlyContainedNonLivingNonMovable);
            }
        }
    }

    private AvTimeSpan narrateNonLivingMovableObjectsAndUpgradeKnown() {
        // Unbewegliche Objekte sollen bereits in der Raumbeschreibung mitgenannt werden,
        // nicht hier! (Das betrifft auch indirekt enthaltene unbewegliche Objekte.)

        final ImmutableList.Builder<String> descriptionsPerLocation = builder();
        int numMovableObjectsInRoom = 0;
        @Nullable IDescribableGO lastObjectInRoom = null;
        for (final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables :
                buildRecursiveLocationsAndDescribables(loadTo())) {
            descriptionsPerLocation.add(
                    //  "auf dem Boden liegen A und B"
                    buildObjectsInLocationDescription(locationAndDescribables));

            numMovableObjectsInRoom += locationAndDescribables.second.size();
            lastObjectInRoom = locationAndDescribables.second
                    .get(locationAndDescribables.second.size() - 1);

            upgradeKnown(locationAndDescribables);
        }

        AvTimeSpan elapsedTimeOnEnter = noTime();
        if (numMovableObjectsInRoom > 0) {
            //  "Auf dem Boden liegen A und B und auf dem Tisch liegt C"
            final String movableObjectsInRoomDescription =
                    buildMovableObjectsInRoomDescription(descriptionsPerLocation.build());

            elapsedTimeOnEnter = elapsedTimeOnEnter.plus(
                    narrateObjects(movableObjectsInRoomDescription, numMovableObjectsInRoom,
                            lastObjectInRoom));
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return elapsedTimeOnEnter;
    }

    private AvTimeSpan narrateObjects(final String objectsDescription, final int numObjects,
                                      final IDescribableGO lastObject) {
        return n.add(
                neuerSatz(
                        objectsDescription,
                        secs(numObjects * 2))
                        .phorikKandidat(
                                world.getDescription(lastObject,
                                        false),
                                lastObject.getId()));
    }

    private void upgradeKnown(
            @NonNull
            final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables) {
        upgradeKnown(locationAndDescribables.second,
                locationAndDescribables.first.storingPlaceComp()
                        .getLichtverhaeltnisse());

        for (final IGameObject gameObject : locationAndDescribables.second) {
            if (gameObject instanceof ILocationGO) {
                // Die untergeordnet enthaltenen nicht-movable Objekte sollten in der Beschreibung
                // des gameObject alle enthalten gewesen sein (mindestens implizit), auch rekursiv.
                // Sie müssen auf Known gesetzt werden, damit gleich etwas wie
                // "Hier steht ein Tisch mit einer schweren Vase darauf. In der schweren Vase
                // liegt eine Kugel" geschrieben werden kann.
                // Dabei gehen wir mal davon aus, dass alle diese nicht movablen-
                // Objekte auch bei Dunkelheit erkennbar sind - andererseits
                // hätten wir zumindest bei den nicht-movablen Locations das Problem,
                // dass man z.B. die schwere Vase bei Dunkelheit nicht sieht, aber
                // trotzdem die Kugel, die drauf liegt?!
                upgradeNonLivingNonMovableRecursiveInventoryKnown((ILocationGO) gameObject);
            }
        }
    }

    @NonNull
    private ILocationGO loadTo() {
        return (ILocationGO) world.load(spatialConnection.getTo());
    }

    @NonNull
    private String buildObjectsInLocationDescription(
            @NonNull
            final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables) {
        return buildObjectsInLocationDescription(
                locationAndDescribables.first,
                locationAndDescribables.second);
    }

    @NonNull
    private static String buildMovableObjectsInRoomDescription(
            final ImmutableList<String> descriptionsPerLocation) {
        return capitalize(
                buildAufzaehlung(descriptionsPerLocation));
    }

    private ImmutableList<Pair<ILocationGO, ? extends List<LOC_DESC>>> buildRecursiveLocationsAndDescribables(
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<
                Pair<ILocationGO, ? extends List<LOC_DESC>>>
                res = builder();

        final ImmutableList<LOC_DESC> movableObjectsInLocation =
                world.loadDescribableNonLivingMovableInventory(location.getId());

        if (!movableObjectsInLocation.isEmpty()) {
            res.add(new Pair<ILocationGO, List<LOC_DESC>>(
                    location, movableObjectsInLocation));
        }

        for (final ILocationGO directContainedLocations :
                world.loadDescribableNonLivingLocationInventory(location)) {
            res.addAll(buildRecursiveLocationsAndDescribables(directContainedLocations));
        }

        return res.build();
    }

    @NonNull
    private String buildObjectsInLocationDescription(
            final ILocationGO location,
            @NonNull final List<? extends IDescribableGO> movableObjectsInLocation) {
        return buildObjectInLocationDescriptionPrefix(location,
                movableObjectsInLocation.size())
                + " "
                + buildDescriptionAufzaehlung(movableObjectsInLocation);
    }

    private boolean scWirdMitEssenKonfrontiert() {
        final GameObject newRoom = world.load(spatialConnection.getTo());

        if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                .hasState(BEGONNEN)) {
            if (oldRoom.is(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    newRoom.is(SCHLOSS_VORHALLE)) {
                return true;
            }
            if (newRoom.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
                return true;
            }
        }

        if (newRoom.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            // STORY Im Dunkeln kann man keine Früchte sehen
            return true;
        }

        return false;
    }

    private AvTimeSpan narrateAndDoSCMitEssenKonfrontiert() {
        final Hunger hunger = sc.feelingsComp().getHunger();
        switch (hunger) {
            case SATT:
                return noTime();
            case HUNGRIG:
                return narrateAnDoSCMitEssenKonfrontiertReagiertHungrig();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private AvTimeSpan narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
        return n.addAlt(
                neuerSatz("Mmh!", noTime()),
                neuerSatz("Dir läuft das Wasser im Munde zusammen", noTime()),
                du(SENTENCE, "hast", "Hunger", noTime())
                        .undWartest(),
                du(SENTENCE, "bist", "hungrig", noTime())
                        .undWartest(),
                neuerSatz("Dir fällt auf, wie hungrig du bist", noTime())
                        .komma()
        );
    }

    /**
     * Aktualisiert den Gemütszustand des Spielercharakters. "Zeit heilt alle Wunden" - oder so
     * ähnlich.
     */
    private void updateMood() {
        final Mood mood = sc.feelingsComp().getMood();
        if (oldRoom.is(SCHLOSS_VORHALLE)
                && spatialConnection.getTo().equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                && mood == Mood.ANGESPANNT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        } else if (mood == Mood.ETWAS_GEKNICKT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        }
    }

    private AvTimeSpan narrateAndDoRoomOnly(@NonNull final ILocationGO to) {
        final AbstractDescription description = getNormalDescriptionAndDo(initialStoryState,
                to.storingPlaceComp().getLichtverhaeltnisse());

        if (description instanceof DuDescription &&
                initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                isDefinitivDiskontinuitaet()) {
            return n.add(
                    satzanschluss(", besinnst dich aber und "
                                    + ((DuDescription) description)
                                    .getDescriptionSatzanschlussOhneSubjekt(),
                            description.getTimeElapsed())
                            .dann(description.isDann())
                            .komma(description.isKommaStehtAus()));
        }

        if (description.getStartsNew() == WORD &&
                initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                description instanceof DuDescription) {
            return n.add(description);
        }

        if (isDefinitivDiskontinuitaet()) {
            // TODO Es ist - häufig - keine Diskontinuität, wenn
            //  dazwischen eine Reaction liegt. Z.B.: Spieler verlässt das Schloss,
            //  sieht den Prinzen wegfahren und geht danach wieder in das Schloss.
            //  Anderes fiktives Beispiel: Spieler nimmt der Frosch, der Frosch quakt hässlich,
            //  der Spieler setzt den Frosch wieder ab. Oder: Der Spieler geht
            //  irgendwo hin, die Zauberin kommt ihm entgegen, er kehrt um und geht ihr nach.
            //  Vielleicht kann man das irgendwie daran festmachen, ob die
            //  Reaction "die Lage geändert" hat? Oder man geht erst mal immer
            //  davon aus, dass eine Reaction die Lage ändert - man bräuchte also nur
            //  mitzuschreiben (boolean), ob es nach der letzten Action eine Reaction gab?!

            final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
            if (numberOfWays == ONLY_WAY) {
                alt.add(
                        du("schaust", "dich nur kurz um, dann "
                                        + uncapitalize(description.getDescriptionHauptsatz()),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .undWartest(
                                        description
                                                .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
            } else {
                alt.add(neuerSatz(
                        "Was willst du hier eigentlich? "
                                + description.getDescriptionHauptsatz(),
                        description.getTimeElapsed()));
                if (description instanceof DuDescription) {
                    alt.add(neuerSatz(
                            "Was willst du hier eigentlich? "
                                    + ((DuDescription) description)
                                    .getDescriptionHauptsatzMitSpeziellemVorfeld(),
                            description.getTimeElapsed()));
                }

                alt.add(neuerSatz("Aber dir kommt ein Gedanke und "
                                + uncapitalize(description.getDescriptionHauptsatz()),
                        description.getTimeElapsed()));
                alt.add(neuerSatz("Dir kommt ein Gedanke – "
                                + uncapitalize(description.getDescriptionHauptsatz()),
                        description.getTimeElapsed()));
            }
            return n.addAlt(alt);
        }

        if (sc.memoryComp().getLastAction().is(BEWEGEN)) {
            if (initialStoryState.getEndsThis() == StructuralElement.WORD &&
                    initialStoryState.dann()) {
                // "Du stehst wieder vor dem Schloss; dann gehst du wieder hinein in das Schloss."
                final String satzEvtlMitDann = description
                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
                                "dann");
                return n.add(
                        satzanschluss(
                                "; " +
                                        uncapitalize(
                                                satzEvtlMitDann),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .dann(description.isDann()
                                        && !satzEvtlMitDann.startsWith("Dann")));
            } else {
                return n.add(description);
            }
        } else {
            if (initialStoryState.dann()) {
                return n.add(
                        neuerSatz(PARAGRAPH, description
                                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("danach"),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .undWartest(description
                                        .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
            }

            return n.add(description);
        }
    }

    private AbstractDescription getNormalDescriptionAndDo(final StoryState currentStoryState,
                                                          final Lichtverhaeltnisse
                                                                  lichtverhaeltnisseInNewRoom) {
        final Known newRoomKnown = sc.memoryComp().getKnown(spatialConnection.getTo());

        final boolean alternativeDescriptionAllowed =
                oldRoom.spatialConnectionComp().isAlternativeMovementDescriptionAllowed(
                        spatialConnection.getTo(),
                        newRoomKnown, lichtverhaeltnisseInNewRoom);

        final AbstractDescription standardDescription =
                spatialConnection
                        .getSCMoveDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);

        if (!alternativeDescriptionAllowed) {
            return standardDescription;
        }

        if (newRoomKnown == Known.KNOWN_FROM_LIGHT) {
            if (numberOfWays == ONLY_WAY) {
                if (sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                        lichtverhaeltnisseInNewRoom == HELL &&
                        currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                        sc.memoryComp().getLastAction().is(Action.Type.NEHMEN)) {
                    return du("springst", "damit fort", "damit",
                            standardDescription.getTimeElapsed().times(0.8))
                            .undWartest()
                            .dann();
                }

                if (sc.feelingsComp().hasMood(Mood.UNTROESTLICH)) {
                    return du("trottest", "tieftraurig von dannen", "tieftraurig",
                            standardDescription.getTimeElapsed().times(2))
                            .undWartest();
                }
            } else if (numberOfWays == ONE_IN_ONE_OUT
                    && sc.memoryComp().getLastAction().is(BEWEGEN) &&
                    !sc.locationComp().lastLocationWas(spatialConnection.getTo()) &&
                    sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                    lichtverhaeltnisseInNewRoom ==
                            HELL &&
                    currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return du("eilst", "weiter", standardDescription.getTimeElapsed().times(0.8))
                        .undWartest();
            }
        }

        return standardDescription;
    }

    @NonNull
    private static String buildObjectInLocationDescriptionPrefix(
            @NonNull final ILocationGO location,
            final int numberOfObjects) {
        final String res = location.storingPlaceComp().getLocationMode().getWo(false);

        if (numberOfObjects == 1) {
            return res + " liegt";
        }

        return res + " liegen";
    }

    /**
     * Gibt eine Aufzählung zurück wie "der hässliche Frosch",
     * "die goldene Kugel und der hässliche Frosch" oder "das schöne Glas, die goldene Kugel und
     * der hässliche Frosch".
     */
    @NonNull
    private String buildDescriptionAufzaehlung(
            @NonNull final List<? extends IDescribableGO> describables) {
        return buildAufzaehlung(
                describables.stream()
                        .map(this::getNom)
                        .collect(toList())
        );
    }

    private String getNom(final IDescribableGO d) {
        return world.getDescription(d, false).nom();
    }

    private void upgradeKnown(
            @NonNull final List<? extends IGameObject> objects,
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final Known known = Known.getKnown(lichtverhaeltnisse);
        sc.memoryComp().upgradeKnown(objects, known);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return sc.memoryComp().getLastAction().is(BEWEGEN) &&
                sc.locationComp().lastLocationWas(spatialConnection.getTo());
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(BEWEGEN, spatialConnection.getTo());
    }
}