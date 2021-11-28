package de.nb.aventiure2.data.world.syscomp.description.impl;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DICK;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HAESSLICH;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.DEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.Belebtheit.BELEBT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FROSCH;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionTriple;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;

/**
 * Implementierung von {@link AbstractDescriptionComp} für den
 * Froschprinzen.
 */
public class FroschprinzDescriptionComp extends MultiDescriptionComp {
    private final FroschprinzStateComp stateComp;

    private final DescriptionTriple froschDescriptionTriple;
    private final DescriptionTriple prinzDescriptionTriple;

    public FroschprinzDescriptionComp(final CounterDao counterDao,
                                      final FroschprinzStateComp stateComp) {
        super(FROSCHPRINZ);
        this.stateComp = stateComp;
        froschDescriptionTriple = new DescriptionTriple(
                counterDao,
                np(INDEF,
                        new ZweiAdjPhrOhneLeerstellen(
                                DICK,
                                true,
                                HAESSLICH),
                        FROSCH,
                        FROSCHPRINZ),
                np(DEF, HAESSLICH, FROSCH, FROSCHPRINZ),
                np(FROSCH, FROSCHPRINZ));
        prinzDescriptionTriple =
                new DescriptionTriple(
                        counterDao,
                        np(M, INDEF, "junger Königssohn",
                                "jungen Königssohn",
                                "jungen Königssohn", BELEBT, FROSCHPRINZ
                        ),
                        np(M, DEF, "junge Königssohn",
                                "jungen Königssohn",
                                "jungen Königssohn", BELEBT, FROSCHPRINZ

                        ));
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
