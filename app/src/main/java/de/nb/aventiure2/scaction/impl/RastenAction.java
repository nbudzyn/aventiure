package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class RastenAction extends AbstractScAction {
    private final ILocationGO location;

    public static Collection<RastenAction> buildActions(
            final AvDatabase db,
            final World world,
            @Nullable final ILocationGO location) {
        final ImmutableList.Builder<RastenAction> res = ImmutableList.builder();
        if (location != null && location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            res.add(new RastenAction(db, world, location));
        }

        return res.build();
    }

    private RastenAction(final AvDatabase db,
                         final World world,
                         final ILocationGO location) {
        super(db, world);
        this.location = location;
    }

    @Override
    public String getType() {
        return "actionRasten";
    }

    @Override
    @NonNull
    public String getName() {
        return "Rasten";
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        final AvTimeSpan timeElapsed;

        if (isDefinitivWiederholung() &&
                ((IHasStateGO) world.load(RAPUNZEL)).stateComp()
                        .hasState(SINGEND)) {
            timeElapsed = narrateAndDoRapunzelZuhoeren();
        } else if (location.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL) {
            timeElapsed = narrateAndDoDunkel();
        } else {
            timeElapsed = narrateAndDoHell();
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoRapunzelZuhoeren() {
        sc.feelingsComp().setMoodMin(Mood.GLUECKLICH);

        return n.addAlt(
                du("bist", "ganz still",
                        mins(4))
                        .undWartest()
                        .dann(),
                du("genießt deine Rast",
                        mins(4))
                        .undWartest()
                        .dann(),
                du(SENTENCE, "sitzt", "glücklich da und genießt",
                        "glücklich",
                        mins(4))
                        .beendet(SENTENCE),
                neuerSatz("Dein Herz wird ganz warm von dem Gesang", mins(4))
                        .beendet(SENTENCE));
    }

    private AvTimeSpan narrateAndDoDunkel() {
        sc.feelingsComp().setMoodMax(Mood.VERUNSICHERT);

        return n.addAlt(
                neuerSatz("Die Bäume rauschen in "
                        + "der Dunkelheit, die Eulen schnarren, und "
                        + "und es fängt an, dir angst zu werden", mins(3))
                        .beendet(SENTENCE),
                neuerSatz("Es ist dunkel und ungemütlich. Krabbelt da etwas auf "
                                + "deinem rechten Bein? Du schlägst mit der Hand zu, kannst aber nichts erkennen",
                        mins(3))
                        .beendet(SENTENCE),
                neuerSatz("In den Ästen über dir knittert und rauscht es. Dich friert",
                        mins(3)));
    }

    private AvTimeSpan narrateAndDoHell() {
        sc.feelingsComp().setMoodMin(Mood.ZUFRIEDEN);

        // STORY Hier ist sehr auffällig, dass die dann()-Logik nicht stimmt:
        //  Ob "Dann..." sinnvoll ist, hängt wesentlich (auch) vom Folgesatz ab.
        //  Rast -> Rast -> Rast: Kein "Dann..."
        //  Rast -> Aufstehen: "Dann..."
        //  Anscheinend setzt "Dann..." eine Art "Aktionsänderung" voraus.

        // TODO "Dann" nicht bei statischen Verben (du hast Glück, du hast Hunger) verwenden

        // TODO "Dann" nur verwenden, wenn der es einen Aktor gibt und der Aktor im letzten
        //  Satz gleich war. (Nach der Logik kann man dann auch für Beschreibungen in
        //  der dritten Person verwenden!)

        return n.addAlt(
                du(SENTENCE, "hältst",
                        "verborgen unter den Bäumen noch eine Zeitlang Rast",
                        "verborgen unter den Bäumen",
                        mins(10))
                        .beendet(SENTENCE)
                        .dann(),
                neuerSatz("Es tut gut, eine Weile zu rasten. Über dir zwitschern die "
                                + "Vögel und die Grillen zirpen",
                        mins(10))
                        .beendet(SENTENCE)
                        .dann(),
                du(SENTENCE, "streckst", "die Glieder und hörst auf das Rauschen "
                                + "in den "
                                + "Ästen über dir. Ein Rabe setzt "
                                + "sich neben dich und fliegt nach einer Weile wieder fort",
                        mins(10))
                        .beendet(SENTENCE)
                        .dann(),
                du(SENTENCE, "ruhst", "noch eine Weile aus und lauschst, wie die "
                                + "Insekten "
                                + "zirpen und der Wind saust",
                        "eine Weile",
                        mins(10))
                        .beendet(SENTENCE)
                        .dann(),
                neuerSatz("Deine müden Glieder brauchen Erholung. Du bist ganz "
                        + "still und die Vögel setzen sich "
                        + "auf die Äste über dir "
                        + "und singen, was sie nur wissen", mins(10))
                        .beendet(SENTENCE)
                        .dann());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        if (n.lastNarrationWasFromReaction()) {
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
