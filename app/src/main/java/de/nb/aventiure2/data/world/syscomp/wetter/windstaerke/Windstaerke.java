package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.base.Betweenable;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEAENGSTIGEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STUERMISCH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STURMWIND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.UNWETTER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WINDHAUCH;

public enum Windstaerke implements Betweenable<Windstaerke> {
    // Reihenfolge ist relevant - nicht ändern!
    WINDSTILL(ImmutableList.of(),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDSTILL)),
    LUEFTCHEN(ImmutableList.of(WINDHAUCH),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDSTILL.mitGraduativerAngabe("fast"),
                    AdjektivOhneErgaenzungen.WINDSTILL.mitGraduativerAngabe("beinahe"))),
    WINDIG(ImmutableList.of(WIND),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDIG)),
    KRAEFTIGER_WIND(ImmutableList.of(),
            ImmutableList.of(AdjektivOhneErgaenzungen.WINDIG.mitGraduativerAngabe("sehr")),
            1.2),
    STURM(ImmutableList.of(NomenFlexionsspalte.STURM, STURMWIND),
            ImmutableList.of(STUERMISCH), 1.4),
    SCHWERER_STURM(ImmutableList.of(UNWETTER),
            ImmutableList.of(STUERMISCH.mitAdvAngabe(new AdvAngabeSkopusSatz(BEAENGSTIGEND))),
            1.7);
    // IDEA ORKAN (müsste dann zu starken Reaktionen der Umwelt und der NSCs führen,
    //  würde Schäden anrichten und dem SC massiv z.B. beim Gehen oder Klettern
    //  behindern)

    /**
     * Beschreibung als {@link de.nb.aventiure2.german.base.NomenFlexionsspalte} - evtl. leer.
     */
    private final ImmutableList<NomenFlexionsspalte> altSpNomenFlexionsspalte;

    /**
     * Alternative Adjektivphrasen für das Wetter.
     */
    private final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWetter;

    /**
     * Faktor, mit dem die übliche Bewegungsgeschwindigkeit bei dieser Windstärke multipliziert
     * wird (nie unter 1).
     */
    private final double movementSpeedFactor;

    Windstaerke(final ImmutableList<NomenFlexionsspalte> altSpNomenFlexionsspalte,
                final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWetter) {
        this(altSpNomenFlexionsspalte, altAdjPhrWetter, 1.0);
    }

    Windstaerke(final ImmutableList<NomenFlexionsspalte> altSpNomenFlexionsspalte,
                final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWetter,
                final double movementSpeedFactor) {
        this.altSpNomenFlexionsspalte = altSpNomenFlexionsspalte;
        this.altAdjPhrWetter = altAdjPhrWetter;
        this.movementSpeedFactor = movementSpeedFactor;
    }

    /**
     * Gibt alternative Beschreibungen als {@link NomenFlexionsspalte}-Objekt zurück,
     * evtl. leer.
     */
    public ImmutableList<NomenFlexionsspalte> altSpNomenFlexionsspalte() {
        return altSpNomenFlexionsspalte;
    }

    /**
     * Gibt alternative Adjektivphrasen für das Wetter zurück
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

    public int minus(final Windstaerke other) {
        return ordinal() - other.ordinal();
    }

    public Windstaerke getLokaleWindstaerkeDraussenGeschuetzt() {
        if (compareTo(LUEFTCHEN) <= 0) {
            return this;
        }

        // FIXME Auch im Wald sollte es stürmen (sonst versteht man "der Sturm hat..." nicht.
        //  Nicht um 2 Stufen reduzieren - evtl. Reduktion
        //  erst später?

        if (compareTo(KRAEFTIGER_WIND) <= 0) {
            return getVorgaenger();
        }

        return getVorgaenger().getVorgaenger();
    }

    public Windstaerke getLokaleWindstaerkeDraussen(final boolean unterOffenemHimmel) {
        if (unterOffenemHimmel) {
            return this;
        }

        return getLokaleWindstaerkeDraussenGeschuetzt();
    }

    public double getMovementSpeedFactor() {
        return movementSpeedFactor;
    }
}
