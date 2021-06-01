package de.nb.aventiure2.data.world.syscomp.description.impl;

import com.google.common.collect.ImmutableMap;

import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState;
import de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.GEBROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.NOCH_NICHT_GEBROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.NOCH_NICHT_GESAMMELT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GESAMMELT;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HOLZ;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KLAUBHOLZ;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.RUNDHOELZER;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

/**
 * Implementierung von {@link AbstractDescriptionComp} für das Holz,
 * das der Sturm von den Bäumen bricht und aus dem
 * der SC dann eine Strickleiter baut.
 */
public class HolzFuerStrickleiterDescriptionComp
        extends StateMapDescriptionComp<HolzFuerStrickleiterState> {
    public HolzFuerStrickleiterDescriptionComp(final HolzFuerStrickleiterStateComp stateComp) {
        super(HOLZ_FUER_STRICKLEITER, stateComp,
                ImmutableMap.of(
                        NOCH_NICHT_GESAMMELT, new DescriptionTriple(
                                np(PL_MFN, INDEF,
                                        "dünne und kräftige von den Bäumen gebrochene Äste",
                                        "dünnen und kräftigen von den Bäumen gebrochenen Ästen",
                                        "dünnen und kräftigen von den Bäumen gebrochenen Äste",
                                        HOLZ_FUER_STRICKLEITER),
                                np(N, DEF, "vom Sturm abgebrochene Holz",
                                        "vom Sturm abgebrochenen Holz", HOLZ_FUER_STRICKLEITER),
                                np(HOLZ, HOLZ_FUER_STRICKLEITER)),
                        NOCH_NICHT_GEBROCHEN, new DescriptionTriple(
                                np(null, GESAMMELT, HOLZ, HOLZ_FUER_STRICKLEITER),
                                np(DEF, GESAMMELT, HOLZ, HOLZ_FUER_STRICKLEITER),
                                np(KLAUBHOLZ, HOLZ_FUER_STRICKLEITER)),
                        GEBROCHEN, new DescriptionTriple(
                                np(PL_MFN, INDEF, "in handliche Stücke gebrochene Äste",
                                        "in handliche Stücke gebrochenen Ästen",
                                        "in handliche Stücke gebrochene Äste",
                                        HOLZ_FUER_STRICKLEITER),
                                np(PL_MFN, DEF, "in handliche Stücke gebrochenen Äste",
                                        "in handliche Stücke gebrochenen Ästen",
                                        "in handliche Stücke gebrochenen Äste",
                                        HOLZ_FUER_STRICKLEITER),
                                np(RUNDHOELZER, HOLZ_FUER_STRICKLEITER))));
    }
}
