package de.nb.aventiure2.scaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.german.base.InterrogativadverbVerbAllg.WIE;
import static de.nb.aventiure2.german.base.Konstituentenfolge.schliesseInKommaEin;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.INSEKTEN;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ZIRPEN;
import static de.nb.aventiure2.scaction.impl.AbstractWartenRastenAction.Counter.WARTEN_ODER_RASTEN_IN_FOLGE;
import static de.nb.aventiure2.scaction.impl.RastenAction.Counter.RASTEN;
import static de.nb.aventiure2.util.StreamUtil.*;

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
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.Satzreihe;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class RastenAction extends AbstractWartenRastenAction {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        RASTEN
    }

    private final ILocationGO location;

    public static List<RastenAction> buildActions(
            final CounterDao counterDao,
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            @Nullable final ILocationGO location) {
        final ImmutableList.Builder<RastenAction> res = ImmutableList.builder();
        if (location != null
                && location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME, BETT_OBEN_IM_ALTEN_TURM)) {
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
        if (location.is(BETT_OBEN_IM_ALTEN_TURM)) {
            return "Still daliegen";
        }

        return "Rasten";
    }

    @Override
    public void narrateAndDo() {
        counterDao.inc(RASTEN);

        if (!sc.memoryComp().getLastAction().is(Action.Type.WARTEN) &&
                !sc.memoryComp().getLastAction().is(Action.Type.RASTEN)) {
            counterDao.reset(WARTEN_ODER_RASTEN_IN_FOLGE);
        }

        if (automatischesEinschlafen()) {
            narrateAndDoSchlafen();
        } else {
            if (location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                narrateAndDoSchattenDerBaeume();
            } else {
                narrateAndDoObenImTurmUnterBett();
            }
        }
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoObenImTurmUnterBett() {
        sc.feelingsComp().requestMoodMax(Mood.ANGESPANNT);

        final AltDescriptionsBuilder alt = alt();

        alt.add(du(SENTENCE, "liegst",
                "lange Zeit ganz still")
                        .schonLaenger()
                        .dann(),
                neuerSatz("der Staub kribbelt in deiner Nase")
                        .schonLaenger(),
                du("scheinst", "dich gut versteckt zu haben")
                        .schonLaenger(),
                neuerSatz("nur weiter schön still dagelegen!").schonLaenger());

        n.narrateAlt(alt, mins(10), WARTEN_ODER_RASTEN_IN_FOLGE);
    }

    private void narrateAndDoSchattenDerBaeume() {
        if (isDefinitivFortsetzung()
                && loadRapunzel().stateComp().hasState(SINGEND)) {
            narrateAndDoRapunzelZuhoeren();
        } else if (location.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL) {
            narrateAndDoSchattenDerBaeumeDunkel();
        } else {
            narrateAndDoSchattenDerBaeumeHell();
        }
    }

    @Override
    protected void narrateAndDoEinschlafen(final AvTimeSpan schlafdauer) {
        final AltDescriptionsBuilder alt = alt();

        if (isDefinitivFortsetzung()) {
            if (!location.is(BETT_OBEN_IM_ALTEN_TURM)) {
                alt.add(neuerSatz("aber dann fällt dir doch dein Kopf vornüber und du fällst in",
                        "einen tiefen Schlaf"));
            }
            if (location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                alt.add(neuerSatz("wie du so an einen Baumstamm gelehnt dasitzt, fallen dir",
                        "die Augen von selber zu und du schläfst ein"),
                        neuerSatz("dir fallen die Augen zu und auf einem Lager von Moos",
                                "schläfst du ein"));
            }
            alt.add(neuerSatz("dir fällts aber wie Blei auf die Augen und du schläfst ein"),
                    neuerSatz("dir fallen die Augen zu und du schläfst ein"));
            if (sc.feelingsComp().getHunger() == HUNGRIG) {
                alt.add(du("schläfst", "vor Müdigkeit und Hunger ein")
                        .mitVorfeldSatzglied("vor Müdigkeit und Hunger")
                        .undWartest());
            }
        } else {
            if (location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
                alt.add(du("lehnst",
                        "dich an einen Baumstamm, da fallen dir die Augen von selber "
                                + "zu und du schläfst ein"));
            }
            if (location.is(BETT_OBEN_IM_ALTEN_TURM)) {
                alt.add(neuerSatz("kaum liegst du im Dunkel unter dem Bett, da fallen dir",
                        "die Augen von selber zu und du schläfst ein"));
            }
            alt.addAll(altNeueSaetze(sc.feelingsComp().altMuedigkeitAdjPhr().stream()
                            .map(a -> a.getPraedikativ(World.duSc())), // müde
                    "wie du bist schläfst du sofort ein"));
        }

        n.narrateAlt(alt, schlafdauer);
    }

    private void narrateAndDoRapunzelZuhoeren() {
        sc.feelingsComp().requestMoodMin(Mood.GLUECKLICH);

        n.narrateAlt(mins(4), WARTEN_ODER_RASTEN_IN_FOLGE,
                du("bist", "ganz still")
                        .schonLaenger()
                        .undWartest()
                        .dann(),
                du("genießt deine Rast")
                        .schonLaenger()
                        .undWartest()
                        .dann(),
                du(SENTENCE, "sitzt", "glücklich da und genießt")
                        .mitVorfeldSatzglied("glücklich").schonLaenger()
                ,
                neuerSatz("Dein Herz wird ganz warm von dem Gesang"));

        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);
    }

    private void narrateAndDoSchattenDerBaeumeDunkel() {
        sc.feelingsComp().requestMoodMax(Mood.VERUNSICHERT);

        final Windstaerke windstaerke =
                loadWetter().wetterComp().getWindstaerkeAmOrtDesSc();

        final AltDescriptionsBuilder alt = alt();

        // IDEA Waldgeräusche bei Wind:
        //  - expletives es: "es knittert und rauscht"
        //  - echtes Subjekt: "die Bäume rauschen (in der Dunkelheit)"
        //  - "das Rauschen in den Ästen über dir"

        alt.add(neuerSatz("Es ist dunkel und ungemütlich. Krabbelt da etwas auf "
                + "deinem rechten Bein? Du schlägst mit der Hand zu, kannst aber "
                + "nichts erkennen"));

        if (windstaerke.compareTo(Windstaerke.LUEFTCHEN) >= 0) {
            alt.add(neuerSatz("Die Bäume rauschen in",
                    "der Dunkelheit, die Eulen schnarren,",
                    "und es fängt an, dir angst zu werden"),
                    neuerSatz("In den Ästen über dir knittert und rauscht es. Dich friert"));
        } else {
            alt.add(neuerSatz("In der Stille des Waldes erschrickst du bei jedem Knacken,",
                    "jedem unbestimmbaren Laut aufs Neue"));
        }

        n.narrateAlt(alt.schonLaenger(), mins(3), WARTEN_ODER_RASTEN_IN_FOLGE);
    }

    private void narrateAndDoSchattenDerBaeumeHell() {
        sc.feelingsComp().requestMoodMin(Mood.ZUFRIEDEN);

        // IDEA "Dann" maximal dann verwenden, wenn der es einen Aktor gibt und der Aktor im letzten
        //  SemSatz gleich war. (Nach dieser Logik kann man "dann" auch für Beschreibungen in
        //  der dritten Person verwenden!)

        final Windstaerke windstaerke =
                loadWetter().wetterComp().getWindstaerkeAmOrtDesSc();

        final AltDescriptionsBuilder alt = alt();

        alt.add(
                du(SENTENCE, "hältst",
                        "verborgen unter den Bäumen noch eine Zeitlang Rast")
                        .schonLaenger()
                        .mitVorfeldSatzglied("verborgen unter den Bäumen")
                        .dann(),
                neuerSatz("Es tut gut, eine Weile zu rasten. Über dir zwitschern die "
                        + "Vögel und die Grillen zirpen")
                        .dann());
        if (windstaerke.compareTo(Windstaerke.LUEFTCHEN) >= 0) {
            alt.add(
                    du(SENTENCE, "streckst",
                            "die Glieder und hörst auf das Rauschen "
                                    + "in den "
                                    + "Ästen über dir. Ein Rabe setzt "
                                    + "sich neben dich und fliegt nach einer Weile wieder fort")
                            .schonLaenger()
                            .dann());
            alt.addAll(mapToSet(
                    world.loadWetter().wetterComp()
                            .altSpWindgeraeuscheSaetze(isUnterOffenemHimmel()),
                    derWindSaust ->
                            du(SENTENCE, "ruhst",
                                    "noch eine Weile aus und lauschst",
                                    schliesseInKommaEin(
                                            new Satzreihe(
                                                    ZIRPEN.alsSatzMitSubjekt(INSEKTEN)
                                                            .mitAdvAngabe(WIE),
                                                    derWindSaust.mitAdvAngabe(WIE))
                                                    .getIndirekteFrage()))
                                    .schonLaenger()
                                    .mitVorfeldSatzglied("eine Weile")
                                    .dann()));
        }

        if (world.loadSC().feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH) {
            neuerSatz("Deine müden Glieder brauchen Erholung. Du bist ganz "
                    + "still und die Vögel setzen sich "
                    + "auf die Äste über dir "
                    + "und singen, was sie nur wissen")
                    .komma()
                    .schonLaenger()
                    .dann();
        }

        n.narrateAlt(alt, mins(10), WARTEN_ODER_RASTEN_IN_FOLGE);
    }

    private boolean isUnterOffenemHimmel() {
        return location.storingPlaceComp().getDrinnenDraussen() ==
                DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
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
        // Rastet der Spieler weiter, obwohl andere Dinge passiert sind?
        return isDefinitivFortsetzung() && n.lastNarrationWasFromReaction();
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.RASTEN);
    }

    @NonNull
    private IHasStateGO<RapunzelState> loadRapunzel() {
        return loadRequired(RAPUNZEL);
    }
}
