package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.GermanUtil;

public interface AbstractDuTextPart {
    default String getDuHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        final String duHauptsatz = getDuHauptsatz();

        final String duHauptsatzMitSpeziellemVorfeld =
                getDuHauptsatzMitSpeziellemVorfeld();

        if (!duHauptsatz.equals(duHauptsatzMitSpeziellemVorfeld)) {
            return duHauptsatzMitSpeziellemVorfeld;
        }

        return getDuHauptsatzMitVorfeld(konjunktionaladverb);
    }

    String getDuHauptsatzMitVorfeld(String vorfeld);

    String getDuHauptsatzMitSpeziellemVorfeld();

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    default String getDuHauptsatz() {
        return GermanUtil.joinToNullString(
                "Du",
                getDuSatzanschlussOhneSubjekt());
    }

    String getDuSatzanschlussOhneSubjekt();
}
