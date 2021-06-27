package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satzreihe;
import de.nb.aventiure2.german.string.GermanStringUtil;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.collect.ImmutableList.of;
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
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HOEHE;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FALLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LANDEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ROLLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHLAGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.VERSCHWINDEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFFANGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WERFEN;
import static de.nb.aventiure2.scaction.impl.HochwerfenAction.Counter.HOCHWERFEN_ACTION_WIEDERHOLUNG;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Der Spieler(charakter) wirft einen Gegenstand hoch.
 */
@ParametersAreNonnullByDefault
public class HochwerfenAction<OBJ extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        HOCHWERFEN_ACTION_WIEDERHOLUNG
    }

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
        if (gameObject instanceof ILivingBeingGO
                || location.storingPlaceComp().isNiedrig()) {
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

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        return GermanStringUtil.capitalize(world.getDescription(object).akkStr()) + " hochwerfen";
    }

    @Override
    public void narrateAndDo() {
        if (object.locationComp().isVielteilig()) {
            narrateAndDoVielteilig();
        } else if (isDefinitivWiederholung()) {
            narrateAndDoWiederholung();
        } else {
            narrateAndDoErstesMal();
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoVielteilig() {
        final EinzelneSubstantivischePhrase objectDescLong = world.getDescription(object, false);
        final EinzelneSubstantivischePhrase objectDescShort = world.getDescription(object, true);

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altNeueSaetze(
                WERFEN.mit(objectDescLong)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher("in die Höhe"))
                        .alsSatzMitSubjekt(duSc()),
                "–",
                FALLEN.mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher(
                        "überall " +
                                location.storingPlaceComp().getLocationMode()
                                        .getWohin(false)))
                        .alsSatzMitSubjekt(objectDescShort)
        ));

        alt.addAll(altNeueSaetze(
                WERFEN.mit(objectDescLong)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher("in die Höhe"))
                        .alsSatzMitSubjekt(duSc()),
                "–",
                LANDEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                        "überall " +
                                location.storingPlaceComp().getLocationMode()
                                        .getWo(false)))
                        .alsSatzMitSubjekt(objectDescShort)
        ));

        alt.add(new Satzreihe(
                WERFEN.mit(objectDescShort)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher("hoch"))
                        .alsSatzMitSubjekt(duSc()),
                FALLEN.mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher("überallhin"))
                        .alsSatzMitSubjekt(objectDescShort.persPron())));

        n.narrateAlt(alt, secs(10));

        sc.feelingsComp().requestMoodMax(ETWAS_GEKNICKT);

        object.locationComp().narrateAndSetLocation(location);
    }

    private void narrateAndDoErstesMal() {
        final IHasStateGO<FroschprinzState> froschprinz = world.load(FROSCHPRINZ);

        if (location.is(IM_WALD_BEIM_BRUNNEN) &&
                !froschprinz.stateComp().hasState(FroschprinzState.UNAUFFAELLIG)) {
            narrateAndDoFroschBekannt(
                    (IHasStateGO<FroschprinzState> & ILocatableGO) froschprinz);
            return;
        }

        final SubstantivischePhrase anaph = world.anaph(object, false);

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            narrateAndDoHochwerfenAuffangen(
                    satzanschluss(", wirfst",
                            anaph.akkK(),
                            "in die Höhe und fängst",
                            anaph.persPron().akkK(),
                            " wieder auf")
                            .timed(secs(3))
                            .dann());
            return;
        }

        narrateAndDoHochwerfenAuffangen(mapToList(sc.feelingsComp().altAdvAngabenSkopusSatz(),
                a -> du(PARAGRAPH, new ZweiPraedikateOhneLeerstellen(
                        WERFEN.mit(anaph)
                                .mitAdvAngabe(a)
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbWohinWoher(
                                                IN_AKK.mit(HOEHE))),
                        AUFFANGEN.mit(anaph.persPron())
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusSatz(
                                                "wieder"))
                ))
                        .timed(secs(3))
                        .dann()));
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
            final EinzelneSubstantivischePhrase objectDesc = world.getDescription(object);

            narrateAndDoHochwerfenAuffangen(
                    du(PARAGRAPH, "wirfst",
                            objectDesc.akkK(),
                            "hoch in die Luft und fängst",
                            objectDesc.persPron().akkK(),
                            "geschickt wieder auf")
                            .timed(secs(3))
                            .dann());
            return;
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        narrateAndDoObjectFaelltSofortInDenBrunnen();
        if (world.hasSameVisibleOuterMostLocationAsSC(froschprinz)) {
            return;
        }

        sc.feelingsComp().requestMoodMax(ETWAS_GEKNICKT);

        final String praefix =
                location.storingPlaceComp().getLichtverhaeltnisse() == HELL ? "Weit und breit" :
                        "Im Dunkeln ist";

        n.narrate(
                neuerSatz(praefix + " kein Frosch zu sehen… Das war vielleicht etwas "
                        + "ungeschickt, oder?")
                        .timed(NO_TIME));

        // FIXME Wenn das Holz in den Brunnen gefallen ist: Holz regenerieren
        //  (in der ReactionsComp, neuer Status REGENERATED, "hier liegt noch weiteres Holz..."
        //  - wieder auf UNBEKANNT setzen?!
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
        final EinzelneSubstantivischePhrase objectDesc = world.getDescription(object, false);

        n.narrate(
                du(PARAGRAPH, "wirfst",
                        objectDesc.akkK(),
                        "nur ein einziges Mal in die Höhe,",
                        "aber wie das Unglück es will,",
                        FALLEN.getPraesensOhnePartikel(objectDesc.persPron()),
                        objectDesc.persPron().akkK(),
                        "sofort in den Brunnen:",
                        "Platsch! – weg",
                        SeinUtil.istSind(objectDesc.getNumerusGenus()),
                        objectDesc.persPron().akkK(), PARAGRAPH)
                        .mitVorfeldSatzglied("nur ein einziges Mal")
                        .timed(secs(10))
                        .dann(!n.dann()));

        object.locationComp().narrateAndSetLocation(UNTEN_IM_BRUNNEN);
    }

    private void narrateAndDoWiederholung() {
        final IHasStateGO<FroschprinzState> froschprinz = world.load(FROSCHPRINZ);
        final EinzelneSubstantivischePhrase objectDescShort = world.getDescription(object, false);
        final EinzelneSubstantivischePhrase objectDescLong = world.getDescription(object, true);

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
                            "– bei dieser Dunkelheit schon gar nicht" : null;

            n.narrate(du("wirfst",
                    world.getDescription(object).akkK(),
                    "noch einmal in die Höhe… doch o nein,",
                    objectDescShort.nomK(),
                    FALLEN.getPraesensOhnePartikel(objectDescShort),
                    "dir nicht in die Hände, sondern",
                    SCHLAGEN.getPraesensOhnePartikel(objectDescShort),
                    "vorbei",
                    "auf den Brunnenrand und",
                    ROLLEN.getPraesensOhnePartikel(objectDescShort),
                    "geradezu ins Wasser hinein.",
                    "Du folgst",
                    objectDescShort.persPron().datK(),
                    "mit den Augen nach, aber",
                    objectDescLong.nomK(),
                    VERSCHWINDEN.getPraesensOhnePartikel(objectDescLong),
                    ", und der Brunnen ist tief, so tief, dass",
                    "man keinen Grund sieht",
                    dunkelheitNachsatz, PARAGRAPH).mitVorfeldSatzglied("noch einmal")
                    .timed(secs(10)));

            sc.feelingsComp().requestMoodMax(UNTROESTLICH);

            object.locationComp().narrateAndSetLocation(UNTEN_IM_BRUNNEN);
            return;
        }

        n.narrate(du("schleuderst",
                objectDescShort.akkK(),
                "übermütig noch einmal in die Luft, aber ",
                objectDescShort.persPron().akkK(),
                "wieder aufzufangen will dir",
                "dieses Mal nicht gelingen",
                SENTENCE,
                LANDEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                        location.storingPlaceComp().getLocationMode()
                                .getWo(false)))
                        .alsSatzMitSubjekt(world.getDescription(object, true)))
                .mitVorfeldSatzglied("übermütig")
                .timed(secs(5)));

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

    @SuppressWarnings("IfStatementWithIdenticalBranches")
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
