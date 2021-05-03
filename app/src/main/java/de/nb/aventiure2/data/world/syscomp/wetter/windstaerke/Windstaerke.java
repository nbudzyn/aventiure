package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import de.nb.aventiure2.data.time.Tageszeit;

public enum Windstaerke {
    WINDSTILL,
    LUEFTCHEN,
    WINDIG,
    // FIXME Wird die Zauberin beim KRAEFTIGEM WIND o.Ä. zu Rapunzel hinaufsteigen?
    // FIXME Wie reagiert das Schlossfest auf KRAEFTIGEN WIND?
    //  Gutes Konzept bauen, das alle ReactionComps auf Wetter(änderungen)
    //  reagieren können?
    KRAEFTIGER_WIND,
    // FIXME Kann der SC bei STURM oder schwerem Sturm auf einen Baum klettern?
    //  Zu Rapunzeln hinauf oder hinabsteigen?
    STURM,
    SCHWERER_STURM;
    // IDEA ORKAN (müsste dann zu starken Reaktionen der Umwelt und der NSCs führen,
    //  würde Schäden anrichten und dem SC massiv z.B. beim Gehen oder Klettern
    //  behindern)

    public boolean isUnauffaellig(final Tageszeit tageszeit) {
        return compareTo(LUEFTCHEN) < 0;
    }

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
