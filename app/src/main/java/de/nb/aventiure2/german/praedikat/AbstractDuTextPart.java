package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Wortfolge;

public interface AbstractDuTextPart {
    default Wortfolge getDuHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        final Wortfolge duHauptsatz = getDuHauptsatz();

        final Wortfolge duHauptsatzMitSpeziellemVorfeld =
                getDuHauptsatzMitSpeziellemVorfeld();

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
        return GermanUtil.joinToNull(
                "Du",
                getDuSatzanschlussOhneSubjekt());
    }

    Wortfolge getDuSatzanschlussOhneSubjekt();
}
