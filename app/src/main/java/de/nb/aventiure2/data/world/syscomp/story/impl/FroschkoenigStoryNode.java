package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.description.AbstractDescription;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobject.World.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.german.description.AllgDescription.paragraph;
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
            KUGEL_GENOMMEN // STORY Es könnte auch andere Dinge zum Im-Brunnen-Verlieren geben
    ),
    FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT(10, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt,
            ETWAS_IM_BRUNNEN_VERLOREN
    ),
    ZUM_SCHLOSSFEST_GEGANGEN(30, // STORY Diese Zahl ermitteln!
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
    public int getExpAchievementSteps() {
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

    // STORY Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht

    private static AvTimeSpan narrateAndDoHintAction_KugelGenommen(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            alt.add(paragraph("Du fühlst dich von der goldenen Kugel magisch angezogen"));
        } else {
            alt.add(paragraph("Zusammenhanglos kommt dir ein Gedanke in den "
                    + "Kopf: Hättest du vielleicht die goldene Kugel mitnehmen sollen?"));

            alt.add(paragraph("Die goldene Kugel aus dem Schloss will dir nicht mehr aus dem "
                    + "Kopf"));

            alt.add(paragraph(
                    "Auf einmal musst du wieder an die goldene Kugel denken, die dich im "
                            + "Schloss so angelacht hat"));

            alt.add(paragraph("Du musst spontan denken: Bei den reichen Leuten liegen oft so "
                    + "viele Herrlichkeiten ungenutzt herum… – wie kommst du jetzt "
                    + "bloß darauf?"));

            alt.add(paragraph("Deine Gedanken schweifen ab und du musst an die goldene Kugel "
                    + "denken, die du im Schloss gelassen hast. Warum eigentlich?"));
        }

        if (world.loadSC().feelingsComp().getMood().isTraurigerAls(ERSCHOEPFT)) {
            alt.add(paragraph("Dir kommt ein Gedanke: Ein glänzendes Spielzeug – das würde "
                    + "dich wohl aufheitern! Aber woher nehmen und nicht stehlen?"));
        }

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_MitKugelZumBrunnenGegangen(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        final ILocatableGO goldeneKugel = (ILocatableGO) world.load(GOLDENE_KUGEL);

        if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
            if (db.nowDao().now().getTageszeit().equals(NACHTS)) {
                alt.addAll(altNachtsSchlafen(world));
            } else {
                alt.addAll(altHeissHeutKuehlerOrtWaereSchoen());
            }
        }

        if (!goldeneKugel.locationComp().hasSameUpperMostLocationAs(SPIELER_CHARAKTER)) {
            alt.addAll(altKugelVermissen());
        }

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_EtwasImBrunnenVerloren(
            final AvDatabase db, final NarrationDao n, final World world) {
        // STORY
        //  - (bis BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT) Eine verwirrte alte Frau
        //   läuft zwiscchen vor dem Schloss und Wald hin und her und jammert: Im Königreich nebenan
        //   ist der Prinz verschwunden. Er soll verwünscht oder in ein Tier
        //   verwandelt worden sein und treibe sich jetzt im Wald herum. Keiner wisse, wo
        //   er zu finden sei...

        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        final ILocatableGO goldeneKugel = (ILocatableGO) world.load(GOLDENE_KUGEL);
        if (goldeneKugel.locationComp().hasSameUpperMostLocationAs(SPIELER_CHARAKTER)) {
            alt.add(paragraph("Du hast Lust, einmal wieder mit deiner goldenen Kugel "
                    + "zu spielen"));
            if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
                alt.addAll(altHeissHeutKuehlerOrtWaereSchoen());
            }
        } else {
            alt.addAll(altKugelVermissen());
        }

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Unvermittelt befällt dich ein Gedanke: Ist es wohl gut, seine "
                + "Gefühle zu unterdrücken?"));

        if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
            if (world.loadSC().feelingsComp().getMood().isFroehlicherAls(UNTROESTLICH)) {
                alt.add(paragraph(
                        "Ob du wohl jemals zurückbekommst, was dir in den Brunnen gefallen "
                                + "ist? – so fragst du dich auf einmal. Du wirst ganz traurig"));
            } else {
                alt.add(paragraph(
                        "Ob du wohl jemals zurückbekommst, was dir in den Brunnen gefallen "
                                + "ist? – so fragst du dich auf einmal"));
            }
        }

        world.loadSC().feelingsComp().setMoodMax(UNTROESTLICH);

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_ZumSchlossfestGegangen(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (db.nowDao().now().getTageszeit().equals(NACHTS)) {
            alt.addAll(altNachtsSchlafen(world));
        }

        alt.add(paragraph("Wann sollte eigentlich das Schlossfest sein? Da gibt es sicher "
                + "etwas Gutes zu essen!"));

        alt.add(paragraph("Heute ist viel passiert"));

        alt.add(paragraph("Plötzlich überkommt dich ein schlechtes Gewissen. Hättest du nicht "
                + "mit dem Frosch gemeinsam essen wollen? Hattest du nicht etwas in der Art "
                + "versprochen? Nur weil er eine hässliche, eklige und glibschige "
                + "Kreatur ist, heißt das ja noch lange nicht… also…, es heißt "
                + "nicht zwangsläufig…"));

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Der leckere Duft aus dem Schloss geht dir nicht aus Nase und Sinn"));

        alt.add(paragraph("Welches Versprechen hattest du dem Frosch noch gegeben? Du kannst "
                + "dich kaum mehr erinnern"));

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_PrinzIstErloest(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Kann es sein, dass du vor etwas davonläufst?"));

        alt.add(paragraph("Ein schlechtes Gewissen ist kein gutes Ruhekissen – so geht es "
                + "die ganze Zeit in deinem Kopf"));

        return n.addAlt(alt);
    }

    public static AvTimeSpan narrateAndDoHintAction_PrinzIstWeggefahren(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Du willst auch sehen, was vor dem Schloss geschieht!"));

        return n.addAlt(alt);
    }

    private static ImmutableCollection<AbstractDescription<?>> altNachtsSchlafen(
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(HUETTE_IM_WALD)) {
            if (world.loadSC().feelingsComp().hasMood(ERSCHOEPFT)) {
                alt.add(paragraph("Du solltest etwas schlafen"));
                alt.add(paragraph("Du kannst gewiss eine Mütze Schlaf gebrauchen!"));
                alt.add(paragraph("Ein Bett!"));
            } else {
                alt.add(paragraph("Du bist vom Tag noch ganz aufgedreht"));
            }
        } else {
            alt.add(paragraph("Vielleicht solltest du dir einen Platz zum Schlafen suchen?"));
            alt.add(paragraph("Ob es sicher ist, nachts herumzulaufen? Wo könntest du "
                    + "übernachten?"));
        }

        return alt.build();
    }

    private static ImmutableCollection<AbstractDescription<?>> altKugelVermissen() {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Wo ist eigentlich die schöne goldene Kugel, die du "
                + "aus dem Schloss… die dir so gut Gesellschaft geleistet hatte?"));
        alt.add(paragraph("Eigentlich schade – musst du plötzlich denken –, dass "
                + "du deine goldene Kugel nicht mehr bei dir hast. Sie war doch ein "
                + "sehr schönes Spielzeug"));

        return alt.build();
    }

    private static ImmutableCollection<AbstractDescription<?>> altHeissHeutKuehlerOrtWaereSchoen() {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.add(paragraph("Heut ist ein heißer Tag!"));
        alt.add(paragraph("Es ist ziemlich heiß heute – ein kühler Ort wäre schön"));

        return alt.build();
    }
}