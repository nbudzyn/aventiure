package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;

public class WoertlicheRede {
    private final String woertlicheRedeText;

    public WoertlicheRede(final String woertlicheRedeText) {
        this.woertlicheRedeText = woertlicheRedeText;
    }

    public String fuerNachfeld() {
        final String woertlicheRedeTextTrimmed = woertlicheRedeText.trim();
        final String woertlicheRedeTextOhnePunkt =
                woertlicheRedeTextTrimmed.endsWith(".") ?
                        woertlicheRedeTextTrimmed.substring(
                                0, woertlicheRedeTextTrimmed.length() - 1) :
                        woertlicheRedeTextTrimmed;
        return joinToNullString(
                ":",
                "„",
                woertlicheRedeTextOhnePunkt); // Der "Punkt" wird je nachdem später als
        // ".“" (Satzende) oder "“" (im Satz) ausgegeben.
    }
}
