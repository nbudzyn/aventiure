package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.ISCActionDoneListenerComponent;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelsZauberinTalkingComp;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp.Counter.BEGRUESSUNG_DU_ALTE_IST_SO_NEUGIERIG;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp.Counter.BEGRUESSUNG_KANNST_DU_MIR_NUN_HELFEN;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp.Counter.NACHGERUFEN_KOMM_NICHT_WENN_DIE_ALTE_DA_IST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.DO_START_HAARE_VOM_TURM_HERUNTERLASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_BIENEN_UND_BLUMEN_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_HERKUNFT_DER_GOLDENEN_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.NORMAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.altDannHaareFestbinden;
import static de.nb.aventiure2.german.base.Nominalphrase.FUSS;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altSaetze;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SCHIEBEN;
import static java.util.stream.Collectors.toList;

/**
 * "Reaktionen" von Rapunzel, z.B. darauf, dass Zeit vergeht
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class RapunzelReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, IRufReactions,
        IStateChangedReactions, ITimePassedReactions, ISCActionDoneListenerComponent {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        NACHGERUFEN_KOMM_NICHT_WENN_DIE_ALTE_DA_IST,
        BEGRUESSUNG_KANNST_DU_MIR_NUN_HELFEN,
        BEGRUESSUNG_DU_ALTE_IST_SO_NEUGIERIG
    }

    private static final AvTimeSpan DAUER_WIE_LANGE_DIE_HAARE_MAX_UNTEN_BLEIBEN = mins(3);

    private final TimeTaker timeTaker;
    private final MemoryComp memoryComp;
    private final RapunzelStateComp stateComp;
    private final LocationComp locationComp;
    private final FeelingsComp feelingsComp;
    private final RapunzelTalkingComp talkingComp;

    public RapunzelReactionsComp(final CounterDao counterDao,
                                 final TimeTaker timeTaker, final Narrator n,
                                 final World world,
                                 final MemoryComp memoryComp,
                                 final RapunzelStateComp stateComp,
                                 final LocationComp locationComp,
                                 final FeelingsComp feelingsComp,
                                 final RapunzelTalkingComp talkingComp) {
        super(RAPUNZEL, counterDao, n, world);
        this.timeTaker = timeTaker;
        this.memoryComp = memoryComp;
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.feelingsComp = feelingsComp;
        this.talkingComp = talkingComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable,
                        final ILocationGO from,
                        @Nullable final ILocationGO to) {
        talkingComp.updateSchonBegruesstMitSCOnLeave(locatable, from, to);
    }

    @Override
    public boolean verbirgtSichVorEintreffendemSC() {
        return false;
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

        if (talkingComp.scUndRapunzelKoennenEinanderSehen()
                && locationComp.hasSameOuterMostLocationAs(to)
                && world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)
                && locatable.is(GOLDENE_KUGEL)
                // Der Spieler hat die goldene Kugel genommen, aufgefangen o.Ä.
                && !memoryComp.isKnown(GOLDENE_KUGEL)
                // und Rapunzel kennt die goldene Kugel noch nicht
                && stateComp.hasState(NORMAL)
            // und Rapunzel hat nicht gerade die Haare heruntergelassen o.Ä.
        ) {
            rapunzelMoechteGoldeneKugelHaben();
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, VOR_DEM_ALTEN_TURM)) {
            onSCEnter_VorDemAltenTurm(from, to);
            return;
        }

        if (world.isOrHasRecursiveLocation(to, OBEN_IM_ALTEN_TURM)) {
            onSCEnter_ObenImAltenTurm(from, to);
            return;
        }

        return;
    }

    private void onSCEnter_VorDemAltenTurm(@Nullable final ILocationGO from,
                                           final ILocationGO to) {
        switch (stateComp.getState()) {
            case UNAEUFFAELLIG:
                onSCEnter_VorDemAltenTurm_Unauffaellig(to);
                return;
            case SINGEND:
                onSCEnter_VorDemAltenTurm_Singend(from);
                return;
            case HAARE_VOM_TURM_HERUNTERGELASSEN:
                onSCEnter_VorDemAltenTurm_HaareHeruntergelassen(from);
                return;
            default:
                return;
        }
    }

    private void onSCEnter_VorDemAltenTurm_Unauffaellig(final ILocationGO to) {
        if (to.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            stateComp.narrateAndSetState(NORMAL);
            // Ab jetzt wird Rapunzel hin und wieder singen.
        }
    }

    private void onSCEnter_VorDemAltenTurm_Singend(@Nullable final ILocationGO from) {
        if (!world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            return;
        }

        loadSC().feelingsComp().requestMoodMin(BEWEGT);

        if (!loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            n.narrate(neuerSatz(PARAGRAPH,
                    "Wie du näher kommst, hörst du einen Gesang, so lieblich, dass es "
                            + "dir das Herz rührt. Du hältst still und horchst: Kommt die "
                            + "Stimme aus dem kleinen Fensterchen oben im Turm?", PARAGRAPH)
                    .timed(secs(20)));

            world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);
            return;
        }
        n.narrateAlt(
                du("hörst", "erneut die süße Stimme aus dem Turmfenster singen")
                        .mitVorfeldSatzglied("erneut")
                        .timed(secs(10)),
                du("hörst", "es wieder von oben aus dem Turm singen")
                        .mitVorfeldSatzglied("von oben aus dem Turm")
                        .timed(NO_TIME),
                du(PARAGRAPH, "hörst", "wieder Gesang von oben", PARAGRAPH)
                        .mitVorfeldSatzglied("wieder")
                        .timed(NO_TIME),
                neuerSatz("Erneut hörst du den Gesang aus dem Turmfenster")
                        .timed(NO_TIME)
        );

        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);
    }

    private void onSCEnter_VorDemAltenTurm_HaareHeruntergelassen(
            @Nullable final ILocationGO from) {
        if (world.isOrHasRecursiveLocation(from, OBEN_IM_ALTEN_TURM)) {
            stateComp.narrateAndSetState(NORMAL);

            final AltTimedDescriptionsBuilder alt = altTimed();
            alt.addAll(altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm());

            if (counterDao.get(NACHGERUFEN_KOMM_NICHT_WENN_DIE_ALTE_DA_IST) == 0
                    && counterDao.get(
                    RapunzelTalkingComp.Counter.HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT)
                    > 0) {
                final SubstantivischePhrase anaph = anaph();

                alt.add(neuerSatz("Als",
                        anaph.persPron().nomK(), // "sie"
                        anaph.persPron().possArt().vor(N).akkStr(), // "ihr"
                        "Haar wieder heraufgerafft hat, ruft",
                        anaph.nomK(), // "Rapunzel"
                        "dir noch nach: „Aber komm nicht, wenn die Alte bei mir ist!“",
                        "Danach sieht der Turm wieder verlassen und unbewohnt aus", PARAGRAPH)
                        .timed(secs(20))
                        .withCounterIdIncrementedIfTextIsNarrated(
                                NACHGERUFEN_KOMM_NICHT_WENN_DIE_ALTE_DA_IST));
            }

            alt.add(neuerSatz("Als du unten bist, verschwinden die goldenen Haare "
                    + "wieder oben im Fenster", PARAGRAPH)
                    .timed(secs(15)));

            n.narrateAlt(alt);
            return;
        }

        if (world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            loadSC().feelingsComp().requestMoodMin(NEUTRAL);
            // FIXME Andere und alternative Beschreibungen, wenn der SC
            //  Rapunzel schon kennengelernt hat
            n.narrate(neuerSatz("Aus dem kleinen "
                    + "Fenster oben im Turm hängen lange, goldene Haarzöpfe herab")
                    .timed(NO_TIME));

            world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_HAARE);
            return;
        }
    }

    private void onSCEnter_ObenImAltenTurm(@Nullable final ILocationGO from,
                                           final ILocationGO to) {
        if (!world.isOrHasRecursiveLocation(from, BETT_OBEN_IM_ALTEN_TURM)
                && to.is(BETT_OBEN_IM_ALTEN_TURM)) {
            if (!stateComp.hasState(PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN)) {
                onSCEnter_UnterBettObenImAltenTurm_unnuetz();
                return;
            }

            return;
        }

        if (LocationSystem.haveSameOuterMostLocation(from, to)) {
            return;
        }

        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            onSCEnter_ObenImAltenTurm_RapunzelUnbekannt();
            return;
        }

        onSCEnter_ObenImAltenTurm_RapunzelBekannt();
    }

    private void onSCEnter_UnterBettObenImAltenTurm_unnuetz() {
        final int zuneigungRapunzelZumSc =
                feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);

        final AltDescriptionsBuilder alt = alt();

        if (zuneigungRapunzelZumSc <= -FeelingIntensity.DEUTLICH) {
            alt.add(neuerSatz("„Was ist denn jetzt wieder los?“, fragt",
                    anaph().nomK(), "genervt"));
        }

        if (zuneigungRapunzelZumSc >= -FeelingIntensity.DEUTLICH
                && zuneigungRapunzelZumSc <= FeelingIntensity.NEUTRAL) {
            alt.add(neuerSatz("„Was soll das jetzt?“, fragt", anaph().nomK()));
        }

        if (zuneigungRapunzelZumSc >= -FeelingIntensity.MERKLICH) {
            alt.add(neuerSatz("„Was ist jetzt los?“, fragt", anaph().nomK()));
        }

        if (zuneigungRapunzelZumSc >= FeelingIntensity.NUR_LEICHT) {
            alt.add(neuerSatz("„Alles gut?“, hörst du", anaph().nomK(), "fragen"));
        }

        n.narrateAlt(alt, secs(10));
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelUnbekannt() {
        if (timeTaker.now().getTageszeit() == NACHTS) {
            onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_nachts();
            return;
        }

        onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_tagsueber();
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_tagsueber() {
        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZEL);
        final Nominalphrase desc = getDescription();

        // "zu ihr"
        // "sie"
        // "ihre"
        // "sie"
        n.narrate(neuerSatz("Am Fenster sitzt eine junge Frau, so schön als",
                "du unter der Sonne noch keine gesehen hast.",
                "Ihre Haare, fein wie gesponnen",
                "Gold, hat sie um einen Fensterhaken gewickelt, so konntest du",
                "daran heraufsteigen",
                PARAGRAPH,
                desc.nomK(),
                "erschrickt gewaltig, als du",
                PraepositionMitKasus.ZU.getDescription(desc.persPron()), // "zu ihr"
                "hereinkommst. Schnell bindet",
                desc.persPron().nomK(), // "sie"
                desc.possArt().vor(PL_MFN).akkStr(), // "ihre"
                "Haare wieder zusammen, dann starrt",
                desc.persPron().nomK(), // "sie"
                "dich an")
                .timed(secs(20)));

        stateComp
                .setState(NORMAL);
        memoryComp.narrateAndUpgradeKnown(SPIELER_CHARAKTER);

        // Rapunzel ist erst einmal verschreckt.
        feelingsComp.upgradeFeelingsTowards(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                -1, FeelingIntensity.MERKLICH);

        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            // Jetzt weiß der SC, wer so schön gesungen hat! Und schön ist sie obendrein noch!
            loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                    ZUNEIGUNG_ABNEIGUNG, 2, FeelingIntensity.STARK);
        }

        loadSC().feelingsComp().requestMood(BEWEGT);
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_nachts() {
        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZEL);
        final Nominalphrase desc = getDescription();

        n.narrate(neuerSatz("Am Fenster sitzt eine junge Frau",
                "und schaut dich entsetzt an. Du hast sie wohl gerade aus tiefem",
                "Nachtschlaf geweckt",
                SENTENCE,
                desc.nomK(),
                "ist in ein paar Decken gewickelt,",
                desc.possArt().vor(PL_MFN).akkStr(), // "ihre"
                "langen Haare hat sie um einen Fensterhaken gewickelt, so",
                "konntest du",
                "daran heraufsteigen. Mit fahrigen Handbewegungen rafft",
                desc.persPron().nomK(), // "sie
                "jetzt",
                desc.possArt().vor(PL_MFN).akkStr(), // "ihre"
                "Haare zusammen, dann weicht",
                desc.persPron().nomK(), // "sie"
                "vor dir in das dunkle Zimmer zurück")
                .timed(secs(25)));

        stateComp.setState(NORMAL);
        memoryComp.narrateAndUpgradeKnown(SPIELER_CHARAKTER);

        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            // Jetzt weiß der SC, wer so schön gesungen hat!
            loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                    ZUNEIGUNG_ABNEIGUNG, 0.5f, FeelingIntensity.DEUTLICH);
        }
        loadSC().feelingsComp().requestMoodMin(ANGESPANNT);

        feelingsComp.upgradeFeelingsTowards(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                -3, FeelingIntensity.STARK);
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelBekannt() {
        loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);
        stateComp
                .setState(NORMAL);

        if (timeTaker.now().getTageszeit() == NACHTS) {
            narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Nachts();
        } else {
            narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Tagsueber();
        }

        memoryComp.narrateAndUpgradeKnown(SPIELER_CHARAKTER);

        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZEL);
    }

    private void narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Tagsueber() {
        final SubstantivischePhrase anaph = anaph(true);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                1f, FeelingIntensity.MERKLICH);
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 0.75f, FeelingIntensity.DEUTLICH);

        final int zuneigungSCTowardsRapunzel =
                loadSC().feelingsComp().getFeelingTowardsForActionsMitEmpathischerSchranke(
                        RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
        if (talkingComp.duzen()
                && zuneigungSCTowardsRapunzel >= FeelingIntensity.MERKLICH
                && counterDao.get(
                RapunzelTalkingComp.Counter.HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT)
                > 0) {

            final AltTimedDescriptionsBuilder alt = altTimed();

            if (counterDao.get(BEGRUESSUNG_KANNST_DU_MIR_NUN_HELFEN) == 0) {
                alt.add(du("findest", "oben", anaph.akkK(),
                        "ganz aufgeregt vor: „Da bist du wieder!“, sagt",
                        anaph.persPron().nomK(),
                        ",",
                        "„Kannst du mir nun helfen?“")
                        .mitVorfeldSatzglied("oben")
                        .schonLaenger()
                        .timed(secs(15))
                        .withCounterIdIncrementedIfTextIsNarrated(
                                BEGRUESSUNG_KANNST_DU_MIR_NUN_HELFEN));
            }

            if (counterDao.get(BEGRUESSUNG_DU_ALTE_IST_SO_NEUGIERIG) == 0) {
                alt.add(neuerSatz(
                        "„Die Alte hat nichts bemerkt“, sprudelt die wunderschöne",
                        "junge Frau los, „aber lange werden wir uns",
                        "nicht treffen können. Sie ist so neugierig!“")
                        .timed(secs(15))
                        .withCounterIdIncrementedIfTextIsNarrated(
                                BEGRUESSUNG_DU_ALTE_IST_SO_NEUGIERIG));
            }

            if (!alt.isEmpty()) {
                n.narrateAlt(alt);
                talkingComp.setTalkingTo(SPIELER_CHARAKTER);
                return;
            }
        }

        final AltTimedDescriptionsBuilder alt = altTimed();

        final ImmutableList<Satz> altReaktionSaetze =
                feelingsComp.altReaktionBeiBegegnungMitScSaetze(anaph);

        alt.addAll(altSaetze(altReaktionSaetze).schonLaenger().timed(secs(5)));

        if (loadSC().memoryComp().getKnown(RAPUNZEL) == KNOWN_FROM_DARKNESS) {
            alt.addAll(altNeueSaetze("Am Fenster sitzt die junge Frau, schön als",
                    "du unter der Sonne noch keine gesehen hast.",
                    "Ihre Haare glänzen fein wie gesponnen Gold",
                    SENTENCE,
                    altReaktionSaetze)
                    .timed(secs(30)));
        }
        n.narrateAlt(alt);
    }

    private void narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Nachts() {
        final AltTimedDescriptionsBuilder alt = altTimed();

        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 0.5f, FeelingIntensity.DEUTLICH);

        final SubstantivischePhrase anaph = anaph(true);

        final ImmutableList<Satz> altReaktionSaetze =
                feelingsComp.altReaktionBeiBegegnungMitScSaetze(anaph);

        alt.addAll(altSaetze(altReaktionSaetze).schonLaenger().timed(secs(5)));

        alt.addAll(altSaetze(
                feelingsComp.altSCBeiBegegnungAnsehenSaetze(anaph).stream()
                        // Diese Sätze sind bereits in altZuneigungAbneigungSaetze enthalten...
                        // ...aber noch nicht mit dieser Ergänzung:
                        .map(s -> s.mitAdverbialerAngabe(
                                new AdverbialeAngabeSkopusSatz("oben im dunklen Zimmer")))
                        .collect(toList())).schonLaenger()
                .timed(secs(15)));

        alt.add(du(SENTENCE, "hast", anaph.akkK(),
                "offenbar aus dem Bett geholt",
                SENTENCE,
                anaph.persPron().nomK(), // Sie
                "sieht sehr zerknittert aus")
                .mitVorfeldSatzglied("offenbar")
                .schonLaenger()
                .timed(secs(30)));
        if (loadSC().memoryComp().getKnown(RAPUNZEL) == KNOWN_FROM_LIGHT) {
            alt.add(neuerSatz(anaph.persPron().nomK(),
                    "ist auch nachts wunderschön – allerdings ist die",
                    "junge, verschlafene",
                    "Frau in ihren Decken auch sichtlich überrascht, dass du zu",
                    "dieser Nachtzeit noch einmal bei ihr vorbeischaust")
                    .timed(secs(15))
                    .phorikKandidat(F, RAPUNZEL));
        }
        n.narrateAlt(alt);
    }

    private void onZauberinEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(from, VOR_DEM_ALTEN_TURM)
                && world.isOrHasRecursiveLocation(to, OBEN_IM_ALTEN_TURM)) {
            onZauberinEnterFromVorTurmToOben();
            return;
        }

        if (world.isOrHasRecursiveLocation(from, OBEN_IM_ALTEN_TURM)
                && to.is(VOR_DEM_ALTEN_TURM)
                && stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            rapunzelZiehtHaareWiederHoch();
            return;
        }
    }

    private void onZauberinEnterFromVorTurmToOben() {
        if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            rapunzelZiehtHaareWiederHoch();

            if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) &&
                    !world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
                n.narrate(neuerSatz("„Das ist also die Leiter, auf welcher man hinaufkommt!“, "
                        + "denkst du bei dir", PARAGRAPH)
                        .timed(secs(5)));

                world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELRUF);
                return;
            }

            if (loadSC().locationComp().hasRecursiveLocation(BETT_OBEN_IM_ALTEN_TURM)
                    && feelingsComp.getFeelingTowards(
                    SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG) <= -FeelingIntensity.MERKLICH) {
                n.narrate(neuerSatz(
                        "„Unter meinem Bett liegt ein Einbrecher“, hörst du",
                        world.getDescription(RAPUNZEL).akkK(),
                        "sagen").timed(secs(5)));

                loadZauberin().talkingComp().zauberinZaubertVergessenszauber();
                return;
            }
        }
    }

    private void rapunzelMoechteGoldeneKugelHaben() {
        final SubstantivischePhrase anaph = anaph();
        n.narrate(neuerSatz(anaph.nomK(),
                "sieht interessiert zu. „Darf ich auch",
                "einmal?“, fragt", anaph.persPron().nomK(), "dich")
                .timed(secs(30)));

        memoryComp.narrateAndUpgradeKnown(GOLDENE_KUGEL);

        talkingComp.setTalkingTo(SPIELER_CHARAKTER);
        stateComp.narrateAndSetState(HAT_NACH_KUGEL_GEFRAGT);
    }

    private void rapunzelZiehtHaareWiederHoch() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            n.narrateAlt(altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm());
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)
                && talkingComp.scUndRapunzelKoennenEinanderSehen()) {
            n.narrateAlt(altRapunzelZiehtHaareWiederHoch_ObenImAltenTurm());
        }

        stateComp.narrateAndSetState(NORMAL);
    }

    @NonNull
    private static ImmutableList<TimedDescription<?>>
    altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm() {
        return ImmutableList.of(
                neuerSatz("Dann verschwinden die prächtigen Haare wieder oben im Fenster",
                        PARAGRAPH)
                        .timed(secs(15)),
                du("schaust", "fasziniert zu, wie die langen Haare wieder in "
                        + "das Turmfenster "
                        + "zurückgezogen werden", PARAGRAPH).mitVorfeldSatzglied("fasziniert")
                        .schonLaenger()
                        .timed(secs(15)),
                neuerSatz("Nur ein paar Augenblicke, dann sind die Haare "
                        + "wieder oben im Fenster verschwunden", PARAGRAPH)
                        .timed(secs(10))
        );
    }

    private ImmutableList<TimedDescription<?>>
    altRapunzelZiehtHaareWiederHoch_ObenImAltenTurm() {
        final SubstantivischePhrase anaph = anaph(false);

        return ImmutableList.of(
                neuerSatz("Jetzt zieht",
                        anaph.nomK(), // "die junge Frau"
                        anaph.possArt().vor(PL_MFN).akkStr(), // "ihre"
                        "Haare wieder hoch", PARAGRAPH)
                        .timed(secs(15)),
                neuerSatz("Die Haare zieht",
                        anaph.nomK(),
                        "wieder hoch", PARAGRAPH)
                        .timed(secs(15))
        );
    }

    @Override
    public void onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        // Hört Rapunzel den Ruf?
        if (!locationComp.hasSameOuterMostLocationAs(rufer) &&
                (!rufer.locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) ||
                        !locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM))) {
            return;
        }

        if (ruftyp == Ruftyp.LASS_DEIN_HAAR_HERUNTER) {
            onRapunzelruf();
            return;
        }
    }

    private void onRapunzelruf() {
        if (!stateComp.hasState(SINGEND,
                NORMAL,
                HAT_NACH_KUGEL_GEFRAGT,
                HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT,
                HAT_NACH_HERKUNFT_DER_GOLDENEN_KUGEL_GEFRAGT,
                HAT_NACH_BIENEN_UND_BLUMEN_GEFRAGT)) {
            return;
        }

        if (loadZauberin().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            return;
        }

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                    >= -FeelingIntensity.NUR_LEICHT) {
                rapunzelSchiebtFremdeGegenstaendeUntersBett();
            }

            if (stateComp.hasState(SINGEND)) {
                if (world.loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                    n.narrate(
                            neuerSatz("Sofort hört der Gesang auf – und gleich darauf "
                                    + "fallen aus dem kleinen "
                                    + "Fenster oben im Turm lange, goldene Haarzöpfe "
                                    + "herab, sicher zwanzig Ellen tief bis auf den Boden")
                                    .timed(secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                } else {
                    n.narrate(
                            neuerSatz("Der Gesang hört auf, und wieder fallen "
                                    + "die wunderschönen goldenen Haare aus dem Fenster "
                                    + "bis ganz auf den Boden")
                                    .timed(secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                }

                world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);
            } else {
                if (world.loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                    n.narrate(
                            neuerSatz("Wieder fallen die langen, golden "
                                    + "glänzenden Zöpfe aus dem "
                                    + "Fenster bis zum Boden herab")
                                    .timed(secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                } else {
                    n.narrate(
                            neuerSatz("Gleich darauf fallen aus dem kleinen "
                                    + "Fenster oben im Turm lange, goldene Haarzöpfe herab, "
                                    + "sicher zwanzig Ellen tief bis auf den Boden")
                                    .timed(secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                }
            }

            world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_HAARE);
            stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
            return;
        }

        if (loadSC().locationComp().hasLocation(OBEN_IM_ALTEN_TURM) &&
                feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                        >= -FeelingIntensity.MERKLICH) {
            if (!loadSC().locationComp().hasRecursiveLocation(BETT_OBEN_IM_ALTEN_TURM)) {
                rapunzelVerzoegertHaareHerunterlassen();
                return;
            }
        }

        if (feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                >= -FeelingIntensity.NUR_LEICHT) {
            rapunzelSchiebtFremdeGegenstaendeUntersBett();
        }

        stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
    }

    private void rapunzelVerzoegertHaareHerunterlassen() {
        talkingComp.narrateOWehZauberinKommt();

        rapunzelSchiebtFremdeGegenstaendeUntersBett();

        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 0.5f, FeelingIntensity.DEUTLICH);

        stateComp.narrateAndSetState(PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN);
    }

    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    void rapunzelSchiebtFremdeGegenstaendeUntersBett() {
        final ImmutableList<LOC_DESC> gegenstaendeFuerUntersBett =
                world.<LOC_DESC>loadDescribableNonLivingMovableInventory(OBEN_IM_ALTEN_TURM)
                        .stream()
                        .filter(o -> !o.locationComp().hasRecursiveLocation(
                                BETT_OBEN_IM_ALTEN_TURM, SPIELER_CHARAKTER, RAPUNZEL))
                        .collect(toImmutableList());
        if (gegenstaendeFuerUntersBett.isEmpty()) {
            return;
        }

        n.narrate(neuerSatz(SCHIEBEN
                .mit(world.getDescriptionSingleOrReihung(gegenstaendeFuerUntersBett))
                .mitAdverbialerAngabe(
                        new AdverbialeAngabeSkopusVerbWohinWoher(UNTER.mit(
                                world.getDescription(BETT_OBEN_IM_ALTEN_TURM))))
                .mitAdverbialerAngabe(
                        new AdverbialeAngabeSkopusVerbAllg(MIT_DAT.mit(FUSS)))
                .alsSatzMitSubjekt(anaph()))
                .timed(secs(5)));

        for (final LOC_DESC object : gegenstaendeFuerUntersBett) {
            object.locationComp().narrateAndSetLocation(BETT_OBEN_IM_ALTEN_TURM);
        }
    }

    @Override
    public void onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                               final Enum<?> newState) {
        if (gameObject.is(RAPUNZELS_ZAUBERIN)) {
            onZauberinStateChanged((RapunzelsZauberinState) newState);
            return;
        }
    }

    private void onZauberinStateChanged(final RapunzelsZauberinState newState) {
        if (newState == RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL) {
            onZauberinStateChangedToAufDemRueckwegVonRapunzel();
            return;
        }
    }

    private void onZauberinStateChangedToAufDemRueckwegVonRapunzel() {
        if (loadZauberin().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                !stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            talkingComp
                    .rapunzelLaesstHaareZumAbstiegHerunterBzwGibtDemSCNochZeitZumVerstecken();
            return;
        }
    }

    void reagiertAufFrageVonZauberinNachGeruch() {
        if (feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                >= FeelingIntensity.NEUTRAL) {
            n.narrate(neuerSatz("„Oh, das… müssen wieder die Fledermäuse sein!“, sagt",
                    anaph().nomK(),
                    "und stellt sich vor das Bett. Dir pocht das Herz")
                    .timed(secs(20)));
        }
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {

        if (stateComp.hasState(DO_START_HAARE_VOM_TURM_HERUNTERLASSEN)) {
            if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)
                    && talkingComp.scUndRapunzelKoennenEinanderSehen()) {
                n.narrateAlt(altDannHaareFestbinden(getDescription(true)), secs(10));
            }

            stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
            return;
        }

        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN) &&
                endTime.isAfter(
                        stateComp.getStateDateTime().plus(
                                DAUER_WIE_LANGE_DIE_HAARE_MAX_UNTEN_BLEIBEN))) {
            rapunzelZiehtHaareWiederHoch();
            return;
        }

        if (rapunzelMoechteSingen(endTime)) {
            onTimePassed_RapunzelMoechteSingen();
            return;
        }

        onTimePassed_RapunzelMoechteNichtSingen();

    }

    private boolean rapunzelMoechteSingen(final AvDateTime now) {
        if (!stateComp.hasState(NORMAL, SINGEND)) {
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
                // ... Minuten mit...
                mins(25),
                // ... Minuten Pause danach - bis um...
                oClock(19));
    }

    private void onTimePassed_RapunzelMoechteSingen() {
        if (stateComp.hasState(NORMAL)) {
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

        loadSC().feelingsComp().requestMoodMin(BEWEGT);

        if (!loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            n.narrate(neuerSatz(PARAGRAPH,
                    "Auf einmal hebt ein Gesang an, so lieblich, dass es dir das "
                            + "Herz rührt. Du hältst still und horchst: Kommt die Stimme aus "
                            + "dem kleinen Fensterchen oben im Turm?")
                    .timed(secs(20)));

            world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);

            loadSC().waitingComp().stopWaiting();
            return;
        }

        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            n.narrateAlt(
                    du("hörst", "erneut die süße Stimme aus dem Turmfenster singen")
                            .mitVorfeldSatzglied("erneut")
                            .timed(secs(10)),
                    du("hörst", "es von oben aus dem Turm singen")
                            .mitVorfeldSatzglied("von oben aus dem Turm")
                            .schonLaenger()
                            .timed(NO_TIME),
                    du(PARAGRAPH, "hörst", "wieder Gesang von oben schallen",
                            PARAGRAPH)
                            .mitVorfeldSatzglied("wieder")
                            .schonLaenger()
                            .timed(NO_TIME),
                    neuerSatz(PARAGRAPH, "Plötzlich erschallt über dir wieder Gesang")
                            .timed(NO_TIME),
                    du("hörst", "den Gesang erneut")
                            .mitVorfeldSatzglied("erneut")
                            .timed(NO_TIME)
            );

            world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);

            return;
        }

        n.narrateAlt(
                du(SENTENCE, "hörst",
                        "aus dem Turmfenster die junge Frau singen. Dir wird ganz",
                        "warm beim Zuhören")
                        .mitVorfeldSatzglied("aus dem Turmfenster")
                        .schonLaenger()
                        .timed(secs(10))
                        .undWartest()
                        .phorikKandidat(F, RAPUNZEL),
                du(SENTENCE, "hörst",
                        "plötzlich wieder Gesang aus dem Turmfenster. Wann wirst du",
                        "die junge Frau endlich retten können?", PARAGRAPH)
                        .mitVorfeldSatzglied("plötzlich")
                        .timed(NO_TIME)
                        .phorikKandidat(F, RAPUNZEL),
                du("hörst", "erneut die süße Stimme aus dem Turmfenster singen. Jetzt "
                        + "weißt du "
                        + "endlich, wer dort singt – und sein Vertrauen in dich setzt")
                        .mitVorfeldSatzglied("erneut")
                        .timed(NO_TIME)
        );

        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 0.5f, FeelingIntensity.DEUTLICH);
    }

    private void onTimePassed_RapunzelMoechteNichtSingen() {
        if (stateComp.hasState(SINGEND)) {
            stateComp.narrateAndSetState(NORMAL);
            onTimePassed_moechteNichtMehrSingen_bislangGesungen();
            return;
        }

        // Rapunzel hat schon die ganze Zeit nicht gesungen
    }

    private void onTimePassed_moechteNichtMehrSingen_bislangGesungen() {
        if (!loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return;
        }

        loadSC().feelingsComp().requestMoodMin(BEWEGT);

        final AltDescriptionsBuilder alt = alt();

        alt.add(
                neuerSatz("Plötzlich endet der Gesang", PARAGRAPH),
                neuerSatz("Plötzlich wird es still"),
                neuerSatz(PARAGRAPH,
                        "Nun hat der Gesang geendet - wie gern würdest noch länger "
                                + "zuhören!", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Nun ist es wieder still"),
                neuerSatz(PARAGRAPH,
                        "Jetzt hat der süße Gesang aufgehört"),
                neuerSatz(PARAGRAPH,
                        "Jetzt ist es wieder still. Dein Herz ist noch ganz bewegt",
                        PARAGRAPH));
        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Auf einmal ist nichts mehr zu hören. Es lässt dir keine Ruhe: "
                                    + "Wer mag dort oben so lieblich singen?", PARAGRAPH));
        }

        n.narrateAlt(alt, NO_TIME);

        world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_GESANG);
    }

    @Override
    public void onSCActionDone(final AvDateTime startTimeOfUserAction) {
        if (stateComp.hasState(PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            stateComp.narrateAndSetState(DO_START_HAARE_VOM_TURM_HERUNTERLASSEN);
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private <Z extends ILocatableGO & IResponder & ITalkerGO<RapunzelsZauberinTalkingComp>>
    Z loadZauberin() {
        return (Z) world.load(RAPUNZELS_ZAUBERIN);
    }

    public void forgetAll() {
        counterDao.reset(RapunzelTalkingComp.Counter.class);
    }
}