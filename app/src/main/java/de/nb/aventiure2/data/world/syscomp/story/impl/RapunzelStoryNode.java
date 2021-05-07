package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode.Counter.STORY_ADVANCE;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altParagraphs;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.duParagraph;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static java.util.Arrays.asList;

@SuppressWarnings("UnnecessaryReturnStatement")
public enum RapunzelStoryNode implements IStoryNode {
    // FIXME Die zentrale Dramatische Frage für des Märchens ermitteln:
    //  Schafft es (Charakter X) (Charakter Y) zu (Aktion), so dass (Ergebnis / Ziel).
    //  Zwei oder mehr Charaktere, ein gewünschtes Ergebnis
    //  Oft ist X der Actor, Y der Resistor.
    //  Im Idealfall können wir die zentrale dramatische Frage des Märchen direkt übernehmen. Je
    //  früher man sie einführt, desto besser.
    //  Das Ergebnis / Ziel sollte (für den Character / den SC) schwer zu
    //  erreichen sein. Das erzeugt Spannung.

    //FIXME Es sollte nach jedem Story Beat noch eine offene Dramatische Frage geben.
    // Bestenfalls die zentrale Dramatische Frage des Märchens - oder eine andere.
    // Die Tipps könnten auf die / eine noch offene dramatische Frage Bezug nehmen -
    // wenn die Frage nicht zu allgemein ist.
    // Oft ist es dramatisch interessanter, wenn eine dramatische Frage mit
    // Nein beantwortet wird.

    TURM_GEFUNDEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmGefunden),
    RAPUNZEL_SINGEN_GEHOERT(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelSingenGehoert,
            TURM_GEFUNDEN),

    //  Dies wird durch checkAndAdvanceIfAppropriate() automatisch freigeschaltet.
    //  Tipps dafür wären nicht sinnvoll
    ZAUBERIN_MACHT_RAPUNZELBESUCHE,

    ZAUBERIN_AUF_TURM_WEG_GETROFFEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinAufTurmWegGefunden,
            TURM_GEFUNDEN),
    // Ab hier muss bei allen Tipps auch der Sonderfall berücksichtigt
    //  werden, dass der SC alles vergessen hat
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet,
            TURM_GEFUNDEN),
    ZU_RAPUNZEL_HINAUFGESTIEGEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZuRapunzelHinaufgestiegen,
            ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET),
    RAPUNZEL_RETTUNG_VERSPROCHEN(15, OBEN_IM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelRettungVersprochen,
            ZU_RAPUNZEL_HINAUFGESTIEGEN),
    TURMZIMMER_VERLASSEN_UM_RAPUNZEL_ZU_BEFREIEN(10, OBEN_IM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmzimmmerVerlassenUmRapunzelZuBefreien,
            RAPUNZEL_RETTUNG_VERSPROCHEN),

    //  Dies wird automatisch freigeschaltet, wenn der Sturm die Äste von den Bäumen
    //  gebrochen hat. Tipps dafür wären nicht sinnvoll

    STURM_HAT_AESTE_VON_BAEUMEN_GEBROCHEN;

    // Auch ab hier muss bei allen Tipps der Sonderfall eingearbeitet werden,
    // dass der SC alles vergessen hat

    // FIXME "An der Wand lehnt ein alter Rucksack / ... Kiepe...,
    //  wie man sie zum Holzsammeln verwenden würde"
    //  Du setzt... auf. Ziemlich schwer. (Man wird schneller müde.)
    //  Du setzt... ab. Du stellst.... auf den Boden / Waldboden...

    // FIXME "Hier hat der Sturm hat viele Äste von den Bäumen gebrochen. Überall liegt
    //  Holz herum, kleine und große Äste.
    //  - Du klaubst Holz auf
    //  - Holz in armlange Stücke brechen
    //  - "du sammelst Holz"

    // FIXME Beinahe krank werden (draußen beim schlechtem Wetter - man kann nicht klettern -
    //  und Kraut dagegen finden (o.Ä. vgl. Märchen! "Fieber", "krank"...) Aber Spielerlebnis
    //  nicht unangenehm machen!!
    // FIXME Zwerge könnten helfen, kraut zu finden.
    // FIXME Man könnte gewisse Spezialfertigkeiten erhalten. (Ähnlichr 6e kommen durch die ganze
    //  Welt o.ä.).

    // FIXME Binsen, Seil flechten...
    //  - "du rupfst Binsen und flichst ein weiches Seil daraus"
    //  -- oder Seilflechten erst von eine (alten? armen?) Frau lernen? ("Mentor")
    //  - "Binsenseil"
    //  - "Sprossen"
    //  - "steigst die Leiter herauf"

    // FIXME Rabe mit Sinn HINTERlegen!
    // FIXME Begrenzte Tragkraft?!

    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        STORY_ADVANCE
    }

    private final ImmutableSet<RapunzelStoryNode> preconditions;

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
    RapunzelStoryNode() {
        this(null, null, null);
    }

    RapunzelStoryNode(@Nullable final Integer expAchievementSteps,
                      @Nullable final GameObjectId locationId,
                      @Nullable final IHinter hinter,
                      final RapunzelStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    RapunzelStoryNode(final Collection<RapunzelStoryNode> preconditions,
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
    public GameObjectId getLocationId() {
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
        n.narrateAlt(alt, NO_TIME);
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

        n.narrateAlt(alt, NO_TIME);
    }

    private static void narrateAndDoHintAction_ZauberinAufTurmWegGefunden(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altTurmWohnenHineinHeraus(world));

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
                paragraph("was will die Frau bloß?"),
                paragraph("was will die Frau wohl?"),
                paragraph("was mag die Frau wollen?"),
                paragraph("es wäre spannend, die Frau einmal heimlich zu beobachten"),
                paragraph("ist es nicht seltsam, dass die Frau so kurz angebunden ist "
                        + "und nicht erzählen mag, wohin sie eigentlich möchte?"));
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
                        + "begegnet"),
                paragraph("des Nachts scheint hier kaum jemand auf den Beinen zu sein - "
                        + "außer dir").schonLaenger()
                ,
                paragraph("Es scheint, ein jeder liegt nachts in seinem Bettchen. Und "
                        + "du?"));
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum_Tagsueber(
            final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(paragraph("unvermittelt kommt dir die magere Frau in den Sinn, die "
                + "so geschäftig umherläuft. Wohin will die bloß?"));

        alt.addAll(altTurmWohnenHineinHeraus(world));

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
            alt.add(paragraph("Ob es wohl gefährlich ist, die Haare hinaufsteigen?"));
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
            alt.addAll(altHintsAllesVergessenNichtObenImTurm(world));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private static ImmutableSet<AbstractDescription<?>> altHintsAllesVergessenNichtObenImTurm(
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
                            + "einmal an dieselbe Stelle zurückgehen"));
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
                        "eigentlich gerade darauf?"));
        alt.addAll(altTurmWohnenHineinHeraus(world));

        return alt.build();
    }


    private static void narrateAndDoHintAction_RapunzelRettungVersprochen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();
        final IHasStateGO<RapunzelState> rapunzel = loadRapunzel(world);

        if (world.loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            if (world.loadSC().talkingComp().isTalkingTo(RAPUNZEL)) {
                alt.add(neuerSatz("Ein gutes Gespräch!"),
                        du(SENTENCE, "unterhältst", "dich gern mit",
                                world.anaph(RAPUNZEL).datK(), SENTENCE).schonLaenger(),
                        neuerSatz("Schön, sich mit",
                                world.anaph(RAPUNZEL).datK(),
                                "zu unterhalten"));
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
            alt.addAll(altHintsAllesVergessenNichtObenImTurm(world));
        } else if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (rapunzel.stateComp().hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
                alt.add(paragraph("Droht dir wohl Gefahr, wenn du die Haare hinaufsteigst?"));
            } else {
                alt.add(paragraph("Warum nicht oben im Turm einmal Hallo sagen?"),
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
                    world.getDescription(RAPUNZEL).akkK()).schonLaenger());
            alt.add(duParagraph("fühlst", "dich etwas einsam").schonLaenger());
            alt.addAll(altParagraphs("Warum nicht mal wieder bei der",
                    ImmutableList.of("netten", ""),
                    "jungen Frau oben im Turm vorbeischauen?"));
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
                    "kommst!")
                            .phorikKandidat(F, RAPUNZELS_ZAUBERIN),
                    paragraph("Nun heißt es wohl geduldig sein"));
        } else {
            alt.add(paragraph(
                    "Zeit, dich auf den Weg zu machen!"),
                    paragraph("So nett es hier oben ist – du solltest allmählich gehen!"),
                    paragraph("Oh, eigentlich hättest du schon längst los gewollt! Wie",
                            "leicht man sich doch verplaudert!"));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    @CheckReturnValue
    private static ImmutableSet<AbstractDescription<?>> altTurmWohnenHineinHeraus(
            final World world) {
        final AltDescriptionsBuilder alt = alt();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Wenn im Turm jemand wohnt – wie kommt er herein "
                    + "oder hinaus?"));
            alt.add(paragraph("Ob jemand im Turm ein und aus geht? Aber wie bloß?"));
        } else {
            alt.add(du(PARAGRAPH, "musst",
                    "wieder an den alten Turm denken… wenn dort jemand wohnt, "
                            + "wie kommt der bloß hinein oder heraus?", PARAGRAPH)
                    .mitVorfeldSatzglied("wieder").schonLaenger()
            );
            alt.add(paragraph(
                    "Dir kommt auf einmal wieder der alte Turm in den Sinn: "
                            + "Wer wird darinnen wohl wohnen?"));
        }

        return alt.build();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private static IHasStateGO<RapunzelState> loadRapunzel(final World world) {
        return (IHasStateGO<RapunzelState>) world.load(RAPUNZEL);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private static <Z extends IHasStateGO<RapunzelsZauberinState> & ILocatableGO>
    Z loadZauberin(final World world) {
        return (Z) world.load(RAPUNZELS_ZAUBERIN);
    }
}
