package de.nb.aventiure2.data.world.syscomp.taking.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.taking.AbstractTakingComp;
import de.nb.aventiure2.data.world.syscomp.taking.SCTakeAction;
import de.nb.aventiure2.german.base.DeklinierbarePhraseUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.GLUECKLICH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public class RapunzelTakingComp extends AbstractTakingComp {
    private final RapunzelStateComp stateComp;
    private final MemoryComp memoryComp;

    @Nullable
    private final FeelingsComp feelingsComp;

    public RapunzelTakingComp(final AvDatabase db, final Narrator n, final World world,
                              final RapunzelStateComp stateComp,
                              final MemoryComp memoryComp,
                              @Nullable final FeelingsComp feelingsComp) {
        super(RAPUNZEL, db, n, world);
        this.stateComp = stateComp;
        this.memoryComp = memoryComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    public <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> getAction(final GIVEN given) {
        if (stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT)) {
            stateComp.narrateAndSetState(STILL);
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
        final SubstantivischePhrase rapunzelAnaph =
                world.getAnaphPersPronWennMglSonstShortDescription(RAPUNZEL);

        final Nominalphrase givenDesc = world.getDescription(given);
        final Nominalphrase givenDescAtFirstSight =
                // "eine goldene Kugel"
                given.descriptionComp().getDescriptionAtFirstSight();

        n.narrateAlt(secs(10),
                neuerSatz(rapunzelAnaph.nomStr()
                        + " sieht dich verwundert an"),
                neuerSatz("„Ähm, was soll ich damit?“, "
                        + "ist "
                        + rapunzelAnaph.possArt().vor(F).nomStr()
                        + " Reaktion"),
                neuerSatz("„Das ist nett“, sagt "
                        + rapunzelAnaph.nomStr()
                        + ", "
                        + "„aber was mache ich damit?“ Du steckst "
                        + givenDesc.akkStr()
                        + " wieder ein"),
                neuerSatz(rapunzelAnaph.nomStr()
                        + " schaut kurz, aber dann scheint "
                        + rapunzelAnaph.persPron().nomStr() // "sie"
                        + " das Interesse zu verlieren"),
                neuerSatz("„Verstehe“, sagt "
                        + rapunzelAnaph.nomStr()
                        + ", „wenn ich einmal "
                        + givenDescAtFirstSight.akkStr()
                        + " brauche, "
                        + "weiß ich, wo ich "
                        // Gehen wir davon aus, dass der Benutzer nur
                        // "zählbare Dinge" jemandem geben kann, z.B.
                        // "den Stuhl" oder "den Wein", also "einen".
                        + DeklinierbarePhraseUtil.getIndefinitAnapherZaehlbar(
                        givenDesc.getNumerusGenus()).akkStr() // "eine" / "welche"
                        + " bekommen kann.“ "
                        + capitalize(rapunzelAnaph.persPron().nomStr())
                        + " lächelt dich "
                        + (getZuneigungAbneigungTowardsSC()
                        > FeelingIntensity.NEUTRAL ?
                        "liebenswürdig" : "schnippisch")
                        + " an")
        );
        memoryComp.upgradeKnown(given);

        // Das Gespräch wird nicht beendet!
    }

    private int getZuneigungAbneigungTowardsSC() {
        if (feelingsComp == null) {
            return FeelingIntensity.NEUTRAL;
        }

        return feelingsComp.getFeelingTowards(SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG);
    }

    private <GIVEN extends IDescribableGO & ILocatableGO> void narrateTakerAndDo_GoldeneKugel(
            final GIVEN given) {
        final SubstantivischePhrase rapunzelAnaph =
                world.getAnaphPersPronWennMglSonstShortDescription(RAPUNZEL);
        final SubstantivischePhrase givenAnaph =
                world.getAnaphPersPronWennMglSonstShortDescription(given);

        final Nominalphrase givenDesc = world.getDescription(given);
        final Nominalphrase givenDescShort = world.getDescription(given, true);

        upgradeZuneigungAbneigung(1f, FeelingIntensity.DEUTLICH);

        n.narrateAlt(secs(30),
                neuerSatz(rapunzelAnaph.nomStr()
                        + " dreht "
                        + givenAnaph.akkStr()
                        + " in den Händen und wirft "
                        + givenAnaph.persPron().akkStr()
                        + " sanft "
                        + "in die Höhe - dann gibt "
                        + rapunzelAnaph.persPron().nomStr()
                        + " dir "
                        + givenDesc.akkStr()
                        + " zurück"));
        if (getZuneigungAbneigungTowardsSC() >= FeelingIntensity.MERKLICH) {
            n.narrateAlt(secs(30), neuerSatz(rapunzelAnaph.nomStr()
                            + " schaut dich "
                            + (stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT) ? "glücklich" : "überrascht")
                            + " an und nimmt "
                            + givenDescShort.akkStr()
                            + ". "
                            + capitalize(rapunzelAnaph.persPron().nomStr())
                            + " spielt "
                            + "eine Weile damit herum, dann gibt "
                            + rapunzelAnaph.persPron().nomStr()
                            + " "
                            + givenDescShort.persPron().akkStr()
                            + " dir zurück"),
                    neuerSatz((stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT) ?
                            "Gespannt" : "Überrascht")
                            + " nimmt "
                            + rapunzelAnaph.nomStr()
                            + " "
                            + givenDescShort.akkStr()
                            + " und versucht, "
                            + rapunzelAnaph.reflPron().akk() // sich
                            + " darin zu spiegeln. "
                            + capitalize(rapunzelAnaph.persPron().nomStr())
                            + " streicht eine Locke zurecht, dann gibt "
                            + rapunzelAnaph.persPron().nomStr()
                            + " "
                            + givenDescShort.persPron().akkStr()
                            + " dir zurück und lächelt dich an")
            );
        }

        memoryComp.upgradeKnown(given);

        world.loadSC().feelingsComp().upgradeFeelingsTowards(
                RAPUNZEL, ZUNEIGUNG_ABNEIGUNG, 1, FeelingIntensity.DEUTLICH);
        world.loadSC().feelingsComp().requestMoodMin(GLUECKLICH);

        // Das Gespräch wird nicht beendet!
    }

    private void upgradeZuneigungAbneigung(final float increment, final int bound) {
        if (feelingsComp == null) {
            return;
        }

        feelingsComp.upgradeFeelingsTowards(
                SPIELER_CHARAKTER, ZUNEIGUNG_ABNEIGUNG, increment, bound);
    }
}
