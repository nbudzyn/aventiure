package de.nb.aventiure2.data.world.syscomp.talking.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.movement.MovementComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_RUECKWEG_VON_RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entryReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStNSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.base.Nominalphrase.IHR_ZIEL;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.SICH_VERABSCHIEDEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.FRAGEN_NACH;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSPRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEGRUESSEN;

/**
 * Component for {@link World#RAPUNZELS_ZAUBERIN}: Der Spieler
 * kann versuchen, mit Rapunzels Zauberin ein Gespräch zu führen.
 */
@SuppressWarnings("DuplicateBranchesInSwitch")
public class RapunzelsZauberinTalkingComp extends AbstractTalkingComp {
    private final LocationComp locationComp;
    private final RapunzelsZauberinStateComp stateComp;
    private final FeelingsComp feelingsComp;
    private final MovementComp movementComp;

    public RapunzelsZauberinTalkingComp(final AvDatabase db,
                                        final Narrator n,
                                        final TimeTaker timeTaker,
                                        final World world,
                                        final LocationComp locationComp,
                                        final RapunzelsZauberinStateComp stateComp,
                                        final FeelingsComp feelingsComp,
                                        final MovementComp movementComp,
                                        final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZELS_ZAUBERIN, db, timeTaker, n, world, initialSchonBegruesstMitSC);
        this.locationComp = locationComp;
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
        this.movementComp = movementComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        switch (stateComp.getState()) {
            case MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE:
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                // fall-through
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                return ImmutableList.of(
                        entryReEntrySt(ANSPRECHEN, this::ansprechen),
                        st(FRAGEN_NACH.mitPraep(
                                np(N, null, "ihr Ziel",
                                        "ihrem Ziel")),
                                this::frageNachZiel),
                        exitSt(this::gespraechBeenden),
                        immReEntryStSCHatteGespraechBeendet(FRAGEN_NACH.mitPraep(IHR_ZIEL),
                                this::frageNachZiel_ImmReEntrySCHatteGespraechBeendet),
                        immReEntryStNSCHatteGespraechBeendet(FRAGEN_NACH.mitPraep(IHR_ZIEL),
                                this::frageNachZiel_ImmReEntryNSCHatteGespraechBeendet)
                );
            case BEI_RAPUNZEL_OBEN_IM_TURM:
                return ImmutableList.of(
                        entryReEntrySt(ANSPRECHEN, this::ansprechenObenImTurm));
            case WEISS_DASS_RAPUNZEL_BEFREIT_WURDE:
                // STORY Man kann die  Zauberin ansprechen, nachdem Rapunzel befreit wurde
                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }

    private void ansprechen() {
        if (!isSchonBegruesstMitSC()) {
            scBegruesst();
        } else {
            scSprichtAnBereitsBegruesst();
        }

        zauberinReagiertAufAnsprechen();
    }

    private void ansprechenObenImTurm() {
        n.narrate(neuerSatz(PARAGRAPH,
                "„Hallo“, rufst du auf einmal, „hier bin ich! Unter dem Bett!“")
                .timed(secs(5)));
        setTalkingTo(SPIELER_CHARAKTER);

        zauberinZaubertVergessenszauber();
    }

    private void scSprichtAnBereitsBegruesst() {
        final SubstantivischePhrase anaph = anaph(false);
        n.narrateAlt(secs(10),
                neuerSatz(PARAGRAPH, "„Gute Frau“, sprichst du", anaph.akkK(), "an").dann(),
                du(PARAGRAPH, "wendest", "dich noch einmal", anaph.datK(), "zu")
                        .undWartest());

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void scBegruesst() {
        final SubstantivischePhrase anaph = anaph(false);

        n.narrateAlt(alt()
                        .addAll(altNeueSaetze(PARAGRAPH,
                                "„",
                                altBegruessungen(),
                                // "Einen schönen guten Tag"
                                "“, sprichst du",
                                anaph.akkK(),
                                "an"))
                        .addAll(altNeueSaetze(PARAGRAPH,
                                "„",
                                altBegruessungen(),
                                // "Einen schönen guten Tag"
                                "“, redest du",
                                anaph.akkK(),
                                "an"))
                        .add(neuerSatz(PARAGRAPH,
                                "„Holla, gute Frau“, sprichst du",
                                anaph.akkK(),
                                "an").dann(),
                                neuerSatz("„Schön Euch zu sehen“, sprichst du",
                                        anaph.akkK(),
                                        "an")
                                        .dann(),
                                du(PARAGRAPH, BEGRUESSEN.mit(anaph))),
                secs(5));

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void zauberinReagiertAufAnsprechen() {
        final SubstantivischePhrase anaph = anaph(true);

        final ImmutableList<Satz> altReaktionSaetze =
                feelingsComp.altReaktionBeiBegegnungMitScSaetze(anaph);

        n.narrateAlt(altSaetze(altReaktionSaetze).schonLaenger(), secs(5));

        if (feelingsComp.getFeelingTowardsForActionsMitEmpathischerSchranke(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                <= -FeelingIntensity.STARK) {
            talkerBeendetGespraech();
        }
    }

    private void gespraechBeenden() {
        final SubstantivischePhrase anaph = anaph(false);

        n.narrateAlt(alt()
                .addAll(altNeueSaetze(
                        "„",
                        altVerabschiedungen(),
                        // "Tschüss"
                        "!“ Du wendest dich ab").undWartest().dann())
                .add(du("sagst", anaph.datK(), "Abschied"),
                        du(SICH_VERABSCHIEDEN.mit(anaph)).schonLaenger()
                )
                .addAll(altNeueSaetze(
                        "„",
                        altVerabschiedungen(),
                        // "Tschüss"
                        "“, verabschiedest du dich",
                        PraepositionMitKasus.VON.mit(anaph).getDescription())
                        .undWartest().dann())
                .add(du("verabschiedest",
                        "dich wieder",
                        PraepositionMitKasus.VON.mit(anaph).getDescription())
                                .undWartest().dann(),
                        du("sagst", anaph.datK(), "Ade")
                                .undWartest().dann()
                ), secs(10));

        gespraechspartnerBeendetGespraech();
    }

    private void frageNachZiel_ImmReEntrySCHatteGespraechBeendet() {
        n.narrateAlt(NO_TIME, neuerSatz("Aber dann fragst du doch noch:"));

        frageNachZiel();
    }

    private void frageNachZiel_ImmReEntryNSCHatteGespraechBeendet() {
        final SubstantivischePhrase anaph = anaph(false);

        n.narrateAlt(NO_TIME,
                neuerSatz("Aber du lässt nicht locker:"),
                du("fragst", anaph.akkK(), "erneut:"));

        frageNachZiel();
    }

    private void frageNachZiel() {
        final SubstantivischePhrase anaph = anaph(false);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER,
                ZUNEIGUNG_ABNEIGUNG, -0.34f, FeelingIntensity.DEUTLICH);

        final AltDescriptionsBuilder alt = alt();

        final int zuneigungAbneigungGegenSC =
                feelingsComp.getFeelingTowardsForActionsMitEmpathischerSchranke(
                        SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);
        if (zuneigungAbneigungGegenSC >= -FeelingIntensity.MERKLICH) {
            alt.add(
                    neuerSatz("„Ihr habt es wohl eilig?“ – „So ist es“, antwortet",
                            anaph.persPron().nomK(),
                            "dir", PARAGRAPH)
            );
        } else {
            alt.add(
                    neuerSatz("„Wohin seid ihr auf dem Weg?“ – „Das ist meine",
                            "Sache!“, antwortet",
                            anaph.nomK(), PARAGRAPH),
                    neuerSatz("„Wohin des Wegs?“ – „Was geht es dich an?“, ist",
                            anaph.possArt().vor(F).nomStr(), // "ihre"
                            "abweisende Antwort", PARAGRAPH),
                    neuerSatz("„Wohin geht es denn heute?“ – „Das geht dich überhaupt",
                            "nichts an!“, gibt",
                            anaph.nomK(),
                            "pampig zurück", PARAGRAPH)
            );
        }

        n.narrateAlt(alt, secs(10));

        if (feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                <= -FeelingIntensity.DEUTLICH) {
            talkerBeendetGespraech();
        }
    }

    public void zauberinZaubertVergessenszauber() {
        narrateUnmittelbarerVergessenszauber();

        // Zauberin kann den Spieler nicht mehr ausstehen
        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER,
                ZUNEIGUNG_ABNEIGUNG, -3, FeelingIntensity.SEHR_STARK);

        scUndRapunzelVergessenAlles();

        // Die Zauberin ist schon weit auf dem Rückweg und der SC findet sich unter
        // den Bäumen wieder
        locationComp.narrateAndSetLocation(IM_WALD_NAHE_DEM_SCHLOSS);
        if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            // Ohne Reactions - der Spieler bekommt ja nichts davon mit.
            loadSC().locationComp().setLocation(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME);
        }
        zauberinNichtImTurmBeginntRueckweg();

        narrateNachDemVergessenszauber();
    }

    private void narrateUnmittelbarerVergessenszauber() {
        if (loadSC().locationComp().hasRecursiveLocation(BETT_OBEN_IM_ALTEN_TURM)) {
            // Rapunzel hat den SC verraten
            n.narrate(du("hörst", "und fühlst Schritte. Dann schaut",
                    "die magere Frau",
                    "unters Bett – und dir direkt in die Augen. Du bist wie",
                    "gebannt und kannst deinen Blick gar nicht abwenden, und die Frau",
                    "murmelt etwas…", CHAPTER)
                    .timed(mins(5)));
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.narrate(neuerSatz("Jetzt geht alles ganz schnell. Die magere Frau schaut",
                    "zum Fenster herein. Ihr Blick fällt auf dich – und mit einem Mal",
                    "sieht sie dir direkt in die Augen. Du bist wie",
                    "gebannt und kannst deinen Blick gar nicht abwenden, und die Frau",
                    "scheint etwas zu murmeln…", CHAPTER)
                    .timed(mins(5)));
        } else if (locationComp.hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            n.narrate(neuerSatz(PARAGRAPH, "Jetzt schaut oben aus dem Turmfenster die "
                    + "magere Frau heraus. "
                    + "Kurz sucht ihr Blick umher, dann sieht sie dich direkt an. Ihre Augen "
                    + "sind - du kannst deinen Blick gar nicht abwenden. Ihr Mund formt Worte, "
                    + "die du nicht verstehst, und du bekommst es mit der Angst zu tun…", CHAPTER)
                    .timed(mins(5)));
        } else {
            n.narrateAlt(mins(5),
                    neuerSatz(PARAGRAPH, "Die magere Frau sieht dich mit einem Mal "
                            + "direkt an. Ihre Augen sind – du kannst deinen Blick "
                            + "gar nicht abwenden. Dann scheint sie etwas zu murmeln - doch nicht "
                            + "etwa einen Zauberspruch? –", CHAPTER),
                    neuerSatz(PARAGRAPH, "Plötzlich sieht dir die Frau unmittelbar in",
                            "die Augen. Du bist wie gebannt und hörst sie fremdartige Worte",
                            "murmeln – will sie dich etwa verhexen?", CHAPTER));
        }
    }

    private void scUndRapunzelVergessenAlles() {
        // Spieler wird verzaubert und vergisst alles.
        unsetTalkingTo(true);
        loadRapunzel().talkingComp().unsetTalkingTo(true);
        loadSC().talkingComp().unsetTalkingTo(true);
        loadSC().mentalModelComp().unsetAssumedLocations(
                RAPUNZEL, RAPUNZELS_ZAUBERIN);
        loadSC().memoryComp().narretAndForget(
                RAPUNZEL, RAPUNZELS_ZAUBERIN, RAPUNZELS_GESANG, RAPUNZELS_HAARE, RAPUNZELRUF,
                SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT,
                OBEN_IM_ALTEN_TURM, BETT_OBEN_IM_ALTEN_TURM,
                RAPUNZELS_NAME,
                RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU,
                RAPUNZELS_FREIHEITSWUNSCH);
        loadSC().feelingsComp().resetFeelingsTowards(RAPUNZEL);
        counterDao.reset(VorDemTurmConnectionComp.Counter.ALTER_TURM_UMRUNDET);
        counterDao.reset(BettFactory.Counter.class);

        // Auch Rapunzel wird verzaubert und vergisst den Spieler!
        loadRapunzel().memoryComp().narretAndForget(SPIELER_CHARAKTER, GOLDENE_KUGEL,
                SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT);
        loadSC().feelingsComp().resetFeelingsTowards(SPIELER_CHARAKTER);
        loadRapunzel().talkingComp().forgetAll();
        ((RapunzelReactionsComp) loadRapunzel().reactionsComp()).forgetAll();

        // SC ist etwas müde
        loadSC().feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                FeelingIntensity.NUR_LEICHT, hours(1)
        );

        // Rapunzel ist still
        loadRapunzel().stateComp().narrateAndSetState(RapunzelState.NORMAL);
    }

    public void zauberinNichtImTurmBeginntRueckweg() {
        stateComp.narrateAndSetState(AUF_DEM_RUECKWEG_VON_RAPUNZEL);
        movementComp.startMovement(timeTaker.now(), ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN);
    }

    private void narrateNachDemVergessenszauber() {
        final String ortsbeschreibung;
        if (loadSC().locationComp().hasLocation(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)) {
            ortsbeschreibung = "sitzt im Unterholz vor dem alten Turm";
        } else {
            ortsbeschreibung = "stehst ganz allein vor dem alten Turm";
        }

        n.narrateAlt(altNeueSaetze(CHAPTER,
                ImmutableList.of("Auf einmal", "Plötzlich", "Jetzt"),
                "ist alles wie weggeblasen. Du",
                ortsbeschreibung,
                "und fühlst dich etwas verwirrt: Was hattest du "
                        + "eigentlich gerade vor? Ob der Turm wohl "
                        + "bewohnt ist? Niemand ist zu sehen")
                .timed(secs(15)));
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private <R extends
            IHasMemoryGO &
            IHasStateGO<RapunzelState> &
            ITalkerGO<RapunzelTalkingComp> &
            IResponder>
    R loadRapunzel() {
        return (R) world.load(RAPUNZEL);
    }
}
