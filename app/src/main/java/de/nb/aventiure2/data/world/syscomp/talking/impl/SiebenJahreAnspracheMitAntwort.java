package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractFrageMitAntworten;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkContext;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSPRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SAGEN;

class SiebenJahreAnspracheMitAntwort extends AbstractFrageMitAntworten {

    private final LocationComp locationComp;

    SiebenJahreAnspracheMitAntwort(final Narrator n, final World world,
                                   final LocationComp locationComp,
                                   final ITalkContext talkContext) {
        super(LOBEBAUER, n, world, talkContext);

        this.locationComp = locationComp;
    }

    @Override
    public void nscStelltFrage() {
        locationComp.narrateAndSetLocation(loadSC().getVisibleOuterMostLocation());

        final SubstantivischePhrase anaph = anaph();

        n.narrate(neuerSatz(anaph.nomK(),
                ANSPRECHEN.getPraesensOhnePartikel(anaph),
                "dich von der Seite an: „Sieben Jahre!“",
                SAGEN.getPraesensOhnePartikel(anaph),
                anaph.persPron().nomK(),
                ", „Sieben Jahre hat ihn keiner gesehen! – Verzaubert, haben sie gesagt!",
                "Ich glaube sowas ja nicht. Aber sein Vater, der wird Augen machen!“",
                PARAGRAPH)
                .timed(secs(20)));
        locationComp.narrateAndSetLocation(loadSC().locationComp().getLocation());
        world.narrateAndUpgradeScKnownAndAssumedState(LOBEBAUER);
    }

    @Override
    public Iterable<SCTalkAction> getAntwortActions() {
        return ImmutableList.of(
                st(VerbSubjObj.ANTWORTEN, this::antworten));
    }

    private void antworten() {
        final SubstantivischePhrase anaph = anaph();
        n.narrateAlt(secs(10),
                neuerSatz("Bevor du etwas sagen kannst,",
                        SeinUtil.istSind(anaph),
                        anaph.nomK(),
                        "in der Menge verschwunden. Was bleibt, ist das gute Gefühl, geholfen zu",
                        "haben!",
                        CHAPTER));

        talkContext.gespraechspartnerBeendetGespraech();
        locationComp.narrateAndUnsetLocation();

        loadSC().feelingsComp().requestMoodMin(BEWEGT);
    }
}
