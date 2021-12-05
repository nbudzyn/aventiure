package de.nb.aventiure2.data.world.syscomp.description;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

/**
 * Implementierung von {@link AbstractDescriptionComp}, die auch die Menge
 * mitbeschreiben kann.
 */
public class AmountDescriptionComp extends SimpleDescriptionComp {
    @NonNull
    private final ImmutableMap<Integer, EinzelneSubstantivischePhrase>
            descriptionsAtFirstSightByMinAmount;


    public AmountDescriptionComp(final CounterDao counterDao,
                                 final GameObjectId id,
                                 final Map<Integer, EinzelneSubstantivischePhrase> descriptionsAtFirstSightByMinAmount,
                                 final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                                 final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        super(counterDao, id, getDescriptionAtFirstSight(descriptionsAtFirstSightByMinAmount, 1),
                normalDescriptionWhenKnown, shortDescriptionWhenKnown);

        checkArgument(descriptionsAtFirstSightByMinAmount.containsKey(0),
                "Keine Beschreibung für Menge 0");

        this.descriptionsAtFirstSightByMinAmount =
                ImmutableMap.copyOf(descriptionsAtFirstSightByMinAmount);
    }

    public ImmutableList<EinzelneSubstantivischePhrase> altDescriptions(final int amount,
                                                                        final boolean known,
                                                                        final boolean shortIfKnown) {
        if (!known) {
            return ImmutableList.of(getDescriptionAtFirstSight(amount));
        }

        return altDescriptionsWhenKnown(shortIfKnown);
    }

    public EinzelneSubstantivischePhrase getDescription(final int amount,
                                                        final boolean known,
                                                        final boolean shortIfKnown) {
        if (!known) {
            return getDescriptionAtFirstSight(amount);
        }

        return getDescriptionWhenKnown(shortIfKnown);
    }

    public EinzelneSubstantivischePhrase getDescriptionAtFirstSight(final int amount) {
        return getDescriptionAtFirstSight(descriptionsAtFirstSightByMinAmount, amount);
    }

    private static EinzelneSubstantivischePhrase getDescriptionAtFirstSight(
            final Map<Integer, EinzelneSubstantivischePhrase> descriptionsAtFirstSightByMinAmount,
            final int amount) {
        checkState(amount >= 0, "amount < 0");

        @Nullable EinzelneSubstantivischePhrase res = null;
        int bestThreshold = 0;

        for (final Map.Entry<Integer, EinzelneSubstantivischePhrase> thresholdAndDescription :
                descriptionsAtFirstSightByMinAmount.entrySet()) {
            final Integer threshold = thresholdAndDescription.getKey();

            if (amount >= threshold && bestThreshold <= threshold) {
                bestThreshold = threshold;
                res = thresholdAndDescription.getValue();
            }
        }

        return requireNonNull(res);
    }
}