package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.german.base.Nominalphrase;

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

    public FroschprinzDescriptionComp(final GameObjectId id,
                                      final FroschprinzStateComp stateComp) {
        super(id);
        this.stateComp = stateComp;
        froschDescriptionTriple = new DescriptionTriple(
                np(M, "ein dicker, hässlicher Frosch",
                        "einem dicken, hässlichen Frosch",
                        "einen dicken, hässlichen Frosch"),
                np(M, "der hässliche Frosch",
                        "dem hässlichen Frosch",
                        "den hässlichen Frosch"),
                np(M, "der Frosch",
                        "dem Frosch",
                        "den Frosch"));
        prinzDescriptionTriple =
                new DescriptionTriple(
                        np(M, "ein junger Königssohn",
                                "einem jungen Königssohn",
                                "einen jungen Königssohn"),
                        np(M, "der junge Königssohn",
                                "dem jungen Königssohn",
                                "den jungen Königssohn"));
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
        switch (stateComp.getState()) {
            case ZURUECKVERWANDELT_IN_VORHALLE:
            case ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN:
                return prinzDescriptionTriple;
            default:
                return froschDescriptionTriple;
        }
    }
}
