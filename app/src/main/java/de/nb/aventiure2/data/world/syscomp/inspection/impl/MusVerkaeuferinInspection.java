package de.nb.aventiure2.data.world.syscomp.inspection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.ALLES_ERLAUBT;
import static de.nb.aventiure2.data.world.syscomp.inspection.impl.MusVerkaeuferinInspection.Counter.BEOBACHTEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SUESS;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.EINIGE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FLIEGEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MUS;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.IntentionalesVerb.VERSUCHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANLOCKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERSCHEUCHEN;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspectableGO;
import de.nb.aventiure2.data.world.syscomp.inspection.IInspection;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.praedikat.VerbSubjWoertlicheRede;
import de.nb.aventiure2.german.string.GermanStringUtil;

/**
 * Entspricht einer {@link de.nb.aventiure2.scaction.impl.UntersuchenAction},
 * die der Benutzer an der Mus-Verkäuferin durchführen kann.
 */
public class MusVerkaeuferinInspection implements IInspection,
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin {
    private final World world;
    private final CounterDao counterDao;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public enum Counter {
        BEOBACHTEN
    }

    public MusVerkaeuferinInspection(final World world,
                                     final CounterDao counterDao) {
        this.world = world;
        this.counterDao = counterDao;
    }

    @Override
    public IInspectableGO getInspectable() {
        return world.load(MUS_VERKAEUFERIN);
    }

    @Override
    public String getActionName() {
        return GermanStringUtil.capitalize(
                VerbSubjObj.BEOBACHTEN.mit(getDescription())
                        .getInfinitiv(duSc().getPerson(), nachAnschlusswort, duSc().getNumerus())
                        .joinToString());
    }

    @Override
    public ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions() {
        final AltTimedDescriptionsBuilder alt = altTimed();

        final EinzelneSubstantivischePhrase desc = getDescription(
                ALLES_ERLAUBT,
                true);
        final SubstantivischePhrase anaph = anaph();

        final int countBeobachtet = counterDao.get(BEOBACHTEN);

        if (countBeobachtet == 1) {
            alt.add(neuerSatz("Ein Käufer kommt, besieht sich alles genau, hält lange die",
                    "Nase",
                    AN_AKK.mit(np(desc.possArt(), MUS)), SENTENCE,
                    "Dann lässt er sich",
                    VON.getDescription(desc),
                    "vier Lot Mus abwiegen,",
                    "„wenns auch ein Viertelpfund ist,",
                    "kommt es mir nicht darauf an.“", SENTENCE,
                    "Die Frau wiegt das Mus ab und wirkt etwas ärgerlich und brummig",
                    PARAGRAPH)
                    .timed(secs(90)));
        } else if (countBeobachtet == 2) {
            alt.add(neuerSatz(
                    // "Das süße Mus hat einige Fliegen angelockt"
                    ANLOCKEN.mit(np(EINIGE, FLIEGEN))
                            .alsSatzMitSubjekt(MUS.mit(SUESS))
                            .perfekt())
                    .timed(secs(20)));
        } else if (countBeobachtet > 2 && countBeobachtet % 3 == 0) {
            alt.add(neuerSatz(
                    // "Mit einem Lappen versucht die Bäuerin die Fliegen zu verscheuchen"
                    VERSUCHEN.mitLexikalischemKern(VERSCHEUCHEN.mit(FLIEGEN))
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("mit einem Lappen"))
                            .alsSatzMitSubjekt(anaph))
                    .timed(secs(15)));
        } else {
            alt.add(neuerSatz(
                    "„Mus feil!“, ruft",
                    anaph.nomK(),
                    ", „gut Mus feil!“")
                            .timed(secs(10)),
                    neuerSatz(VerbSubjWoertlicheRede.RUFEN
                            .mitWoertlicheRede("Mus feil!")
                            .alsSatzMitSubjekt(anaph))
                            .timed(secs(10)));

            world.narrateAndDoReactions().onRuf(getInspectable().getId(), Ruftyp.MUS_FEIL);
        }

        return alt.withCounterIdIncrementedIfTextIsNarrated(BEOBACHTEN).build();
    }

    @Override
    public World getWorld() {
        return world;
    }
}
