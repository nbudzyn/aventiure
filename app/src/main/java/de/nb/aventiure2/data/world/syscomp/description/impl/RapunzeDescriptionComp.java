package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

/**
 * Implementierung von {@link AbstractDescriptionComp} für Rapunzel
 */
public class RapunzeDescriptionComp extends MultiDescriptionComp {
    private final World world;

    private final DescriptionTriple jungeFrauDescriptionTriple;
    private final DescriptionTriple rapunzelDescriptionTriple;

    public RapunzeDescriptionComp(final World world) {
        super(RAPUNZEL);
        this.world = world;

        jungeFrauDescriptionTriple = new DescriptionTriple(
                np(F, INDEF, "wunderschöne junge Frau",
                        "wunderschönen jungen Frau", RAPUNZEL),
                np(F, DEF, "schöne junge Frau",
                        "schönen jungen Frau", RAPUNZEL),
                np(F, DEF, "junge Frau",
                        "jungen Frau", RAPUNZEL));
        rapunzelDescriptionTriple = new DescriptionTriple(
                np(NomenFlexionsspalte.RAPUNZEL, RAPUNZEL),
                np(NomenFlexionsspalte.RAPUNZEL, RAPUNZEL));
    }

    @Override
    protected DescriptionTriple chooseDescriptionTriple() {
        if (world.loadSC().memoryComp().isKnown(RAPUNZELS_NAME)) {
            return rapunzelDescriptionTriple;
        }

        return jungeFrauDescriptionTriple;
    }
}
