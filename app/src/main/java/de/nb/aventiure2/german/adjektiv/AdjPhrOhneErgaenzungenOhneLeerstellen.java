package de.nb.aventiure2.german.adjektiv;


import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Eine Adjektivphrase die keiner Ergänzungen fordert. Beispiele:
 * <ul>
 *     <li>"glücklich"
 *     <li>"sehr glücklich"
 * </ul>
 */
public class AdjPhrOhneErgaenzungenOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    @Valenz
    AdjPhrOhneErgaenzungenOhneLeerstellen(
            final Adjektiv adjektiv) {
        this(null, adjektiv);
    }

    private AdjPhrOhneErgaenzungenOhneLeerstellen(
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv) {
        super(graduativeAngabe, adjektiv);
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrOhneErgaenzungenOhneLeerstellen(
                graduativeAngabe,
                getAdjektiv()
        );
    }

    @Override
    public Konstituentenfolge getPraedikativOderAdverbial(final Person person,
                                                          final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getGraduativeAngabe(), // "sehr"
                getAdjektiv().getPraedikativ() // "zufrieden"
        );
    }

    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return null;
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return false;
    }
}
