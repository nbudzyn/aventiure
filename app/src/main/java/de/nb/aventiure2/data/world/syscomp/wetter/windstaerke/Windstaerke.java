package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STUERMISCH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STURMWIND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.UNWETTER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WINDHAUCH;

public enum Windstaerke {
    // Reihenfolge ist relevant - nicht ändern!
    WINDSTILL(ImmutableList.of(),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDSTILL)),
    LUEFTCHEN(ImmutableList.of(WINDHAUCH),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDSTILL.mitGraduativerAngabe("fast"),
                    AdjektivOhneErgaenzungen.WINDSTILL.mitGraduativerAngabe("beinahe"))),
    WINDIG(ImmutableList.of(WIND),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDIG)),
    // FIXME Wird die Zauberin beim KRAEFTIGEM WIND o.Ä. zu Rapunzel hinaufsteigen?
    // FIXME Wie reagiert das Schlossfest auf KRAEFTIGEN WIND?
    //  Gutes Konzept bauen, das alle ReactionComps auf Wetter(änderungen)
    //  reagieren können?
    KRAEFTIGER_WIND(ImmutableList.of(),
            ImmutableList.of()),
    // FIXME Kann der SC bei STURM oder schwerem Sturm auf einen Baum klettern?
    //  Zu Rapunzeln hinauf oder hinabsteigen?
    STURM(ImmutableList.of(NomenFlexionsspalte.STURM, STURMWIND),
            ImmutableList.of(STUERMISCH)),
    SCHWERER_STURM(ImmutableList.of(UNWETTER),
            ImmutableList.of());
    // IDEA ORKAN (müsste dann zu starken Reaktionen der Umwelt und der NSCs führen,
    //  würde Schäden anrichten und dem SC massiv z.B. beim Gehen oder Klettern
    //  behindern)

    /**
     * Beschreibung als {@link de.nb.aventiure2.german.base.NomenFlexionsspalte} - evtl. leer.
     */
    private final ImmutableList<NomenFlexionsspalte> altNomenFlexionsspalte;

    /**
     * Alternative Adjektivphrasen für das Wetter, evtl. leer.
     */
    private final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWetter;

    Windstaerke(final ImmutableList<NomenFlexionsspalte> altNomenFlexionsspalte,
                final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWetter) {
        this.altNomenFlexionsspalte = altNomenFlexionsspalte;
        this.altAdjPhrWetter = altAdjPhrWetter;
    }

    /**
     * Gibt alternative Beschreibungen als {@link NomenFlexionsspalte}-Objekt zurück,
     * evtl. leer.
     */
    public ImmutableList<NomenFlexionsspalte> altNomenFlexionsspalte() {
        return altNomenFlexionsspalte;
    }

    /**
     * Gibt alternative Adjektivphrasen für das Wetter zurück, evtl. leer.
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWetter() {
        return altAdjPhrWetter;
    }

    public boolean isUnauffaellig() {
        return compareTo(LUEFTCHEN) <= 0;
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
