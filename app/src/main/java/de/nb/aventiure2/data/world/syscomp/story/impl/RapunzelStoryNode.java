package de.nb.aventiure2.data.world.syscomp.story.impl;

import static java.util.Arrays.asList;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode.Counter.STORY_ADVANCE;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altParagraphs;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.duParagraph;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.SeinUtil;

@SuppressWarnings("UnnecessaryReturnStatement")
public enum RapunzelStoryNode implements IStoryNode {
    // Zentrale Dramatische Frage für des Märchens:
    // Schafft es der SC, der jungen Frau aus dem Turm (Rapunzel) dauerhaft die Freiheit zu geben?
    // (Letztlich mit Ja beantwortet.)
    // (Und: Gelingt es dem SC, zu ihr eine dauerhafte, engere Beziehung aufzubauen? - Letztlich
    // mit Nein beantwortet)
    // Charaktere: SC, Rapunzel, Zauberin; Seilerin
    // Gewünschtes Ergebnis: Rapunzel ist dauerhaft in Freiheit
    // Der SC ist Actor, Zauberin ist Resistor.
    // Schwierigkeiten:
    // - Der SC muss Rapunzel erst einmal finden, zu ihr kommen und ihr Vertrauen gewinnen.
    // - Wenn die Zauberin Wind von der Sache bekommt, spricht sie einen Vergessenszauber.
    // - Der SC muss Rapunzel eine Abstiegsmöglichkeit bauen.
    // - Die Zauberin bekommt irgendwann auf jeden Fall Wind von der Sache und
    //  verzaubert Rapunzel in einen Vogel. Der SC muss die Verzauberung auflösen.
    // - Der SC muss die Macht der Zauberin dauerhaft brechen.

    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    // Dramatische Frage (vor diesem Story Node): Gibt es in der Welt noch mehr zu entdecken?
    TURM_GEFUNDEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmGefunden),
    // Dramatische Frage: Was hat es mit dem Turm auf sich?
    RAPUNZEL_SINGEN_GEHOERT(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelSingenGehoert,
            TURM_GEFUNDEN),

    // Dramatische Frage: Wer singt da im Turm so schön, und wie kann man Kontakt aufnehmen?

    //  Dies wird durch checkAndAdvanceIfAppropriate() automatisch freigeschaltet.
    //  Tipps dafür wären nicht sinnvoll
    ZAUBERIN_MACHT_RAPUNZELBESUCHE,

    ZAUBERIN_AUF_TURM_WEG_GETROFFEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinAufTurmWegGefunden,
            TURM_GEFUNDEN),
    // Dramatische Fragen: Wer singt da im Turm so schön, und wie kann man Kontakt aufnehmen?
    //  Und was hat es mit der Frau auf sich?

    // Ab hier muss bei allen Tipps auch der Sonderfall berücksichtigt
    //  werden, dass der SC alles vergessen hat
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet,
            TURM_GEFUNDEN),
    // Dramatische Frage: Schafft es der SC, auch in den Turm zu kommen, indem er selbst ruft?
    ZU_RAPUNZEL_HINAUFGESTIEGEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZuRapunzelHinaufgestiegen,
            ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET),
    // Dramatische Frage: Kann der SC die junge Frau irgendwie unterstützen?
    // Und gelingt es dem SC, zu ihr eine dauerhafte, engere Beziehung aufzubauen?
    RAPUNZEL_RETTUNG_VERSPROCHEN(15, OBEN_IM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelRettungVersprochen,
            ZU_RAPUNZEL_HINAUFGESTIEGEN),
    // Dramatische Frage ab hier: Schafft es der SC, der jungen Frau aus dem Turm (Rapunzel)
    // die Freiheit zu geben? Und gelingt es dem SC, zu ihr eine dauerhafte, engere Beziehung
    // aufzubauen?
    TURMZIMMER_VERLASSEN_UM_RAPUNZEL_ZU_BEFREIEN(10, OBEN_IM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmzimmmerVerlassenUmRapunzelZuBefreien,
            RAPUNZEL_RETTUNG_VERSPROCHEN),

    // IDEA "An der Wand lehnt ein alter Rucksack / ... Kiepe...,
    //  wie man sie zum Holzsammeln verwenden würde"
    //  Du setzt... auf. Ziemlich schwer. (Man wird schneller müde.)
    //  Du setzt... ab. Du stellst.... auf den Boden / Waldboden...

    //  Dies wird automatisch freigeschaltet, wenn der Sturm die Äste von den Bäumen
    //  gebrochen hat. Tipps dafür wären nicht sinnvoll
    STURM_HAT_AESTE_VON_BAEUMEN_GEBROCHEN,

    // (Neue, zusätzliche offene Frage: Kann man aus den Ästen eine Vorrichtung bauen,
    // mit der Rapunzel den Turm verlassen kann?)
    AESTE_GENOMMEN(10, DRAUSSEN_VOR_DEM_SCHLOSS,
            // Auch hier muss bei den Tipps der Sonderfall eingearbeitet werden,
            // dass der SC alles vergessen hat
            RapunzelStoryNode::narrateAndDoHintAction_AesteGenommen,
            STURM_HAT_AESTE_VON_BAEUMEN_GEBROCHEN),

    AESTE_IN_STUECK_GEBROCHEN(10,
            RapunzelStoryNode::getVisibibleOuterMostLocationIdForHolz,
            // Auch hier muss bei den Tipps der Sonderfall eingearbeitet werden,
            // dass der SC alles vergessen hat
            RapunzelStoryNode::narrateAndDoHintAction_AesteInStueckGebrochen,
            AESTE_GENOMMEN);

    // Auch ab hier muss bei allen Tipps der Sonderfall eingearbeitet werden,
    // dass der SC alles vergessen hat

    //FIXME Die Tipps ab hier könnten auf die / eine noch offene dramatische Frage Bezug nehmen.

    // FIXME BINSENSEIL_GEFLOCHTEN

    // FIXME Seil flechten..
    //  - "du [...Binsen...] flichst ein weiches Seil daraus" (Evtl. Zustandsänderungs-Aktion?)
    //  - "Binsenseil", "Fingerspitzengefühl und Kraft"
    //  - Wenn zu wenige Binsen: Du erhältst nur ein sehr kurzes Seil. / Das Seil ist
    //  nicht besonders lang, stabil sieht es auch nicht aus. / Aus den vielen Binsen flichst du
    //  ein langes, stabiles Seil.

    // FIXME Sackgasse: "Am Seil herunterlassen" / "am Seil herunterklettern": Das tut Rapunzel
    //  nicht.

    // FIXME KombinierenAction (1. Parameter bestimmt die Reihenfolge in den Aktionen, die
    //   restlichen müssen auch alle vorhanden sein)
    //  - "Sprossen"

    // FIXME "steigst die Leiter herauf"

    // FIXME Zauberin hat Rapunzel zwischenzeitlich die Haare abgeschnitten und sie
    //  in einen Vogel verwandelt

    // FIXME Blume löst sich auf nach erlösung rapunzel

    // FIXME Vielleicht kann man auch den Raben erlösen (wenn man ihr schon vorher gesehen hat?)

    // FIXME Rabe mit Sinn HINTERlegen!

    // FIXME Auflösung der letzen / zentralen dramatischen Fragen:
    //  Schafft es der SC, der jungen Frau aus dem Turm (Rapunzel)
    //  die Freiheit zu geben? - Ja.
    //  Und gelingt es dem SC, zu ihr eine dauerhafte, engere Beziehung
    //  aufzubauen? - Nein.

    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        STORY_ADVANCE
    }

    private final ImmutableSet<RapunzelStoryNode> preconditions;

    @Nullable
    private final Integer expAchievementSteps;

    private final Function<World, GameObjectId> locationIdProducer;

    @Nullable
    private final IHinter hinter;

    /**
     * Konstruktor für einen Story Node, der nur automatisch freigeschaltet wird, für den
     * es also keine Tipps geben soll.
     */
    RapunzelStoryNode() {
        this(null, (GameObjectId) null, null);
    }

    RapunzelStoryNode(@Nullable final Integer expAchievementSteps,
                      @Nullable final GameObjectId locationId,
                      @Nullable final IHinter hinter,
                      final RapunzelStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    RapunzelStoryNode(@Nullable final Integer expAchievementSteps,
                      @Nullable final Function<World, GameObjectId> locationIdProducer,
                      @Nullable final IHinter hinter,
                      final RapunzelStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationIdProducer, hinter);
    }

    RapunzelStoryNode(final Collection<RapunzelStoryNode> preconditions,
                      @Nullable final Integer expAchievementSteps,
                      @Nullable final GameObjectId locationId,
                      @Nullable final IHinter hinter) {
        this(preconditions, expAchievementSteps, w -> locationId, hinter);
    }

    RapunzelStoryNode(final Collection<RapunzelStoryNode> preconditions,
                      @Nullable final Integer expAchievementSteps,
                      @Nullable final Function<World, GameObjectId> locationIdProducer,
                      @Nullable final IHinter hinter) {
        this.preconditions = ImmutableSet.copyOf(preconditions);
        this.locationIdProducer = locationIdProducer;
        this.expAchievementSteps = expAchievementSteps;
        this.hinter = hinter;
    }

    @Override
    public Story getStory() {
        return Story.RAPUNZEL;
    }

    @Override
    public ImmutableSet<RapunzelStoryNode> getPreconditions() {
        return preconditions;
    }

    @Override
    @Nullable
    public Integer getExpAchievementSteps() {
        return expAchievementSteps;
    }

    @Nullable
    @Override
    public GameObjectId getLocationId(final World world) {
        return locationIdProducer.apply(world);
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

    @SuppressWarnings("unused")
    public static boolean checkAndAdvanceIfAppropriate(
            final AvDatabase db,
            final TimeTaker timeTaker, final Narrator n,
            final World world) {
        final IHasStateGO<RapunzelsZauberinState> zauberin = loadZauberin(world);

        if (db.counterDao().incAndGet(STORY_ADVANCE) > 5) {
            if (zauberin.stateComp().hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE)
                    && world.loadSC().locationComp()
                    .hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)
                    && RapunzelsZauberinReactionsComp.
                    liegtImZeitfensterFuerRapunzelbesuch(timeTaker.now())) {
                ensureAdvancedToZauberinMachtRapunzelbesuche(db.counterDao(), world);
                return true;
            }
        }

        return false;
    }

    public static void ensureAdvancedToZauberinMachtRapunzelbesuche(
            final CounterDao counterDao, final World world) {
        final IHasStateGO<RapunzelsZauberinState> zauberin = loadZauberin(world);

        if (zauberin.stateComp().hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE)) {
            counterDao.reset(STORY_ADVANCE);
            zauberin.stateComp().narrateAndSetState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH);
            return;
        }
    }

    // IDEA Weitere Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht
    private static void narrateAndDoHintAction_TurmGefunden(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();
        alt.add(
                paragraph("Hast du den Wald eigentlich schon überall erkundet?"),
                paragraph("Was gibt es wohl noch alles im Wald zu entdecken, fragst du dich"),
                paragraph("Dir kommt der geheimnisvolle Turm in den Sinn - du wirst sein "
                        + "Geheimnis bestimmt noch lüften!"));
        n.narrateAlt(alt.schonLaenger(), NO_TIME);
    }

    private static void narrateAndDoHintAction_RapunzelSingenGehoert(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Eine kleine Pause hat noch keinem geschadet"),
                    paragraph("In der Ruhe liegt die Kraft – hast du mal irgendwo gehört"),
                    paragraph("Eine längere Rast würde dir sicher guttun"));
        } else {
            alt.add(paragraph(
                    "Dir kommt noch einmal der alte Turm auf der Hügelkuppe "
                            + "in den Sinn. Ob der wohl bewohnt ist?"));
        }

        n.narrateAlt(alt.schonLaenger(), NO_TIME);
    }

    private static void narrateAndDoHintAction_ZauberinAufTurmWegGefunden(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altTurmWohnenHineinHeraus(n.getNarrationEndedBy(), world));

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        if (world.hasSameVisibleOuterMostLocationAsSC(RAPUNZELS_ZAUBERIN)) {
            narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberSichtbarinImRaum(n);
        } else {
            narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtSichtbarImRaum(
                    timeTaker, n, world);
        }
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberSichtbarinImRaum(
            final Narrator n) {
        n.narrateAlt(NO_TIME,
                paragraph("was will die Frau bloß?").schonLaenger(),
                paragraph("was will die Frau wohl?").schonLaenger(),
                paragraph("was mag die Frau wollen?").schonLaenger(),
                paragraph("es wäre spannend, die Frau einmal heimlich zu beobachten")
                        .schonLaenger(),
                paragraph("ist es nicht seltsam, dass die Frau so kurz angebunden ist "
                        + "und nicht erzählen mag, wohin sie eigentlich möchte?").schonLaenger());
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtSichtbarImRaum(
            final TimeTaker timeTaker, final Narrator n, final World world) {
        if (timeTaker.now().getTageszeit() == NACHTS) {
            narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum_Nachts(
                    n);
            return;
        }

        narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum_Tagsueber(n,
                world);
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum_Nachts(
            final Narrator n) {
        n.narrateAlt(NO_TIME,
                du(PARAGRAPH, "musst", "an die magere Frau denken; du "
                        + "wirst ihr Geheimnis schon knacken! Aber nachts – da schläft "
                        + "sie ja"
                        + " wohl?").schonLaenger()
                ,
                paragraph("dir fällt auf: Nachts ist dir die magere Frau noch nie "
                        + "begegnet").schonLaenger(),
                paragraph("des Nachts scheint hier kaum jemand auf den Beinen zu sein - "
                        + "außer dir").schonLaenger()
                ,
                paragraph("Es scheint, ein jeder liegt nachts in seinem Bettchen. Und "
                        + "du?").schonLaenger());
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum_Tagsueber(
            final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(paragraph("unvermittelt kommt dir die magere Frau in den Sinn, die "
                + "so geschäftig umherläuft. Wohin will die bloß?").schonLaenger());

        alt.addAll(altTurmWohnenHineinHeraus(n.getNarrationEndedBy(), world));

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(du(PARAGRAPH, "wirst",
                    "bestimmt noch den Turm hinaufkommen!", PARAGRAPH)
                    .schonLaenger()
                    .mitVorfeldSatzglied("bestimmt"));
            alt.add(du(PARAGRAPH, "wirst",
                    "bestimmt noch den Turm hinaufkommen – vielleicht musst du dich "
                            + "nur einmal auf die Lauer legen und beobachten, ob jemand "
                            + "hineinkommt?", PARAGRAPH)
                    .schonLaenger().mitVorfeldSatzglied("bestimmt"));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_ZuRapunzelHinaufgestiegen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();
        final IHasStateGO<RapunzelState> rapunzel = loadRapunzel(world);

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) &&
                rapunzel.stateComp().hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            alt.add(paragraph("Ob es wohl gefährlich ist, die Haare hinaufsteigen?")
                    .schonLaenger());
        } else if (world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
            alt.add(paragraph(
                    "Du bist dir plötzlich sicher: Wenn dich jemand in dieser Welt braucht, "
                            + "wartet er – wartet sie! – oben im Turm auf dich!"),
                    paragraph(
                            "Plötzlich steht es dir klar vor Augen: Du musst in den Turm",
                            "steigen, und du weißt, was du zu rufen hast! – Aber die dürre Frau, "
                                    + "die macht dir Angst…"));
        } else {
            // SC hat alles vergessen
            alt.addAll(altHintsAllesVergessenNichtObenImTurm(n.getNarrationEndedBy(), world));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static ImmutableSet<AbstractDescription<?>> altHintsAllesVergessenNichtObenImTurm(
            final StructuralElement narrationEndedBy,
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (!world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph(
                    "Hin und wieder musst du an den alten Turm denken. Du hast das Gefühl, "
                            + "etwas Wichtiges vergessen zu haben, aber es will dir partout "
                            + "nicht einfallen"),
                    paragraph("Du musst kurz innehalten. Dein Herz zieht dich zum alten Turm "
                            + "auf der Hügelkuppe. Und du kannst nicht sagen, warum!"),
                    paragraph("Die Leute sagen ja: Wenn man etwas vergessen hat, soll man noch "
                            + "einmal an dieselbe Stelle zurückgehen").schonLaenger());
        }

        alt.add(paragraph(
                "Manchmal hast du das Gefühl: Du hast noch eine wichtige Rolle "
                        + "zu spielen. Aber wenn du genauer darüber nachdenkst, weißt "
                        + "du plötzlich nicht weiter. Es ist wie verhext"),
                paragraph(
                        "Du hast die ganze Zeit das Gefühl, etwas Wichtiges vergessen zu "
                                + "haben! Aber was bloß?"),
                paragraph("Ist es nicht immer ein Glück, wenn einem das Leben",
                        "eine zweite Chance schenkt? Und wie kommst du",
                        "eigentlich gerade darauf?").schonLaenger()
                // FIXME Weitere Alternativen
        );
        alt.addAll(altTurmWohnenHineinHeraus(narrationEndedBy, world));

        return alt.build();
    }


    private static void narrateAndDoHintAction_RapunzelRettungVersprochen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();
        final IHasStateGO<RapunzelState> rapunzel = loadRapunzel(world);

        if (world.loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            if (world.loadSC().talkingComp().isTalkingTo(RAPUNZEL)) {
                alt.add(neuerSatz("Ein gutes Gespräch!").schonLaenger(),
                        du(SENTENCE, "unterhältst", "dich gern mit",
                                world.anaph(RAPUNZEL).datK(), SENTENCE).schonLaenger(),
                        neuerSatz("Schön, sich mit",
                                world.anaph(RAPUNZEL).datK(),
                                "zu unterhalten").schonLaenger());
            } else {
                alt.add(duParagraph("hast", "noch so viele Fragen an",
                        world.anaph(RAPUNZEL).akkK()).schonLaenger(),
                        duParagraph("hast",
                                "den Eindruck, die schöne junge Frau hätte dir noch viel zu",
                                "erzählen").schonLaenger()
                );
            }
        } else if (!world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
            // SC hat alles vergessen
            alt.addAll(altHintsAllesVergessenNichtObenImTurm(n.getNarrationEndedBy(), world));
        } else if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (rapunzel.stateComp().hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
                alt.add(paragraph("Droht dir wohl Gefahr, wenn du die Haare hinaufsteigst?")
                        .schonLaenger());
            } else {
                alt.add(paragraph("Warum nicht oben im Turm einmal Hallo sagen?").schonLaenger(),
                        duParagraph("hättest",
                                "Lust, wieder einmal bei der jungen Frau",
                                "oben im Turm vorbeizuschauen").schonLaenger()
                );
            }
        } else {
            alt.addAll(altParagraphs("Die junge Frau oben im Turm",
                    ImmutableList.of("geht", "will"),
                    "dir nicht mehr aus dem Kopf").schonLaenger());
            alt.addAll(altParagraphs("Die schöne junge Frau oben im Turm will dir nicht",
                    ImmutableList.of("mehr", ""),
                    "aus dem Kopf gehen").schonLaenger());
            alt.add(paragraph("Deine Gedanken kreisen immer wieder um",
                    getDescription(world).akkK()).schonLaenger());
            alt.add(duParagraph("fühlst", "dich etwas einsam").schonLaenger());
            alt.addAll(altParagraphs("Warum nicht mal wieder bei der",
                    ImmutableList.of("netten", ""),
                    "jungen Frau oben im Turm vorbeischauen?").schonLaenger());
            alt.add(duParagraph("musst",
                    "an die junge Frau oben im Turm denken. Freut die sich wohl,",
                    "wenn du noch einmal bei ihr vorbeischaust?").schonLaenger());
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_TurmzimmmerVerlassenUmRapunzelZuBefreien(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (loadZauberin(world).locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            alt.add(paragraph(
                    "Hoffentlich ist die alte Schachtel bald weg, dass du endlich los",
                    "kommst!").schonLaenger()
                            .phorikKandidat(F, RAPUNZELS_ZAUBERIN),
                    paragraph("Nun heißt es wohl geduldig sein").schonLaenger());
        } else {
            alt.add(paragraph(
                    "Zeit, dich auf den Weg zu machen!").schonLaenger(),
                    paragraph("So nett es hier oben ist – du solltest allmählich gehen!")
                            .schonLaenger(),
                    paragraph("Oh, eigentlich hättest du schon längst los gewollt! Wie",
                            "leicht man sich doch verplaudert!").schonLaenger());
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_AesteGenommen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.<ILocatableGO>loadRequired(HOLZ_FUER_STRICKLEITER).locationComp()
                .hasRecursiveLocation(
                        world.loadSC().locationComp().getLocation())) {
            alt.add(paragraph("Holz hat schon vielen Menschen als guter Rohstoff",
                    "für allerlei nützliche Dinge gedient, jaja!").schonLaenger());
        } else {
            if (world.loadSC().mentalModelComp().hasAssumedState(SCHLOSSFEST, VERWUESTET,
                    NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN,
                    NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN)) {
                alt.add(paragraph("Manchmal haben Durcheinander und Katastrophen auch",
                        "ihr Gutes und schaffen neue Möglichkeiten"),
                        paragraph("Der Sturm hat die Welt wieder einmal kräftig",
                                "durchgeschüttelt!"),
                        neuerSatz(PARAGRAPH, "Was gibt es wohl nach diesem Sturm noch",
                                "neues zu entdecken?"));
            } else {
                alt.add(paragraph("Manchmal muss man sich einfach gut in der Welt umsehen,",
                        "dann fallen einem die Möglichkeiten vor die Füße!"),
                        paragraph("Das Gold liegt nicht auf der Straße. Aber andere Dinge",
                                "liegen vielleicht schon… irgendwo… – puh, du bist total",
                                "durcheinander!"));
            }
        }

        final EinzelneSubstantivischePhrase rapunzelDesc = getDescription(world);
        if (world.loadSC().memoryComp().isKnown(RAPUNZEL)) {
            alt.add(paragraph("Wie kannst du bloß", rapunzelDesc.akkK(),
                    "in die Freiheit bringen…").schonLaenger());

            if (!world.hasSameVisibleOuterMostLocationAsSC(RAPUNZEL)) {
                alt.add(du(PARAGRAPH, "musst", "immerzu an",
                        rapunzelDesc.akkK(),
                        "in",
                        rapunzelDesc.possArt().vor(M).datStr(),
                        "Turm denken: Wie kannst du",
                        rapunzelDesc.persPron().akkK(), "nur befreien? –")
                        .mitVorfeldSatzglied("immerzu")
                        .schonLaenger());
            }
        }

        if (world.loadSC().memoryComp().isKnown(RAPUNZELS_NAME)) {
            alt.add(paragraph("Rapunzel, Rapunzel, Rapunzel – immerzu geht dir ihr Name",
                    "durch den Kopf. Du wirst ihre Rettung sein, da bist du ganz sicher!")
                    .schonLaenger());
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_AesteInStueckGebrochen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.<ILocatableGO>loadRequired(HOLZ_FUER_STRICKLEITER).locationComp()
                .hasRecursiveLocation(SPIELER_CHARAKTER)) {
            alt.add(neuerSatz("Diese langen Äste, die du da bei dir trägst, sind",
                    "ziemlich unpraktisch").schonLaenger(),
                    neuerSatz(PARAGRAPH, "Haben Dinge erst die richtigen Maße, kann man oft viele",
                            "praktische Dinge daraus machen – fällt dir gerade auf", SENTENCE));
        }

        final EinzelneSubstantivischePhrase rapunzelDesc = getDescription(world);
        if (world.loadSC().memoryComp().isKnown(RAPUNZEL)) {
            alt.add(neuerSatz("Wie mag es nur",
                    rapunzelDesc.datK(),
                    "gehen,",
                    rapunzelDesc.relPron().nomK(), // "die"
                    "oben im Turm gefangen",
                    SeinUtil.istSind(rapunzelDesc), // "ist"
                    "?").schonLaenger());
        }

        final EinzelneSubstantivischePhrase descHolz =
                world.getDescription(HOLZ_FUER_STRICKLEITER);
        if (!world.hasSameVisibleOuterMostLocationAsSC(HOLZ_FUER_STRICKLEITER)) {
            alt.add(neuerSatz("Da kommt dir eine Idee", SENTENCE),
                    neuerSatz("Ein Gedanke schießt dir durch den Kopf", SENTENCE));
        } else {
            alt.add(paragraph("Wo hattest du eigentlich",
                    descHolz.akkK(),
                    "abgelegt – wäre doch schade, wenn",
                    descHolz.persPron().akkK(), "jemand…",
                    "einfach so mitgehen ließe!"),
                    paragraph("Wo hast du eigentlich dein Klaubholz? Nicht dass es jemand",
                            "verfeuert!"),
                    paragraph("Wo war eigentlich",
                            descHolz.nomK(), ",", descHolz.relPron().akkK(),
                            "du mit Mühe gesammelt hast?"));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    // FIXME Wenn man das Seil hat und sich noch an Rapunzel erinnert:
    //  "In dir reift ein Plan, wie die Rapunzel retten kannst!"
    //  "Du warst schon immer der Bastler-Typ!"

    @CheckReturnValue
    private static ImmutableSet<AbstractDescription<?>> altTurmWohnenHineinHeraus(
            final StructuralElement narrationEndedBy, final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Wenn im Turm jemand wohnt – wie kommt er herein "
                    + "oder hinaus?").schonLaenger());
            alt.add(paragraph("Ob jemand im Turm ein und aus geht? Aber wie bloß?").schonLaenger());
        } else {
            if (narrationEndedBy != CHAPTER) {
                alt.add(du(PARAGRAPH, "musst",
                        "wieder an den alten Turm denken… wenn dort jemand wohnt, "
                                + "wie kommt der bloß hinein oder heraus?", PARAGRAPH)
                        .mitVorfeldSatzglied("wieder").schonLaenger());
            }
            alt.add(paragraph(
                    "Dir kommt auf einmal wieder der alte Turm in den Sinn: "
                            + "Wer wird darinnen wohl wohnen?").schonLaenger());
        }

        return alt.build();
    }

    @Nullable
    private static GameObjectId getVisibibleOuterMostLocationIdForHolz(final World world) {
        return Optional.ofNullable(world.<ILocatableGO>loadRequired(HOLZ_FUER_STRICKLEITER)
                .getVisibleOuterMostLocation()).map(IGameObject::getId).orElse(null);
    }

    @NonNull
    private static IHasStateGO<RapunzelState> loadRapunzel(final World world) {
        return world.loadRequired(RAPUNZEL);
    }

    @NonNull
    private static <Z extends IHasStateGO<RapunzelsZauberinState> & ILocatableGO>
    Z loadZauberin(final World world) {
        return world.loadRequired(RAPUNZELS_ZAUBERIN);
    }

    private static EinzelneSubstantivischePhrase getDescription(final World world) {
        return world.getDescription(RAPUNZEL);
    }
}
