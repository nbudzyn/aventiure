package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
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
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.DirektivesVerb.BITTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.AUSSCHUETTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINUNTERLASSEN;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
public class RapunzelTalkingComp extends AbstractTalkingComp {
    private final RapunzelStateComp stateComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final World world,
                               final RapunzelStateComp stateComp,
                               final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZEL, db, world, initialSchonBegruesstMitSC);
        this.stateComp = stateComp;
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
                                        .mitObj(getDescription()),
                                this::gespraechBeginnen_EntryReEntry),
                        SCTalkAction.entrySt(
                                bittenHaareHerunterzulassenPraedikat(),
                                this::haareHerunterlassenBitte_EntryReEntry),
                        SCTalkAction.st(
                                this::zuneigungDesSCZuRapunzelDeutlich,
                                // "Der jungen Frau dein Herz ausschütten"
                                AUSSCHUETTEN
                                        .mitDat(getDescription(true))
                                        .mitObj(DEIN_HERZ),
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
                .mitLexikalischemKern(HINUNTERLASSEN
                        .mitObj(IHRE_HAARE)
                        .mitAdverbialerAngabe(
                                // "wieder hinunterlassen": Das "wieder" gehört
                                // quasi zu "hinunter".
                                new AdverbialeAngabeSkopusVerbWohinWoher(
                                        "wieder")));
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

        n.narrateAlt(
                neuerSatz(
                        // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                        "„Die letzen Tage waren ziemlich warm“, sagst du",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(
                        // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                        "„Nachts war es in letzter Zeit ziemlich kalt“, wirfst du "
                                + "in den Raum",
                        secs(15))
                        .beendet(PARAGRAPH)
        );

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void begruessen_EntryReEntry() {
        final SubstantivischePhrase anaph = getAnaphPersPronWennMglSonstShortDescription();

        if (db.counterDao().incAndGet("RapunzelTalkingComp_begruessen_EntryReEntry") == 1) {
            n.narrate(
                    neuerSatz(
                            // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen.
                            //  Ansonsten freut sie sich vermutlich!
                            "„Hallihallo!“, sagst du und lächelst breit. – Schweigen",
                            secs(5))
                            .beendet(PARAGRAPH));
            setSchonBegruesstMitSC(true);
            return;
        }

        n.narrateAlt(
                // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                neuerSatz(
                        "„Hallo, da bin ich wieder!“ sprichst du "
                                + anaph.akk()
                                + " an. „Ja…“, gibt "
                                + anaph.persPron().nom()
                                + " zurück",
                        secs(10))
                        .beendet(PARAGRAPH)
                        .phorikKandidat(anaph, RAPUNZEL),
                neuerSatz(
                        "Du schaust "
                                + anaph.akk()
                                + " an. „Schön, euch wiederzusehen, sagst du. „Ja…“, "
                                + "ist "
                                + anaph.possArt().vor(F).dat()
                                + " "
                                + "Antwort",
                        secs(10))
                        .phorikKandidat(anaph, RAPUNZEL)
                        .beendet(PARAGRAPH)
        );

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
                    // STORY UND WENN GESANG UNBEKANNT?
                    + " glänzenden Locken";
        }

        // STORY Das hier sollte nur einmal gehen.
        n.narrate(du(PARAGRAPH, "fängst", "an ganz freundlich mit "
                        + anaph.dat()
                        + " zu reden. Du erzählst, dass von "
                        + wovonHerzBewegtDat
                        + " dein Herz so sehr sei bewegt worden, dass es dir "
                        + "keine Ruhe gelassen und du "
                        + anaph.persPron().akk()
                        + " selbst habest sehen müssen."

                        // STORY Rapunzel die Kugel geben - ansonsten "taut sie nicht auf" und
                        //  Dieser zweite Teil kommt nicht?

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
        loadSC().feelingsComp().setMoodMin(BEWEGT);
    }

    private void haareHerunterlassenBitte_EntryReEntry() {
        n.narrateAlt(
                // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                neuerSatz(PARAGRAPH,
                        "„Ich wollte euch nicht belästigen“, sprichst du "
                                + getAnaphPersPronWennMglSonstShortDescription().akk()
                                + " an, "
                                + "„lasst mich wieder hinunter und ich lasse euch euren Frieden.“",
                        secs(10))
                        .beendet(PARAGRAPH)
        );

        setSchonBegruesstMitSC(true);
        haareHerunterlassen();
    }

    private void still_haareHerunterlassenBitte_ExitImmReEntry() {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstDescription(true);
        final ImmutableList.Builder<AbstractDescription<?>> alt =
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

        if (loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG) >=
                FeelingIntensity.DEUTLICH) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "„Lässt du mich wieder hinunter?“, fragst du in die "
                                    + "Stille hinein",
                            secs(15))
                            .beendet(PARAGRAPH)
            );
        }

        if (loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG) >=
                FeelingIntensity.STARK) {
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

        loadSC().feelingsComp().setMoodMin(AUFGEDREHT);

        haareHerunterlassen();
    }

    private void hatNachKugelGefragt_haareHerunterlassenBitte_ExitImmReEntry() {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstDescription(true);
        final ImmutableList.Builder<AbstractDescription<?>> alt =
                ImmutableList.builder();

        alt.add(neuerSatz(
                "Doch du reagierst gar nicht darauf, sondern forderst "
                        + rapunzelAnaph.akk()
                        + " nur auf, die Haare "
                        + "wieder heruterzulassen, dass du wieder gehen kannst",
                secs(15))
                .beendet(PARAGRAPH)
        );

        if (loadSC().feelingsComp().getFeelingTowards(RAPUNZEL, ZUNEIGUNG_ABNEIGUNG) >=
                FeelingIntensity.DEUTLICH) {
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
}
