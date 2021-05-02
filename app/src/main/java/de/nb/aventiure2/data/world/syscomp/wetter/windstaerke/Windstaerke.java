package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

public enum Windstaerke {
    WINDSTILL,
    LUEFTCHEN,
    WINDIG,
    KRAEFTIGER_WIND,
    STURM,
    SCHWERER_STURM,
    ORKAN;

    public Windstaerke getVorgaenger() {
        if (ordinal() == 0) {
            return values()[values().length - 1];
        }

        return values()[ordinal() - 1];
    }

    public Windstaerke getNachfolger() {
        if (ordinal() == values().length - 1) {
            return values()[0];
        }

        return values()[ordinal() + 1];
    }
}
