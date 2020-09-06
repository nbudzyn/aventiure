package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

/**
 * Einfache Implementierung von {@link AbstractDescriptionComp} für den
 * Froschprinzen.
 */
public class FroschprinzDescriptionComp extends AbstractDescriptionComp {
    private final FroschprinzStateComp stateComp;

    private final DescriptionTriple froschDescriptionTriple;
    private final DescriptionTriple prinzDescriptionTriple;

    public FroschprinzDescriptionComp(final FroschprinzStateComp stateComp) {
        super(FROSCHPRINZ);
        this.stateComp = stateComp;
        froschDescriptionTriple = new DescriptionTriple(
                np(M, INDEF, "dicker, hässlicher Frosch",
                        "dicken, hässlichen Frosch",
                        "dicken, hässlichen Frosch"),
                np(M, DEF, "hässliche Frosch",
                        "hässlichen Frosch",
                        "hässlichen Frosch"),
                np(M, DEF, "Frosch"));
        prinzDescriptionTriple =
                new DescriptionTriple(
                        np(M, INDEF, "junger Königssohn",
                                "jungen Königssohn",
                                "jungen Königssohn"),
                        np(M, DEF, "junge Königssohn",
                                "jungen Königssohn",
                                "jungen Königssohn"));
    }

    @Override
    public Nominalphrase getDescriptionAtFirstSight() {
        return chooseDescriptionTriple().getDescriptionAtFirstSight();
    }

    @Override
    public Nominalphrase getNormalDescriptionWhenKnown() {
        return chooseDescriptionTriple().getNormalDescriptionWhenKnown();
    }

    @Override
    public Nominalphrase getShortDescriptionWhenKnown() {
        return chooseDescriptionTriple().getShortDescriptionWhenKnown();
    }

    private DescriptionTriple chooseDescriptionTriple() {
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
