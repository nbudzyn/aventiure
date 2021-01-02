package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AllgDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
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
import static de.nb.aventiure2.german.description.DescriptionBuilder.altNeueSaetzeMitPhorikKandidat;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.DirektivesVerb.BITTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.AUSSCHUETTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINUNTERLASSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.WIRKEN;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
public class RapunzelTalkingComp extends AbstractTalkingComp {
    public static final String SC_BEGRUESST = "RapunzelTalkingComp_sc_begruesst";
    public static final String RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG =
            "RapunzelTalkingComp_Rapunzel_reagiert_auf_SC_Begruessung";
    private final RapunzelStateComp stateComp;
    private final FeelingsComp feelingsComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final Narrator n,
                               final World world,
                               final RapunzelStateComp stateComp,
                               final FeelingsComp feelingsComp,
                               final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZEL, db, n, world, initialSchonBegruesstMitSC);
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        switch (stateComp.getState()) {
            case STILL:
                // "Die junge Frau bitten ihre Haare wieder hinunterzulassen"
                return ImmutableList.of(
                        SCTalkAction.entrySt(
                                VerbSubjAkkPraep.BEGINNEN
                                        .mitAkk(EIN_GESPRAECH)
                                        .mit(getDescription()),
                                this::gespraechBeginnen_EntryReEntry),
                        SCTalkAction.entrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        SCTalkAction.st(
                                // FIXME Nur, wenn auch Rapunzel entsprechend Zutrauen
                                //  gefasst hat! Ansonsten wäre das creepy!
                                this::zuneigungDesSCZuRapunzelDeutlich,
                                // "Der jungen Frau dein Herz ausschütten"
                                AUSSCHUETTEN
                                        .mitDat(getDescription(true))
                                        .mit(DEIN_HERZ),
                                this::herzAusschuetten),
                        SCTalkAction.exitSt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::still_haareHerunterlassenBitte_ExitImmReEntry),
                        SCTalkAction.immReEntrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::still_haareHerunterlassenBitte_ExitImmReEntry)
                );
            case SINGEND:
                // FALL-THROUGH
            case HAARE_VOM_TURM_HERUNTERGELASSEN:
                return ImmutableList.of();
            case HAT_NACH_KUGEL_GEFRAGT:
                return ImmutableList.of(
                        SCTalkAction.entrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        SCTalkAction.exitSt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::hatNachKugelGefragt_haareHerunterlassenBitte_ExitImmReEntry),
                        SCTalkAction.immReEntrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::hatNachKugelGefragt_haareHerunterlassenBitte_ExitImmReEntry)
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

    private void gespraechBeginnen_EntryReEntry() {
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
                        "„Nachts war es in letzter Zeit ziemlich kalt“, wirfst du "
                                + "in den Raum")
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
        final SubstantivischePhrase anaph = getAnaphPersPronWennMglSonstShortDescription();

        final ImmutableList.Builder<TimedDescription<?>> alt = ImmutableList.builder();

        if (db.counterDao().get(SC_BEGRUESST) == 0) {
            alt.add(neuerSatz("„Hallihallo!“, sagst du und lächelst breit",
                    secs(5), SC_BEGRUESST));
        } else {
            final int zuneigungSCTowardsRapunzel =
                    loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
            alt.add(neuerSatz(
                    "„Hallo, da bin ich wieder!“ sprichst du "
                            + anaph.akk()
                            + " an",
                    secs(5))
                    .phorikKandidat(anaph, RAPUNZEL));

            if (zuneigungSCTowardsRapunzel >= FeelingIntensity.MERKLICH) {
                if (duzen(zuneigungSCTowardsRapunzel)) {
                    alt.add(du("schaust ",
                            anaph.akk()
                                    + " an. „Schön, dich wiederzusehen, sagst du",
                            secs(5))
                            .phorikKandidat(anaph, RAPUNZEL)
                    );
                } else {
                    alt.add(du("schaust ",
                            anaph.akk()
                                    + " an. „Schön, "
                                    + "euch wiederzusehen, sagst du",
                            secs(5))
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

        final SubstantivischePhrase anaph = getAnaphPersPronWennMglSonstShortDescription();
        @Nullable final Personalpronomen persPron = n.getAnaphPersPronWennMgl(RAPUNZEL);

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        final ImmutableList<AbstractDescription<?>> altZuneigungAbneigungSaetze =
                // Es wäre besser, wenn der Phorik-Kandidat schon beim Erzeugen des
                // Satzes gesetzt würde.
                altNeueSaetzeMitPhorikKandidat(
                        anaph, RAPUNZEL,
                        feelingsComp
                                .altFeelingsBeiBegegnungMitScSaetze(anaph, ZUNEIGUNG_ABNEIGUNG));

        // Könnte leer sein
        final ImmutableList<AdjPhrOhneLeerstellen> altZuneigungAbneigungEindruckAdjPhrasen =
                feelingsComp.altEindruckAufScBeiBegegnungAdjPhr(
                        anaph.getNumerusGenus(),
                        ZUNEIGUNG_ABNEIGUNG);

        final int zuneigungTowardsSC =
                feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);
        if (zuneigungTowardsSC <= -FeelingIntensity.SEHR_STARK) {
            alt.add(neuerSatz("„Verschwinde! Sofort!“, schreit "
                            + anaph.nom()
                            + " dich an")
                            .beendet(PARAGRAPH)
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz("Aber "
                            + anaph.nom()
                            + " schreit nur: „Raus! Auf der Stelle!“")
                            .beendet(PARAGRAPH)
                            .phorikKandidat(anaph, RAPUNZEL)
            );
        } else if (zuneigungTowardsSC == -FeelingIntensity.STARK) {
            alt.add(neuerSatz("„Was willst du hier?“, antwortet "
                            + anaph.nom()
                            + " ungehalten und schaut dich vergrätzt an")
                            .beendet(PARAGRAPH)
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz("„Verschwinde!“, versetzt "
                            + anaph.nom())
                            .beendet(PARAGRAPH)
                            .phorikKandidat(anaph, RAPUNZEL)
            );
        } else if (zuneigungTowardsSC == -FeelingIntensity.DEUTLICH) {
            alt.add(neuerSatz("„Was willst du hier?“, gibt "
                    + anaph.nom()
                    + " zur Antwort")
                    .beendet(PARAGRAPH)
                    .phorikKandidat(anaph, RAPUNZEL)
            );
            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.add(neuerSatz("„Was willst du noch?“, faucht "
                                + anaph.nom()
                                + " dich geradezu an")
                                .beendet(PARAGRAPH)
                                .phorikKandidat(anaph, RAPUNZEL),
                        neuerSatz("„Was willst du wieder hier?“, fragt "
                                + anaph.nom()
                                + " unfreundlich")
                                .beendet(PARAGRAPH)
                                .phorikKandidat(anaph, RAPUNZEL)
                );
            }

        } else if (zuneigungTowardsSC == -FeelingIntensity.MERKLICH) {
            alt.add(neuerSatz("„Hallo“, sagt "
                            + anaph.nom()
                            + " reserviert")
                            .beendet(SENTENCE)
                            .phorikKandidat(anaph, RAPUNZEL),
                    du("erhältst", "nur ein knappes Nicken zurück",
                            "nur ein knappes Nicken")
                            .beendet(PARAGRAPH),
                    neuerSatz(anaph.nom()
                            + " nickt dir nur knapp zu")
                            .beendet(SENTENCE)
                            .phorikKandidat(anaph, RAPUNZEL)
            );
        } else if (zuneigungTowardsSC == -FeelingIntensity.NUR_LEICHT) {
            alt.add(neuerSatz("„Hallo!“, entgegnet "
                            + anaph.nom()
                            + " knapp")
                            .beendet(SENTENCE)
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz("„Hallo!“, versetzt sie "
                            + anaph.nom()
                            + " kurz angebunden")
                            .beendet(SENTENCE)
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz("– Schweigen")
                            .beendet(PARAGRAPH));
        } else if (zuneigungTowardsSC == FeelingIntensity.NEUTRAL) {
            alt.add(neuerSatz(anaph.nom()
                            + " erwidert die Begrüßung")
                            .beendet(SENTENCE)
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz(anaph.nom()
                            + " gibt den Gruß zurück")
                            .beendet(SENTENCE)
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz("„Ja…“, gibt "
                            + anaph.nom()
                            + " zurück")
                            .beendet(PARAGRAPH)
                            .phorikKandidat(anaph, RAPUNZEL));
            if (persPron != null) {
                alt.add(neuerSatz("„Ja…“, "
                        + "ist "
                        + persPron.possArt().vor(F).dat() // ihre
                        + " "
                        + "Antwort")
                        .phorikKandidat(anaph, RAPUNZEL)
                        .beendet(PARAGRAPH));
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.NUR_LEICHT) {
            final ImmutableList<Wortfolge> wirktWortfolge =
                    altZuneigungAbneigungEindruckAdjPhrasen.stream()
                            .map(adjPhr ->
                                    Wortfolge.joinToNullWortfolge(
                                            WIRKEN.mit(adjPhr)
                                                    .getVerbzweit(
                                                            anaph.getPerson(),
                                                            anaph.getNumerus())))
                            .collect(toImmutableList());
            alt.addAll(altKombinationenBeendetParagraph(
                    "„Hallo“, antwortet "
                            + anaph.nom()
                            + " und ",
                    wirktWortfolge.stream()
                            .map(wf -> satzanschluss(wf).phorikKandidat(F, RAPUNZEL))
                            .collect(toImmutableList())));
            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.addAll(altKombinationenBeendetParagraph(
                        "„Ach, ihr seid es wieder.“ ",
                        altZuneigungAbneigungSaetze));
                alt.addAll(altKombinationenBeendetParagraph(
                        "„Oh, ihr seid es wieder.“ ",
                        altZuneigungAbneigungSaetze));
                alt.add(neuerSatz("„Ich hatte mich schon gefragt, ob ihr mal wieder "
                        + "vorbeischaut! Willkommen.“ –")
                        .beendet(SENTENCE)
                );
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.MERKLICH) {
            alt.add(neuerSatz("„Ah, hallo! – Willkommen!“ "
                            + capitalize(anaph.nom())
                            + " schaut dich freundlich an")
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz(anaph.nom()
                            + " heißt dich etwas überrascht willkommen")
                            .phorikKandidat(anaph, RAPUNZEL),
                    neuerSatz(
                            // FIXME X freut sich, dich zu sehen" automatisch erzeugen!
                            anaph.nom()
                                    + " freut sich, dich zu sehen")
                            .phorikKandidat(anaph, RAPUNZEL)
            );

            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.add(neuerSatz("„Ihr seid's! Hallo!“, antwortet "
                                + anaph.nom())
                                .phorikKandidat(anaph, RAPUNZEL),
                        neuerSatz(anaph.nom()
                                // FIXME X freut sich, dich wiederzusehen" automatisch erzeugen!
                                + " freut sich, dich wiederzusehen")
                                .phorikKandidat(anaph, RAPUNZEL),
                        neuerSatz(anaph.nom()
                                + " freut sich, dass du wieder da bist")
                                .phorikKandidat(anaph, RAPUNZEL));
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.DEUTLICH) {
            alt.add(neuerSatz("„Oh, wie schön“, antwortet "
                    + anaph.nom()
                    + " „ich freue mich, dich zu sehen!“")
                    .phorikKandidat(anaph, RAPUNZEL)
                    .beendet(PARAGRAPH));

            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.add(neuerSatz("„Oh, wie schön, dasss du wieder da bist“, antwortet "
                                + anaph.nom()
                                // FIXME Schaut überrascht / schaut glücklich automatisch erzeugen!
                                //  (prädikative Adjektivphrase!)
                                //  ansehen-Methode verwenden
                                + " und schaut glücklich")
                                .phorikKandidat(anaph, RAPUNZEL)
                                .beendet(PARAGRAPH),
                        neuerSatz("„Schön, dich wiederzusehen!“, freut "
                                + anaph.nom()
                                + " sich")
                                .phorikKandidat(anaph, RAPUNZEL)
                );
            }
        } else if (zuneigungTowardsSC == FeelingIntensity.STARK) {
            alt.add(neuerSatz(
                    // FIXME Automatisch erzeugen!
                    anaph.nom() + " strahlt dich nur an"));

            if (persPron != null) {
                alt.add(neuerSatz(persPron.possArt().vor(PL_MFN).nom()
                        + " Augen strahlen, als "
                        + persPron.nom()
                        + " dich begrüßt"));
            }

            if (scBereitsZuvorSchonEinmalGetroffen) {
                alt.add(
                        neuerSatz("„Endlich! Ich hatte dich schon erwartet“, antwortet "
                                + anaph.nom()
                                + " dir und "
                                // FIXME schaut... drein automatisch erzeugen!
                                + "schaut ganz fröhlich drein")
                                .phorikKandidat(anaph, RAPUNZEL)
                                .beendet(PARAGRAPH),
                        neuerSatz("„Oh, eine Freude, dich wiederzusehen!“"),
                        neuerSatz("„Endlich bist du wieder da! Ich habe dich schon "
                                + "vermisst.“")
                );
            }
        }

        n.narrateAlt(alt.build(), secs(5), RAPUNZEL_REAGIERT_AUF_SC_BEGRUESSUNG);

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void herzAusschuetten() {
        final SubstantivischePhrase anaph = getAnaphPersPronWennMglSonstShortDescription();
        final SubstantivischePhrase desc = getDescription();

        final String wovonHerzBewegtDat;
        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            wovonHerzBewegtDat = anaph.possArt().vor(M).dat() // "ihrem"
                    + " Gesang";
        } else {
            wovonHerzBewegtDat = anaph.possArt().vor(PL_MFN).dat() // "ihrem"
                    + " glänzenden Locken";
        }

        // FIXME Das hier sollte nur einmal gehen.
        n.narrate(du(PARAGRAPH, "fängst", "an ganz freundlich mit "
                        + anaph.dat()
                        + " zu reden. Du erzählst, dass von "
                        + wovonHerzBewegtDat
                        + " dein Herz so sehr sei bewegt worden, dass es dir "
                        + "keine Ruhe gelassen und du "
                        + anaph.persPron().akk()
                        + " selbst habest sehen müssen."

                        // FIXME Dieser zweite Teil muss von Rapunzels
                        //  Zuneigung abhängen!

                        + " Da verliert "
                        + desc.nom()
                        + " ihre Angst und es bricht aus "
                        + desc.persPron().dat()
                        + " heraus."
                        + " Eine alte Zauberin hätte "
                        + desc.persPron().akk()
                        + " "
                        + desc.possArt().vor(PL_MFN).dat()  // "ihren"
                        + " Eltern fortgenommen, seit "
                        + desc.possArt().vor(N).dat()  // "ihrem"
                        + " zwölften Jahre sei "
                        + desc.persPron().nom() // "sie"
                        + " in diesen Turm geschlossen",
                "ganz freundlich",
                mins(1)));

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
                    neuerSatz("„Weißt du“, wendest du dich an "
                            + desc.akk()
                            + ", „eigentlich wollte ich "
                            + "nur schauen, ob's dir gut geht. Lässt du mich wieder hinunter?“")
                            .phorikKandidat(desc, RAPUNZEL));
        } else {
            n.narrateAlt(secs(10),
                    neuerSatz(PARAGRAPH,
                            "„Ich wollte euch nicht belästigen“, sprichst du "
                                    + getAnaphPersPronWennMglSonstShortDescription().akk()
                                    + " an, "
                                    + "„lasst mich wieder hinunter und ich lasse euch euren Frieden.“")
                            .beendet(PARAGRAPH)
            );
        }

        feelingsComp.upgradeFeelingsTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG,
                0.25f, FeelingIntensity.MERKLICH);

        setSchonBegruesstMitSC(true);
        haareHerunterlassen();
    }

    private void still_haareHerunterlassenBitte_ExitImmReEntry() {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstDescription(true);
        final ImmutableList.Builder<TimedDescription<?>> alt =
                ImmutableList.builder();

        alt.add(
                neuerSatz(PARAGRAPH,
                        "„Jetzt muss ich aber gehen“, sagst du unvermittelt und "
                                + "blickst "
                                + "zum Fenster hin",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(SENTENCE, "„Ich muss wieder hinaus in die Welt!“, "
                                + "sagst du",
                        secs(10)),
                neuerSatz(PARAGRAPH, "„Dann will ich wieder ins "
                                + "Abenteuer hinaus“, sagst du "
                                + ZU.getDescription(rapunzelAnaph),
                        secs(15))
                        .phorikKandidat(rapunzelAnaph, RAPUNZEL)
        );

        final int zuneigungSCZuRapunzel =
                loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
        if (duzen(zuneigungSCZuRapunzel)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "„Lässt du mich wieder hinunter?“, fragst du in die "
                                    + "Stille hinein",
                            secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        if (zuneigungSCZuRapunzel >= FeelingIntensity.STARK) {
            alt.add(
                    du(PARAGRAPH,
                            "spürst", "plötzlich neuen Tatendrang in dir. „Lass "
                                    + "mich gehen“, "
                                    + "sagst du, "
                                    + "„bald bin ich wieder zurück!“",
                            "plötzlich",
                            secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        n.narrateAlt(alt);

        setSchonBegruesstMitSC(true);

        loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);

        haareHerunterlassen();
    }

    private void hatNachKugelGefragt_haareHerunterlassenBitte_ExitImmReEntry() {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstDescription(true);
        final ImmutableList.Builder<TimedDescription<?>> alt =
                ImmutableList.builder();

        alt.add(neuerSatz(
                "Doch du reagierst gar nicht darauf, sondern forderst "
                        + rapunzelAnaph.akk()
                        + " nur auf, die Haare "
                        + "wieder heruterzulassen, dass du wieder gehen kannst",
                secs(15))
                .beendet(PARAGRAPH)
        );

        final int zuneigungSCZuRapunzel =
                loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG);
        if (duzen(zuneigungSCZuRapunzel)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "„Lass mich wieder gehen!“, gibst du zurück",
                            secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        n.narrateAlt(alt);

        setSchonBegruesstMitSC(true);
        haareHerunterlassen();
    }

    private void haareHerunterlassen() {
        stateComp.rapunzelLaesstHaareZumAbstiegHerunter();

        unsetTalkingTo();
    }

    private static Iterable<AllgDescription> altKombinationenBeendetParagraph(
            final String praefix,
            final ImmutableList<AbstractDescription<?>> alt) {
        final ImmutableList.Builder<AllgDescription> res =
                ImmutableList.builder();

        for (final AbstractDescription<?> desc : alt) {
            res.add(neuerSatz(praefix + desc.getDescriptionHauptsatz())
                    .woertlicheRedeNochOffen(desc.isWoertlicheRedeNochOffen())
                    .komma(desc.isKommaStehtAus())
                    .phorikKandidat(desc.getPhorikKandidat())
                    .beendet(PARAGRAPH));
        }

        return res.build();
    }

    public static boolean duzen(final int zuneigung) {
        return zuneigung > FeelingIntensity.DEUTLICH ||
                zuneigung <= -FeelingIntensity.DEUTLICH;
    }
}
