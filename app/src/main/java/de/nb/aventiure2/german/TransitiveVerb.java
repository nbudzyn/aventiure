package de.nb.aventiure2.german;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.object.ObjectData;

/**
 * Ein transitives Verb, ggf. mit Präfix.
 */
public enum TransitiveVerb {
    NEHMEN("nimmst"),
    AUFHEBEN("hebst", "auf"),
    HERAUSKLAUBEN("klaubst", "heraus");

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("hebst")
     */
    @NonNull
    private final String duForm;

    /**
     * Ggf. das abgetrennte Präfix des Verbs ("auf").
     * <p>
     * Wird das Präfix <i>nicht</i> abgetrennt ("ver"), ist dieses Feld <code>null</code>.
     */
    @Nullable
    private final String abgetrenntesPraefix;

    private TransitiveVerb(@NonNull final String duForm) {
        this(duForm, null);
    }

    private TransitiveVerb(@NonNull final String duForm, @Nullable final String abgetrenntesPraefix) {
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
    }

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesem Objekt.
     */
    public String getDescriptionHauptsatz(final ObjectData objectData) {
        if (!hasAbgetrenntesPraefix()) {
            return "Du " + duForm +
                    " " + objectData.akk(true);
        }

        return "Du " + duForm +
                " " + objectData.akk(true) +
                " " + getAbgetrenntesPraefix();
    }

    @NonNull
    public String getDuForm() {
        return duForm;
    }

    public boolean hasAbgetrenntesPraefix() {
        return abgetrenntesPraefix != null;
    }

    @Nullable
    public String getAbgetrenntesPraefix() {
        return abgetrenntesPraefix;
    }
}
