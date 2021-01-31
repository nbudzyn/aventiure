package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class RastenAction extends AbstractWartenRastenAction {
    private final ILocationGO location;

    public static List<RastenAction> buildActions(
            final CounterDao counterDao,
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            @Nullable final ILocationGO location) {
        final ImmutableList.Builder<RastenAction> res = ImmutableList.builder();
        if (location != null && location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            res.add(new RastenAction(
                    counterDao, scActionStepCountDao, timeTaker, n, world, location));
        }

        return res.build();
    }

    private RastenAction(final CounterDao counterDao,
                         final SCActionStepCountDao scActionStepCountDao,
                         final TimeTaker timeTaker,
                         final Narrator n,
                         final World world,
                         final ILocationGO location) {
        super(counterDao, scActionStepCountDao, timeTaker, n, world);
        this.location = location;
    }

    @Override
    @NonNull
    public String getName() {
        return "Rasten";
    }

    @Override
    public void narrateAndDo() {
        if (!sc.memoryComp().getLastAction().is(Action.Type.WARTEN) &&
                !sc.memoryComp().getLastAction().is(Action.Type.RASTEN)) {
            counterDao.reset(COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE);
        }

        if (automatischesEinschlafen()) {
            narrateAndDoSchlafen();
        } else {
            if (isDefinitivFortsetzung() && loadRapunzel().stateComp().hasState(SINGEND)) {
                narrateAndDoRapunzelZuhoeren();
            } else if (location.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL) {
                narrateAndDoDunkel();
            } else {
                narrateAndDoHell();
            }
        }
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    @Override
    protected void narrateAndDoEinschlafen(final AvTimeSpan schlafdauer) {
        final AltDescriptionsBuilder alt = alt();

        if (isDefinitivWiederholung()) {
            alt.add(neuerSatz("aber dann fällt dir doch dein Kopf vornüber und du fällst in",
                    "einen tiefen Schlaf"),
                    neuerSatz("wie du so an einen Baumstamm gelehnt dasitzt, fallen dir",
                            "die Augen von selber zu und du schläfst ein"),
                    neuerSatz("dir fällts aber wie Blei auf die Augen und du schläfst ein"),
                    neuerSatz("dir fallen die Augen zu und auf einem Lager von Moos",
                            "schläfst du ein"));
            if (sc.feelingsComp().getHunger() == HUNGRIG) {
                alt.add(du("schläfst", "vor Müdigkeit und Hunger ein")
                        .mitVorfeldSatzglied("vor Müdigkeit und Hunger")
                        .undWartest());
            }
        } else {
            alt.add(du("lehnst",
                    "dich an einen Baumstamm, da fallen dir die Augen von selber "
                            + "zu und du schläfst ein"));
        }

        n.narrateAlt(alt, schlafdauer);
    }

    private void narrateAndDoRapunzelZuhoeren() {
        sc.feelingsComp().requestMoodMin(Mood.GLUECKLICH);

        n.narrateAlt(mins(4), COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE,
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

        n.narrateAlt(mins(3), COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE,
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

        // IDEA "Dann" nicht bei (?) oder nach (?) "statischen Verben" (du hast Glück, du hast
        //  Hunger, du freust dich) verwenden.

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

        n.narrateAlt(alt, mins(10), COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE);
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

    @SuppressWarnings("unchecked")
    @NonNull
    private IHasStateGO<RapunzelState> loadRapunzel() {
        return (IHasStateGO<RapunzelState>) world.load(RAPUNZEL);
    }
}
