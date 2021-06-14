package de.nb.aventiure2.data.world.syscomp.wetter;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * Flags für die einzelnen Wetter-Parameter. Damit kann man z.B. ausdrücken, dass gewisse
 * Wetter-Parameter nicht beschrieben werden sollen o.Ä.
 */
@Immutable
class WetterParamFlags {
    private final boolean temperatur;

    private final boolean windstaerke;

    private final boolean bewoelkung;

    private final boolean blitzUndDonner;

    static WetterParamFlags keine() {
        return new WetterParamFlags(false, false, false, false);
    }

    static WetterParamFlags alle() {
        return new WetterParamFlags(true, true, true, true);
    }

    WetterParamFlags(final boolean temperatur, final boolean windstaerke,
                     final boolean bewoelkung,
                     final boolean blitzUndDonner) {
        this.temperatur = temperatur;
        this.windstaerke = windstaerke;
        this.bewoelkung = bewoelkung;
        this.blitzUndDonner = blitzUndDonner;
    }

    public boolean isTemperatur() {
        return temperatur;
    }

    public boolean isWindstaerke() {
        return windstaerke;
    }

    public boolean isBewoelkung() {
        return bewoelkung;
    }

    public boolean isBlitzUndDonner() {
        return blitzUndDonner;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WetterParamFlags that = (WetterParamFlags) o;
        return temperatur == that.temperatur &&
                windstaerke == that.windstaerke &&
                bewoelkung == that.bewoelkung &&
                blitzUndDonner == that.blitzUndDonner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperatur, windstaerke, bewoelkung, blitzUndDonner);
    }

    @Override
    public String toString() {
        return "WetterParamFlags{" +
                "temperatur=" + temperatur +
                ", windstaerke=" + windstaerke +
                ", bewoelkung=" + bewoelkung +
                ", blitzUndDonner=" + blitzUndDonner +
                '}';
    }
}
