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
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_BIENEN_UND_BLUMEN_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.NORMAL;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.NaschereifrageMitAntworten.Counter.FRAGE_BEANTWORTET;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GENAU;
import static de.nb.aventiure2.german.base.Nominalphrase.EIN_GROBER_ABRISS;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ERKLAEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;

class NaschereifrageMitAntworten extends AbstractFrageMitAntworten {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        FRAGE_BEANTWORTET
    }

    NaschereifrageMitAntworten(final CounterDao counterDao,
                               final Narrator n, final World world,
                               final RapunzelStateComp stateComp,
                               final FeelingsComp feelingsComp,
                               final IScBegruessable begruesstMitScSetter) {
        super(RAPUNZEL, counterDao, n, world, stateComp, feelingsComp, begruesstMitScSetter);
    }

    @Override
    public void nscStelltFrage() {
        final SubstantivischePhrase anaph = anaph();

        n.narrate(neuerSatz("„Weißt du, was meine Liebste Nascherei ist?“, fragt",
                anaph.nomK(),
                ". Nein, du weißt es nicht. – „Honig! Und ich wüsste so gern, woher der",
                "kommt. Die Alte wills mir nicht erklären.“ ",
                "„Na, den holen die Bienen aus den Blumen!“",
                "„Was machen denn die Bienen in den Blumen?“, fragt ",
                anaph.persPron().nomK(),
                "überrascht")
                .timed(secs(20)));

        stateComp.narrateAndSetState(HAT_NACH_BIENEN_UND_BLUMEN_GEFRAGT);

    }

    @Override
    public Iterable<SCTalkAction> getAntwortActions() {

        return ImmutableList.of(
                st(ERKLAEREN.mitAkk(Indefinitpronomen.ALLES)
                                .mitDat(getDescription())
                                .mitAdverbialerAngabe(
                                        new AdverbialeAngabeSkopusVerbAllg(
                                                GENAU.mitGraduativerAngabe("ganz"))),
                        this::allesGanzGenauErklaeren),
                st(GEBEN.mitAkk(EIN_GROBER_ABRISS), this::einenGrobenAbrissGeben));
    }

    private void allesGanzGenauErklaeren() {
        final SubstantivischePhrase anaph = anaph();
        n.narrate(du("erklärst",
                anaph.datK(),
                "alles ganz genau mit den Bienchen und den Blümchen",
                SENTENCE,
                anaph.nomK(),
                "zeigt großes Interesse")
                .timed(mins(5))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));
        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.6f, FeelingIntensity.STARK);

        stateComp.narrateAndSetState(NORMAL);

        scBegruessable.setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);
    }

    private void einenGrobenAbrissGeben() {
        final SubstantivischePhrase anaph = anaph();
        n.narrate(du("gibst",
                anaph.datK(),
                "einen groben Abriss",
                SENTENCE,
                anaph.persPron().nomK(),
                "verliert bald das Interesse")
                .timed(secs(45))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG,
                0.2f, FeelingIntensity.DEUTLICH);

        stateComp.narrateAndSetState(NORMAL);

        scBegruessable.setSchonBegruesstMitSC(true);
    }

    @Override
    public void forgetAll() {
        counterDao.reset(Counter.class);
    }
}
