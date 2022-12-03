package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

/**
 * Ein einzelnes "semantisches Prädikat" im Sinne eines Verbs mit allen Ergänzungen und Angaben,
 * jedoch ohne Subjekt, bei dem alle semantischen Leerstellen (mit Diskursreferenten) besetzt
 * sind ("mit dem Frosch reden").
 *
 * @see EinzelnerSemSatz
 */
public interface EinzelnesSemPraedikatOhneLeerstellen extends SemPraedikatOhneLeerstellen {
    @Override
    default ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(getFinit(textContext, anschlusswort, praedRegMerkmale));
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang
     * (in einem Verbzweitsatz würde dann noch das Subjekt davor stehen) - für ein
     * Subjekt wie dieses (was Person und Numerus angeht).
     */
    default AbstractFinitesPraedikat getFinit(
            final ITextContext textContext,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final SubstantivischePhrase subjekt) {
        return getFinit(textContext, konnektor, subjekt.getPraedRegMerkmale());
    }

    /**
     * Gibt eine Liste finiter Prädikat zurück ("nimmt das Schwert", "geht noch Norden").
     * <p>
     * Das finite Prädikat hat eine Person, Numerus etc. - Beispiel:
     * "[Ich] nehme das Schwert an mich" (nicht *"[Ich] nimmt das Schwert an sich")
     * <p>
     * Wenn ein syntaktisches Element erzeugt wird, darf diese Methode nicht mehrfach
     * aufgerufen werden - sofern wichtig ist, dass sich immer dasselbe Ergebnis ergibt.
     */
    AbstractFinitesPraedikat getFinit(final ITextContext textContext,
                                      @Nullable
                                      final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
                                      PraedRegMerkmale praedRegMerkmale);
}
