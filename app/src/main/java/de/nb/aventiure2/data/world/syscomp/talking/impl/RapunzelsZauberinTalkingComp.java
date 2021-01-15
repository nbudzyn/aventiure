package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;

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
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.Wortfolge.joinToWortfolge;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.FRAGEN_NACH;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSPRECHEN;

/**
 * Component for {@link World#RAPUNZELS_ZAUBERIN}: Der Spieler
 * kann versuchen, mit Rapunzels Zauberin ein Gespräch zu führen.
 */
public class RapunzelsZauberinTalkingComp extends AbstractTalkingComp {
    private final LocationComp locationComp;
    private final RapunzelsZauberinStateComp stateComp;

    public RapunzelsZauberinTalkingComp(final AvDatabase db,
                                        final Narrator n,
                                        final World world,
                                        final LocationComp locationComp,
                                        final RapunzelsZauberinStateComp stateComp,
                                        final boolean initialSchonBegruesstMitSC) {
        super(RAPUNZELS_ZAUBERIN, db, n, world, initialSchonBegruesstMitSC);
        this.locationComp = locationComp;
        this.stateComp = stateComp;
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
        // FIXME Ansprechen: Wenn noch nicht begrüßt, dann begrüßen, sonst
        //  allgemein ansprechen
//        if (!isSchonBegruesstMitSC()) {
//            begruessen_EntryReEntry();
//            return;
//        }

        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);


        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(PARAGRAPH, "„Einen schönen guten Tag“, sprichst du " +
                        anaphOderDesc.akkStr() +
                        " an")
                        .dann()
                        .phorikKandidat(anaphOderDesc, getGameObjectId()),
                neuerSatz(PARAGRAPH, "„Holla, gute Frau“, sprichst du " +
                        anaphOderDesc.akkStr() +
                        " an")
                        .dann()
                        .phorikKandidat(anaphOderDesc, getGameObjectId()),
                neuerSatz("„Schön euch zu sehen“, sprichst du " +
                        anaphOderDesc.akkStr() +
                        " an")
                        .dann()
                        .phorikKandidat(anaphOderDesc, getGameObjectId()));

        n.narrateAlt(alt, secs(10));

        setTalkingTo(SPIELER_CHARAKTER);
    }

    private void gespraechBeenden() {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz("„Na, dann erstmal!“ Du wendest dich ab"),
                du("verabschiedest",
                        joinToWortfolge("dich wieder",
                                // FIXME Praepositionalphrase berücksichtigt
                                //  Phorik-Kandidat noch nicht.
                                PraepositionMitKasus.VON.mit(anaphOderDesc).getDescription()))
                        .undWartest()
                        .dann());
        // FIXME weitere Alternativen

        n.narrateAlt(alt, secs(10));
        gespraechspartnerBeendetGespraech();
    }

    private void frageNachZiel_ImmReEntrySCHatteGespraechBeendet() {
        n.narrateAlt(NO_TIME,
                neuerSatz("Aber dann fragst du doch noch:"));

        frageNachZiel(SENTENCE);
    }

    private void frageNachZiel_ImmReEntryNSCHatteGespraechBeendet() {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        n.narrateAlt(NO_TIME,
                neuerSatz("Aber du lässt nicht locker:"),
                du("fragst",
                        joinToWortfolge(anaphOderDesc.akkK(), "erneut:")));

        frageNachZiel(SENTENCE);
    }

    private void frageNachZiel() {
        frageNachZiel(PARAGRAPH);

    }

    private void frageNachZiel(final StructuralElement startsWith) {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        n.narrateAlt(secs(10),
                neuerSatz(startsWith, "„Wohin des Wegs?“ – „Was geht es dich an?“, ist "
                        + anaphOderDesc.possArt().vor(F).nomStr() // "ihre"
                        + " abweisende Antwort")
                        .phorikKandidat(anaphOderDesc, getGameObjectId())
                        .beendet(PARAGRAPH),
                neuerSatz(startsWith, "„Ihr habt es wohl eilig?“ – „So ist es“, antwortet "
                        + anaphOderDesc.persPron().nomStr()
                        + " dir")
                        .phorikKandidat(anaphOderDesc, getGameObjectId())
                        .beendet(PARAGRAPH)
        );

        // FIXME Weiteres Ansprechen der Zauberin, evtl. Reaktion

        // FIXME Weitere Antworten der Zauberin auf Frage nach dem Weg

        // FIXME Wenn im Gespräch: Hexe Kugel geben (Diebesgut...) (beendet Gespräch)

        // FIXME Hexe nach Turm fragen (Abneigung steigt - danach "misstrauisch...")

        setSchonBegruesstMitSC(true);
        talkerBeendetGespraech();
    }
}
