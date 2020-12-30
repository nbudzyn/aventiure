package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Wortfolge;

public interface AbstractDuTextPart {
    default Wortfolge getDuHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        final Wortfolge duHauptsatz = getDuHauptsatz();

        final Wortfolge duHauptsatzMitSpeziellemVorfeld = getDuHauptsatzMitSpeziellemVorfeld();

        if (!duHauptsatz.equals(duHauptsatzMitSpeziellemVorfeld)) {
            return duHauptsatzMitSpeziellemVorfeld;
        }

        return getDuHauptsatzMitVorfeld(konjunktionaladverb);
    }

    Wortfolge getDuHauptsatzMitVorfeld(String vorfeld);

    Wortfolge getDuHauptsatzMitSpeziellemVorfeld();

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    default Wortfolge getDuHauptsatz() {
        return Wortfolge.joinToNullWortfolge(
                "Du",
                getDuSatzanschlussOhneSubjekt());
    }

    Wortfolge getDuSatzanschlussOhneSubjekt();

    // equals() und hashCode() überschreiben wir extra nicht! Alle PraedikatDuTextParts
    // sollen als "verschieden" gelten. Ansonsten müssten wir auch in allen
    // PraedikatOhneLeerstellen-Implementierungen equals() und hashCode() überschreiben.
    // Das wäre inhaltlich richtig, aber viel Arbeit.
}
