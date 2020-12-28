package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Zwei Adjektivphrasen ohne Leerstellen, die mit <i>und</i>
 * verbunden werden
 */
public class ZweiAdjPhrOhneLeerstellen implements AdjPhrOhneLeerstellen {
    private final AdjPhrOhneLeerstellen ersteAdjPhr;
    private final AdjPhrOhneLeerstellen zweiteAdjPhr;

    public ZweiAdjPhrOhneLeerstellen(
            final AdjPhrOhneLeerstellen ersteAdjPhr,
            final AdjPhrOhneLeerstellen zweiteAdjPhr) {
        this.ersteAdjPhr = ersteAdjPhr;
        this.zweiteAdjPhr = zweiteAdjPhr;
    }

    @Override
    public AdjPhrOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                ersteAdjPhr.mitGraduativerAngabe(graduativeAngabe),
                zweiteAdjPhr
        );
    }

    @Override
    public Iterable<Konstituente> getPraedikativOderAdverbial(final Person person,
                                                              final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersteAdjPhr.getPraedikativ(person, numerus),
                "und",
                zweiteAdjPhr.getPraedikativ(person, numerus)
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return ersteAdjPhr.enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() ||
                zweiteAdjPhr.enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }

}
