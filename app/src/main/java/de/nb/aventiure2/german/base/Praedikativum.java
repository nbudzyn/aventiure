package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die als Prädikativum dienen kann: "(Peter ist) ein Esel", (Peter ist) doof".
 */
public interface Praedikativum {
    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird:
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher" o.Ä.
     */
    Iterable<Konstituente> getPraedikativ(final Person person, final Numerus numerus);
}
