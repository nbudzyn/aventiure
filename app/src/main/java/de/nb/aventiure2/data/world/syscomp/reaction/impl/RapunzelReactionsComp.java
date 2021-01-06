package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
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
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;
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
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.TimedDescription.toTimed;

/**
 * "Reaktionen" von Rapunzel, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, IRufReactions,
        IStateChangedReactions, ITimePassedReactions {
    private static final AvTimeSpan DAUER_WIE_LANGE_DIE_HAARE_MAX_UNTEN_BLEIBEN = mins(3);

    private final TimeTaker timeTaker;
    private final MemoryComp memoryComp;
    private final RapunzelStateComp stateComp;
    private final LocationSystem locationSystem;
    private final LocationComp locationComp;
    private final FeelingsComp feelingsComp;
    private final RapunzelTalkingComp talkingComp;

    public RapunzelReactionsComp(final AvDatabase db,
                                 final TimeTaker timeTaker, final Narrator n,
                                 final World world,
                                 final MemoryComp memoryComp,
                                 final RapunzelStateComp stateComp,
                                 final LocationSystem locationSystem,
                                 final LocationComp locationComp,
                                 final FeelingsComp feelingsComp,
                                 final RapunzelTalkingComp talkingComp) {
        super(RAPUNZEL, n, world);
        this.timeTaker = timeTaker;
        this.memoryComp = memoryComp;
        this.stateComp = stateComp;
        this.locationSystem = locationSystem;
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
                // IDEA Konzept dafür entwickeln, dass der Benutzer Rapunzel gut gelaunt
                //  verlässt und niedergeschlagen zu Rapunzel zurückkehrt und
                //  Rapunzel auf den Wechsel reagiert (Mental Model für Rapunzel?)
                return;
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
                            + "Stimme aus dem kleinen Fensterchen oben im Turm?",
                    secs(20))
                    .beendet(PARAGRAPH));

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
            return;
        }
        n.narrateAlt(
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

            final ImmutableList.Builder<TimedDescription<?>> alt = ImmutableList.builder();
            alt.addAll(altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm());
            // STORY Wenn Rapunzel das mit der Zauberin erzählt hat (aber auch dann nur
            //  einmal): „Aber komm nicht, wenn die Alte bei mir ist, ruft sie dir noch nach"
            //  (Das wäre ein neuer RufTyp!)

            alt.add(neuerSatz(
                    "Als du unten bist, verschwinden die goldenen Haare "
                            + "wieder oben im Fenster", secs(15)
            )
                    .beendet(PARAGRAPH));

            n.narrateAlt(alt);

            return;
        }

        if (world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            loadSC().feelingsComp().requestMoodMin(NEUTRAL);
            // STORY Andere und alternative Beschreibungen, wenn der SC
            //  Rapunzel schon kennengelernt hat
            n.narrate(neuerSatz("Aus dem kleinen "
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
        if (timeTaker.now().getTageszeit() == NACHTS) {
            onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_nachts();
            return;
        }

        onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_tagsueber();
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_tagsueber() {
        world.loadSC().memoryComp().upgradeKnown(RAPUNZEL);
        final Nominalphrase desc = getDescription();

        n.narrate(neuerSatz(
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

        // Rapunzel ist erst einmal verschreckt.
        feelingsComp.upgradeFeelingsTowards(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                -FeelingIntensity.NUR_LEICHT, FeelingIntensity.MERKLICH);

        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            // Jetzt weiß der SC, wer so schön gesungen hat! Und schön ist
            // sie obendrein noch!
            loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                    ZUNEIGUNG_ABNEIGUNG, 2, FeelingIntensity.STARK);
        }

        loadSC().feelingsComp().requestMood(BEWEGT);
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelUnbekannt_nachts() {
        world.loadSC().memoryComp().upgradeKnown(RAPUNZEL);
        final Nominalphrase desc = getDescription();

        n.narrate(neuerSatz(
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

        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            // Jetzt weiß der SC, wer so schön gesungen hat!
            loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                    ZUNEIGUNG_ABNEIGUNG, 1, FeelingIntensity.DEUTLICH);
        }
        loadSC().feelingsComp().requestMoodMin(ANGESPANNT);

        feelingsComp.upgradeFeelingsTowards(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                -FeelingIntensity.DEUTLICH, FeelingIntensity.SEHR_STARK);
    }

    private void onSCEnter_ObenImAltenTurm_RapunzelBekannt() {
        loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);
        stateComp.setState(STILL);

        if (timeTaker.now().getTageszeit() == NACHTS) {
            narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Nachts();
        } else {
            narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Tagsueber();
        }

        memoryComp.upgradeKnown(SPIELER_CHARAKTER);

        world.loadSC().memoryComp().upgradeKnown(RAPUNZEL);
    }

    private void narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Tagsueber() {
        final SubstantivischePhrase anaph =
                getAnaphPersPronWennMglSonstDescription(true);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                1f, FeelingIntensity.MERKLICH);
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 0.75f, FeelingIntensity.DEUTLICH);

        final ImmutableList.Builder<TimedDescription<?>> alt = ImmutableList.builder();

        final ImmutableList<AbstractDescription<?>> altReaktionSaetze =
                // Es wäre besser, wenn der Phorik-Kandidat schon beim Erzeugen des
                // Satzes gesetzt würde.
                feelingsComp
                        .altReaktionBeiBegegnungMitScSaetze(anaph).stream()
                        .flatMap(s1 -> altNeueSaetze(s1).stream())
                        .map(allgDesc -> allgDesc
                                .phorikKandidat(anaph.getNumerusGenus(),
                                        RAPUNZEL))
                        .collect(toImmutableList());
        alt.addAll(toTimed(altReaktionSaetze, secs(5)));

        final int zuneigungSCTowardsRapunzel =
                loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
        if (RapunzelTalkingComp.duzen(zuneigungSCTowardsRapunzel)
                && zuneigungSCTowardsRapunzel >= FeelingIntensity.MERKLICH) {
            // FIXME Dies beides darf erst erscheinen, wenn Rapunzel von der Zauberin erzählt hat
            //  und den SCu m Hilfe gebeten oder der SC Hilfe angeboten hat
            alt.addAll(TimedDescription.toTimed(secs(15),
                    du("findest",
                            "oben die junge Frau ganz aufgeregt vor: „Du bist schon "
                                    + "wieder da!“, sagt sie, „Kannst du mir nun helfen?“",
                            "oben")
                            .phorikKandidat(F, RAPUNZEL),
                    neuerSatz(
                            "„Die Alte hat nichts bemerkt“, sprudelt die "
                                    + "wunderschöne junge Frau los, „aber lange werden wir uns "
                                    + "nicht treffen können. Sie ist so neugierig!“")
                            .phorikKandidat(F, RAPUNZEL)
            ));
        }

        if (loadSC().memoryComp().getKnown(RAPUNZEL) == KNOWN_FROM_DARKNESS) {
            alt.addAll(
                    altReaktionSaetze.stream()
                            .map(s -> new TimedDescription<>(
                                    s.toTextDescription()
                                            .mitPraefixCapitalize(
                                                    "Am Fenster sitzt die junge Frau, schön als "
                                                            + "du unter der Sonne noch keine gesehen hast. "
                                                            + "Ihre Haare glänzen fein wie gesponnen Gold. "
                                            ), secs(30)))
                            .collect(ImmutableList.toImmutableList())
            );
        }
        n.narrateAlt(alt);
    }

    private void narrateAndUpgradeFeelings_ScTrifftRapunzelObenImAltenTurmAn_Nachts() {
        final ImmutableList.Builder<TimedDescription<?>> alt = ImmutableList.builder();

        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 0.75f, FeelingIntensity.DEUTLICH);

        final SubstantivischePhrase anaph =
                getAnaphPersPronWennMglSonstDescription(true);

        final ImmutableList<AbstractDescription<?>> altReaktionSaetze =
                // Es wäre besser, wenn der Phorik-Kandidat durch den Satz
                //  gesetzt würde. Das ist allerdings kompliziert...
                feelingsComp
                        .altReaktionBeiBegegnungMitScSaetze(anaph).stream()
                        .flatMap(s -> altNeueSaetze(s).stream())
                        .map(d -> d.phorikKandidat(anaph.getNumerusGenus(), RAPUNZEL))
                        .collect(toImmutableList());

        alt.addAll(toTimed(altReaktionSaetze, secs(5)));

        // Könnte leer sein
        // ...aber noch nicht mit dieser Ergänzung:
        // Diese Sätze sind bereits in altZuneigungAbneigungSaetze enthalten...
        final ImmutableList<AbstractDescription<?>> altSCBeiBegegnungAnsehenSaetze =
                // Es wäre besser, wenn der Phorik-Kandidat durch den Satz
                //  gesetzt würde. Das ist allerdings kompliziert...
                feelingsComp
                        .altSCBeiBegegnungAnsehenSaetze(anaph)
                        // ...aber noch nicht mit dieser Ergänzung:
                        .stream()
                        .map(s -> s.mitAdverbialerAngabe(
                                new AdverbialeAngabeSkopusSatz("oben im dunklen Zimmer")
                        ))
                        .collect(Collectors.toList()).stream()
                        .flatMap(s1 -> altNeueSaetze(s1).stream())
                        .map(allgDesc -> allgDesc
                                .phorikKandidat(anaph.getNumerusGenus(),
                                        RAPUNZEL))
                        .collect(toImmutableList());
        alt.addAll(toTimed(altSCBeiBegegnungAnsehenSaetze, secs(15)));

        alt.add(du(SENTENCE, "hast",
                anaph.akk()
                        + " offenbar aus dem Bett "
                        + "geholt. "
                        + capitalize(anaph.persPron().nom()) // Sie
                        + " sieht sehr zerknittert "
                        + "aus",
                "offenbar",
                secs(30))
                .phorikKandidat(anaph.persPron(), RAPUNZEL));
        if (loadSC().memoryComp().getKnown(RAPUNZEL) == KNOWN_FROM_LIGHT) {
            alt.add(neuerSatz(anaph.persPron().nom()
                            + " ist auch nachts wunderschön – allerdings ist die "
                            + "junge, verschlafene "
                            + "Frau in ihren Decken auch sichtlich überrascht, dass du zu "
                            + "dieser Nachtzeit noch einmal bei ihr vorbeischaust",
                    secs(15))
                    .phorikKandidat(F, RAPUNZEL));
        }
        n.narrateAlt(alt);
    }

    private void onZauberinEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM) &&
                from != null && from.is(VOR_DEM_ALTEN_TURM) &&
                to.is(OBEN_IM_ALTEN_TURM)) {
            rapunzelZiehtHaareWiederHoch();

            if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM) &&
                    !world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
                n.narrate(neuerSatz(
                        "„Das ist also die Leiter, auf welcher man hinaufkommt!“, "
                                + "denkst du bei dir", secs(5))
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

    private void onGoldeneKugelEnter(@Nullable final ILocationGO from,
                                     final ILocationGO to) {
        if (!locationComp.hasSameOuterMostLocationAs(to)) {
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
    }

    private void rapunzelMoechteGoldeneKugelHaben() {
        final SubstantivischePhrase desc =
                getAnaphPersPronWennMglSonstShortDescription();
        n.narrate(
                neuerSatz(desc.nom() +
                                " sieht interessiert zu. „Darf ich auch "
                                + "einmal?“, fragt " + desc.persPron().nom() + " dich",
                        secs(30))
                        .phorikKandidat(F, RAPUNZEL));

        memoryComp.upgradeKnown(GOLDENE_KUGEL);

        talkingComp.setTalkingTo(SPIELER_CHARAKTER);
        stateComp.narrateAndSetState(HAT_NACH_KUGEL_GEFRAGT);
    }

    private void rapunzelZiehtHaareWiederHoch() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            n.narrateAlt(altRapunzelZiehtHaareWiederHoch_VorDemAltenTurm());
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.narrateAlt(altRapunzelZiehtHaareWiederHoch_ObenImAltenTurm());
        }

        stateComp.narrateAndSetState(STILL);
    }

    @NonNull
    private static ImmutableList<TimedDescription<?>>
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

    private ImmutableList<TimedDescription<?>>
    altRapunzelZiehtHaareWiederHoch_ObenImAltenTurm() {
        final SubstantivischePhrase anaph =
                getAnaphPersPronWennMglSonstDescription(false);

        return ImmutableList.of(
                neuerSatz(
                        "Jetzt zieht "
                                + anaph.nom() // "die junge Frau"
                                + " "
                                + anaph.possArt().vor(PL_MFN).akk() // "ihre"
                                + " Haare wieder hoch",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(
                        "Die Haare zieht "
                                + anaph.nom()
                                + " wieder hoch",
                        secs(15))
                        .beendet(PARAGRAPH)
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
            onRapunzelruf(rufer);
            return;
        }
    }

    private void onRapunzelruf(final ILocatableGO rufer) {
        if (!stateComp.hasState(SINGEND, STILL)) {
            return;
        }

        if (loadZauberin().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            return;
        }

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (stateComp.hasState(SINGEND)) {
                if (world.loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                    n.narrate(
                            neuerSatz(
                                    "Sofort hört der Gesang auf – und gleich darauf "
                                            + "fallen aus dem kleinen "
                                            + "Fenster oben im Turm lange, goldene Haarzöpfe "
                                            + "herab, sicher zwanzig Ellen tief bis auf den Boden",
                                    secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                } else {
                    n.narrate(
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
                    n.narrate(
                            neuerSatz("Wieder fallen die langen, golden "
                                    + "glänzenden Zöpfe aus dem "
                                    + "Fenster bis zum Boden herab", secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                } else {
                    n.narrate(
                            neuerSatz("Gleich darauf fallen aus dem kleinen "
                                            + "Fenster oben im Turm lange, goldene Haarzöpfe herab, "
                                            + "sicher zwanzig Ellen tief bis auf den Boden",
                                    secs(30))
                                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));
                }
            }

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_HAARE);
            stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
            return;
        }

        // Sonderfall: Rapunzel verzögert das Haare-Herunterlassen
        if (loadSC().locationComp().hasLocation(OBEN_IM_ALTEN_TURM)) {
            n.narrateAlt(
                    neuerSatz(
                            "„O weh, die Alte kommt!“, entfährt es der jungen "
                                    + "Frau. „Du musst dich verstecken! Sie "
                                    + "ist eine mächtige Zauberin!“",
                            secs(10)),
                    neuerSatz(
                            "„O nein, die Alte kommt schon wieder!“, sagt "
                                    + "die junge Frau entsetzt. „Versteck dich "
                                    + "schnell!“",
                            secs(15)),
                    neuerSatz("Alarmiert schaut die junge Frau dich an. Dann wandert "
                                    + "ihr Blick auf das Bett",
                            secs(20))
                            .phorikKandidat(F, RAPUNZEL)
                    // Hier wäre "dann" nur sinnvoll, wenn Rapunzel etwas tut, nicht der SC
            );

            loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                    ZUNEIGUNG_ABNEIGUNG, 0.5f, FeelingIntensity.DEUTLICH);

            // FIXME Hier ruft die Hexe sofort noch einmal - und Hexe und Rapunzel landen
            //  in einer ewigen Rückkopplungsschleife (Anwendung hängt). Die Hexe sollte einen
            //  Zusatndswechsel haben und nach dem Rufen (erst einmal) abwarten, ob sich was tut!

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
                !stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            talkingComp.rapunzelLaesstHaareZumAbstiegHerunter();
            return;
        }
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN) &&
                endTime.isAfter(
                        stateComp.getStateDateTime().plus(
                                DAUER_WIE_LANGE_DIE_HAARE_MAX_UNTEN_BLEIBEN))) {
            rapunzelZiehtHaareWiederHoch();
            return;
        }

        if (rapunzelMoechteSingen(endTime)) {
            onTimePassed_RapunzelMoechteSingen(startTime, endTime);
            return;
        }

        onTimePassed_RapunzelMoechteNichtSingen(startTime, endTime);
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

        loadSC().feelingsComp().requestMoodMin(BEWEGT);

        if (!loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            n.narrate(neuerSatz(PARAGRAPH,
                    "Auf einmal hebt ein Gesang an, so lieblich, dass es dir das "
                            + "Herz rührt. Du hältst still und horchst: Kommt die Stimme aus "
                            + "dem kleinen Fensterchen oben im Turm?",
                    secs(20)));

            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
            return;
        }

        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            n.narrateAlt(
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

        n.narrateAlt(
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
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG, 1, FeelingIntensity.DEUTLICH);
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

        loadSC().feelingsComp().requestMoodMin(BEWEGT);

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(
                neuerSatz("Plötzlich endet der Gesang")
                        .beendet(PARAGRAPH),
                neuerSatz("Plötzlich wird es still"),
                neuerSatz(PARAGRAPH,
                        "Nun hat der Gesang geendet - wie gern würdest noch länger "
                                + "zuhören!")
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Nun ist es wieder still"),
                neuerSatz(PARAGRAPH,
                        "Jetzt hat der süße Gesang aufgehört"),
                neuerSatz(PARAGRAPH,
                        "Jetzt ist es wieder still. Dein Herz ist noch ganz bewegt")
                        .beendet(PARAGRAPH));
        if (!loadSC().memoryComp().isKnown(RAPUNZEL)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Auf einmal ist nichts mehr zu hören. Es lässt dir keine Ruhe: "
                                    + "Wer mag dort oben so lieblich singen?")
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());

        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_GESANG);
    }

    @NonNull
    private ILocatableGO loadZauberin() {
        return (ILocatableGO) world.load(RAPUNZELS_ZAUBERIN);
    }
}