package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entryReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStNSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntryStSCHatteGespraechBeendet;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.base.Nominalphrase.IHR_ZIEL;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
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
                                neuerSatz("„Schön euch zu sehen“, sprichst du",
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

        n.narrateAlt(altSaetze(altReaktionSaetze), secs(5));

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
                        du(SICH_VERABSCHIEDEN.mit(anaph)))
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

        setSchonBegruesstMitSC(true);

        if (feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG)
                <= -FeelingIntensity.DEUTLICH) {
            talkerBeendetGespraech();
        }
    }
}
