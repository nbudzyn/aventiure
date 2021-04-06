package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.DescriptionUmformulierer;
import de.nb.aventiure2.german.description.Kohaerenzrelation;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WARTEN;
import static de.nb.aventiure2.scaction.impl.AbstractWartenRastenAction.Counter.WARTEN_ODER_RASTEN_IN_FOLGE;

/**
 * Der Spielercharakter wartet (wach!) auf etwas.
 */
public class WartenAction<LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
        extends AbstractWartenRastenAction {
    private final LIVGO erwartet;

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    List<WartenAction<LIVGO>> buildActions(
            final CounterDao counterDao,
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker, final Narrator n, final World world,
            final LIVGO erwartet,
            final ILocationGO location) {
        final ImmutableList.Builder<WartenAction<LIVGO>> res = ImmutableList.builder();
        if (location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME) &&
                counterDao.get(RastenAction.Counter.RASTEN) >= 2 &&
                erwartet.is(RAPUNZELS_ZAUBERIN) &&
                world.loadSC().memoryComp().isKnown(RAPUNZELS_ZAUBERIN) &&
                !erwartet.locationComp().hasSameVisibleOuterMostLocationAs(location)) {
            res.add(new WartenAction<>(counterDao, scActionStepCountDao, timeTaker, n, world,
                    erwartet));
        }

        return res.build();
    }

    @VisibleForTesting
    WartenAction(final CounterDao counterDao,
                 final SCActionStepCountDao scActionStepCountDao,
                 final TimeTaker timeTaker,
                 final Narrator n,
                 final World world,
                 final LIVGO erwartet) {
        super(counterDao, scActionStepCountDao, timeTaker, n, world);
        this.erwartet = erwartet;
    }

    @Override
    @NonNull
    public String getName() {
        // "Auf die magere Frau warten"
        return joinToKonstituentenfolge(
                SENTENCE,
                WARTEN
                        .mit(world.getDescription(erwartet))
                        .getInfinitiv(P2, SG))
                .joinToString();
    }

    @Override
    public void narrateAndDo() {
        if (!sc.memoryComp().getLastAction().is(Action.Type.WARTEN) &&
                !sc.memoryComp().getLastAction().is(Action.Type.RASTEN)) {
            counterDao.reset(WARTEN_ODER_RASTEN_IN_FOLGE);
        }

        // Der SC beginnt zu warten
        narrateWarten();

        if (automatischesEinschlafen()) {
            narrateAndDoSchlafen();
        } else {
            // Erst einmal vergeht fast keine Zeit. Die ScAutomaticReactionsComp sorgt
            // im onTimePassed() im Zusammenspiel mit der WaitingComp dafür, dass die
            // Zeit vergeht (in Summe maximal 3 Stunden).
            world.loadSC().waitingComp().startWaiting(timeTaker.now().plus(hours(3)));
        }
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    @Override
    protected void narrateAndDoEinschlafen(final AvTimeSpan schlafdauer) {
        n.narrateAlt(schlafdauer,
                du(PARAGRAPH, "wirst",
                        "über dem Warten schläfrig und nickst schließlich ein")
                        .mitVorfeldSatzglied("über dem Warten").schonLaenger()
                ,
                neuerSatz("da fallen dir die Augen zu, und schon schläfst du fest"),
                du("willst", "wachen und warten, aber dann überkommt dich",
                        "doch der Schlaf")
                        .schonLaenger()
                        .mitVorfeldSatzglied("wachen und warten"),
                du("fängst", "an, einzuschlafen")
                        .schonLaenger()
                        .komma().undWartest(),
                du(PARAGRAPH, "machst", "ganz kurz die Augen zu, da bist du",
                        "auch schon eingeschlafen").mitVorfeldSatzglied("ganz kurz"));
    }

    private void narrateWarten() {
        final Kohaerenzrelation kohaerenzrelation = getKohaerenzrelationFuerUmformulierung();

        if (kohaerenzrelation == VERSTEHT_SICH_VON_SELBST) {
            final SubstantivischePhrase anaph = world.anaph(erwartet, false);
            n.narrateAlt(secs(5),
                    WARTEN_ODER_RASTEN_IN_FOLGE,
                    du(WARTEN.mit(anaph)).dann(),
                    du("beginnst", "auf", anaph.akkK(), "zu warten").dann());
        } else {
            final SubstantivischePhrase anaph =
                    world.anaph(erwartet, true);
            n.narrateAlt(
                    DescriptionUmformulierer.drueckeAus(
                            kohaerenzrelation,
                            du(WARTEN
                                    .mit(anaph)
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("weiter"))
                            ).schonLaenger()
                                    .dann()),
                    secs(5), WARTEN_ODER_RASTEN_IN_FOLGE);
        }
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
        return false;
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.WARTEN, erwartet);
    }
}
