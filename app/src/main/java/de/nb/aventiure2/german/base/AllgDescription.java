package de.nb.aventiure2.german.base;

import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * A general description. The subject may be anything.
 */
public class AllgDescription extends AbstractDescription<AllgDescription> {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private final String description;

    public static AllgDescription neuerSatz(final String description,
                                            final AvTimeSpan timeElapsed) {
        return new AllgDescription(StructuralElement.SENTENCE, description, timeElapsed);
    }

    public static AllgDescription neuerSatz(final StructuralElement startsNew,
                                            final String description,
                                            final AvTimeSpan timeElapsed) {
        return new AllgDescription(startsNew, description, timeElapsed);
    }

    public static AllgDescription satzanschluss(final String description,
                                                final AvTimeSpan timeElapsed) {
        return new AllgDescription(StructuralElement.WORD, description, timeElapsed);
    }

    private AllgDescription(final StructuralElement startsNew,
                            final String description,
                            final AvTimeSpan timeElapsed) {
        super(startsNew, timeElapsed);
        this.description = description;
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // Konjunktionaladverb ist in diesen Fällen nicht nötig:
        // "Du gehst in den Wald. Der Weg führt an einem Bach entlang."
        return getDescriptionHauptsatz();
    }

    @Override
    public String getDescriptionHauptsatz() {
        return description;
    }

}
