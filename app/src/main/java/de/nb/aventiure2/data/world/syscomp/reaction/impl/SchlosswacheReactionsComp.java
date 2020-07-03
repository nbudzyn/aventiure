package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.data.world.gameobject.World.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * "Reaktionen" der Schlosswache, z.B. darauf, dass Zeit vergeht,
 * der Benutzer die Goldene Kugel mitnimmt o.Ä.
 */
public class SchlosswacheReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, ITimePassedReactions {

    private final AbstractDescriptionComp descriptionComp;
    private final SchlosswacheStateComp stateComp;
    private final LocationComp locationComp;

    public SchlosswacheReactionsComp(final AvDatabase db,
                                     final World world,
                                     final AbstractDescriptionComp descriptionComp,
                                     final SchlosswacheStateComp stateComp,
                                     final LocationComp locationComp) {
        super(SCHLOSSFEST, db, world);
        this.descriptionComp = descriptionComp;
        this.stateComp = stateComp;
        this.locationComp = locationComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            return onRelocationToSC(locatable, from);
        }

        if (world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            return onRelocationFromSC(to);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from,
                                 final ILocationGO to) {
        if (!to.is(SCHLOSS_VORHALLE)) {
            return noTime();
        }

        if (to.equals(from)) {
            // (Der Spieler wurde durch ein Erdbeben in der Schloss-Vorhalle hochgeworfen??)
            return noTime();
        }

        if (stateComp.hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        final ILocatableGO goldeneKugel = (ILocatableGO) world.load(GOLDENE_KUGEL);
        if (!goldeneKugel.locationComp().hasRecursiveLocation(SPIELER_CHARAKTER)
                && goldeneKugel.locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            if (db.counterDao().incAndGet(
                    "SchlosswacheReactions_onEnterRoom_SchlossVorhalle") > 1) {
                return n.add(neuerSatz(
                        capitalize(getSchlosswacheDescription(true).nom())
                                + " scheint dich nicht zu bemerken", secs(3)));
            }
        }

        if (from == null) {
            return noTime();
        }

        return scMussDasSchlossWiederVerlassen(from);
    }

    private AvTimeSpan scMussDasSchlossWiederVerlassen(
            final ILocationGO raumAusDemDerSCDasSchlossBetretenHat) {
        // STORY Ausspinnen: Der Spieler sollte selbst entscheiden,
        //  ob der das Schloss wieder verlässt - oder ggf. im Kerker landet.

        final SpielerCharakter sc = loadSC();

        AvTimeSpan timeSpan = n.addAlt(
                neuerSatz("Die Wache spricht dich sofort an und macht dir unmissverständlich "
                                + "klar, dass du hier "
                                + "vor dem großen Fest nicht erwünscht bist. Du bist "
                                + "leicht zu "
                                + "überzeugen und trittst wieder "
                                + schlossVerlassenWohinDescription(
                        raumAusDemDerSCDasSchlossBetretenHat)
                                // "in den Sonnenschein"
                                + " hinaus",
                        secs(10))
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "„Heho, was wird das?“, tönt dir eine laute Stimme entgegen. "
                                + "„Als ob hier ein jeder "
                                + "nach Belieben hereinspazieren könnt. Das würde dem König so "
                                + "passen. Und "
                                + "seinem Kerkermeister auch.“ "
                                + "Du bleibst besser draußen",
                        secs(10))
                        .beendet(PARAGRAPH)
        );

        timeSpan = timeSpan.plus(
                sc.locationComp().narrateAndSetLocation(raumAusDemDerSCDasSchlossBetretenHat));

        sc.memoryComp().setLastAction(
                new Action(Action.Type.BEWEGEN, raumAusDemDerSCDasSchlossBetretenHat));

        return timeSpan;
    }

    private String schlossVerlassenWohinDescription(
            final ILocationGO raumAusDemDerSCDasSchlossBetretenHat) {
        return schlossVerlassenWohinDescription(
                ((ILocationGO) world.load(SCHLOSS_VORHALLE)),
                raumAusDemDerSCDasSchlossBetretenHat);
    }

    private static String schlossVerlassenWohinDescription(
            final ILocationGO schlossRoom,
            final ILocationGO wohinRoom) {
        final Lichtverhaeltnisse lichtverhaeltnisseImSchloss =
                getLichtverhaeltnisse(schlossRoom);
        final Lichtverhaeltnisse lichtverhaeltnisseDraussen =
                getLichtverhaeltnisse(wohinRoom);
        if (lichtverhaeltnisseImSchloss  // Im Schloss ist es immer hell, wenn es also draußen
                // auch hell ist...
                == lichtverhaeltnisseDraussen) {
            return "in den Sonnenschein";
        }

        // Draußen ist es (anders als im Schloss) dunkel
        return lichtverhaeltnisseDraussen.getWohin();
    }

    /**
     * Reaktionen darauf, dass etwas "zum Spieler gewandert ist". Der Spieler hat den
     * Gegenstand also genommen, als Geschenk erhalten, heimlich zugesteckt bekommen,
     * in seiner Hand materialisiert o.Ä.
     */
    @Contract("_, null -> !null")
    private AvTimeSpan onRelocationToSC(final ILocatableGO locatable,
                                        @Nullable final ILocationGO from) {
        if (from == null || !locationComp.hasRecursiveLocation(from)) {
            // The Schlosswache does not notice.
            return noTime();
        }

        if (!world.isOrHasRecursiveLocation(from, SCHLOSS_VORHALLE)
                && !world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            return noTime();
        }

        if (((IHasStateGO) world.load(SCHLOSSFEST)).stateComp().hasState(BEGONNEN)) {
            // Schlosswache hat andere Dinge zu tun
            return noTime();
        }

        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
                return scHatEtwasGenommenOderHochgeworfenUndAufgefangen_wacheWirdAufmerksam();
            case AUFMERKSAM:
                if (locatable.is(GOLDENE_KUGEL)) {
                    return scHatGoldeneKugelGenommenOderHochgeworfenUndAufgefangen_wacheIstAufmerksam(
                            locatable, from);
                }
            default:
                return noTime();
        }
    }

    private AvTimeSpan scHatEtwasGenommenOderHochgeworfenUndAufgefangen_wacheWirdAufmerksam() {
        stateComp.setState(AUFMERKSAM);
        final SpielerCharakter sc = loadSC();
        @Nullable final ILocationGO from = sc.locationComp().getLocation();

        final AvTimeSpan timeElapsed = n.add(
                neuerSatz(PARAGRAPH, "Da wird eine Wache auf dich aufmerksam. "
                                + "„Wie seid Ihr hier hereingekommen?“, fährt sie dich "
                                // STORY Ausspinnen: Auf dem Fest kriegt der Frosch beim Essen
                                //  seinen Willen.
                                + "scharf an. „Das Fest ist erst am Sonntag. Heute "
                                + "ist Samstag und Ihr habt hier nichts zu suchen!“ "
                                + "Mit kräftiger Hand klopft die Wache auf ihre Hellebarde",
                        secs(20)));

        sc.memoryComp().upgradeKnown(SCHLOSSWACHE,
                Known.getKnown(getLichtverhaeltnisse(from)));
        sc.feelingsComp().setMood(ANGESPANNT);

        return timeElapsed;
    }

    /**
     * Gibt die Lichtverhältnisse an diesem Ort zurück.
     */
    private static Lichtverhaeltnisse getLichtverhaeltnisse(
            @Nullable final ILocationGO location) {
        if (location == null) {
            return Lichtverhaeltnisse.HELL;
        }

        return location.storingPlaceComp().getLichtverhaeltnisse();
    }

    private AvTimeSpan scHatGoldeneKugelGenommenOderHochgeworfenUndAufgefangen_wacheIstAufmerksam(
            final ILocatableGO goldeneKugel,
            final ILocationGO fromSchlossVorhalleOderSC) {
        if (world.isOrHasRecursiveLocation(fromSchlossVorhalleOderSC, SPIELER_CHARAKTER)) {
            return scHatGoldeneKugelHochgeworfenUndAufgefangen_wacheIstAufmerksam(goldeneKugel);
        }

        // Spieler hat goldene Kugel in SCHLOSS_VORHALLE genommen

        if (db.counterDao().incAndGet(
                "SchlosswacheReactions_nehmenGoldeneKugel_wacheIstAufmerksam") == 1) {
            return scHatGoldeneKugelGenommen_wacheIstAufmerksam_erwischt(goldeneKugel);
        }

        return scHatGoldeneKugelGenommen_wacheIstAufmerksam_nichtErwischt();
    }

    private AvTimeSpan scHatGoldeneKugelHochgeworfenUndAufgefangen_wacheIstAufmerksam(
            final ILocatableGO goldeneKugel) {
        AvTimeSpan timeSpan = n.add(
                neuerSatz(PARAGRAPH, "„Was treibt Ihr für einen Unfug, legt sofort das "
                        + "Schmuckstück wieder hin!“, "
                        + "ruft dir "
                        + getSchlosswacheDescription(true).nom()
                        + " zu", secs(5)));

        // TODO Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker

        final SpielerCharakter sc = loadSC();
        sc.feelingsComp().setMood(ANGESPANNT);

        timeSpan = timeSpan.plus(n.add(du(PARAGRAPH,
                "legst", "die schöne goldene Kugel eingeschüchtert wieder an ihren Platz",
                "eingeschüchtert",
                secs(5))
                .undWartest()
                .phorikKandidat(NumerusGenus.F, goldeneKugel.getId())));

        timeSpan = timeSpan.plus(
                goldeneKugel.locationComp().narrateAndSetLocation(
                        sc.locationComp().getLocation()
                ));

        sc.memoryComp()
                .setLastAction(Action.Type.ABLEGEN, goldeneKugel, sc.locationComp().getLocation());

        return timeSpan;
    }

    private AvTimeSpan scHatGoldeneKugelGenommen_wacheIstAufmerksam_erwischt(
            final ILocatableGO goldeneKugel) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        if (n.getStoryState().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(", doch keine Sekunde später baut sich die "
                            + "Wache vor dir auf. "
                            + "„Wir haben hier sehr gute Verliese, Ihr dürftet "
                            + "überrascht sein“, sagt sie und schaut dich "
                            + "durchdringend an",
                    secs(15)));
        }

        alt.add(neuerSatz("„Ihr habt da wohl etwas, das nicht Euch gehört“, "
                        + "wirst du von hinten angesprochen.",
                secs(15)));

        AvTimeSpan timeElapsed = n.addAlt(alt);

        timeElapsed = timeElapsed.plus(n.add(neuerSatz(PARAGRAPH,
                "Da legst du doch besser die schöne goldene Kugel "
                        + "wieder an ihren Platz",
                secs(5))
                .undWartest()
                .phorikKandidat(NumerusGenus.F, goldeneKugel.getId())));

        // STORY Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker

        timeElapsed = timeElapsed.plus(
                goldeneKugel.locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE));

        loadSC().memoryComp().setLastAction(Action.Type.ABLEGEN, goldeneKugel, SCHLOSS_VORHALLE);

        return timeElapsed;
    }

    private AvTimeSpan scHatGoldeneKugelGenommen_wacheIstAufmerksam_nichtErwischt() {
        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        if (n.getStoryState().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(
                    ", während "
                            + getSchlosswacheDescription().nom()
                            + " gerade damit beschäftigt ist, ihre Waffen zu polieren",
                    secs(3))
                    .dann());
        } else {
            alt.add(du(
                    "hast", "großes Glück, denn "
                            + getSchlosswacheDescription().nom()
                            + " ist gerade damit beschäftigt, ihre Waffen zu polieren", secs(3))
                    .komma(true)
                    .dann());
        }

        alt.add(neuerSatz(
                capitalize(getSchlosswacheDescription().dat())
                        + " ist anscheinend nichts aufgefallen",
                secs(3))
                .dann());

        return n.addAlt(alt);
    }

    /**
     * Reaktionen darauf, dass etwas "den Spieler verlassen hat". Der Spieler hat den
     * Gegenstand also abgelegt, an jemand anderen weitergegeben, gestohlen bekommen,
     * der Gegenstand hat sich aufgelöst o.Ä.
     */
    private AvTimeSpan onRelocationFromSC(final ILocationGO to) {
        if (!locationComp.hasSameUpperMostLocationAs(to)) {
            // The Schlosswache does not notice.
            return noTime();
        }

        if (!to.is(SCHLOSS_VORHALLE)) {
            return noTime();
        }

        switch (stateComp.getState()) {
            case AUFMERKSAM:
                return scHatEtwasInSchlosVorhalleHingelegt_wacheIstAufmerksam();
            default:
                return noTime();
        }
    }

    private AvTimeSpan scHatEtwasInSchlosVorhalleHingelegt_wacheIstAufmerksam() {
        if (db.counterDao()
                .incAndGet("SchlosswacheReactions_ablegen_wacheIstAufmerksam") > 2) {
            return noTime();
        }

        if (n.getStoryState().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            return n.add(satzanschluss(", von der kopfschüttelnden Wache beobachtet",
                    secs(5))
                    .dann());
        }

        loadSC().feelingsComp().setMood(ANGESPANNT);
        return n.addAlt(
                neuerSatz(capitalize(getSchlosswacheDescription().nom())
                        + " beoabachtet dich dabei", secs(5))
                        .dann(),
                neuerSatz(capitalize(getSchlosswacheDescription().nom())
                        + " sieht dir belustig dabei zu", secs(5))
                        .dann(),
                // STORY Das hier nur, wenn die Kugel HOCHGEWORFEN wurde (und nicht aufgefangen)
                neuerSatz(PARAGRAPH, "Die Wache sieht sehr missbilligend zu", secs(3)));
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        if (SCHLOSSFEST_BEGINN_DATE_TIME.isWithin(lastTime, now)) {
            timeElapsed = timeElapsed.plus(schlossfestBeginnt());
        }

        return timeElapsed;
    }

    private AvTimeSpan schlossfestBeginnt() {
        final SpielerCharakter sc = loadSC();
        if (sc.locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE)) {
            return schlossfestBeginnt_Vorhalle(sc);
        }

        // Beim Fest ist die Schlosswache beschäftigt
        stateComp.setState(UNAUFFAELLIG);
        return noTime(); // Passiert nebenher und braucht KEINE zusätzliche Zeit
    }

    @NonNull
    private AvTimeSpan schlossfestBeginnt_Vorhalle(final SpielerCharakter sc) {
        // Beim Fest ist die Schlosswache mit anderen Dingen beschäftigt
        stateComp.setState(UNAUFFAELLIG);

        final AvTimeSpan timeElapsed = n.add(neuerSatz(PARAGRAPH,
                "Die Wache spricht dich an: „Wenn ich Euch dann "
                        + "hinausbitten dürfte? Wer wollte "
                        + "denn den Vorbereitungen für das große Fest im Wege stehen?“ – Nein, "
                        + "das willst du sicher nicht.", secs(30)));

        return timeElapsed.plus(
                sc.locationComp().narrateAndSetLocation(DRAUSSEN_VOR_DEM_SCHLOSS,
                        () -> {
                            final AvTimeSpan timeElapsedOnEnter =
                                    n.add(neuerSatz(PARAGRAPH,
                                            "Draußen sind Handwerker dabei, im "
                                                    + "ganzen Schlossgarten kleine bunte "
                                                    + "Pagoden aufzubauen. Du schaust eine "
                                                    + "Zeitlang zu.\n"
                                                    + "Zunehmend strömen von allen Seiten "
                                                    + "Menschen herzu und wie es scheint, "
                                                    + "ist auch "
                                                    + "der Zugang zum Schloss jetzt für alle "
                                                    + "geöffnet. Aus dem Schloss "
                                                    + "weht dich der "
                                                    + "Geruch von Gebratenem an.", mins(44))
                                            .beendet(PARAGRAPH));

                            sc.feelingsComp().setMood(NEUTRAL);

                            // Der Spieler weiß jetzt, dass das Schlossfest läuft
                            db.counterDao().incAndGet(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

                            return timeElapsedOnEnter;
                        }));
    }

    /**
     * Gibt eine Nominalphrase zurück, die die Schlosswache beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler die Schlosswache schon kennt oder nicht.
     */
    private Nominalphrase getSchlosswacheDescription() {
        return getSchlosswacheDescription(false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die die Schlosswache beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler die Schlosswache schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> die
     *                     Schlosswache schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    private Nominalphrase getSchlosswacheDescription(final boolean shortIfKnown) {
        return descriptionComp.getDescription(
                loadSC().memoryComp().isKnown(SCHLOSSWACHE), shortIfKnown);
    }
}