package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;
import static de.nb.aventiure2.german.base.Nominalphrase.HOEHE;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFFANGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WERFEN;

/**
 * Der Spieler(charakter) wirft einen Gegenstand hoch.
 */
@ParametersAreNonnullByDefault
public class HochwerfenAction<OBJ extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    private static final String HOCHWERFEN_ACTION_WIEDERHOLUNG = "HochwerfenAction_Wiederholung";
    private final CounterDao counterDao;
    @NonNull
    private final OBJ object;

    private final ILocationGO location;

    public static <OBJ extends IDescribableGO & ILocatableGO>
    Collection<HochwerfenAction<OBJ>> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final CounterDao counterDao,
            final Narrator n, final World world,
            final ILocationGO location, @NonNull final OBJ gameObject) {
        if (gameObject instanceof ILivingBeingGO) {
            return of();
        }

        return of(new HochwerfenAction<>(scActionStepCountDao, timeTaker, counterDao,
                n, world, gameObject, location));
    }

    private HochwerfenAction(final SCActionStepCountDao scActionStepCountDao,
                             final TimeTaker timeTaker,
                             final CounterDao counterDao,
                             final Narrator n,
                             final World world,
                             @NonNull final OBJ object,
                             final ILocationGO location) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.counterDao = counterDao;
        this.object = object;
        this.location = location;
    }

    @Override
    public String getType() {
        return "actionHochwerfen";
    }

    @Override
    @NonNull
    public String getName() {
        return GermanUtil.capitalize(world.getDescription(object).akkStr()) + " hochwerfen";
    }

    @Override
    public void narrateAndDo() {
        if (isDefinitivWiederholung()) {
            narrateAndDoWiederholung();
        } else {
            narrateAndDoErstesMal();
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoErstesMal() {
        final IHasStateGO<FroschprinzState> froschprinz =
                (IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ);

        if (location.is(IM_WALD_BEIM_BRUNNEN) &&
                !froschprinz.stateComp().hasState(FroschprinzState.UNAUFFAELLIG)) {
            narrateAndDoFroschBekannt(
                    (IHasStateGO<FroschprinzState> & ILocatableGO) froschprinz);
            return;
        }

        final SubstantivischePhrase anaph = world.anaph(object, false);

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            narrateAndDoHochwerfenAuffangen(
                    satzanschluss(", wirfst " +
                            anaph.akkStr() +
                            " in die Höhe und fängst " +
                            anaph.persPron().akkStr() +
                            " wieder auf", secs(3))
                            .dann());
            return;
        }

        narrateAndDoHochwerfenAuffangen(sc.feelingsComp().altAdverbialeAngabenSkopusSatz().stream()
                .map(a ->
                        du(PARAGRAPH,
                                new ZweiPraedikateOhneLeerstellen(
                                        WERFEN.mit(anaph)
                                                .mitAdverbialerAngabe(a)
                                                .mitAdverbialerAngabe(
                                                        new AdverbialeAngabeSkopusVerbWohinWoher(
                                                                IN_AKK.mit(HOEHE))),
                                        AUFFANGEN.mit(anaph.persPron())
                                                .mitAdverbialerAngabe(
                                                        new AdverbialeAngabeSkopusSatz(
                                                                "wieder"))
                                ), secs(3))
                                .dann())
                .collect(toImmutableList()));
    }

    private <F extends IHasStateGO<FroschprinzState> & ILocatableGO> void
    narrateAndDoFroschBekannt(final F froschprinz) {
        if (froschprinz.stateComp().hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {

            narrateAndDoObjectFaelltSofortInDenBrunnen();
            return;
            // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
            // lassen, obwohl er noch mit dem Frosch verhandelt.
        }

        // Der Frosch ist nicht mehr in Stimmung, Dinge aus dem Brunnen zu holen.
        if (object.is(GOLDENE_KUGEL)) {
            final Nominalphrase objectDesc = world.getDescription(object);

            narrateAndDoHochwerfenAuffangen(
                    du(PARAGRAPH, "wirfst", objectDesc.akkStr() +
                            " hoch in die Luft und fängst " +
                            objectDesc.persPron().akkStr() +
                            " geschickt wieder auf", secs(3))
                            .dann());
            return;
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        narrateAndDoObjectFaelltSofortInDenBrunnen();
        if (world.hasSameOuterMostLocationAsSC(froschprinz)) {
            return;
        }

        sc.feelingsComp().requestMoodMax(ETWAS_GEKNICKT);

        final String praefix =
                location.storingPlaceComp().getLichtverhaeltnisse() == HELL ? "Weit und breit" :
                        "Im Dunkeln ist";

        n.narrate(
                neuerSatz(praefix + " kein Frosch zu sehen… Das war vielleicht etwas "
                        + "ungeschickt, oder?", NO_TIME));
    }

    private void narrateAndDoHochwerfenAuffangen(final TimedDescription<?> desc) {
        narrateAndDoHochwerfenAuffangen(ImmutableList.of(desc));
    }

    private void narrateAndDoHochwerfenAuffangen(final ImmutableList<TimedDescription<?>> alt) {
        n.narrateAlt(alt);

        object.locationComp().narrateAndDoLeaveReactions(SPIELER_CHARAKTER);

        world.narrateAndDoReactions()
                // Hier wird das onLeave() und onEnter() etwas missbraucht, um Reaktionen auf
                // das Hochwerfen zu provozieren. Da from und to gleich sind, müssen wir
                // from explizit angebeben, es darf nicht die lastLocation verwendet werden,
                // wie es sonst automatisch passiert.
                .onEnter(object,
                        // from
                        world.loadSC(),
                        // to
                        SPIELER_CHARAKTER);
    }

    private void narrateAndDoObjectFaelltSofortInDenBrunnen() {
        final Nominalphrase objectDesc = world.getDescription(object, false);

        n.narrate(
                du(PARAGRAPH, "wirfst", objectDesc.akkStr() +
                        " nur ein einziges Mal in die Höhe, " +
                        "aber wie das Unglück es will, fällt " +
                        objectDesc.persPron().akkStr() +
                        " sofort in den Brunnen: " +
                        "Platsch! – weg " +
                        SeinUtil.istSind(objectDesc.getNumerusGenus()) +
                        " " +
                        objectDesc.persPron().akkStr(), "nur ein einziges Mal", secs(10))
                        .dann(!n.dann())
                        .beendet(PARAGRAPH));

        object.locationComp().narrateAndSetLocation(UNTEN_IM_BRUNNEN);
    }

    private void narrateAndDoWiederholung() {
        final IHasStateGO<FroschprinzState> froschprinz =
                (IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ);

        if (counterDao.get(HOCHWERFEN_ACTION_WIEDERHOLUNG) == 0 ||
                (location.is(IM_WALD_BEIM_BRUNNEN) && !froschprinz.stateComp()
                        .hasState(UNAUFFAELLIG))) {
            n.narrateAlt(secs(3), HOCHWERFEN_ACTION_WIEDERHOLUNG,
                    neuerSatz("Und noch einmal – was ein schönes Spiel!")
                            .dann(),
                    neuerSatz("So ein Spaß!")
                            .dann(),
                    neuerSatz("Und in die Höhe damit – juchei!")
                            .dann());
            return;
        }

        if (location.is(IM_WALD_BEIM_BRUNNEN)) {
            final String dunkelheitNachsatz =
                    location.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL ?
                            "– bei dieser Dunkelheit schon gar nicht" : "";

            n.narrate(du("wirfst",
                    world.getDescription(object).akkStr() +
                            " noch einmal in die Höhe… doch oh nein, " +
                            world.getDescription(object, true).nomStr() +
                            " fällt dir nicht in die Hände, sondern schlägt vorbei " +
                            "auf den Brunnenrand und rollt geradezu ins Wasser hinein." +
                            " Du folgst ihr mit den Augen nach, aber " +
                            world.getDescription(object, true).nomStr() +
                            " verschwindet, und der Brunnen ist tief, so tief, dass " +
                            "man keinen Grund sieht"
                            + dunkelheitNachsatz,
                    "noch einmal", secs(10))
                    .beendet(PARAGRAPH));

            sc.feelingsComp().requestMoodMax(UNTROESTLICH);

            object.locationComp().narrateAndSetLocation(UNTEN_IM_BRUNNEN);
            return;
        }

        n.narrate(du("schleuderst",
                world.getDescription(object).akkStr() +
                        " übermütig noch einmal in die Luft, aber sie wieder aufzufangen will dir "
                        +
                        "dieses Mal nicht gelingen. "
                        + GermanUtil.capitalize(
                        world.getDescription(object, true).nomStr()) +
                        " landet " +
                        location.storingPlaceComp().getLocationMode().getWo(false),
                "übermütig",
                secs(5)));

        sc.feelingsComp().requestMoodMax(ETWAS_GEKNICKT);

        object.locationComp().narrateAndSetLocation(location);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        if (n.lastNarrationWasFromReaction()) {
            return false;
        }

        return false;
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.HOCHWERFEN, object);
    }
}
