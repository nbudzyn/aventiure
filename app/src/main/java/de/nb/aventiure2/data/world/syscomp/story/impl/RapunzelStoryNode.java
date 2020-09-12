package de.nb.aventiure2.data.world.syscomp.story.impl;

import androidx.annotation.Nullable;

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
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
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
    // STORY Zauberin erst loslaufen lassen, wenn der Spieler bei anderen Dingen
    //  nicht weiter kommt. Vorher logischerweise auch keine Tipps für Zauberin!!
    //  (10000 Schritte o.ä.??)
    //  Dann häufiger loslaufen lassen, wenn Spieler nicht
    //  weiterkommt, nicht nur 1x täglich!
    ZAUBERIN_AUF_TURM_WEG_GETROFFEN(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinAufTurmWegGefunden,
            TURM_GEFUNDEN),
    ZAUBERIN_HEIMLICH_BEIM_RUFEN_BEOBACHTET(10, VOR_DEM_ALTEN_TURM,
            RapunzelStoryNode::narrateAndDoHintAction_ZauberinHeimlichBeimRufenBeobachtet,
            TURM_GEFUNDEN
    )
    // STORY "und der Königssohn stieg hinauf.
    //  Anfangs erschrak Rapunzel gewaltig, als ein Mann zu ihr
    //  hereinkam, wie ihre Augen noch nie einen erblickt hatten,
    //  doch der Königssohn fing an ganz freundlich mit ihr zu reden
    //  und erzählte ihr, daß von ihrem Gesang sein Herz so sehr sei
    //  bewegt worden, daß es ihm keine Ruhe gelassen und er sie
    //  selbst habe sehen müssen. Da verlor Rapunzel ihre Angst, und
    //  als er sie fragte, ob sie ihn zum Mann nehmen wollte, und sie
    //  sah, daß er jung und schön war, so dachte sie »der wird mich
    //  lieber haben als die alte Frau Gotel,« und sagte ja, und legte
    //  ihre Hand in seine Hand. Sie sprach »ich will gerne mit dir
    //  gehen, aber ich weiß nicht, wie ich herabkommen kann. Wenn
    //  du kommst, so bringe jedesmal einen Strang Seide mit, daraus
    //  will ich eine Leiter flechten, und wenn die fertig ist, so steige
    //  ich herunter und du nimmst mich auf dein Pferd.« Sie verabredeten,
    //  daß er bis dahin alle Abend zu ihr kommen sollte,
    //  denn bei Tag kam die Alte. Die Zauberin merkte auch nichts
    //  davon, bis einmal Rapunzel anfing und zu ihr sagte »sag sie
    //  mir doch, Frau Gotel, wie kommt es nur, sie wird mir viel
    //  schwerer heraufzuziehen als der junge Königssohn, der ist in
    //  67
    //  einem Augenblick bei mir.« »Ach du gottloses Kind,« rief die
    //  Zauberin, »was muß ich von dir hören, ich dachte, ich hätte
    //  dich von aller Welt geschieden, und du hast mich doch betrogen
    //  !« In ihrem Zorne packte sie die schönen Haare der Rapunzel,
    //  schlug sie ein paarmal um ihre linke Hand, griff eine
    //  Schere mit der rechten, und ritsch, ratsch waren sie abgeschnitten,
    //  und die schönen Flechten lagen auf der Erde. Und
    //  sie war so unbarmherzig, daß sie die arme Rapunzel in eine
    //  Wüstenei brachte, wo sie in großem Jammer und Elend leben
    //  mußte.
    //  Denselben Tag aber, wo sie Rapunzel verstoßen hatte, machte
    //  abends die Zauberin die abgeschnittenen Flechten oben am
    //  Fensterhaken fest, und als der Königssohn kam und rief
    //  »Rapunzel, Rapunzel,
    //  laß dein Haar herunter,«
    //  so ließ sie die Haare hinab. Der Königssohn stieg hinauf, aber
    //  er fand oben nicht seine liebste Rapunzel, sondern die Zauberin,
    //  die ihn mit bösen und giftigen Blicken ansah. »Aha,« rief
    //  sie höhnisch, »du willst die Frau Liebste holen, aber der
    //  schöne Vogel sitzt nicht mehr im Nest und singt nicht mehr,
    //  die Katze hat ihn geholt und wird dir auch noch die Augen
    //  auskratzen. Für dich ist Rapunzel verloren, du wirst sie nie
    //  wieder erblicken.« Der Königssohn geriet außer sich vor
    //  Schmerzen, und in der Verzweiflung sprang er den Turm herab
    //  : das Leben brachte er davon, aber die Dornen, in die er fiel,
    //  zerstachen ihm die Augen. Da irrte er blind im Walde umher,
    //  aß nichts als Wurzeln und Beeren, und tat nichts als jammern
    //  und weinen über den Verlust seiner liebsten Frau. So wanderte
    //  er einige Jahre im Elend umher und geriet endlich in die Wüstenei,
    //  wo Rapunzel mit den Zwillingen, die sie geboren hatte,
    //  einem Knaben und Mädchen, kümmerlich lebte. Er vernahm
    //  eine Stimme, und sie deuchte ihn so bekannt : da ging er darauf
    //  zu, und wie er herankam, erkannte ihn Rapunzel und fiel
    //  ihm um den Hals und weinte. Zwei von ihren Tränen aber benetzten
    //  seine Augen, da wurden sie wieder klar, und er konnte
    //  damit sehen wie sonst. Er führte sie in sein Reich, wo er mit
    //  68
    //  Freude empfangen ward, und sie lebten noch lange glücklich
    //  und vergnügt."
    ;


    private final ImmutableSet<RapunzelStoryNode> preconditions;

    private final int expAchievementSteps;

    @Nullable
    private final GameObjectId locationId;

    private final IHinter hinter;

    RapunzelStoryNode(final int expAchievementSteps, @Nullable final GameObjectId locationId,
                      final IHinter hinter,
                      final RapunzelStoryNode... preconditions) {
        this(asList(preconditions), expAchievementSteps, locationId, hinter);
    }

    RapunzelStoryNode(final Collection<RapunzelStoryNode> preconditions,
                      final int expAchievementSteps, @Nullable final GameObjectId locationId,
                      final IHinter hinter) {
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
        return this == values()[values().length - 1];
    }

    @Override
    public IHinter getHinter() {
        return hinter;
    }

    // STORY Alternativen für Tipp-Texte, bei denen Foreshadowing stärker im
    //  Vordergrund steht
    private static AvTimeSpan narrateAndDoHintAction_TurmGefunden(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
        alt.add(paragraph("Hast du den Wald eigentlich schon überall erkundet?"));
        alt.add(paragraph("Was gibt es wohl noch alles im Wald zu entdecken, fragst du dich"));
        alt.add(paragraph("Dir kommt der geheimnisvolle Turm in den Sinn - du wirst sein "
                + "Geheimnis bestimmt noch lüften!"));

        // STORY (bis SC Rapunzel gefunden hat) Mutter sammelt im
        //  Wald Holz und klagt ihr Leid: Tochter an Zauberin verloren
        return n.addAlt(alt);
    }

    private static AvTimeSpan narrateAndDoHintAction_RapunzelSingenGehoert(
            final AvDatabase db, final NarrationDao n, final World world) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (world.loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            alt.add(paragraph("Ob der Turm wohl bewohnt ist?"));
            alt.add(paragraph("Eine Rast würde dir sicher guttun"));
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

        final ILocatableGO zauberin = (ILocatableGO) world.load(RAPUNZELS_ZAUBERIN);
        if (zauberin.locationComp()
                .hasSameUpperMostLocationAs(SPIELER_CHARAKTER) &&
                // Vor dem Schloss fällt sie dem SC nicht auf
                !zauberin.locationComp().hasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            alt.add(paragraph("Was will die Frau bloß?"));
            alt.add(paragraph("Was will die Frau wohl?"));
            alt.add(paragraph("Was mag die Frau wollen?"));
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
}
