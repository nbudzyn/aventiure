package de.nb.aventiure2.data.world.syscomp.taking.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.GLUECKLICH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.NORMAL;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.taking.AbstractTakingComp;
import de.nb.aventiure2.data.world.syscomp.taking.SCTakeAction;
import de.nb.aventiure2.german.base.DeklinierbarePhraseUtil;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

public class RapunzelTakingComp extends AbstractTakingComp {
    private final RapunzelStateComp stateComp;

    private final FeelingsComp feelingsComp;

    public RapunzelTakingComp(final Narrator n, final World world,
                              final RapunzelStateComp stateComp,
                              final FeelingsComp feelingsComp) {
        super(RAPUNZEL, n, world);
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    public <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> getAction(final GIVEN given) {
        if (stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT)) {
            stateComp.narrateAndSetState(NORMAL);
        }

        if (given.is(GOLDENE_KUGEL)) {
            return SCTakeAction.zunaechstAngenommen(
                    given,
                    this::narrateTakerAndDo_GoldeneKugel);
        }

        return SCTakeAction.sofortAbgelehnt(
                given,
                this::narrateTakerAndDo_Sonstiges_Abgelehnt);
    }

    private <GIVEN extends IDescribableGO & ILocatableGO>
    void narrateTakerAndDo_Sonstiges_Abgelehnt(final GIVEN given) {
        final SubstantivischePhrase rapunzelAnaph = world.anaph(RAPUNZEL);

        final EinzelneSubstantivischePhrase givenDesc = world.getDescription(given);
        final EinzelneSubstantivischePhrase givenDescAtFirstSight =
                // "eine goldene Kugel" / "einige Binsen"
                getDescriptionAtFirstSight(given);

        n.narrateAlt(secs(10),
                neuerSatz(rapunzelAnaph.nomK(),
                        " sieht dich verwundert an"),
                neuerSatz("„Ähm, was soll ich damit?“,",
                        "ist",
                        rapunzelAnaph.possArt().vor(F).nomStr(),
                        "Reaktion"),
                neuerSatz("„Das ist nett“, sagt",
                        rapunzelAnaph.nomK(),
                        ",",
                        "„aber was mache ich damit?“ Du steckst",
                        givenDesc.akkK(),
                        "wieder ein"),
                neuerSatz(rapunzelAnaph.nomK(),
                        "schaut kurz, aber dann scheint",
                        rapunzelAnaph.persPron().nomK(), // "sie"
                        "das Interesse zu verlieren"),
                neuerSatz("„Verstehe“, sagt",
                        rapunzelAnaph.nomK(),
                        ", „wenn ich einmal",
                        givenDescAtFirstSight.akkK(),
                        "brauche,",
                        "weiß ich, wo ich",
                        // Gehen wir davon aus, dass der Benutzer nur
                        // "zählbare Dinge" jemandem geben kann, z.B.
                        // "den Stuhl" oder "den Wein", also "einen".
                        DeklinierbarePhraseUtil.getIndefinitAnapherZaehlbar(
                                givenDesc.getNumerusGenus()).akkK(),// "eine" / "welche"
                        "bekommen kann.“",
                        SENTENCE,
                        rapunzelAnaph.persPron().nomK(),
                        "lächelt dich",
                        (getZuneigungAbneigungTowardsSCMitEmpathischerSchranke()
                                > FeelingIntensity.NEUTRAL ?
                                "liebenswürdig" : "schnippisch"),
                        "an")
        );
        world.narrateAndUpgradeScKnownAndAssumedState(given);

        // Das Gespräch wird nicht beendet!
    }

    private int getZuneigungAbneigungTowardsSCMitEmpathischerSchranke() {
        return feelingsComp.getFeelingTowardsForActionsMitEmpathischerSchranke(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);
    }

    private <GIVEN extends IDescribableGO & ILocatableGO> void narrateTakerAndDo_GoldeneKugel(
            final GIVEN given) {
        final SubstantivischePhrase rapunzelAnaph = world.anaph(RAPUNZEL);
        final SubstantivischePhrase givenAnaph = world.anaph(given);

        final EinzelneSubstantivischePhrase givenDesc = world.getDescription(given);
        final EinzelneSubstantivischePhrase givenDescShort = world.getDescription(given, true);

        feelingsComp.upgradeFeelingsTowards(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG, 0.3f, FeelingIntensity.DEUTLICH);

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(rapunzelAnaph.nomK(),
                "dreht",
                givenAnaph.akkK(),
                "in den Händen und wirft",
                givenAnaph.persPron().akkK(),
                "sanft",
                "in die Höhe – dann gibt",
                rapunzelAnaph.persPron().nomK(),
                "dir",
                givenDesc.akkK(),
                "zurück"));

        if (getZuneigungAbneigungTowardsSCMitEmpathischerSchranke() >= FeelingIntensity.MERKLICH) {
            alt.add(neuerSatz(rapunzelAnaph.nomK(),
                    "schaut dich",
                    (stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT) ? "glücklich" : "überrascht"),
                    "an und nimmt",
                    givenDescShort.akkK(),
                    SENTENCE,
                    rapunzelAnaph.persPron().nomK(),
                    "spielt",
                    "eine Weile damit herum, dann gibt",
                    rapunzelAnaph.persPron().nomK(),
                    givenDescShort.persPron().akkK(),
                    "dir zurück"),
                    neuerSatz((stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT) ?
                                    "Gespannt" : "Überrascht"),
                            "nimmt",
                            rapunzelAnaph.nomK(),
                            givenDescShort.akkK(),
                            "und versucht,",
                            rapunzelAnaph.reflPron().akk(), // sich
                            "darin zu spiegeln",
                            SENTENCE,
                            rapunzelAnaph.persPron().nomK(),
                            "streicht eine Locke zurecht, dann gibt",
                            rapunzelAnaph.persPron().nomK(),
                            givenDescShort.persPron().akkK(),
                            "dir zurück und lächelt dich an")
            );
        }

        n.narrateAlt(alt, secs(30));

        world.narrateAndUpgradeScKnownAndAssumedState(given);

        world.loadSC().feelingsComp().upgradeFeelingsTowards(
                RAPUNZEL, ZUNEIGUNG_ABNEIGUNG, 0.5f, FeelingIntensity.DEUTLICH);
        world.loadSC().feelingsComp().requestMoodMin(GLUECKLICH);

        // Das Gespräch wird nicht beendet!
    }
}
