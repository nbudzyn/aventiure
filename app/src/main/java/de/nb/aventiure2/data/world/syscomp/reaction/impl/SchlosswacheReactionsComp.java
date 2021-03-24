package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlosswacheReactionsComp.Counter.SCHLOSSWACHE_NEHMEN_GOLDENE_KUGEL_WACHE_IST_AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlosswacheReactionsComp.Counter.SCHLOSSWACHE_ON_ENTER_ROOM_SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlosswacheReactionsComp.Counter.SCHLOSSWACHE_REACTIONS_ABLEGEN_WACHE_IST_AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.UNAUFFAELLIG;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;

/**
 * "Reaktionen" der Schlosswache, z.B. darauf, dass Zeit vergeht,
 * der Benutzer die Goldene Kugel mitnimmt o.Ä.
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class SchlosswacheReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        SCHLOSSWACHE_ON_ENTER_ROOM_SCHLOSS_VORHALLE,
        SCHLOSSWACHE_REACTIONS_ABLEGEN_WACHE_IST_AUFMERKSAM,
        SCHLOSSWACHE_NEHMEN_GOLDENE_KUGEL_WACHE_IST_AUFMERKSAM
    }

    private final SchlosswacheStateComp stateComp;
    private final LocationComp locationComp;

    public SchlosswacheReactionsComp(final CounterDao counterDao,
                                     final Narrator n, final World world,
                                     final SchlosswacheStateComp stateComp,
                                     final LocationComp locationComp) {
        super(SCHLOSSWACHE, counterDao, n, world);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable,
                        final ILocationGO from,
                        @Nullable final ILocationGO to) {
    }

    @Override
    public boolean verbirgtSichVorEintreffendemSC() {
        if (!loadSC().locationComp().hasLocation(SCHLOSS_VORHALLE)) {
            return true;
        }

        return stateComp.hasState(UNAUFFAELLIG);
    }


    @Override
    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }

        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            onRelocationToSC(locatable, from);
            return;
        }

        if (world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            onRelocationFromSC(to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from,
                           final ILocationGO to) {
        if (!to.is(SCHLOSS_VORHALLE)) {
            return;
        }

        if (to.equals(from)) {
            // (Der Spieler wurde durch ein Erdbeben in der Schloss-Vorhalle hochgeworfen??)
            return;
        }

        if (stateComp.hasState(UNAUFFAELLIG)) {
            return;
        }

        final ILocatableGO goldeneKugel = (ILocatableGO) world.load(GOLDENE_KUGEL);
        if (!goldeneKugel.locationComp().hasRecursiveLocation(SPIELER_CHARAKTER)
                && goldeneKugel.locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            if (counterDao.incAndGet(SCHLOSSWACHE_ON_ENTER_ROOM_SCHLOSS_VORHALLE) > 1) {
                n.narrate(neuerSatz(getDescription(true).nomK(),
                        "scheint dich nicht zu bemerken")
                        .timed(secs(3)));
                return;
            }
        }

        if (from == null) {
            return;
        }

        scMussDasSchlossWiederVerlassen(from);
    }

    private void scMussDasSchlossWiederVerlassen(
            final ILocationGO raumAusDemDerSCDasSchlossBetretenHat) {
        // IDEA Ausspinnen: Der Spieler sollte selbst entscheiden,
        //  ob der das Schloss wieder verlässt - oder ggf. im Kerker landet.

        final SpielerCharakter sc = loadSC();

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altNeueSaetze("Die Wache spricht dich sofort an und macht dir",
                "unmissverständlich klar, dass du hier vor dem großen Fest nicht",
                "erwünscht bist. Du bist leicht zu überzeugen und trittst wieder",
                altSchlossVerlassenWohinAdvAngaben(raumAusDemDerSCDasSchlossBetretenHat)
                        .stream()
                        .map(aa -> aa.getDescription(P2, SG)), // "in den Sonnenschein"
                "hinaus", PARAGRAPH));

        alt.add(neuerSatz(PARAGRAPH,
                "„Heho, was wird das?“, tönt dir eine laute Stimme entgegen. "
                        + "„Als ob hier ein jeder "
                        + "nach Belieben hereinspazieren könnt. Das würde dem König so "
                        + "passen. Und "
                        + "seinem Kerkermeister auch.“ "
                        + "Du bleibst besser draußen", PARAGRAPH)
                // IDEA Eine Nacht im Kerker! (Ist auch ein Übernachten!)
        );

        n.narrateAlt(alt, secs(10));

        sc.locationComp().narrateAndSetLocation(raumAusDemDerSCDasSchlossBetretenHat);

        sc.memoryComp().setLastAction(
                new Action(Action.Type.BEWEGEN, raumAusDemDerSCDasSchlossBetretenHat));
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altSchlossVerlassenWohinAdvAngaben(
            final ILocationGO raumAusDemDerSCDasSchlossBetretenHat) {
        return altSchlossVerlassenWohinAdvAngaben(
                ((ILocationGO) world.load(SCHLOSS_VORHALLE)),
                raumAusDemDerSCDasSchlossBetretenHat);
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altSchlossVerlassenWohinAdvAngaben(
            final ILocationGO schlossRoom,
            final ILocationGO wohinRoom) {
        final Lichtverhaeltnisse lichtverhaeltnisseImSchloss =
                LocationSystem.getLichtverhaeltnisse(schlossRoom);
        final Lichtverhaeltnisse lichtverhaeltnisseDraussen =
                LocationSystem.getLichtverhaeltnisse(wohinRoom);

        if (lichtverhaeltnisseImSchloss  // Im Schloss ist es immer hell, wenn es also draußen
                // auch hell ist...
                == lichtverhaeltnisseDraussen) {
            return world.loadWetter().wetterComp().altWohinHinaus(lichtverhaeltnisseDraussen);
        }

        // FIXME In diesem Fall vielleicht ebenfalls
        //  world.loadWetter().wetterComp().altWohinHinaus() nutzen?

        // Draußen ist es (anders als im Schloss) dunkel
        return ImmutableSet.of(lichtverhaeltnisseDraussen.getWohin());
    }

    /**
     * Reaktionen darauf, dass etwas "zum Spieler gewandert ist". Der Spieler hat den
     * Gegenstand also genommen, als Geschenk erhalten, heimlich zugesteckt bekommen,
     * in seiner Hand materialisiert o.Ä.
     */
    @SuppressWarnings("unchecked")
    private void onRelocationToSC(final ILocatableGO locatable,
                                  @Nullable final ILocationGO from) {
        if (from == null || !locationComp.hasRecursiveLocation(from)) {
            // The Schlosswache does not notice.
            return;
        }

        if (!world.isOrHasRecursiveLocation(from, SCHLOSS_VORHALLE)
                && !world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            return;
        }

        if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                .hasState(BEGONNEN)) {
            // Schlosswache hat andere Dinge zu tun
            return;
        }

        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
                scHatEtwasGenommenOderHochgeworfenUndAufgefangen_wacheWirdAufmerksam();
                return;
            case AUFMERKSAM:
                if (locatable.is(GOLDENE_KUGEL)) {
                    scHatGoldeneKugelGenommenOderHochgeworfenUndAufgefangen_wacheIstAufmerksam(
                            locatable, from);
                    return;
                }
            default:
                return;
        }
    }

    private void scHatEtwasGenommenOderHochgeworfenUndAufgefangen_wacheWirdAufmerksam() {
        final SpielerCharakter sc = loadSC();

        n.narrate(
                neuerSatz(PARAGRAPH, "Da wird eine Wache auf dich aufmerksam. "
                        + "„Wie seid Ihr hier hereingekommen?“, fährt sie dich "
                        + "scharf an. „Das Fest ist erst am Sonntag. Heute "
                        + "ist Samstag und Ihr habt hier nichts zu suchen!“ "
                        + "Mit kräftiger Hand klopft die Wache auf ihre Hellebarde")
                        .timed(secs(20)));

        stateComp.narrateAndSetState(AUFMERKSAM);
        loadSC().memoryComp().narrateAndUpgradeKnown(SCHLOSSWACHE);
        loadSC().mentalModelComp().setAssumedLocationToActual(SCHLOSSWACHE);
        sc.feelingsComp().requestMood(ANGESPANNT);
    }

    private void scHatGoldeneKugelGenommenOderHochgeworfenUndAufgefangen_wacheIstAufmerksam(
            final ILocatableGO goldeneKugel,
            final ILocationGO fromSchlossVorhalleOderSC) {
        if (world.isOrHasRecursiveLocation(fromSchlossVorhalleOderSC, SPIELER_CHARAKTER)) {
            scHatGoldeneKugelHochgeworfenUndAufgefangen_wacheIstAufmerksam(goldeneKugel);
            return;
        }

        // Spieler hat goldene Kugel in SCHLOSS_VORHALLE genommen

        if (counterDao.incAndGet(SCHLOSSWACHE_NEHMEN_GOLDENE_KUGEL_WACHE_IST_AUFMERKSAM) == 1) {
            scHatGoldeneKugelGenommen_wacheIstAufmerksam_erwischt(goldeneKugel);
            return;
        }

        scHatGoldeneKugelGenommen_wacheIstAufmerksam_nichtErwischt();
    }

    private void scHatGoldeneKugelHochgeworfenUndAufgefangen_wacheIstAufmerksam(
            final ILocatableGO goldeneKugel) {
        n.narrate(
                neuerSatz(PARAGRAPH, "„Was treibt Ihr für einen Unfug, legt sofort das",
                        "Schmuckstück wieder hin!“,",
                        "ruft dir",
                        getDescription(true).nomK(),
                        "zu")
                        .timed(secs(5)));

        // IDEA Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker

        final SpielerCharakter sc = loadSC();
        sc.feelingsComp().requestMood(ANGESPANNT);

        n.narrate(du(PARAGRAPH, "legst",
                "die schöne goldene Kugel eingeschüchtert wieder an ihren Platz")
                .mitVorfeldSatzglied("eingeschüchtert")
                .timed(secs(5))
                .undWartest()
                .phorikKandidat(NumerusGenus.F, goldeneKugel.getId()));

        goldeneKugel.locationComp().narrateAndSetLocation(
                sc.locationComp().getLocation()
        );

        sc.memoryComp()
                .setLastAction(Action.Type.ABLEGEN, goldeneKugel, sc.locationComp().getLocation());
    }

    private void scHatGoldeneKugelGenommen_wacheIstAufmerksam_erwischt(
            final ILocatableGO goldeneKugel) {
        final AltTimedDescriptionsBuilder alt = altTimed();

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(", doch keine Sekunde später baut sich die "
                    + "Wache vor dir auf. "
                    + "„Wir haben hier sehr gute Verliese, Ihr dürftet "
                    + "überrascht sein“, sagt sie und schaut dich "
                    + "durchdringend an")
                    .timed(secs(15)));
        }

        alt.add(neuerSatz("„Ihr habt da wohl etwas, das nicht Euch gehört“, "
                + "wirst du von hinten angesprochen.")
                .timed(secs(15)));

        n.narrateAlt(alt);

        n.narrate(neuerSatz(PARAGRAPH, "Da legst du doch besser die schöne goldene Kugel "
                + "wieder an ihren Platz")
                .timed(secs(5))
                .undWartest()
                .phorikKandidat(NumerusGenus.F, goldeneKugel.getId()));

        // IDEA Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker

        goldeneKugel.locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE);

        loadSC().memoryComp().setLastAction(Action.Type.ABLEGEN, goldeneKugel, SCHLOSS_VORHALLE);
    }

    private void scHatGoldeneKugelGenommen_wacheIstAufmerksam_nichtErwischt() {
        final AltTimedDescriptionsBuilder alt = altTimed();

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(", während",
                    getDescription().nomK(),
                    "gerade damit beschäftigt ist, ihre Waffen zu polieren")
                    .schonLaenger()
                    .timed(secs(3))
                    .dann());
        } else {
            alt.add(
                    du("hast", "großes Glück, denn",
                            getDescription().nomK(),
                            "ist gerade damit beschäftigt, ihre Waffen zu polieren")
                            .schonLaenger()
                            .komma(true).timed(secs(3))
                            .dann());
        }

        alt.add(neuerSatz(getDescription().datK(), "ist anscheinend nichts aufgefallen")
                .timed(secs(3))
                .dann());

        n.narrateAlt(alt);
    }

    /**
     * Reaktionen darauf, dass etwas "den Spieler verlassen hat". Der Spieler hat den
     * Gegenstand also abgelegt, an jemand anderen weitergegeben, gestohlen bekommen,
     * der Gegenstand hat sich aufgelöst o.Ä.
     */
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void onRelocationFromSC(final ILocationGO to) {
        if (!locationComp.hasSameOuterMostLocationAs(to)) {
            // The Schlosswache does not notice.
            return;
        }

        if (!to.is(SCHLOSS_VORHALLE)) {
            return;
        }

        switch (stateComp.getState()) {
            case AUFMERKSAM:
                scHatEtwasInSchlosVorhalleHingelegt_wacheIstAufmerksam();
                return;
            default:
                return;
        }
    }

    private void scHatEtwasInSchlosVorhalleHingelegt_wacheIstAufmerksam() {
        if (counterDao.get(SCHLOSSWACHE_REACTIONS_ABLEGEN_WACHE_IST_AUFMERKSAM) > 1) {
            return;
        }

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.narrate(satzanschluss(", von der kopfschüttelnden Wache beobachtet")
                    .timed(secs(5))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            SCHLOSSWACHE_REACTIONS_ABLEGEN_WACHE_IST_AUFMERKSAM)
                    .dann());
            return;
        }

        loadSC().feelingsComp().requestMood(ANGESPANNT);
        n.narrateAlt(
                neuerSatz(getDescription().nomK(),
                        "beoabachtet dich dabei")
                        .timed(secs(5))
                        .dann(),
                neuerSatz(getDescription().nomK(),
                        "sieht dir belustig dabei zu")
                        .timed(secs(5))
                        .dann(),
                neuerSatz(PARAGRAPH, "Die Wache sieht sehr missbilligend zu")
                        .timed(secs(3)));
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        if (SCHLOSSFEST_BEGINN_DATE_TIME.isWithin(startTime, endTime)) {
            schlossfestBeginnt();
        }
    }

    private void schlossfestBeginnt() {
        final SpielerCharakter sc = loadSC();
        if (sc.locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            schlossfestBeginnt_Vorhalle(sc);
            return;
        }

        // Beim Fest ist die Schlosswache beschäftigt
        stateComp.narrateAndSetState(UNAUFFAELLIG);
        // Passiert nebenher und braucht KEINE zusätzliche Zeit
    }

    private void schlossfestBeginnt_Vorhalle(final SpielerCharakter sc) {
        // Beim Fest ist die Schlosswache mit anderen Dingen beschäftigt
        n.narrate(neuerSatz(PARAGRAPH, "Die Wache spricht dich an: „Wenn ich Euch dann "
                + "hinausbitten dürfte? Wer wollte "
                + "denn den Vorbereitungen für das große Fest im Wege stehen?“ – Nein, "
                + "das willst du sicher nicht.")
                .timed(secs(30)));
        stateComp.narrateAndSetState(UNAUFFAELLIG);

        sc.locationComp().narrateAndSetLocation(DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> {
                    n.narrate(neuerSatz(PARAGRAPH, "Draußen sind Handwerker dabei, im "
                            + "ganzen Schlossgarten kleine bunte "
                            + "Pagoden aufzubauen. Du schaust eine "
                            + "Zeitlang zu.\n"
                            + "Zunehmend strömen von allen Seiten "
                            + "Menschen herzu und wie es scheint, "
                            + "ist auch "
                            + "der Zugang zum Schloss jetzt für alle "
                            + "geöffnet. Aus dem Schloss "
                            + "weht dich der "
                            + "Geruch von Gebratenem an.", PARAGRAPH)
                            .timed(mins(44)));

                    sc.feelingsComp().requestMood(NEUTRAL);

                    // Der Spieler weiß jetzt, dass das Schlossfest läuft
                    sc.memoryComp().narrateAndUpgradeKnown(SCHLOSSFEST);
                });
    }
}