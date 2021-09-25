package de.nb.aventiure2.data.world.syscomp.story.impl;

import static java.util.Arrays.asList;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altParagraphs;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

public enum FroschkoenigStoryNode implements IStoryNode {
    // Zentrale Dramatische Frage für des Märchens:
    //  Schafft es der SC, sich zu überwinden und mit dem Frosch zu essen, so dass etwas
    //  Magisches passiert?
    //  Charaktere: SC, Frosch (Lobebauer)
    //  Gewünschtes Ergebnis: etwas Magisches passiert
    //  Der SC ist Actor und Resistor gleichermaßen.
    //  Schwierigkeiten:
    //  - Der SC muss sich überwinden.
    //  - Der SC muss erkennen, dass man beim Schlossfest zusammen essen kann.
    //  - Der SC muss auf das Schlossfest warten.
    //  (Also für den Spieler eher leicht zu erreichen -> geringe Spannung.)
    //  (Das könnte auch die zentrale dramatische Frage des Märchen sein.)
    //  Die Frage wird eher spät eingeführt. (Zu den dramtischen Fragen vorher siehe unten.)

    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    // Dramatische Frage (vor diesem Story Node):
    // Schafft es der Spieler, etwas die App richtig zu bedienen, so dass er Spaß hat und
    // nichts kaputt geht oder er nicht stirbt?
    KUGEL_GENOMMEN(20, SCHLOSS_VORHALLE,
            FroschkoenigStoryNode::narrateAndDoHintAction_KugelGenommen),
    // Dramatische Frage: Schafft es der SC, die goldene Kugel zu nehmen, ohne dass er ins
    // Gefängnis kommt o.Ä.?
    MIT_KUGEL_ZUM_BRUNNEN_GEGANGEN(8, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_MitKugelZumBrunnenGegangen,
            KUGEL_GENOMMEN),
    // Dramatische Fragen: Schafft es der SC, mit der goldenen Kugel das Schloss zu verlassen?
    // Schaft es der SC, etwas Sinnvolles mit der goldenen Kugel anzustellen?
    ETWAS_IM_BRUNNEN_VERLOREN(
            6, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_EtwasImBrunnenVerloren,
            KUGEL_GENOMMEN), // IDEA Es könnte auch andere Dinge zum Im-Brunnen-Verlieren geben
    // Dramatische Frage: Schafft es der SC seine goldene Kugel zurückzubekommen?
    FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT(10, IM_WALD_BEIM_BRUNNEN,
            FroschkoenigStoryNode::narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt,
            ETWAS_IM_BRUNNEN_VERLOREN),
    // Ab hier zentrale dramatische Frage!
    ZUM_SCHLOSSFEST_GEGANGEN(15,
            DRAUSSEN_VOR_DEM_SCHLOSS,
            FroschkoenigStoryNode::narrateAndDoHintAction_ZumSchlossfestGegangen,
            KUGEL_GENOMMEN, // Ansonsten kann der Spieler nicht wissen, dass es ein Schlossfest
            // überhaupt gibt
            FROSCH_HAT_ETWAS_AUS_BRUNNEN_GEHOLT),// Ansonsten bringt einem das Schlossfest nichts
    BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT(4,
            SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            FroschkoenigStoryNode::narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt,
            ZUM_SCHLOSSFEST_GEGANGEN),
    PRINZ_IST_ERLOEST(6, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
            FroschkoenigStoryNode::narrateAndDoHintAction_PrinzIstErloest,
            BEIM_SCHLOSSFEST_AN_DEN_TISCH_GESETZT),
    // Dramatische Frage: Schafft es der SC, zu erfahren, wie es mit dem Prinzen weitergeht?
    PRINZ_IST_WEGGEFAHREN(4, DRAUSSEN_VOR_DEM_SCHLOSS,
            FroschkoenigStoryNode::narrateAndDoHintAction_PrinzIstWeggefahren,
            PRINZ_IST_ERLOEST),
    // Dramatische Frage: Wars das jetzt? Hat sich das alles gelohnt?
    // (Das passiert automatisch - keine Tipps sinnvoll)
    SC_WURDE_IMPLIZIT_GELOBT_ODER_HAT_ES_ZEITLICH_VERPASST;

    private final ImmutableSet<FroschkoenigStoryNode> preconditions;

    @Nullable
    private final Integer expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    @Nullable
    private final IHinter hinter;

    /**
     * Konstruktor für einen Story Node, der nur automatisch freigeschaltet wird, für den
     * es also keine Tipps geben soll.
     */
    @SuppressWarnings("RedundantCast")
    FroschkoenigStoryNode() {
        this(null, (GameObjectId) null, null);
    }

    FroschkoenigStoryNode(@Nullable final Integer expAchievementSteps,
                          @Nullable final GameObjectId locationId,
                          @Nullable final IHinter hinter,
                          final FroschkoenigStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    FroschkoenigStoryNode(final Collection<FroschkoenigStoryNode> preconditions,
                          @Nullable final Integer expAchievementSteps,
                          @Nullable final GameObjectId locationId,
                          @Nullable final IHinter hinter) {
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

    @Nullable
    @Override
    public Integer getExpAchievementSteps() {
        return expAchievementSteps;
    }

    @Nullable
    @Override
    public GameObjectId getLocationId(final World world) {
        return locationId;
    }

    @Override
    public boolean beendetStory() {
        return this == values()[values().length - 1];
    }

    @Override
    @Nullable
    public IHinter getHinter() {
        return hinter;
    }

    public static boolean checkAndAdvanceIfAppropriate() {
        // Diese Story kann von Anfang an durchgespielt werden.
        return false;
    }

    private static void narrateAndDoHintAction_KugelGenommen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.loadSC().locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            alt.add(du(PARAGRAPH, "fühlst",
                    "dich von der goldenen Kugel magisch angezogen", PARAGRAPH)
                    .schonLaenger()
                    .mitVorfeldSatzglied("von der goldenen Kugel"));
        } else {
            alt.add(paragraph("Zusammenhanglos kommt dir ein Gedanke in den "
                    + "Kopf: Hättest du vielleicht die goldene Kugel mitnehmen sollen?"));

            alt.add(paragraph("Die goldene Kugel aus dem Schloss will dir nicht mehr aus dem "
                    + "Kopf"));

            alt.add(du(PARAGRAPH, "musst",
                    "auf einmal wieder an die goldene Kugel denken, die dich im",
                    "Schloss so angelacht hat", PARAGRAPH)
                    .mitVorfeldSatzglied("auf einmal"));

            alt.add(du(PARAGRAPH,
                    "musst",
                    "spontan denken: Bei den reichen Leuten liegen oft so",
                    "viele Herrlichkeiten ungenutzt herum… – wie kommst du jetzt",
                    "bloß darauf?", PARAGRAPH)
                    .mitVorfeldSatzglied("spontan"));

            alt.add(paragraph("Deine Gedanken schweifen ab und du musst an die goldene Kugel "
                    + "denken, die du im Schloss gelassen hast. Warum eigentlich?"));
        }

        if (world.loadSC().feelingsComp().isTraurigerAls(ETWAS_GEKNICKT)) {
            alt.add(paragraph("Dir kommt ein Gedanke: Ein glänzendes Spielzeug – das würde "
                    + "dich wohl aufheitern! Aber woher nehmen und nicht stehlen?"));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_MitKugelZumBrunnenGegangen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        final ILocatableGO goldeneKugel = world.load(GOLDENE_KUGEL);

        if (!world.hasSameVisibleOuterMostLocationAsSC(goldeneKugel)) {
            alt.addAll(altKugelVermissen());
        } else {
            if (timeTaker.now().getTageszeit().equals(NACHTS)) {
                alt.addAll(altNachtsSchlafen(world));
            } else {
                final ImmutableCollection<AbstractDescription<?>> hintGgfWetterhinweis =
                        altHintZumBrunnenGehenGgfWetterhinweis(world);
                if (!hintGgfWetterhinweis.isEmpty()) {
                    // Wetterhinweise müssen auf jeden Fall ausgegeben werden
                    n.narrateAlt(hintGgfWetterhinweis, NO_TIME);
                    return;
                }
            }
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_EtwasImBrunnenVerloren(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        final ILocatableGO goldeneKugel = world.load(GOLDENE_KUGEL);
        if (world.hasSameVisibleOuterMostLocationAsSC(goldeneKugel)) {
            alt.add(du(PARAGRAPH,
                    "hast",
                    "Lust, einmal wieder mit deiner goldenen Kugel "
                            + "zu spielen", PARAGRAPH).schonLaenger()
            );
            if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
                if (timeTaker.now().getTageszeit().equals(NACHTS)) {
                    alt.addAll(altNachtsSchlafen(world));
                } else {
                    final ImmutableCollection<AbstractDescription<?>> hintGgfWetterhinweis =
                            altHintZumBrunnenGehenGgfWetterhinweis(world);
                    if (!hintGgfWetterhinweis.isEmpty()) {
                        // Wetterhinweise müssen auf jeden Fall ausgegeben werden!
                        n.narrateAlt(hintGgfWetterhinweis, NO_TIME);
                        return;
                    }
                }
            }
        } else {
            alt.addAll(altKugelVermissen());
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_FroschHatEtwasAusBrunnenGeholt(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(paragraph("Unvermittelt befällt dich ein Gedanke: Ist es wohl gut, seine "
                + "Gefühle zu unterdrücken?"));

        if (!world.loadSC().locationComp().hasRecursiveLocation(IM_WALD_BEIM_BRUNNEN)) {
            if (world.loadSC().feelingsComp().isFroehlicherAls(UNTROESTLICH)) {
                alt.add(paragraph(
                        "Ob du wohl jemals zurückbekommst, was dir in den Brunnen gefallen "
                                + "ist? – so fragst du dich auf einmal. Du wirst ganz traurig")
                        .schonLaenger());
            } else {
                alt.add(paragraph(
                        "Ob du wohl jemals zurückbekommst, was dir in den Brunnen gefallen "
                                + "ist? – so fragst du dich auf einmal")
                        .schonLaenger());
            }
        }

        world.loadSC().feelingsComp().requestMoodMax(UNTROESTLICH);

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_ZumSchlossfestGegangen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (timeTaker.now().getTageszeit().equals(NACHTS)) {
            alt.addAll(altNachtsSchlafen(world));
        }

        alt.add(paragraph("Wann sollte eigentlich das Schlossfest sein? Da gibt es sicher",
                "etwas Gutes zu essen!").schonLaenger());

        alt.add(paragraph("Heute ist viel passiert").schonLaenger());

        if (world.<IHasStateGO<SchlossfestState>>load(SCHLOSSFEST).stateComp()
                .hasState(SchlossfestState.BEGONNEN, VERWUESTET)) {
            alt.add(paragraph("Plötzlich überkommt dich ein schlechtes Gewissen. Hättest du nicht "
                    + "mit dem Frosch gemeinsam essen wollen? Hattest du nicht etwas in der Art "
                    + "versprochen? Nur weil er eine hässliche, eklige und glibschige "
                    + "Kreatur ist, heißt das ja noch lange nicht… also…, es heißt "
                    + "nicht zwangsläufig…"));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_BeimSchlossfestAnDenTischGesetzt(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(paragraph("Der leckere Duft aus dem Schloss geht dir nicht aus Nase und Sinn"));

        alt.add(paragraph("Welches Versprechen hattest du dem Frosch noch gegeben? Du kannst",
                "dich kaum mehr erinnern"));

        n.narrateAlt(alt.schonLaenger(), NO_TIME);
    }

    private static void narrateAndDoHintAction_PrinzIstErloest(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(paragraph("Kann es sein, dass du vor etwas davonläufst?"));

        alt.add(paragraph("Ein schlechtes Gewissen ist kein gutes Ruhekissen – so geht es "
                + "die ganze Zeit in deinem Kopf"));

        n.narrateAlt(alt.schonLaenger(), NO_TIME);
    }

    private static void narrateAndDoHintAction_PrinzIstWeggefahren(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(du(PARAGRAPH,
                "willst",
                "auch sehen, was vor dem Schloss geschieht!", PARAGRAPH)
                .schonLaenger());

        n.narrateAlt(alt, NO_TIME);
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> altNachtsSchlafen(
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.loadSC().locationComp().hasRecursiveLocation(HUETTE_IM_WALD)) {
            if (world.loadSC().feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH) {
                alt.add(du(PARAGRAPH, "wirst",
                        "ausgeschlafen mehr ausrichten können", PARAGRAPH)
                        .mitVorfeldSatzglied("ausgeschlafen").schonLaenger()
                );
            } else {
                alt.add(du(PARAGRAPH,
                        "bist", "vom Tag noch ganz aufgedreht", PARAGRAPH)
                        .schonLaenger()
                );
            }
        } else {
            if (world.loadSC().feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH) {
                alt.add(paragraph("Vielleicht solltest du dir einen Platz zum Schlafen suchen?"));
                alt.add(paragraph("Ob es sicher ist, nachts herumzulaufen? Wo könntest du "
                        + "übernachten?"));
            } else {
                alt.add(paragraph("Es ist zwar Nacht, aber du bist noch gar nicht müde"));
                alt.add(paragraph("Es ist längst Nacht. Aber recht müde bist du noch nicht"));
                alt.add(paragraph("Leider bist du noch nicht wirklich müde – obwohl es doch",
                        "längst Nacht ist"));
                alt.add(paragraph("Vielleicht kannst du etwas Anstrengendes tun, um müde zu",
                        "werden? Nachts sollte man schließlich schlafen!"));
            }
        }

        return alt.schonLaenger().build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> altKugelVermissen() {
        // FIXME Dieser Text erscheint teilweise, obwohl der SC die Kugel bei sich hat!
        final AltDescriptionsBuilder alt = alt();

        alt.add(paragraph("Wo ist eigentlich die schöne goldene Kugel, die du "
                + "aus dem Schloss… die dir so gut Gesellschaft geleistet hatte?").schonLaenger());
        alt.add(paragraph("Eigentlich schade – musst du plötzlich denken –, dass "
                + "du deine goldene Kugel nicht mehr bei dir hast. Sie war doch ein "
                + "sehr schönes Spielzeug"));

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> altHintZumBrunnenGehenGgfWetterhinweis(
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.loadWetter().wetterComp().getTemperaturFuerAktuellenZeitpunktAmOrtDesSc()
                .compareTo(Temperatur.RECHT_HEISS)
                >= 0) {
            if (!scIsDraussen(world)) {
                final ImmutableCollection<AbstractDescription<?>> altSpWetterhinweise =
                        world.loadWetter().wetterComp()
                                .altSpWetterhinweiseFuerAktuellenZeitpunktAmOrtDesSC();
                if (!altSpWetterhinweise.isEmpty()) {
                    return altSpWetterhinweise;
                    // Von denen muss auf jeden Fall einer ausgegeben werden.
                }
            }

            if (scIsDraussen(world)) {
                final ImmutableCollection<AbstractDescription<?>> altSpHeuteHeisserTagSaetze =
                        world.loadWetter().wetterComp()
                                .altSpDescUeberHeuteOderDenTagWennDraussenSinnvoll();
                if (!altSpHeuteHeisserTagSaetze.isEmpty()) {
                    alt.addAll(altParagraphs(altSpHeuteHeisserTagSaetze));
                    alt.addAll(altParagraphs(altSpHeuteHeisserTagSaetze,
                            SENTENCE,
                            "Ein kühler Ort wäre schön").schonLaenger());
                }
            }
        }

        if (world.loadSC().memoryComp().isKnown(IM_WALD_BEIM_BRUNNEN)) {
            alt.addIfOtherwiseEmpty(paragraph("Dir kommt ein Gedanke:",
                    "Wäre es nicht schön, irgendwo gemütlich zu sitzen und ein wenig mit der",
                    "goldenen Kugel zu spielen?"));
            alt.addIfOtherwiseEmpty(paragraph("Soviel ist passiert die letzte Zeit!",
                    "Irgendwo in Ruhe sitzen, mit deiner goldenen Kugel spielen –",
                    "das würde dir sicher gut tun."));
            alt.addIfOtherwiseEmpty(
                    paragraph("Braucht nicht ein jeder auch einmal Zeit für sich selbst?",
                            "An einem ruhigen Ort sitzen, ein wenig mit deiner goldenen Kugel",
                            "spielen – ja, das stellst du dir sehr meditativ vor!"));
        } else {
            alt.addIfOtherwiseEmpty(
                    paragraph("Es gibt sicher noch viel zu erleben").schonLaenger());
        }

        return alt.build();
    }

    private static boolean scIsDraussen(final World world) {
        @Nullable final ILocationGO scLocation = world.loadSC().locationComp().getLocation();

        if (scLocation == null) {
            return false;
        }

        return scLocation.storingPlaceComp().getDrinnenDraussen().isDraussen();
    }
}
