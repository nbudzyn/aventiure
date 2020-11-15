package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class RastenAction extends AbstractScAction {
    private final ILocationGO location;

    public static Collection<RastenAction> buildActions(
            final AvDatabase db,
            final Narrator n, final World world,
            @Nullable final ILocationGO location) {
        final ImmutableList.Builder<RastenAction> res = ImmutableList.builder();
        if (location != null && location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            res.add(new RastenAction(db, n, world, location));
        }

        return res.build();
    }

    private RastenAction(final AvDatabase db,
                         final Narrator n,
                         final World world,
                         final ILocationGO location) {
        super(db, n, world);
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
    public void narrateAndDo() {
        // FIXME "Auf die magere Frau warten" als Aktion?
        //  - Nur unter gewissen Umständen?
        //  - Oder als allgemeines Konzept?

        // FIXME Ab einem Punkt, wo man davon ausgehen kann, dass der Spieler
        //  bewusst rastet, um die Frau zu beobachten, sollte die Frau nach 4x Rasten gekommen
        //  sein.
        //  - Die Frau könnte umkehren.
        //  - Oder die Rast-Zeit wird entsprechend angepasst.

        //  FIXME Mehrere verschiedenen bestätigende Texte, dass sich das Rasten lohnt
        //   (damit der Spieler nicht zu bald aufgibt).
        if (isDefinitivWiederholung() &&
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
                du(SENTENCE, "sitzt", "glücklich da und genießt",
                        "glücklich")
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
                        + "deinem rechten Bein? Du schlägst mit der Hand zu, kannst aber nichts erkennen")
                        .beendet(SENTENCE),
                neuerSatz("In den Ästen über dir knittert und rauscht es. Dich friert"));
    }

    private void narrateAndDoHell() {
        sc.feelingsComp().requestMoodMin(Mood.ZUFRIEDEN);

        // STORY Hier ist sehr auffällig, dass die dann()-Logik nicht stimmt:
        //  Ob "Dann..." sinnvoll ist, hängt wesentlich (auch) vom Folgesatz ab.
        //  Rast -> Rast -> Rast: Kein "Dann..."
        //  Rast -> Aufstehen: "Dann..."
        //  Anscheinend setzt "Dann..." eine Art "Aktionsänderung" voraus.

        // TODO "Dann" nicht bei statischen Verben (du hast Glück, du hast Hunger) verwenden

        // TODO "Dann" nur verwenden, wenn der es einen Aktor gibt und der Aktor im letzten
        //  Satz gleich war. (Nach der Logik kann man dann auch für Beschreibungen in
        //  der dritten Person verwenden!)

        n.narrateAlt(mins(10),
                du(SENTENCE, "hältst",
                        "verborgen unter den Bäumen noch eine Zeitlang Rast",
                        "verborgen unter den Bäumen")
                        .beendet(SENTENCE)
                        .dann(),
                neuerSatz("Es tut gut, eine Weile zu rasten. Über dir zwitschern die "
                        + "Vögel und die Grillen zirpen")
                        .beendet(SENTENCE)
                        .dann(),
                du(SENTENCE, "streckst", "die Glieder und hörst auf das Rauschen "
                                + "in den "
                                + "Ästen über dir. Ein Rabe setzt "
                                + "sich neben dich und fliegt nach einer Weile wieder fort"
                        //    STORY Rabe mit Märchenbezug?
                )
                        .beendet(SENTENCE)
                        .dann(),
                du(SENTENCE, "ruhst", "noch eine Weile aus und lauschst, wie die "
                                + "Insekten "
                                + "zirpen und der Wind saust",
                        "eine Weile"
                )
                        .beendet(SENTENCE)
                        .dann(),
                // FIXME Nur, wenn man ausreichend müde (erschöpft) ist
                neuerSatz("Deine müden Glieder brauchen Erholung. Du bist ganz "
                        + "still und die Vögel setzen sich "
                        + "auf die Äste über dir "
                        + "und singen, was sie nur wissen")
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
