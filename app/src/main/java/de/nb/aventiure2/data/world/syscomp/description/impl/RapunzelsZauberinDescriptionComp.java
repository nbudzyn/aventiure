package de.nb.aventiure2.data.world.syscomp.description.impl;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.MAGER;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.Belebtheit.BELEBT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FRAU;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ZAUBERIN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionTriple;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;

/**
 * Implementierung von {@link AbstractDescriptionComp} für Rapunzels Zauberin
 */
public class RapunzelsZauberinDescriptionComp extends MultiDescriptionComp {
    private final World world;

    private final DescriptionTriple magereFrauDescriptionTriple;
    private final DescriptionTriple zauberinDescriptionTriple;

    public RapunzelsZauberinDescriptionComp(final CounterDao counterDao, final World world) {
        super(RAPUNZELS_ZAUBERIN);
        this.world = world;

        magereFrauDescriptionTriple = new DescriptionTriple(
                counterDao,
                np(F, INDEF, "magere Frau mit krummer, bis zum Kinn "
                                + "reichender Nase",
                        "mageren Frau mit krummer, bis zum Kinn "
                                + "reichender Nase", BELEBT, RAPUNZELS_ZAUBERIN
                ),
                np(MAGER, FRAU, RAPUNZELS_ZAUBERIN),
                np(NomenFlexionsspalte.FRAU, RAPUNZELS_ZAUBERIN));
        zauberinDescriptionTriple = new DescriptionTriple(
                counterDao,
                np(INDEF, MAGER, ZAUBERIN, RAPUNZELS_ZAUBERIN),
                np(ZAUBERIN, RAPUNZELS_ZAUBERIN));
    }

    @Override
    protected DescriptionTriple chooseDescriptionTriple() {
        if (world.loadSC().memoryComp().isKnown(
                RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU)) {
            return zauberinDescriptionTriple;
        }

        return magereFrauDescriptionTriple;
    }
}
