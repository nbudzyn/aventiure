package de.nb.aventiure2.german.adjektiv;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Eine Adjektivphrase mit zu-Infinitiv, in der alle Leerstellen besetzt sind. Beispiel:
 * <ul>
 *     <li>"glücklich, Peter zu sehen"
 *     <li>"sehr glücklich, Peter zu sehen"
 * </ul>
 */
public class AdjPhrMitZuInfinitivOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    /**
     * "(...glücklich,) Peter zu sehen"
     */
    @Nonnull
    @Komplement
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    AdjPhrMitZuInfinitivOhneLeerstellen(
            final Adjektiv adjektiv,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(null, adjektiv, lexikalischerKern);
    }

    private AdjPhrMitZuInfinitivOhneLeerstellen(
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(graduativeAngabe, adjektiv);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public AdjPhrMitZuInfinitivOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrMitZuInfinitivOhneLeerstellen(
                graduativeAngabe,
                getAdjektiv(),
                lexikalischerKern
        );
    }

    @Override
    public Iterable<Konstituente> getPraedikativOderAdverbial(final Person person,
                                                              final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                getGraduativeAngabe(), // "sehr"
                k(getAdjektiv().getPraedikativ()), // "glücklich"
                getPraedikativAnteilKandidatFuerNachfeld(person, numerus)
                // ", sich erheben zu dürfen[, ]"
        );
    }

    @Override
    public Iterable<Konstituente> getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                           final Numerus numerus) {
        return Konstituente.schliesseInKommaEin(
                lexikalischerKern.getZuInfinitiv(person, numerus)
                // "[,] sich erheben zu dürfen[,] "
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return true;
    }
}
