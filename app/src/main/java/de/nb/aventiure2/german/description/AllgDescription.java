package de.nb.aventiure2.german.description;

import de.nb.aventiure2.german.base.StructuralElement;

/**
 * A general description. The subject may be anything.
 */
public class AllgDescription extends AbstractDescription<AllgDescription> {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private final String description;


    AllgDescription(final StructuralElement startsNew,
                    final String description) {
        super(startsNew);
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
