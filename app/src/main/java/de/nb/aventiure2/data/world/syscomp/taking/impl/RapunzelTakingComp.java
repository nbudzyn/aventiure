package de.nb.aventiure2.data.world.syscomp.taking.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.taking.AbstractTakingComp;
import de.nb.aventiure2.data.world.syscomp.taking.SCTakeAction;
import de.nb.aventiure2.german.base.DeklinierbarePhraseUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;

public class RapunzelTakingComp extends AbstractTakingComp {
    private final MemoryComp memoryComp;

    public RapunzelTakingComp(final AvDatabase db, final World world,
                              final MemoryComp memoryComp) {
        super(RAPUNZEL, db, world);
        this.memoryComp = memoryComp;
    }

    @Override
    public <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> getAction(final GIVEN given) {
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

        n.addAlt(
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
        final Nominalphrase givenDescAtFirstSight =
                // "eine goldene Kugel"
                given.descriptionComp().getDescriptionAtFirstSight();

        n.addAlt(
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
                        + " zurück", secs(30))
                // STORY Alternative Texte
        );
        memoryComp.upgradeKnown(given);

        // Das Gespräch wird nicht beendet!
    }
}
