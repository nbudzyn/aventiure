package de.nb.aventiure2.data.world.syscomp.talking.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.NORMAL;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelJahreszeitenFrageMitAntworten.Counter.FRAGE_BEANTWORTET;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObjWoertlicheRede.ANTWORTEN;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkContext;
import de.nb.aventiure2.data.world.syscomp.talking.RapunzelFrageMitAntworten;

class RapunzelJahreszeitenFrageMitAntworten extends RapunzelFrageMitAntworten {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        FRAGE_BEANTWORTET
    }

    RapunzelJahreszeitenFrageMitAntworten(final CounterDao counterDao,
                                          final Narrator n, final World world,
                                          final RapunzelStateComp stateComp,
                                          final FeelingsComp feelingsComp,
                                          final ITalkContext talkContext) {
        super(RAPUNZEL, counterDao, n, world, stateComp, feelingsComp, talkContext);
    }

    @Override
    public void nscStelltFrage() {
        n.narrate(neuerSatz("„Welche Jahreszeit riechst du am liebsten?“, fragt",
                anaph().nomK(),
                "dich")
                .timed(secs(20)));

        stateComp.narrateAndSetState(HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT);
    }

    @Override
    public Iterable<SCTalkAction> getAntwortActions() {
        return ImmutableList.of(
                st(ANTWORTEN.mitWoertlicheRede("Den Frühling"),
                        this::mitFruehlingAntworten),
                st(ANTWORTEN.mitWoertlicheRede("Den Sommer"),
                        this::mitSommerAntworten),
                st(ANTWORTEN.mitWoertlicheRede("Den Herbst"),
                        this::mitHerbstAntworten),
                st(ANTWORTEN.mitWoertlicheRede("Den Winter"),
                        this::mitWinterAntworten));
    }

    private void mitFruehlingAntworten() {
        narrateEntscheidestDichFuer("den Frühling");
        rapunzelReagiertAufAntwort(true);
    }

    private void mitSommerAntworten() {
        narrateEntscheidestDichFuer("den Sommer");
        rapunzelReagiertAufAntwort(false);
    }

    private void mitHerbstAntworten() {
        narrateEntscheidestDichFuer("den Herbst");
        rapunzelReagiertAufAntwort(false);
    }

    private void mitWinterAntworten() {
        narrateEntscheidestDichFuer("den Winter");
        rapunzelReagiertAufAntwort(false);
    }

    private void narrateEntscheidestDichFuer(final String jahrezeitAkk) {
        n.narrate(du("entscheidest", "dich für", jahrezeitAkk)
                .schonLaenger()
                .timed(secs(15))
                .undWartest());
    }

    private void rapunzelReagiertAufAntwort(final boolean antwortWarFruehling) {
        if (antwortWarFruehling) {
            feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                    0.5f, FeelingIntensity.STARK);

            n.narrate(neuerSatz("„Oh, ich auch“,",
                    "ruft", anaph().nomK(), "aus und strahlt")
                    .timed(secs(10)));
        } else {
            n.narrate(neuerSatz("„Bei mir ist es der Frühling“,",
                    "sagt", anaph().nomK(), "und lächelt gedankenversunken")
                    .timed(secs(10)));
        }

        n.narrate(neuerSatz(
                "„Ich wache auf und sofort weiß ich – die Krokusse sinds aufgeblüht!",
                "Dann freue ich mich schon auf die Schwalben, die in der Mauer über",
                "dem Fenster brüten.“")
                .timed(secs(20))
                .withCounterIdIncrementedIfTextIsNarrated(FRAGE_BEANTWORTET));

        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG,
                0.35f, FeelingIntensity.STARK);

        stateComp.narrateAndSetState(NORMAL);

        talkContext.setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMin(BEWEGT);
    }

    @Override
    public void forgetAll() {
        counterDao.reset(Counter.class);
    }
}
