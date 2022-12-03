package de.nb.aventiure2.german.praedikat;

import static com.google.common.base.Preconditions.checkArgument;

import androidx.annotation.NonNull;

/**
 * Ein Modalverb. Modalverben regieren den reinen Infinitiv
 * (z.B. <i>etw. tun können</i>).
 */
public enum Modalverb implements VerbMitValenz, PraedikatNurMitLeerstelleFuerLexikalischenKern {
    DUERFEN("dürfen",
            "darf", "darfst", "darf", "dürft",
            Perfektbildung.HABEN,
            // "Das hat er nicht gedurft."
            // "Er hat nicht schlafen dürfen." (Ersatzinfinitiv)
            "gedurft"),
    // "Rapunzel kann die Haare herunterlassen"
    KOENNEN("können",
            "kann", "kannst", "kann", "könnt",
            Perfektbildung.HABEN,
            // "Das hat er nicht gekannt."
            // "Er hat nicht schlafen können." (Ersatzinfinitiv)
            "gekonnt"),
    MOEGEN("mögen",
            "mag", "magst", "mag", "mögt",
            Perfektbildung.HABEN,
            // "Dass Essen hat er nicht gemocht."
            // "Er hat nicht schlafen mögen." (Ersatzinfinitiv)
            "gemocht"),
    MUESSEN("müssen",
            "muss", "musst", "muss", "müsst",
            Perfektbildung.HABEN,
            // "Das hat er nicht gemusst."
            // "Er hat nicht schlafen müssen." (Ersatzinfinitiv)
            "gemusst"),
    SOLLEN("sollen",
            "soll", "sollst", "soll", "sollt",
            Perfektbildung.HABEN,
            // "Das hat er nicht gesollt."
            // "Er hat nicht schlafen sollen." (Ersatzinfinitiv)
            "gesollt"),
    WOLLEN("wollen",
            "will", "willst", "will", "wollt",
            Perfektbildung.HABEN,
            // "Den Schaden hat er nicht gewollt."
            // "Er hat nicht schlafen wollen." (Ersatzinfinitiv)
            "gewollt");

    // (Für brauchen / nicht brauchen, möchten, lassen, werden gibt es spezielle Regeln.
    // Ob das Modalverben sind, ist eher Ansichtssache. Manche erfordern
    // Ersatzinfinitive!)

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    Modalverb(@NonNull final String infinitiv,
              @NonNull final String ichForm,
              @NonNull final String duForm,
              @NonNull final String erSieEsForm,
              @NonNull final String ihrForm,
              final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));

        checkArgument(infinitiv.equals(partizipII),
                "Bei Modalverben müssen Infinitiv und Partizip II gleich sein "
                        + "(\"Ersatzinfinitiv\"). Infinitiv: %s, Partizip II: %s",
                infinitiv, partizipII);
    }

    Modalverb(@NonNull final Verb verb) {
        this.verb = verb;
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    @Override
    public SemPraedikatModalverbOhneLeerstellen mitLexikalischemKern(
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        return new SemPraedikatModalverbOhneLeerstellen(verb, lexikalischerKern);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
