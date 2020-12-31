package de.nb.aventiure2.data.world.syscomp.feelings;


import static de.nb.aventiure2.data.time.AvTime.oClock;

/**
 * {@link Biorhythmus} für Müdigkeit, wie ihn ein Mensch typischerweise haben könnte.
 */
public class MenschlicherMuedigkeitsBiorhythmus extends Biorhythmus {
    public MenschlicherMuedigkeitsBiorhythmus() {
        super(
                oClock(2, 30), FeelingIntensity.SEHR_STARK,
                oClock(5), FeelingIntensity.STARK,
                oClock(7), FeelingIntensity.NUR_LEICHT,
                oClock(7, 30), FeelingIntensity.NEUTRAL,
                // Mittagstief
                oClock(12, 30), FeelingIntensity.MERKLICH,
                oClock(14, 0), FeelingIntensity.NEUTRAL,
                oClock(17, 30), FeelingIntensity.NUR_LEICHT,
                oClock(18, 30), FeelingIntensity.DEUTLICH,
                oClock(22), FeelingIntensity.STARK
        );
    }
}
