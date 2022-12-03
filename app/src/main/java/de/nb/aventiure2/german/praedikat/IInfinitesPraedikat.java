package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Ebene innerhalb eines finiten Prädikats (also in einem {@link KomplexesFinitesPraedikat}):
 * <ul>
 * <li>"laufen" / "Spannendes berichten" / "gelaufen sein" (normaler Infinitiv)
 * <li>"den Weg gelaufen" / "Spannendes berichtet" (Partizip II)
 * <li>"Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
 * <li>"zu schlafen", "keine Gefahr darzustellen" (Inifitiv mit zu)
 * </ul>
 */
interface IInfinitesPraedikat extends IKonstituentenfolgable {
    static Konstituentenfolge toKonstituentenfolge(
            final List<? extends IInfinitesPraedikat> infinitePraedikate) {
        Konstituentenfolge res = null;

        for (int i = 0; i < infinitePraedikate.size(); i++) {
            final IInfinitesPraedikat zuInfinitiv = infinitePraedikate.get(i);

            res = Konstituentenfolge.joinToKonstituentenfolge(
                    res,
                    i > 0 && i == infinitePraedikate.size() - 1
                            && zuInfinitiv.getKonnektor() == null ?
                            zuInfinitiv.mitKonnektor(UND) :
                            zuInfinitiv
                                    .toKonstituentenfolge()
                                    .withVorkommaNoetigMin(
                                            i > 0 && i < infinitePraedikate.size() - 1
                                                    && zuInfinitiv.getKonnektor() == null)
            );
        }

        return res;
    }

    @Nullable
    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor();

    default IInfinitesPraedikat mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    IInfinitesPraedikat mitKonnektor(
            @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor);

    IInfinitesPraedikat ohneKonnektor();

    @Nullable
    @Override
    default Konstituentenfolge toKonstituentenfolge() {
        return joinToKonstituentenfolge(
                toKonstituentenfolgeOhneNachfeld(null, false),
                getNachfeld()
        );
    }

    Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean mitNachfeld);

    /**
     * Ob die finite Verbform bei Verbletztstellung im Oberfeld - also vor dem übrigen
     * Verbalkomplex - erscheinen soll ("dass er es hat wissen wollen") oder nicht
     * ("dass er es gewusst hat"). (Dies ist eine seltene Ausnahme, die im wesentlichen
     * das Wort "hat" mit mehreren reinen Infinitiven betrifft.)
     */
    boolean finiteVerbformBeiVerbletztstellungImOberfeld();

    @Nonnull
    Nachfeld getNachfeld();

    @Nullable
    Vorfeld getSpeziellesVorfeldSehrErwuenscht();

    @Nullable
    Vorfeld getSpeziellesVorfeldAlsWeitereOption();

    @Nullable
    Konstituentenfolge getRelativpronomen();

    @Nullable
    Konstituentenfolge getErstesInterrogativwort();
}
