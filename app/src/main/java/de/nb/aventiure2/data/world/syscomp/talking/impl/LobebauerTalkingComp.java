package de.nb.aventiure2.data.world.syscomp.talking.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.REAGIEREN;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;

public class LobebauerTalkingComp extends AbstractTalkingComp {
    private final SiebenJahreAnspracheMitAntwort siebenJahreAnsprache;
    private final LocationComp locationComp;

    public LobebauerTalkingComp(final AvDatabase db,
                                final TimeTaker timeTaker,
                                final Narrator n,
                                final World world,
                                final LocationComp locationComp) {
        super(LOBEBAUER, db, timeTaker, n, world,
                // Der Lobebauer tut so, als hätte man sich schon begrüßt
                true);
        this.locationComp = locationComp;

        siebenJahreAnsprache = new SiebenJahreAnspracheMitAntwort(
                n, world, locationComp, this);
    }

    public void sprichtScAnUndHaeltSiebenJahreAnsprache() {
        setTalkingTo(SPIELER_CHARAKTER);
        siebenJahreAnsprache.nscStelltFrage();
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        final ImmutableList.Builder<SCTalkAction> res = ImmutableList.builder();
        res.add(exitSt(
                REAGIEREN.mit(getDescription(textContext, possessivDescriptionVorgabe, true)).neg(),
                this::scReagiertNicht_Exit));
        res.addAll(siebenJahreAnsprache.getAntwortActions());
        return res.build();
    }

    private void scReagiertNicht_Exit() {
        final SubstantivischePhrase anaph = anaph(textContext, possessivDescriptionVorgabe);
        n.narrateAlt(secs(10),
                neuerSatz("Schon",
                        SeinUtil.istSind(anaph),
                        anaph.nomK(),
                        "im Gedränge verschwunden. Was bleibt, ist das gute Gefühl, geholfen zu "
                                + "haben!",
                        CHAPTER));

        gespraechspartnerBeendetGespraech();
        locationComp.narrateAndUnsetLocation();

        loadSC().feelingsComp().requestMoodMin(BEWEGT);
    }
}
