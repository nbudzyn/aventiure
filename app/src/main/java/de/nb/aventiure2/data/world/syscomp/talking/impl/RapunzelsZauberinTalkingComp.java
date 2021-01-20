package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.DescriptionBuilder;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entryReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStNSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.Wortfolge.joinToWortfolge;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.FRAGEN_NACH;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSPRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BEGRUESSEN;

/**
 * Component for {@link World#RAPUNZELS_ZAUBERIN}: Der Spieler
 * kann versuchen, mit Rapunzels Zauberin ein Gespräch zu führen.
 */
public class RapunzelsZauberinTalkingComp extends AbstractTalkingComp {
    private final LocationComp locationComp;
    private final RapunzelsZauberinStateComp stateComp;
    private final FeelingsComp feelingsComp;

    public RapunzelsZauberinTalkingComp(final AvDatabase db,
                                        final Narrator n,
                                        final TimeTaker timeTaker,
                                        final World world,
                                        final LocationComp locationComp,
                                        final RapunzelsZauberinStateComp stateComp,
                                        final FeelingsComp feelingsComp,
                                        final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZELS_ZAUBERIN, db, timeTaker, n, world, initialSchonBegruesstMitSC);
        this.locationComp = locationComp;
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        switch (stateComp.getState()) {
            case MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE:
            case AUF_DEM_WEG_ZU_RAPUNZEL:
                // fall-through
            case AUF_DEM_RUECKWEG_VON_RAPUNZEL:
                if (locationComp.hasRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                    // Hier bemerkt der SC die Zauberin nicht - bzw. sie
                    // "verschwindet" ohnehin gleich wieder.
                    return ImmutableList.of();
                }

                return ImmutableList.of(
                        entryReEntrySt(ANSPRECHEN, this::ansprechen),
                        st(FRAGEN_NACH.mitPraep(
                                np(N, null, "ihr Ziel",
                                        "ihrem Ziel")),
                                this::frageNachZiel),
                        exitSt(this::gespraechBeenden),
                        immReEntryStSCHatteGespraechBeendet(FRAGEN_NACH.mitPraep(
                                np(N, null, "ihr Ziel",
                                        "ihrem Ziel")),
                                this::frageNachZiel_ImmReEntrySCHatteGespraechBeendet),
                        immReEntryStNSCHatteGespraechBeendet(FRAGEN_NACH.mitPraep(
                                np(N, null, "ihr Ziel",
                                        "ihrem Ziel")),
                                this::frageNachZiel_ImmReEntryNSCHatteGespraechBeendet)
                );
            case BEI_RAPUNZEL_OBEN_IM_TURM:
                // FIXME Kann man die Zauberin oben im Turm ansprechen? Wie reagiert
                //  sie?
                return ImmutableList.of();
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

    private void scSprichtAnBereitsBegruesst() {
        final SubstantivischePhrase anaph = anaph(false);
        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(PARAGRAPH,
                // FIXME  joinToWortfolge() möglichst überall
                //  durch neuerSatz o.Ä. ersetzen.
                "„Gute Frau“, sprichst du",
                anaph.akkK(),
                "an").dann(),
                du(PARAGRAPH,
                        "wendest",
                        joinToWortfolge(
                                "dich noch einmal",
                                anaph.datK(), "zu"))
                        .undWartest()
        );

        n.narrateAlt(alt, secs(10));
        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void begruessen() {
        scBegruesst();
        zauberinReagiertAufAnsprechen();
    }

    private void scBegruesst() {
        final SubstantivischePhrase anaph = anaph(false);
        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altNeueSaetze(PARAGRAPH,
                "„",
                altBegruessungenCap(),
                // "Einen schönen guten Tag"
                "“, sprichst du",
                anaph.akkK(),
                "an"));
        alt.addAll(altNeueSaetze(PARAGRAPH,
                "„",
                altBegruessungenCap(),
                // "Einen schönen guten Tag"
                "“, redest du",
                anaph.akkK(),
                "an"));
        alt.add(neuerSatz(PARAGRAPH,
                "„Holla, gute Frau“, sprichst du",
                anaph.akkK(),
                "an").dann(),
                neuerSatz("„Schön euch zu sehen“, sprichst du",
                        anaph.akkK(),
                        "an")
                        .dann(),
                du(PARAGRAPH, BEGRUESSEN.mit(anaph)));
        n.narrateAlt(alt, secs(5));

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void zauberinReagiertAufAnsprechen() {
        final SubstantivischePhrase anaph = anaph(true);

        final ImmutableList<Satz> altReaktionSaetze =
                feelingsComp.altReaktionBeiBegegnungMitScSaetze(anaph);

        n.narrateAlt(
                altReaktionSaetze.stream().map(DescriptionBuilder::satz),
                secs(5));
    }

    private void gespraechBeenden() {
        final SubstantivischePhrase anaph = anaph(false);

        final AltDescriptionsBuilder alt = alt();

        // FIXME joinToAltWortfolgen überall in dieser Art
        //  ersetzen!
        alt.addAll(altNeueSaetze(
                "„",
                altVerabschiedungenCap(),
                // "Tschüss"
                "!“ Du wendest dich ab").undWartest().dann());

        alt.addAll(altNeueSaetze(
                "„",
                altVerabschiedungenCap(),
                // "Tschüss"
                "“, verabschiedest du dich",
                PraepositionMitKasus.VON.mit(anaph).getDescription())
                .undWartest().dann());

        alt.add(du("verabschiedest",
                joinToWortfolge("dich wieder",
                        PraepositionMitKasus.VON.mit(anaph).getDescription()))
                        .undWartest().dann(),
                du("sagst",
                        joinToWortfolge(anaph.datK(), "Ade"))
                        .undWartest().dann()
        );

        n.narrateAlt(alt, secs(10));
        gespraechspartnerBeendetGespraech();
    }

    private void frageNachZiel_ImmReEntrySCHatteGespraechBeendet() {
        n.narrateAlt(NO_TIME,
                neuerSatz("Aber dann fragst du doch noch:"));

        frageNachZiel();
    }

    private void frageNachZiel_ImmReEntryNSCHatteGespraechBeendet() {
        final SubstantivischePhrase anaph = anaph(false);

        n.narrateAlt(NO_TIME,
                neuerSatz("Aber du lässt nicht locker:"),
                du("fragst",
                        joinToWortfolge(anaph.akkK(), "erneut:")));

        frageNachZiel();
    }

    private void frageNachZiel() {
        final SubstantivischePhrase anaph = anaph(false);

        n.narrateAlt(secs(10),
                neuerSatz("„Wohin des Wegs?“ – „Was geht es dich an?“, ist "
                        + anaph.possArt().vor(F).nomStr() // "ihre"
                        + " abweisende Antwort")
                        .phorikKandidat(anaph, getGameObjectId())
                        .beendet(PARAGRAPH),
                neuerSatz("„Ihr habt es wohl eilig?“ – „So ist es“, antwortet "
                        + anaph.persPron().nomStr()
                        + " dir")
                        .phorikKandidat(anaph, getGameObjectId())
                        .beendet(PARAGRAPH)
        );

        // FIXME Zauberin wird abweisender beim Fragen nach dem Weg

        // FIXME Weitere Antworten der Zauberin auf Frage nach dem Weg

        // FIXME Wenn im Gespräch: Hexe Kugel geben? (Diebesgut...) (beendet Gespräch, macht
        //  sie noch abweisender)

        // FIXME Hexe nach Turm fragen? (Abneigung steigt - danach "misstrauisch...")

        setSchonBegruesstMitSC(true);
        talkerBeendetGespraech();
    }
}
