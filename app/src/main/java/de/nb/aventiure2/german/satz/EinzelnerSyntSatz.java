package de.nb.aventiure2.german.satz;

import static de.nb.aventiure2.german.base.Belebtheit.BELEBT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.DeklinierbarePhraseUtil;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Relativpronomen;

/**
 * Ein einzelner ("syntaktischer") Satz, in dem alle Diskursreferenten
 * (Personen, Objekte etc.) auf jeweils eine konkrete sprachliche Repräsentation (z.B. ein
 * konkretes Nomen oder Personalpronomen) festgelegt sind.
 */
@Immutable
public class EinzelnerSyntSatz implements SyntSatz {
    @Override
    public Konstituentenfolge getRelativsatz() {
        // Zurzeit unterstützen wir nur die reinen Relativpronomen für die normalen Kasus
        // wie "der" oder "das".
        // Später sollten auch unterstützt werden:
        // - Relativpronomen mit Präposition ("mit dem")
        // - "substantivische Relativphrasen" wie "dessen Heldentaten"
        // - "Infinitiv-Relativphrasen" wie "die Geschichte, die zu erzählen du vergessen hast"
        // - "Relativsätze mit Interrogativadverbialien": "der Ort, wo"
        if (subjekt instanceof Relativpronomen) {
            // "der etwas zu berichten hat", "der was zu berichten hat", "die kommt"
            return getRelativsatzMitRelativpronomenSubjekt();
        }

        @Nullable final Konstituentenfolge relativpronomenImPraedikat =
                praedikat.getRelativpronomen();

        if (relativpronomenImPraedikat == null) {
            throw new IllegalStateException("Kein (eindeutiges) Relativpronomen im Prädikat "
                    + "gefunden: " + praedikat
                    .getVerbzweit(DeklinierbarePhraseUtil.EINER_UNBELEBT.mitBelebheit(BELEBT)));
        }

        // "das du zu berichten hast", "dem er was gegeben hat", "der kommt"
        return joinToKonstituentenfolge(
                // FIXME Anschlusswort beim Relativsatz ist nur bei Reihungen sinnvoll...
                //  Höchstens "das aber du zu berichten das..."
                anschlusswort, // "und"
                relativpronomenImPraedikat.withVorkommaNoetigMin(anschlusswort == null),
                // "das" / "dem"
                getVerbletztsatz().cutFirst(
                        relativpronomenImPraedikat
                ))// "du zu berichten hast", "er was gegeben hat", "kommt"
                .withVorkommaNoetigMin(anschlusswort == null);
    }
}
