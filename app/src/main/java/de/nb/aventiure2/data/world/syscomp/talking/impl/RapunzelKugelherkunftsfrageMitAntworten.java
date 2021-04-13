package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractFrageMitAntworten;
import de.nb.aventiure2.data.world.syscomp.talking.IScBegruessable;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ZUFRIEDEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_HERKUNFT_DER_GOLDENEN_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.NORMAL;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelKugelherkunftsfrageMitAntworten.Counter.FRAGE_BEANTWORTET;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLEIN;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NOTLUEGE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WAHRHEIT;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.SICH_ERLAUBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANGEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SAGEN;

class RapunzelKugelherkunftsfrageMitAntworten extends AbstractFrageMitAntworten {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        FRAGE_BEANTWORTET
    }

    RapunzelKugelherkunftsfrageMitAntworten(final CounterDao counterDao,
                                            final Narrator n, final World world,
                                            final RapunzelStateComp stateComp,
                                            final FeelingsComp feelingsComp,
                                            final IScBegruessable begruesstMitScSetter) {
        super(RAPUNZEL, counterDao, n, world, stateComp, feelingsComp, begruesstMitScSetter);
    }

    @Override
    public void nscStelltFrage() {
        final SubstantivischePhrase anaph = anaph();

        n.narrate(neuerSatz(
                anaph.possArt().vor(M).nomStr(), // "Ihr"
                "Blick fällt auf",
                world.getDescription(GOLDENE_KUGEL, true).akkK(),
                // FIXME Eigabe Anführungszeichen, Gedankenstrich, Auslassungspunkte erleichern?
                "„Woher hast du die?“, fragt",
                anaph.nomK(),
                "dich")
                .timed(secs(15)));

        stateComp.narrateAndSetState(HAT_NACH_HERKUNFT_DER_GOLDENEN_KUGEL_GEFRAGT);
    }

    @Override
    public Iterable<SCTalkAction> getAntwortActions() {

        return ImmutableList.of(
                st(SICH_ERLAUBEN.mit(Nominalphrase.np(INDEF, KLEIN, NOTLUEGE)),
                        this::notluege),
                st(ANGEBEN,
                        this::aufschneiden),
                st(SAGEN.mit(WAHRHEIT),
                        this::dieWahrheitSagen));
    }

    private void notluege() {
        n.narrate(neuerSatz("„Ach das war ein Geschenk“, sagst du",
                SENTENCE,
                anaph().nomK(),
                "runzelt die Stirn")
                .timed(secs(15))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));
        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                -0.35f, FeelingIntensity.MERKLICH);

        stateComp.narrateAndSetState(NORMAL);

        scBegruessable.setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMax(AUFGEDREHT);
    }

    private void aufschneiden() {
        n.narrate(neuerSatz("„Die habe ich aus dem Schatz eines Drachen geraubt“, sagst du.",
                "„Du Aufschneider“, grinst",
                anaph().nomK(),
                "dich an, „Drachen gibt es doch nur im Märchen!“")
                .timed(secs(20))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));
        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.35f, FeelingIntensity.STARK);
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG,
                0.2f, FeelingIntensity.STARK);

        stateComp.narrateAndSetState(NORMAL);

        scBegruessable.setSchonBegruesstMitSC(true);
    }

    private void dieWahrheitSagen() {
        final SubstantivischePhrase anaph = anaph();
        n.narrate(neuerSatz("„Die habe ich aus einem Schloss mitgehen lassen“, sagst du.",
                "„Ehrlich?“, fragt", anaph.nomK(),
                ". Du schaust", anaph.persPron().akkK(), "betreten an")
                .undWartest()
                .dann()
                .timed(secs(20))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));

        stateComp.narrateAndSetState(NORMAL);

        scBegruessable.setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMax(ZUFRIEDEN);
    }

    @Override
    public void forgetAll() {
        counterDao.reset(Counter.class);
    }
}
