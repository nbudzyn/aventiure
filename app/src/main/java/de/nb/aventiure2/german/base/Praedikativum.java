package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die als Prädikativum dienen kann: "(Peter ist) ein Esel", (Peter ist) doof".
 */
public interface Praedikativum {
    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, - ohne den Anteil, der nach Möglichkeit
     * ins Nachfeld gestellt werden soll.
     */
    default Iterable<Konstituente> getPraedikativOhneAnteilKandidatFuerNachfeld(
            final Person person, final Numerus numerus) {
        return Konstituente.cutLast(
                getPraedikativ(person, numerus),
                getPraedikativAnteilKandidatFuerNachfeld(person, numerus));
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird:
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher" o.Ä.
     */
    Iterable<Konstituente> getPraedikativ(final Person person, final Numerus numerus);

    /**
     * Liefert die Teil-Konstituenten-Folge von {@link #getPraedikativ(Person, Numerus)},
     * die nach Möglichkeit in das Nachfeld gestellt werden sollte - so dass letztlich
     * eine diskontinuierliche Konstituente entsteht. Ist in einfachen Fällen leer.
     * <p>
     * Als Beispiel sollen sich letztlich Sätze ergeben wie "Sie ist glücklich gewesen, dich
     * zu sehen."
     */
    Iterable<Konstituente> getPraedikativAnteilKandidatFuerNachfeld(
            final Person person, final Numerus numerus);

}
