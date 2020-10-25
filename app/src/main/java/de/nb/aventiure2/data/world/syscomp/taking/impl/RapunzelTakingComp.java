package de.nb.aventiure2.data.world.syscomp.taking.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.taking.AbstractTakingComp;
import de.nb.aventiure2.data.world.syscomp.taking.SCTakeAction;
import de.nb.aventiure2.german.base.DeklinierbarePhraseUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.GLUECKLICH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAT_NACH_KUGEL_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;

public class RapunzelTakingComp extends AbstractTakingComp {
    private final RapunzelStateComp stateComp;
    private final MemoryComp memoryComp;

    public RapunzelTakingComp(final AvDatabase db, final Narrator n, final World world,
                              final RapunzelStateComp stateComp,
                              final MemoryComp memoryComp) {
        super(RAPUNZEL, db, n, world);
        this.stateComp = stateComp;
        this.memoryComp = memoryComp;
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

    public <GIVEN extends IDescribableGO & ILocatableGO>
    void narrateTakerAndDo_Sonstiges_Abgelehnt(final GIVEN given) {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstShortDescription(RAPUNZEL);

        final Nominalphrase givenDesc = world.getDescription(given);
        final Nominalphrase givenDescAtFirstSight =
                // "eine goldene Kugel"
                given.descriptionComp().getDescriptionAtFirstSight();

        n.narrateAlt(
                neuerSatz(rapunzelAnaph.nom()
                        + " sieht dich verwundert an", secs(10)),
                neuerSatz("„Ähm, was soll ich damit?“, "
                        + "ist "
                        + rapunzelAnaph.possArt().vor(F).nom()
                        + " Reaktion", secs(10)),
                neuerSatz("„Das ist nett“, sagt "
                                + rapunzelAnaph.nom()
                                + ", "
                                + "„aber was mache ich damit?“ Du steckst "
                                + givenDesc.akk()
                                + " wieder ein",
                        secs(10)),
                neuerSatz(rapunzelAnaph.nom()
                        + " schaut kurz, aber dann scheint "
                        + rapunzelAnaph.persPron().nom() // "sie"
                        + " das Interesse zu verlieren", secs(10)),
                neuerSatz("„Verstehe“, sagt "
                        + rapunzelAnaph.nom()
                        + ", „wenn ich einmal "
                        + givenDescAtFirstSight.akk()
                        + " brauche, "
                        + "weiß ich, wo ich "
                        // Gehen wir davon aus, dass der Benutzer nur
                        // "zählbare Dinge" jemandem geben kann, z.B.
                        // "den Stuhl" oder "den Wein", also "einen".
                        + DeklinierbarePhraseUtil.getIndefinitAnapherZaehlbar(
                        givenDesc.getNumerusGenus()).akk() // "eine" / "welche"
                        + " bekommen kann.“ "
                        + capitalize(rapunzelAnaph.persPron().nom())
                        + " lächelt dich liebenswürdig an", secs(10))
        );
        memoryComp.upgradeKnown(given);

        // Das Gespräch wird nicht beendet!
    }

    private <GIVEN extends IDescribableGO & ILocatableGO> void narrateTakerAndDo_GoldeneKugel(
            final GIVEN given) {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstShortDescription(RAPUNZEL);
        final SubstantivischePhrase givenAnaph =
                getAnaphPersPronWennMglSonstShortDescription(given);

        final Nominalphrase givenDesc = world.getDescription(given);
        final Nominalphrase givenDescShort = world.getDescription(given, true);
        final Nominalphrase givenDescAtFirstSight =
                // "eine goldene Kugel"
                given.descriptionComp().getDescriptionAtFirstSight();

        n.narrateAlt(
                neuerSatz(rapunzelAnaph.nom()
                        + " dreht "
                        + givenAnaph.akk()
                        + " in den Händen und wirft "
                        + givenAnaph.persPron().akk()
                        + " sanft "
                        + "in die Höhe - dann gibt "
                        + rapunzelAnaph.persPron().nom()
                        + " dir "
                        + givenDesc.akk()
                        + " zurück", secs(30)),
                neuerSatz(rapunzelAnaph.nom()
                        + " schaut dich "
                        + (stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT) ? "glücklich" : "überrascht")
                        + " an und nimmt "
                        + givenDescShort.akk()
                        + ". "
                        + capitalize(rapunzelAnaph.persPron().nom())
                        + " spielt "
                        + "eine Weile damit herum, dann gibt "
                        + rapunzelAnaph.persPron().nom()
                        + " "
                        + givenDescShort.persPron().akk()
                        + " dir zurück", secs(30)),
                neuerSatz((stateComp.hasState(HAT_NACH_KUGEL_GEFRAGT) ?
                                "Gespannt" : "Überrascht")
                                + " nimmt "
                                + rapunzelAnaph.nom()
                                + " "
                                + givenDescShort.akk()
                                + " und versucht, "
                                + rapunzelAnaph.reflPron().akk() // sich
                                + " darin zu spiegeln. "
                                + capitalize(rapunzelAnaph.persPron().nom())
                                + " streicht eine Locke zurecht, dann gibt "
                                + rapunzelAnaph.persPron().nom()
                                + " "
                                + givenDescShort.persPron().akk()
                                + " dir zurück und lächelt dich an",
                        secs(30))
        );

        memoryComp.upgradeKnown(given);

        world.loadSC().feelingsComp().upgradeFeelingsTowards(
                RAPUNZEL, ZUNEIGUNG_ABNEIGUNG, 1, FeelingIntensity.DEUTLICH);
        world.loadSC().feelingsComp().requestMoodMin(GLUECKLICH);

        // Das Gespräch wird nicht beendet!
    }
}
