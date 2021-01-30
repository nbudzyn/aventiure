package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class RastenAction extends AbstractScAction {
    private final ILocationGO location;

    public static List<RastenAction> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            @Nullable final ILocationGO location) {
        final ImmutableList.Builder<RastenAction> res = ImmutableList.builder();
        if (location != null && location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            res.add(new RastenAction(scActionStepCountDao, timeTaker, n, world, location));
        }

        return res.build();
    }

    private RastenAction(final SCActionStepCountDao scActionStepCountDao,
                         final TimeTaker timeTaker,
                         final Narrator n,
                         final World world,
                         final ILocationGO location) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.location = location;
    }

    @Override
    public String getType() {
        return "actionRastenWarten";
    }

    @Override
    @NonNull
    public String getName() {
        return "Rasten";
    }

    @Override
    public void narrateAndDo() {
        // FIXME Schläft man (nach mehrfachem Rasten?
        //  Und bei ausreichender Müdigkeit?) automatisch ein?

        if (isDefinitivFortsetzung() &&
                ((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp()
                        .hasState(SINGEND)) {
            narrateAndDoRapunzelZuhoeren();
        } else if (location.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL) {
            narrateAndDoDunkel();
        } else {
            narrateAndDoHell();
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoRapunzelZuhoeren() {
        sc.feelingsComp().requestMoodMin(Mood.GLUECKLICH);

        n.narrateAlt(mins(4),
                du("bist", "ganz still")
                        .undWartest()
                        .dann(),
                du("genießt deine Rast")
                        .undWartest()
                        .dann(),
                du(SENTENCE, "sitzt", "glücklich da und genießt")
                        .mitVorfeldSatzglied("glücklich")
                        .beendet(SENTENCE),
                neuerSatz("Dein Herz wird ganz warm von dem Gesang")
                        .beendet(SENTENCE));

        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
    }

    private void narrateAndDoDunkel() {
        sc.feelingsComp().requestMoodMax(Mood.VERUNSICHERT);

        n.narrateAlt(mins(3),
                neuerSatz("Die Bäume rauschen in "
                        + "der Dunkelheit, die Eulen schnarren, und "
                        + "und es fängt an, dir angst zu werden")
                        .beendet(SENTENCE),
                neuerSatz("Es ist dunkel und ungemütlich. Krabbelt da etwas auf "
                        + "deinem rechten Bein? Du schlägst mit der Hand zu, kannst aber nichts "
                        + "erkennen")
                        .beendet(SENTENCE),
                neuerSatz("In den Ästen über dir knittert und rauscht es. Dich friert"));
    }

    private void narrateAndDoHell() {
        sc.feelingsComp().requestMoodMin(Mood.ZUFRIEDEN);

        // IDEA Hier ist sehr auffällig, dass die dann()-Logik nicht stimmt:
        //  Ob "Dann..." sinnvoll ist, hängt wesentlich (auch) vom Folgesatz ab.
        //  Rast -> Rast -> Rast: Kein "Dann..."
        //  Rast -> Aufstehen: "Dann..."
        //  Anscheinend setzt "Dann..." eine Art "Aktionsänderung" voraus.

        // IDEA "Dann" nicht bei "statischen Verben" (du hast Glück, du hast Hunger,
        //  du freust dich) verwenden.

        // IDEA "Dann" nur verwenden, wenn der es einen Aktor gibt und der Aktor im letzten
        //  Satz gleich war. (Nach der Logik kann man dann auch für Beschreibungen in
        //  der dritten Person verwenden!)

        final AltDescriptionsBuilder alt = alt();

        alt.add(
                du(SENTENCE, "hältst",
                        "verborgen unter den Bäumen noch eine Zeitlang Rast")
                        .mitVorfeldSatzglied("verborgen unter den Bäumen")
                        .beendet(SENTENCE)
                        .dann(),
                neuerSatz("Es tut gut, eine Weile zu rasten. Über dir zwitschern die "
                        + "Vögel und die Grillen zirpen")
                        .beendet(SENTENCE)
                        .dann(),
                du(SENTENCE, "streckst", "die Glieder und hörst auf das Rauschen "
                        + "in den "
                        + "Ästen über dir. Ein Rabe setzt "
                        + "sich neben dich und fliegt nach einer Weile wieder fort")
                        .beendet(SENTENCE)
                        .dann(),
                du(SENTENCE, "ruhst",
                        "noch eine Weile aus und lauschst, wie die Insekten",
                        "zirpen und der Wind saust")
                        .mitVorfeldSatzglied("eine Weile")
                        .beendet(SENTENCE)
                        .dann()
        );

        if (world.loadSC().feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH) {
            neuerSatz("Deine müden Glieder brauchen Erholung. Du bist ganz "
                    + "still und die Vögel setzen sich "
                    + "auf die Äste über dir "
                    + "und singen, was sie nur wissen")
                    .beendet(SENTENCE)
                    .dann();
        }

        n.narrateAlt(alt, mins(10));
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        if (isDefinitivFortsetzung() && n.lastNarrationWasFromReaction()) {
            // Der Spieler rastet weiter, obwohl andere Dinge passiert sind...
            return true;
        }

        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.RASTEN);
    }
}
