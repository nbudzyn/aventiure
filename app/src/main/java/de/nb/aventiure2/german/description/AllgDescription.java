package de.nb.aventiure2.german.description;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;

/**
 * A general description. The subject may be anything.
 */
@ParametersAreNonnullByDefault
public class AllgDescription extends AbstractDescription<AllgDescription> {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private final String description;

    // IDEA Das Konzept könnte man verallgemeinern: Die AllgDescription könnte am Ende
    //  Koordination (d.h. und-Verbindungen) auf verschiedenen Ebenen erlauben:
    //  - Du nimmst die Lampe UND DAS GLAS: Koordination im AkkObj des NEHMEN-Prädikat
    //  - Den Weg hinunter kommen eine Frau UND EIN MANN: Koordination im Subj des KOMMEN-Prädikats
    //  - Du hast gute Laune und GEHST WEITER: Koordination zweier Verben zum selben Subj (P2)
    //  - Die Frau hat gute Laune und GEHT WEITER: Koordination zweier Verben zum selben Subj (P3)
    //  - Die Frau geht den Weg hinunten UND DU GEHST HINTERHER: Koordination zweier Hauptsätze
    //  Dazu bräuchte man wohl eine Kontextinfo in der Art "Womit endet die AllgDescription?"
    //  Das könnte allerdings auch über die Prädikate... gelöst werden...

    AllgDescription(final StructuralElement startsNew,
                    final Wortfolge wortfolge) {
        this(new DescriptionParams(startsNew,
                        wortfolge.woertlicheRedeNochOffen(), wortfolge.kommmaStehtAus()),
                wortfolge.getString());
    }

    AllgDescription(final StructuralElement startsNew,
                    final String description) {
        this(new DescriptionParams(startsNew), description);
    }

    public AllgDescription(final DescriptionParams descriptionParams,
                           final String description) {
        super(descriptionParams);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final AllgDescription that = (AllgDescription) o;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description);
    }
}
