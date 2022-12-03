package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine einfache Infinitivkonstruktion mit "zu" ("den Frosch zu ignorieren", "das Leben zu
 * genießen").
 */
public class EinfacherZuInfinitiv extends AbstractEinfacherInfinitiv
        implements ZuInfinitiv {
    EinfacherZuInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final Verb verb) {
        this(konnektor, TopolFelder.EMPTY, verb);
    }

    EinfacherZuInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final TopolFelder topolFelder,
            final Verb verb) {
        super(// "danach Spannendes" / ": Odysseus ist zurück."
                konnektor, topolFelder,
                "zu "
                        + verb.getInfinitiv() // "berichten"
        );
    }

    private EinfacherZuInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final TopolFelder topolFelder, final String verbalkomplex) {
        super(konnektor, topolFelder, verbalkomplex);
    }

    @Override
    public ZuInfinitiv mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    @Override
    public ZuInfinitiv mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        if (konnektor == null) {
            return this;
        }

        return new EinfacherZuInfinitiv(konnektor,
                getTopolFelder(),
                getVerbalkomplex());
    }

    @Override
    public ZuInfinitiv ohneKonnektor() {
        if (getKonnektor() == null) {
            return this;
        }

        return new EinfacherZuInfinitiv(null,
                getTopolFelder(),
                getVerbalkomplex());
    }

    @Override
    public boolean finiteVerbformBeiVerbletztstellungImOberfeld() {
        // "zu schlafen scheint", "darzustellen scheint" etc.
        return false;
    }
}
