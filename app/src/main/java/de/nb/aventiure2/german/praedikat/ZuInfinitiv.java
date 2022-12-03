package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine Infinitivkonstruktion mit "zu" ("den Frosch zu ignorieren", "das Leben zu genießen").
 */
public interface ZuInfinitiv extends IInfinitesPraedikat, IKonstituentenfolgable {
    @Override
    ZuInfinitiv mitKonnektorUndFallsKeinKonnektor();

    @Override
    ZuInfinitiv mitKonnektor(
            @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor);

    @Override
    ZuInfinitiv ohneKonnektor();

    @Override
    boolean finiteVerbformBeiVerbletztstellungImOberfeld();
}
