package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.GermanUtil.joinToString;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WoertlicheRede {
    private final String woertlicheRedeText;

    public WoertlicheRede(final String woertlicheRedeText) {
        this.woertlicheRedeText = woertlicheRedeText;
    }

    public String getDescription() {
        final String woertlicheRedeTextTrimmed = woertlicheRedeText.trim();
        final String woertlicheRedeTextOhnePunkt =
                woertlicheRedeTextTrimmed.endsWith(".") ?
                        woertlicheRedeTextTrimmed.substring(
                                0, woertlicheRedeTextTrimmed.length() - 1) :
                        woertlicheRedeTextTrimmed;
        return joinToString(
                "„",
                woertlicheRedeTextOhnePunkt); // Der "Punkt" wird je nachdem später als
        // ".“" (Satzende) oder "“" (im Satz) ausgegeben.
    }

    public boolean isLangOderMehrteilig() {
        return woertlicheRedeText.length() > 35 || enthaeltAberEndetNichtMit(".!?");
    }

    private boolean enthaeltAberEndetNichtMit(final String characters) {
        for (int i = 0; i < characters.length(); i++) {
            if (woertlicheRedeText.substring(0, woertlicheRedeText.length() - 2)
                    .contains(characters.substring(i, i + 1))) {
                return true;
            }
        }

        return false;
    }
}
