package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

/**
 * Implementierung von {@link AbstractDescriptionComp} für den
 * Froschprinzen.
 */
public class FroschprinzDescriptionComp extends MultiDescriptionComp {
    private final FroschprinzStateComp stateComp;

    private final DescriptionTriple froschDescriptionTriple;
    private final DescriptionTriple prinzDescriptionTriple;

    public FroschprinzDescriptionComp(final FroschprinzStateComp stateComp) {
        super(FROSCHPRINZ);
        this.stateComp = stateComp;
        froschDescriptionTriple = new DescriptionTriple(
                np(M, INDEF, "dicker, hässlicher Frosch",
                        "dicken, hässlichen Frosch",
                        "dicken, hässlichen Frosch", FROSCHPRINZ),
                np(M, DEF, "hässliche Frosch",
                        "hässlichen Frosch",
                        "hässlichen Frosch", FROSCHPRINZ),
                np(M, DEF, "Frosch", FROSCHPRINZ));
        prinzDescriptionTriple =
                new DescriptionTriple(
                        np(M, INDEF, "junger Königssohn",
                                "jungen Königssohn",
                                "jungen Königssohn", FROSCHPRINZ),
                        np(M, DEF, "junge Königssohn",
                                "jungen Königssohn",
                                "jungen Königssohn", FROSCHPRINZ));
    }

    @Override
    protected DescriptionTriple chooseDescriptionTriple() {
        switch (stateComp.getState().getGestalt()) {
            case FROSCH:
                return froschDescriptionTriple;
            case MENSCH:
                return prinzDescriptionTriple;
            default:
                throw new IllegalStateException("Unerwartete Gestalt: " +
                        stateComp.getState().getGestalt());
        }
    }
}
