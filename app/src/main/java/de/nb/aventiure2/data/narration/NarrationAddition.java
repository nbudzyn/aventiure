package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.DescriptionParams;

/**
 * Builder for {@link Narration}.
 */
@ParametersAreNonnullByDefault
public class NarrationAddition {
    private final DescriptionParams descriptionParams;

    @PrimaryKey
    @NonNull
    private final String text;

    // STORY Das Konzept könnte man verallgemeinern: Die NarrationAddition könnte am Ende
    //  Koordination (d.h. und-Verbindungen) auf verschiedenen Ebenen erlauben:
    //  - Du nimmst die Lampe UND DAS GLAS: Koordination im AkkObj des NEHMEN-Prädikat
    //  - Den Weg hinunter kommen eine Frau UND EIN MANN: Koordination im Subj des KOMMEN-Prädikats
    //  - Du hast gute Laune und GEHST WEITER: Koordination zweier Verben zum selben Subj (P2)
    //  - Die Frau hat gute Laune und GEHT WEITER: Koordination zweier Verben zum selben Subj (P3)
    //  - Die Frau geht den Weg hinunten UND DU GEHST HINTERHER: Koordination zweier Hauptsätze
    //  Dazu bräuchte man wohl eine Kontextinfo in der Art "Womit endet die NarrationAddition?"

    NarrationAddition(final StructuralElement startsNew,
                      final String text) {
        this(new DescriptionParams(startsNew), text);
    }

    NarrationAddition(final DescriptionParams descriptionParams,
                      final String text) {
        this.descriptionParams = descriptionParams;
        this.text = text;
    }

    public NarrationAddition phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                                            final IGameObject gameObject) {
        descriptionParams.phorikKandidat(substantivischePhrase, gameObject);
        return this;
    }

    public NarrationAddition phorikKandidat(final NumerusGenus numerusGenus,
                                            final IGameObject gameObject) {
        descriptionParams.phorikKandidat(numerusGenus, gameObject);
        return this;

    }

    public NarrationAddition phorikKandidat(final NumerusGenus numerusGenus,
                                            final GameObjectId gameObjectId) {
        descriptionParams.phorikKandidat(numerusGenus, gameObjectId);
        return this;

    }

    public NarrationAddition phorikKandidat(
            @Nullable final PhorikKandidat phorikKandidat) {
        descriptionParams.phorikKandidat(phorikKandidat);
        return this;

    }

    public NarrationAddition beendet(final StructuralElement structuralElement) {
        descriptionParams.beendet(structuralElement);
        return this;
    }

    public NarrationAddition komma() {
        descriptionParams.komma();
        return this;
    }

    public NarrationAddition komma(final boolean kommaStehtAus) {
        descriptionParams.komma(kommaStehtAus);
        return this;
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public NarrationAddition undWartest() {
        descriptionParams.undWartest();
        return this;
    }

    public NarrationAddition undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        descriptionParams.undWartest(allowsAdditionalPlayerSatzreihengliedOhneSubjekt);
        return this;
    }

    public NarrationAddition dann() {
        descriptionParams.dann();
        return this;
    }

    public NarrationAddition dann(final boolean dann) {
        descriptionParams.dann(dann);
        return this;
    }

    StructuralElement getStartsNew() {
        return descriptionParams.getStartsNew();
    }

    StructuralElement getEndsThis() {
        return descriptionParams.getEndsThis();
    }

    @NonNull
    String getText() {
        return text;
    }

    boolean isKommaStehtAus() {
        return descriptionParams.isKommaStehtAus();
    }

    boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return descriptionParams.isAllowsAdditionalDuSatzreihengliedOhneSubjekt();
    }

    boolean isDann() {
        return descriptionParams.isDann();
    }

    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return descriptionParams.getPhorikKandidat();
    }
}
