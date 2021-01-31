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
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.string.GermanStringUtil;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WARTEN;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
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
                erwartet.is(RAPUNZELS_ZAUBERIN) &&
                world.loadSC().memoryComp().isKnown(RAPUNZELS_ZAUBERIN) &&
                !erwartet.locationComp().hasSameOuterMostLocationAs(location)) {
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
        return GermanStringUtil.capitalize(
                WARTEN
                        .mit(world.getDescription(erwartet))
                        .getInfinitiv(P2, SG).joinToString(
                )
        );
    }

    @Override
    public void narrateAndDo() {
        if (!sc.memoryComp().getLastAction().is(Action.Type.WARTEN) &&
                !sc.memoryComp().getLastAction().is(Action.Type.RASTEN)) {
            counterDao.reset(COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE);
        }

        // Der SC beginnt zu warten
        narrateWarten();

        if (automatischesEinschlafen()) {
            narrateAndDoSchlafen();
        } else {
            // FIXME: Warten auf die richtige Länge setzen. Wenn das NICHT funktioniert:
            //  - Ab einem Punkt, wo man davon ausgehen kann, dass der Spieler
            //   bewusst wartet oder rastet, um die Frau zu beobachten, sollte die Frau nach 4x
            //   Rasten
            //   oder 4x Warten gekommen
            //  -  (Alternative zum Warten)  mehrere verschiedenen bestätigende Texte, dass sich das
            //  Rasten lohnt (damit der Spieler nicht zu bald aufgibt).

            // Erst einmal vergeht fast keine Zeit. Die ScAutomaticReactionsComp sorgt
            // im onTimePassed() im Zusammenspiel mit der WaitingComp dafür, dass die
            // Zeit vergeht (maximal 3 Stunden).
            world.loadSC().waitingComp().startWaiting(timeTaker.now().plus(hours(3)));
        }
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    @Override
    protected void narrateAndDoEinschlafen(final AvTimeSpan schlafdauer) {
        n.narrateAlt(schlafdauer,
                du(PARAGRAPH, "wirst",
                        "über dem Warten schläfrig und nickst schließlich ein")
                        .mitVorfeldSatzglied("über dem Warten"),
                neuerSatz("da fallen dir die Augen zu, und schon schläfst du fest"),
                du("willst", "wachen und warten, aber dann überkommt dich",
                        "doch der Schlaf")
                        .mitVorfeldSatzglied("wachen und warten"),
                du("fängst", "an, einzuschlafen")
                        .komma().undWartest(),
                du(PARAGRAPH, "machst", "ganz kurz die Augen zu, da bist du",
                        "auch schon eingeschlafen").mitVorfeldSatzglied("ganz kurz"));
    }

    private void narrateWarten() {
        final Kohaerenzrelation kohaerenzrelation = getKohaerenzrelationFuerUmformulierung();

        if (kohaerenzrelation == VERSTEHT_SICH_VON_SELBST) {
            final SubstantivischePhrase anaph = world.anaph(erwartet, false);
            n.narrateAlt(secs(5),
                    COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE,
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
                                    .mitAdverbialerAngabe(
                                            new AdverbialeAngabeSkopusSatz("weiter")
                                    )
                            )
                                    .dann()),
                    secs(5), COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE);
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
