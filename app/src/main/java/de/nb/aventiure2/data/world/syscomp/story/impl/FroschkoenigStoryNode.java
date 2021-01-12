package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.german.description.AbstractDescription;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static java.util.Arrays.asList;

public enum FroschkoenigStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    KUGEL_GENOMMEN(20, SCHLOSS_VORHALLE,
            FroschkoenigStoryNode::narrateAndDoHintAction_KugelGenommen),
    MIT_KUGEL_ZUM_BRUNNEN_GEGANGEN(8, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_MitKugelZumBrunnenGegangen,
            KUGEL_GENOMMEN
    ),
    ETWAS_IM_BRUNNEN_VERLOREN(6, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_EtwasImBrunnenVerloren,
            KUGEL_GENOMMEN // IDEA Es könnte auch andere Dinge zum Im-Brunnen-Verlieren geben
    ),
    FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT(10, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt
            ,
            ETWAS_IM_BRUNNEN_VERLOREN
    ),
    ZUM_SCHLOSSFEST_GEGANGEN(30, // TODO Diese Zahl ermitteln!
            DRAUSSEN_VOR_DEM_SCHLOSS,
            FroschkoenigStoryNode::narrateAndDoHintAction_ZumSchlossfestGegangen,
            KUGEL_GENOMMEN, // Ansonsten kann der Spieler nicht wissen, dass es ein Schlossfest
            // überhaupt gibt
            FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT // Ansonsten bringt einem das Schlossfest nichts
    ),
    BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT(4,
            SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            FroschkoenigStoryNode::narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt,
            ZUM_SCHLOSSFEST_GEGANGEN
    ),
    PRINZ_IST_ERLOEST(6, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            FroschkoenigStoryNode::narrateAndDoHintAction_PrinzIstErloest,
            BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT
    ),
    PRINZ_IST_WEGGEFAHREN(4, DRAUSSEN_VOR_DEM_SCHLOSS,
            FroschkoenigStoryNode::narrateAndDoHintAction_PrinzIstWeggefahren,
            PRINZ_IST_ERLOEST
    );

    private final ImmutableSet<FroschkoenigStoryNode> preconditions;

    private final int expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    private final IHinter hinter;

    FroschkoenigStoryNode(final int expAchievementSteps, @Nullable final GameObjectId locationId,
                          final IHinter hinter,
                          final FroschkoenigStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    FroschkoenigStoryNode(final Collection<FroschkoenigStoryNode> preconditions,
                          final int expAchievementSteps, @Nullable final GameObjectId locationId,
                          final IHinter hinter) {
        this.preconditions = ImmutableSet.copyOf(preconditions);
        this.locationId = locationId;
        this.expAchievementSteps = expAchievementSteps;
        this.hinter = hinter;
    }

    @Override
    public Story getStory() {
        return Story.FROSCHKOENIG;
    }

    @Override
    public ImmutableSet<FroschkoenigStoryNode> getPreconditions() {
        return preconditions;
    }

    @Override
    public Integer getExpAchievementSteps() {
        return expAchievementSteps;
    }

    @Nullable
    @Override
    public GameObjectId getLocationId() {
        return locationId;
    }

    @Override
    public boolean beendetStory() {
        return this == PRINZ_IST_WEGGEFAHREN;
    }

    @Override
    public IHinter getHinter() {
        return hinter;
    }

    public static boolean checkAndAdvanceIfAppropriate(
            final AvDatabase db,
            final Narrator n,
            final World world) {
        // Diese Story kann von Anfang an durchgespielt werden.
        return false;
    }

    // IDEA Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht

    private static void narrateAndDoHintAction_KugelGenommen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            alt.add(du(PARAGRAPH,
                    "fühlst", "dich von der goldenen Kugel magisch angezogen",
                    "von der goldenen Kugel")
                    .beendet(PARAGRAPH));
        } else {
            alt.add(paragraph("Zusammenhanglos kommt dir ein Gedanke in den "
                    + "Kopf: Hättest du vielleicht die goldene Kugel mitnehmen sollen?"));

            alt.add(paragraph("Die goldene Kugel aus dem Schloss will dir nicht mehr aus dem "
                    + "Kopf"));

            alt.add(du(PARAGRAPH,
                    "musst",
                    "auf einmal wieder an die goldene Kugel denken, die dich im "
                            + "Schloss so angelacht hat",
                    "auf einmal")
                    .beendet(PARAGRAPH));

            alt.add(du(PARAGRAPH,
                    "musst",
                    "spontan denken: Bei den reichen Leuten liegen oft so "
                            + "viele Herrlichkeiten ungenutzt herum… – wie kommst du jetzt "
                            + "bloß darauf?",
                    "spontan")
                    .beendet(PARAGRAPH));

            alt.add(paragraph("Deine Gedanken schweifen ab und du musst an die goldene Kugel "
                    + "denken, die du im Schloss gelassen hast. Warum eigentlich?"));
        }

        if (world.loadSC().feelingsComp().isTraurigerAls(ETWAS_GEKNICKT)) {
            alt.add(paragraph("Dir kommt ein Gedanke: Ein glänzendes Spielzeug – das würde "
                    + "dich wohl aufheitern! Aber woher nehmen und nicht stehlen?"));
        }

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_MitKugelZumBrunnenGegangen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        final ILocatableGO goldeneKugel = (ILocatableGO) world.load(GOLDENE_KUGEL);

        if (!world.hasSameOuterMostLocationAsSC(goldeneKugel)) {
            alt.addAll(altKugelVermissen());
        } else {
            if (timeTaker.now().getTageszeit().equals(NACHTS)) {
                alt.addAll(altNachtsSchlafen(world));
            } else {
                alt.addAll(altHeissHeutKuehlerOrtWaereSchoen());
            }
        }

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_EtwasImBrunnenVerloren(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        final ILocatableGO goldeneKugel = (ILocatableGO) world.load(GOLDENE_KUGEL);
        if (world.hasSameOuterMostLocationAsSC(goldeneKugel)) {
            alt.add(du(PARAGRAPH,
                    "hast",
                    "Lust, einmal wieder mit deiner goldenen Kugel "
                            + "zu spielen")
                    .beendet(PARAGRAPH));
            if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
                alt.addAll(altHeissHeutKuehlerOrtWaereSchoen());
            }
        } else {
            alt.addAll(altKugelVermissen());
        }

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Unvermittelt befällt dich ein Gedanke: Ist es wohl gut, seine "
                + "Gefühle zu unterdrücken?"));

        if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
            if (world.loadSC().feelingsComp().isFroehlicherAls(UNTROESTLICH)) {
                alt.add(paragraph(
                        "Ob du wohl jemals zurückbekommst, was dir in den Brunnen gefallen "
                                + "ist? – so fragst du dich auf einmal. Du wirst ganz traurig"));
            } else {
                alt.add(paragraph(
                        "Ob du wohl jemals zurückbekommst, was dir in den Brunnen gefallen "
                                + "ist? – so fragst du dich auf einmal"));
            }
        }

        world.loadSC().feelingsComp().requestMoodMax(UNTROESTLICH);

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_ZumSchlossfestGegangen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (timeTaker.now().getTageszeit().equals(NACHTS)) {
            alt.addAll(altNachtsSchlafen(world));
        }

        alt.add(paragraph("Wann sollte eigentlich das Schlossfest sein? Da gibt es sicher "
                + "etwas Gutes zu essen!"));

        alt.add(paragraph("Heute ist viel passiert"));

        if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                .hasState(SchlossfestState.BEGONNEN)) {
            alt.add(paragraph("Plötzlich überkommt dich ein schlechtes Gewissen. Hättest du nicht "
                    + "mit dem Frosch gemeinsam essen wollen? Hattest du nicht etwas in der Art "
                    + "versprochen? Nur weil er eine hässliche, eklige und glibschige "
                    + "Kreatur ist, heißt das ja noch lange nicht… also…, es heißt "
                    + "nicht zwangsläufig…"));
        }

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Der leckere Duft aus dem Schloss geht dir nicht aus Nase und Sinn"));

        alt.add(paragraph("Welches Versprechen hattest du dem Frosch noch gegeben? Du kannst "
                + "dich kaum mehr erinnern"));

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_PrinzIstErloest(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Kann es sein, dass du vor etwas davonläufst?"));

        alt.add(paragraph("Ein schlechtes Gewissen ist kein gutes Ruhekissen – so geht es "
                + "die ganze Zeit in deinem Kopf"));

        n.narrateAlt(alt, noTime());
    }

    public static void narrateAndDoHintAction_PrinzIstWeggefahren(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(du(PARAGRAPH,
                "willst",
                "auch sehen, was vor dem Schloss geschieht!")
                .beendet(PARAGRAPH));

        n.narrateAlt(alt, noTime());
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> altNachtsSchlafen(
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(HUETTE_IM_WALD)) {
            if (world.loadSC().feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH) {
                alt.add(du(PARAGRAPH,
                        "wirst", "ausgeschlafen mehr ausrichten können",
                        "ausgeschlafen")
                        .beendet(PARAGRAPH));
            } else {
                alt.add(du(PARAGRAPH,
                        "bist", "vom Tag noch ganz aufgedreht")
                        .beendet(PARAGRAPH));
            }
        } else {
            alt.add(paragraph("Vielleicht solltest du dir einen Platz zum Schlafen suchen?"));
            alt.add(paragraph("Ob es sicher ist, nachts herumzulaufen? Wo könntest du "
                    + "übernachten?")); // FIXME Übernachten nicht, wenn man nicht müde ist?!
        }

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> altKugelVermissen() {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Wo ist eigentlich die schöne goldene Kugel, die du "
                + "aus dem Schloss… die dir so gut Gesellschaft geleistet hatte?"));
        alt.add(paragraph("Eigentlich schade – musst du plötzlich denken –, dass "
                + "du deine goldene Kugel nicht mehr bei dir hast. Sie war doch ein "
                + "sehr schönes Spielzeug"));

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> altHeissHeutKuehlerOrtWaereSchoen() {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Heut ist ein heißer Tag!"));
        alt.add(paragraph("Es ist ziemlich heiß heute – ein kühler Ort wäre schön"));

        // TODO ab dem zb 3. Mal deutlichere Hinweise, noch zum Brunnen zu gehen:
        //  Heiß vielleicht irhendwo am wasser...

        return alt.build();
    }
}
