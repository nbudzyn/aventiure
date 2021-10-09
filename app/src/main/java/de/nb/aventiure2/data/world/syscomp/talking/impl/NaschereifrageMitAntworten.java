package de.nb.aventiure2.data.world.syscomp.talking.impl;

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
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GROB;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABRISS;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ERKLAEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkContext;
import de.nb.aventiure2.data.world.syscomp.talking.RapunzelFrageMitAntworten;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

class NaschereifrageMitAntworten extends RapunzelFrageMitAntworten {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        FRAGE_BEANTWORTET
    }

    NaschereifrageMitAntworten(final CounterDao counterDao,
                               final Narrator n, final World world,
                               final RapunzelStateComp stateComp,
                               final FeelingsComp feelingsComp,
                               final ITalkContext talkContext) {
        super(RAPUNZEL, counterDao, n, world, stateComp, feelingsComp, talkContext);
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
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbAllg(
                                                GENAU.mitGraduativerAngabe("ganz"))),
                        this::allesGanzGenauErklaeren),
                st(GEBEN.mitAkk(Nominalphrase.np(INDEF, GROB, ABRISS)),
                        this::einenGrobenAbrissGeben));
    }

    private void allesGanzGenauErklaeren() {
        final SubstantivischePhrase anaph = anaph();
        n.narrate(du("erklärst",
                anaph.datK(),
                "alles ganz genau mit den Bienchen und den Blümchen",
                SENTENCE,
                anaph.nomK(),
                "zeigt großes Interesse")
                .schonLaenger()
                .timed(mins(5))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));
        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.6f, FeelingIntensity.STARK);

        stateComp.narrateAndSetState(NORMAL);

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
    }

    @Override
    public void forgetAll() {
        counterDao.reset(Counter.class);
    }
}
