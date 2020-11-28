package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelsZauberinReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.story.IStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.Story;
import de.nb.aventiure2.german.description.AbstractDescription;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static java.util.Arrays.asList;

public enum RapunzelStoryNode implements IStoryNode {

    TURM_GEFUNDEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_TurmGefunden),
    RAPUNZEL_SINGEN_GEHOERT(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_RapunzelSingenGehoert,
            TURM_GEFUNDEN
    ),
    // STORY Automatisch dann freischalten, wenn der SC vom Brunnen erstmals zurückkehrt und es
    //  Tag ist etc., so dass der SC der Zauberin auf jeden Fall einmal im Wald begegnet
    // Dies wird durch checkAndAdvanceIfAppropriate() automatisch freigeschaltet.
    // Tipps dafür wäre nicht sinnvoll
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


    // STORY Was, wenn die Kugel zu Boden fällt oder der SC sie hinlegt?
    //  "(Die junge Frau) hebt (die goldene Kugel) auf und..."

    // STORY "Mit RAPUNZEL unterhalten"
    //  - "Du fragst sie nach ihrem Namen, aber der ist so ungewöhnlich, dass
    //   du ihn dir nicht merken kannst. Es ist dir zu ungegenehm, noch einmal zu fragen."
    //  - Ihr plaudert eine Weile - ihr versteht euch sehr gut.
    //  - (Dann...) RAPUNZEL schwärmt vom Sonnenaufgang über dem Blätterdach. Ihre Augen leuchten.
    //  - GGF Du erzählst IHR von dieser seltsamen Sache, die du mit dem Frosch erlebt hast. Sie
    //    schaut dich nachdenklich an.
    //  - "Welche Jahreszeit riechst du am liebsten?" fragt sie dich. Du entscheidest dich für
    //    den Sommer. "Bei mir ist es der Frühling" sagt sie und strahlt bei dem Gedanken
    //    "Ich wache auf und sofort weiß ich - die Krokusse sind aufgeblüht. Dann freue
    //    ich mich schon auf die Schwalben, die in der Mauer über mir brüten"
    //  - "Die Frau mit dieser... Nase - das ist deine..."  - ja, sie hält mich gefangen.
    //    Aber sie ist gut zu mir. -> memoryComp: Spieler weiß, dass es die Zauberin ist,
    //    DESCRIPTION ANPASSEN?!
    //  - Wie kannst du ihr helfen, so fragst du sie. Wenn ich einen Ballen Seide hätte, sagt sie,
    //    ich könnte mir eine Leiter flechten und steige daran herunter. Aber ein Ballen Seide -
    //    der wäre ein Vermögen wert! (Meldung muss beim 3. Mal auch jeden Fall kommen ->
    //    Status anpassen?! Kombinierter Status (X /Y)?)

    // STORY Wenn Zauberin kommt: "Du kriechst unter das Bett. Es ist eng und staubig"

    // STORY "Die Zauberin und RAPUNZEL unterhalten sich, aber eigentlich haben sie
    //  einander nichts zu sagen."
    //  - Die ZAUBERIN erzählt von ihren täglichen Verrichtungen und RAPUNZEL hört
    //    artig zu.
    //  - Die Zauberin begrüßt RAPUNZEL, dann ist sie auf einmal still.
    //    "Wonach riecht es hier?" fragt die Zauberin mit scharfer Stimme
    //    "Oh, das... müssen wieder die Fledermäuse sein, sagt die junge Frau und stellt sich vor
    //    das Bett. Dir pocht das Herz"
    //  - Die Zauberin hat Essen und Trinken mitgebracht und du hörst den beiden bei der
    //   Mahlzeit zu. BEI HUNGER: Dein Magen knurrt, aber es scheint niemand zu bemerken.
    // STORY WARTEN "Du liegst lange Zeit ganz still. Der Staub kribbelt in deiner Nase."
    //  "Endlich verabschiedet sich die ZAUBERIN und steigt herab"
    //  "Du kannst wieder herauskommen - hörst du es lieblich sagen"

    // STORY Oder: Man muss eine Strickleiter
    //  besorgen - oder Seide kaufen und etwas zum Stricken??? Gold gabs vielleicht
    //  vom Froschprinzen?

    // STORY fremdländisch aussehender Händler auf Schlossfest vor einem Zelt

    // STORY Seide für großen Klumpen Gold (goldene Kugel,
    //  dabei stressen, das man sie lieb gewonnen hat)
    //  "kaufst ihm ... ab"
    //  Oder für ein Goldstück, das man vom Prinzen bekommen hat?
    //  Oder Sterntaler / Münzen in Lichtung im Wald, die man brauchen kann, um Seide
    //  für Rapunzel zu kaufen.

    // STORY Wieder hinaufsteigen mit schwerem Ballen Seide
    //  "du warst mich heute schwer heraufzuziehen"
    //  Unter dem Bett verstecken
    //  "wie deine Augen noch nie eine erblickt hatten"

    // STORY Rapunzel: Will sich vom Spieler aus dem Wald führen lassen

    // STORY?! Zauberin, wenn man sie trifft  "sieht dich mit bösen und giftigen Blicken an"

    // STORY Rapunzel flicht die Leiter

    // STORY SC steigt erneut hinauf.
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
            final Narrator n,
            final World world) {
        final IHasStateGO<RapunzelsZauberinState> zauberin = loadZauberin(world);

        if (db.counterDao().incAndGet(STORY_ADVANCE_COUNTER) > 5) {
            if (zauberin.stateComp().hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE) &&
                    world.loadSC().locationComp()
                            .hasRecursiveLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM) &&
                    RapunzelsZauberinReactionsComp.
                            liegtImZeitfensterFuerRapunzelbesuch(db.nowDao().now())) {
                ensureAdvancedToZauberinMachtRapunzelbesuche(db, world);
                return true;
            }
        }

        return false;
    }

    public static void ensureAdvancedToZauberinMachtRapunzelbesuche(
            final AvDatabase db, final World world) {
        final IHasStateGO<RapunzelsZauberinState> zauberin = loadZauberin(world);

        if (zauberin.stateComp().hasState(MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE)) {
            db.counterDao().reset(STORY_ADVANCE_COUNTER);
            zauberin.stateComp().narrateAndSetState(VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH);
            return;
        }
    }

    // FIXME Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht
    private static void narrateAndDoHintAction_TurmGefunden(
            final AvDatabase db, final Narrator n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
        alt.add(
                paragraph("Hast du den Wald eigentlich schon überall erkundet?"),
                paragraph("Was gibt es wohl noch alles im Wald zu entdecken, fragst du dich"),
                paragraph("Dir kommt der geheimnisvolle Turm in den Sinn - du wirst sein "
                        + "Geheimnis bestimmt noch lüften!"));

        // STORY (bis SC Rapunzel gefunden hat) Mutter sammelt im
        //  Wald Holz und klagt ihr Leid: Tochter an Zauberin verloren
        n.narrateAlt(alt, noTime());
    }

    private static void narrateAndDoHintAction_RapunzelSingenGehoert(
            final AvDatabase db, final Narrator n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        // FIXME Kein Tipp für Rapunzel, wenn Hexe noch nicht losgegangen ist (schließlich
        //  kann der Spieler nichts tun)

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Ob der Turm wohl bewohnt ist?"),
                    paragraph("Eine längere Rast würde dir sicher guttun"));

            // FIXME ab dem zb 3. Mal deutlichere Hinweise länger Rasten - sofern das
            //  Warten nicht ohnehin lang genug ist.
        } else {
            alt.add(paragraph(
                    "Dir kommt noch einmal der alte Turm auf der Hügelkuppe "
                            + "in den Sinn. Ob der wohl bewohnt ist?"));
        }

        n.narrateAlt(alt, noTime());
    }

    private static void narrateAndDoHintAction_ZauberinAufTurmWegGefunden(
            final AvDatabase db, final Narrator n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.addAll(altTurmWohnenHineinHeraus(world));

        n.narrateAlt(alt, noTime());
    }

    private static void narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet(
            final AvDatabase db, final Narrator n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        alt.addAll(altTurmWohnenHineinHeraus(world));

        // FIXME Mehr sätze für RAPUNZELbeobachtentipps

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(du(PARAGRAPH,
                    "wirst",
                    "bestimmt noch den Turm hinaufkommen!",
                    "bestimmt")
                    .beendet(PARAGRAPH));
            alt.add(du(PARAGRAPH,
                    "wirst",
                    "bestimmt noch den Turm hinaufkommen – vielleicht musst du dich "
                            + "nur einmal auf die Lauer legen und beobachten, ob jemand "
                            + "hineinkommt?",
                    "bestimmt")
                    .beendet(PARAGRAPH));
            // FIXME Mehr Hinweise, dass man die magere Frau heimlich beobachten sollte
        }

        if (world.hasSameUpperMostLocationAsSC((IGameObject) loadZauberin(world))) {
            alt.add(paragraph("Was will die Frau bloß?"),
                    paragraph("Was will die Frau wohl?"),
                    paragraph("Was mag die Frau wollen?"));
        }

        n.narrateAlt(alt, noTime());
    }

    private static void narrateAndDoHintAction_ZuRapunzelHinaufgestiegen(
            final AvDatabase db, final Narrator n, final World world) {
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
            //  FIXME Dies hier nicht, wenn die Zauberin oben im Turm ist?
            //   Es ist ja ein irreführender Tipp!
        } else {
            // SC hat alles vergessen
            if (!world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
                alt.add(paragraph(
                        "Hin und wieder musst du an den alten Turm denken. Du hast das Gefühl, etwas "
                                + "Wichtiges vergessen zu haben, aber es will dir partout nicht "
                                + "einfallen"));
            }

            alt.add(paragraph(
                    "Manchmal hast du das Gefühl: Du hast noch eine wichtige Rolle "
                            + "zu spielen. Aber wenn du genauer darüber nachdenkst, weißt "
                            + "du plötzlich nicht weiter. Es ist wie verhext"));
            // FIXME Mehr Texte für diesen Fall!
            alt.addAll(altTurmWohnenHineinHeraus(world));
        }

        n.narrateAlt(alt, noTime());
    }

    @CheckReturnValue
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
                    "wieder")
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
