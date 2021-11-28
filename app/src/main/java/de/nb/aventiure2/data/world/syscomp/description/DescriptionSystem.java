package de.nb.aventiure2.data.world.syscomp.description;

import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.NICHT_POSSESSIV;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Functionality concerned with descriptions that might span several game objects..
 */
@SuppressWarnings({"MethodMayBeStatic", "RedundantSuppression"})
public class DescriptionSystem {
    /**
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht des <code>observer</code>s beschreibt, ggf. kurz-
     */
    @NonNull
    public EinzelneSubstantivischePhrase getPOVDescription(
            final ITextContext textContext,
            final IGameObject observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        if (observer instanceof IHasMemoryGO) {
            return getPOVDescription(textContext, (IHasMemoryGO) observer, describable,
                    possessivDescriptionVorgabe, shortIfKnown);
        }

        return describable.descriptionComp().getNormalDescriptionWhenKnown();
    }

    /**
     * Gibt alternative beschreibende substantivische Phrasen zurück, die das
     * <code>describable</code>
     * aus Sicht des <code>observer</code>s beschreibt, ggf. kurz.
     */
    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altPOVDescriptions(
            final ITextContext textContext,
            final IGameObject observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        if (observer instanceof IHasMemoryGO) {
            return altPOVDescriptions(textContext, (IHasMemoryGO) observer, describable,
                    possessivDescriptionVorgabe, shortIfKnown);
        }

        return describable.descriptionComp().altNormalDescriptionsWhenKnown();
    }

    public ImmutableList<EinzelneSubstantivischePhrase> altPOVDescriptions(
            final ITextContext textContext,
            final IHasMemoryGO observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        final boolean known = observer.memoryComp().isKnown(describable);

        if ((describable instanceof IAmountableGO)
                && describable.descriptionComp() instanceof AmountDescriptionComp) {
            return ((AmountDescriptionComp) describable.descriptionComp())
                    .altDescriptions(
                            ((IAmountableGO) describable).amountComp().getAmount(),
                            known, shortIfKnown);
        }

        if ((describable.descriptionComp() instanceof PossessivDescriptionComp)
                && possessivDescriptionVorgabe != NICHT_POSSESSIV) {
            return altPOVPossessivDescriptions(textContext, observer, describable,
                    possessivDescriptionVorgabe,
                    shortIfKnown, known);
        }

        return describable.descriptionComp().altDescriptions(known, shortIfKnown);
    }

    public EinzelneSubstantivischePhrase getPOVDescription(
            final ITextContext textContext,
            final IHasMemoryGO observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        final boolean known = observer.memoryComp().isKnown(describable);

        if ((describable instanceof IAmountableGO)
                && describable.descriptionComp() instanceof AmountDescriptionComp) {
            return ((AmountDescriptionComp) describable.descriptionComp())
                    .getDescription(
                            ((IAmountableGO) describable).amountComp().getAmount(),
                            known, shortIfKnown);
        }

        if ((describable.descriptionComp() instanceof PossessivDescriptionComp)
                && possessivDescriptionVorgabe != NICHT_POSSESSIV) {
            return getPOVPossessivDescription(textContext, observer, describable,
                    possessivDescriptionVorgabe,
                    shortIfKnown, known);
        }


        return describable.descriptionComp().getDescription(known, shortIfKnown);
    }

    @NonNull
    private ImmutableList<EinzelneSubstantivischePhrase> altPOVPossessivDescriptions(
            final ITextContext textContext, final IHasMemoryGO observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown,
            final boolean known) {
        final DescriptionTriple possessivDescriptionTriple =
                getPOVPossessivDescriptionTriple(textContext, observer, describable,
                        possessivDescriptionVorgabe
                );

        return possessivDescriptionTriple
                .alt(known, shortIfKnown); // "ihre Haare" / "Rapunzels Haare"
    }

    @NonNull
    private EinzelneSubstantivischePhrase getPOVPossessivDescription(
            final ITextContext textContext, final IHasMemoryGO observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown,
            final boolean known) {
        final DescriptionTriple possessivDescriptionTriple =
                getPOVPossessivDescriptionTriple(textContext, observer, describable,
                        possessivDescriptionVorgabe
                );

        return possessivDescriptionTriple
                .get(known, shortIfKnown); // "ihre Haare" / "Rapunzels Haare"
    }

    @NonNull
    private DescriptionTriple getPOVPossessivDescriptionTriple(
            final ITextContext textContext, final IHasMemoryGO observer,
            final IDescribableGO describable,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        final PossessivDescriptionComp possessivDescriptionComp =
                (PossessivDescriptionComp) describable.descriptionComp();

        final GameObjectId besitzerId = possessivDescriptionComp.getBesitzerId();

        final boolean besitzerKnown = observer.memoryComp().isKnown(besitzerId);

        @Nullable final GameObjectId besitzerNameId =
                possessivDescriptionComp.getBesitzerNameId();

        final boolean besitzerNameKnown =
                besitzerNameId == null || observer.memoryComp().isKnown(besitzerNameId);

        return possessivDescriptionComp
                .getPossessivDescriptionTriple(textContext, possessivDescriptionVorgabe,
                        besitzerKnown, besitzerNameKnown);
    }
}
