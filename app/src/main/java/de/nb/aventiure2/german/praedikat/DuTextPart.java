package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.GermanUtil;

public interface DuTextPart {
    default String getDuHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        final String duHauptsatz = getDuHauptsatz();

        final String duHauptsatzMitSpeziellemVorfeld =
                getDuHauptsatzMitSpeziellemVorfeld();

        if (!duHauptsatz.equals(duHauptsatzMitSpeziellemVorfeld)) {
            return duHauptsatzMitSpeziellemVorfeld;
        }

        return getDuHauptsatzMitKonjunktionaladverb(konjunktionaladverb);
    }

    String getDuHauptsatzMitKonjunktionaladverb(String konjunktionaladverb);

    String getDuHauptsatzMitSpeziellemVorfeld();

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    default String getDuHauptsatz() {
        return GermanUtil.joinToNull(
                "Du",
                getDuSatzanschlussOhneSubjekt());
    }

    String getDuSatzanschlussOhneSubjekt();
}
