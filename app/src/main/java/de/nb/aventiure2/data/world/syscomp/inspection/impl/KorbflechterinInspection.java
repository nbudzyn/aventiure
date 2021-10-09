package de.nb.aventiure2.data.world.syscomp.inspection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ZUFRIEDEN;
import static de.nb.aventiure2.data.world.syscomp.inspection.impl.KorbflechterinInspection.Counter.BEIM_FLECHTEN_BEOBACHTEN;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZUSEHEN;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspectableGO;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspection;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

public class KorbflechterinInspection implements IInspection,
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin {

    private final World world;
    private final CounterDao counterDao;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public enum Counter {
        BEIM_FLECHTEN_BEOBACHTEN
    }

    public KorbflechterinInspection(final World world,
                                    final CounterDao counterDao) {
        this.world = world;
        this.counterDao = counterDao;
    }

    @Override
    public IInspectableGO getInspectable() {
        return world.load(KORBFLECHTERIN);
    }

    @Override
    public String getActionName() {
        return capitalize(joinToString(
                getDescription(KORBFLECHTERIN, false).datK(),
                "bei ihrer Arbeit zusehen"));
    }

    @Override
    public ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions() {
        final AltTimedDescriptionsBuilder alt = altTimed();

        final SubstantivischePhrase anaph = anaph(KORBFLECHTERIN);

        final int countBeimFlechtenBeobachtet = counterDao.get(BEIM_FLECHTEN_BEOBACHTEN);
        if (countBeimFlechtenBeobachtet == 0) {
            if (loadSC().feelingsComp().isFroehlicherAls(Mood.BETRUEBT)) {
                loadSC().feelingsComp().requestMoodMin(Mood.BEWEGT);
            }

            alt.add(neuerSatz(PARAGRAPH,
                    anaph.persPron().nomK(),
                    "arbeitet an einem Binsenkorb", SENTENCE,
                    "du siehst",
                    anaph.persPron().datK(),
                    "genau dabei zu", SENTENCE,
                    anaph.persPron().nomK(),
                    "verdreht immer drei Stränge von Binsen zu einem Seil, das legt",
                    anaph.persPron().nomK(),
                    "dann im Kreis, bis daraus, ganz allmählich,",
                    "ein großer Korb entsteht…", SENTENCE,
                    "so schwer sieht es eigentlich gar nicht aus…", PARAGRAPH)
                    .timed(secs(90)));
        } else {
            alt.add(du(ZUSEHEN
                    .mit(anaph) // der alten Frau
                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("bei der Arbeit")))
                    .dann()
                    .timed(mins(1)));

            if (loadSC().feelingsComp().isFroehlicherAls(ZUFRIEDEN)) {
                alt.add(neuerSatz("es ist eine Freude, der fleißigen Alten zuzusehen!")
                        .phorikKandidat(F, KORBFLECHTERIN)
                        .schonLaenger()
                        .timed(mins(1)));
            }
            if (countBeimFlechtenBeobachtet > 2) {
                // Allählich wird's langweilig
                loadSC().feelingsComp().requestMoodMax(ZUFRIEDEN);

                alt.add(neuerSatz("Eine langwierige Arbeit!")
                        .phorikKandidat(F, KORBFLECHTERIN)
                        .schonLaenger()
                        .timed(mins(1)));
            }
        }

        loadSC().memoryComp().narrateAndUpgradeKnown(BINSEN_FLECHTEN);

        return alt.withCounterIdIncrementedIfTextIsNarrated(BEIM_FLECHTEN_BEOBACHTEN).build();
    }

    @Override
    public World getWorld() {
        return world;
    }
}
