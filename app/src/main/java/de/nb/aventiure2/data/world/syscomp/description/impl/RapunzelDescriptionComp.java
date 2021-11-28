package de.nb.aventiure2.data.world.syscomp.description.impl;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.JUNG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHOEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WUNDERSCHOEN;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.DEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FRAU;
import static de.nb.aventiure2.german.base.Nominalphrase.np;

import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionTriple;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;

/**
 * Implementierung von {@link AbstractDescriptionComp} für Rapunzel
 */
public class RapunzelDescriptionComp extends MultiDescriptionComp {
    private final World world;

    private final DescriptionTriple jungeFrauDescriptionTriple;
    private final DescriptionTriple rapunzelDescriptionTriple;

    public RapunzelDescriptionComp(final CounterDao counterDao, final World world) {
        super(RAPUNZEL);
        this.world = world;

        jungeFrauDescriptionTriple = new DescriptionTriple(
                counterDao,
                np(INDEF,
                        new ZweiAdjPhrOhneLeerstellen(
                                WUNDERSCHOEN,
                                false,
                                JUNG),
                        FRAU,
                        RAPUNZEL),
                np(DEF, new ZweiAdjPhrOhneLeerstellen(
                                SCHOEN,
                                false,
                                JUNG),
                        FRAU,
                        RAPUNZEL),
                np(JUNG, FRAU, RAPUNZEL));
        rapunzelDescriptionTriple = new DescriptionTriple(
                counterDao,
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
