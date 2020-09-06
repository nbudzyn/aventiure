package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

public class WoertlicheRede {
    private final String woertlicheRedeText;

    public WoertlicheRede(final String woertlicheRedeText) {
        this.woertlicheRedeText = woertlicheRedeText;
    }

    public String amSatzende() {
        @Nullable final String woertlichRedeSatzendeZeichen =
                !woertlicheRedeText.endsWith(".") ? "." : null;
        return joinToNull(
                "„",
                woertlicheRedeText,
                woertlichRedeSatzendeZeichen,
                "“");
    }
}
