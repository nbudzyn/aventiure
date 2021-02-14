package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractFrageMitAntworten;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.InterrogativadverbVerbAllg;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp.getPersonalpronomenSC;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingsSaetzeUtil.altAnsehenSaetze;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingsSaetzeUtil.altEindrueckSaetze;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BETRUEBT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ZUFRIEDEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.GEFRAGT_NACH_RAPUNZELN;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.GESPRAECH_BEGONNEN_ODER_UNMITTELBAR_FORTGESETZT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.RETTUNG_ZUGESAGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.SCHON_IMMER_GEWUENSCHT_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.RapunzelTalkingComp.Counter.SC_BEGRUESST;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entryReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStNSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEGEISTERT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FREUDESTRAHLEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GENERVT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SKEPTISCH;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToAltKonstituentenfolgen;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Nominalphrase.DEIN_HERZ;
import static de.nb.aventiure2.german.base.Nominalphrase.EIN_GESPRAECH;
import static de.nb.aventiure2.german.base.Nominalphrase.GESPRAECH;
import static de.nb.aventiure2.german.base.Nominalphrase.IHRE_HAARE;
import static de.nb.aventiure2.german.base.Nominalphrase.IHR_NAME;
import static de.nb.aventiure2.german.base.Nominalphrase.RETTUNG_OHNE_ART;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altParagraphs;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.duParagraph;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.paragraph;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.DirektivesVerb.BITTEN;
import static de.nb.aventiure2.german.praedikat.Modalverb.KOENNEN;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.SICH_UNTERHALTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.FRAGEN_NACH;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.AUSSCHUETTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.ZUSAGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HELFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINUNTERLASSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObjIndirekterFragesatz.FRAGEN_OB_W;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "unchecked"})
public class RapunzelTalkingComp extends AbstractTalkingComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public
    enum Counter {
        GESPRAECH_BEGONNEN_ODER_UNMITTELBAR_FORTGESETZT,
        SC_BEGRUESST,
        RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG,
        GEFRAGT_NACH_RAPUNZELN,
        NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT,
        SCHON_IMMER_GEWUENSCHT_GEFRAGT,
        HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT,
        RETTUNG_ZUGESAGT
    }

    private final MemoryComp memoryComp;
    private final RapunzelStateComp stateComp;
    private final FeelingsComp feelingsComp;

    private final AbstractFrageMitAntworten jahreszeitenFrage;
    private final AbstractFrageMitAntworten kugelherkunftsfrage;
    private final AbstractFrageMitAntworten naschereifrage;

    public RapunzelTalkingComp(final AvDatabase db,
                               final TimeTaker timeTaker,
                               final Narrator n,
                               final World world,
                               final MemoryComp memoryComp,
                               final RapunzelStateComp stateComp,
                               final FeelingsComp feelingsComp,
                               final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZEL, db, timeTaker, n, world, initialSchonBegruesstMitSC);
        this.memoryComp = memoryComp;
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;

        jahreszeitenFrage =
                new RapunzelJahreszeitenFrageMitAntworten(
                        counterDao, n, world, stateComp, feelingsComp, this);
        kugelherkunftsfrage =
                new RapunzelKugelherkunftsfrageMitAntworten(
                        counterDao, n, world, stateComp, feelingsComp, this);
        naschereifrage =
                new NaschereifrageMitAntworten(
                        counterDao, n, world, stateComp, feelingsComp, this);
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        switch (stateComp.getState()) {
            case UNAEUFFAELLIG:
                return ImmutableList.of();
            case NORMAL:
                return ImmutableList.of(
                        entryReEntrySt(VerbSubjAkkPraep.BEGINNEN.mitAkk(EIN_GESPRAECH),
                                this::gespraechBeginnen_EntryReEntryImmReEntry),
                        entryReEntrySt(this::scKenntRapunzelsNamenNicht,
                                FRAGEN_NACH.mitPraep(IHR_NAME), this::nachNameFragen),
                        // "Die junge Frau bitten ihre Haare wieder hinunterzulassen"
                        entryReEntrySt(bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        st(this::scKenntRapunzelsNamenNicht,
                                FRAGEN_NACH.mitPraep(IHR_NAME), this::nachNameFragen),
                        st(this::frageNachHelfenSinnvoll,
                                // "Die junge Frau fragen, wie du ihr helfen kannst"
                                FRAGEN_OB_W.mitIndirekterFragesatz(
                                        KOENNEN.mitLexikalischemKern(
                                                HELFEN.mit(anaph()).mitAdverbialerAngabe(
                                                        InterrogativadverbVerbAllg.WIE))
                                                .alsSatzMitSubjekt(getPersonalpronomenSC())),
                                this::fragenWieSCHelfenKann),
                        st(this::rapunzelsFreiheitswunschBekannt,
                                ZUSAGEN.mitAkk(RETTUNG_OHNE_ART),
                                this::rapunzelRettungZusagen),
                        st(this::frageNachRapunzelsMutterSinnvoll,
                                FRAGEN_NACH.mitPraep(world.getDescription(RAPUNZELS_ZAUBERIN)),
                                this::nachRapunzelsZauberinFragen),
                        st(this::herzAusschuettenMoeglich,
                                // "Der jungen Frau dein Herz ausschütten"
                                AUSSCHUETTEN.mitAkk(DEIN_HERZ),
                                this::herzAusschuetten),
                        st(SICH_UNTERHALTEN, this::unterhalten_allg),
                        exitSt(bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(
                                VerbSubjAkkPraep.FORTSETZEN.mitAkk(GESPRAECH),
                                this::gespraechBeginnen_EntryReEntryImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(this::scKenntRapunzelsNamenNicht,
                                FRAGEN_NACH.mitPraep(IHR_NAME), this::nachNameFragen),
                        immReEntryStSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                VerbSubjAkkPraep.FORTSETZEN.mitAkk(GESPRAECH),
                                this::gespraechBeginnen_EntryReEntryImmReEntry),
                        immReEntryStNSCHatteGespraechBeendet(this::scKenntRapunzelsNamenNicht,
                                FRAGEN_NACH.mitPraep(IHR_NAME), this::nachNameFragen),
                        immReEntryStNSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry));
            case SINGEND:
                // FALL-THROUGH
            case HAARE_VOM_TURM_HERUNTERGELASSEN:
                return ImmutableList.of();
            case HAT_NACH_KUGEL_GEFRAGT:
                return ImmutableList.of(
                        entryReEntrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        exitSt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::hatNachKugelGefragt_haareHerunterlassenBitte_Exit),
                        immReEntryStSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry)
                );
            case HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT: {
                final ImmutableList.Builder<SCTalkAction> res = ImmutableList.builder();
                res.add(entryReEntrySt(
                        bittenHaareHerunterzulassenPraedikat(),
                        this::haareHerunterlassenBitte_EntryReEntry),
                        exitSt(bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry));
                res.addAll(jahreszeitenFrage.getAntwortActions());
                return res.build();
            }
            case HAT_NACH_HERKUNFT_DER_GOLDENEN_KUGEL_GEFRAGT: {
                final ImmutableList.Builder<SCTalkAction> res = ImmutableList.builder();
                res.add(entryReEntrySt(
                        bittenHaareHerunterzulassenPraedikat(),
                        this::haareHerunterlassenBitte_EntryReEntry),
                        exitSt(bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry));
                res.addAll(kugelherkunftsfrage.getAntwortActions());
                return res.build();
            }
            case HAT_NACH_BIENEN_UND_BLUMEN_GEFRAGT: {
                final ImmutableList.Builder<SCTalkAction> res = ImmutableList.builder();
                res.add(entryReEntrySt(
                        bittenHaareHerunterzulassenPraedikat(),
                        this::haareHerunterlassenBitte_EntryReEntry),
                        exitSt(bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry));
                res.addAll(naschereifrage.getAntwortActions());
                return res.build();
            }
            default:
                throw new IllegalStateException("Unexpected state: " + stateComp.getState());
        }
    }

    private boolean frageNachRapunzelsMutterSinnvoll() {
        return counterDao.get(HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT) > 0
                && !loadSC().memoryComp().isKnown(
                RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU);
    }

    private boolean frageNachHelfenSinnvoll() {
        return counterDao.get(HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT) > 0
                && !loadSC().memoryComp().isKnown(
                RAPUNZELS_FREIHEITSWUNSCH);
    }

    private static PraedikatMitEinerObjektleerstelle bittenHaareHerunterzulassenPraedikat() {
        return BITTEN
                .mitLexikalischerKern(HINUNTERLASSEN
                        .mit(IHRE_HAARE)
                        .mitAdverbialerAngabe(
                                // "wieder hinunterlassen": Das "wieder" gehört
                                // quasi zu "hinunter", beides zusammen ("wieder hinunter")
                                // bildet praktisch die adverbiale "Wohin?"-Bestimmung.
                                new AdverbialeAngabeSkopusVerbWohinWoher("wieder")));
    }

    private boolean herzAusschuettenMoeglich() {
        return counterDao.get(HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT) == 0
                && loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG)
                >= FeelingIntensity.DEUTLICH
                && feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                >= FeelingIntensity.DEUTLICH;
    }

    private void gespraechBeginnen_EntryReEntryImmReEntry() {
        counterDao.inc(GESPRAECH_BEGONNEN_ODER_UNMITTELBAR_FORTGESETZT);

        if (!isSchonBegruesstMitSC()) {
            begruessen_EntryReEntry();
            return;
        }

        gespraechBeginnenOhneBegruessung();
    }

    private void gespraechBeginnenOhneBegruessung() {
        final int zuneigungSCTowardsRapunzel =
                loadSC().feelingsComp().getFeelingTowardsForActionsMitEmpathischerSchranke(
                        RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);

        final int zuneigungRapunzelTowardsSC =
                feelingsComp.getFeelingTowardsForActionsMitEmpathischerSchranke(
                        SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);

        final AltTimedDescriptionsBuilder alt = altTimed();

        if (zuneigungSCTowardsRapunzel <= FeelingIntensity.MERKLICH) {
            alt.add(paragraph("„Die letzen Tage waren ziemlich warm“, sagst du")
                            .timed(secs(10)),
                    paragraph("„Nachts war es in letzter Zeit ziemlich kalt“, wirfst du",
                            "in den Raum")
                            .timed(secs(10)));
        }

        if (zuneigungSCTowardsRapunzel >= -FeelingIntensity.NUR_LEICHT) {
            alt.add(neuerSatz(PARAGRAPH,
                    "„Ist ja doch ein wenig eng hier oben, oder?“, bemerkst du. „",
                    zuneigungRapunzelTowardsSC <= FeelingIntensity.MERKLICH ?
                            "Meinst du?" : "Stimmt schon",
                    "“, gibt",
                    anaph().nomK(),
                    "zurück").timed(secs(15)));

            if (loadSC().memoryComp().isKnown(RAPUNZELS_NAME)
                    && counterDao.get(GEFRAGT_NACH_RAPUNZELN) == 0
                    && duzen(zuneigungRapunzelTowardsSC)) {
                alt.add(paragraph(
                        "„Du magst also Rapunzeln?“ – Sie strahlt übers",
                        "ganze Gesicht. „Und wie! Manchmal bringt",
                        "mir die Alte welche aus ihrem Garten mit.",
                        "Frisch gezupft – die sind großartig, sag ich dir!“")
                        .timed(secs(20))
                        .withCounterIdIncrementedIfTextIsNarrated(GEFRAGT_NACH_RAPUNZELN));
            }
        }

        if (duzen(zuneigungRapunzelTowardsSC) &&
                zuneigungRapunzelTowardsSC >= FeelingIntensity.DEUTLICH) {
            alt.add(duParagraph("willst",
                    "gerade anfangen, zu sprechen, da fragt Rapunzel:",
                    "„Erzähl mir vom Wald!“ „Naja“, sagst du und erzählst etwas",
                    "langatmig von Hasen und Raben").timed(mins(2)));
        }

        if (counterDao.get(GESPRAECH_BEGONNEN_ODER_UNMITTELBAR_FORTGESETZT) >= 3) {
            alt.addAll(altParagraphs("„Nachts ist es kälter als draußen“, sagst du.",
                    anaph().nomK().capitalize(),
                    "schaut dich",
                    ImmutableList.of("verständlos", "verwirrt", "irritiert"),
                    "an").timed(secs(10)));
        }

        n.narrateAlt(alt);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.25f, FeelingIntensity.MERKLICH);
        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG,
                0.3f, FeelingIntensity.STARK);

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void begruessen_EntryReEntry() {
        scBegruesst();
        rapunzelBeantwortetBegruessung();
    }

    private void scBegruesst() {
        final SubstantivischePhrase anaph = anaph();

        final AltTimedDescriptionsBuilder alt = altTimed();

        if (counterDao.get(SC_BEGRUESST) == 0) {
            alt.add(neuerSatz("„Hallihallo!“, sagst du und lächelst breit")
                    .timed(secs(5))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            SC_BEGRUESST));
        } else {
            final int zuneigungSCTowardsRapunzel =
                    loadSC().feelingsComp()
                            .getFeelingTowardsForActionsMitEmpathischerSchranke(
                                    RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
            alt.add(neuerSatz("„Hallo, da bin ich wieder!“ sprichst du",
                    anaph.akkK(),
                    "an")
                    .timed(secs(5)));

            if (zuneigungSCTowardsRapunzel >= FeelingIntensity.MERKLICH) {
                if (duzen(zuneigungSCTowardsRapunzel)) {
                    alt.add(du("schaust ", anaph.akkK(),
                            "an. „Schön, dich wiederzusehen, sagst du")
                            .timed(secs(5))
                    );
                } else {
                    alt.add(du("schaust ", anaph.akkK(),
                            "an. „Schön, euch wiederzusehen, sagst du")
                            .timed(secs(5)));
                }
            }
        }

        n.narrateAlt(alt);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.2f, FeelingIntensity.MERKLICH);

        setSchonBegruesstMitSC(true);
    }

    private void rapunzelBeantwortetBegruessung() {
        final boolean scBereitsZuvorSchonEinmalGetroffen =
                counterDao.get(RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG) > 0;
        final SubstantivischePhrase anaph = anaph();
        @Nullable final Personalpronomen persPron = n.getAnaphPersPronWennMgl(RAPUNZEL);

        final AltDescriptionsBuilder alt = alt();

        final ImmutableList<Satz> altReaktionSaetze =
                feelingsComp.altReaktionBeiBegegnungMitScSaetze(anaph);

        // Könnte leer sein
        final ImmutableList<Satz> altEindruckSaetze =
                feelingsComp.altEindruckAufScBeiBegegnungSaetze(anaph);

        // Könnte ebenfalls leer sein
        final ImmutableList<AdverbialeAngabeSkopusVerbAllg> altEindruckAdvAngaben =
                feelingsComp.altEindruckAufScBeiBegegnungAdvAngaben(anaph);

        final int zuneigungTowardsSC =
                feelingsComp.getFeelingTowardsForActionsMitEmpathischerSchranke(
                        SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);

        if (zuneigungTowardsSC >= -FeelingIntensity.MERKLICH) {
            alt.addAll(altNeueSaetze(
                    anaph.nomK(),
                    "heißt dich",
                    altEindruckAdvAngaben.stream()
                            .map(a -> a.getDescription(anaph.getPerson(), anaph.getNumerus())),
                    "willkommen"));
        }

        if (zuneigungTowardsSC >= -FeelingIntensity.MERKLICH
                && zuneigungTowardsSC <= FeelingIntensity.DEUTLICH) {
            alt.addAll(altNeueSaetze(
                    "„Hallo“, sagt",
                    anaph.nomK(),
                    altEindruckAdvAngaben.stream()
                            .map(a -> a.getDescription(anaph.getPerson(), anaph.getNumerus())))
                    .beendet(SENTENCE));
        }

        if (zuneigungTowardsSC <= -FeelingIntensity.SEHR_STARK) {
            alt.add(neuerSatz("„Verschwinde! Sofort!“, schreit",
                    anaph.nomK(),
                    "dich an")
                            .beendet(PARAGRAPH),
                    neuerSatz("Aber",
                            anaph.nomK(),
                            "schreit nur: „Raus! Auf der Stelle!“")
                            .beendet(PARAGRAPH));
        } else if (zuneigungTowardsSC == -FeelingIntensity.STARK) {
            alt.add(neuerSatz("„Was willst du hier?“, antwortet",
                    anaph.nomK(),
                    "ungehalten und schaut dich vergrätzt an")
                            .beendet(PARAGRAPH),
                    neuerSatz("„Verschwinde!“, versetzt",
                            anaph.nomK())
                            .beendet(PARAGRAPH)
            );
        } else if (zuneigungTowardsSC == -FeelingIntensity.DEUTLICH) {
            alt.add(neuerSatz("„Was willst du hier?“, gibt",
                    anaph.nomK(),
                    "zur Antwort")
                    .beendet(PARAGRAPH)
            );
            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.add(neuerSatz("„Was willst du noch?“, faucht",
                        anaph.nomK(),
                        "dich geradezu an")
                                .beendet(PARAGRAPH),
                        neuerSatz("„Was willst du wieder hier?“, fragt",
                                anaph.nomK(),
                                "unfreundlich")
                                .beendet(PARAGRAPH)
                );
            }
        } else if (zuneigungTowardsSC == -FeelingIntensity.MERKLICH) {
            alt.add(
                    du("erhältst", "nur ein knappes Nicken zurück")
                            .mitVorfeldSatzglied("nur ein knappes Nicken")
                            .beendet(PARAGRAPH),
                    neuerSatz(anaph.nomK(),
                            "nickt dir nur knapp zu")
                            .beendet(SENTENCE)
            );
        } else if (zuneigungTowardsSC == -FeelingIntensity.NUR_LEICHT) {
            alt.add(neuerSatz("„Hallo!“, entgegnet",
                    anaph.nomK(),
                    "knapp")
                            .beendet(SENTENCE),
                    neuerSatz("„Hallo!“, versetzt",
                            anaph.nomK(),
                            "kurz angebunden")
                            .beendet(SENTENCE),
                    neuerSatz("– Schweigen")
                            .beendet(PARAGRAPH));
        } else if (zuneigungTowardsSC == FeelingIntensity.NEUTRAL) {
            alt.add(neuerSatz(anaph.nomK(),
                    "erwidert die Begrüßung")
                            .beendet(SENTENCE),
                    neuerSatz(anaph.nomK(),
                            "gibt den Gruß zurück")
                            .beendet(SENTENCE),
                    neuerSatz("„Ja…“, gibt",
                            anaph.nomK(),
                            "zurück")
                            .beendet(PARAGRAPH));
            if (persPron != null) {
                alt.add(neuerSatz("„Ja…“,",
                        "ist",
                        persPron.possArt().vor(F).datStr(), // ihre
                        "Antwort")
                        .beendet(PARAGRAPH));
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.NUR_LEICHT) {
            // FIXME "Hallo antwortet die junge Frau und die
            //  junge Frau scheint überrascht, dich wiederzusehen" - wie kommt das?
            alt.addAll(altNeueSaetze(PARAGRAPH,
                    "„Hallo“, antwortet",
                    anaph.nomK(),
                    altEindruckSaetze.stream()
                            .map(s -> s.mitAnschlusswort("und").altVerzweitsaetze())));
            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.addAll(altNeueSaetze(
                        "„Ach, ihr seid es wieder.“",
                        altReaktionSaetze.stream()
                                .flatMap(s -> s.altVerzweitsaetze().stream()))
                        .beendet(PARAGRAPH));
                alt.addAll(altNeueSaetze(
                        "„Oh, ihr seid es wieder.“",
                        altReaktionSaetze.stream()
                                .flatMap(s -> s.altVerzweitsaetze().stream()))
                        .beendet(PARAGRAPH));
                alt.add(neuerSatz("„Ich hatte mich schon gefragt, ob ihr mal wieder ",
                        "vorbeischaut! Willkommen.“ –")
                        .beendet(SENTENCE)
                );
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.MERKLICH) {
            alt.add(neuerSatz("„Ah, hallo! – Willkommen!“",
                    anaph.nomK().capitalize(),
                    "schaut dich freundlich an")
            );

            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.add(neuerSatz("„Ihr seid's! Hallo!“, antwortet",
                        anaph.nomK()),
                        neuerSatz(anaph.nomK(),
                                "freut sich, dass du wieder da bist"));
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.DEUTLICH) {
            alt.add(neuerSatz("„Oh, wie schön“, antwortet",
                    anaph.nomK(),
                    "„ich freue mich, dich zu sehen!“")
                    .beendet(PARAGRAPH));

            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.addAll(altNeueSaetze(
                        "„Oh, wie schön, dasss du wieder da bist“, antwortet",
                        anaph.nomK(), // autom. Phorik-Kandidat!
                        altEindruckSaetze.stream()
                                .map(s -> s.mitAnschlusswort("und").getSatzanschlussOhneSubjekt()))
                        .beendet(PARAGRAPH));
                alt.add(neuerSatz("„Schön, dich wiederzusehen!“, freut",
                        anaph.nomK(),
                        "sich"));
                alt.add(neuerSatz(
                        "„Hast du den Falken gesehen?“, sprudelt",
                        anaph.nomK(),
                        "hervor. „Die armen Rotkehlchen!“ Du reagierst sehr verständnisvoll")
                        .dann());
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.STARK) {
            alt.add(neuerSatz(anaph.nomK(), "strahlt dich nur an"));

            if (persPron != null) {
                alt.add(neuerSatz(persPron.possArt().vor(PL_MFN).nomStr(),
                        "Augen strahlen, als",
                        persPron.nomK(),
                        "dich begrüßt"));
            }

            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.addAll(altNeueSaetze(
                        "„Endlich! Ich hatte dich schon",
                        "erwartet“, antwortet",
                        anaph.nomK(), // autom. Phorik-Kandidat
                        "dir",
                        altEindruckSaetze.stream()
                                .map(s -> s.mitAnschlusswort("und").getVerbzweitsatzStandard()))
                        .beendet(PARAGRAPH));

                alt.add(neuerSatz("„Oh, eine Freude, dich wiederzusehen!“"),
                        neuerSatz("„Endlich bist du wieder da! Ich habe dich schon vermisst.“")
                );
            }
        }

        n.narrateAlt(alt.build(), secs(5), RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG);

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private boolean scKenntRapunzelsNamenNicht() {
        return !loadSC().memoryComp().isKnown(RAPUNZELS_NAME);
    }

    private void nachNameFragen() {
        scFragtNachName();
        rapunzelBeantwortenNamensfrage();
    }

    private void scFragtNachName() {
        final SubstantivischePhrase anaph = anaph();
        n.narrateAlt(secs(10),
                du("fragst", anaph.akkK(), "nach",
                        anaph.possArt().vor(M).datStr(), "Namen"),
                du("fragst", anaph.akkK(), ", wie",
                        anaph.persPron().nomK(), "heißt"),
                du("möchtest",
                        anaph.possArt().vor(M).akkStr(), "Namen wissen"));

        setSchonBegruesstMitSC(true);
    }

    private void rapunzelBeantwortenNamensfrage() {
        final SubstantivischePhrase anaph = anaph();

        if (feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                >= FeelingIntensity.MERKLICH) {
            n.narrate(neuerSatz(
                    "„Ach“, sagt",
                    anaph.nomK(), ", „du kannst mich einfach Rapunzel nennen.“",
                    anaph.persPron().nomK().capitalize(), "grinst verlegen.",
                    "„Ich ess die so gern.“")
                    .beendet(PARAGRAPH)
                    .timed(secs(10)));

            feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                    0.2f, FeelingIntensity.DEUTLICH);
            loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL,
                    ZUNEIGUNG_ABNEIGUNG, 0.3f, FeelingIntensity.DEUTLICH);
            loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_NAME);
            loadSC().feelingsComp().requestMoodMin(ZUFRIEDEN);
        } else {
            n.narrateAlt(secs(10),
                    satzanschluss(
                            ", aber",
                            anaph.nomK(), "presst nur die Lippen aufeinander und sagt kein Wort")
                            .beendet(PARAGRAPH),
                    satzanschluss(
                            ", aber",
                            anaph.nomK(), "gibt dir keine Antwort")
                            .beendet(PARAGRAPH));

            feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                    0.15f, FeelingIntensity.MERKLICH);

            loadSC().feelingsComp().requestMoodMax(NEUTRAL);
        }
    }

    private void unterhalten_allg() {
        final SubstantivischePhrase anaph = anaph();

        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG,
                0.35f, FeelingIntensity.STARK);
        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.35f, FeelingIntensity.DEUTLICH);

        final int zuneigungZuRapunzel =
                loadSC().feelingsComp().getFeelingTowardsForActionsMitEmpathischerSchranke(RAPUNZEL,
                        ZUNEIGUNG_ABNEIGUNG);
        final int zuneigungRapunzelZumSC =
                feelingsComp.getFeelingTowardsForActionsMitEmpathischerSchranke(SPIELER_CHARAKTER,
                        ZUNEIGUNG_ABNEIGUNG);

        if (counterDao.get(RapunzelJahreszeitenFrageMitAntworten.Counter.FRAGE_BEANTWORTET) == 0
                && zuneigungZuRapunzel >= FeelingIntensity.DEUTLICH) {
            jahreszeitenFrage.nscStelltFrage();
            return;
        }

        if (counterDao.get(RapunzelKugelherkunftsfrageMitAntworten.Counter.FRAGE_BEANTWORTET) == 0
                && zuneigungRapunzelZumSC >= FeelingIntensity.MERKLICH
                && duzen(zuneigungRapunzelZumSC)
                && world.hasSameOuterMostLocationAsSC(GOLDENE_KUGEL)
                && anaph instanceof Personalpronomen) {
            kugelherkunftsfrage.nscStelltFrage();
            return;
        }

        if (counterDao.get(NaschereifrageMitAntworten.Counter.FRAGE_BEANTWORTET) == 0
                && zuneigungRapunzelZumSC >= FeelingIntensity.DEUTLICH
                && duzen(zuneigungRapunzelZumSC)
                && counterDao.get(HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT) > 0) {
            naschereifrage.nscStelltFrage();
            return;
        }

        final AltTimedDescriptionsBuilder alt = altTimed();

        if (zuneigungZuRapunzel <= FeelingIntensity.NEUTRAL) {
            alt.add(neuerSatz(SENTENCE, "ihr wechselt ein paar Worte")
                    .timed(mins(1)));
        }

        if (zuneigungZuRapunzel >= FeelingIntensity.NUR_LEICHT) {
            alt.add(du("redest",
                    "ganz freundlich mit",
                    anaph.datK())
                    .mitVorfeldSatzglied("ganz freundlich")
                    .undWartest()
                    .timed(mins(1)));
        }

        {
            final Konstituentenfolge antwort;
            if (zuneigungRapunzelZumSC < FeelingIntensity.DEUTLICH) {
                antwort = joinToKonstituentenfolge(
                        anaph.persPron().nomK().capitalize(), "guckt dich nur verunsichert an");
            } else {
                antwort = joinToKonstituentenfolge(
                        anaph.persPron().nomK().capitalize(),
                        "grinst dich an. „Und wenn nicht?“, fragt",
                        anaph.persPron().nomK(),
                        "zurück");
            }

            alt.add(neuerSatz("„",
                    duzen(zuneigungRapunzelZumSC) ? "Lebst du" : "Lebt Ihr",
                    "hier allein?“, fragst du",
                    anaph.akkK(),
                    ".",
                    antwort)
                    .timed(secs(20))
            );
        }

        if (zuneigungZuRapunzel >= FeelingIntensity.NUR_LEICHT
                && zuneigungRapunzelZumSC >= -FeelingIntensity.NUR_LEICHT) {
            alt.add(neuerSatz("„",
                    duzen(zuneigungRapunzelZumSC) ? "Wohnst du" : "wohnt Ihr",
                    "hier das ganze Jahr? Wird es nicht sehr kalt im Winter?“ „Ach, ich habe",
                    "ja einen Ofen“, sagt",
                    anaph.nomK()).timed(secs(15)));
        }

        {
            final Collection<Konstituentenfolge> altAntworten;
            if (zuneigungRapunzelZumSC >= FeelingIntensity.DEUTLICH) {
                altAntworten = joinToAltKonstituentenfolgen(
                        "„",
                        ImmutableList.of("Absolut", "Klar"),
                        "! Deshalb freu ich mich ja immer so, wenn mal jemand Nettes",
                        "vorbeischaut.“",
                        anaph().nomK().capitalize(),
                        "zwinkert dir zu. – Oder du hast dir das eingebildet");
            } else {
                altAntworten = ImmutableList.of(
                        joinToKonstituentenfolge("Aber", anaph().nomK(), "antwortet nicht"),
                        joinToKonstituentenfolge("Aber", anaph().nomK(), "bleibt still"));
            }

            alt.addAll(altNeueSaetze("„Wird",
                    duzen(zuneigungZuRapunzel) ? "dir" : "euch",
                    "hier nicht langweilig?“, fragst du.",
                    altAntworten).timed(secs(20)));
        }

        if (counterDao.get(HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT) > 0) {
            alt.add(du("siehst",
                    "dich um und dein Blick fällt auf die nackte Mauer.",
                    "„Was ist das hier eigentlich für ein Turm“, fragst du in den Raum.",
                    "„Der gehört der Zauberin“, sagt",
                    anaph().nomK(),
                    zuneigungRapunzelZumSC >= FeelingIntensity.MERKLICH
                            && duzen(zuneigungZuRapunzel) ?
                            "Der muss sehr alt sein. Und magisch, wenn du mich fragst" : null,
                    ".“").timed(secs(30)));
        }

        if (zuneigungZuRapunzel >= FeelingIntensity.MERKLICH &&
                scHatSeltsameSacheMitFroschErlebt()) {
            alt.add(du("erzählst",
                    anaph.datK(),
                    "von dieser seltsamen Sache, die du mit dem Frosch erlebt hast.",
                    anaph.persPron().nomK().capitalize(),
                    "schaut dich nachdenklich an")
                    .timed(mins(4)));
        }

        if (zuneigungZuRapunzel >= FeelingIntensity.MERKLICH
                && zuneigungRapunzelZumSC >= FeelingIntensity.NUR_LEICHT
                && counterDao.get(NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT) == 0) {
            alt.add(neuerSatz(PARAGRAPH,
                    "„Ich habe im Leben noch nie so lange Haare gesehen!“, sagst du.",
                    "„Wirklich?“",
                    anaph.nomK().capitalize(),
                    "schaut dich überrascht an.",
                    "„Und die jungen Frauen draußen, wie…?“ Naja, du hüstelst verlegen; dann",
                    "fällt dir ein: „Dir stehen sie am besten.“",
                    anaph.persPron().nomK().capitalize(),
                    "lächelt")
                    .timed(secs(20))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            NOCH_NIE_SO_LANGE_HAARE_GESEHEN_GESAGT));
        }

        if (zuneigungRapunzelZumSC >= FeelingIntensity.DEUTLICH) {
            alt.add(neuerSatz(anaph.nomK(),
                    "schwärmt vom Sonnenaufgang über dem Blätterdach.",
                    capitalize(anaph.possArt().vor(PL_MFN).nomStr()),
                    "Augen leuchten")
                    .timed(mins(2)));
        }

        if (zuneigungZuRapunzel >= FeelingIntensity.DEUTLICH
                && duzen(zuneigungZuRapunzel)
                && counterDao.get(SCHON_IMMER_GEWUENSCHT_GEFRAGT) == 0) {
            alt.add(
                    neuerSatz(
                            "„Was hat du dir eigentlich schon immer gewünscht?“ –",
                            anaph.nomK().capitalize(),
                            "denkt kurz nach. „Soll ich ehrlich sein?“",
                            anaph.persPron().nomK().capitalize(), "hält kurz inne",
                            "„Fliegen zu können, das hab ich mir immer gewünscht. Hoch oben im",
                            "Wind. Wie die Gänse, die im Herbst über den Wald ziehen.“",
                            anaph.persPron().nomK().capitalize(),
                            "lächelt verlegen").timed(secs(30))
                            .withCounterIdIncrementedIfTextIsNarrated(
                                    SCHON_IMMER_GEWUENSCHT_GEFRAGT));
        }

        n.narrateAlt(alt);

        setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMin(BETRUEBT);
    }

    private boolean scHatSeltsameSacheMitFroschErlebt() {
        return ((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ)).stateComp()
                .hasState(FroschprinzState.HAT_FORDERUNG_GESTELLT,
                        FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                        FroschprinzState.WARTET_AUF_SC_BEIM_SCHLOSSFEST,
                        FroschprinzState.HAT_HOCHHEBEN_GEFORDERT,
                        FroschprinzState.BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN,
                        FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE,
                        FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN);
    }

    private void herzAusschuetten() {
        final SubstantivischePhrase anaph = anaph();
        final SubstantivischePhrase desc = getDescription();

        final String wovonHerzBewegtDat;
        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            wovonHerzBewegtDat = anaph.possArt().vor(M).datStr() // "ihrem"
                    + " Gesang";
        } else {
            wovonHerzBewegtDat = anaph.possArt().vor(PL_MFN).datStr() // "ihrem"
                    + " glänzenden Locken";
        }

        n.narrate(du("erzählst",
                ", dass von",
                wovonHerzBewegtDat,
                "dein Herz so sehr sei bewegt worden, dass es dir",
                "keine Ruhe gelassen und du",
                anaph.persPron().akkK(),
                "selbst habest sehen müssen.",
                "Da verliert",
                desc.nomK(),
                "alle Angst und es bricht aus",
                desc.persPron().datK(),
                "heraus:",
                "Eine alte Zauberin hätte",
                desc.persPron().akkK(),
                desc.possArt().vor(PL_MFN).datStr(),  // "ihren"
                "Eltern fortgenommen, seit",
                desc.possArt().vor(N).datStr(),  // "ihrem"
                "zwölften Jahre sei",
                desc.persPron().nomK(), // "sie"
                "in diesen Turm geschlossen")
                .timed(mins(1))
                .withCounterIdIncrementedIfTextIsNarrated(
                        HERZ_AUSGESCHUETTET_ZAUBERIN_GESCHICHTE_ERZAEHLT));

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                1.5f, FeelingIntensity.DEUTLICH);

        loadSC().feelingsComp().upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.5f, FeelingIntensity.STARK);

        setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMin(BEWEGT);
    }

    private void nachRapunzelsZauberinFragen() {
        n.narrate(neuerSatz(
                "„Die Frau mit dieser… Nase – das ist deine…“ –",
                "„Ja, sie hält mich gefangen“, antwortet",
                anaph().nomK(),
                "„Aber sie ist gut zu mir.“")
                .timed(secs(20)));

        loadSC().memoryComp()
                .narrateAndUpgradeKnown(
                        RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU);

        setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMax(ETWAS_GEKNICKT);
    }

    private void fragenWieSCHelfenKann() {
        final SubstantivischePhrase anaph = anaph();

        n.narrate(neuerSatz("Wie kannst du ihr helfen, so fragst du",
                anaph.akkK(),
                ". „Frei will ich sein“, sagt",
                anaph.persPron().akkK(),
                ", „durch den Wald laufen, auf Bäume klettern!“")
                .timed(secs(25)));

        loadSC().feelingsComp().upgradeFeelingsTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG,
                0.5f, FeelingIntensity.STARK);

        loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_FREIHEITSWUNSCH);

        setSchonBegruesstMitSC(true);
        loadSC().feelingsComp().requestMoodMin(BEWEGT);
    }

    private boolean rapunzelsFreiheitswunschBekannt() {
        return loadSC().memoryComp().isKnown(RAPUNZELS_FREIHEITSWUNSCH);
    }

    private void rapunzelRettungZusagen() {
        if (counterDao.get(RETTUNG_ZUGESAGT) == 0) {
            n.narrate(neuerSatz("„Du hast mein Wort!“",
                    "Du siehst ihr in die Augen. „Vertrau mir, mir fällt etwas ein! Wir bringen",
                    "dich hier raus.“")
                    .timed(secs(10)).withCounterIdIncrementedIfTextIsNarrated(RETTUNG_ZUGESAGT));
        } else {
            n.narrate(du("siehst",
                    anaph().akkK(),
                    "bedeutungsschwer an: „Vertrau mir, wir bringen dich hier raus!“ –")
                    .beendet(PARAGRAPH)
                    .timed(secs(10)).withCounterIdIncrementedIfTextIsNarrated(RETTUNG_ZUGESAGT));

            final int rettungZugesagtCount = counterDao.get(RETTUNG_ZUGESAGT);
            final int zuneigungZuSC =
                    feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);
            final AltDescriptionsBuilder alt = alt();

            if ((rettungZugesagtCount <= 2 && zuneigungZuSC >= -FeelingIntensity.NUR_LEICHT)
                    || zuneigungZuSC >= FeelingIntensity.STARK) {
                alt.addAll(altAnsehenSaetze(anaph(), getPersonalpronomenSC(), FREUDESTRAHLEND));
            }

            if ((rettungZugesagtCount == 3 && zuneigungZuSC >= FeelingIntensity.NEUTRAL)
                    || zuneigungZuSC >= FeelingIntensity.STARK) {
                alt.addAll(altAnsehenSaetze(anaph(), getPersonalpronomenSC(),
                        BEGEISTERT));
            }

            if (rettungZugesagtCount == 4 && zuneigungZuSC >= -FeelingIntensity.DEUTLICH) {
                alt.add(neuerSatz(anaph().nomK(), "zieht eine Augenbraue hoch"));
                alt.addAll(altEindrueckSaetze(anaph(), SKEPTISCH.mitGraduativerAngabe("etwas")));
            }

            if (rettungZugesagtCount == 5
                    && zuneigungZuSC >= -FeelingIntensity.DEUTLICH
                    && zuneigungZuSC <= FeelingIntensity.STARK) {
                alt.add(neuerSatz(anaph().nomK(),
                        "schaut dich an, und du siehst den Zweifel",
                        "in ihrem Blick"));
            }

            if ((rettungZugesagtCount == 6 && zuneigungZuSC <= FeelingIntensity.STARK)
                    || zuneigungZuSC <= -FeelingIntensity.DEUTLICH) {
                alt.addAll(altEindrueckSaetze(anaph(), GENERVT));
            }

            if ((rettungZugesagtCount >= 7 && zuneigungZuSC <= FeelingIntensity.STARK)
                    || zuneigungZuSC <= -FeelingIntensity.DEUTLICH) {
                alt.add(neuerSatz("„Jaja“, sagt", anaph().nomK(),
                        ", „wie oft ich das schon gehört habe…“"),
                        neuerSatz("„Das wird ja doch nichts“, gibt",
                                anaph().nomK(), ", zurück"));
            }

            alt.addIfOtherwiseEmpty(neuerSatz(anaph().nomK(), "wendet den Blick ab"));

            n.narrateAlt(
                    alt.timed(secs(5)).withCounterIdIncrementedIfTextIsNarrated(RETTUNG_ZUGESAGT));

            feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                    -0.4f, -FeelingIntensity.STARK);
        }

        memoryComp.narrateAndUpgradeKnown(SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT);
        loadSC().memoryComp().narrateAndUpgradeKnown(SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT);

        setSchonBegruesstMitSC(true);
        gespraechspartnerBeendetGespraech();
    }

    private void haareHerunterlassenBitte_EntryReEntry() {
        final int zuneigungZuRapunzel =
                loadSC().feelingsComp().getFeelingTowardsForActionsMitEmpathischerSchranke
                        (RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);

        final Nominalphrase desc = getDescription(true);
        if (duzen(zuneigungZuRapunzel)) {
            n.narrateAlt(secs(10),
                    neuerSatz("„Weißt du“, wendest du dich an",
                            desc.akkK(),
                            ", „eigentlich wollte ich",
                            "nur schauen, ob's dir gut geht. Lässt du mich wieder hinunter?“"));
        } else {
            n.narrateAlt(secs(10),
                    neuerSatz(PARAGRAPH,
                            "„Ich wollte euch nicht belästigen“, sprichst du",
                            anaph().akkK(),
                            "an,",
                            "„lasst mich wieder hinunter und ich lasse euch euren Frieden.“")
                            .beendet(PARAGRAPH)
            );
        }

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.25f, FeelingIntensity.MERKLICH);

        setSchonBegruesstMitSC(true);
        haareHerunterlassen();
        gespraechspartnerBeendetGespraech();
    }

    private void haareHerunterlassenBitte_ExitImmReEntry() {
        final SubstantivischePhrase anaph = anaph(true);
        final AltTimedDescriptionsBuilder alt = altTimed();

        alt.add(
                neuerSatz(PARAGRAPH, "„Jetzt muss ich aber gehen“, sagst du",
                        "unvermittelt und blickst zum Fenster hin")
                        .timed(secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(SENTENCE, "„Ich muss wieder hinaus in die Welt!“,"
                        + "sagst du")
                        .timed(secs(10)),
                neuerSatz(PARAGRAPH, "„Dann will ich wieder ins",
                        "Abenteuer hinaus“, sagst du",
                        ZU.getDescription(anaph))
                        .timed(secs(15))
        );

        final int zuneigungSCZuRapunzel =
                loadSC().feelingsComp().getFeelingTowardsForActionsMitEmpathischerSchranke(
                        RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
        if (duzen(zuneigungSCZuRapunzel)) {
            alt.add(
                    neuerSatz(PARAGRAPH, "„Lässt du mich wieder hinunter?“, fragst du in die "
                            + "Stille hinein")
                            .timed(secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        if (zuneigungSCZuRapunzel >= FeelingIntensity.STARK) {
            alt.add(
                    du(PARAGRAPH, "spürst",
                            "plötzlich neuen Tatendrang in dir. „Lass mich gehen“,",
                            "sagst du, „bald bin ich wieder zurück!“")
                            .mitVorfeldSatzglied("plötzlich")
                            .timed(secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        n.narrateAlt(alt);

        setSchonBegruesstMitSC(true);

        loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);

        haareHerunterlassen();
        gespraechspartnerBeendetGespraech();
    }

    private void hatNachKugelGefragt_haareHerunterlassenBitte_Exit() {
        final SubstantivischePhrase anaph = anaph(true);
        final AltTimedDescriptionsBuilder alt = altTimed();

        alt.add(neuerSatz("Doch du reagierst gar nicht darauf, sondern forderst",
                anaph.akkStr(), // Missverständnis mit Haaren möglich
                "nur auf, die Haare",
                "wieder heruterzulassen, dass du wieder gehen kannst")
                .timed(secs(15))
                .beendet(PARAGRAPH)
        );

        final int zuneigungSCZuRapunzel =
                loadSC().feelingsComp().getFeelingTowardsForActionsMitEmpathischerSchranke(
                        RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
        if (duzen(zuneigungSCZuRapunzel)) {
            alt.add(
                    neuerSatz(PARAGRAPH, "„Lass mich wieder gehen!“, gibst du zurück")
                            .timed(secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        n.narrateAlt(alt);

        setSchonBegruesstMitSC(true);
        haareHerunterlassen();
        gespraechspartnerBeendetGespraech();
    }

    private void haareHerunterlassen() {
        rapunzelLaesstHaareZumAbstiegHerunter();

        // TODO Anfrage nach Storytelling / Narrative Designer bei Github einstellen?
        //  Inhalt: Storytelling Grimms Märchen deutsch rein textbasiert, kein Zufall
        //  (kein Auswürfeln), aber simulierte Welt
    }

    public void rapunzelLaesstHaareZumAbstiegHerunter() {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            if (!loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                n.narrate(du(PARAGRAPH, "siehst", " über dir eine Bewegung: "
                        + "Aus dem Turmfenster fallen auf einmal lange, golden "
                        + "glänzende Haare bis zum Boden herab")
                        .timed(secs(10))
                        .dann());
            } else {
                n.narrate(du(PARAGRAPH, "siehst", " über dir eine Bewegung: "
                        + "Aus dem Turmfenster fallen wieder die "
                        + "langen, golden glänzenden Haare bis zum Boden herab")
                        .timed(secs(10))
                        .dann());
            }
            world.loadSC().memoryComp().narrateAndUpgradeKnown(RAPUNZELS_HAARE);
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            final Nominalphrase rapunzelDesc = getDescription(true);

            final AltDescriptionsBuilder alt = alt();

            final ImmutableList<Satz> altReaktionSaetze
                    = feelingsComp.altReaktionWennTargetGehenMoechteSaetze(rapunzelDesc);

            alt.addAll(altNeueSaetze(
                    altReaktionSaetze.stream()
                            .flatMap(s -> s.altVerzweitsaetze().stream()),
                    ",",
                    altDannHaareFestbinden(rapunzelDesc).stream()
                            .flatMap(d -> d.altTextDescriptions().stream())
                            .map(TextDescription::toSingleKonstituente)
            ));

            alt.add(neuerSatz(rapunzelDesc.nomK(),
                    "wickelt",
                    rapunzelDesc.possArt().vor(PL_MFN).akkStr(), // "ihre"
                    "Haare wieder um den Fensterhaken")
                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));

            //  FIXME "Oh, ich wünschte, ihr könntet noch einen Moment bleiben!" antwortet RAPUNZEL.
            //    Aber sie knotet doch ihrer Haare wieder über den Haken am Fenster"

            n.narrateAlt(alt, secs(10));
        }

        stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN);
        // Ggf. steigt die Zauberin als Reaktion daran herunter
    }

    private static ImmutableSet<AbstractDescription<?>> altDannHaareFestbinden(
            final Nominalphrase rapunzelDesc) {
        return alt()
                .add(satzanschluss(
                        "dann bindet",
                        rapunzelDesc.persPron().nomK(), //"sie"
                        rapunzelDesc.possArt().vor(PL_MFN).akkStr(),// "ihre"
                        "Haare wieder um den Haken am Fenster"))
                .add(satzanschluss(
                        "dann knotet",
                        rapunzelDesc.persPron().nomK(), //"sie"
                        rapunzelDesc.possArt().vor(PL_MFN).akkStr(),// "ihre"
                        "Haare wieder um den Fensterhaken"))
                .add(satzanschluss(
                        "dann bindet",
                        rapunzelDesc.persPron().nomK(), //"sie"
                        rapunzelDesc.possArt().vor(PL_MFN).akkStr(),// "ihre"
                        "Haare wieder am Fenster fest"))
                .build();
    }

    public static boolean duzen(final int zuneigung) {
        return zuneigung > FeelingIntensity.DEUTLICH ||
                zuneigung <= -FeelingIntensity.DEUTLICH;
    }

    public void forgetAll() {
        jahreszeitenFrage.forgetAll();
        kugelherkunftsfrage.forgetAll();
        naschereifrage.forgetAll();

        counterDao.reset(Counter.class);
    }
}
