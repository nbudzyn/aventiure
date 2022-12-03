package de.nb.aventiure2.data.world.syscomp.inspection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.ALLES_ERLAUBT;
import static de.nb.aventiure2.data.world.syscomp.inspection.impl.TopfVerkaeuferinInspection.Counter.BEOBACHTEN;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HINSTARREN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STARREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANGLOTZEN;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspectableGO;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspection;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

/**
 * Entspricht einer {@link de.nb.aventiure2.scaction.impl.UntersuchenAction},
 * die der Benutzer an der Topf-Verkäuferin durchführen kann.
 */
public class TopfVerkaeuferinInspection implements IInspection,
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin {
    private final TimeTaker timeTaker;
    private final World world;
    private final CounterDao counterDao;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public enum Counter {
        BEOBACHTEN
    }

    public TopfVerkaeuferinInspection(final TimeTaker timeTaker,
                                      final World world,
                                      final CounterDao counterDao) {
        this.timeTaker = timeTaker;
        this.world = world;
        this.counterDao = counterDao;
    }

    @Override
    public IInspectableGO getInspectable() {
        return world.load(TOPF_VERKAEUFERIN);
    }

    @Override
    public String getActionName() {
        return "Der schönen jungen Frau mit den Töpfen zusehen";
    }

    @Override
    public ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions() {
        final AltTimedDescriptionsBuilder alt = altTimed();

        final EinzelneSubstantivischePhrase desc = getDescription(
                ALLES_ERLAUBT, false);
        final SubstantivischePhrase anaph = anaph();

        final int countBeobachtet = counterDao.get(BEOBACHTEN);
        if (countBeobachtet == 0) {
            final AvTimeSpan inspectionTime = mins(1);

            alt.addAll(altNeueSaetze(PARAGRAPH,
                    desc.nomK(),
                    ImmutableList.of("sortiert", "sortiert gerade"),
                    desc.possArt().vor(N).akkStr(),
                    "Geschirr neu", SENTENCE,
                    desc.persPron().nomK(),
                    "nimmt einzelne Töpfe in",
                    desc.possArt().vor(PL_MFN).akkStr(),
                    "Hände und stellt sie so hin, dass sie",
                    loadWetter().wetterComp().altLichtInDemEtwasLiegt(
                            timeTaker.now().plus(inspectionTime), true).stream()
                            .map(IN_DAT::mit),
                    // "im Abendlicht"
                    "besser zur Geltung kommen", SENTENCE,
                    "dir fällt auf, was für feine Hände",
                    desc.persPron().nomK(),
                    "hat")
                    .schonLaenger()
                    .timed(inspectionTime));
        } else if (countBeobachtet == 1) {
            alt.add(du("schaust",
                    ZU.mit(desc), SENTENCE,
                    "mit",
                    desc.possArt().vor(N).datStr(), // "ihrem"
                    "ernsthaften, edlen Gesicht hebt",
                    desc.persPron().nomK(),
                    "sich von den anderen Marktweibern ab")
                    .dann()
                    .timed(secs(30)));
        } else {
            alt.add(neuerSatz(
                    "ob",
                    anaph.nomK(),
                    "wohl etwas traurig dreinblick? Oder nachdenklich?")
                    .timed(secs(10)));

            if (countBeobachtet > 2) {
                alt.add(du(HINSTARREN
                        .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher(
                                ZU.mit(anaph))))
                        .dann()
                        .timed(secs(15)));
                alt.add(du(STARREN
                        .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher(
                                ZU.mit(anaph))))
                        .dann()
                        .timed(secs(15)));
                alt.add(du(ANGLOTZEN.mit(anaph))
                        .dann()
                        .timed(secs(15)));
            }
        }

        return alt.withCounterIdIncrementedIfTextIsNarrated(BEOBACHTEN).build();
    }

    @Override
    public World getWorld() {
        return world;
    }
}
