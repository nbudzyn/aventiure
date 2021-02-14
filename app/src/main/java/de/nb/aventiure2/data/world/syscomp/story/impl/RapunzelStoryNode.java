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
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altParagraphs;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.duParagraph;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static java.util.Arrays.asList;

@SuppressWarnings("UnnecessaryReturnStatement")
public enum RapunzelStoryNode implements IStoryNode {
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
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet,
            TURM_GEFUNDEN
    ),
    ZU_RAPUNZEL_HINAUFGESTIEGEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZuRapunzelHinaufgestiegen,
            ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET),
    RAPUNZEL_RETTUNG_VERSPROCHEN(15, OBEN_IM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelRettungVersprochen,
            ZU_RAPUNZEL_HINAUFGESTIEGEN);

    // FIXME Wenn Zauberin kommt: "Du kriechst unter das Bett. Es ist eng und staubig"
    //  - Idee: Das Bett eraufbauen / Drunterkriechen erst ermöglichen, wenn Zauberin kommt
    //    ("versteck dich..." Du schaust um dich und dein Blick fällt auf das Bett")
    //  - Oder: unter-Bett ist immer zugänglich, erhöht Abneigung.
    //  SC kriecht unter das Bett. Achtung: Rapunzel kann nicht schauen und spricht nicht
    //   mit SC, der unter dem Bett liegt. Außer "willst du nicht wieder rauskommen??"
    //   Danach... Rapunzel schaut...
    //   "Was sollte jetzt das?"

    // FIXME "Vergiss deine Kugel / ... nicht (collective) ruft sie dir noch nach, aber
    //  da bist du schon unten / stehst du schon vor...

    // FIXME Vor dem Absteigen: Prüfen ob Zauberin da ist?

    // FIXME "Die Zauberin und RAPUNZEL unterhalten sich, aber eigentlich haben sie
    //  einander nichts zu sagen."
    //  - Die ZAUBERIN erzählt von ihren täglichen Verrichtungen und RAPUNZEL hört
    //    artig zu.
    //  - Die Zauberin begrüßt RAPUNZEL, dann ist sie auf einmal still.
    //    "Wonach riecht es hier?" fragt die Zauberin mit scharfer Stimme
    //    "Oh, das... müssen wieder die Fledermäuse sein, sagt die junge Frau und stellt sich vor
    //    das Bett. Dir pocht das Herz"
    //  - Die Zauberin hat Essen und Trinken mitgebracht und du hörst den beiden bei der
    //   Mahlzeit zu. BEI HUNGER: Dein Magen knurrt, aber es scheint niemand zu bemerken.

    // FIXME WARTEN "Du liegst lange Zeit ganz still. Der Staub kribbelt in deiner Nase."
    //  "Endlich verabschiedet sich die ZAUBERIN und steigt herab"
    //  "Du kannst wieder herauskommen - hörst du es lieblich sagen"

    // FIXME "An der Wand lehnt ein alter Rucksack / ... Kiepe...,
    //  wie man sie zum Holzsammeln verwenden würde"
    //  Du setzt... auf. Ziemlich schwer. (Man wird schneller müde.)
    //  Du setzt... ab. Du stellst.... auf den Boden / Waldboden...

    // FIXME Draußen hat sich das Wetter verändert. Es hat deutlich abgekühlt und der
    //  Himmel bezieht sich.
    //  Der Wind wird stärker
    //  Der Wind pfeift dir ums Gesicht
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?
    //  Ein Sturm zieht auf
    //  Hoffentlich bleibt es wenigstens trocken
    //  (Kein Regen - keine nassen Klamotten o.Ä.)
    //  Der Wind ist jetzt sehr kräftig und angenehm. Kalt ist es geworden.
    //  Der Sturm biegt die Bäume.
    //  Ein ziemlicher Krach (Hexe geht nicht mehr spazieren. Schlossfest?!)
    //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
    //  wäre schön.
    //  Langsam scheint sich das Wetter wieder zu bessern / der Sturm flaut allmählich ab.
    //  Fürs Wetter lässt sich wohl einiges von Hunger oder Müdigkeit übernehmen.
    //  Das Wetter müsste quasi eine "Tendenz" erhalten, nach der es sich langsam
    //  entwickelt. Und man braucht regelmäßige Hinweise (je nach Dramatik des Wettes).
    //  Außerdem sollten alle "heiß..."–Hinweise dort zentralisiert werden.
    //  Man könnte auch verschiedene Skalen haben kalt-heiß, windstill-stürmisch...
    //  und Zielwerte mit Zielzeiten festsetzen. Schwierigkeit: Die Texte müssten
    //  alle Skalen gleichzeitig berücksichtigen?
    //  Oder es gibt "Wetterwege" Zwischenstationen, und man gibt nur ein Ziel vor...
    //  (Ähnlich wie beim movement)

    // FIXME Wetter als Konzept ähnlich Tageszeiten? Aber nur dramturgisch geändert, nicht
    //  automatisch?
    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept!)

    // FIXME "Hier hat der Sturm hat viele Äste von den Bäumen gebrochen. Überall liegt 
    //  Holz herum, kleine und große Äste.
    // - Du klaubst Holz auf 
    // - Holz in armlange Stücke brechen

    // FIXME Binsen, Seil flechten...

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

    // IDEA Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
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
        if (world.hasSameOuterMostLocationAsSC(RAPUNZELS_ZAUBERIN)) {
            narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinImRaum(n);
        } else {
            narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum(
                    timeTaker, n, world);
        }
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinImRaum(
            final Narrator n) {
        n.narrateAlt(NO_TIME,
                paragraph("was will die Frau bloß?"),
                paragraph("was will die Frau wohl?"),
                paragraph("was mag die Frau wollen?"),
                paragraph("es wäre spannend, die Frau einmal heimlich zu beobachten"),
                paragraph("ist es nicht seltsam, dass die Frau so kurz angebunden ist "
                        + "und nicht erzählen mag, wohin sie eigentlich möchte?"));
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet_ZauberinNichtImRaum(
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
                        + " wohl?"),
                paragraph("dir fällt auf: Nachts ist dir die magere Frau noch nie "
                        + "begegnet"),
                paragraph("des Nachts scheint hier kaum jemand auf den Beinen zu sein - "
                        + "außer dir"),
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
            alt.add(du(PARAGRAPH, "wirst", "bestimmt noch den Turm hinaufkommen!")
                    .mitVorfeldSatzglied("bestimmt")
                    .beendet(PARAGRAPH));
            alt.add(du(PARAGRAPH, "wirst",
                    "bestimmt noch den Turm hinaufkommen – vielleicht musst du dich "
                            + "nur einmal auf die Lauer legen und beobachten, ob jemand "
                            + "hineinkommt?").mitVorfeldSatzglied("bestimmt")
                    .beendet(PARAGRAPH));
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
                            "steigen, und du weißt, was du zu rufen hast! –  Aber die dürre Frau, "
                                    + "die macht dir Angst…"));
        } else {
            // SC hat alles vergessen
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
                                    + "haben! Aber was bloß?"));
            alt.addAll(altTurmWohnenHineinHeraus(world));
        }

        n.narrateAlt(alt, NO_TIME);
    }


    private static void narrateAndDoHintAction_RapunzelRettungVersprochen(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        final AltDescriptionsBuilder alt = alt();
        final IHasStateGO<RapunzelState> rapunzel = loadRapunzel(world);

        if (world.loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            alt.add(duParagraph("hast", "noch so viele Fragen an",
                    world.anaph(RAPUNZEL).akkK()),
                    duParagraph("hast",
                            "Eindruck, die schöne junge Frau hätte dir noch viel zu",
                            "erzählen"));
        } else if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (rapunzel.stateComp().hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
                alt.add(paragraph("Droht dir wohl Gefahr, wenn du die Haare hinaufsteigst?"));
            } else {
                alt.add(paragraph("Warum nicht oben im Turm einmal Hallo sagen?"),
                        duParagraph("hättest",
                                "Lust, wieder einmal bei der jungen Frau",
                                "oben im Turm vorbeizuschauen"));
            }
        } else {
            alt.addAll(altParagraphs("Die junge Frau oben im Turm",
                    ImmutableList.of("geht", "will"),
                    "dir nicht mehr aus dem Kopf"));
            alt.addAll(altParagraphs("Die schöne junge Frau oben im Turm will dir nicht",
                    ImmutableList.of("mehr", ""),
                    "aus dem Kopf gehen"));
            alt.add(paragraph("Deine Gedanken kreisen immer wieder um",
                    world.getDescription(RAPUNZEL).akkK()));
            alt.add(duParagraph("fühlst", "dich etwas einsam"));
            alt.addAll(altParagraphs("Warum nicht mal wieder bei der",
                    ImmutableList.of("netten", ""),
                    "jungen Frau oben im Turm vorbeischauen?"));
            alt.add(duParagraph("musst",
                    "an die junge Frau oben im Turm denken. Freut die sich wohl,",
                    "wenn du noch einmal bei ihr vorbeischaust?"));
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
                            + "wie kommt der bloß hinein oder heraus?")
                    .mitVorfeldSatzglied("wieder")
                    .beendet(PARAGRAPH));
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
