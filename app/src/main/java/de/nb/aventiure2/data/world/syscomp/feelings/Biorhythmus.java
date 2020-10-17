package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.core.util.Pair;

import java.util.List;

import de.nb.aventiure2.data.world.time.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;

/**
 * Biorhythmus für ein bestimmtes Gefühl: Zu gewissen Tages- und Nachtzeiten
 * ist das Gefühl üblicherweise soundso stark.
 */
public class Biorhythmus {
    private final List<Pair<AvTime, Integer>> pairsInTimeOrder;

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2)));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3)
        ));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4)
        ));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4,
                       final AvTime time5, final Integer intensity5) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4),
                Pair.create(time5, intensity5)
        ));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4,
                       final AvTime time5, final Integer intensity5,
                       final AvTime time6, final Integer intensity6) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4),
                Pair.create(time5, intensity5),
                Pair.create(time6, intensity6)
        ));
    }


    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4,
                       final AvTime time5, final Integer intensity5,
                       final AvTime time6, final Integer intensity6,
                       final AvTime time7, final Integer intensity7) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4),
                Pair.create(time5, intensity5),
                Pair.create(time6, intensity6),
                Pair.create(time7, intensity7)
        ));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4,
                       final AvTime time5, final Integer intensity5,
                       final AvTime time6, final Integer intensity6,
                       final AvTime time7, final Integer intensity7,
                       final AvTime time8, final Integer intensity8) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4),
                Pair.create(time5, intensity5),
                Pair.create(time6, intensity6),
                Pair.create(time7, intensity7),
                Pair.create(time8, intensity8)
        ));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4,
                       final AvTime time5, final Integer intensity5,
                       final AvTime time6, final Integer intensity6,
                       final AvTime time7, final Integer intensity7,
                       final AvTime time8, final Integer intensity8,
                       final AvTime time9, final Integer intensity9
    ) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4),
                Pair.create(time5, intensity5),
                Pair.create(time6, intensity6),
                Pair.create(time7, intensity7),
                Pair.create(time8, intensity8),
                Pair.create(time9, intensity9)
        ));
    }

    public Biorhythmus(final AvTime time1, final Integer intensity1,
                       final AvTime time2, final Integer intensity2,
                       final AvTime time3, final Integer intensity3,
                       final AvTime time4, final Integer intensity4,
                       final AvTime time5, final Integer intensity5,
                       final AvTime time6, final Integer intensity6,
                       final AvTime time7, final Integer intensity7,
                       final AvTime time8, final Integer intensity8,
                       final AvTime time9, final Integer intensity9,
                       final AvTime time10, final Integer intensity10
    ) {
        this(asList(
                Pair.create(time1, intensity1),
                Pair.create(time2, intensity2),
                Pair.create(time3, intensity3),
                Pair.create(time4, intensity4),
                Pair.create(time5, intensity5),
                Pair.create(time6, intensity6),
                Pair.create(time7, intensity7),
                Pair.create(time8, intensity8),
                Pair.create(time9, intensity9),
                Pair.create(time10, intensity10)
        ));
    }

    public Biorhythmus(final List<Pair<AvTime, Integer>> pairsInTimeOrder) {
        check(pairsInTimeOrder);

        this.pairsInTimeOrder = pairsInTimeOrder;
    }

    private static void check(final List<Pair<AvTime, Integer>> pairsInTimeOrder) {
        checkArgument(!pairsInTimeOrder.isEmpty(), "Keine Pairs");

        for (int i = 0; i < pairsInTimeOrder.size() - 1; i++) {
            checkArgument(pairsInTimeOrder.get(i).first
                            .isBefore(pairsInTimeOrder.get(i + 1).first),
                    "Falsch geordnet: "
                            + pairsInTimeOrder.get(i).first
                            + ", "
                            + pairsInTimeOrder.get(i + 1).first);
        }
    }

    public int get(final AvTime time) {
        int res = pairsInTimeOrder.get(pairsInTimeOrder.size() - 1).second;

        for (final Pair<AvTime, Integer> pair : pairsInTimeOrder) {
            if (pair.first.isAfter(time)) {
                return res;
            }

            res = pair.second;
        }

        return res;
    }
}
