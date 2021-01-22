package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entryReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStNSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.base.Nominalphrase.DEIN_HERZ;
import static de.nb.aventiure2.german.base.Nominalphrase.EIN_GESPRAECH;
import static de.nb.aventiure2.german.base.Nominalphrase.IHRE_HAARE;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.Wortfolge.joinToWortfolge;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.DirektivesVerb.BITTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.AUSSCHUETTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINUNTERLASSEN;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
@SuppressWarnings("DuplicateBranchesInSwitch")
public class RapunzelTalkingComp extends AbstractTalkingComp {
    public static final String SC_BEGRUESST = "RapunzelTalkingComp_sc_begruesst";
    public static final String RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG =
            "RapunzelTalkingComp_Rapunzel_reagiert_auf_SC_Begruessung";
    private final RapunzelStateComp stateComp;
    private final FeelingsComp feelingsComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final TimeTaker timeTaker,
                               final Narrator n,
                               final World world,
                               final RapunzelStateComp stateComp,
                               final FeelingsComp feelingsComp,
                               final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZEL, db, timeTaker, n, world, initialSchonBegruesstMitSC);
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        switch (stateComp.getState()) {
            case UNAEUFFAELLIG:
                return ImmutableList.of();
            case STILL:
                // "Die junge Frau bitten ihre Haare wieder hinunterzulassen"
                return ImmutableList.of(
                        entryReEntrySt(
                                VerbSubjAkkPraep.BEGINNEN
                                        .mitAkk(EIN_GESPRAECH)
                                        .mit(getDescription()),
                                this::gespraechBeginnen_EntryReEntryImmReEntry),
                        entryReEntrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        st(
                                // FIXME Nur, wenn auch Rapunzel entsprechend Zutrauen
                                //  gefasst hat! Ansonsten wäre das creepy!
                                this::zuneigungDesSCZuRapunzelDeutlich,
                                // "Der jungen Frau dein Herz ausschütten"
                                AUSSCHUETTEN
                                        .mitDat(getDescription(true))
                                        .mit(DEIN_HERZ),
                                this::herzAusschuetten),
                        exitSt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(
                                VerbSubjAkkPraep.BEGINNEN
                                        .mitAkk(EIN_GESPRAECH)
                                        .mit(getDescription()),
                                this::gespraechBeginnen_EntryReEntryImmReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                VerbSubjAkkPraep.BEGINNEN
                                        .mitAkk(EIN_GESPRAECH)
                                        .mit(getDescription()),
                                this::gespraechBeginnen_EntryReEntryImmReEntry),
                        immReEntryStSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry),
                        immReEntryStNSCHatteGespraechBeendet(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_ExitImmReEntry)
                );
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
            default:
                throw new IllegalStateException("Unexpected state: " + stateComp.getState());
        }
    }

    private PraedikatOhneLeerstellen bittenHaareHerunterzulassenPraedikat() {
        return BITTEN
                .mitObj(getDescription(true))
                .mitLexikalischerKern(HINUNTERLASSEN
                        .mit(IHRE_HAARE)
                        .mitAdverbialerAngabe(
                                // "wieder hinunterlassen": Das "wieder" gehört
                                // quasi zu "hinunter", beides zusammen ("wieder hinunter")
                                // bildet praktisch die adverbiale "Wohin?"-Bestimmung.
                                new AdverbialeAngabeSkopusVerbWohinWoher("wieder")));
    }

    private boolean zuneigungDesSCZuRapunzelDeutlich() {
        return loadSC().feelingsComp().getFeelingTowards(RAPUNZEL,
                ZUNEIGUNG_ABNEIGUNG) >= FeelingIntensity.DEUTLICH;
    }

    private void gespraechBeginnen_EntryReEntryImmReEntry() {
        if (!isSchonBegruesstMitSC()) {
            begruessen_EntryReEntry();
            return;
        }

        n.narrateAlt(secs(15),
                neuerSatz(
                        // FIXME Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                        "„Die letzen Tage waren ziemlich warm“, sagst du")
                        .beendet(PARAGRAPH),
                neuerSatz(
                        // FIXME Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                        "„Nachts war es in letzter Zeit ziemlich kalt“, wirfst du",
                        "in den Raum")
                        .beendet(PARAGRAPH)
        );

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.25f, FeelingIntensity.MERKLICH);

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void begruessen_EntryReEntry() {
        scBegruesst();
        rapunzelBeantwortetBegruessung();
    }

    private void scBegruesst() {
        final SubstantivischePhrase anaph = anaph();

        final AltTimedDescriptionsBuilder alt = altTimed();

        if (db.counterDao().get(SC_BEGRUESST) == 0) {
            alt.add(neuerSatz("„Hallihallo!“, sagst du und lächelst breit")
                    .timed(secs(5))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            SC_BEGRUESST));
        } else {
            final int zuneigungSCTowardsRapunzel =
                    loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
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
                            .timed(secs(5))
                            .phorikKandidat(anaph, RAPUNZEL));
                }
            }
        }

        n.narrateAlt(alt);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.5f, FeelingIntensity.MERKLICH);

        setSchonBegruesstMitSC(true);
    }

    private void rapunzelBeantwortetBegruessung() {
        final boolean scBereitsZuvorSchonEinmalGetroffen =
                db.counterDao().get(RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG) > 0;

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
                feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);

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
                    anaph.nomStr(),
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
                    anaph.nomStr(),
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
                    // FIXME phorikKandidat() prüfen - überzählige entfernen!
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
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.STARK) {
            alt.add(neuerSatz(anaph.nomK(), "strahlt dich nur an"));

            if (persPron != null) {
                alt.add(neuerSatz(persPron.possArt().vor(PL_MFN).nomStr(),
                        "Augen strahlen, als",
                        persPron.nomStr(),
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

        // FIXME Das hier sollte nur einmal gehen.
        // FIXME Dieser zweite Teil muss von Rapunzels
        //  Zuneigung abhängen!
        // "ihren"
        // "ihrem"
        // "sie"
        // FIXME Dieser zweite Teil muss von Rapunzels
        //  Zuneigung abhängen!
        // "ihren"
        // "ihrem"
        // "sie"
        n.narrate(du(PARAGRAPH, "fängst",
                "an ganz freundlich mit",
                anaph.datK(),
                "zu reden. Du erzählst, dass von",
                wovonHerzBewegtDat,
                "dein Herz so sehr sei bewegt worden, dass es dir",
                "keine Ruhe gelassen und du",
                anaph.persPron().akkK(),
                "selbst habest sehen müssen.",

                // FIXME Dieser zweite Teil muss von Rapunzels
                //  Zuneigung abhängen!

                "Da verliert",
                desc.nomK(),
                "ihre Angst und es bricht aus",
                desc.persPron().datK(),
                "heraus.",
                "Eine alte Zauberin hätte",
                desc.persPron().akkK(),
                desc.possArt().vor(PL_MFN).datStr(),  // "ihren"
                "Eltern fortgenommen, seit",
                desc.possArt().vor(N).datStr(),  // "ihrem"
                "zwölften Jahre sei",
                desc.persPron().nomK(), // "sie"
                "in diesen Turm geschlossen")
                .mitVorfeldSatzglied("ganz freundlich")
                .timed(mins(1)));

        setSchonBegruesstMitSC(true);

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                1.5f, FeelingIntensity.DEUTLICH);

        loadSC().feelingsComp().requestMoodMin(BEWEGT);
    }

    private void haareHerunterlassenBitte_EntryReEntry() {
        final int zuneigungZuRapunzel =
                loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);

        final Nominalphrase desc = getDescription(true);
        if (duzen(zuneigungZuRapunzel)) {
            n.narrateAlt(secs(10),
                    neuerSatz("„Weißt du“, wendest du dich an",
                            desc.akkK(),
                            ", „eigentlich wollte ich",
                            "nur schauen, ob's dir gut geht. Lässt du mich wieder hinunter?“")
                            .phorikKandidat(desc, RAPUNZEL));
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
                        "Abenteuer hinaus“, sagst du "
                                // FIXME ZU.getDescription(anaph)) sollte keinen
                                //  String, sondern eine Wortfolge erzeugen und einen
                                //  Phorik-Kandidaten setzen
                                + ZU.getDescription(anaph))
                        .timed(secs(15))
                        .phorikKandidat(anaph, RAPUNZEL)
        );

        final int zuneigungSCZuRapunzel =
                loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
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
                loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
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
                        .withCounterIdIncrementedIfTextIsNarrated(null)
                        .dann());
            } else {
                n.narrate(du(PARAGRAPH, "siehst", " über dir eine Bewegung: "
                        + "Aus dem Turmfenster fallen wieder die "
                        + "langen, golden glänzenden Haare bis zum Boden herab")
                        .timed(secs(10))
                        .withCounterIdIncrementedIfTextIsNarrated(null)
                        .dann());
            }
            world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_HAARE);
        } else if (loadSC().locationComp().hasRecursiveLocation(OBEN_IM_ALTEN_TURM)) {
            final Nominalphrase rapunzelDesc = getDescription(true);

            final AltDescriptionsBuilder alt = alt();

            final ImmutableList<Satz> altReaktionSaetze
                    = feelingsComp.altReaktionWennTargetGehenMoechteSaetze(rapunzelDesc);

            alt.addAll(altNeueSaetze(
                    altReaktionSaetze.stream()
                            .flatMap(s ->
                                    // Das braucht man hier wohl nicht mehr:
                                    // joinToAltWortfolgen(
                                    s.altVerzweitsaetze()
                                            //)
                                            .stream()),
                    ",",
                    altDannHaareFestbinden(rapunzelDesc))
                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE));

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

    private static ImmutableList<Wortfolge> altDannHaareFestbinden(
            final Nominalphrase rapunzelDesc) {
        // FIXME Falsche Abstraktion?! Der fachliche Code sollte eher nicht mit
        //  Wortfolgen agieren müssen, eher mit Alt...Builder etc.
        return ImmutableList.of(
                joinToWortfolge(
                        "dann bindet",
                        rapunzelDesc.persPron().nomK(), //"sie"
                        rapunzelDesc.possArt().vor(PL_MFN).akkStr(),// "ihre"
                        "Haare wieder um den Haken am Fenster"),
                joinToWortfolge(
                        "dann knotet",
                        rapunzelDesc.persPron().nomK(), //"sie"
                        rapunzelDesc.possArt().vor(PL_MFN).akkStr(),// "ihre"
                        "Haare wieder um den Fensterhaken"),
                joinToWortfolge(
                        "dann bindet",
                        rapunzelDesc.persPron().nomK(), //"sie"
                        rapunzelDesc.possArt().vor(PL_MFN).akkStr(),// "ihre"
                        "Haare wieder am Fenster fest"));
    }

    public static boolean duzen(final int zuneigung) {
        return zuneigung > FeelingIntensity.DEUTLICH ||
                zuneigung <= -FeelingIntensity.DEUTLICH;
    }
}
