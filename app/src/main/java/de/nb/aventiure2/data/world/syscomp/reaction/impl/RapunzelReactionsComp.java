package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.data.world.time.AvTime.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.data.world.time.Tageszeit.*;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

/**
 * "Reaktionen" von Rapunzel, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, IRufReactions,
        IStateChangedReactions, ITimePassedReactions {
    private static final AvTimeSpan DAUER_WIE_LANGE_DIE_HAARE_MAX_UNTEN_BLEIBEN = mins(3);

    private final MemoryComp memoryComp;
    private final RapunzelStateComp stateComp;
    private final LocationComp locationComp;
    private final RapunzelTalkingComp talkingComp;

    public RapunzelReactionsComp(final AvDatabase db,
                                 final World world,
                                 final MemoryComp memoryComp,
                                 final RapunzelStateComp stateComp,
                                 final LocationComp locationComp,
                                 final RapunzelTalkingComp talkingComp) {
        super(RAPUNZEL, db, world);
        this.memoryComp = memoryComp;
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.talkingComp = talkingComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable,
                        final ILocationGO from,
                        @Nullable final ILocationGO to) {
    }

    @Override
    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }

        if (locatable.is(RAPUNZELS_ZAUBERIN)) {
            onZauberinEnter(from, to);
            return;
        }

        if (locatable.is(GOLDENE_KUGEL)) {
            onGoldeneKugelEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            onSCEnter_VorDemAltenTurm(from);
            return;
        }

        if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            onSCEnter_ObenImAltenTurm();
            return;
        }

        return;
    }

    private void onSCEnter_VorDemAltenTurm(@Nullable final ILocationGO from) {
        switch (stateComp.getState()) {
            case SINGEND:
                onSCEnter_VorDemAltenTurm_Singend(from);
                return;
            case HAARE_VOM_TURM_HERUNTERGELASSEN:
                onSCEnter_VorDemAltenTurm_HaareHeruntergelassen(from);
                return;
            default:
                // STORY Konzept dafür entwickeln, dass der Benutzer Rapunzel gut gelaunt
                //  verlässt und niedergeschlagen zu Rapunzel zurückkehrt und
                //  Rapunzel auf den Wechsel reagiert (Mental Model für Rapunzel?)
                return;
        }
    }

    private void onSCEnter_VorDemAltenTurm_Singend(@Nullable final ILocationGO from) {
        if (!world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            return;
        }

        loadSC().feelingsComp().setMoodMin(BEWEGT);

        if (!loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            n.add(neuerSatz(PARAGRAPH,
                    "Wie du näher kommst, hörst du einen Gesang, so lieblich, dass es "
                            + "dir das Herz rührt. Du hältst still und horchst: Kommt die "
                            + "Stimme aus dem kleinen Fensterchen oben im Turm?",
                    secs(20))
                    .beendet(PARAGRAPH));

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
            return;
        }
        n.addAlt(
                du("hörst",
                        "erneut die süße Stimme aus dem Turmfenster singen",
                        "erneut", secs(10)),
                du("hörst",
                        "es wieder von oben aus dem Turm singen",
                        "von oben aus dem Turm",
                        noTime()),
                du(PARAGRAPH, "hörst",
                        "wieder Gesang von oben",
                        "wieder",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz("Erneut hörst du den Gesang aus dem Turmfenster",
                        noTime())
        );

        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
    }

    private void onSCEnter_VorDemAltenTurm_HaareHeruntergelassen(
            @Nullable final ILocationGO from) {
        if (world.isOrHasRecursiveLocation(from, OBEN_IM_ALTEN_TURM)) {
            stateComp.narrateAndSetState(STILL);

            final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
            alt.addAll(altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm());
            // STORY Wenn Rapunzel das mit der Zauberin erzählt hat (aber auch dann nur
            //  einmal): „Aber komm nicht, wenn die Alte bei mir ist, ruft sie dir noch nach"
            //  (Die ist ein neuer RufTyp!)

            alt.add(neuerSatz(
                    "Als du unten bist, verschwinden die goldenen Haare "
                            + "wieder oben im Fenster",
                    secs(15))
                    .beendet(PARAGRAPH));

            n.addAlt(alt);

            return;
        }

        if (world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            loadSC().feelingsComp().setMoodMin(NEUTRAL);
            // STORY Andere und alternative Beschreibungen, wenn der SC
            //  Rapunzel schon kennengelernt hat
            n.add(neuerSatz("Aus dem kleinen "
                            + "Fenster oben im Turm hängen lange, goldene Haarzöpfe herab",
                    noTime()));

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_HAARE);
            return;
        }
    }

    private void onSCEnter_ObenImAltenTurm() {
        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            onSCEnter_ObenImAltenTurm_RapunzelUnbekannt();
            return;
        }

        onSCEnter_ObenImAltenTurm_RapunzelBekannt();
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelUnbekannt() {
        world.loadSC().memoryComp().upgradeKnown(RAPUNZEL);
        final Nominalphrase desc = getDescription();

        if (db.nowDao().now().getTageszeit() == NACHTS) {
            n.add(neuerSatz(
                    "Am Fenster sitzt eine junge Frau "
                            + "und schaut dich entsetzt an. Du hast sie wohl gerade aus tiefem "
                            + "Nachtschlaf geweckt. "
                            + capitalize(desc.nom())
                            + " ist in ein paar Decken gewickelt, "
                            + desc.possArt().vor(PL_MFN).akk() // "ihre"
                            + " langen Haare hat sie um einen Fensterhaken gewickelt, so "
                            + "konntest du "
                            + "daran heraufsteigen. Mit fahrigen Handbewegungen rafft "
                            + desc.persPron().nom() // "sie"
                            + " jetzt "
                            + desc.possArt().vor(PL_MFN).akk() // "ihre"
                            + " Haare zusammen, dann weicht "
                            + desc.persPron().nom() // "sie"
                            + " vor dir in das dunkle Zimmer zurück",
                    secs(25))
                    .phorikKandidat(desc, RAPUNZEL));

            stateComp.setState(STILL);
            memoryComp.upgradeKnown(SPIELER_CHARAKTER);

            loadSC().feelingsComp().setMoodMin(ANGESPANNT);

            return;
        }
        // Tagsüber
        n.add(neuerSatz(
                "Am Fenster sitzt eine junge Frau, so schön als "
                        + "du unter der Sonne noch keine gesehen hast. "
                        + "Ihre Haare, fein wie gesponnen "
                        + "Gold, hat sie um einen Fensterhaken gewickelt, so konntest du "
                        + "daran heraufsteigen.\n"
                        + capitalize(desc.nom())
                        + " erschrickt gewaltig, als du "
                        + PraepositionMitKasus.ZU.getDescription(desc.persPron()) // "zu ihr"
                        + " hereinkommst. Schnell bindet "
                        + desc.persPron().nom() // "sie"
                        + " "
                        + desc.possArt().vor(PL_MFN).akk() // "ihre"
                        + " Haare wieder zusammen, dann starrt "
                        + desc.persPron().nom() // "sie"
                        + " dich an",
                secs(20))
                .phorikKandidat(desc, RAPUNZEL));

        stateComp.setState(STILL);
        memoryComp.upgradeKnown(SPIELER_CHARAKTER);

        loadSC().feelingsComp().setMood(AUFGEDREHT);
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelBekannt() {
        loadSC().feelingsComp().setMoodMin(AUFGEDREHT);
        stateComp.setState(STILL);

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        if (db.nowDao().now().getTageszeit() == NACHTS) {
            alt.add(du(SENTENCE, "hast", "die junge Frau offenbar aus dem Bett "
                            + "geholt. Sie "
                            + "sieht sehr zerknittert "
                            + "aus, freut sich aber, dich zu sehen",
                    "offenbar",
                    secs(30))
                            .phorikKandidat(F, RAPUNZEL),
                    neuerSatz("Oben im dunklen Zimmer heißt dich die junge Frau "
                                    + "etwas überrascht willkommen",
                            secs(15))
                            .phorikKandidat(F, RAPUNZEL));
            if (loadSC().memoryComp().getKnown(RAPUNZEL) == KNOWN_FROM_LIGHT) {
                alt.add(neuerSatz("Sie ist auch nachts wunderschön – allerdings ist die "
                                + "junge, verschlafene "
                                + "Frau in ihren Decken auch sichtlich überrascht, dass zu "
                                + "dieser Nachtzeit noch einmal bei ihr vorbeischaust",
                        secs(15))
                        .phorikKandidat(F, RAPUNZEL));
            }
        } else {
            alt.add(
                    // STORY Wenn man sich noch nicht so kennt:
                    neuerSatz("Die junge Frau schaut dich überrascht und etwas "
                                    + "verwirrt an",
                            secs(40))
                            .phorikKandidat(F, RAPUNZEL)
//                    // STORY Dies nur, wenn man sich schon "duzt"
//                    du("findest",
//                            "oben die junge Frau ganz aufgeregt vor: „Du bist schon wieder "
//                                    + "da!”, sagt "
//                                    + "sie, „Kannst du mir nun helfen?”",
//                            "oben", secs(20))
//                            .phorikKandidat(F, RAPUNZEL),
//                    // STORY Dies nur, wenn man sich schon besser kennt
//                    neuerSatz("Die junge Frau ist gespannt, was du ihr zu berichten hast",
//                            secs(40))
//                            .phorikKandidat(F, RAPUNZEL),
//                    // STORY Dies nur, wenn Rapunzel schon von der Zauberin erzählt hat
//                    neuerSatz(
//                            "„Die Alte hat nichts bemerkt”, sprudelt die "
//                                    + "wunderschöne junge Frau los, „aber lange werden wir uns "
//                                    + "nicht treffen können. Sie ist so neugierig!”",
//                            secs(40))
//                            .phorikKandidat(F, RAPUNZEL)
            );

            if (loadSC().memoryComp().getKnown(RAPUNZEL) == KNOWN_FROM_DARKNESS) {
                alt.add(neuerSatz("Am Fenster sitzt die junge Frau, schön als "
                        + "du unter der Sonne noch keine gesehen hast. "
                        + "Ihre Haare glänzen fein wie gesponnen Gold. Sie ist "
                        + "glücklich, dich zu sehen", secs(30))
                        .phorikKandidat(F, RAPUNZEL));
            }
        }

        memoryComp.upgradeKnown(SPIELER_CHARAKTER);

        world.loadSC().memoryComp().upgradeKnown(RAPUNZEL);

        n.addAlt(alt);
    }


    private void onZauberinEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                from != null && from.is(VOR_DEM_ALTEN_TURM) &&
                to.is(OBEN_IM_ALTEN_TURM)) {
            rapunzelZiehtHaareWiederHoch();

            if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) &&
                    !world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
                n.add(neuerSatz(
                        "„Das ist also die Leiter, auf welcher man hinaufkommt!“, denkst du "
                                + "bei dir", secs(5))
                        .beendet(PARAGRAPH));

                world.loadSC().memoryComp().upgradeKnown(RAPUNZELRUF);
            }

            return;
        }

        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN) &&
                from != null && from.is(OBEN_IM_ALTEN_TURM) &&
                to.is(VOR_DEM_ALTEN_TURM)) {
            rapunzelZiehtHaareWiederHoch();
            return;
        }
    }

    private void onGoldeneKugelEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (!locationComp.hasSameUpperMostLocationAs(to)) {
            return;
        }

        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)
                // Der Spieler hat die goldene Kugel genommen, aufgefangen o.Ä.
                && !memoryComp.isKnown(GOLDENE_KUGEL)
            // und Rapunzel kennt die goldene Kugel noch nicht
        ) {
            rapunzelMoechteGoldeneKugelHaben();
            return;
        }

        // STORY Was, wenn die Kugel zu Boden fällt oder der SC sie hinlegt?
        //  "(Die junge Frau) hebt (die goldene Kugel) auf und..."
    }

    private void rapunzelMoechteGoldeneKugelHaben() {
        final SubstantivischePhrase desc =
                getAnaphPersPronWennMglSonstShortDescription();
        n.add(
                neuerSatz(desc.nom() +
                                " sieht interessiert zu. „Darf ich auch "
                                + "einmal?“, fragt " + desc.persPron().nom() + " dich",
                        secs(30))
                        .phorikKandidat(F, RAPUNZEL));

        memoryComp.upgradeKnown(GOLDENE_KUGEL);

        talkingComp.setTalkingTo(SPIELER_CHARAKTER);
        loadSC().talkingComp().setTalkingTo(RAPUNZEL);
    }

    private void rapunzelZiehtHaareWiederHoch() {
        stateComp.narrateAndSetState(STILL);

        // TODO SC erlebt das von OBEN_IM_TURM mit.
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            n.addAlt(altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm());
        }
    }

    @NonNull
    private static ImmutableList<AbstractDescription<?>>
    altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm() {
        return ImmutableList.of(
                neuerSatz(
                        "Dann verschwinden die prächtigen Haare wieder oben im Fenster",
                        secs(15))
                        .beendet(PARAGRAPH),
                du("schaust",
                        "fasziniert zu, wie die langen Haare wieder in "
                                + "das Turmfenster "
                                + "zurückgezogen werden",
                        "fasziniert",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz("Nur ein paar Augenblicke, dann sind die Haare "
                                + "wieder oben im Fenster verschwunden",
                        secs(10))
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public void onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        // Hört Rapunzel den Ruf?
        if (!locationComp.hasSameUpperMostLocationAs(rufer) &&
                (!rufer.locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) ||
                        !locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM))) {
            return;
        }

        if (ruftyp == Ruftyp.LASS_DEIN_HAAR_HERUNTER) {
            onRapunzelruf(rufer);
            return;
        }
    }

    public void onRapunzelruf(final ILocatableGO rufer) {
        if (!stateComp.hasState(SINGEND, STILL)) {
            return;
        }

        if (loadZauberin().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            return;
        }

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (stateComp.hasState(SINGEND)) {
                if (world.loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                    n.add(
                            neuerSatz(
                                    "Sofort hört der Gesang auf – und gleich darauf fallen "
                                            + "aus dem kleinen "
                                            + "Fenster oben im Turm lange, goldene Haarzöpfe herab, "
                                            + "sicher zwanzig Ellen tief bis auf den Boden",
                                    secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                } else {
                    n.add(
                            neuerSatz(
                                    "Der Gesang hört auf, und wieder fallen "
                                            + "die wunderschönen goldenen Haare aus dem Fenster "
                                            + "bis ganz auf den Boden",
                                    secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                }

                world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
            } else {
                if (world.loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                    n.add(
                            neuerSatz("Wieder fallen die langen, golden "
                                    + "glänzenden Zöpfe aus dem "
                                    + "Fenster bis zum Boden herab", secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                } else {
                    n.add(
                            neuerSatz("Gleich darauf fallen aus dem kleinen "
                                    + "Fenster oben im Turm lange, goldene Haarzöpfe herab, sicher "
                                    + "zwanzig Ellen tief bis auf den Boden", secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                }
            }

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_HAARE);
            stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
            return;
        }

        // Sonderfall: Rapunzel verzögert das Haare-Herunterlassen
        if (loadSC().locationComp().hasLocation(OBEN_IM_ALTEN_TURM)) {
            n.addAlt(
                    neuerSatz(
                            "„O weh, die Alte kommt!”, entfährt es der jungen "
                                    + "Frau. „Du musst dich verstecken! Sie "
                                    + "ist eine mächtige Zauberin!”",
                            secs(10)),
                    neuerSatz(
                            "„O nein, die Alte kommt schon wieder!”, sagt "
                                    + "die junge Frau entsetzt. „Versteck dich "
                                    + "schnell!”",
                            secs(15)),
                    neuerSatz("Alarmiert schaut die junge Frau dich an. Dann wandert "
                                    + "ihr Blick "
                                    + "auf das Bett",
                            secs(20))
                            .phorikKandidat(F, RAPUNZEL)
                    // Hier wäre "dann" nur sinnvoll, wenn Rapunzel etwas tut, nicht der SC
            );
            return;
        }

        stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
    }

    @Override
    public void onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                               final Enum<?> newState) {
        if (gameObject.is(RAPUNZELS_ZAUBERIN)) {
            onZauberinStateChanged(
                    (RapunzelsZauberinState) oldState, (RapunzelsZauberinState) newState);
            return;
        }
    }

    private void onZauberinStateChanged(
            final RapunzelsZauberinState oldState, final RapunzelsZauberinState newState) {
        if (newState == RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL) {
            onZauberinStateChangedToAufDemRueckwegVonRapunzel();
            return;
        }
    }

    private void onZauberinStateChangedToAufDemRueckwegVonRapunzel() {
        if (loadZauberin().locationComp()
                .hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                stateComp.hasState(SINGEND, STILL)) {
            stateComp.rapunzelLaesstHaareZumAbstiegHerunter();
            return;
        }
    }

    @Override
    public void onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN) &&
                now.isAfter(
                        stateComp.getStateDateTime().plus(
                                DAUER_WIE_LANGE_DIE_HAARE_MAX_UNTEN_BLEIBEN))) {
            rapunzelZiehtHaareWiederHoch();
            return;
        }

        if (rapunzelMoechteSingen(now)) {
            onTimePassed_RapunzelMoechteSingen(lastTime, now);
            return;
        }

        onTimePassed_RapunzelMoechteNichtSingen(lastTime, now);
    }

    private boolean rapunzelMoechteSingen(final AvDateTime now) {
        if (!stateComp.hasState(STILL, SINGEND)) {
            return false;
        }

        // Rapunzel singt nur, wenn es denkt, niemand wäre in der Gegend.
        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM) ||
                loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN))
                .locationComp().hasLocation(VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN)).locationComp()
                .hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            // Während Rapunzel von der Zauberin Besuch hat, singt sie nicht
            return false;
        }

        // Ansonsten singt Rapunzel innerhalb gewisser Zeiten immer mal wieder
        return now.getTageszeit().getLichtverhaeltnisseDraussen() == HELL &&
                !isZeitFuerMittagsruhe(now) &&
                immerMalWieder(now);
    }

    private static boolean isZeitFuerMittagsruhe(final AvDateTime now) {
        return now.getTime().isWithin(oClock(1), oClock(2, 30));
    }

    private static boolean immerMalWieder(final AvDateTime now) {
        return now.getTime().isInRegularTimeIntervalIncl(
                // Ab...
                oClock(7),
                // ... immer für ...
                mins(10),
                // ... Minuten mit
                mins(25),
                // ... Minuten Pause danach - bis um
                oClock(19));
    }

    private void onTimePassed_RapunzelMoechteSingen(final AvDateTime lastTime,
                                                    final AvDateTime now) {
        if (stateComp.hasState(STILL)) {
            stateComp.narrateAndSetState(SINGEND);
            onTimePassed_moechteSingen_bislangStill();
            return;
        }

        // Rapunzel hat schon die ganze Zeit gesungen
    }

    private void onTimePassed_moechteSingen_bislangStill() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return;
        }

        loadSC().feelingsComp().setMoodMin(BEWEGT);

        if (!loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            n.add(neuerSatz(PARAGRAPH,
                    "Auf einmal hebt ein Gesang an, so lieblich, dass es dir das "
                            + "Herz rührt. Du hältst still und horchst: Kommt die Stimme aus "
                            + "dem kleinen Fensterchen oben im Turm?",
                    secs(20)));

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
            return;
        }

        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            n.addAlt(
                    du("hörst",
                            "erneut die süße Stimme aus dem Turmfenster singen",
                            "erneut", secs(10)),
                    du("hörst",
                            "es von oben aus dem Turm singen",
                            "von oben aus dem Turm",
                            noTime()),
                    du(PARAGRAPH, "hörst",
                            "wieder Gesang von oben schallen",
                            "wieder",
                            noTime())
                            .beendet(PARAGRAPH),
                    neuerSatz(PARAGRAPH, "Plötzlich erschallt über dir wieder Gesang",
                            noTime()),
                    du("hörst",
                            "den Gesang erneut",
                            "erneut",
                            noTime())
            );

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);

            return;
        }

        n.addAlt(
                du(SENTENCE, "hörst",
                        "aus dem Turmfenster die junge Frau singen. Dir wird ganz "
                                + "warm beim Zuhören",
                        "aus dem Turmfenster", secs(10))
                        .undWartest()
                        .phorikKandidat(F, RAPUNZEL),
                du(SENTENCE, "hörst",
                        "plötzlich wieder Gesang aus dem Turmfenster. Wann wirst du "
                                + "die junge Frau "
                                + "endlich retten können?",
                        "plötzlich",
                        noTime())
                        .beendet(PARAGRAPH)
                        .phorikKandidat(F, RAPUNZEL),
                du("hörst",
                        "erneut die süße Stimme aus dem Turmfenster singen. Jetzt "
                                + "weißt du "
                                + "endlich, wer dort singt – und sein Vertrauen in dich setzt",
                        "erneut",
                        noTime())
        );

        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
    }

    private void onTimePassed_RapunzelMoechteNichtSingen(final AvDateTime lastTime,
                                                         final AvDateTime now) {
        if (stateComp.hasState(SINGEND)) {
            stateComp.narrateAndSetState(STILL);
            onTimePassed_moechteNichtMehrSingen_bislangGesungen();
            return;
        }

        // Rapunzel hat schon die ganze Zeit nicht gesungen
    }

    private void onTimePassed_moechteNichtMehrSingen_bislangGesungen() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return;
        }

        loadSC().feelingsComp().setMoodMin(BEWEGT);

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(
                neuerSatz("Plötzlich endet der Gesang",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz("Plötzlich wird es still",
                        noTime()),
                neuerSatz(PARAGRAPH,
                        "Nun hat der Gesang geendet - wie gern würdest noch länger "
                                + "zuhören!",
                        noTime())
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Nun ist es wieder still",
                        noTime()),
                neuerSatz(PARAGRAPH,
                        "Jetzt hat der süße Gesang aufgehört",
                        noTime()),
                neuerSatz(PARAGRAPH,
                        "Jetzt ist es wieder still. Dein Herz ist noch ganz bewegt",
                        noTime())
                        .beendet(PARAGRAPH));
        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Auf einmal ist nichts mehr zu hören. Es lässt dir keine Ruhe: "
                                    + "Wer mag dort oben so lieblich singen?",
                            noTime())
                            .beendet(PARAGRAPH));
        }

        n.addAlt(alt);

        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
    }

    @NonNull
    private ILocatableGO loadZauberin() {
        return (ILocatableGO) world.load(RAPUNZELS_ZAUBERIN);
    }
}