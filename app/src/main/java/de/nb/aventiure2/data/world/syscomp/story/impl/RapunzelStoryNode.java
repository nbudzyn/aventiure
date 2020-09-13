package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.description.AbstractDescription;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELRUF;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AllgDescription.paragraph;
import static de.nb.aventiure2.german.description.DuDescription.du;
import static java.util.Arrays.asList;

public enum RapunzelStoryNode implements IStoryNode {
    // Idee für die Schritte: Das muss man machen, dann kommt man weiter (sonst nicht)

    TURM_GEFUNDEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmGefunden),
    RAPUNZEL_SINGEN_GEHOERT(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelSingenGehoert,
            TURM_GEFUNDEN
    ),
    // Dies wird durch checkAndAdvanceIfAppropriate() automatisch freigeschaltet.
    // Tipps dafür wäre sinnvoll
    ZAUBERIN_MACHT_RAPUNZELBESUCHE(),
    ZAUBERIN_AUF_TURM_WEG_GETROFFEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinAufTurmWegGefunden,
            TURM_GEFUNDEN),
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet,
            TURM_GEFUNDEN
    ),
    ZU_RAPUNZEL_HINAUFGESTIEGEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZuRapunzelHinaufgestiegen,
            ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET);

    // STORY "Am Fenster sitzt eine junge Frau, so schön als
    //  du unter der Sonne noch keine gesehen hast.
    //  Ihre Haare, fein wie gesponnen
    //  Gold, hat sie um einen Fensterhaken gewickelt, so konntest du
    //  daran heraufsteigen.
    //  Die junge Frau erschrickt gewaltig, als du zu ihr
    //  hereinkommst. Schnell bindet sie ihre Haare wieder zusammen,
    //  dann starrt sie dich an.
    //  Doch du fängst an ganz freundlich mit ihr zu reden
    //  und erzählst ihr, dass von ihrem Gesang dein Herz so sehr sei
    //  bewegt worden, dass es dir keine Ruhe gelassen und du sie
    //  selbst habest sehen müssen.
    //  Da verliert die junge Frau ihre Angst und es bricht aus ihr heraus.
    //  Eine alte Zauberin hötte sie ihren Eltern fortgenommen, seit ihrem
    //  zwölften Jahre sei sie in diesen Turm geschlossen.
    //  Du fragst sie nach ihrem Namen, aber der ist so ungewöhnlich, dass
    //  du ihn dir nicht merken kannst. Es ist dir zu ungegenehm, noch einmal zu fragen.
    //  Wie kannst du ihr helfen, so fragst du sie.
    //  Bring mir einen Ballen Seide, sagt sie,
    //  Daraus
    //  will ich eine Leiter flechten, und wenn die fertig ist, so steige
    //  ich herunter.
    //  So verabredet ihr es.
    //  „Aber komm nicht, wenn die Alte bei mir ist, sagt sie noch,
    //  sonst sind wir beide verloren"

    // STORY Extra: Zauberin ruft von unten nach Rapunzel: "Schnell versteckt euch unter dem Bett"

    // STORY
    //  "du steigst wieder herab" /  "Schnell kommst du wieder herab"

    // STORY fremdländisch aussehender Händler auf Schlossfest vor einem Zelt

    // STORY Seide für großen Klumpen Gold (goldene Kugel verkaufen)
    //  "kaufst ihm ... ab"

    // STORY Wieder hinaufsteigen mit schwerem Ballen Seide
    //  "du warst mich heute schwer heraufzuziehen"
    //  Unter dem Bett verstecken
    //  "wie deine Augen noch nie eine erblickt hatten"

    // STORY Rapunzel: Will sich vom Spieler aus dem Wald führen lassen

    // STORY?! Zauberin, wenn man sie trifft  "sieht dich mit bösen und giftigen Blicken an"

    // STORY Rapunzel flicht die Leiter

    // STORY SC steigt erneut hinauf.
    //  "in einem Augenblick bist du oben"
    //  "Oben findest du..."
    //  "Die Alte hat nichts gemerkt"

    //  STORY Die Leiter ist fertig.
    //  STORY Leiter oben am Fensterhaken fest
    //   lässt sie herab
    //   Beide steigen hinunter, die Leiter bleibt hängen (Raum bleibt zugänglich)

    // STORY Spieler führt Rapunzel aus dem Wald hinaus - ENDE

    private static final String STORY_ADVANCE_COUNTER =
            "RapunzelStoryNode_STORY_ADVANCE_COUNTER";

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
    public IHinter getHinter() {
        return hinter;
    }

    @Nullable
    public static AvTimeSpan checkAndAdvanceIfAppropriate(
            final AvDatabase db,
            final NarrationDao n,
            final World world) {
        final IHasStateGO<RapunzelsZauberinState> zauberin = loadZauberin(world);

        if (db.counterDao().incAndGet(STORY_ADVANCE_COUNTER) > 5) {
            if (zauberin.stateComp().hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE) &&
                    world.loadSC().locationComp()
                            .hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM) &&
                    RapunzelsZauberinReactionsComp.
                            liegtImZeitfensterFuerRapunzelbesuch(db.nowDao().now())) {
                return ensureAdvancedToZauberinMachtRapunzelbesuche(db, world);
            }
        }

        return null;
    }

    @NonNull
    public static AvTimeSpan ensureAdvancedToZauberinMachtRapunzelbesuche(
            final AvDatabase db, final World world) {
        final IHasStateGO<RapunzelsZauberinState> zauberin = loadZauberin(world);

        if (zauberin.stateComp().hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE)) {
            db.counterDao().reset(STORY_ADVANCE_COUNTER);
            return zauberin.stateComp().narrateAndSetState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH);
        }

        return noTime();
    }

    // STORY Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht
    private static AvTimeSpan narrateAndDoHintAction_TurmGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
        alt.add(
                paragraph("Hast du den Wald eigentlich schon überall erkundet?"),
                paragraph("Was gibt es wohl noch alles im Wald zu entdecken, fragst du dich"),
                paragraph("Dir kommt der geheimnisvolle Turm in den Sinn - du wirst sein "
                        + "Geheimnis bestimmt noch lüften!"));

        // STORY (bis SC Rapunzel gefunden hat) Mutter sammelt im
        //  Wald Holz und klagt ihr Leid: Tochter an Zauberin verloren
        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_RapunzelSingenGehoert(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Ob der Turm wohl bewohnt ist?"),
                    paragraph("Eine Rast würde dir sicher guttun"));
        } else {
            alt.add(paragraph(
                    "Dir kommt noch einmal der alte Turm auf der Hügelkuppe "
                            + "in den Sinn. Ob der wohl bewohnt ist?"));
        }

        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_ZauberinAufTurmWegGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.addAll(altTurmWohnenHineinHeraus(world));

        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.addAll(altTurmWohnenHineinHeraus(world));

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(du(PARAGRAPH,
                    "wirst",
                    "bestimmt noch den Turm hinaufkommen!",
                    "bestimmt",
                    noTime())
                    .beendet(PARAGRAPH));
        }

        if (loadZauberin(world).locationComp().hasSameUpperMostLocationAs(SPIELER_CHARAKTER) &&
                // Vor dem Schloss fällt sie dem SC nicht auf
                !loadZauberin(world).locationComp()
                        .hasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            alt.add(paragraph("Was will die Frau bloß?"),
                    paragraph("Was will die Frau wohl?"),
                    paragraph("Was mag die Frau wollen?"));
        }

        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_ZuRapunzelHinaufgestiegen(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
        final IHasStateGO<RapunzelState> rapunzel = loadRapunzel(world);

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) &&
                rapunzel.stateComp().hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            alt.add(paragraph("Warum nicht auch die Haare hinaufsteigen?"));
        } else if (world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
            alt.add(paragraph(
                    "Du bist dir plötzlich sicher: Wenn dich jemand in dieser Welt braucht, "
                            + "wartet er – wartet sie! – oben im Turm auf dich!"),
                    paragraph(
                            "Plötzlich steht es dir klar vor Augen: Du musst in den Turm steigen – "
                                    +
                                    "du weißt doch, was du zu rufen hast! Du wirst doch keine Angst "
                                    + "vor der dürren Frau haben?"));

        } else {
            // SC hat alles vergessen
            alt.add(paragraph(
                    "Hin und wieder musst du an den alten Turm denken. Du hast das Gefühl, etwas "
                            + "Wichtiges vergessen zu haben, aber es will dir partout nicht "
                            + "einfallen"),
                    paragraph(
                            "Manachmal hast du das Gefühl: Du hast noch eine wichtige Rolle "
                                    + "zu spielen. Aber wenn du genauer darüber nachdenkst, weißt "
                                    + "du plötzlich nicht weiter. Es ist wie verhext"));
            alt.addAll(altTurmWohnenHineinHeraus(world));
        }

        return n.addAlt(alt);
    }

    private static ImmutableList<AbstractDescription<?>> altTurmWohnenHineinHeraus(
            final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Wenn im Turm jemand wohnt – wie kommt er herein "
                    + "oder hinaus?"));
            alt.add(paragraph("Ob jemand im Turm ein und aus geht? Aber wie bloß?"));
        } else {
            alt.add(du(PARAGRAPH,
                    "musst",
                    "wieder an den alten Turm denken… wenn dort jemand wohnt, "
                            + "wie kommt der bloß hinein oder heraus?",
                    "wieder",
                    noTime())
                    .beendet(PARAGRAPH));
            alt.add(paragraph(
                    "Dir kommt auf einmal wieder der alte Turm in den Sinn: "
                            + "Wer wird darinnen wohl wohnen?"));
        }

        return alt.build();
    }

    @NonNull
    private static IHasStateGO<RapunzelState> loadRapunzel(final World world) {
        return (IHasStateGO<RapunzelState>) world.load(RAPUNZEL);
    }

    @NonNull
    private static <Z extends IHasStateGO<RapunzelsZauberinState> & ILocatableGO>
    Z loadZauberin(final World world) {
        return (Z) world.load(RAPUNZELS_ZAUBERIN);
    }
}
